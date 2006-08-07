/* BSD License
 *
 * Copyright (c) 2006, Harald Wellmann, Harman/Becker Automotive Systems
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
package datascript.emit;

import antlr.collections.AST;
import datascript.ast.EnumItem;
import datascript.ast.EnumType;
import datascript.ast.Field;
import datascript.ast.StructType;
import datascript.ast.UnionType;

/**
 * @author HWellmann
 *
 */
public class ConsoleEmitter implements Emitter
{

    /* (non-Javadoc)
     * @see datascript.emit.Emitter#beginTranslationUnit()
     */
    public void beginTranslationUnit()
    {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see datascript.emit.Emitter#endTranslationUnit()
     */
    public void endTranslationUnit()
    {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see datascript.emit.Emitter#beginField(antlr.collections.AST)
     */
    public void beginField(AST f)
    {
        Field field = (Field)f;
        System.out.println("begin " + field.getName());
    }

    /* (non-Javadoc)
     * @see datascript.emit.Emitter#endField(antlr.collections.AST)
     */
    public void endField(AST f)
    {
        Field field = (Field)f;
        System.out.println("end " + field.getName());
    }

    /* (non-Javadoc)
     * @see datascript.emit.Emitter#beginSequence(antlr.collections.AST)
     */
    public void beginSequence(AST s)
    {
        StructType struct = (StructType)s;
        System.out.println("begin " + struct.getName());
    }

    /* (non-Javadoc)
     * @see datascript.emit.Emitter#endSequence(antlr.collections.AST)
     */
    public void endSequence(AST s)
    {
        StructType struct = (StructType)s;
        System.out.println("end " + struct.getName());
    }

    /* (non-Javadoc)
     * @see datascript.emit.Emitter#beginUnion(antlr.collections.AST)
     */
    public void beginUnion(AST u)
    {
        UnionType union = (UnionType)u;
        System.out.println("begin " + union.getName());
    }

    /* (non-Javadoc)
     * @see datascript.emit.Emitter#endUnion(antlr.collections.AST)
     */
    public void endUnion(AST u)
    {
        UnionType union = (UnionType)u;
        System.out.println("end " + union.getName());
    }

    public void beginEnumeration(AST e)
    {
        EnumType enumType = (EnumType)e;
        System.out.println("begin " + enumType.getName());
    }

    /* (non-Javadoc)
     * @see datascript.emit.Emitter#endUnion(antlr.collections.AST)
     */
    public void endEnumeration(AST e)
    {
        EnumType enumType = (EnumType)e;
        System.out.println("end " + enumType.getName());
    }

    public void beginEnumItem(AST e)
    {
        EnumItem item = (EnumItem)e;
        System.out.println("begin " + item.getName());
    }

    public void endEnumItem(AST e)
    {
        EnumItem item = (EnumItem)e;
        System.out.println("end " + item.getName());
    }
}
