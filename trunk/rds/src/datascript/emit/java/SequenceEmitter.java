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

import datascript.ast.BitFieldType;
import datascript.ast.CompoundType;
import datascript.ast.DataScriptException;
import datascript.ast.Field;
import datascript.ast.FunctionType;
import datascript.ast.Parameter;
import datascript.ast.SequenceType;
import datascript.ast.StdIntegerType;
import datascript.ast.TypeInterface;
import datascript.ast.TypeReference;
import freemarker.template.Configuration;
import freemarker.template.Template;



public class SequenceEmitter extends CompoundEmitter
{
    private final List<SequenceFunctionFMEmitter> functions = 
        new ArrayList<SequenceFunctionFMEmitter>();
    private final List<SequenceFieldFMEmitter> fields = 
        new ArrayList<SequenceFieldFMEmitter>();

    private SequenceType seq;
    private SequenceFieldEmitter fieldEmitter;



    public static class SequenceFunctionFMEmitter
    {
        private final FunctionType func;
        private static Template tpl = null;

        public SequenceFunctionFMEmitter(FunctionType func)
        {
            this.func = func;
        }


        public void emitFreeMarker(PrintWriter writer, Configuration cfg) throws Exception
        {
            if (tpl == null)
                tpl = cfg.getTemplate("java/FunctionTmpl.ftl");
            tpl.process(this, writer);
        }


        public String getName()
        {
            return func.getName();
        }


        public String getResult()
        {
            ExpressionEmitter ee = new ExpressionEmitter();
            return ee.emit(func.getResult());
        }


        public String getReturnType()
        {
            TypeInterface returnType = func.getReturnType();
            return TypeNameEmitter.getTypeName(returnType);
        }
    }



    public static class SequenceFieldFMEmitter extends FieldEmitter
    {
        private static Template tpl = null;

        private final TypeInterface type;

        private String optional = null;
        private String constraint = null;
        private String label = null;


        public SequenceFieldFMEmitter(Field f, CompoundEmitter j)
        {
            super(j);
            field = f;
            type = TypeReference.resolveType(field.getFieldType());
        }


        public void emit(Field f)
        {
            throw new RuntimeException("emit does not exist for SequenceFieldFMEmitter");
        }


        public void emitFreeMarker(PrintWriter writer, Configuration cfg) throws Exception
        {
            if (tpl == null)
                tpl = cfg.getTemplate("java/SequenceFieldAccessor.ftl");
            tpl.process(this, writer);
        }


        public String getReadField()
        {
            return getCompoundEmitter().readField(field);
        }


        public String getWriteField()
        {
            return getCompoundEmitter().writeField(field);
        }


        public String getOptionalClause()
        {
            if (optional == null)
            {
                optional = getCompoundEmitter().getOptionalClause(field);
            }
            return optional;
        }


        public String getConstraint()
        {
            if (constraint == null)
            {
                constraint = getCompoundEmitter().getConstraint(field);
            }
            return constraint;
        }


        public String getLabelExpression()
        {
            if (label == null)
            {
                label = getCompoundEmitter().getLabelExpression(field);
            }
            return label;
        }


        public String getName()
        {
            return field.getName();
        }


        public String getCanonicalTypeName()
        {
            return type.getClass().getCanonicalName();
        }


        public String getJavaTypeName()
        {
            return TypeNameEmitter.getTypeName(field.getFieldType());
        }


        public String getGetterName()
        {
            return AccessorNameEmitter.getGetterName(field);
        }


        public String getSetterName()
        {
            return AccessorNameEmitter.getSetterName(field);
        }


        public String getIndicatorName()
        {
            return AccessorNameEmitter.getIndicatorName(field);
        }


        public int getBitFieldLength()
        {
            if (type instanceof BitFieldType)
                return ((BitFieldType)type).getLength();
            throw new RuntimeException("type of field " + field.getName() + "is not a BitFieldType");
        }


        public boolean getIsUINT64()
        {
            if (type instanceof StdIntegerType)
                return ((StdIntegerType)type).getType() == datascript.antlr.DataScriptParserTokenTypes.UINT64;
            throw new RuntimeException("type of field " + field.getName() + "is not a StdIntegerType");
        }
    }



    public SequenceEmitter(JavaDefaultEmitter j, SequenceType sequence)
    {
        super(j);
        seq = sequence;
        fieldEmitter = new SequenceFieldEmitter(this);
    }


    public void setWriter(PrintWriter writer)
    {
        super.setWriter(writer);
        fieldEmitter.setWriter(writer);
    }


    public SequenceType getSequenceType()
    {
        return seq;
    }


    public CompoundType getCompoundType()
    {
        return seq;
    }


    public SequenceFieldEmitter getFieldEmitter()
    {
        return fieldEmitter;
    }


    public void beginFreemarker(Configuration cfg)
    {
        fields.clear();
        for (Field field : seq.getFields())
        {
            SequenceFieldFMEmitter fe = new SequenceFieldFMEmitter(field, this);
            fields.add(fe);
        }
        params.clear();
        for (Parameter param : seq.getParameters())
        {
            CompoundParameterFMEmitter p = new CompoundParameterFMEmitter(param);
            params.add(p);
        }
        functions.clear();
        for (FunctionType func : seq.getFunctions())
        {
            SequenceFunctionFMEmitter f = new SequenceFunctionFMEmitter(func);
            functions.add(f);
        }

        try
        {
            Template tpl = cfg.getTemplate("java/SequenceBegin.ftl");
            tpl.process(this, writer);

            for (SequenceFieldFMEmitter field : fields)
            {
                field.emitFreeMarker(writer, cfg);
            }

            for (CompoundParameterFMEmitter param : params)
            {
                param.emitFreeMarker(writer, cfg);
            }

            for (SequenceFunctionFMEmitter func : functions)
            {
                func.emitFreeMarker(writer, cfg);
            }

            tpl = cfg.getTemplate("java/SequenceRead.ftl");
            tpl.process(this, writer);

            tpl = cfg.getTemplate("java/SequenceWrite.ftl");
            tpl.process(this, writer);
        }
        catch (Exception e)
        {
            throw new DataScriptException(e);
        }
    }


    public void endFreemarker(Configuration cfg)
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
    }



    public List<SequenceFieldFMEmitter> getFields()
    {
        return fields;
    }

}
