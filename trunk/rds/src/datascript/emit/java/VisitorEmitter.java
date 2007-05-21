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

import antlr.collections.AST;
import datascript.ast.EnumType;
import datascript.ast.SequenceType;
import datascript.ast.UnionType;
import datascript.jet.java.SequenceEnd;
import datascript.jet.java.Visitor;


public class VisitorEmitter extends JavaDefaultEmitter
{
    private Visitor visitorTmpl = new Visitor();
    private SequenceEnd endTmpl = new SequenceEnd();



    public VisitorEmitter(String outPathName, String defaultPackageName)
    {
        super(outPathName, defaultPackageName);
    }


    public void beginTranslationUnit(AST rootNode, AST unitNode)
    {
        findAllPackageNames(rootNode, allPackageNames);
        setPackageName(unitNode);
        openOutputFile(dir, "__Visitor.java");
        String result = visitorTmpl.generate(this);
        out.print(result);
    }


    public void endTranslationUnit()
    {
        String result = endTmpl.generate(this);
        out.print(result);
        out.close();
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


    private void emitVisitor(String typeName)
    {
        StringBuilder buffer = new StringBuilder("    public void visit(");
        buffer.append(typeName);
        buffer.append(" node, Object arg);");
        out.println(buffer);
    }
}
