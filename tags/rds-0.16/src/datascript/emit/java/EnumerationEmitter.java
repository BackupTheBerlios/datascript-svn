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


import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import datascript.antlr.util.ToolContext;
import datascript.ast.DataScriptException;
import datascript.ast.EnumItem;
import datascript.ast.EnumType;
import datascript.ast.IntegerValue;
import freemarker.template.Configuration;
import freemarker.template.Template;



/**
 * @author HWellmann
 * 
 */
public class EnumerationEmitter
{
    private JavaEmitter global;
    private EnumType enumType;
    private String javaType;
    private PrintWriter writer;
    private final List<EnumerationItemEmitter> items = 
        new ArrayList<EnumerationItemEmitter>();



    public static class EnumerationItemEmitter
    {
        private EnumItem item;
        private final IntegerValue maxVal;

        
        public EnumerationItemEmitter(EnumItem item)
        {
            this.item = item;
            maxVal = new IntegerValue(1).shiftLeft(item.getEnumType().getBaseType().bitsizeof(null));
        }


        public String getName()
        {
            return item.getName();
        }


        public int getValue()
        {
            return item.getValue().integerValue().intValue();
        }


        public boolean getExceedMaxValue()
        {
            if (maxVal.compareTo(item.getValue()) != 1)
                ToolContext.logError(item, "typeconflict with enum item " + 
                        item.getName() + ", value " + item.getValue() + 
                        " will not fit in enumtype");
            return false;
        }
    }



    public EnumerationEmitter(JavaEmitter j, EnumType e)
    {
        this.global = j;
        this.enumType = e;
    }


    public JavaEmitter getGlobal()
    {
        return global;
    }


    public EnumType getEnumerationType()
    {
        return enumType;
    }


    public void setWriter(PrintWriter writer)
    {
        this.writer = writer;
    }


    public String getBaseType()
    {
        if (javaType == null)
        {
            javaType = TypeNameEmitter.getTypeName(enumType.getBaseType());
        }
        return javaType;
    }


    public void emit(Configuration cfg, EnumType enumType2)
    {
        items.clear();
        for (EnumItem item : enumType.getItems())
        {
            EnumerationItemEmitter fe = new EnumerationItemEmitter(item);
            items.add(fe);
        }

        try
        {
            Template tpl = cfg.getTemplate("java/Enumeration.ftl");
            tpl.process(this, writer);
        }
        catch (Exception e)
        {
            throw new DataScriptException(e);
        }
    }




    /**** interface to freemarker FileHeader.inc template ****/

    public String getRdsVersion()
    {
        return global.getRdsVersion();
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
        return enumType.getName();
    }


    public List<EnumerationItemEmitter> getItems()
    {
        return items;
    }


    public int getEnumSize()
    {
        return enumType.sizeof(null).integerValue().intValue();
    }


    public int getEnumBitsize()
    {
        return enumType.bitsizeof(null).integerValue().intValue();
    }
}
