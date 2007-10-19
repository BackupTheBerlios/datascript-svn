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

import datascript.antlr.util.ToolContext;
import datascript.ast.CompoundType;
import datascript.ast.DataScriptException;
import datascript.ast.Field;
import datascript.ast.Parameter;
import datascript.ast.UnionType;

import freemarker.template.Configuration;
import freemarker.template.Template;



public class UnionEmitter extends CompoundEmitter
{
    private final List<UnionFieldEmitter> fields = 
        new ArrayList<UnionFieldEmitter>();

    private UnionType union;



    public static class UnionFieldEmitter extends FieldEmitter
    {
        private static Template tpl = null;


        public UnionFieldEmitter(Field f, CompoundEmitter j)
        {
            super(f, j);
        }


        public void emit(PrintWriter writer, Configuration cfg) throws Exception
        {
            //super.emit(writer, cfg, "java/UnionFieldAccessor.ftl");
            if (tpl == null)
                tpl = cfg.getTemplate("java/UnionFieldAccessor.ftl");
            tpl.process(this, writer);
        }


        public String getCheckerName()
        {
            return AccessorNameEmitter.getCheckerName(field);
        }


        public String getText()
        {
            return field.toString();
        }


        public String getClassName()
        {
            return TypeNameEmitter.getClassName(field.getFieldType());
        }
    }


    public UnionEmitter(JavaDefaultEmitter j, UnionType union)
    {
        super(j);
        this.union = union;
    }


    public UnionType getUnionType()
    {
        return union;
    }


    public CompoundType getCompoundType()
    {
        return union;
    }

    public void begin(Configuration cfg)
    {
        fields.clear();
        for (Field field : union.getFields())
        {
            if (field.getAlignment() != null)
                ToolContext.logError(field, "align is not allowed in union");
            UnionFieldEmitter fe = new UnionFieldEmitter(field, this);
            fields.add(fe);
        }
        params.clear();
        for (Parameter param : union.getParameters())
        {
            CompoundParameterEmitter p = new CompoundParameterEmitter(param);
            params.add(p);
        }

        try
        {
            Template tpl = cfg.getTemplate("java/UnionBegin.ftl");
            tpl.process(this, writer);

            for (UnionFieldEmitter field : fields)
            {
                field.emit(writer, cfg);
            }

            for (CompoundParameterEmitter param : params)
            {
                param.emit(writer, cfg);
            }

            tpl = cfg.getTemplate("java/UnionRead.ftl");
            tpl.process(this, writer);

            tpl = cfg.getTemplate("java/UnionWrite.ftl");
            tpl.process(this, writer);
        }
        catch (Exception e)
        {
            throw new DataScriptException(e);
        }
    }


    public void end(Configuration cfg)
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


    public List<UnionFieldEmitter> getFields()
    {
        return fields;
    }

}
