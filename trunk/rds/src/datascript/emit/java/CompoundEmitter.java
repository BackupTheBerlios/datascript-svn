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
import datascript.ast.CompoundType;
import datascript.ast.DataScriptException;
import datascript.ast.EnumType;
import datascript.ast.Expression;
import datascript.ast.Field;
import datascript.ast.IntegerType;
import datascript.ast.StringType;
import datascript.ast.Parameter;
import datascript.ast.StdIntegerType;
import datascript.ast.TypeInstantiation;
import datascript.ast.TypeInterface;
import datascript.ast.TypeReference;
import datascript.ast.Value;

import datascript.jet.java.ArrayRead;
import datascript.jet.java.ArrayWrite;
import freemarker.template.Configuration;
import freemarker.template.Template;



abstract public class CompoundEmitter
{
    protected final List<CompoundParameterFMEmitter> params = 
        new ArrayList<CompoundParameterFMEmitter>();

    private JavaDefaultEmitter global;
    private ExpressionEmitter exprEmitter = new ExpressionEmitter();
    private ArrayRead arrayReadTmpl = new ArrayRead();
    private ArrayWrite arrayWriteTmpl = new ArrayWrite();
    protected PrintWriter writer;
    protected ParameterEmitter paramEmitter;

    private StringBuilder buffer;
    private String formalParams;
    private String actualParams;



    public static class CompoundParameterFMEmitter
    {
        private final Parameter param;
        private static Template tpl = null;


        public CompoundParameterFMEmitter(Parameter param)
        {
            this.param = param;
        }


        public void emitFreeMarker(PrintWriter writer, Configuration cfg)
                throws Exception
        {
            if (tpl == null)
                tpl = cfg.getTemplate("java/ParameterAccessor.ftl");
            tpl.process(this, writer);
        }


        public String getName()
        {
            return param.getName();
        }


        public String getJavaTypeName()
        {
            return TypeNameEmitter.getTypeName(param.getType());
        }


        public String getGetterName()
        {
            return AccessorNameEmitter.getGetterName(param);
        }


        public String getSetterName()
        {
            return AccessorNameEmitter.getSetterName(param);
        }
    }



    public static class ArrayFMEmitter
    {
        private static final ExpressionEmitter ee = new ExpressionEmitter();

        private final Field field;
        private final ArrayType array;


        public ArrayFMEmitter(Field field, ArrayType array)
        {
            this.field = field;
            this.array = array;
        }


        public String getElType()
        {
            return TypeNameEmitter.getTypeName(array.getElementType());
        }


        public boolean getIsVariable()
        {
            return array.isVariable();
        }


        public String getGetterName()
        {
            return AccessorNameEmitter.getGetterName(field);
        }


        public String getSetterName()
        {
            return AccessorNameEmitter.getSetterName(field);
        }


        public String getActualParameterList()
        {
            StringBuilder buffer = new StringBuilder();
            TypeInterface elType = array.getElementType();
            if (elType instanceof TypeInstantiation)
            {
                ExpressionEmitter exprEmitter = new ExpressionEmitter();
                TypeInstantiation inst = (TypeInstantiation) elType;
                Iterable<Expression> arguments = inst.getArguments();
                for (Expression arg : arguments)
                {
                    String javaArg = exprEmitter.emit(arg);
                    buffer.append(", ");
                    buffer.append(javaArg);
                }
            }
            return buffer.toString();
        }


        public String getLengthExpr()
        {
            return ee.emit(array.getLengthExpression());
        }
    }



    public CompoundEmitter(JavaDefaultEmitter j)
    {
        this.global = j;
        paramEmitter = new ParameterEmitter(this);
    }


    abstract public CompoundType getCompoundType();


    public JavaDefaultEmitter getGlobal()
    {
        return global;
    }


    protected void reset()
    {
        formalParams = null;
        actualParams = null;
    }


    public void setWriter(PrintWriter writer)
    {
        this.writer = writer;
        paramEmitter.setWriter(writer);
    }


    public String readField(Field field)
    {
        buffer = new StringBuilder();
        TypeInterface type = field.getFieldType();
        type = TypeReference.resolveType(type);
        if (type instanceof IntegerType)
        {
            readIntegerField(field, (IntegerType) type);
        }
        else if (type instanceof CompoundType)
        {
            readCompoundField(field, (CompoundType) type);
        }
        else if (type instanceof ArrayType)
        {
            readArrayField(field, (ArrayType) type);
        }
        else if (type instanceof EnumType)
        {
            readEnumField(field, (EnumType) type);
        }
        else if (type instanceof TypeInstantiation)
        {
            TypeInstantiation inst = (TypeInstantiation) type;
            readInstantiatedField(field, inst);
        }
        else if (type instanceof StringType)
        {
            StringType inst = (StringType) type;
            readStringField(field, inst);
        }
        else
        {
            throw new InternalError("unhandled type: "
                    + type.getClass().getName());
        }
        return buffer.toString();
    }


    private void readIntegerField(Field field, IntegerType type)
    {
        buffer.append(AccessorNameEmitter.getSetterName(field));
        buffer.append("(");
        readIntegerValue(field, type);
        buffer.append(");");
    }


    private void readIntegerValue(Field field, IntegerType type)
    {
        String methodSuffix;
        String cast = "";
        String arg = "";
        switch (type.getType())
        {
            case DataScriptParserTokenTypes.INT8:
                methodSuffix = "Byte";
                break;

            case DataScriptParserTokenTypes.INT16:
                methodSuffix = "Short";
                break;

            case DataScriptParserTokenTypes.INT32:
                methodSuffix = "Int";
                break;

            case DataScriptParserTokenTypes.INT64:
                methodSuffix = "Long";
                break;

            case DataScriptParserTokenTypes.UINT8:
                methodSuffix = "UnsignedByte";
                cast = "(short) ";
                break;

            case DataScriptParserTokenTypes.UINT16:
                methodSuffix = "UnsignedShort";
                break;

            case DataScriptParserTokenTypes.UINT32:
                methodSuffix = "UnsignedInt";
                break;

            case DataScriptParserTokenTypes.UINT64:
                methodSuffix = "BigInteger";
                arg = "64";
                break;

            case DataScriptParserTokenTypes.BIT:
                Expression lengthExpr = ((BitFieldType) type)
                        .getLengthExpression();
                Value lengthValue = lengthExpr.getValue();
                if (lengthValue == null)
                {
                    methodSuffix = "BigInteger";
                }
                else
                {
                    int length = lengthValue.integerValue().intValue();
                    if (length < 64)
                    {
                        methodSuffix = "Bits";
                        cast = "(" + TypeNameEmitter.getTypeName(type) + ") ";
                    }
                    else
                    {
                        methodSuffix = "BigInteger";
                    }
                }
                arg = exprEmitter.emit(lengthExpr);
                break;

            default:
                throw new InternalError("unhandled type = " + type.getType());
        }
        buffer.append(cast);
        buffer.append("__in.read");
        buffer.append(methodSuffix);
        buffer.append("(");
        buffer.append(arg);
        buffer.append(")");
    }


    private void readStringField(Field field, StringType type)
    {
        buffer.append(AccessorNameEmitter.getSetterName(field));
        buffer.append("(__in.readString());");
    }


    private void readCompoundField(Field field, CompoundType type)
    {
        buffer.append(AccessorNameEmitter.getSetterName(field));
        buffer.append("(new ");
        buffer.append(type.getName());
        buffer.append("(__in, __cc));");
    }


    private void readInstantiatedField(Field field, TypeInstantiation inst)
    {
        CompoundType compound = inst.getBaseType();

        buffer.append(AccessorNameEmitter.getSetterName(field));
        buffer.append("(new ");
        buffer.append(compound.getName());
        buffer.append("(__in, __cc");
        appendArguments(inst);
        buffer.append("));");
    }


    private void appendArguments(TypeInstantiation inst)
    {
        CompoundType compound = inst.getBaseType();
        Iterable<Expression> arguments = inst.getArguments();
        if (arguments != null)
        {
            int argIndex = 0;
            for (Expression arg : arguments)
            {
                buffer.append(", ");
                boolean cast = emitTypeCast(compound, arg, argIndex);
                String javaArg = exprEmitter.emit(arg);
                buffer.append(javaArg);
                if (cast)
                {
                    buffer.append(")");
                }
                argIndex++;
            }
        }
    }


    /**
     * Emits a type cast for passing an argument to a parameterized type.
     * @param type              compound type with parameters
     * @param expr              argument expression in type instantiation
     * @param paramIndex        index of argument in argument list
     */
    private boolean emitTypeCast(CompoundType type, Expression expr,
            int paramIndex)
    {
        boolean cast = false;
        Parameter param = type.getParameterAt(paramIndex);
        TypeInterface paramType = TypeReference.resolveType(param.getType());
        if (paramType instanceof StdIntegerType)
        {
            StdIntegerType intType = (StdIntegerType) paramType;
            switch (intType.getType())
            {
                case DataScriptParserTokenTypes.INT8:
                    buffer.append("(byte)(");
                    cast = true;
                    break;

                case DataScriptParserTokenTypes.UINT8:
                case DataScriptParserTokenTypes.INT16:
                    buffer.append("(short)(");
                    cast = true;
                    break;
            }
        }
        return cast;
    }


    private void readArrayField(Field field, ArrayType array)
    {
        String elTypeJavaName = TypeNameEmitter.getTypeName(array);
        if (elTypeJavaName.startsWith("ObjectArray"))
        {
            if (global.getUseFreeMarker())
            {
                try
                {
                    ArrayFMEmitter ae = new ArrayFMEmitter(field, array);
                    Template tpl = global.getTemplateConfig().getTemplate("java/ArrayRead.ftl");
                    tpl.process(ae, writer);
                }
                catch (Exception e)
                {
                    throw new DataScriptException(e);
                }
            }
            else
            {
                String elTypeName = TypeNameEmitter.getTypeName(array
                        .getElementType());
                ArrayEmitter arrayEmitter = new ArrayEmitter(field, array,
                        elTypeName);
                String result = arrayReadTmpl.generate(arrayEmitter);
                buffer.append(result);
            }
        }
        else
        {
            Expression length = array.getLengthExpression();
            buffer.append(AccessorNameEmitter.getSetterName(field));
            buffer.append("(new ");
            buffer.append(elTypeJavaName);
            buffer.append("(__in, (int)(");
            buffer.append(getLengthExpression(length));
            buffer.append(")");
            TypeInterface elType = array.getElementType();
            if (elType instanceof BitFieldType)
            {
                BitFieldType bitField = (BitFieldType) elType;
                Expression numBits = bitField.getLengthExpression();
                buffer.append(", ");
                buffer.append(getLengthExpression(numBits));
            }
            buffer.append("));");
        }
    }


    private void readEnumField(Field field, EnumType type)
    {
        IntegerType baseType = (IntegerType) type.getBaseType();
        buffer.append(AccessorNameEmitter.getSetterName(field));
        buffer.append("(");
        buffer.append(type.getName());
        buffer.append(".toEnum(");
        readIntegerValue(field, baseType);
        buffer.append("));");
    }


    private String getLengthExpression(Expression expr)
    {
        if (expr == null)
        {
            // TODO handle variable length
            //throw new InternalError("Variable length arrays are not implemented now!");
            return "-1";
        }
        return exprEmitter.emit(expr);
    }


    public String getConstraint(Field field)
    {
        String result = null;
        Expression expr = field.getCondition();
        if (expr != null)
        {
            result = exprEmitter.emit(expr);
        }
        else
        {
            expr = field.getInitializer();
            if (expr != null)
            {
                result = field.getName() + " == " + exprEmitter.emit(expr);
            }
        }
        return result;
    }


    public String getOptionalClause(Field field)
    {
        String result = null;
        Expression expr = field.getOptionalClause();
        if (expr != null)
        {
            result = exprEmitter.emit(expr);
        }
        return result;
    }


    public void buildParameterLists()
    {
        StringBuilder formal = new StringBuilder();
        StringBuilder actual = new StringBuilder();
        CompoundType compound = getCompoundType();
        for (Parameter param : compound.getParameters())
        {
            String paramName = param.getName();
            TypeInterface paramType = TypeReference
                    .resolveType(param.getType());

            String typeName = TypeNameEmitter.getTypeName(paramType);
            formal.append(", ");
            formal.append(typeName);
            formal.append(" ");
            formal.append(paramName);

            actual.append(", ");
            actual.append(paramName);
        }
        formalParams = formal.toString();
        actualParams = actual.toString();
    }


    public String getFormalParameterList()
    {
        if (formalParams == null)
        {
            buildParameterLists();
        }
        return formalParams;
    }


    public String getActualParameterList()
    {
        if (actualParams == null)
        {
            buildParameterLists();
        }
        return actualParams;
    }


    public String getLabelExpression(Field field)
    {
        String result = null;
        Expression label = field.getLabel();
        if (label != null)
        {
            StringBuilder buffer = new StringBuilder();
            AST labelBase = label.getNextSibling();
            if (labelBase != null)
            {
                String name = labelBase.getText();
                buffer.append("((");
                buffer.append(name);
                buffer.append(")__cc.find(\"");
                buffer.append(name);
                buffer.append("\")).");
            }
            buffer.append("__fpos + 8*");
            String labelExpr = exprEmitter.emit(label);
            buffer.append(labelExpr);
            result = buffer.toString();
        }
        return result;
    }


    public String writeField(Field field)
    {
        buffer = new StringBuilder();
        TypeInterface type = field.getFieldType();
        type = TypeReference.resolveType(type);
        if (type instanceof IntegerType)
        {
            writeIntegerField(field, (IntegerType) type);
        }
        else if (type instanceof CompoundType)
        {
            writeCompoundField(field, (CompoundType) type);
        }
        else if (type instanceof ArrayType)
        {
            writeArrayField(field, (ArrayType) type);
        }
        else if (type instanceof EnumType)
        {
            writeEnumField(field, (EnumType) type);
        }
        else if (type instanceof TypeInstantiation)
        {
            writeInstantiatedField(field, (TypeInstantiation) type);
        }
        else if (type instanceof StringType)
        {
            writeStringType(field, (StringType) type);
        }
        else
        {
            throw new InternalError("unhandled type: "
                    + type.getClass().getName());
        }
        return buffer.toString();
    }


    private void writeIntegerField(Field field, IntegerType type)
    {
        writeIntegerValue(AccessorNameEmitter.getGetterName(field) + "()", type);
    }


    private void writeIntegerValue(String value, IntegerType type)
    {
        String methodSuffix;
        String cast = "";
        String arg = "";
        switch (type.getType())
        {
            case DataScriptParserTokenTypes.INT8:
                methodSuffix = "Byte";
                break;

            case DataScriptParserTokenTypes.INT16:
                methodSuffix = "Short";
                break;

            case DataScriptParserTokenTypes.INT32:
                methodSuffix = "Int";
                break;

            case DataScriptParserTokenTypes.INT64:
                methodSuffix = "Long";
                break;

            case DataScriptParserTokenTypes.UINT8:
                methodSuffix = "Byte";
                cast = "(short) ";
                break;

            case DataScriptParserTokenTypes.UINT16:
                methodSuffix = "Short";
                break;

            case DataScriptParserTokenTypes.UINT32:
                methodSuffix = "UnsignedInt";
                cast = "(int) ";
                break;

            case DataScriptParserTokenTypes.UINT64:
                methodSuffix = "BigInteger";
                arg = "64";
                break;

            case DataScriptParserTokenTypes.BIT:
                Expression lengthExpr = ((BitFieldType) type)
                        .getLengthExpression();
                Value lengthValue = lengthExpr.getValue();
                if (lengthValue == null)
                {
                    methodSuffix = "BigInteger";
                }
                else
                {
                    int length = lengthValue.integerValue().intValue();
                    if (length < 64)
                    {
                        methodSuffix = "Bits";
                        cast = "(" + TypeNameEmitter.getTypeName(type) + ") ";
                    }
                    else
                    {
                        methodSuffix = "BigInteger";
                    }
                }
                arg = exprEmitter.emit(lengthExpr);
                break;

            default:
                throw new InternalError("unhandled type = " + type.getType());
        }
        buffer.append("__out.write");
        buffer.append(methodSuffix);
        buffer.append("(");
        buffer.append(cast);
        buffer.append(value);
        if (arg.length() != 0)
        {
            buffer.append(", ");
            buffer.append(arg);
        }
        buffer.append(");");
    }


    private void writeStringType(Field field, StringType type)
    {
        buffer.append("__out.writeString(");
        buffer.append(AccessorNameEmitter.getGetterName(field));
        buffer.append("());");
    }


    private void writeCompoundField(Field field, CompoundType type)
    {
        buffer.append(AccessorNameEmitter.getGetterName(field));
        buffer.append("().write(__out, __cc);");
    }


    private void writeInstantiatedField(Field field, TypeInstantiation inst)
    {
        String getter = AccessorNameEmitter.getGetterName(field);
        setParameters(getter + "()", inst);
        buffer.append(getter);
        buffer.append("().write(__out, __cc);");
    }


    private void setParameters(String lhs, TypeInstantiation inst)
    {
        CompoundType compound = inst.getBaseType();
        Iterable<Expression> arguments = inst.getArguments();
        if (arguments != null)
        {
            int argIndex = 0;
            for (Expression arg : arguments)
            {
                Parameter param = compound.getParameterAt(argIndex);
                String setter = AccessorNameEmitter.getSetterName(param);
                buffer.append(lhs);
                buffer.append(".");
                buffer.append(setter);
                buffer.append("(");
                boolean cast = emitTypeCast(compound, arg, argIndex);
                String javaArg = exprEmitter.emit(arg);
                buffer.append(javaArg);
                if (cast)
                {
                    buffer.append(")");
                }
                buffer.append(");\n                ");
                argIndex++;
            }
        }
    }


    private void writeArrayField(Field field, ArrayType array)
    {
        String elTypeJavaName = TypeNameEmitter.getTypeName(array);
        if (elTypeJavaName.startsWith("ObjectArray"))
        {
            if (global.getUseFreeMarker())
            {
                try
                {
                    ArrayFMEmitter ae = new ArrayFMEmitter(field, array);
                    Template tpl = global.getTemplateConfig().getTemplate("java/ArrayWrite.ftl");
                    tpl.process(ae, writer);
                }
                catch (Exception e)
                {
                    throw new DataScriptException(e);
                }
            }
            else
            {
                String elTypeName = TypeNameEmitter.getTypeName(array
                        .getElementType());
                ArrayEmitter arrayEmitter = new ArrayEmitter(field, array,
                        elTypeName);
                String result = arrayWriteTmpl.generate(arrayEmitter);
                buffer.append(result);
            }
        }
        else
        {
            buffer.append(AccessorNameEmitter.getGetterName(field));
            buffer.append("().write(__out, __cc);");
        }

    }


    private void writeEnumField(Field field, EnumType type)
    {
        IntegerType baseType = (IntegerType) type.getBaseType();
        writeIntegerValue(AccessorNameEmitter.getGetterName(field)
                + "().getValue()", baseType);
    }


    /**** interface to freemarker FileHeader.inc template ****/

    public String getRdsVersion()
    {
        return global.getRDSVersion();
    }


    public String getPackageName()
    {
        return global.getPackageName();
    }


    public String getRootPackageName()
    {
        return datascript.ast.Package.getRoot().getPackageName();
    }


    public boolean getEqualsCanThrowExceptions()
    {
        return global.getThrowsException();
    }


    public String getPackageImports()
    {
        return global.getPackageImports();
    }


    public String getClassName()
    {
        return getCompoundType().getName();
    }
}
