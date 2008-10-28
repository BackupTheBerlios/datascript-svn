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
package datascript.emit.html;

import datascript.antlr.DataScriptParserTokenTypes;
import datascript.ast.CompoundType;
import datascript.ast.EnumItem;
import datascript.ast.EnumType;
import datascript.ast.Expression;
import datascript.ast.Field;
import datascript.ast.Parameter;
import datascript.ast.Scope;
import datascript.ast.TypeInterface;
import datascript.ast.Value;

/**
 * @author HWellmann
 *
 */
public class ExpressionEmitter
{
    private StringBuilder buffer;
    private String compoundName;


    public ExpressionEmitter()
    {        
    }
    
    public String emit(Expression expr)
    {
        this.compoundName = null;
        buffer = new StringBuilder();
        append(expr);
        return buffer.toString();
    }
    
    public String emit(Expression expr, String compndName)
    {
        this.compoundName = compndName;
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
                throw new UnsupportedOperationException();
                
        }       
    }
    
    private void emitAtom(Expression expr)
    {
        switch (expr.getType())
        {
            case DataScriptParserTokenTypes.INTEGER_LITERAL:
            case DataScriptParserTokenTypes.UINT8:
                Value value = expr.getValue();
                buffer.append(value.integerValue());
                break;

            case DataScriptParserTokenTypes.ID:
                //buffer.append(expr.getText());
                appendIdentifier(expr);
                break;

            default:
                throw new UnsupportedOperationException("type = " + expr.getType());
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
            case DataScriptParserTokenTypes.INDEX:
                append(expr.op1());
            	buffer.append("$index");
            	return;
            case DataScriptParserTokenTypes.LENGTHOF:
                buffer.append("lengthof ");
                append(expr.op1());
                return;
            case DataScriptParserTokenTypes.SIZEOF:
                buffer.append("sizeof ");
                append(expr.op1());
                return;
            case DataScriptParserTokenTypes.BITSIZEOF:
                buffer.append("bitsizeof ");
                append(expr.op1());
                return;
            case DataScriptParserTokenTypes.SUM:
                buffer.append("sum(");
                append(expr.op1());
                buffer.append(")");
                return;
            case DataScriptParserTokenTypes.FUNCTIONCALL:
                append(expr.op1());
                buffer.append("()");
                return;
            case DataScriptParserTokenTypes.EXPLICIT:
                buffer.append("explicit ");
                buffer.append(expr.op1().getText());
                return;
            default:
                throw new UnsupportedOperationException();
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
                op = " % ";
                break;
        
            case DataScriptParserTokenTypes.ARRAYELEM:
                appendArrayExpression(expr);
                return;
        
            case DataScriptParserTokenTypes.DOT:
                appendDotExpression(expr);
                return;
        
            default:
                throw new UnsupportedOperationException();
        }
        append(expr.op1());
        buffer.append(op);
        append(expr.op2());
    }

    private void emitTernaryExpression(Expression expr)
    {
        switch (expr.getType())
        {
            case DataScriptParserTokenTypes.QUESTIONMARK:
                buffer.append('(');
                append(expr.op1());
                buffer.append(") ? ");
                append(expr.op2());
                buffer.append(" : ");
                append(expr.op3());
                break;
            default:
                throw new UnsupportedOperationException();
        }        
    }
    
    private void appendArrayExpression(Expression expr)
    {
        append(expr.op1());
        buffer.append("[");
        append(expr.op2());
        buffer.append(']');        
    }

    private void appendDotExpression(Expression expr)
    {
        append(expr.op1());
        buffer.append('.');        
        append(expr.op2());        
    }
    
    private void appendIdentifier(Expression expr)
    {
        String symbol = expr.getText();
        Scope scope = expr.getScope();
        Object obj = scope.getTypeOrSymbol(symbol);
        if (obj instanceof EnumType)
        {
            EnumType enumeration = (EnumType) obj;
            buffer.append(enumeration.getName());                
        }
        else if (obj instanceof TypeInterface)
        {
            CompoundType compound = (CompoundType) scope.getOwner();
            if (compound != null && compound.isParameter(symbol))
            {
                emitCompoundPrefix();
                buffer.append(symbol);
            }
            else
            {
                buffer.append(symbol);
                emitCompoundPrefix();
            }
        }
        else if (obj instanceof Parameter)
        {
            Parameter param = (Parameter)obj;
            String pName = param.getName();
            buffer.append(pName);
        }
        else if (obj instanceof Field)
        {
            Field field = (Field)obj;
            buffer.append(field.getName());
        }
        else if (obj instanceof EnumItem)
        {
            EnumItem item = (EnumItem)obj;
            String value = item.getName();
            buffer.append(value);
        }
        else
        {
            throw new InternalError("unhandled type of identifier: " +
                    obj.getClass().getName());
        }
    }

    private void emitCompoundPrefix()
    {
        if (compoundName != null)
        {
            buffer.append(compoundName);
            buffer.append(".");
        }
    } 
}
