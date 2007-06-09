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
package datascript.tools;

import datascript.antlr.DataScriptParser;


public interface Parameters
{
    /**
     * For historical reason, it was not nessecary to give a package name in the script file
     * This method gives a default name for a package.
     * @return  returns the name of the default package, if non is given in a DataScript package
     */
    public String getDefaultPackageName();


    /**
     * This is nessecary for the html extension. This functions tells the extension 
     * if the generation of html documentation ist needed.
     * @return  returns true if the "-doc" flag is set at commandline, false if not
     */
    public boolean getGenerateDocs();


    /**
     * 
     * @return  returns true if the "-c" flag is set at commandline, false if not
     */
    public boolean getCheckSyntax();


    /**
     * 
     * @return  returns the (relative) pathname to DataScript source files
     */
    public String getPathName();


    /**
     * 
     * @return  returns the (relative) pathname to the outputdirectory
     */
    public String getOutPathName();


    /**
     * 
     * @return  returns the name of the initial DataScript file
     */
    public String getFileName();


    /**
     * The xml-extension needs this parser to resolve the name of a node in the AST.
     * Normally the emitter ist given to process the AST.  
     * @return  returns a datascript parser
     */
    public DataScriptParser getParser();


    /**
     * This method tests if a argument exists
     * @param   key name of the key to test if it exists
     * @return  true if key is present, fals if not
     */
    public boolean argumentExists(String key);


    /**
     * This method returns the value of a specific commandlineargument
     * @param   key name of the key to get his value from
     * @return  returns the value of the argument to a given key
     * @throws  Exception
     */
    public String getCommandlineArg(String key) throws Exception;
}
