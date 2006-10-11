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

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import antlr.collections.AST;
import datascript.ast.EnumType;
import datascript.ast.SequenceType;
import datascript.ast.TypeInterface;
import datascript.ast.UnionType;
import datascript.emit.Emitter;

public class JavaEmitter implements Emitter
{
    private static String JAVA_EXT = ".java";
    private String packageName;
    private SequenceEmitter sequenceEmitter = new SequenceEmitter(this);
    private UnionEmitter unionEmitter = new UnionEmitter(this);
    private TypeNameEmitter typeEmitter = new TypeNameEmitter();
    private PrintStream out;
    
    public void setPackageName(String packageName)
    {
        this.packageName = packageName;
    }

    public String getPackageName()
    {
        return packageName;
    }

    public String getTypeName(TypeInterface type)
    {
        return typeEmitter.getTypeName(type);
    }
    
    public void beginTranslationUnit()
    {
        // TODO Auto-generated method stub

    }

    public void endTranslationUnit()
    {
        // TODO Auto-generated method stub

    }

    public void beginField(AST f)
    {
        // TODO Auto-generated method stub

    }

    public void endField(AST f)
    {
        // TODO Auto-generated method stub

    }

    private void openOutputFile(String typeName)
    {
        File directory = new File(packageName);
        if (! directory.exists())
        {
            directory.mkdir();
        }
        File outputFile = new File(directory, typeName + JAVA_EXT);
        outputFile.delete();
        try
        {
            outputFile.createNewFile();
            out = new PrintStream(outputFile);
        }
        catch (IOException exc)
        {
            exc.printStackTrace();
        }
    }

    public void beginSequence(AST s)
    {
        SequenceType sequence = (SequenceType) s;
        String typeName = getTypeName(sequence);
        openOutputFile(typeName);
        sequenceEmitter.setOutputStream(out);
        sequenceEmitter.begin(sequence);
    }

    public void endSequence(AST s)
    {
        SequenceType sequence = (SequenceType)s;
        sequenceEmitter.end(sequence);
        out.close();
    }

    public void beginUnion(AST u)
    {
        UnionType union = (UnionType) u;
        String typeName = getTypeName(union);
        openOutputFile(typeName);
        unionEmitter.setOutputStream(out);
        unionEmitter.begin(union);
    }

    public void endUnion(AST u)
    {
        UnionType union = (UnionType)u;
        unionEmitter.end(union);
        out.close();
    }

    public void beginEnumeration(AST e)
    {
        EnumType enumType = (EnumType) e;
        EnumerationEmitter enumEmitter = new EnumerationEmitter(this, enumType);
        String typeName = getTypeName(enumType);
        openOutputFile(typeName);
        enumEmitter.setOutputStream(out);
        enumEmitter.emit(enumType);
    }

    public void endEnumeration(AST e)
    {
        out.close();
    }

    public void beginEnumItem(AST e)
    {
        // TODO Auto-generated method stub

    }

    public void endEnumItem(AST e)
    {
        // TODO Auto-generated method stub

    }

}
