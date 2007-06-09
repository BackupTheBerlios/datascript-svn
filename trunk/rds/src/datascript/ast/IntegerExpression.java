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
