/* BSD License
 *
 * Copyright (c) 2006, Henrik Wedekind, Harman/Becker Automotive Systems
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

import datascript.ast.CompoundType;
import datascript.ast.DataScriptException;
import datascript.ast.Field;
import datascript.ast.SqlIntegerType;
import freemarker.template.Configuration;
import freemarker.template.Template;



/**
 * @author HWedekind
 * 
 */
public class SqlIntegerEmitter extends CompoundEmitter
{
    private final List<IntegerFieldEmitter> fields = 
        new ArrayList<IntegerFieldEmitter>();

    private int totalTypeSize;
    private SqlIntegerType integerType;
    private PrintWriter writer;



    public class IntegerFieldEmitter extends SequenceEmitter.SequenceFieldEmitter
    {
        public IntegerFieldEmitter(Field field, SqlIntegerEmitter global)
        {
            super(field, global);
        }


        public long getBitmask()
        {
            int bitSize = field.getFieldType().sizeof(null).integerValue().intValue();
            return (1L << bitSize) - 1L;
        }


        public long getBitsize()
        {
            return field.getFieldType().sizeof(null).integerValue().intValue();
        }
    }



    public SqlIntegerEmitter(JavaEmitter j, SqlIntegerType integerType)
    {
        super(j);
        this.integerType = integerType;
    }


    public String getName()
    {
        return integerType.getName();
    }


    public SqlIntegerType getSqlIntegerType()
    {
        return integerType;
    }


    @Override
    public CompoundType getCompoundType()
    {
        return integerType;
    }


    public void setWriter(PrintWriter writer)
    {
        this.writer = writer;
    }


    public void emit(Configuration cfg, SqlIntegerType integerType2)
    {
        totalTypeSize = 0;
        fields.clear();
        for (Field field : integerType.getFields())
        {
            IntegerFieldEmitter fe = new IntegerFieldEmitter(field, this);
            fields.add(fe);
            totalTypeSize += field.getFieldType().sizeof(null).integerValue().intValue();
        }

        try
        {
            Template tpl = cfg.getTemplate("java/SqlIntegerBegin.ftl");
            tpl.process(this, writer);

            for (IntegerFieldEmitter field : fields)
            {
                field.emit(writer, cfg);
            }

            tpl = cfg.getTemplate("java/SqlIntegerRead.ftl");
            tpl.process(this, writer);
        }
        catch (Exception e)
        {
            throw new DataScriptException(e);
        }
    }


    public List<IntegerFieldEmitter> getFields()
    {
        return fields;
    }


    public String getFittingType()
    {
        if (totalTypeSize <= 8)
            return "byte";
        else if (totalTypeSize <= 16)
            return "short";
        else if (totalTypeSize <= 32)
            return "int";
        else if (totalTypeSize <= 64)
            return "long";
        else /* if (totalTypeSize > 64) */
            throw new RuntimeException("total size of all fields in '" + getName() + "' exceed 64 bits");
    }
}
