/**
 * 
 */
package datascript.ast;

import datascript.antlr.DataScriptParserTokenTypes;
import datascript.tools.ToolContext;

/**
 * @author HWellmann
 *
 */
public class BooleanExpression extends Expression
{
    public BooleanExpression()
    {
        type  = BooleanType.booleanType;        
    }
    
    public void evaluate()
    {
        switch (getType())
        {
            case DataScriptParserTokenTypes.EQ:
            case DataScriptParserTokenTypes.NE:
                testEquality(op1(), op2());
                break;
                               
            case DataScriptParserTokenTypes.LT:   
            case DataScriptParserTokenTypes.LE:   
            case DataScriptParserTokenTypes.GE:   
            case DataScriptParserTokenTypes.GT:   
                checkIntegerOperand(op1());
                checkIntegerOperand(op2());
                compare(op1(), op2());
                break;
                
            case DataScriptParserTokenTypes.LOGICALAND:   
            case DataScriptParserTokenTypes.LOGICALOR:
                checkBooleanOperand(op1());
                checkBooleanOperand(op2());
                evaluate(op1(), op2());
                break;

            case DataScriptParserTokenTypes.BANG:   
                checkBooleanOperand(op1());
                if (op1().value != null)
                {
                    value = ((BooleanValue)op1().value).not();
                }
                break;

            default:
                throw new InternalError("illegal boolean operation: type = " + 
                                        getType());
        }
        if (value != null)
        {
            //System.out.println("expr = " + value);
        }
    }
    

    public void testEquality(Expression op1, Expression op2)
    {
        // Check if types are comparable.
        // Take care of integer types: e.g. uint8 and int16 are comparable,
        // but not equal.
        TypeInterface type1 = op1.getExprType();
        TypeInterface type2 = op2.getExprType();
        if (!IntegerType.checkCompatibility(type1, type2))
        {
            ToolContext.logError(this, "type mismatch in comparison");            
        }
        
        if (op1.value == null || op2.value == null)
            return;
        
        boolean b = (op1.value.compareTo(op2.value) == 0);
        
        switch(getType())
        {
            case DataScriptParserTokenTypes.EQ:
                break;
            case DataScriptParserTokenTypes.NE:
                b = !b;
                break;
                
        }
        value = new BooleanValue(b);
    }
    
    public void compare(Expression op1, Expression op2)
    {
        if (op1.value == null || op2.value == null)
            return;
        
        IntegerValue val1 = (IntegerValue)op1.value;
        IntegerValue val2 = (IntegerValue)op2.value;
        int cmp = val1.compareTo(val2);
        boolean b = false;
        switch(getType())
        {
            case DataScriptParserTokenTypes.LT:
                b = (cmp < 0);
                break;
                
            case DataScriptParserTokenTypes.LE:   
                b = (cmp <= 0);
                break;
                
            case DataScriptParserTokenTypes.GE:   
                b = (cmp >= 0);
                break;
                
            case DataScriptParserTokenTypes.GT:   
                b = (cmp > 0);
                break;                
        }
        value = new BooleanValue(b);
    }

    public void evaluate(Expression op1, Expression op2)
    {
        if (op1.value == null || op2.value == null)
            return;
        
        boolean val1 = ((BooleanValue)op1.value).booleanValue();
        boolean val2 = ((BooleanValue)op2.value).booleanValue();
        boolean b = false;
        switch(getType())
        {
            case DataScriptParserTokenTypes.LOGICALAND:
                b = val1 || val2;
                break;
                
            case DataScriptParserTokenTypes.LOGICALOR:
                b = val1 && val2;
                break;
        }
        value = new BooleanValue(b);
    }
    
}
