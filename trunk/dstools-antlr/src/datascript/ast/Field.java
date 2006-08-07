/*
 * Field.java
 *
 * @author: Godmar Back
 * @version: $Id: Field.java,v 1.2 2003/06/19 19:53:34 gback Exp $
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

    CompoundType getCompound()
    {
        return compound;
    }



    TokenAST getCondition()
    {
        return fieldCondition;
    }

    public void setCondition(AST fieldCondition)
    {
        this.fieldCondition = (TokenAST) fieldCondition;
    }

    public TokenAST getOptionalClause()
    {
        return fieldOptionalClause;
    }

    public void setOptionalClause(AST optional)
    {
        this.fieldOptionalClause = (TokenAST)optional;
    }

    public TokenAST getInitializer()
    {
        return fieldInitializer;
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
