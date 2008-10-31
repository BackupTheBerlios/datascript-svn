/* BSD License
 *
 * Copyright (c) 2008, Harald Wellmann, Harman/Becker Automotive Systems
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


package net.sf.dstools.maven.rds;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

import datascript.tools.DataScriptTool;

/**
 * Goal for generating Java sources from a DataScript model
 * 
 */
public abstract class AbstractRdsMojo extends AbstractMojo
{
    // ----------------------------------------------------------------------
    // Mojo parameters
    // ----------------------------------------------------------------------

    /**
     * The enclosing project.
     * 
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    protected MavenProject project;

    // ----------------------------------------------------------------------
    // rds parameters
    // ----------------------------------------------------------------------

    /**
     * Path to source file of the top package, relative to sourceDirectory.
     * 
     * @parameter expression="${root}"
     * @required
     */
    protected String modelRoot;

    /**
     * Activates debugging features in the generated code.
     * 
     * @parameter expression="${debug}" default-value="false"
     */
    private boolean debug;

    /**
     * If true, HTML documentation will be generated for the model.
     * 
     * @parameter expression="${generateHtml}" default-value="true"
     */
    private boolean generateHtml;

    /**
     * Destination directory for generated HTML documentation files.
     * 
     * @parameter expression="${htmlDirectory}"
     *            default-value="${project.build.directory}/rds/html"
     */
    private File htmlDirectory;

    private File visitorFile;


    public void execute() throws MojoExecutionException
    {
        if (!modelRoot.endsWith(".ds"))
        {
            throw new MojoExecutionException("model root file name must have extension '.ds'");
        }

        File f = getOutputDirectory();
        if (!f.exists())
        {
            f.mkdirs();
        }
        else if (checkUpToDate())
        {
            getLog().info("Nothing to do, output is up-to-date");
            return;
        }

        List<String> arguments = new ArrayList<String>();
        if (generateHtml)
        {
            arguments.add("-doc");
            arguments.add(getHtmlOutDirectory().getPath());
        }
        arguments.add("-out");
        arguments.add(getOutputDirectory().getPath());
        arguments.add("-src");
        arguments.add(getSourceDirectory().getPath());
        if (debug)
        {
            arguments.add("-debug");
        }
        arguments.add(modelRoot);
        getLog().info("arguments = " + arguments.toString());
        DataScriptTool.main(arguments.toArray(new String[0]));

        registerSourceRoot();
    }


    private boolean checkUpToDate()
    {
        String genFileName = new File(getOutputDirectory(), modelRoot).getPath();
        String dir = genFileName.substring(0, genFileName.length()-3);
        visitorFile = new File(dir, "__DepthFirstVisitor.java");
        return checkUpToDate(getSourceDirectory());
    }


    private boolean checkUpToDate(File srcDir)
    {
        File[] files = srcDir.listFiles(new FileFilter()
        {
            @Override
            public boolean accept(File pathname)
            {
                return pathname.isDirectory() || pathname.getName().endsWith(".ds");
            }
        });

        for (int i = 0; i < files.length-1; i++)
        {
            File curFile = files[i];

            if (curFile.isDirectory())
            {
                if (!checkUpToDate(curFile))
                    return false;
            }
            else
            {
                if (!checkUpToDate(srcDir, curFile.getName()))
                    return false;
            }
        }
        return true;
    }


    private boolean checkUpToDate(File srcDir, String fileName)
    {
        File sourceFile = new File(srcDir, fileName);
        return visitorFile.lastModified() >= sourceFile.lastModified();
    }


    protected abstract void registerSourceRoot();

    protected abstract File getSourceDirectory();

    protected abstract File getOutputDirectory();

    protected File getHtmlOutDirectory()
    {
        return htmlDirectory;
    }
}
