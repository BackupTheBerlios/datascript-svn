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

import antlr.collections.AST;
import datascript.ast.DataScriptException;
import datascript.ast.EnumType;
import datascript.ast.SequenceType;
import datascript.ast.UnionType;
import datascript.ast.SqlIntegerType;
import freemarker.template.Template;

public class VisitorEmitter extends JavaDefaultEmitter
{
    public VisitorEmitter(String outPathName, String defaultPackageName)
    {
        super(outPathName, defaultPackageName);
    }

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
        catch (Exception e)
        {
            throw new DataScriptException(e);
        }
    }

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

    public void beginSequence(AST s)
    {
        SequenceType sequence = (SequenceType) s;
        String typeName = getTypeName(sequence);
        emitVisitor(typeName);
    }

    public void endSequence(AST s)
    {
    }

    public void beginUnion(AST u)
    {
        UnionType union = (UnionType) u;
        String typeName = getTypeName(union);
        emitVisitor(typeName);
    }

    public void endUnion(AST u)
    {
    }

    public void beginEnumeration(AST e)
    {
        EnumType enumType = (EnumType) e;
        String typeName = getTypeName(enumType);
        emitVisitor(typeName);
    }

    public void endEnumeration(AST e)
    {
    }

    public void beginSqlInteger(AST s)
    {
        SqlIntegerType integerType = (SqlIntegerType) s;
        String typeName = getTypeName(integerType);
        emitVisitor(typeName);
    }

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

    /**** interface to freemarker FileHeader.inc template ****/

    public String getRootPackageName()
    {
        return datascript.ast.Package.getRoot().getPackageName();
    }
}
