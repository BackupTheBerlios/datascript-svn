/**
 * 
 */
package datascript.emit.java;

import datascript.antlr.DataScriptParserTokenTypes;
import datascript.ast.Expression;
import datascript.ast.Value;

/**
 * @author HWellmann
 *
 */
public class ExpressionEmitter
{
    private Expression expression;
    private StringBuilder buffer;
    
    public ExpressionEmitter()
    {
        
    }
    
    public String emit(Expression expr)
    {
        this.expression = expr;
        buffer = new StringBuilder();
        append(expr);
        return buffer.toString();
    }
    
    private void append(Expression expr)
    {
        switch (expr.getNumberOfChildren())
        {
            case 0:
                emitAtom(expr);
                break;
            case 1:
                emitUnaryExpression(expr);
                break;
            case 2:
                emitBinaryExpression(expr);
                break;
            case 3:
                emitTernaryExpression(expr);
                break;
            default:
                throw new IllegalArgumentException();
                
        }       
    }
    
    private void emitAtom(Expression expr)
    {
        switch (expr.getType())
        {
            case DataScriptParserTokenTypes.INTEGER_LITERAL:
                Value value = expr.getValue();
                buffer.append(value.integerValue());
                break;

            case DataScriptParserTokenTypes.ID:
                buffer.append(expr.getText());
                break;

            default:
                throw new IllegalArgumentException("type = " + expr.getType());
        }
    }

    private void emitUnaryExpression(Expression expr)
    {
        char op;
        boolean paren = false;
        switch (expr.getType())
        {
            case DataScriptParserTokenTypes.UPLUS:
                op = '+';
                break;
            case DataScriptParserTokenTypes.UMINUS:
                op = '-';
                break;
            case DataScriptParserTokenTypes.TILDE:
                op = '~';
                break;
            case DataScriptParserTokenTypes.BANG:
                op = '!';
                break;
            case DataScriptParserTokenTypes.LPAREN:
                op = '(';
                paren = true;
                break;
            default:
                throw new IllegalArgumentException();
        }
        buffer.append(op);
        append(expr.op1());
        if (paren)
        {
            buffer.append(')');
        }
    }

    private void emitBinaryExpression(Expression expr)
    {
        String op;
        switch (expr.getType())
        {
            case DataScriptParserTokenTypes.COMMA:
                op = ", ";
                break;
        
            case DataScriptParserTokenTypes.LOGICALOR:
                op = " || ";
                break;
        
            case DataScriptParserTokenTypes.LOGICALAND:
                op = " && ";
                break;
        
            case DataScriptParserTokenTypes.OR:
                op = " | ";
                break;
        
            case DataScriptParserTokenTypes.XOR:
                op = " ^ ";
                break;
        
            case DataScriptParserTokenTypes.AND:
                op = " & ";
                break;
        
            case DataScriptParserTokenTypes.EQ:
                op = " == ";
                break;
        
            case DataScriptParserTokenTypes.NE:
                op = " != ";
                break;
        
            case DataScriptParserTokenTypes.LT:
                op = " < ";
                break;
        
            case DataScriptParserTokenTypes.LE:
                op = " <= ";
                break;
        
            case DataScriptParserTokenTypes.GE:
                op = " >= ";
                break;
        
            case DataScriptParserTokenTypes.GT:
                op = " > ";
                break;
        
            case DataScriptParserTokenTypes.LSHIFT:
                op = " << ";
                break;
        
            case DataScriptParserTokenTypes.RSHIFT:
                op = " >> ";
                break;
        
            case DataScriptParserTokenTypes.PLUS:
                op = " + ";
                break;
        
            case DataScriptParserTokenTypes.MINUS:
                op = " - ";
                break;
        
            case DataScriptParserTokenTypes.MULTIPLY:
                op = " * ";
                break;
        
            case DataScriptParserTokenTypes.DIVIDE:
                op = " / ";
                break;
        
            case DataScriptParserTokenTypes.MODULO:
                op = " > ";
                break;
        
            case DataScriptParserTokenTypes.ARRAYELEM:
                appendArrayExpression(expr);
                return;
        
            case DataScriptParserTokenTypes.DOT:
                appendDotExpression(expr);
                return;
        
            default:
                throw new IllegalArgumentException();
        }
        append(expr.op1());
        buffer.append(op);
        append(expr.op2());
    }

    private void emitTernaryExpression(Expression expr)
    {
        
    }
    
    private void appendArrayExpression(Expression expr)
    {
        append(expr.op1());
        buffer.append('[');
        append(expr.op2());
        buffer.append(']');        
    }

    private void appendDotExpression(Expression expr)
    {
        append(expr.op1());
        buffer.append('.');
        
        append(expr.op2());        
    }
}
