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
package datascript.emit.html;

import java.io.File;
import java.io.PrintStream;
import java.util.Collection;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import antlr.collections.AST;

import datascript.ast.CompoundType;
import datascript.ast.EnumItem;
import datascript.ast.EnumType;
import datascript.ast.Field;
import datascript.ast.SequenceType;
import datascript.ast.SetType;
import datascript.ast.SqlIntegerType;
import datascript.ast.SqlMetadataType;
import datascript.ast.SqlPragmaType;
import datascript.ast.SqlTableType;
import datascript.ast.Subtype;
import datascript.ast.SqlDatabaseType;
import datascript.ast.TypeInterface;
import datascript.ast.UnionType;

import datascript.jet.html.Compound;
import datascript.jet.html.Enum;
import datascript.jet.html.Comment;


public class ContentEmitter extends DefaultHTMLEmitter
{
    private class Pair<A, B>
    {
        private A first;
        private B second;

        Pair(A a, B b)
        {
            first = a;
            second = b;
        }
        
        public A getFirst()
        {
            return first;
        }
        
        public B getSecond()
        {
            return second;
        }
    }


    private Compound compoundTmpl = new Compound();
    private Enum enumTmpl = new Enum();
    private datascript.jet.html.Subtype subtypeTmpl = new datascript.jet.html.Subtype();

    private SortedMap<String, Pair<String, TypeInterface> > typeMap = 
        new TreeMap<String, Pair<String, TypeInterface> >();


    /**** implementation of abstract methods ****/

    public void beginTranslationUnit()
    {
    }
    
    public void endTranslationUnit()
    {
        /* now producing the real content */
        directory = new File(directory, contentFolder);
        setCurrentFolder(contentFolder);

        for (Pair<String, TypeInterface> p : typeMap.values())
        {
            TypeInterface type = p.getSecond();
            // TODO: restore the right package tree
            setPackageName(p.getFirst());
            if (type instanceof CompoundType)
            {
                setPackageNode((AST)type);
                emitCompound((CompoundType) type);
            }
            else if (type instanceof EnumType)
            {
                setPackageNode((AST)type);
                emitEnumeration((EnumType) type);
            }
            else if (type instanceof Subtype)
            {
                setPackageNode((AST)type);
                emitSubtype((Subtype) type);
            }
        }
    }


    public void beginPackage(AST p)
    {
    }


    public void endPackage(AST p)
    {
    }


    public void beginImport(AST importNode)
    {
    }


    public void endImport()
    {
    }


    public void beginSequence(AST s)
    {
        setPackageName(getPackageNode());
        SequenceType seq = (SequenceType)s;
        Pair<String, TypeInterface> p = new Pair<String, TypeInterface>(getPackageName(), seq);
        typeMap.put(seq.getName(), p);
    }
    
    public void endSequence(AST s)
    {
    }

    public void beginUnion(AST u)
    {
        setPackageName(getPackageNode());
        UnionType un = (UnionType)u;
        Pair<String, TypeInterface> p = new Pair<String, TypeInterface>(getPackageName(), un);
        typeMap.put(un.getName(), p);
    }

    public void endUnion(AST u)
    {
    }

    public void beginField(AST f)
    {
    }

    public void endField(AST f)
    {
    }

    public void beginEnumeration(AST e)
    {
        setPackageName(getPackageNode());
        EnumType et = (EnumType)e;
        Pair<String, TypeInterface> p = new Pair<String, TypeInterface>(getPackageName(), et);
        typeMap.put(et.getName(), p);
    }


    public void endEnumeration(AST e)
    {
    }


    public void beginEnumItem(AST e)
    {
    }


    public void endEnumItem(AST e)
    {
    }


    public void beginSubtype(AST s)
    {
        setPackageName(getPackageNode());
        Subtype st = (Subtype)s;
        Pair<String, TypeInterface> p = new Pair<String, TypeInterface>(getPackageName(), st);
        typeMap.put(st.getName(), p);
    }


    public void endSubtype(AST s)
    {
    }

    public void beginSqlDatabase(AST s)
    {
        setPackageName(getPackageNode());
        SqlDatabaseType sqlDb = (SqlDatabaseType)s;
        Pair<String, TypeInterface> p = new Pair<String, TypeInterface>(getPackageName(), sqlDb);
        typeMap.put(sqlDb.getName(), p);
    }


    public void endSqlDatabase(AST s)
    {
    }

    public void beginSqlMetadata(AST s)
    {
        setPackageName(getPackageNode());
        SqlMetadataType sqlMeta = (SqlMetadataType)s;
        Pair<String, TypeInterface> p = new Pair<String, TypeInterface>(getPackageName(), sqlMeta);
        typeMap.put(sqlMeta.getName(), p);
    }


    public void endSqlMetadata(AST s)
    {
    }

    public void beginSqlPragma(AST s)
    {
        setPackageName(getPackageNode());
        SqlPragmaType sqlPragma = (SqlPragmaType)s;
        Pair<String, TypeInterface> p = new Pair<String, TypeInterface>(getPackageName(), sqlPragma);
        typeMap.put(sqlPragma.getName(), p);
    }


    public void endSqlPragma(AST s)
    {
    }


    public void beginSqlTable(AST s)
    {
        setPackageName(getPackageNode());
        SqlTableType sqlTab = (SqlTableType)s;
        Pair<String, TypeInterface> p = new Pair<String, TypeInterface>(getPackageName(), sqlTab);
        typeMap.put(sqlTab.getName(), p);
    }


    public void endSqlTable(AST s)
    {
    }


    public void beginSqlInteger(AST s)
    {
        setPackageName(getPackageNode());
        SqlIntegerType sqlInt = (SqlIntegerType)s;
        Pair<String, TypeInterface> p = new Pair<String, TypeInterface>(getPackageName(), sqlInt);
        typeMap.put(sqlInt.getName(), p);
    }


    public void endSqlInteger(AST s)
    {
    }

/**** end of implementation of abstract methods ****/
    
    public String getCategoryPlainText()
    {
        if (currentType instanceof SequenceType)
        {
            return "Sequence";
        }
        else if (currentType instanceof UnionType)
        {
            return "Union";
        }
        else if (currentType instanceof SqlDatabaseType)
        {
            return "SQL Database";
        }
        else if (currentType instanceof SqlMetadataType)
        {
            return "SQL Matadata";
        }
        else if (currentType instanceof SqlTableType)
        {
            return "SQL Table";
        }
        else if (currentType instanceof SqlPragmaType)
        {
            return "SQL Pragma";
        }
        else if (currentType instanceof SqlIntegerType)
        {
            return "SQL Integer";
        }
        throw new RuntimeException("unknown category " 
                  + currentType.getClass().getName());
    }

    public String getCategoryKeyword()
    {
        if (currentType instanceof SequenceType)
        {
            return "";
        }
        else if (currentType instanceof UnionType)
        {
            return "union ";
        }
        else if (currentType instanceof SqlDatabaseType)
        {
            return "sql_database ";
        }
        else if (currentType instanceof SqlMetadataType)
        {
            return "sql_metadata ";
        }
        else if (currentType instanceof SqlTableType)
        {
            return "sql_table ";
        }
        else if (currentType instanceof SqlPragmaType)
        {
            return "sql_pragma ";
        }
        else if (currentType instanceof SqlIntegerType)
        {
            return "sql_integer ";
        }
        throw new RuntimeException("unknown category " 
                  + currentType.getClass().getName());
    }

    public EnumType getEnum()
    {
        return (EnumType)currentType;
    }

    public Subtype getSubtype()
    {
        return (Subtype)currentType;
    }
    
    public CompoundType getCompound()
    {
        return (CompoundType)currentType;
    }
    
    public Set<String> getTypeNames()
    {
        return typeMap.keySet();
    }

    public Collection< Pair<String, TypeInterface> > getTypes()
    {
        return typeMap.values();
    }

    public TypeInterface getType(String typeName)
    {
        Pair<String, TypeInterface> p = typeMap.get(typeName);
        return p.getSecond();
    }
    
    private void emitCompound(CompoundType seq)
    {
        currentType = seq;
        PrintStream indexOut = out;
        try
        {
            openOutputFile(directory, seq.getName() + HTML_EXT);
            String result = compoundTmpl.generate(this);
            out.print(result);
            out.close();
        }
        finally
        {
            out = indexOut;
        }
    }

    private void emitEnumeration(EnumType e)
    {
        currentType = e;
        PrintStream indexOut = out;
        try
        {
            openOutputFile(directory, e.getName() + HTML_EXT);
            String result = enumTmpl.generate(this);
            out.print(result);
            out.close();
        }
        finally
        {
            out = indexOut;
        }
    }

    private void emitSubtype(Subtype s)
    {
        currentType = s;
        PrintStream indexOut = out;
        try
        {
            openOutputFile(directory, s.getName() + HTML_EXT);
            String result = subtypeTmpl.generate(this);
            out.print(result);
            out.close();
        }
        finally
        {
            out = indexOut;
        }
    }


    public String getDocumentation(CompoundType compound)
    {
        return getDocumentation(compound.getDocumentation());
    }
    
    public String getDocumentation(SetType settype)
    {
        return getDocumentation(settype.getDocumentation());
    }
    
    public String getDocumentation(EnumItem item)
    {
        return getDocumentation(item.getDocumentation());
    }
    
    public String getDocumentation(Subtype subtype)
    {
        return getDocumentation(subtype.getDocumentation());
    }
    
    public String getDocumentation(Field field)
    {
        String doc = field.getDocumentation();
        return (doc == null)? "" : getDocumentation(doc);
    }
    
    private String getDocumentation(String doc)
    {
        if (doc.equals(""))
            return doc;

        Comment commentGenerator = new Comment();
        return commentGenerator.generate(doc);
    }
}
