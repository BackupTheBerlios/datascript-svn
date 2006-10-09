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
package datascript.emit.java;

import datascript.ast.CompoundType;
import datascript.ast.Field;
import datascript.ast.UnionType;
import datascript.jet.java.SequenceEnd;
import datascript.jet.java.UnionBegin;
import datascript.jet.java.UnionRead;

public class UnionEmitter extends CompoundEmitter
{
    private UnionType union;
    private UnionFieldEmitter fieldEmitter;
    private UnionBegin beginTmpl = new UnionBegin();
    private SequenceEnd endTmpl = new SequenceEnd();
    private UnionRead readTmpl = new UnionRead();
    
    public UnionEmitter(JavaEmitter j)
    {
        super(j);
        fieldEmitter = new UnionFieldEmitter(j);
    }
   
    public UnionType getUnionType()
    {
        return union;
    }
    
    public CompoundType getCompoundType()
    {
        return union;
    }        

    public FieldEmitter getFieldEmitter()
    {
        return fieldEmitter;
    }
    
    public void begin(UnionType s)
    {
        union = s;
        String result = beginTmpl.generate(this);
        out.print(result);
        
        for (Field field : s.getFields())
        {
            fieldEmitter.emit(field);
        }
        result = readTmpl.generate(this);
        out.print(result);
    }
    
    public void end(UnionType s)
    {
        String result = endTmpl.generate(this);
        out.print(result);
    }    
}
