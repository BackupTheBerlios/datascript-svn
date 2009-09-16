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
import java.util.HashSet;

import antlr.collections.AST;

import datascript.antlr.DataScriptParserTokenTypes;
import datascript.ast.TypeInterface;
import datascript.emit.DefaultEmitter;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;


public class JavaDefaultEmitter extends DefaultEmitter
{
    protected static Configuration cfg = null;
    protected static final String JAVA_EXT = ".java";
    
    protected String packageName;
    protected String packagePath;
    protected File dir = null;
    protected final HashSet<String> allPackageNames = new HashSet<String>();
    private boolean generateExceptionsOnEquals;



    public JavaDefaultEmitter(String outPathName, String defaultPackageName)
    {
        packagePath = outPathName;
        packageName = defaultPackageName;
        useFreeMarker();
    }


    private static void useFreeMarker()
    {
        if (cfg != null) 
            return;

        cfg = new Configuration();
        cfg.setClassForTemplateLoading(JavaDefaultEmitter.class, "/freemarker/");
        cfg.setObjectWrapper(new DefaultObjectWrapper());
    }


    public Configuration getTemplateConfig()
    {
        return cfg;
    }


    public void setPackageName(String packageName)
    {
        this.packageName = packageName;
        dir = new File(packagePath, packageName.replace('.', File.separatorChar));
    }


    protected void setPackageName(AST unitNode)
    {
        curUnitNode = unitNode;
        if (unitNode == null)
            return;
        AST sibling = unitNode;

        if (sibling.getType() == DataScriptParserTokenTypes.TRANSLATION_UNIT)
        {
            sibling = unitNode.getFirstChild();
            while (sibling != null && sibling.getType() != DataScriptParserTokenTypes.PACKAGE)
                sibling = sibling.getNextSibling();
            if (sibling == null)
                return;
        }

        if (sibling.getType() == DataScriptParserTokenTypes.PACKAGE)
        {
            sibling = sibling.getFirstChild();
            File file = null;
            do
            {                
                file = new File(file, sibling.getText());
                sibling = sibling.getNextSibling();
            } 
            while (sibling != null);
            packageName = file.getPath().replace(File.separatorChar, '.');
            dir = new File(packagePath, file.getPath());
        }
    }


    public String getPackageImports()
    {
        StringBuilder buffer = new StringBuilder();
        java.util.Set<String> importNames = getImportNameList();
        for (String importName : importNames)
        {
            if (!allPackageNames.contains(importName))
            {
                System.err.println("WARNING: could not find package " + importName);
                continue;
            }
            buffer.append("import ");
            buffer.append(importName);
            buffer.append(".*;");
            buffer.append(System.getProperties().getProperty("line.separator"));
        }
        return buffer.toString();
    }


    protected void findAllPackageNames(AST rootNode, java.util.Set<String> names)
    {
        AST silbling = rootNode.getFirstChild();
        while (silbling != null)
        {
            if (silbling.getType() != DataScriptParserTokenTypes.TRANSLATION_UNIT)
                continue;

            AST node = silbling.getFirstChild();
            getPackageNameList(node, names);
            silbling = silbling.getNextSibling();
        }
    }


    public String getPackageName()
    {
        return packageName;
    }


    public String getTypeName(TypeInterface type)
    {
        return TypeNameEmitter.getTypeName(type);
    }


    public void setThrowsException(boolean generateExceptions)
    {
        generateExceptionsOnEquals = generateExceptions;
    }


    public boolean getThrowsException()
    {
        return generateExceptionsOnEquals;
    }

}
