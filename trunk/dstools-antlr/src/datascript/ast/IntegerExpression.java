/**
 * 
 */
package datascript.ast;

import datascript.antlr.DataScriptParserTokenTypes;

/**
 * @author HWellmann
 *
 */
public class IntegerExpression extends Expression
{
    public IntegerExpression()
    {
        type  = IntegerType.integerType;        
    }

    public void evaluate()
    {
        switch (getType())
        {
            case DataScriptParserTokenTypes.INTEGER_LITERAL:
                value = Value.makeValue(getText());
                break;
                               
            case DataScriptParserTokenTypes.PLUS:   
            case DataScriptParserTokenTypes.MINUS:   
            case DataScriptParserTokenTypes.MULTIPLY:   
            case DataScriptParserTokenTypes.DIVIDE:   
            case DataScriptParserTokenTypes.MODULO:
            case DataScriptParserTokenTypes.OR:
            case DataScriptParserTokenTypes.XOR:
            case DataScriptParserTokenTypes.AND:
            case DataScriptParserTokenTypes.LSHIFT:
            case DataScriptParserTokenTypes.RSHIFT:
                checkIntegerOperand(op1());
                checkIntegerOperand(op2());
                evaluate(op1(), op2());
                break;
                
            case DataScriptParserTokenTypes.UPLUS:   
            case DataScriptParserTokenTypes.UMINUS:
            case DataScriptParserTokenTypes.TILDE:
                checkIntegerOperand(op1());
                evaluate(op1());
                break;

            default:
                throw new InternalError("illegal integer operation: type = " + 
                                        getType());
                
        }
        if (value != null)
        {
            //System.out.println("expr = " + value);
        }
    }
    
    public void evaluate(Expression op1, Expression op2)
    {
        IntegerValue val1 = (IntegerValue)op1.value;
        IntegerValue val2 = (IntegerValue)op2.value;
        if (val1 == null || val2 == null)
            return;
        
        switch(getType())
        {
            case DataScriptParserTokenTypes.PLUS:
                value = val1.add(val2);
                break;
                
            case DataScriptParserTokenTypes.MINUS:   
                value = val1.subtract(val2);
                break;
                
            case DataScriptParserTokenTypes.MULTIPLY:   
                value = val1.multiply(val2);
                break;
                
            case DataScriptParserTokenTypes.DIVIDE:   
                value = val1.divide(val2);
                break;
                
            case DataScriptParserTokenTypes.MODULO:
                value = val1.remainder(val2);
                break;

            case DataScriptParserTokenTypes.OR:
                value = val1.or(val2);
                break;

            case DataScriptParserTokenTypes.XOR:
                value = val1.xor(val2);
                break;

            case DataScriptParserTokenTypes.AND:
                value = val1.and(val2);
                break;

            case DataScriptParserTokenTypes.LSHIFT:
                value = val1.shiftLeft(val2);
                break;

            case DataScriptParserTokenTypes.RSHIFT:
                value = val1.shiftRight(val2);
                break;
                
            default:
                throw new InternalError("illegal integer operation: type = " + 
                                        getType());
                
        }
    }

    public void evaluate(Expression op1)
    {
        IntegerValue val1 = (IntegerValue)op1.value;
        if (val1 == null)
            return;
        
        switch(getType())
        {
            case DataScriptParserTokenTypes.UPLUS:
                value = val1;
                break;
                
            case DataScriptParserTokenTypes.UMINUS:
                value = val1.negate();
                break;
                
            case DataScriptParserTokenTypes.TILDE:
                value = val1.not();
                break;
                
            default:
                throw new InternalError("illegal integer operation: type = " + 
                                        getType());
                
        }
    }
    
}
