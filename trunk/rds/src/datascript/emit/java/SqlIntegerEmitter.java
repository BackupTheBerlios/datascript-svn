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


import java.io.PrintStream;
import java.util.List;

import datascript.ast.CompoundType;
import datascript.ast.Field;
import datascript.ast.SqlIntegerType;
import datascript.jet.java.SqlInteger;



/**
 * @author HWedekind
 * 
 */
public class SqlIntegerEmitter extends CompoundEmitter
{
    private SqlIntegerType integerType;
    private PrintStream out;
    private SqlInteger integerTmpl;


    public SqlIntegerEmitter(JavaEmitter j, SqlIntegerType integerType)
    {
        super(j);
        this.integerType = integerType;
        integerTmpl = new SqlInteger();
    }


    public List<Field> getFields()
    {
        return integerType.getFields();
    }


    public String getName()
    {
        return integerType.getName();
    }


    public SqlIntegerType getSqlIntegerType()
    {
        return integerType;
    }


    @Override
    public CompoundType getCompoundType()
    {
        return integerType;
    }


    public void setOutputStream(PrintStream out)
    {
        this.out = out;
    }


    public void emit(SqlIntegerType SqlIntegerType)
    {
        String result = integerTmpl.generate(this);
        out.print(result);
    }


    public String emitEncoding()
    {
        final StringBuffer stringBuffer = new StringBuffer();
        final int fieldCount = getFields().size();
        for (int i = 1; i < fieldCount; i++)
        {
            stringBuffer.append('(');
        }
        boolean isFirst = true;
        for (Field field : getFields())
        {
            if (isFirst)
            {
                stringBuffer.append(field);
                isFirst = false;
            }
            else
            {
                stringBuffer.append(" << ");
                stringBuffer.append(field.getFieldType().sizeof(null));
                stringBuffer.append(") + ");
                stringBuffer.append(field);
            }
        }
        return stringBuffer.toString();
    }
}
