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
import java.io.PrintWriter;
import java.util.Set;
import java.util.HashSet;

import datascript.antlr.DataScriptParserTokenTypes;

import antlr.collections.AST;



/**
 * Implements the Emitter interface and does nothing. Saves some typing for
 * derived classes that only need to implement a few of the emitter actions.
 * 
 * @author HWellmann
 */
public class DefaultEmitter implements Emitter
{
    protected PrintWriter writer = null;
    protected AST curUnitNode = null;
    private String RDS_VERSION = null;


    /**** implementation of interface methods ****/

    public void beginRoot(AST r) {}
    public void endRoot() {}

    public void beginTranslationUnit(AST r, AST u) {}
    public void endTranslationUnit() {}

    public void beginPackage(AST p) {}
    public void endPackage(AST p) {}

    public void beginImport(AST i) {}
    public void endImport() {}

    public void beginConst(AST c) {}
    public void endConst(AST c) {}

    public void beginMembers() {}
    public void endMembers() {}

    public void beginSequence(AST s) {}
    public void endSequence(AST s) {}

    public void beginUnion(AST u) {}
    public void endUnion(AST u) {}

    public void beginChoice(AST c) {}
    public void endChoice(AST c) {}

    public void beginField(AST f) {}
    public void endField(AST f) {}

    public void beginFunction(AST f) {}
    public void endFunction(AST f) {}

    public void beginEnumeration(AST e) {}
    public void endEnumeration(AST e) {}

    public void beginEnumItem(AST e) {}
    public void endEnumItem(AST e) {}

    public void beginSubtype(AST s) {}
    public void endSubtype(AST s) {}

    public void beginSqlDatabase(AST s) {}
    public void endSqlDatabase(AST s) {}

    public void beginSqlMetadata(AST s) {}
    public void endSqlMetadata(AST s) {}

    public void beginSqlPragma(AST s) {}
    public void endSqlPragma(AST s) {}

    public void beginSqlTable(AST s) {}
    public void endSqlTable(AST s) {}

    public void beginSqlInteger(AST s) {}
    public void endSqlInteger(AST s) {}

    /**** end implementation of interface methods ****/


    public void setRdsVersion(String version)
    {
        RDS_VERSION = version;
    }


    public String getRdsVersion()
    {
        return RDS_VERSION;
    }


    public Set<String> getImportNameList()
    {
        HashSet<String> retval = new HashSet<String>();
        //AST in = getRootNode().getFirstChild();
        AST in = curUnitNode.getFirstChild();

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


    protected static Set<String> getName(AST in, Set<String> retval,
            int tokenType)
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
        if (!directory.exists())
        {
            directory.mkdirs();
            // TODO: handle false result
        }
        try
        {
            File outputFile = new File(directory, fileName);
            //System.out.println(String.format("writing %1$s", fileName));
            writer = new PrintWriter(outputFile);
        }
        catch (IOException exc)
        {
            exc.printStackTrace();
        }
    }
}
