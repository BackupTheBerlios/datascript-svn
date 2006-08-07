/**
 * 
 */
package datascript.ast;

import antlr.collections.AST;
import datascript.antlr.DataScriptParserTokenTypes;

/**
 * @author HWellmann
 *
 */
public class EnumItem extends TokenAST
{
    private EnumType enumType;
    private IntegerValue value;
    private String name;
    
    public EnumItem()
    {
        
    }
    
    public String getName()
    {
        if (name == null)
        {
            AST n = getFirstChild();
            if (n.getType() != DataScriptParserTokenTypes.ID)
            {
                n = n.getNextSibling();
            }
            name = n.getText();
        }
        return name;
    }
    
    public EnumType getEnumType()
    {
        return enumType;
    }
    
    public void setEnumType(EnumType enumType)
    {
        this.enumType = enumType;
    }

    public void setValue(IntegerValue value)
    {
        this.value = value;
    }   
    
    public IntegerValue getValue()
    {
        return value;
    }
}
