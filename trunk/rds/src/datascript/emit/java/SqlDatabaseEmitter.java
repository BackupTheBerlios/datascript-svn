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

import datascript.ast.DataScriptException;
import datascript.ast.Expression;
import datascript.ast.Field;
import datascript.ast.SqlDatabaseType;
import freemarker.template.Configuration;
import freemarker.template.Template;



/**
 * @author HWellmann
 * 
 */
public class SqlDatabaseEmitter
{
    private final List<DatabaseFieldFMEmitter> fields = 
        new ArrayList<DatabaseFieldFMEmitter>();
    private final List<PragmaFieldFMEmitter> pragmata = 
        new ArrayList<PragmaFieldFMEmitter>();
    private final List<MetadataFieldFMEmitter> metadatas = 
        new ArrayList<MetadataFieldFMEmitter>();

    private JavaEmitter global;
    private SqlDatabaseType dbType;
    private PrintWriter writer;


    public class DatabaseFieldFMEmitter
    {
        private final Field field;


        public DatabaseFieldFMEmitter(Field field)
        {
            this.field = field;
        }


        public String getName()
        {
            return field.getName();
        }


        public String getJavaTypeName()
        {
            return TypeNameEmitter.getTypeName(field.getFieldType());
        }
    }



    public static class PragmaFieldFMEmitter
    {
        private static final ExpressionEmitter exprEm = new ExpressionEmitter();

        private final Field field;


        public PragmaFieldFMEmitter(Field field)
        {
            this.field = field;
        }


        public String getName()
        {
            return field.getName();
        }


        public String getValue()
        {
            Expression init = field.getInitializer();
            if (init != null)
            {
                return exprEm.emit(init);
            }
            return null;
        }
    }



    public static class MetadataFieldFMEmitter
    {
        private static final ExpressionEmitter exprEm = new ExpressionEmitter();

        private final Field field;


        public MetadataFieldFMEmitter(Field field)
        {
            this.field = field;
        }


        public String getName()
        {
            return field.getName();
        }


        public String getValue()
        {
            Expression init = field.getInitializer();
            if (init != null)
            {
                return exprEm.emit(init);
            }
            return null;
        }
    }



    public SqlDatabaseEmitter(JavaEmitter j, SqlDatabaseType db)
    {
        this.global = j;
        this.dbType = db;
    }


    public JavaEmitter getGlobal()
    {
        return global;
    }


    public SqlDatabaseType getSqlDatabaseType()
    {
        return dbType;
    }


    public void setWriter(PrintWriter writer)
    {
        this.writer = writer;
    }


    public void emitFreemarker(Configuration cfg, SqlDatabaseType db)
    {
        fields.clear();
        for (Field field : dbType.getFields())
        {
            DatabaseFieldFMEmitter fe = new DatabaseFieldFMEmitter(field);
            fields.add(fe);
        }
        pragmata.clear();
        if (dbType.getPragma() != null)
        {
            for (Field field : dbType.getPragma().getFields())
            {
                PragmaFieldFMEmitter fe = new PragmaFieldFMEmitter(field);
                pragmata.add(fe);
            }
        }
        metadatas.clear();
        if (dbType.getMetadata() != null)
        {
            for (Field field : dbType.getMetadata().getFields())
            {
                MetadataFieldFMEmitter fe = new MetadataFieldFMEmitter(field);
                metadatas.add(fe);
            }
        }

        try
        {
            Template tpl = cfg.getTemplate("java/SqlDatabase.ftl");
            tpl.process(this, writer);
        }
        catch (Exception e)
        {
            throw new DataScriptException(e);
        }
    }


    public void emit(SqlDatabaseType SqlDatabaseType)
    {
    }


    /**** interface to freemarker FileHeader.inc template ****/

    public String getRdsVersion()
    {
        return global.getRdsVersion();
    }


    public String getPackageName()
    {
        return global.getPackageName();
    }


    public String getRootPackageName()
    {
        return datascript.ast.Package.getRoot().getPackageName();
    }


    public String getPackageImports()
    {
        return getGlobal().getPackageImports();
    }


    public String getName()
    {
        return dbType.getName();
    }


    public List<DatabaseFieldFMEmitter> getFields()
    {
        return fields;
    }


    public List<PragmaFieldFMEmitter> getPragmaFields()
    {
        return pragmata;
    }


    public List<MetadataFieldFMEmitter> getMetadataFields()
    {
        return metadatas;
    }
}
