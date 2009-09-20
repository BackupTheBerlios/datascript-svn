/* BSD License
 *
 * Copyright (c) 2006, Henrik Wedekind, Harman/Becker Automotive Systems
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
import datascript.ast.ConstType;
import datascript.ast.DataScriptException;
import freemarker.template.Template;
import freemarker.template.TemplateException;



/**
 * @author HWedekind
 * 
 */
public class ConstEmitter extends JavaDefaultEmitter
{
    private ConstType constType;


    public ConstEmitter(String outPathName, String defaultPackageName)
    {
        super(outPathName, defaultPackageName);
    }


    @Override
    public void beginRoot(AST rootNode)
    {
        findAllPackageNames(rootNode, allPackageNames);
        setPackageName(rootNode.getFirstChild());
        openOutputFile(dir, "__ConstType.java");
        try
        {
            Template tpl = cfg.getTemplate("java/ConstType.ftl");
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
    public void beginConst(AST c)
    {
        constType = (ConstType) c;
        try
        {
            Template tpl = cfg.getTemplate("java/ConstEnumeration.ftl");
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
    }


    public ConstType getConstType()
    {
        return constType;
    }


    public String getBaseTypeName()
    {
        return TypeNameEmitter.getTypeName(constType.getBaseType());
    }


    public String getName()
    {
        return constType.getName();
    }


    public String getValue()
    {
        return constType.getValue().toString();
    }


    /**** interface to freemarker FileHeader.inc template ****/


    public String getRootPackageName()
    {
        return datascript.ast.Package.getRoot().getPackageName();
    }
}
