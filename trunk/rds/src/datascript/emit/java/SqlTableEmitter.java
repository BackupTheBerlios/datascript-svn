/* BSD License
 *
 * Copyright (c) 2006, Harald Wellmann, Harman/Becker Automotive Systems
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
import datascript.antlr.util.TokenAST;
import datascript.ast.ArrayType;
import datascript.ast.BitFieldType;
import datascript.ast.CompoundType;
import datascript.ast.DataScriptException;
import datascript.ast.Field;
import datascript.ast.IntegerType;
import datascript.ast.Parameter;
import datascript.ast.SqlIntegerType;
import datascript.ast.SqlTableType;
import datascript.ast.StringType;
import datascript.ast.TypeInterface;
import datascript.ast.TypeReference;
import freemarker.template.Configuration;
import freemarker.template.Template;



/**
 * @author HWellmann
 * 
 */
public class SqlTableEmitter extends CompoundEmitter
{
    private final List<CompoundEmitter.CompoundParameterEmitter> parameters = 
        new ArrayList<CompoundEmitter.CompoundParameterEmitter>();
    private final List<TableFieldEmitter> fields = 
        new ArrayList<TableFieldEmitter>();

    private SqlTableType tableType;



    public class TableFieldEmitter
    {
        private final Field field;
        private final SqlTableEmitter global;


        public TableFieldEmitter(Field field, SqlTableEmitter global)
        {
            this.field = field;
            this.global = global;
        }


        public String getName()
        {
            return field.getName();
        }


        public int getTypeSize()
        {
            TypeInterface ftype = TypeReference.getBaseType(field.getFieldType());
            if ((ftype instanceof SqlIntegerType) || (ftype instanceof IntegerType))
            {
                int typeSize = field.sizeof(null).integerValue().intValue();
                if (typeSize <= 8)
                    return 8;
                else if (typeSize <= 16)
                    return 16;
                else if (typeSize <= 24)
                    return 24;
                else if (typeSize <= 32)
                    return 32;
                else if (typeSize <= 48)
                    return 48;
                else if (typeSize <= 64)
                    return 64;
                else /* if (totalTypeSize > 64) */
                    throw new RuntimeException("size of type '" + field.getName() + 
                            "' in '" + global.getName() + "' exceed 64 bits");
            }
            else
            {
                return 9999;
            }
        }


        public String getSqlType()
        {
            String retval;
            TypeInterface ftype = TypeReference.getBaseType(field.getFieldType());

            if (ftype instanceof ArrayType)
            {
                int count = ftype.getLength();
                ftype = TypeReference.getBaseType(((ArrayType) ftype).getElementType());
                if (!(ftype instanceof IntegerType))
                    retval = "BLOB";
                else if (((IntegerType)ftype).getType() == DataScriptParserTokenTypes.UINT8)
                {
                    retval = "CHAR(" + count + ")";
                }
                else
                    retval = "BLOB";

            }
            else if (ftype instanceof BitFieldType)
            {
                if (((BitFieldType)ftype).getLength() < 64)
                    retval = "INTEGER";
                else
                    retval = "BLOB";
            }
            else if ((ftype instanceof SqlIntegerType) || (ftype instanceof IntegerType))
            {
                retval = "INTEGER";
            }
            else if (ftype instanceof StringType)
            {
                retval = "VARCHAR";
            }
            else
            {
                retval = "BLOB";
            }
            return retval;
        }


        public String getSqlConstraint()
        {
            return field.getSqlConstraint();
        }
    }



    public SqlTableEmitter(JavaEmitter j, SqlTableType table)
    {
        super(j);
        this.tableType = table;
    }


    @Override
    public CompoundType getCompoundType()
    {
        return tableType;
    }


    public SqlTableType getSqlTableType()
    {
        return tableType;
    }


    @Override
    public void setWriter(PrintWriter writer)
    {
        super.setWriter(writer);
    }


    public void emit(Configuration cfg, SqlTableType table)
    {
        parameters.clear();
        for (Parameter param : tableType.getParameters())
        {
            CompoundEmitter.CompoundParameterEmitter pe = 
                new CompoundEmitter.CompoundParameterEmitter(param);
            parameters.add(pe);
        }
        fields.clear();
        for (Field field : tableType.getFields())
        {
            TableFieldEmitter fe = new TableFieldEmitter(field, this);
            fields.add(fe);
        }

        try
        {
            Template tpl = cfg.getTemplate("java/SqlTableBegin.ftl");
            tpl.process(this, writer);

            for (CompoundEmitter.CompoundParameterEmitter pe : parameters)
            {
                pe.emit(writer, cfg);
            }

            tpl = cfg.getTemplate("java/SqlTableWrite.ftl");
            tpl.process(this, writer);
        }
        catch (Exception e)
        {
            throw new DataScriptException(e);
        }
    }


    public String getName()
    {
        return tableType.getName();
    }


    public String getSqlConstraint()
    {
        TokenAST constraint = tableType.getSqlConstraint();
        if (constraint == null)
            return "";

        StringBuilder result = new StringBuilder();
        for (AST node = constraint.getFirstChild(); node != null; 
            node = node.getNextSibling())
        {
            String text = node.getText();
            result.append(text.substring(1, text.length() - 1));
        }
        return result.toString();
    }


    public List<CompoundEmitter.CompoundParameterEmitter> getParameters()
    {
        return parameters;
    }


    public List<TableFieldEmitter> getFields()
    {
        return fields;
    }
}

