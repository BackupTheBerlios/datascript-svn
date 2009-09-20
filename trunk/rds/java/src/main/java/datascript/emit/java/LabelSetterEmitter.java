/* BSD License
 *
 * Copyright (c) 2006, Harald Wellmann, Henrik Wedekind Harman/Becker Automotive Systems
 * All rights reserved.
 * 
 * This software is derived from previous work
 * Copyright (c) 2003, Godmar Back.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 * 
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 * 
 *     * Redistributions in binary form must reproduce the above
 *       copyright notice, this list of conditions and the following
 *       disclaimer in the documentation and/or other materials provided
 *       with the distribution.
 * 
 *     * Neither the name of Harman/Becker Automotive Systems or
 *       Godmar Back nor the names of their contributors may be used to
 *       endorse or promote products derived from this software without
 *       specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */


package datascript.emit.java;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import antlr.collections.AST;
import datascript.antlr.DataScriptParserTokenTypes;
import datascript.ast.ArrayType;
import datascript.ast.BitFieldType;
import datascript.ast.DataScriptException;
import datascript.ast.Expression;
import datascript.ast.Field;
import datascript.ast.IntegerType;
import datascript.ast.SequenceType;
import datascript.ast.StringType;
import datascript.ast.TypeInterface;
import datascript.ast.TypeReference;
import freemarker.template.Template;
import freemarker.template.TemplateException;



public class LabelSetterEmitter extends JavaDefaultEmitter
{
    protected static ExpressionEmitter exprEmitter = new ExpressionEmitter();
    protected SequenceType sequence;
    private List<SequenceFieldEmitter> fields = new ArrayList<SequenceFieldEmitter>();


    public static class SequenceFieldEmitter
    {
        private Field field;

        private LabelSetterEmitter global;

        private String labelString;


        public SequenceFieldEmitter(Field field, LabelSetterEmitter global)
        {
            this.field = field;
            this.global = global;
        }


        public String getVisitor()
        {
            TypeInterface type = field.getFieldType();
            return global.getVisitor(type, "node."
                    + AccessorNameEmitter.getGetterName(field) + "()", 
                    "\"" + field.getName() + "\"" /*"arg"*/);
        }


        public String getOptionalClause()
        {
            SequenceEmitter e = new SequenceEmitter(global, global.getSequenceType());
            return e.getOptionalClause(field);
        }


        public String getIndicatorName()
        {
            return AccessorNameEmitter.getIndicatorName(field) + "()";
        }


        public String getName()
        {
            return field.getName();
        }


        public boolean getHasAlignment()
        {
            return field.getAlignment() != null;
        }


        public int getAlignmentValue()
        {
            return field.getAlignmentValue();
        }
        
        /**
         * Returns a label setter for the current field. If the field
         * has a composite label <tt>header.offsets.offset1</tt>, the
         * result is <tt>getHeader().getOffsets().setOffset1</tt>.
         * 
         * @return setter without argument list
         */
        public String getLabelSetter()
        {
            Expression expr = field.getLabel();
            StringBuilder buffer = new StringBuilder();
            buffer.append("node.");
            appendLabelSetter(buffer, expr);
            return buffer.toString();
        }

        /**
         * Recursively traverses a label expression to construct the setter
         * name for the label. We rely on the results of the ExpressionEvaluator
         * in assuming that the expression type is either DOT or ID, and that
         * an ID always resolved to a Field in the expression scope.
         * 
         * @param buffer string buffer used for appending the partial result
         * @param expr   subexpression of current label expression
         */
        private void appendLabelSetter(StringBuilder buffer, Expression expr)
        {
            if (expr.getType() == DataScriptParserTokenTypes.DOT)
            {
                Expression op1 = expr.op1();
                String symbol = op1.getText();
                Field f = (Field) op1.getScope().getTypeOrSymbol(symbol);
                String getter = AccessorNameEmitter.getGetterName(f);
                buffer.append(getter);
                buffer.append("().");
                appendLabelSetter(buffer, expr.op2());
            }
            else
            {                
                Field f = (Field) expr.getScope().getTypeOrSymbol(expr.getText());
                buffer.append(AccessorNameEmitter.getSetterName(f));
            }            
        }


        public String getLabelTypeName()
        {
            return TypeNameEmitter.getTypeName(field.getLabel().getExprType());
        }

        public String getLabelExpression()
        {
            if (labelString == null)
            {
                Expression label = field.getLabel();
                if (label == null)
                    return null;

                StringBuilder builder = new StringBuilder();
                AST labelBase = label.getNextSibling();
                if (labelBase != null)
                {
                    String name = labelBase.getText();
                    builder.append("((");
                    builder.append(name);
                    builder.append(")__cc.find(\"");
                    builder.append(name);
                    builder.append("\")).");
                }
                builder.append("__fpos + 8*");
                String labelExpr = exprEmitter.emit(label);
                builder.append(labelExpr);

                return builder.toString();
            }
            return labelString;
        }
    }



    public LabelSetterEmitter(String outPathName, String defaultPackageName)
    {
        super(outPathName, defaultPackageName);
    }


    @Override
    public void beginRoot(AST rootNode)
    {
        findAllPackageNames(rootNode, allPackageNames);
        setPackageName(rootNode.getFirstChild());
        openOutputFile(dir, "__LabelSetter.java");
        try
        {
            Template tpl = cfg.getTemplate("java/LabelSetter.ftl");
            tpl.process(this, writer);
        }
        catch (IOException exc)
        {
            throw new DataScriptException(exc);
        }
        catch (TemplateException exc)
        {
            throw new DataScriptException(exc);
        }
    }


    @Override
    public void endRoot()
    {
        try
        {
            Template tpl = cfg.getTemplate("java/SequenceEnd.ftl");
            tpl.process(this, writer);
        }
        catch (IOException exc)
        {
            throw new DataScriptException(exc);
        }
        catch (TemplateException exc)
        {
            throw new DataScriptException(exc);
        }
        writer.close();
    }


    @Override
    public void beginSequence(AST s)
    {
        sequence = (SequenceType) s;
        if (! sequence.hasLabels())
            return;

        fields.clear();
        for (Field field : sequence.getFields())
        {
            SequenceFieldEmitter fe = new SequenceFieldEmitter(field, this);
            fields.add(fe);
        }

        try
        {
            Template tpl = cfg.getTemplate("java/LabelSetterSequence.ftl");
            tpl.process(this, writer);
        }
        catch (IOException exc)
        {
            throw new DataScriptException(exc);
        }
        catch (TemplateException exc)
        {
            throw new DataScriptException(exc);
        }
    }



    /** ***************************************************************** */

    public SequenceType getSequenceType()
    {
        return sequence;
    }



    public String getSequencePackageName()
    {
        return sequence.getPackage().getPackageName();
    }



    public String getVisitor(TypeInterface t, String nodeName, String fieldName)
    {
        TypeInterface type = TypeReference.getBaseType(t);
        Expression length = null;
        StringBuilder buffer = new StringBuilder();
        if (type instanceof IntegerType)
        {
            buffer.append("visit");
            IntegerType itype = (IntegerType) type;
            switch (itype.getType())
            {
                case DataScriptParserTokenTypes.INT8:
                    buffer.append("Int8");
                    break;
                case DataScriptParserTokenTypes.UINT8:
                    buffer.append("UInt8");
                    break;
                case DataScriptParserTokenTypes.INT16:
                    buffer.append("Int16");
                    break;
                case DataScriptParserTokenTypes.UINT16:
                    buffer.append("UInt16");
                    break;
                case DataScriptParserTokenTypes.INT32:
                    buffer.append("Int32");
                    break;
                case DataScriptParserTokenTypes.UINT32:
                    buffer.append("UInt32");
                    break;
                case DataScriptParserTokenTypes.INT64:
                    buffer.append("Int64");
                    break;
                case DataScriptParserTokenTypes.UINT64:
                    buffer.append("UInt64");
                    break;
                case DataScriptParserTokenTypes.BIT:
                    BitFieldType bftype = (BitFieldType) itype;
                    length = bftype.getLengthExpression();
                    buffer.append("BitField");
                    break;
                case DataScriptParserTokenTypes.INT:
                    bftype = (BitFieldType) itype;
                    length = bftype.getLengthExpression();
                    buffer.append("SignedBitField");
                    break;
                default:
                    throw new IllegalStateException();
            }
            buffer.append("(");
            buffer.append(nodeName);
            if (length != null)
            {
                buffer.append(", ");
                buffer.append(exprEmitter.emit(length, "node"));
            }
            buffer.append(", ");
            buffer.append(fieldName);
            buffer.append(")");
        }
        else if (type instanceof StringType)
        {
            buffer.append("visitString(" + nodeName + ", " + fieldName + ")");
        }
        else if (type instanceof ArrayType)
        {
            buffer.append("visitArray(" + nodeName + ", " + fieldName + ")");
        }
        else
        {
            buffer.append(nodeName + ".accept(this, " + fieldName + ")");
        }
        return buffer.toString();
    }


    public String getStartType()
    {
        return null;
    }


    public String getEndType()
    {
        return null;
    }


    /** ** interface to freemarker FileHeader.inc template *** */

    public String getRootPackageName()
    {
        return datascript.ast.Package.getRoot().getPackageName();
    }


    public List<SequenceFieldEmitter> getFields()
    {
        return fields;
    }
}
