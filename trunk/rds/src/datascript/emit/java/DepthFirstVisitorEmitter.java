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


import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import antlr.collections.AST;
import datascript.antlr.DataScriptParserTokenTypes;
import datascript.ast.ArrayType;
import datascript.ast.BitFieldType;
import datascript.ast.ChoiceMember;
import datascript.ast.ChoiceType;
import datascript.ast.DataScriptException;
import datascript.ast.EnumType;
import datascript.ast.Expression;
import datascript.ast.Field;
import datascript.ast.IntegerType;
import datascript.ast.SequenceType;
import datascript.ast.SqlIntegerType;
import datascript.ast.TypeInterface;
import datascript.ast.TypeReference;
import datascript.ast.UnionType;
import datascript.emit.java.ChoiceEmitter.ChoiceMemberEmitter;
import freemarker.template.Configuration;
import freemarker.template.Template;



public class DepthFirstVisitorEmitter extends JavaDefaultEmitter
{
    private final List<SequenceFieldEmitter> fields = new ArrayList<SequenceFieldEmitter>();
    private final List<ChoiceMemberEmitter> members = new ArrayList<ChoiceMemberEmitter>();
    protected SequenceType sequence;
    protected UnionType union;
    protected EnumType enumeration;
    protected SqlIntegerType sqlinteger;
    protected ChoiceType choice;
    protected static final ExpressionEmitter exprEmitter = new ExpressionEmitter();


    public static class SequenceFieldEmitter
    {
        private final Field field;

        private final DepthFirstVisitorEmitter global;


        public SequenceFieldEmitter(Field field, DepthFirstVisitorEmitter global)
        {
            this.field = field;
            this.global = global;
        }


        public String getVisitor()
        {
            TypeInterface type = field.getFieldType();
            return global.getVisitor(type, "node."
                    + AccessorNameEmitter.getGetterName(field) + "()");
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
    }



    public static class ChoiceMemberEmitter
    {
        private final DepthFirstVisitorEmitter global;
        protected final ChoiceMember member;

        private Field field = null;


        public ChoiceMemberEmitter(ChoiceMember choiceMember, DepthFirstVisitorEmitter choiceEmitter)
        {
            member = choiceMember;
            global = choiceEmitter;
        }


        private Field getField()
        {
            if (field != null)
                return field;

            AST node = member.getFirstChild();
            while (node != null)
            {
                int type = node.getType();
                if (type == DataScriptParserTokenTypes.FIELD)
                {
                    field = (Field)node;
                    break;
                }
                node = node.getNextSibling();
            }
            return field;
        }


        public boolean getIsDefault()
        {
            return member.getType() == DataScriptParserTokenTypes.DEFAULT;
        }


        public List<Expression> getCases()
        {
            List<Expression> caseList = new ArrayList<Expression>();

            AST node = member.getFirstChild();
            while (node != null)
            {
                if (node instanceof Expression)
                {
                    TypeInterface type = ((Expression)node).getExprType();
                    if (!(type instanceof Field))
                    {
                        Expression e = (Expression)node;
                        caseList.add(e);
                    }
                }
                node = node.getNextSibling();
            }

            return caseList;
        }


        public String getVisitor()
        {
            TypeInterface type = getField().getFieldType();
            return global.getVisitor(type, "node."
                    + AccessorNameEmitter.getGetterName(getField()) + "()");
        }
    }



    public DepthFirstVisitorEmitter(String outPathName, String defaultPackageName)
    {
        super(outPathName, defaultPackageName);
    }


    @Override
    public void beginRoot(AST rootNode)
    {
        findAllPackageNames(rootNode, allPackageNames);
        setPackageName(rootNode.getFirstChild());
        openOutputFile(dir, "__DepthFirstVisitor.java");
        try
        {
            Template tpl = cfg.getTemplate("java/DepthFirstVisitor.ftl");
            tpl.process(this, writer);
        }
        catch (Exception e)
        {
            throw new DataScriptException(e);
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
        catch (Exception e)
        {
            throw new DataScriptException(e);
        }
        writer.close();
    }


    @Override
    public void beginSequence(AST s)
    {
        sequence = (SequenceType) s;

        fields.clear();
        for (Field field : sequence.getFields())
        {
            SequenceFieldEmitter fe = new SequenceFieldEmitter(field, this);
            fields.add(fe);
        }

        try
        {
            Template tpl = cfg.getTemplate("java/DepthFirstSequence.ftl");
            tpl.process(this, writer);
        }
        catch (Exception e)
        {
            throw new DataScriptException(e);
        }
    }


    @Override
    public void beginUnion(AST u)
    {
        union = (UnionType) u;

        fields.clear();
        for (Field field : union.getFields())
        {
            SequenceFieldEmitter fe = new SequenceFieldEmitter(field, this);
            fields.add(fe);
        }

        try
        {
            Template tpl = cfg.getTemplate("java/DepthFirstUnion.ftl");
            tpl.process(this, writer);
        }
        catch (Exception e)
        {
            throw new DataScriptException(e);
        }
    }


    @Override
    public void beginChoice(AST c)
    {
        choice = (ChoiceType) c;

        members.clear();
        for (ChoiceMember choiceMember : choice.getChoiceMembers())
        {
            ChoiceMemberEmitter fe = new ChoiceMemberEmitter(choiceMember, this);
            members.add(fe);
        }

        try
        {
            Template tpl = cfg.getTemplate("java/DepthFirstChoice.ftl");
            tpl.process(this, writer);
        }
        catch (Exception e)
        {
            throw new DataScriptException(e);
        }
    }


    @Override
    public void beginEnumeration(AST e)
    {
        enumeration = (EnumType) e;

        try
        {
            Template tpl = cfg.getTemplate("java/DepthFirstEnumeration.ftl");
            tpl.process(this, writer);
        }
        catch (Exception ex)
        {
            throw new DataScriptException(ex);
        }
    }


    @Override
    public void beginSqlInteger(AST s)
    {
        sqlinteger = (SqlIntegerType) s;

        fields.clear();
        for (Field field : sqlinteger.getFields())
        {
            SequenceFieldEmitter fe = new SequenceFieldEmitter(field, this);
            fields.add(fe);
        }

        try
        {
            Template tpl = cfg.getTemplate("java/DepthFirstSqlInteger.ftl");
            tpl.process(this, writer);
        }
        catch (Exception e)
        {
            throw new DataScriptException(e);
        }
    }


    /** ***************************************************************** */

    public SequenceType getSequenceType()
    {
        return sequence;
    }


    public UnionType getUnionType()
    {
        return union;
    }


    public ChoiceType getChoiceType()
    {
        return choice;
    }


    public EnumType getEnumerationType()
    {
        return enumeration;
    }


    public SqlIntegerType getSqlIntegerType()
    {
        return sqlinteger;
    }


    public String getSequencePackageName()
    {
        return sequence.getPackage().getPackageName();
    }


    public String getUnionPackageName()
    {
        return union.getPackage().getPackageName();
    }


    public String getChoicePackageName()
    {
        return choice.getPackage().getPackageName();
    }


    public String getEnumPackageName()
    {
        return enumeration.getPackage().getPackageName();
    }


    public String getSqlIntPackageName()
    {
        return sqlinteger.getPackage().getPackageName();
    }


    public String getVisitor(TypeInterface type, String nodeName)
    {
        type = TypeReference.resolveType(type);
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
            }
            buffer.append("(");
            buffer.append(nodeName);
            if (length != null)
            {
                buffer.append(", ");
                buffer.append(exprEmitter.emit(length, "node"));
            }
            buffer.append(", arg)");
        }
        else if (type instanceof datascript.ast.ArrayType)
        {
            buffer.append("visitArray(" + nodeName + ", arg)");
        }
        else if (type instanceof datascript.ast.StringType)
        {
            buffer.append("visitString(" + nodeName + ", arg)");
        }
        else
        {
            /*
             * String typeName = typeEmitter.getTypeName(type);
             * buffer.append("visit"); buffer.append(typeName.substring(0,
             * 1).toUpperCase()); buffer.append(typeName.substring(1,
             * typeName.length()));
             * 
             * buffer.append("("); buffer.append(nodeName); buffer.append(",
             * arg)");
             */
            buffer.append(nodeName);
            buffer.append(".accept(this, arg)");
        }
        return buffer.toString();
    }


    public String getElementType(Field field)
    {
        String result = null;
        TypeInterface type = field.getFieldType();
        if (type instanceof ArrayType)
        {
            ArrayType array = (ArrayType) type;
            result = getTypeName(array.getElementType());
        }
        return result;
    }


    public String getElementVisitor(Field field)
    {
        String result = null;
        TypeInterface type = field.getFieldType();
        if (type instanceof ArrayType)
        {
            ArrayType array = (ArrayType) type;
            TypeInterface elemType = array.getElementType();
            result = getVisitor(elemType, "__elem");
        }

        return result;
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


    public List<ChoiceMemberEmitter> getMembers()
    {
        return members;
    }


    public String getSelector()
    {
        String selector = null;

        AST node = choice.getSelectorAST();
        if (node != null)
        {
            ExpressionEmitter ee = new ExpressionEmitter();
            selector = ee.emit((Expression) node, "node");
        }

//        if (selector == null)
//            throw new ComputeError("missing selector");
        return selector;
    }
}
