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
public class Expression extends TokenAST
{
    protected Value value;
    protected TypeInterface type;
    protected Scope scope;
    
    public Expression()
    {
        
    }

    public Value getValue()
    {
        return value;
    }
    public void setValue(Value value)
    {
        this.value = value;
    }
    
    public TypeInterface getExprType()
    {
        return type;
    }
    
    public Expression op1()
    {
        return (Expression) getFirstChild();
    }
    
    public Expression op2()
    {
        return (Expression) op1().getNextSibling();
    }
    
    public Expression op3()
    {
        return (Expression) op2().getNextSibling();
    }
    
    public void checkBooleanOperand(Expression op)
    {
        if (! (op.getExprType() instanceof BooleanType))
        {
            ToolContext.logError(op, "boolean type expected");
        }                        
    }

    public void checkIntegerOperand(Expression op)
    {
        if (! (op.getExprType() instanceof IntegerType))
        {
            ToolContext.logError(op, "integer type expected");
        }                        
    }

    
    public void evaluate(Scope scope)
    {
        this.scope = scope;
        switch (getType())
        {
            case DataScriptParserTokenTypes.ID:
                evaluateIdentifier();
                break;

            default:
                throw new InternalError("illegal operation: type = " + 
                                        getType());
                
        }
    }
    
    public void evaluate()
    {
        switch (getType())
        {
            
            case DataScriptParserTokenTypes.LPAREN:
                type  = op1().type;
                value = op1().value;
                break;

                
            case DataScriptParserTokenTypes.DOT:
                evaluateMember();
                break;

            case DataScriptParserTokenTypes.ARRAYELEM:
                evaluateArrayElement();
                break;

            case DataScriptParserTokenTypes.QUESTIONMARK:
                evaluateConditionalExpression();
                break;

            default:
                throw new InternalError("illegal operation: type = " + 
                                        getType());
                
        }
    }
    
    private void evaluateIdentifier()
    {
        String symbol = getText();
        Object obj = scope.getSymbol(symbol);
        if (obj == null)
        {
            ToolContext.logError(this, "'" + symbol + 
                    "' undefined in current scope");
        }
        if (obj instanceof Field)
        {
            Field field = (Field)obj;
            type = TypeReference.resolveType(field.getFieldType());
        }
        else if (obj instanceof TypeReference)
        {
            TypeReference ref = (TypeReference)obj;
            type = TypeReference.resolveType(ref);
        }
        else if (obj instanceof IntegerType)
        {
            type = (IntegerType)obj;
        }
        else if (obj instanceof CompoundType)
        {
            CompoundType compound = (CompoundType)obj;
            if (scope.getOwner().isContainedIn(compound))
            {
                type = compound;                
            }
            else
            {
                ToolContext.logError(this, "current type is not contained in '"
                        + compound.getName() + "'");
            }
        }
        else if (obj instanceof EnumItem)
        {
            // If the enum type was defined before the first use of a member
            // in an expression, we can compute the value at this time.
            // Otherwise, the value is left at null.
            // Thus, when a code emitter visits this expression, it should
            // re-evaluate this symbol.
            EnumItem item = (EnumItem)obj;
            value = item.getValue();
            type =  item.getEnumType();
        }
        else 
        {
            ToolContext.logError(this, "cannot resolve symbol '" + symbol + "'");
        }
    }    

    private void evaluateMember()
    {
        TypeInterface t = op1().getExprType();
        if (!(t instanceof CompoundType))
        {
            ToolContext.logError(this, "compound type expected");
        }
        CompoundType compound = (CompoundType)t;
        String symbol = op1().getNextSibling().getText();
        Object obj = compound.getScope().getSymbol(symbol);
        if (obj == null)
        {
            ToolContext.logError(this, "'" + symbol + 
                    "' undefined in current scope");
        }
        if (obj instanceof Field)
        {
            Field field = (Field)obj;
            type = TypeReference.resolveType(field.getFieldType());
        }
        else if (obj instanceof CompoundType)
        {
            type = (CompoundType)obj;
        }
        else 
        {
            throw new InternalError("cannot resolve symbol '" + symbol + "'");
        }
    }    

    private void evaluateArrayElement()
    {
        TypeInterface t = op1().getExprType();
        if (!(t instanceof ArrayType))
        {
            ToolContext.logError(this, "array type expected");
        }
        ArrayType array = (ArrayType)t;
        if (! (op2().getExprType() instanceof IntegerType))
        {
            ToolContext.logError(op2(), "integer type expected");
        }
        type = array.getElementType();
    }    
    
    public void checkInteger()
    {
        if (! (type instanceof IntegerType))
        {
            ToolContext.logError(this, "integer type expected");
        }
    }
    
    public void evaluateConditionalExpression()
    {
        Expression op1 = op1();
        Expression op2 = op2();
        Expression op3 = op3();
        checkBooleanOperand(op1);
        if (! IntegerType.checkCompatibility(op2.type, op3.type))
        {
            ToolContext.logError(op2, "types in conditional expression are incompatible");
        }
        if (op1().value == null)
        {
            type = op2.type;
        }
        else
        {
            Expression result = 
            ((BooleanValue)op1.value).booleanValue() ? op2 : op3;
            value = result.value;
            type  = result.type;
        }
    }
}
