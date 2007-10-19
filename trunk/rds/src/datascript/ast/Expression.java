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
import datascript.antlr.util.TokenAST;
import datascript.antlr.util.ToolContext;



/**
 * @author HWellmann
 *
 */
@SuppressWarnings("serial")
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


    public Scope getScope()
    {
        return scope;
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
        if (!(op.getExprType() instanceof BooleanType))
        {
            ToolContext.logError(op, "boolean type expected");
        }
    }


    public void checkIntegerOperand(Expression op)
    {
        if (!(op.getExprType() instanceof IntegerType))
        {
            ToolContext.logError(op, "integer type expected");
        }
    }


    public void evaluate(Scope scope)
    {
        //System.out.println("evaluating " + getText() + " in " + scope.getOwner());
        this.scope = scope;
        switch (getType())
        {

            case DataScriptParserTokenTypes.ID:
                evaluateIdentifier();
                break;

            case DataScriptParserTokenTypes.INTEGER_LITERAL:
                evaluateInteger();
                break;

            case DataScriptParserTokenTypes.LPAREN:
                type = op1().type;
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

            case DataScriptParserTokenTypes.INDEX:
                evaluateIndexExpression();
                break;

            case DataScriptParserTokenTypes.LT:
            case DataScriptParserTokenTypes.LE:
            case DataScriptParserTokenTypes.GT:
            case DataScriptParserTokenTypes.GE:
            case DataScriptParserTokenTypes.EQ:
            case DataScriptParserTokenTypes.NE:
                evaluateRelationalExpression();
                break;

            case DataScriptParserTokenTypes.PLUS:
            case DataScriptParserTokenTypes.MINUS:
            case DataScriptParserTokenTypes.MULTIPLY:
            case DataScriptParserTokenTypes.DIVIDE:
            case DataScriptParserTokenTypes.MODULO:
            case DataScriptParserTokenTypes.LSHIFT:
            case DataScriptParserTokenTypes.RSHIFT:
                evaluateArithmeticExpression();
                break;

            case DataScriptParserTokenTypes.LOGICALAND:
            case DataScriptParserTokenTypes.LOGICALOR:
                evaluateLogicalExpression();
                break;

            case DataScriptParserTokenTypes.LENGTHOF:
                evaluateLengthOfExpression();
                break;

            case DataScriptParserTokenTypes.SIZEOF:
                evaluateSizeOfExpression();
                break;

            case DataScriptParserTokenTypes.BITSIZEOF:
                evaluateSizeOfExpression();
                break;

            case DataScriptParserTokenTypes.SUM:
                evaluateSumFunction();
                break;

            case DataScriptParserTokenTypes.FUNCTIONCALL:
                evaluateFunctionCallExpression();
                break;

            case DataScriptParserTokenTypes.EXPLICIT:
                // do nothing
                break;

            default:
                throw new InternalError("illegal operation: type = "
                        + getType() + toStringTree() + ", " + getLine() + ":"
                        + getColumn());

        }
    }


    private void evaluateIdentifier()
    {
        String symbol = getText();
        Object obj = scope.getTypeOrSymbol(symbol);
        if (obj == null)
        {
            ToolContext.logError(this, "'" + symbol
                    + "' undefined in scope of '" + scope.getOwner().getName()
                    + "'");
        }
        if (obj instanceof Field)
        {
            Field field = (Field) obj;
            type = TypeReference.resolveType(field.getFieldType());
        }
        else if (obj instanceof Parameter)
        {
            Parameter param = (Parameter) obj;
            type = TypeReference.resolveType(param.getType());
        }
        else if (obj instanceof TypeReference)
        {
            TypeReference ref = (TypeReference) obj;
            type = TypeReference.resolveType(ref);
        }
        else if (obj instanceof EnumType)
        {
            type = (EnumType) obj;
        }
        else if (obj instanceof ConstType)
        {
            type = (ConstType) obj;
        }
        else if (obj instanceof IntegerType)
        {
            type = (IntegerType) obj;
        }
        else if (obj instanceof CompoundType)
        {
            CompoundType compound = (CompoundType) obj;
            CompoundType owner = (CompoundType) scope.getOwner();
            if (owner.isContainedIn(compound))
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
            EnumItem item = (EnumItem) obj;
            value = item.getValue();
            type = item.getEnumType();
        }
        else
        {
            ToolContext
                    .logError(this, "cannot resolve symbol '" + symbol + "'");
        }
    }


    private void evaluateInteger()
    {
        value = Value.makeValue(getText());
        type = value.getType();
    }


    private void evaluateMember()
    {
        Expression op1 = op1();
        Expression op2 = op2();
        TypeInterface t = op1.getExprType();
        String symbol = op2.getText();
        if (t instanceof CompoundType)
        {
            CompoundType compound = (CompoundType) t;
            evaluateCompoundMember(compound, symbol);
        }
        else if (t instanceof EnumType)
        {
            EnumType enumeration = (EnumType) t;
            evaluateEnumerationItem(enumeration, symbol);
        }
        else
        {
            ToolContext.logError(this, "compound or enumeration type expected");
        }
    }


    private void evaluateCompoundMember(CompoundType compound, String symbol)
    {
        Scope scope = compound.getScope();
        Object obj = scope.getSymbol(symbol);
        if (obj == null)
        {
            ToolContext.logError(this, "'" + symbol
                    + "' undefined in current scope");
        }
        op2().scope = scope;
        if (obj instanceof Field)
        {
            Field field = (Field) obj;
            type = TypeReference.resolveType(field.getFieldType());
        }
        else if (obj instanceof FunctionType)
        {
            type = (FunctionType) obj;
        }
        else if (obj instanceof CompoundType)
        {
            type = (CompoundType) obj;
        }
        else if (obj instanceof Parameter)
        {
            Parameter param = (Parameter) obj;
            type = TypeReference.resolveType(param.getType());
        }
        else 
        {
            throw new InternalError("cannot resolve symbol '" + symbol + "'");
        }
    }


    private void evaluateEnumerationItem(EnumType enumeration, String symbol)
    {
        Scope scope = enumeration.getScope();
        Object obj = scope.getSymbol(symbol);
        if (obj == null)
        {
            ToolContext.logError(this, "'" + symbol
                    + "' undefined in enumeration '" + enumeration.getName()
                    + "'");
        }
        op2().scope = scope;
        EnumItem item = (EnumItem) obj;
        value = item.getValue();
        type = item.getEnumType();
    }


    private void evaluateArrayElement()
    {
        TypeInterface t = op1().getExprType();
        if (!(t instanceof ArrayType))
        {
            ToolContext.logError(this, "array type expected");
        }
        ArrayType array = (ArrayType) t;
        if (!(op2().getExprType() instanceof IntegerType))
        {
            ToolContext.logError(op2(), "integer type expected");
        }
        type = array.getElementType();
    }


    public void checkInteger()
    {
        if (!(type instanceof IntegerType))
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
        if (!Expression.checkCompatibility(op2.type, op3.type))
        {
            ToolContext.logError(op2,
                    "types in conditional expression are incompatible");
        }
        if (op1().value == null)
        {
            type = op2.type;
        }
        else
        {
            Expression result = ((BooleanValue) op1.value).booleanValue() ? op2
                    : op3;
            value = result.value;
            type = result.type;
        }
    }


    public void evaluateArithmeticExpression()
    {
        Expression op1 = op1();
        Expression op2 = op2();
        if (!Expression.checkCompatibility(op1.type, op2.type))
        {
            ToolContext.logError(op1,
                    "types in arithmetic expression are incompatible");
        }

        // TODO: calculate value if operands are constant 
        value = null;
        type = IntegerType.integerType;
    }


    public void evaluateRelationalExpression()
    {
        Expression op1 = op1();
        Expression op2 = op2();
        if (!Expression.checkCompatibility(op1.type, op2.type))
        {
            ToolContext.logError(op1,
                    "types in relational expression are incompatible");
        }

        // TODO: calculate value if operands are constant 
        value = null;
        type = BooleanType.booleanType;
    }


    public void evaluateLogicalExpression()
    {
        Expression op1 = op1();
        Expression op2 = op2();
        checkBooleanOperand(op1);
        checkBooleanOperand(op2);
        type = BooleanType.booleanType;
        if (op1.value != null && op2.value != null)
        {
            boolean val1 = op1.value.booleanValue();
            boolean val2 = op1.value.booleanValue();
            switch (getType())
            {
                case DataScriptParserTokenTypes.LOGICALAND:
                    value = new BooleanValue(val1 && val2);
                    break;

                case DataScriptParserTokenTypes.LOGICALOR:
                    value = new BooleanValue(val1 || val2);
                    break;
            }
        }
    }


    public void evaluateIndexExpression()
    {
        type = IntegerType.integerType;
        Field field = scope.getCurrentField();
        if (field == null)
        {
            ToolContext
                    .logError(op1(),
                            "index expression can only be used in an array field context");
            return;
        }
        if (field.getFieldType() instanceof ArrayType)
        {
            if (!field.getName().equals(op1().getText()))
            {
                ToolContext.logError(op1(),
                        "index expression must refer to field '"
                                + field.getName() + "'");
            }
        }
        else
        {
            ToolContext.logError(op1(), "'" + field.getName()
                    + "' is not an array");
        }
    }


    private void evaluateLengthOfExpression()
    {
        type = IntegerType.integerType;
        value = null;
        if (!(op1().getExprType() instanceof ArrayType))
        {
            ToolContext.logError(op1(),
                    "lengthof operator requires array type argument");
        }
    }


    private void evaluateSizeOfExpression()
    {
        type = IntegerType.integerType;
        value = null;
    }


    private void evaluateSumFunction()
    {
        type = IntegerType.integerType;
        value = null;
        if (!(op1().getExprType() instanceof ArrayType))
        {
            ToolContext.logError(op1(),
                    "sum function requires array type argument");
        }
    }


    private void evaluateFunctionCallExpression()
    {
        if (op1().getExprType() instanceof FunctionType)
        {
            type = IntegerType.integerType;
            value = null;
        }
        else
        {
            ToolContext.logError(op1(), "cannot invoke a non-function member");
        }
    }


    /**
     * Checks compatibility of two types for assignment or argument passing
     * @param type1 left hand side type
     * @param type2 right hand side type
     * @return true if types are compatible
     */
    public static boolean checkCompatibility(TypeInterface type1,
            TypeInterface type2)
    {
        TypeInterface t1, t2;

        if (type1 instanceof ConstType)
            t1 = TypeReference.resolveType(((ConstType) type1).getBaseType());
        else t1 = type1;
        if (type2 instanceof ConstType)
            t2 = TypeReference.resolveType(((ConstType) type2).getBaseType());
        else t2 = type2;

        if (t1 instanceof IntegerType && (t2 instanceof IntegerType))
        {
            return true;
        }
        else if (t2 instanceof TypeInstantiation)
        {
            TypeInstantiation inst = (TypeInstantiation) t2;
            CompoundType base = inst.getBaseType();
            return t1.equals(base);
        }
        else
        {
            return t1.equals(t2);
        }
    }
}
