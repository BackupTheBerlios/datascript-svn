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
package datascript.emit.html;


import org.apache.commons.cli.Option;

import datascript.antlr.DataScriptEmitter;
import datascript.antlr.util.TokenAST;
import datascript.ast.DataScriptException;
import datascript.tools.Extension;
import datascript.tools.Parameters;


public class HtmlExtension implements Extension
{
    private Parameters params = null;

    /* (non-Javadoc)
     * @see datascript.tools.Extension#generate(datascript.antlr.DataScriptEmitter, datascript.ast.TokenAST)
     */
    public void generate(DataScriptEmitter emitter, TokenAST rootNode)
            throws Exception
    {
        if (params == null)
            throw new DataScriptException("No parameters set for HtmlBackend!");

        if (!params.argumentExists("-doc"))
        {
            System.out.println("emitting html documentation is disabled.");
            return;
        }

        System.out.println("emitting html documentation");

        ContentEmitter htmlEmitter = new ContentEmitter(params.getDocPathName());

        // emit frameset
        htmlEmitter.emitFrameset();

        // emit stylesheets
        htmlEmitter.emitStylesheet();

        // emit HTML documentation
        emitter.setEmitter(htmlEmitter);
        emitter.root(rootNode);

        // emit list of packages
        PackageEmitter packageEmitter = new PackageEmitter(params.getDocPathName());
        emitter.setEmitter(packageEmitter);
        emitter.root(rootNode);

        // emit list of classes
        OverviewEmitter overviewEmitter = new OverviewEmitter(params.getDocPathName());
        emitter.setEmitter(overviewEmitter);
        emitter.root(rootNode);
    }


    public void getOptions(org.apache.commons.cli.Options rdsOptions)
    {
        Option rdsOption;

        rdsOption = new Option("doc", true, 
                "enables generation of Javadoc-style documentation. " +
                "Optionaly you can specify a path to a output directory." +
                "Default directory name is 'html'.");
        rdsOption.setRequired(false);
        rdsOptions.addOption(rdsOption);
    }


    /* (non-Javadoc)
     * @see datascript.tools.Extension#setParameter(datascript.tools.Parameters)
     */
    public void setParameter(Parameters params)
    {
        this.params = params;
    }

}
