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
package datascript.ast;

import antlr.collections.AST;

public class Field extends TokenAST
{
    private TypeInterface type;

    private TokenAST name; // null is anonymous

    private CompoundType compound; // compound type containing this field

    private TokenAST fieldCondition;

    private TokenAST fieldInitializer;

    private TokenAST fieldLabel;


    private TokenAST fieldOptionalClause;





    public Field()
    {
        
    }
    

    IntegerValue sizeof(Context ctxt)
    {
        if (fieldOptionalClause != null)
        {
            throw new Error("sizeof cannot be applied to optional field");
        }
        return type.sizeof(ctxt);
    }

    public String getName()
    {
        String result = (name == null) ? "<void>" : name.getText(); 
        return result;
    }
    
    public void setName(AST name)
    {
        this.name = (TokenAST)name;
    }

    public TypeInterface getFieldType()
    {
        TypeInterface t = (TypeInterface)getFirstChild();
        return t;
    }

    TokenAST getLabel()
    {
        return fieldLabel;
    }
    
    public void setLabel(AST label)
    {
        this.fieldLabel = (TokenAST)label;
    }

    public CompoundType getCompound()
    {
        return compound;
    }



    public Expression getCondition()
    {
        if (fieldCondition != null)
        {
            return (Expression)fieldCondition.getFirstChild();
        }
        else
        {
            return null;
        }
    }

    public void setCondition(AST fieldCondition)
    {
        this.fieldCondition = (TokenAST) fieldCondition;
    }

    public Expression getOptionalClause()
    {
        if (fieldOptionalClause != null)
        {
            return (Expression) fieldOptionalClause.getFirstChild();
        }
        else
        {
            return null;
        }
    }

    public void setOptionalClause(AST optional)
    {
        this.fieldOptionalClause = (TokenAST)optional;
    }

    public Expression getInitializer()
    {
        if (fieldInitializer != null)
        {
            return (Expression)fieldInitializer.getFirstChild();
        }
        else
        {
            return null;
        }
    }

    public void setInitializer(AST fieldInitializer)
    {
        this.fieldInitializer = (TokenAST)fieldInitializer;
    }

    void setCompound(CompoundType compound)
    {
        this.compound = compound;
    }

    public String toString()
    {
        return getName();
        //return "Field name='" + name + "' type='" + getFieldType() + "' compound='"
        //        + compound + "'";
    }

}
