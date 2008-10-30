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


import antlr.collections.AST;
import datascript.antlr.util.TokenAST;
import datascript.antlr.util.ToolContext;
import datascript.ast.CompoundType;
import datascript.ast.ConstType;
import datascript.ast.EnumType;
import datascript.ast.Subtype;
import datascript.ast.TypeInterface;



public class ContentEmitter extends DefaultHTMLEmitter
{
    private final CompoundEmitter ce;
    private final EnumerationEmitter ee;
    private final SubtypeEmitter se;
    private final ConstTypeEmitter cte;


    public ContentEmitter(String outputPath)
    {
        super(outputPath);

        ce = new CompoundEmitter(outputPath);
        ee = new EnumerationEmitter(outputPath);
        se = new SubtypeEmitter(outputPath);
        cte = new ConstTypeEmitter(outputPath);
    }


    @Override
    public void beginRoot(AST root)
    {
        setCurrentFolder(CONTENT_FOLDER);
    }


    @Override
    public void endPackage(AST p)
    {
        for (String typeName : currentPackage.getLocalTypeNames())
        {
            TypeInterface t = currentPackage.getLocalType(typeName);
            TokenAST type = (TokenAST) t;
            if (type instanceof CompoundType)
            {
                ce.emit((CompoundType) type);
            }
            else if (type instanceof EnumType)
            {
                ee.emit((EnumType) type);
            }
            else if (type instanceof Subtype)
            {
                se.emit((Subtype) type);
            }
            else if (type instanceof ConstType)
            {
                cte.emit((ConstType) type);
            }
            else
            {
                ToolContext.logWarning((TokenAST)p, "don't know how to emit content for type " + type);
            }
        }
    }

}
