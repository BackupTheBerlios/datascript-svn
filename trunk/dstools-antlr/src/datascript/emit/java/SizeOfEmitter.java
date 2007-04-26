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
import datascript.jet.java.SequenceEnd;
import datascript.jet.java.SizeOf;
import datascript.jet.java.SizeOfEnumeration;

public class SizeOfEmitter extends JavaDefaultEmitter
{
    private SizeOf sizeOfTmpl = new SizeOf();
    private SizeOfEnumeration enumerationTmpl = new SizeOfEnumeration();
    private SequenceEnd endTmpl = new SequenceEnd();
    private EnumType enumeration;
    private boolean TmplGenerated = false;



    public SizeOfEmitter(String outPathName, String defaultPackageName, AST rootNode)
    {
        super(outPathName, defaultPackageName, rootNode);
    }


    public void beginTranslationUnit()
    {
        openOutputFile(dir, "__SizeOf.java");
    }


    public void endTranslationUnit()
    {
        if (TmplGenerated)
        {
            String result = endTmpl.generate(this);
            out.print(result);
        }
        out.close();
    }


    public void beginImport(AST r)
    {
        rootNode.push(r);
        if (!TmplGenerated)
        {
            String result = sizeOfTmpl.generate(this);
            out.print(result);
            TmplGenerated = true;
        }
        rootNode.pop();
    }


    public void endImport()
    {
    }


    public void beginMembers(AST p, AST i)
    {
        super.beginMembers(p, i);

        setPackageName(p);
        if (!TmplGenerated)
        {
            String result = sizeOfTmpl.generate(this);
            out.print(result);
            TmplGenerated = true;
        }
    }


    public void endMembers()
    {
        super.endMembers();
    }


    public void beginSequence(AST s)
    {
    }


    public void endSequence(AST s)
    {
    }


    public void beginUnion(AST u)
    {
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
        if (!TmplGenerated)
        {
            String result = sizeOfTmpl.generate(this);
            out.print(result);
            TmplGenerated = true;
        }
        enumeration = (EnumType)e;
        String result = enumerationTmpl.generate(this);
        out.print(result);
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
    }


    public void endSubtype(AST s)
    {
    }


    public void beginSqlDatabase(AST s)
    {
    }


    public void endSqlDatabase(AST s)
    {
    }


    public void beginSqlMetadata(AST s)
    {
    }


    public void endSqlMetadata(AST s)
    {
    }


    public void beginSqlPragma(AST s)
    {
    }


    public void endSqlPragma(AST s)
    {
    }


    public void beginSqlTable(AST s)
    {
    }


    public void endSqlTable(AST s)
    {
    }


    public void beginSqlInteger(AST s)
    {
    }


    public void endSqlInteger(AST s)
    {
    }


    public EnumType getEnumerationType()
    {
        return enumeration;
    }
}
