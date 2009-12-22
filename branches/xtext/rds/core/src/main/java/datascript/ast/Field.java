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


import antlr.Token;
import antlr.collections.AST;
import datascript.antlr.DataScriptParserTokenTypes;
import datascript.antlr.util.TokenAST;



@SuppressWarnings("serial")
public class Field extends TokenAST
{
    private TypeInterface type;
    private TokenAST name; // null is anonymous
    private Container compound; // compound type containing this field

    private TokenAST alignment;
    private TokenAST condition;
    private TokenAST initializer;
    private TokenAST fieldLabel;
    private TokenAST fieldOptionalClause;
    private Token documentation;
    private boolean usedInExpression;
    private int id;

    public Field()
    {
        id = FieldRegistry.registerField(this);
    }


    public IntegerValue sizeof(Scope ctxt)
    {
        if (fieldOptionalClause != null)
        {
            throw new Error("sizeof cannot be applied to optional field");
        }
        type = getFieldType();
        return type.sizeof(ctxt);
    }


    public IntegerValue bitsizeof(Scope ctxt)
    {
        if (fieldOptionalClause != null)
        {
            throw new Error("sizeof cannot be applied to optional field");
        }
        type = getFieldType();
        return type.bitsizeof(ctxt);
    }


    public String getName()
    {
        String result = (name == null) ? "<void>" : name.getText();
        return result;
    }


    public void setName(AST name)
    {
        this.name = (TokenAST) name;
    }


    public TypeInterface getFieldType()
    {
        TypeInterface t = (TypeInterface) getFirstChild();
        return t;
    }


    public Expression getLabel()
    {
        if (fieldLabel != null)
        {
            return (Expression) fieldLabel.getFirstChild();
        }
        else
        {
            return null;
        }
    }


    public void setLabel(AST label)
    {
        this.fieldLabel = (TokenAST) label;
    }


    public Container getCompound()
    {
        return compound;
    }


    void setCompound(Container compound)
    {
        this.compound = compound;
    }


    public Expression getCondition()
    {
        if (condition != null)
        {
            return (Expression) condition.getFirstChild();
        }
        else
        {
            return null;
        }
    }


    public void setCondition(AST fieldCondition)
    {
        this.condition = (TokenAST) fieldCondition;
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
        this.fieldOptionalClause = (TokenAST) optional;
    }


    public Expression getInitializer()
    {
        if (initializer != null)
        {
            return (Expression) initializer.getFirstChild();
        }
        else
        {
            return null;
        }
    }


    public void setInitializer(AST fieldInitializer)
    {
        this.initializer = (TokenAST) fieldInitializer;
    }


    public String getDocumentation()
    {
        return (documentation == null) ? "" : documentation.getText();
    }


    public void setDocumentation(Token doc)
    {
        documentation = doc;
    }


    public String getSqlConstraint()
    {
        String result = "";
        AST node = findFirstChildOfType(DataScriptParserTokenTypes.SQL);
        if (node != null)
        {
            String text = node.getFirstChild().getText();
            if (text.length() > 0)
                result = text.substring(1, text.length() - 1);
        }
        return result;

    }


    @Override
    public String toString()
    {
        return (name == null) ? "FIELD" : name.getText();
    }


    public TokenAST getAlignment()
    {
        return alignment;
    }


    public void setAlignment(AST alignment)
    {
        this.alignment = (TokenAST) alignment;
    }


    public int getAlignmentValue()
    {
        IntegerExpression alignExpr = (IntegerExpression)alignment.getFirstChild();
        if (alignExpr.getValue() == null)
            alignExpr.evaluate();
        return alignExpr.getValue().integerValue().intValue();
    }


    public boolean isUsedInExpression()
    {
        return usedInExpression;
    }


    public void setUsedInExpression(boolean usedInExpression)
    {
        this.usedInExpression = usedInExpression;
    }
    
    public int getId()
    {
        return id;
    }

}
