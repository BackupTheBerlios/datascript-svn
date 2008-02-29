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

import datascript.ast.CompoundType;
import datascript.ast.Container;
import datascript.ast.DataScriptException;
import datascript.ast.Subtype;
import datascript.ast.TypeInterface;
import freemarker.template.Template;



public class SubtypeEmitter extends DefaultHTMLEmitter
{
    private Subtype subtype;


    public SubtypeEmitter()
    {
        super();
        directory = new File(directory, contentFolder);
    }


    public void emit(Subtype s)
    {
        this.subtype = s;
        containers.clear();
        for (Container compund : subtype.getContainers())
        {
            CompoundEmitter ce = new CompoundEmitter((CompoundType)compund);
            containers.add(ce);
        }

        try
        {
            Template tpl = cfg.getTemplate("html/subtype.html.ftl");

            setCurrentFolder(contentFolder);

            File outputDir = new File(directory, s.getPackage().getPackageName());
            openOutputFile(outputDir, s.getName() + HTML_EXT);

            tpl.process(this, writer);
            writer.close();
        }
        catch (Exception exc)
        {
            throw new DataScriptException(exc);
        }
    }


    @Override
    public String getPackageName()
    {
        return subtype.getPackage().getPackageName();
    }


    public LinkedType getBaseType()
    {
        TypeInterface baseType = subtype.getBaseType();
        LinkedType linkedType = new LinkedType(baseType);
        return linkedType;
    }


    public Comment getDocumentation()
    {
        Comment comment = new Comment();
        String doc = subtype.getDocumentation();
        if (doc.length() > 0) comment.parse(doc);
        return comment;
    }


    public Subtype getType()
    {
        return subtype;
    }
}
