package datascript.instance;

import java.util.HashMap;
import java.util.Map;

import datascript.antlr.DataScriptParserTokenTypes;
import datascript.ast.Expression;
import datascript.ast.Field;

public class ExpressionEvaluator
{
    private Map<Integer, Long> fieldToValueMap = new HashMap<Integer, Long>();

    public long evaluate(Expression expr)
    {
        long result = 0;
        switch (expr.getType())
        {

            case DataScriptParserTokenTypes.ID:
                result = evaluateIdentifier(expr);
                break;

//            case DataScriptParserTokenTypes.INTEGER_LITERAL:
//                evaluateInteger();
//                break;
//
//            case DataScriptParserTokenTypes.LPAREN:
//                type = op1().type;
//                value = op1().value;
//                break;
//
//            case DataScriptParserTokenTypes.DOT:
//                evaluateMember();
//                break;
//
//            case DataScriptParserTokenTypes.ARRAYELEM:
//                evaluateArrayElement();
//                break;
//
//            case DataScriptParserTokenTypes.QUESTIONMARK:
//                evaluateConditionalExpression();
//                break;
//
//            case DataScriptParserTokenTypes.INDEX:
//                evaluateIndexExpression();
//                break;
//
//            case DataScriptParserTokenTypes.LT:
//            case DataScriptParserTokenTypes.LE:
//            case DataScriptParserTokenTypes.GT:
//            case DataScriptParserTokenTypes.GE:
//            case DataScriptParserTokenTypes.EQ:
//            case DataScriptParserTokenTypes.NE:
//                evaluateRelationalExpression();
//                break;
//
//            case DataScriptParserTokenTypes.PLUS:
//            case DataScriptParserTokenTypes.MINUS:
//            case DataScriptParserTokenTypes.MULTIPLY:
//            case DataScriptParserTokenTypes.DIVIDE:
//            case DataScriptParserTokenTypes.MODULO:
//            case DataScriptParserTokenTypes.LSHIFT:
//            case DataScriptParserTokenTypes.RSHIFT:
//                evaluateArithmeticExpression();
//                break;
//
//            case DataScriptParserTokenTypes.LOGICALAND:
//            case DataScriptParserTokenTypes.LOGICALOR:
//                evaluateLogicalExpression();
//                break;
//
//            case DataScriptParserTokenTypes.LENGTHOF:
//                evaluateLengthOfExpression();
//                break;
//
//            case DataScriptParserTokenTypes.SIZEOF:
//                evaluateSizeOfExpression();
//                break;
//
//            case DataScriptParserTokenTypes.BITSIZEOF:
//                evaluateSizeOfExpression();
//                break;
//
//            case DataScriptParserTokenTypes.SUM:
//                evaluateSumFunction();
//                break;
//
//            case DataScriptParserTokenTypes.FUNCTIONCALL:
//                evaluateFunctionCallExpression();
//                break;
//
//            case DataScriptParserTokenTypes.EXPLICIT:
//                // do nothing
//                break;

            default:
                throw new InternalError();

        }
        return result;
    }
    
    private long evaluateIdentifier(Expression expr)
    {
        String symbol = expr.getText();
        Object obj = expr.getScope().getTypeOrSymbol(symbol);
        if (obj instanceof Field)
        {
            Field field = (Field) obj;
            long result = fieldToValueMap.get(field.getId());
            return result;
        }
        
        throw new IllegalStateException();
    }

    public void storeFieldValue(int fieldId, long value)
    {
        fieldToValueMap.put(fieldId, value);
    }
}
