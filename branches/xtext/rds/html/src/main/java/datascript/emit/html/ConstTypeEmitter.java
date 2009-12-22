/* BSD License
 *
 * Copyright (c) 2007, Henrik Wedekind, Harman/Becker Automotive Systems
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


package datascript.emit.html;

import java.io.File;
import java.io.IOException;

import datascript.ast.CompoundType;
import datascript.ast.ConstType;
import datascript.ast.Container;
import datascript.ast.DataScriptException;
import datascript.ast.TypeInterface;
import freemarker.template.Template;
import freemarker.template.TemplateException;


/**
 * @author HWedekind
 * 
 */
public class ConstTypeEmitter extends DefaultHTMLEmitter
{
    ConstType consttype;

    public ConstTypeEmitter(String outputPath)
    {
        super(outputPath);
        directory = new File(directory, CONTENT_FOLDER);
    }


    public void emit(ConstType constType)
    {
        this.consttype = constType;
        containers.clear();
        for (Container compund : consttype.getContainers())
        {
            CompoundEmitter ce = new CompoundEmitter((CompoundType)compund);
            containers.add(ce);
        }

        try
        {
            Template tpl = cfg.getTemplate("html/const.html.ftl");

            setCurrentFolder(CONTENT_FOLDER);

            File outputDir = new File(directory, consttype.getPackage().getPackageName());
            openOutputFile(outputDir, consttype.getName() + HTML_EXT);

            tpl.process(this, writer);
            writer.close();
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
    public String getPackageName()
    {
        return consttype.getPackage().getPackageName();
    }


    public ConstType getType()
    {
        return consttype;
    }


    public Comment getDocumentation()
    {
        Comment comment = new Comment();
        String doc = consttype.getDocumentation();
        if (doc.length() > 0)
            comment.parse(doc);
        return comment;
    }


    public LinkedType getBaseType()
    {
        TypeInterface baseType = consttype.getBaseType();
        LinkedType linkedType = new LinkedType(baseType);
        return linkedType;
    }
}
