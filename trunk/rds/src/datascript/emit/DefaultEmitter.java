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
package datascript.emit;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Stack;
import java.util.Set;
import java.util.HashSet;

import datascript.antlr.DataScriptParserTokenTypes;

import antlr.collections.AST;

/**
 * Implements the Emitter interface and does nothing.
 * Saves some typing for derived classes that only need to 
 * implement a few of the emitter actions.
 * @author HWellmann
 */
abstract public class DefaultEmitter implements Emitter
{
    protected PrintStream out = null;
    protected Stack<AST> packageNode = new Stack<AST>();
    protected Stack<AST> rootNode = new Stack<AST>();


    /**** implementation of interface methods ****/

    abstract public void beginTranslationUnit();

    abstract public void endTranslationUnit();

    abstract public void beginPackage(AST p);

    abstract public void endPackage(AST p);

    abstract public void beginImport(AST rootNode);

    abstract public void endImport();

    public void beginMembers(AST p, AST r)
    {
        setPackageNode(p);
        setRootNode(r);
    }

    public void endMembers()
    {
        if (!packageNode.isEmpty())
            packageNode.pop();
        if (!rootNode.isEmpty())
            rootNode.pop();
    }

    abstract public void beginSequence(AST s);

    abstract public void endSequence(AST s);

    abstract public void beginUnion(AST u);

    abstract public void endUnion(AST u);

    abstract public void beginField(AST f);

    abstract public void endField(AST f);

    abstract public void beginEnumeration(AST e);

    abstract public void endEnumeration(AST e);

    abstract public void beginEnumItem(AST e);

    abstract public void endEnumItem(AST e);

    abstract public void beginSubtype(AST s);

    abstract public void endSubtype(AST s);

    abstract public void beginSqlDatabase(AST s);
    
    abstract public void endSqlDatabase(AST s);
    
    abstract public void beginSqlMetadata(AST s);
    
    abstract public void endSqlMetadata(AST s);
    
    abstract public void beginSqlPragma(AST s);
    
    abstract public void endSqlPragma(AST s);
    
    abstract public void beginSqlTable(AST s);
    
    abstract public void endSqlTable(AST s);
    
    abstract public void beginSqlInteger(AST s);
    
    abstract public void endSqlInteger(AST s);

    /**** end implementation of interface methods ****/

    public void setPackageNode(AST p)
    {
        packageNode.push(p);
    }

    public AST getPackageNode()
    {
        if (packageNode.isEmpty())
            return null;
        return packageNode.peek();
    }

    public void setRootNode(AST i)
    {
        rootNode.push(i);
    }

    public AST getRootNode()
    {
        if (rootNode.isEmpty())
            return null;
        return rootNode.peek();
    }

    public Set<String> getImportNameList()
    {
        HashSet<String> retval = new HashSet<String>();
        AST in = getRootNode().getFirstChild();

        while (in != null)
        {
            getName(in, retval, DataScriptParserTokenTypes.IMPORT);
            in = in.getNextSibling();
        }
        return retval;
    }

    protected static Set<String> getPackageNameList(AST in, Set<String> retval)
    {
        return getName(in, retval, DataScriptParserTokenTypes.PACKAGE);
    }

    protected static Set<String> getName(AST in, Set<String> retval, int tokenType)
    {
        if (in == null || in.getType() != tokenType)
            return retval;

        retval.add(getIDName(in.getFirstChild()));
        return getName(in.getNextSibling(), retval, tokenType);
    }

    protected static String getIDName(AST in)
    {
        if (in == null || in.getType() != DataScriptParserTokenTypes.ID)
            return null;

        String name = getIDName(in.getNextSibling());
        if (name == null)
            return in.getText();

        return in.getText() + '.' + name;
    }


    protected void openOutputFile(File directory, String fileName)
    {
        if (! directory.exists())
        {
            directory.mkdirs();
            // TODO: handle false result
        }
        try
        {
            File outputFile = new File(directory, fileName);
            
// TODO HWellmann: What for? The warning is annoying and the delete/create
// is redundant
            
//            if (outputFile.exists())
//            {
//                System.err.println("WARNING: overwriting file " + outputFile.getAbsoluteFile());
//                outputFile.delete();
//            }
//            outputFile.createNewFile();
            out = new PrintStream(outputFile);
        }
        catch (IOException exc)
        {
            exc.printStackTrace();
        }
    }
}
