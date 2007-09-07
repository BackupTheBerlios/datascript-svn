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


package datascript.backend.java;


import antlr.RecognitionException;
import datascript.ast.ComputeError;
import datascript.antlr.DataScriptEmitter;
import datascript.antlr.util.TokenAST;
import datascript.ast.DataScriptException;
import datascript.emit.java.ConstEmitter;
import datascript.emit.java.DepthFirstVisitorEmitter;
import datascript.emit.java.JavaEmitter;
import datascript.emit.java.SizeOfEmitter;
import datascript.emit.java.VisitorEmitter;
import datascript.emit.java.XmlDumperEmitter;
import datascript.tools.Extension;
import datascript.tools.Parameters;



public class JavaExtension implements Extension
{
    private Parameters params = null;


    /*
     * (non-Javadoc)
     * 
     * @see datascript.tools.Extension#generate(datascript.antlr.DataScriptEmitter,
     *      datascript.ast.TokenAST)
     */
    public void generate(DataScriptEmitter emitter, TokenAST rootNode)
            throws DataScriptException, RecognitionException
    {
        if (params == null)
            throw new DataScriptException("No parameters set for JavaBackend!");

        System.out.println("emitting java code");
        try
        {
            boolean generateExceptions = params.argumentExists("-java_e");
            boolean useFreeMarker = params.argumentExists("-java_fm");
            String defaultPackageName = "";
            try
            {
                defaultPackageName = params.getCommandlineArg("-pkg");
            }
            catch (Exception e)
            {
            }

            // emit Java code for decoders
            JavaEmitter javaEmitter = new JavaEmitter(params.getOutPathName(),
                    defaultPackageName);
            if (useFreeMarker)
            {
                JavaEmitter.useFreeMarker();
            }
            javaEmitter.setRDSVersion(params.getVersion());
            javaEmitter.setThrowsException(generateExceptions);
            emitter.setEmitter(javaEmitter);
            emitter.root(rootNode);

            // emit Java __Visitor interface
            VisitorEmitter visitorEmitter = new VisitorEmitter(params
                    .getOutPathName(), defaultPackageName);
            if (useFreeMarker)
            {
                VisitorEmitter.useFreeMarker();
            }
            visitorEmitter.setRDSVersion(params.getVersion());
            javaEmitter.setThrowsException(generateExceptions);
            emitter.setEmitter(visitorEmitter);
            emitter.root(rootNode);

            // emit Java __DepthFirstVisitor class
            DepthFirstVisitorEmitter dfVisitorEmitter = new DepthFirstVisitorEmitter(
                    params.getOutPathName(), defaultPackageName);
            if (useFreeMarker)
            {
                DepthFirstVisitorEmitter.useFreeMarker();
            }
            dfVisitorEmitter.setRDSVersion(params.getVersion());
            javaEmitter.setThrowsException(generateExceptions);
            emitter.setEmitter(dfVisitorEmitter);
            emitter.root(rootNode);

            // emit Java __SizeOf class
            SizeOfEmitter sizeOfEmitter = new SizeOfEmitter(params
                    .getOutPathName(), defaultPackageName);
            if (useFreeMarker)
            {
                SizeOfEmitter.useFreeMarker();
            }
            sizeOfEmitter.setRDSVersion(params.getVersion());
            javaEmitter.setThrowsException(generateExceptions);
            emitter.setEmitter(sizeOfEmitter);
            emitter.root(rootNode);

            // emit Java __Const class
            ConstEmitter constEmitter = new ConstEmitter(params
                    .getOutPathName(), defaultPackageName);
            if (useFreeMarker)
            {
                ConstEmitter.useFreeMarker();
            }
            constEmitter.setRDSVersion(params.getVersion());
            javaEmitter.setThrowsException(generateExceptions);
            emitter.setEmitter(constEmitter);
            emitter.root(rootNode);

            // emit Java __XmlDumper class
            XmlDumperEmitter xmlDumper = new XmlDumperEmitter(params
                    .getOutPathName(), defaultPackageName);
            if (useFreeMarker)
            {
                XmlDumperEmitter.useFreeMarker();
            }
            xmlDumper.setRDSVersion(params.getVersion());
            javaEmitter.setThrowsException(generateExceptions);
            emitter.setEmitter(xmlDumper);
            emitter.root(rootNode);
        }
        catch (RuntimeException e)
        {
            System.err.println("emitter error in '" + params.getFileName()
                    + "': " + e.getMessage());
            e.printStackTrace();
        }
        catch (ComputeError e)
        {
            System.err.println("emitter error in '" + params.getFileName()
                    + "': " + e.getMessage());
        }
    }


    /*
     * (non-Javadoc)
     * 
     * @see datascript.tools.Extension#setParameter(datascript.tools.Parameters)
     */
    public void setParameter(Parameters params)
    {
        this.params = params;
    }


    /* (non-Javadoc)
     * @see datascript.tools.Extension#printUsage()
     */
    public String getUsage()
    {
        final String NL = System.getProperties().getProperty("line.separator");
        final StringBuilder buffer = new StringBuilder();

        buffer.append(" -pkg \"packagename\"\tJava package name for types without a DataScript package" + NL);
        buffer.append(" -java_e\t\tenables throwing exceptions in equals() function, when objects are not equal" + NL);
        buffer.append(" -java_fm\t\tonly for testing, turns on template emmitting using freemarker" + NL);
        return buffer.toString();
    }

}
