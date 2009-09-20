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
import java.util.Set;

import antlr.collections.AST;
import datascript.ast.ChoiceType;
import datascript.ast.DataScriptException;
import datascript.ast.EnumType;
import datascript.ast.SequenceType;
import datascript.ast.SqlIntegerType;
import datascript.ast.UnionType;
import freemarker.template.Template;
import freemarker.template.TemplateException;



public class VisitorEmitter extends JavaDefaultEmitter
{
    public VisitorEmitter(String outPathName, String defaultPackageName)
    {
        super(outPathName, defaultPackageName);
    }


    @Override
    public void beginRoot(AST rootNode)
    {
        findAllPackageNames(rootNode, allPackageNames);
        setPackageName(rootNode.getFirstChild());
        openOutputFile(dir, "__Visitor.java");
        try
        {
            Template tpl = cfg.getTemplate("java/Visitor.ftl");
            tpl.process(this, writer);
        }
        catch (IOException e)
        {
            throw new DataScriptException(e);
        }
        catch (TemplateException e)
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
        SequenceType sequence = (SequenceType) s;
        String typeName = getTypeName(sequence);
        typeName = sequence.getPackage().getPackageName() + "." + typeName;
        emitVisitor(typeName);
    }


    @Override
    public void endSequence(AST s)
    {
    }


    @Override
    public void beginUnion(AST u)
    {
        UnionType union = (UnionType) u;
        String typeName = getTypeName(union);
        typeName = union.getPackage().getPackageName() + "." + typeName;
        emitVisitor(typeName);
    }


    @Override
    public void endUnion(AST u)
    {
    }


    @Override
    public void beginChoice(AST c)
    {
        ChoiceType choice = (ChoiceType) c;
        String typeName = getTypeName(choice);
        typeName = choice.getPackage().getPackageName() + "." + typeName;
        emitVisitor(typeName);
    }


    @Override
    public void endChoice(AST c)
    {
    }


    @Override
    public void beginEnumeration(AST e)
    {
        EnumType enumType = (EnumType) e;
        String typeName = getTypeName(enumType);
        typeName = enumType.getPackage().getPackageName() + "." + typeName;
        emitVisitor(typeName);
    }


    @Override
    public void endEnumeration(AST e)
    {
    }


    @Override
    public void beginSqlInteger(AST s)
    {
        SqlIntegerType integerType = (SqlIntegerType) s;
        String typeName = getTypeName(integerType);
        typeName = integerType.getPackage().getPackageName() + "." + typeName;
        emitVisitor(typeName);
    }


    @Override
    public void endSqlInteger(AST s)
    {
    }


    private void emitVisitor(String typeName)
    {
        StringBuilder buffer = new StringBuilder("    public void visit(");
        buffer.append(typeName);
        buffer.append(" node, Object arg);");
        writer.println(buffer);
    }


    @Override
    public String getPackageImports()
    {
        StringBuilder buffer = new StringBuilder();
        // Set<String> importNames = getImportNameList();
        Set<String> importNames = datascript.ast.Package.getRoot()
                .getAllImportNames();
        for (String importName : importNames)
        {
            if (!allPackageNames.contains(importName))
            {
                System.err.println("WARNING: could not find package "
                        + importName);
                continue;
            }
            buffer.append("import ");
            buffer.append(importName);
            buffer.append(".*;");
            buffer.append(System.getProperties().getProperty("line.separator"));
        }
        return buffer.toString();
    }


    /** ** interface to freemarker FileHeader.inc template *** */

    public String getRootPackageName()
    {
        return datascript.ast.Package.getRoot().getPackageName();
    }
}
