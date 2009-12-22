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
package datascript;

import java.util.Stack;

import datascript.parser.DSConstants;
import datascript.syntaxtree.AndOperand;
import datascript.syntaxtree.ArrayOperand;
import datascript.syntaxtree.CastOperand;
import datascript.syntaxtree.ConditionalExpressionOperand;
import datascript.syntaxtree.DefinedType;
import datascript.syntaxtree.DotOperand;
import datascript.syntaxtree.EqualityOperand;
import datascript.syntaxtree.ExclusiveOrOperand;
import datascript.syntaxtree.FunctionArgument;
import datascript.syntaxtree.FunctionArgumentList;
import datascript.syntaxtree.InclusiveOrOperand;
import datascript.syntaxtree.LogicalAndOperand;
import datascript.syntaxtree.LogicalOrOperand;
import datascript.syntaxtree.Multiplicand;
import datascript.syntaxtree.NodeToken;
import datascript.syntaxtree.RelationalOperand;
import datascript.syntaxtree.ShiftOperand;
import datascript.syntaxtree.SizeOfOperand;
import datascript.syntaxtree.Summand;
import datascript.syntaxtree.UnaryOperand;
import datascript.syntaxtree.VariableName;
import datascript.visitor.DepthFirstVisitor;

public class ExpressionEvaluator extends DepthFirstVisitor
{

    private Stack evalStack;
    private Context scope;
    private CompoundType ctype;

    ExpressionEvaluator(CompoundType ctype)
    {
        this.ctype = ctype;
        this.scope = ctype.getScope();
        this.evalStack = new Stack();
    }

    Object result()
    {
        return evalStack.peek();
    }

    Value value()
    {
        Object res = null;
        try
        {
            return (Value) (res = evalStack.peek());
        }
        catch (ClassCastException e)
        {
            throw new ComputeError(res + " is not a value");
        }
    }

    public void visit(NodeToken n)
    {
        switch (n.kind)
        {
            case DSConstants.INTEGER_LITERAL:
            case DSConstants.STRING_LITERAL:
            case DSConstants.FLOATING_POINT_LITERAL:
            case DSConstants.CHARACTER_LITERAL:
                evalStack.push(Value.makeValue(n.tokenImage));
                break;

            case DSConstants.MINUS:
            case DSConstants.PLUS:
            case DSConstants.MULTIPLY:
            case DSConstants.DIVIDE:
            case DSConstants.MODULO:
            case DSConstants.SHIFTLEFT:
            case DSConstants.SHIFTRIGHT:
            case DSConstants.BANG:
            case DSConstants.TILDE:
            case DSConstants.EQ:
            case DSConstants.NE:
            case DSConstants.GT:
            case DSConstants.GE:
            case DSConstants.LT:
            case DSConstants.LE:
                evalStack.push(n);
                break;
            default:
                break;
        }
    }

    /**
     * <PRE>
     * 
     * nodeToken -> &lt;QUESTIONMARK&gt; expression -> Expression() nodeToken1 ->
     * ":" conditionalExpression -> ConditionalExpression()
     * 
     * </PRE>
     */
    public void visit(ConditionalExpressionOperand n)
    {
        if (value().booleanValue())
        {
            n.expression.accept(this);
        }
        else
        {
            n.conditionalExpression.accept(this);
        }
    }

    /**
     * <PRE>
     * 
     * nodeToken -> "||" logicalOrExpression -> LogicalOrExpression()
     * 
     * </PRE>
     */
    public void visit(LogicalOrOperand n)
    {
        if (value().booleanValue() == true)
        {
            evalStack.push(new BooleanValue(true));
        }
        else
        {
            n.logicalOrExpression.accept(this);
        }
    }

    /**
     * <PRE>
     * 
     * nodeToken -> "&&" logicalAndExpression -> LogicalAndExpression()
     * 
     * </PRE>
     */
    public void visit(LogicalAndOperand n)
    {
        if (value().booleanValue() == false)
        {
            evalStack.push(new BooleanValue(false));
        }
        else
        {
            n.logicalAndExpression.accept(this);
        }
    }

    /**
     * <PRE>
     * 
     * nodeToken -> "|" inclusiveOrExpression -> InclusiveOrExpression()
     * 
     * </PRE>
     */
    public void visit(InclusiveOrOperand n)
    {
        IntegerValue left = (IntegerValue) value();
        n.inclusiveOrExpression.accept(this);
        evalStack.push(left.or((IntegerValue) value()));
    }

    /**
     * <PRE>
     * 
     * nodeToken -> "^" exclusiveOrExpression -> ExclusiveOrExpression()
     * 
     * </PRE>
     */
    public void visit(ExclusiveOrOperand n)
    {
        IntegerValue left = (IntegerValue) value();
        n.exclusiveOrExpression.accept(this);
        evalStack.push(left.xor((IntegerValue) value()));
    }

    /**
     * <PRE>
     * 
     * nodeToken -> "&" andExpression -> AndExpression()
     * 
     * </PRE>
     */
    public void visit(AndOperand n)
    {
        IntegerValue left = (IntegerValue) value();
        n.andExpression.accept(this);
        evalStack.push(left.and((IntegerValue) value()));
    }

    /**
     * <PRE>
     * 
     * nodeChoice -> ( "==" | "!=" ) equalityExpression -> EqualityExpression()
     * 
     * </PRE>
     */
    public void visit(EqualityOperand n)
    {
        Comparable cval1 = (Comparable) value();
        n.equalityExpression.accept(this);
        boolean isEqual = cval1.compareTo(value()) == 0;
        n.nodeChoice.accept(this);

        Value result = null;
        switch (((NodeToken) evalStack.pop()).kind)
        {
            case DSConstants.EQ:
                result = new BooleanValue(isEqual);
                break;
            case DSConstants.NE:
                result = new BooleanValue(!isEqual);
                break;
            default:
                throw new InternalError("unknown equality comparison op");
        }
        evalStack.push(result);
    }

    /**
     * <PRE>
     * 
     * nodeChoice -> ( &lt;LT&gt; | &lt;LE&gt; | &lt;GT&gt; | &lt;GE&gt; )
     * shiftExpression -> ShiftExpression()
     * 
     * </PRE>
     */
    public void visit(RelationalOperand n)
    {
        Comparable cval1 = (Comparable) value();
        n.shiftExpression.accept(this);
        int cmpResult = cval1.compareTo(value());
        Value result = null;

        n.nodeChoice.accept(this);
        switch (((NodeToken) evalStack.pop()).kind)
        {
            case DSConstants.LT:
                result = new BooleanValue(cmpResult < 0);
                break;
            case DSConstants.GT:
                result = new BooleanValue(cmpResult > 0);
                break;
            case DSConstants.LE:
                result = new BooleanValue(cmpResult <= 0);
                break;
            case DSConstants.GE:
                result = new BooleanValue(cmpResult >= 0);
                break;
            default:
                throw new InternalError("unknown relational comparison op");
        }
        evalStack.push(result);
    }

    /**
     * <PRE>
     * 
     * nodeChoice -> ( &lt;SHIFTLEFT&gt; | &lt;SHIFTRIGHT&gt; )
     * additiveExpression -> AdditiveExpression()
     * 
     * </PRE>
     */
    public void visit(ShiftOperand n)
    {
        IntegerValue left = (IntegerValue) value();
        n.additiveExpression.accept(this);

        n.nodeChoice.accept(this);
        switch (((NodeToken) evalStack.pop()).kind)
        {
            case DSConstants.SHIFTLEFT:
                evalStack.push(left.shiftLeft((IntegerValue) value()));
                break;
            case DSConstants.SHIFTRIGHT:
                evalStack.push(left.shiftRight((IntegerValue) value()));
                break;
            default:
                throw new InternalError("unknown shift operator");
        }
    }

    /**
     * <PRE>
     * 
     * nodeChoice -> ( &lt;PLUS&gt; | &lt;MINUS&gt; ) multiplicativeExpression ->
     * MultiplicativeExpression()
     * 
     * </PRE>
     */
    public void visit(Summand n)
    {
        IntegerValue left = (IntegerValue) value();
        n.multiplicativeExpression.accept(this);

        n.nodeChoice.accept(this);
        switch (((NodeToken) evalStack.pop()).kind)
        {
            case DSConstants.PLUS:
                evalStack.push(left.add((IntegerValue) value()));
                break;
            case DSConstants.MINUS:
                evalStack.push(left.subtract((IntegerValue) value()));
                break;
            default:
                throw new InternalError("unknown addition/subtraction operator");
        }
    }

    /**
     * <PRE>
     * 
     * nodeChoice -> ( &lt;MULTIPLY&gt; | &lt;DIVIDE&gt; | &lt;MODULO&gt; )
     * castExpression -> CastExpression()
     * 
     * </PRE>
     */
    public void visit(Multiplicand n)
    {
        IntegerValue left = (IntegerValue) value();
        n.castExpression.accept(this);

        n.nodeChoice.accept(this);
        switch (((NodeToken) evalStack.pop()).kind)
        {
            case DSConstants.MULTIPLY:
                evalStack.push(left.multiply((IntegerValue) value()));
                break;
            case DSConstants.DIVIDE:
                evalStack.push(left.divide((IntegerValue) value()));
                break;
            case DSConstants.MODULO:
                evalStack.push(left.remainder((IntegerValue) value()));
                break;
            default:
                throw new InternalError("unknown multiplication operator");
        }
    }

    /**
     * <PRE>
     * 
     * nodeToken -> "(" typeName -> DefinedType() nodeToken1 -> ")"
     * castExpression -> CastExpression()
     * 
     * </PRE>
     */
    public void visit(CastOperand n)
    {
        n.castExpression.accept(this);
        Value nval, cval = value();

        TypeEvaluator v = new TypeEvaluator(ctype);
        n.definedType.accept(v, scope);
        nval = v.getType().castFrom(cval);

        evalStack.push(nval);
    }

    /**
     * <PRE>
     * 
     * nodeChoice -> ( "+" | "-" | "~" | "!" ) castExpression ->
     * CastExpression()
     * 
     * </PRE>
     */
    public void visit(UnaryOperand n)
    {
        n.castExpression.accept(this);
        Value result = null, val = value();

        n.nodeChoice.accept(this);
        switch (((NodeToken) evalStack.pop()).kind)
        {
            case DSConstants.PLUS:
                result = (IntegerValue) val;
                break;
            case DSConstants.MINUS:
                result = ((IntegerValue) val).negate();
                break;
            case DSConstants.TILDE:
                result = ((IntegerValue) val).not();
                break;
            case DSConstants.BANG:
                result = new BooleanValue(!val.booleanValue());
                break;
            default:
                throw new InternalError("unknown unary operator");
        }
        evalStack.push(result);
    }

    public void visit(DefinedType n)
    {
        TypeEvaluator v = new TypeEvaluator(ctype);
        n.accept(v, scope);
        evalStack.push(v.getType());
    }

    /**
     * <PRE>
     * 
     * nodeToken -> &lt;SIZEOF&gt; unaryExpression -> UnaryExpression()
     * 
     * </PRE>
     */
    public void visit(SizeOfOperand n)
    {
        n.unaryExpression.accept(this);
        Object obj = evalStack.pop();
        if (obj instanceof TypeInterface)
        {
            evalStack.push(((TypeInterface) obj).sizeof(scope));
            return;
        }
        else if (obj instanceof Field)
        {
            evalStack.push(((Field) obj).sizeof(scope));
            return;
        }
        else
            throw new ComputeError("cannot determine sizeof " + obj + " of "
                    + obj.getClass());
    }

    /**
     * <PRE>
     * 
     * nodeToken -> "[" expression -> Expression() nodeToken1 -> "]"
     * 
     * </PRE>
     */
    public void visit(ArrayOperand n)
    {
        // what must be on stack now must be an array variable
        n.expression.accept(this);
        IntegerValue idx = (IntegerValue) value();
        // now get variable from stack, perform array index access and push
        // result
        // XXX
    }

    public void visit(FunctionArgumentList n)
    {
        // n.nodeOptional.accept(callee);
        throw new ComputeError("do not implement condition calling");
    }

    /**
     * <PRE>
     * 
     * nodeToken -> "." nodeToken1 -> &lt;IDENTIFIER&gt;
     * 
     * </PRE>
     */
    public void visit(DotOperand n)
    {
        String fieldname = n.nodeToken1.tokenImage;
        // what must be on stack now is a struct
        // check that it has field 'fieldname' and that it's known in scope
        Object obj = evalStack.pop();
        CompoundType compound = null;

        if (obj instanceof Field)
        {
            Field field = (Field) obj;
            compound = (CompoundType) TypeReference
                    .resolveType(field.getType());
        }
        else if (obj instanceof Parameter)
        {
            Parameter param = (Parameter) obj;
            TypeInterface type = TypeReference.resolveType(param.getType());
            compound = (CompoundType) type;
        }
        else if (obj instanceof CompoundType)
        {
            compound = (CompoundType) obj;
        }
        else
        {
            throw new RuntimeException(
                    "lhs of dot operator has unknown semantic category "
                            + obj.getClass().getName());
        }

        obj = compound.getScope().getSymbol(fieldname);
        if (obj == null)
        {
            throw new SemanticError(n.nodeToken1, "`" + fieldname
                    + "' is not a member of " + compound);
        }

        if (obj instanceof Condition)
        {
            // emitter.openQualifiedConditionCall((Condition)obj);
            evalStack.push(obj);
        }
        else if (obj instanceof Field)
        {
            Field f = (Field) obj;
            // emitter.qualifiedFieldAccess(field);
            evalStack.push(f);
        }
        else if (obj instanceof Parameter)
        {
            TypeInterface t = TypeReference.resolveType(((Parameter) obj)
                    .getType());
            // emitter.qualifiedFieldAccess(field);
            evalStack.push(t);
        }
        else if (obj instanceof CompoundType)
        {
            // emitter.compoundReference((CompoundType)obj);
            evalStack.push(obj);
        }
        else if (obj instanceof Value)
        {
            Value val = (Value) obj;
            // emitter.value(val);
            evalStack.push(val.getType());
        }
        else
        {
            throw new RuntimeException(
                    "lhs of dot operator has unknown semantic category "
                            + obj.getClass().getName());
            // throw new InternalError("don't know what to do with " +
            // compound.getName() + "."
            // + fieldname);
        }
    }

    /**
     * <PRE>
     * 
     * nodeToken -> &lt;IDENTIFIER&gt;
     * 
     * </PRE>
     */
    public void visit(VariableName n)
    {
        String varname = n.nodeToken.tokenImage;
        Object o = scope.getSymbol(varname);
        if (o == null)
        {
            throw new ComputeError("variable " + varname + " not known");
        }
        evalStack.push(o); // !?!?!
    }

    /**
     * <PRE>
     * 
     * assignmentExpression -> AssignmentExpression()
     * 
     * </PRE>
     */
    public void visit(FunctionArgument n)
    {
        n.assignmentExpression.accept(this);
        value();
    }
}
