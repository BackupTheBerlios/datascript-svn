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

import java.util.Iterator;
import java.util.Stack;

import datascript.parser.DSConstants;
import datascript.syntaxtree.AndOperand;
import datascript.syntaxtree.ArrayOperand;
import datascript.syntaxtree.CastOperand;
import datascript.syntaxtree.ChoiceOperand;
import datascript.syntaxtree.ConditionDefinition;
import datascript.syntaxtree.ConditionExpression;
import datascript.syntaxtree.ConditionalExpressionOperand;
import datascript.syntaxtree.DefinedType;
import datascript.syntaxtree.DotOperand;
import datascript.syntaxtree.EqualityOperand;
import datascript.syntaxtree.ExclusiveOrOperand;
import datascript.syntaxtree.FunctionArgument;
import datascript.syntaxtree.FunctionArgumentList;
import datascript.syntaxtree.InclusiveOrOperand;
//import datascript.syntaxtree.LengthOfOperand;
import datascript.syntaxtree.LogicalAndOperand;
import datascript.syntaxtree.LogicalOrOperand;
import datascript.syntaxtree.Multiplicand;
import datascript.syntaxtree.Node;
import datascript.syntaxtree.NodeToken;
import datascript.syntaxtree.ParenthesizedExpression;
import datascript.syntaxtree.QuantifiedExpression;
import datascript.syntaxtree.Quantifier;
import datascript.syntaxtree.RelationalOperand;
import datascript.syntaxtree.ShiftOperand;
import datascript.syntaxtree.SizeOfOperand;
import datascript.syntaxtree.Summand;
import datascript.syntaxtree.UnaryOperand;
import datascript.visitor.DepthFirstVisitor;

/**
 * ExpressionTypeCheckVisitor.
 * 
 * This class has a misleading name. It visits the AST tree of an expression.
 * During this visit, it is accompanied by an expression emitter. It will invoke
 * the expression emitter's method as appropriate.
 */
public class ExpressionTypeCheckVisitor extends DepthFirstVisitor
{

    private Stack typeStack;
    private Context scope;
    private Iterator parameters; // current list of parameter types
    private Stack condCallStack; // for nested condition invocations
    private CompoundType currentCompound;// compound in which this expression
    // occurs
    private ExpressionEmitter emitter; // language-specific emitter

    ExpressionTypeCheckVisitor(CompoundType currentCompound, Context scope)
    {
        this(currentCompound, scope, new ExpressionEmitter.EmptyEmitter());
    }

    ExpressionTypeCheckVisitor(CompoundType currentCompound, Context scope,
            ExpressionEmitter emitter)
    {
        this.scope = scope;
        this.currentCompound = currentCompound;
        this.emitter = emitter;
        this.typeStack = new Stack();
        this.condCallStack = new Stack();
    }

    TypeInterface resultType()
    {
        return (TypeInterface) typeStack.pop();
    }

    private static TypeInterface internIfIntegerType(TypeInterface type)
    {
        type = (TypeInterface) resolveTypeIfReference(type);
        if (type instanceof SetType)
        { // enum or bitmask
            type = ((SetType) type).getType();
        }

        if (type instanceof IntegerType)
        {
            return IntegerType.integerType;
        }
        else
        {
            return type;
        }
    }

    /**
     * hopefully, this method should not be needed anymore now that we resolve
     * all field types more aggressively
     */
    private static Object resolveTypeIfReference(Object obj)
    {
        if (obj instanceof TypeReference)
        {
            return TypeReference.resolveType((TypeReference) obj);
        }
        else
        {
            return obj;
        }
    }

    private void pushSymbol(Object obj, NodeToken where)
    {
        obj = resolveTypeIfReference(obj);

        if (obj instanceof CompoundType)
        {
            CompoundType ctype = (CompoundType) obj;

            /*
             * if (ctype == currentCompound) emitter.thisRef() ???
             */

            if (ctype != currentCompound && !currentCompound.isContained(ctype))
            {
                throw new SemanticError(where, currentCompound
                        + " cannot occur inside a " + ctype);
            }
            emitter.compoundReference(ctype);
            typeStack.push(ctype);
        }
        else if (obj instanceof Field)
        {
            Field field = (Field) obj;
            Main.warn("must check here whether " + field
                    + " is already known in " + currentCompound);
            emitter.fieldAccess(field);
            typeStack.push(internIfIntegerType(field.getType()));
        }
        else if (obj instanceof Condition)
        { // invocation
            emitter.openConditionCall((Condition) obj);
            typeStack.push(obj);
        }
        else if (obj instanceof Value)
        { // const symbol
            Value val = (Value) obj;
            emitter.value(val);
            typeStack.push(internIfIntegerType(val.getType()));
        }
        else if (obj instanceof Parameter)
        {
            Parameter param = (Parameter) obj;
            emitter.localParameter(param);
            typeStack.push(internIfIntegerType(param.getType()));
        }
        else
        {
            throw new LineError(where, "don't know how to treat object " + obj);
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
                Value val = Value.makeValue(n.tokenImage);
                emitter.value(val);
                typeStack.push(internIfIntegerType(val.getType()));
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
            case DSConstants.XOR:
            case DSConstants.AND:
            case DSConstants.OR:
            case DSConstants.LOGICALAND:
            case DSConstants.LOGICALOR:
            case DSConstants.QUESTIONMARK:
                typeStack.push(n);
            default:
                break;

            case DSConstants.IDENTIFIER:
                String varname = n.tokenImage;
                Object obj = scope.getSymbol(varname);
                if (obj == null)
                {
                    throw new SemanticError(n, varname + " not defined in: "
                            + scope);
                }
                pushSymbol(obj, n);
                break;
        }
    }

    /**
     * <PRE>
     * 
     * nodeOptional -> [ Quantifier() ] conditionalExpression ->
     * ConditionalExpression()
     * 
     * </PRE>
     */
    public void visit(QuantifiedExpression n)
    {
        if (n.nodeOptional.present())
        {
            // emit local parameter and array index
            n.nodeOptional.accept(this);
            TypeInterface atype = (TypeInterface) typeStack.pop();
            // should have been checked in Quantifier n
            if (!(atype instanceof ArrayType))
            {
                throw new InternalError(atype + " is not an array type");
            }
            emitter.forall(n.conditionalExpression, (ArrayType) atype);

            // the forall expression is not emitted now, but instead
            // it's up to the langspec emitter when it's emitted.
            ExpressionTypeCheckVisitor justCheck;
            justCheck = new ExpressionTypeCheckVisitor(currentCompound, scope);
            n.conditionalExpression.accept(justCheck);
            TypeInterface cond = justCheck.resultType();
            if (cond != BooleanType.booleanType)
            {
                throw new SemanticError(ClosestToken
                        .find(n.conditionalExpression),
                        " is not a boolean expression");
            }
            typeStack.push(cond);
        }
        else
        {
            n.conditionalExpression.accept(this);
        }
    }

    /**
     * <PRE>
     * 
     * nodeToken -> &lt;FORALL&gt; nodeToken1 -> &lt;IDENTIFIER&gt; nodeToken2 ->
     * &lt;IN&gt; unaryExpression -> UnaryExpression() nodeToken3 -> ":"
     * 
     * </PRE>
     */
    public void visit(Quantifier n)
    {
        // this gives
        ExpressionEvaluator ev;
        ev = new ExpressionEvaluator(currentCompound);
        n.unaryExpression.accept(ev);
        Object obj = ev.result();
        if (!(obj instanceof Field))
        {
            throw new LineError(ClosestToken.find(n), " must be field ");
        }
        Field field = (Field) obj;
        if (!(field.getType() instanceof ArrayType))
        {
            throw new SemanticError(ClosestToken.find(n), Main
                    .printNode(n.unaryExpression)
                    + " is not an array");
        }

        n.nodeToken1.accept(this);
        emitter.push();

        n.unaryExpression.accept(this);
        emitter.push();
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
        Object type = typeStack.pop();
        if (!(type instanceof BooleanType))
        {
            throw new SemanticError(n.nodeToken, "value is not of type boolean");
        }
        emitter.push();

        n.expression.accept(this);
        TypeInterface leftarm = (TypeInterface) typeStack.pop();
        emitter.push();

        n.conditionalExpression.accept(this);
        TypeInterface rightarm = (TypeInterface) typeStack.peek();
        if (leftarm != rightarm)
        {
            throw new SemanticError(n.nodeToken1, "`"
                    + Main.printNode(n.expression) + "' is of type " + leftarm
                    + " but `" + Main.printNode(n.conditionalExpression)
                    + "' is of type " + rightarm);
        }
        emitter.ternaryOperation(n.nodeToken, n.nodeToken1);
    }

    private void checkBooleanOperands(Node op, NodeToken n)
    {
        Object type = typeStack.pop();
        if (!(type instanceof BooleanType))
        {
            throw new SemanticError(n, "left operand is not of boolean type");
        }

        emitter.push();
        n.accept(this); // push and emit operator token
        NodeToken optoken = (NodeToken) typeStack.pop();
        op.accept(this);
        type = typeStack.peek();
        if (!(type instanceof BooleanType))
        {
            throw new SemanticError(n, "right operand is not of boolean type");
        }
        emitter.binaryOperation(optoken);
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
        checkBooleanOperands(n.logicalOrExpression, n.nodeToken);
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
        checkBooleanOperands(n.logicalAndExpression, n.nodeToken);
    }

    /**
     * op is the operand expression n is a NodeChoice that has the operator
     * token in it somewhere
     */
    private void checkIntegerOperands(Node op, Node n)
    {
        Object type = typeStack.pop();
        if (!(type instanceof IntegerType))
        {
            throw new SemanticError(ClosestToken.find(n),
                    "left operand is of type " + type
                            + " where integer was expected");
        }
        emitter.push();

        n.accept(this); // push and emit operator token
        NodeToken optoken = (NodeToken) typeStack.pop();
        op.accept(this);
        type = typeStack.peek();
        if (!(type instanceof IntegerType))
        {
            throw new SemanticError(ClosestToken.find(n),
                    "right operand is of type " + type
                            + " where integer was expected");
        }
        emitter.binaryOperation(optoken);
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
        checkIntegerOperands(n.inclusiveOrExpression, n.nodeToken);
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
        checkIntegerOperands(n.exclusiveOrExpression, n.nodeToken);
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
        checkIntegerOperands(n.andExpression, n.nodeToken);
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
        Object leftside = typeStack.pop();

        emitter.push();
        n.nodeChoice.accept(this); // push and emit operator token
        NodeToken optoken = (NodeToken) typeStack.pop();
        n.equalityExpression.accept(this);
        Object rightside = typeStack.pop();
        if (leftside != rightside)
        {
            throw new SemanticError(ClosestToken.find(n), "cannot compare `"
                    + leftside + "' to `" + rightside + "'");
        }
        typeStack.push(BooleanType.booleanType);
        emitter.binaryOperation(optoken);
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
        Object leftside = typeStack.pop();

        emitter.push();
        n.nodeChoice.accept(this); // push and emit operator token
        NodeToken optoken = (NodeToken) typeStack.pop();
        n.shiftExpression.accept(this);
        Object rightside = typeStack.pop();
        if (leftside != rightside
                || !(leftside instanceof StringType || leftside instanceof IntegerType))
        {
            throw new SemanticError(ClosestToken.find(n), "cannot compare `"
                    + leftside + "' with `" + rightside + "'");
        }
        typeStack.push(BooleanType.booleanType);
        emitter.binaryOperation(optoken);
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
        checkIntegerOperands(n.additiveExpression, n.nodeChoice);
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
        // should we allow string + too???
        checkIntegerOperands(n.multiplicativeExpression, n.nodeChoice);
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
        checkIntegerOperands(n.castExpression, n.nodeChoice);
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
        TypeInterface fromtype = (TypeInterface) typeStack.pop();

        TypeEvaluator v = new TypeEvaluator(currentCompound);
        n.definedType.accept(v, scope);
        Object totype = v.getType();

        // XXX insert check that fromtype can be cast to totype

        typeStack.push(totype);
    }

    /**
     * <PRE>
     * 
     * nodeToken -> "(" expression -> Expression() nodeToken1 -> ")"
     * 
     * </PRE>
     */
    public void visit(ParenthesizedExpression n)
    {
        n.expression.accept(this);
        emitter.parenthesize();
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
        TypeInterface fromtype = (TypeInterface) typeStack.pop();

        n.nodeChoice.accept(this);
        NodeToken optoken = (NodeToken) typeStack.pop();
        switch (optoken.kind)
        {
            case DSConstants.PLUS:
            case DSConstants.MINUS:
            case DSConstants.TILDE:
                if (fromtype != IntegerType.integerType)
                {
                    throw new SemanticError(
                            ClosestToken.find(n.castExpression),
                            "unary operator can only be applied to integer value");
                }
                typeStack.push(fromtype);
                break;

            case DSConstants.BANG:
                if (fromtype != BooleanType.booleanType)
                {
                    throw new SemanticError(
                            ClosestToken.find(n.castExpression),
                            "logical negation can only be applied to boolean value");
                }
                typeStack.push(fromtype);
                break;
            default:
                throw new InternalError("unknown unary operator");
        }
        emitter.unaryOperation(optoken);
    }

    /**
     * <PRE>
     * 
     * nodeChoice -> TypeName() | BuiltinType()
     * 
     * </PRE>
     */
    public void visit(DefinedType n)
    {
        TypeEvaluator v = new TypeEvaluator(currentCompound);
        n.accept(v, scope);
        typeStack.push(v.getType());
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
        /*
         * "UnaryExpression" could be a type or a variable
         */
        try
        {
            ExpressionEvaluator ev;
            ev = new ExpressionEvaluator(currentCompound);
            n.unaryExpression.accept(ev);

            Object obj = ev.result(); // if that evaluates to a type
            if (obj instanceof TypeInterface)
            {
                IntegerValue val = ((TypeInterface) obj).sizeof(scope);
                emitter.value(val);
            }
            else if (obj instanceof Field)
            { // if that evaluates to a field
                // then it's either a known-size field or not
                try
                {
                    IntegerValue val = ((Field) obj).sizeof(scope);
                    emitter.value(val);
                }
                catch (ComputeError ce1)
                {
                    n.unaryExpression.accept(this);
                    TypeInterface fromtype = (TypeInterface) typeStack.pop();
                    Field f = (Field) obj;

                    // XXX make sure this field has already been read here
                    emitter.sizeof();
                }
            }
            typeStack.push(IntegerType.integerType);
            return;
        }
        catch (ComputeError ce2)
        {
            throw new SemanticError(n.nodeToken, Main
                    .printNode(n.unaryExpression)
                    + " does not have a fixed size: " + ce2);
        }
    }

//    /**
//     * Check that a "lengthof foo" expression is valid. If foo is a type, it
//     * must be an array type. If foo is a field name, its type must be an array
//     * type.
//     * 
//     * <PRE>
//     * 
//     * nodeToken -> &lt;LENGTHOF&gt; unaryExpression -> UnaryExpression()
//     * 
//     * </PRE>
//     */
//    public void visit(LengthOfOperand n)
//    {
//        // First, check the expression following "lengthof"
//        ExpressionEvaluator ev;
//        ev = new ExpressionEvaluator(currentCompound);
//        n.unaryExpression.accept(ev);
//
//        Object obj = ev.result();
//        if (obj instanceof TypeInterface)
//        {
//            if (obj instanceof ArrayType)
//            {
//                IntegerValue val = ((ArrayType) obj).length;
//                emitter.value(val);
//            }
//            else
//            {
//                throw new SemanticError(n.nodeToken, Main
//                        .printNode(n.unaryExpression)
//                        + " is not an array type");
//            }
//        }
//        else if (obj instanceof Field)
//        {
//            TypeInterface type = ((Field) obj).getType();
//            if (type instanceof ArrayType)
//            {
//                n.unaryExpression.accept(this);
//                typeStack.pop();
//                emitter.lengthof();
//            }
//            else
//            {
//                throw new SemanticError(n.nodeToken, "type of "
//                        + Main.printNode(n.unaryExpression)
//                        + " is not an array type");
//            }
//        }
//
//        // The result of the lengthof expression has integer type
//        typeStack.push(IntegerType.integerType);
//    }

    /**
     * <PRE>
     * 
     * nodeToken -> "[" expression -> Expression() nodeToken1 -> "]"
     * 
     * </PRE>
     */
    public void visit(ArrayOperand n)
    {
        TypeInterface fromtype = (TypeInterface) typeStack.pop();
        if (!(fromtype instanceof ArrayType))
        {
            throw new SemanticError(n.nodeToken,
                    "cannot apply [] operator, because `" + fromtype
                            + "' is not an array");
        }
        emitter.push();
        n.expression.accept(this);
        TypeInterface idxtype = (TypeInterface) typeStack.pop();
        if (!(idxtype instanceof IntegerType))
        {
            throw new SemanticError(n.nodeToken,
                    "array index must be integer, but `" + idxtype
                            + "' was found");
        }
        TypeInterface etype;
        etype = (TypeInterface) resolveTypeIfReference(((ArrayType) fromtype)
                .getElementType());
        emitter.arrayAccess((ArrayType) fromtype, etype);
        typeStack.push(internIfIntegerType(etype));
    }

    /**
     * <PRE>
     * 
     * nodeToken -> "(" nodeOptional -> [ ArgumentExpressionList() ] nodeToken1 ->
     * ")"
     * 
     * </PRE>
     */
    public void visit(FunctionArgumentList n)
    {

        Object obj = typeStack.pop();
        if (!(obj instanceof Condition))
        {
            throw new SemanticError(n.nodeToken, "not a condition");
        }

        Condition cond = (Condition) obj;
        if (parameters != null)
        {
            condCallStack.push(parameters);
        }
        parameters = cond.getParameters();
        emitter.push();

        n.nodeOptional.accept(this);

        if (parameters.hasNext())
        {
            throw new SemanticError(n.nodeToken,
                    "too few arguments in condition invocation");
        }

        if (!condCallStack.isEmpty())
        {
            parameters = (Iterator) condCallStack.pop();
        }
        else
        {
            parameters = null;
        }
        emitter.closeConditionCall(cond, cond.getNumberOfParameters());
        typeStack.push(BooleanType.booleanType);
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
        if (!parameters.hasNext())
        {
            throw new SemanticError(ClosestToken.find(n),
                    "too many arguments in condition invocation");
        }

        n.assignmentExpression.accept(this);
        Object obj = typeStack.pop();

        Parameter p = (Parameter) parameters.next();
        TypeInterface ptype = p.getType();
        if (obj != internIfIntegerType(ptype))
        {
            throw new SemanticError(ClosestToken.find(n), "argument `"
                    + p.getName() + "' has type `" + obj + "' but type `"
                    + ptype + "' was expected");
        }
        emitter.castTo(/* src */(TypeInterface) obj, /* dst */ptype);
        emitter.push();
    }

    /**
     * <PRE>
     * 
     * nodeToken -> &lt;CONDITION&gt; nodeToken1 -> &lt;IDENTIFIER&gt;
     * nodeToken2 -> "(" nodeOptional -> [ ParameterDefinition() ( ","
     * ParameterDefinition() )* ] nodeToken3 -> ")" nodeToken4 -> "{"
     * nodeListOptional -> ( ConditionExpression() ";" )* nodeToken5 -> "}"
     * 
     * </PRE>
     */
    public void visit(ConditionDefinition n)
    {
        // n.nodeToken.accept(this);
        // n.nodeToken1.accept(this);
        // n.nodeToken2.accept(this);
        Condition cond = (Condition) scope.getSymbol(n.nodeToken1.tokenImage);
        ExpressionTypeCheckVisitor cchecker;
        cchecker = new ExpressionTypeCheckVisitor(currentCompound, cond
                .getScope());
        // n.nodeOptional.accept(cchecker);
        // n.nodeToken3.accept(this);
        // n.nodeToken4.accept(this);
        n.nodeListOptional.accept(cchecker);
        // n.nodeToken5.accept(this);
    }

    /**
     * <PRE>
     * 
     * expression -> Expression()
     * 
     * </PRE>
     */
    public void visit(ConditionExpression n)
    {
        n.expression.accept(this);
        TypeInterface etype = resultType(); // pops type
        if (etype != BooleanType.booleanType)
        {
            throw new SemanticError(ClosestToken.find(n),
                    "expression is of type " + etype
                            + " but boolean was expected");
        }
    }

    /**
     * <PRE>
     * 
     * nodeToken -> &lt;IS&gt; nodeToken1 -> &lt;IDENTIFIER&gt;
     * 
     * </PRE>
     */
    public void visit(ChoiceOperand n)
    {
        // n.nodeToken.accept(this);
        // n.nodeToken1.accept(this);
        Object obj = typeStack.pop();
        if (!(obj instanceof UnionType))
        {
            throw new SemanticError(n.nodeToken, "is operator requires union");
        }

        UnionType ctype = (UnionType) obj;
        String fieldname = n.nodeToken1.tokenImage;
        Iterator fields = ctype.getFields();
        boolean found = false;
        Field field = null;
        while (fields.hasNext())
        {
            field = (Field) fields.next();
            if (field.getName().equals(fieldname))
            {
                found = true;
                break;
            }
        }
        if (!found)
        {
            throw new SemanticError(n.nodeToken, fieldname
                    + " is not a member of union " + ctype);
        }
        emitter.choiceCheck(ctype, field);
        typeStack.push(BooleanType.booleanType);
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
        if (typeStack.isEmpty())
            throw new LineError(n.nodeToken, "where the f*#@! is my basetype?");

        Object obj = resolveTypeIfReference(typeStack.pop());

        Condition bCond = Condition.isBuiltin(fieldname);

        // this is too weak, must make sure that builtin condition
        // matches type
        if (obj instanceof TypeInterface && bCond != null)
        {
            emitter.openQualifiedConditionCall(bCond);
            typeStack.push(bCond);
            return;
        }

        if (!(obj instanceof CompoundType))
        {
            throw new SemanticError(n.nodeToken, obj
                    + ": not a struct or union");
        }

        CompoundType ctype = (CompoundType) obj;
        obj = ctype.getScope().getSymbol(fieldname);
        if (obj == null)
        {
            throw new SemanticError(n.nodeToken1, "`" + fieldname
                    + "' is not a member of " + ctype);
        }

        if (obj instanceof Condition)
        {
            emitter.openQualifiedConditionCall((Condition) obj);
            typeStack.push(obj);
        }
        else if (obj instanceof Field)
        {
            Field field = (Field) obj;
            emitter.qualifiedFieldAccess(field);
            typeStack.push(internIfIntegerType(field.getType()));
        }
        else if (obj instanceof CompoundType)
        {
            emitter.compoundReference((CompoundType) obj);
            typeStack.push(obj);
        }
        else if (obj instanceof Value)
        {
            Value val = (Value) obj;
            emitter.value(val);
            typeStack.push(internIfIntegerType(val.getType()));
        }
        else
        {
            throw new InternalError("don't know what to do with " + ctype + "."
                    + fieldname);
        }
    }

    static void checkExpressions(CompoundType ctype)
    {
        Iterator fields = ctype.getFields();
        while (fields.hasNext())
        {
            Field f = (Field) fields.next();
            Node fcond = f.getCondition();
            if (fcond != null)
            {
                ExpressionTypeCheckVisitor vis;
                fcond.accept(vis = new ExpressionTypeCheckVisitor(ctype, ctype
                        .getScope()));
                TypeInterface res = vis.resultType();
                if (res != BooleanType.booleanType)
                {
                    throw new SemanticError(ClosestToken.find(fcond),
                            "expression is of type " + res
                                    + " but boolean was expected");
                }
            }
            Node finit = f.getInitializer();
            if (finit != null)
            {
                ExpressionTypeCheckVisitor vis;
                finit.accept(vis = new ExpressionTypeCheckVisitor(ctype, ctype
                        .getScope()));
                TypeInterface res = vis.resultType();
                TypeInterface ftype = internIfIntegerType(f.getType());
                if (res != ftype)
                {
                    throw new SemanticError(ClosestToken.find(finit),
                            "expression is of type " + res + " but "
                                    + f.getType() + " was expected");
                }
            }
            Node fopt = f.getOptionalClause();
            if (fopt != null)
            {
                ExpressionTypeCheckVisitor vis;
                fopt.accept(vis = new ExpressionTypeCheckVisitor(ctype, ctype
                        .getScope()));
                TypeInterface res = vis.resultType();
                TypeInterface ftype = internIfIntegerType(f.getType());
                if (res != BooleanType.booleanType)
                {
                    throw new SemanticError(ClosestToken.find(fopt),
                            "expression is of type " + res
                                    + " but boolean was expected");
                }
            }
        }

        Iterator conds = ctype.getConditions();
        while (conds.hasNext())
        {
            Condition c = (Condition) conds.next();
            c.checkBody(ctype);
        }

        Iterator children = ctype.getNestedTypes();
        while (children.hasNext())
        {
            checkExpressions((CompoundType) children.next());
        }
    }
}
