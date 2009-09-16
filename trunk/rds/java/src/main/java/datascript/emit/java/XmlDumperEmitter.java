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

import antlr.collections.AST;
import datascript.ast.ArrayType;
import datascript.ast.DataScriptException;
import datascript.ast.Field;
import datascript.ast.TypeInterface;
import freemarker.template.Template;
import freemarker.template.TemplateException;



public class XmlDumperEmitter extends DepthFirstVisitorEmitter
{
    public XmlDumperEmitter(String outPathName, String defaultPackageName)
    {
        super(outPathName, defaultPackageName);
    }


    @Override
    public void beginRoot(AST rootNode)
    {
        findAllPackageNames(rootNode, allPackageNames);
        setPackageName(rootNode.getFirstChild());
        openOutputFile(dir, "__XmlDumper.java");
        try
        {
            Template tpl = cfg.getTemplate("java/XmlDumper.ftl");
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
    public String getElementVisitor(Field field)
    {
        String result = null;
        TypeInterface type = field.getFieldType();
        if (type instanceof ArrayType)
        {
            ArrayType array = (ArrayType) type;
            TypeInterface elemType = array.getElementType();
            result = getVisitor(elemType, "__elem", field.getName());
        }

        return result;
    }


    @Override
    public String getStartType()
    {
        return "startElement(arg);";
    }


    @Override
    public String getEndType()
    {
        return "endElement(arg);";
    }

}
