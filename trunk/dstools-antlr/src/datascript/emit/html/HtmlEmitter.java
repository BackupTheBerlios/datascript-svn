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
import java.io.StringReader;
import java.util.Collection;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import antlr.ANTLRException;
import antlr.TokenBuffer;
import antlr.collections.AST;
import datascript.antlr.DocCommentLexer;
import datascript.antlr.DocCommentParser;
import datascript.antlr.DocCommentParserTokenTypes;
import datascript.ast.CompoundType;
import datascript.ast.EnumItem;
import datascript.ast.EnumType;
import datascript.ast.Field;
import datascript.ast.SequenceType;
import datascript.ast.SetType;
import datascript.ast.SqlDatabaseType;
import datascript.ast.TypeInterface;
import datascript.ast.UnionType;
import datascript.emit.DefaultEmitter;
import datascript.jet.html.CSS;
import datascript.jet.html.Compound;
import datascript.jet.html.Enum;
import datascript.jet.html.Overview;

public class HtmlEmitter extends DefaultEmitter
{
    private File directory = new File("html");
    private static String HTML_EXT = ".html";
    private String packageName;

    private Overview overviewTmpl = new Overview();
    private Compound compoundTmpl = new Compound();
    private Enum enumTmpl = new Enum();
    private CSS cssTmpl = new CSS();

    private EnumType enumeration;
    
    private TypeInterface currentType;
    
    private SortedMap<String, TypeInterface> typeMap;
    
    public void setPackageName(String packageName)
    {
        this.packageName = packageName;
        typeMap = new TreeMap<String, TypeInterface>();
    }

    public String getPackageName()
    {
        return packageName;
    }
    
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
        throw new RuntimeException("unknown category " 
                  + currentType.getClass().getName());
    }

    public EnumType getEnum()
    {
        return enumeration;
    }
    
    public CompoundType getCompound()
    {
        return (CompoundType)currentType;
    }
/*    
    public String getTypeName(TypeInterface type)
    {
        return typeEmitter.getTypeName(type);
    }
 
*/
    
    public Set<String> getTypeNames()
    {
        return typeMap.keySet();
    }

    public Collection<TypeInterface> getTypes()
    {
        return typeMap.values();
    }

    public TypeInterface getType(String typeName)
    {
        return typeMap.get(typeName);
    }
    
    public void endTranslationUnit()
    {
        openOutputFile(directory, "webStyles.css");
        out.print(cssTmpl.generate(this));
        out.close();

        openOutputFile(directory, "index" + HTML_EXT);
        String result = overviewTmpl.generate(this);
        out.print(result);
        
        for (TypeInterface type : typeMap.values())
        {
            currentType = type;
            if (type instanceof CompoundType)
            {
                emitCompound((CompoundType) type);
            }
            else if (type instanceof EnumType)
            {
                emitEnumeration((EnumType) type);
            }
        }
        out.close();
    }
    
    private void emitCompound(CompoundType seq)
    {
        PrintStream indexOut = out;
        try
        {
            openOutputFile(directory, getCompound().getName() + HTML_EXT);
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
        enumeration = e;
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

    public void beginField(AST f)
    {
        // TODO Auto-generated method stub

    }

    public void endField(AST f)
    {
        // TODO Auto-generated method stub

    }
    
    public void beginSequence(AST s)
    {
        SequenceType seq = (SequenceType)s;
        typeMap.put(seq.getName(), seq);
    }

    public void beginUnion(AST u)
    {
        UnionType un = (UnionType)u;
        typeMap.put(un.getName(), un);
    }

    public void beginEnumeration(AST e)
    {
        EnumType et = (EnumType)e;
        typeMap.put(et.getName(), et);
    }
    
    public void beginSqlDatabase(AST s)
    {
        SqlDatabaseType sqlDb = (SqlDatabaseType)s;
        typeMap.put(sqlDb.getName(), sqlDb);
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
    
    public String getDocumentation(Field field)
    {
        String doc = field.getDocumentation();
        return (doc == null)? "" : getDocumentation(doc);
    }
    
    private String getDocumentation(String doc)
    {
        if (doc.equals(""))
            return doc;

        StringBuilder buffer = new StringBuilder();
        StringReader is = new StringReader(doc);
        DocCommentLexer lexer = new DocCommentLexer(is);
        TokenBuffer tBuffer = new TokenBuffer(lexer);
        DocCommentParser parser = new DocCommentParser(tBuffer);
        try
        {
            parser.comment();
            AST docNode = parser.getAST();
            AST child = docNode.getFirstChild();
            for (; child != null; child = child.getNextSibling())
            {
                switch (child.getType())
                {
                    case DocCommentParserTokenTypes.TEXT:
                    {
                        buffer.append(child.getText());
                        AST text = child.getFirstChild();
                        for (; text != null; text = text.getNextSibling())
                        {
                            buffer.append(text.getText());
                        }
                        break;
                    }
                    
                    case DocCommentParserTokenTypes.AT:
                    {
                        String tag = child.getText().toUpperCase();
                        buffer.append("<br/><span class=\"docuTag\" id=\"" + tag + "\">@");
                        buffer.append(tag);
                        AST text = child.getFirstChild();
                        if (text == null)
                        {
                            buffer.append("</span> ");
                            break;
                        }
                        if (tag.equals("SEE"))
                        {
                            buffer.append(" <a href=\"");
                            buffer.append(text.getText()+".html");
                            buffer.append("\" >");
                            buffer.append(text.getText());
                            buffer.append("</a>");
                        }
                        else
                        {
                            buffer.append(" " + text.getText());
                        }
                        buffer.append("</span> ");
                        text = text.getNextSibling();
                        for (; text != null; text = text.getNextSibling())
                        {
                            buffer.append(text.getText());
                        }
                        break;
                    }
                }
            }
        }
        catch (ANTLRException exc)
        {
            exc.printStackTrace();
        }
        return buffer.toString();
    }
}
