/* BSD License
 *
 * Copyright (c) 2007, Harald Wellmann, Harman/Becker Automotive Systems
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
import java.io.PrintWriter;
import java.io.Writer;
import java.net.URISyntaxException;
import java.net.URL;

import antlr.collections.AST;
import datascript.ast.DataScriptException;
import datascript.ast.Package;
import datascript.ast.TypeInterface;
import datascript.backend.html.HtmlExtension;
import datascript.emit.DefaultEmitter;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;



abstract public class DefaultHTMLEmitter extends DefaultEmitter
{
    protected static Configuration cfg = null;

    public static final String contentFolder = "content";
    protected static final String HTML_EXT = ".html";
    protected File directory = new File("html");
    protected TypeInterface currentType;
    private String currentFolder = "/";
    protected Package currentPackage;


    public DefaultHTMLEmitter() throws IOException, URISyntaxException
    {
        if (cfg != null) return;

        cfg = new Configuration();
        Class c = HtmlExtension.class;
        // URL url = c.getResource("");
        // if (url.getProtocol().startsWith("jar:"))
        // url = new URL(url, "/");
        // else
        // url = new URL(url, "../../../../../freemarker");
        URL url = c.getResource("/");
        if (url == null)
        {
            cfg.setClassForTemplateLoading(c, "/");
        }
        else
        {
            url = new URL(url, "../../../freemarker");
            System.out.println("Using directory '" + url.toString()
                    + "' for template files.");
            cfg.setDirectoryForTemplateLoading(new File(url.toURI()));
        }
        cfg.setObjectWrapper(new DefaultObjectWrapper());
    }


    public void setCurrentFolder(String currentFolder)
    {
        this.currentFolder = currentFolder;
    }


    public String getCurrentFolder()
    {
        return currentFolder;
    }


    public String getPackageName()
    {
        return currentPackage.getPackageName();
    }


    public String getRootPackageName()
    {
        return Package.getRoot().getPackageName();
    }


    public void beginPackage(AST p)
    {
        currentPackage = Package.lookup(p);
    }


    public void emitStylesheet()
    {
        emit("html/webStyles.css.ftl", "webStyles.css");
    }


    public void emitFrameset()
    {
        emit("html/index.html.ftl", "index.html");
    }


    public void emit(String template, String outputName)
    {
        try
        {
            Template tpl = cfg.getTemplate(template);
            openOutputFile(directory, outputName);

            Writer writer = new PrintWriter(out);
            tpl.process(this, writer);
            writer.close();
        }
        catch (Exception exc)
        {
            throw new DataScriptException(exc);
        }
    }
}
