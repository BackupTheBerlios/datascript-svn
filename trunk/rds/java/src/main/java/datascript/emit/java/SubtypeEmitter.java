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


import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;

import datascript.ast.DataScriptException;
import datascript.ast.Subtype;
import datascript.ast.TypeInterface;
import datascript.ast.TypeReference;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;



/**
 * @author HWedekind
 * 
 */
public class SubtypeEmitter
{
    private JavaEmitter global;
    private Subtype subtype;


    private PrintWriter writer;
    // private SubtypeTmpl subtypeTmpl;

    public SubtypeEmitter(JavaEmitter j, Subtype s)
    {
        global = j;
        subtype = s;
        // subtypeTmpl = new SubtypeTmpl();
    }


    public JavaEmitter getGlobal()
    {
        return global;
    }


    public Subtype getSubtype()
    {
        return subtype;
    }


    public void setWriter(PrintWriter writer)
    {
        this.writer = writer;
    }


    public void emit(Configuration cfg, Subtype subtype2)
    {

        try
        {
            Template tpl = cfg.getTemplate("java/SubtypeTmpl.ftl");
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


    /**** interface to freemarker FileHeader.inc template ****/

    public String getRdsVersion()
    {
        return global.getRdsVersion();
    }


    /**
     * Calculates the actual time and returns a formattet string that follow the 
     * ISO 8601 standard (i.e. "2007-13-11T12:08:56.235-0700")
     * @return      actual time as a ISO 8601 formattet string
     */
    public String getTimeStamp()
    {
        return String.format("%1$tFT%1$tT.%1$tL%1$tz", Calendar.getInstance());
    }


    public String getPackageName()
    {
        return global.getPackageName();
    }


    public String getRootPackageName()
    {
        return datascript.ast.Package.getRoot().getPackageName();
    }


    public String getPackageImports()
    {
        return getGlobal().getPackageImports();
    }


    public String getName()
    {
        return subtype.getName();
    }


    public String getSuperClassName()
    {
        TypeInterface baseType = subtype.getBaseType();
        if (baseType instanceof TypeReference)
            baseType = TypeReference.resolveType(baseType);
        if (TypeNameEmitter.isBuiltinType(baseType))
            return "Object";
        return TypeNameEmitter.getTypeName(baseType);
    }


    public String getClassName()
    {
        return subtype.getName();
    }
}
