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


import org.apache.commons.cli.Option;

import antlr.RecognitionException;

import datascript.ast.ComputeError;
import datascript.antlr.DataScriptEmitter;
import datascript.antlr.util.TokenAST;
import datascript.ast.DataScriptException;
import datascript.tools.Extension;
import datascript.tools.Parameters;



public class JavaExtension implements Extension
{
    private Parameters parameters;
    private String defaultPackageName;
    private boolean generateExceptions;
    private boolean ignorePragma;


    public void generate(DataScriptEmitter emitter, TokenAST rootNode)
    {
        if (parameters == null)
            throw new DataScriptException("No parameters set for JavaBackend!");

        System.out.println("emitting java code");
        try
        {
            generateExceptions = parameters.argumentExists("-debug");
            if (parameters.argumentExists("-pkg"))
            {
                defaultPackageName = parameters.getCommandLineArg("-pkg");
            }
            ignorePragma = parameters.argumentExists("-ignorePragma");

            // emit Java code for decoders
            JavaEmitter javaEmitter = new JavaEmitter(parameters.getOutPathName(),
                    defaultPackageName);
            runEmitter(emitter, rootNode, javaEmitter);

            // emit Java __Visitor interface
            VisitorEmitter visitorEmitter = new VisitorEmitter(parameters
                    .getOutPathName(), defaultPackageName);
            runEmitter(emitter, rootNode, visitorEmitter);

            // emit Java __DepthFirstVisitor class
            DepthFirstVisitorEmitter dfVisitorEmitter = new DepthFirstVisitorEmitter(
                    parameters.getOutPathName(), defaultPackageName);
            runEmitter(emitter, rootNode, dfVisitorEmitter);

            // emit Java __SizeOf class
            SizeOfEmitter sizeOfEmitter = new SizeOfEmitter(parameters
                    .getOutPathName(), defaultPackageName);
            runEmitter(emitter, rootNode, sizeOfEmitter);

            // emit Java __Const class
            ConstEmitter constEmitter = new ConstEmitter(parameters
                    .getOutPathName(), defaultPackageName);
            runEmitter(emitter, rootNode, constEmitter);

            // emit Java __XmlDumper class
            XmlDumperEmitter xmlDumper = new XmlDumperEmitter(parameters
                    .getOutPathName(), defaultPackageName);
            runEmitter(emitter, rootNode, xmlDumper);

            // emit Java __LabelSetter class
            LabelSetterEmitter labelSetter = new LabelSetterEmitter(parameters
                    .getOutPathName(), defaultPackageName);
            runEmitter(emitter, rootNode, labelSetter);
        }
        catch (ComputeError e)
        {
            System.err.println("emitter error in '" + parameters.getFileName()
                    + "': " + e.getMessage());
        }
    }


    private void runEmitter(DataScriptEmitter emitter, TokenAST rootNode,
            JavaDefaultEmitter javaEmitter)
    {
        javaEmitter.setRdsVersion(parameters.getVersion());
        javaEmitter.setThrowsException(generateExceptions);
        javaEmitter.setIgnorePragma(ignorePragma);
        emitter.setEmitter(javaEmitter);
        try
        {
            emitter.root(rootNode);
        }
        catch (RecognitionException exc)
        {
            throw new DataScriptException();
        }
    }

    public void getOptions(org.apache.commons.cli.Options rdsOptions)
    {
        Option rdsOption;

        rdsOption = new Option("pkg", true, 
                "\"packagename\"\tJava package name for types without a DataScript package");
        rdsOption.setRequired(false);
        rdsOptions.addOption(rdsOption);

        rdsOption = new Option("debug", false, 
                "enables throwing exceptions in equals() function, when objects are not equal");
        rdsOption.setRequired(false);
        rdsOptions.addOption(rdsOption);

        rdsOption = new Option("ignorePragma", false, 
                "do not pass sql_pragma into generated code");
        rdsOption.setRequired(false);
        rdsOptions.addOption(rdsOption);
    }


    public void setParameters(Parameters params)
    {
        this.parameters = params;
    }

}
