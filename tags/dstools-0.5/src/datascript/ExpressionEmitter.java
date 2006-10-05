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

import datascript.syntaxtree.NodeToken;
import datascript.syntaxtree.Node;

/**
 * Emit an expression for a java-like target.
 * 
 * This is done is a stack machine-like fashion, but not consistently so. Some
 * operations operate directly on a 'current expression", which can be thought
 * of as an accumulator that exists beside the stack. Others operate on the
 * stack and the current expression. The current expression must be pushed
 * explicitly onto the stack if desired.
 *  [ I don't quite remember why I did it this way, but it now seems
 * embarrassingly complicated yet was meant to be simple... It seems somewhat
 * error prone too. I could accidentally overwrite the current expression,
 * losing things. Why did I do it this way?
 * 
 * I think I hoped to exploit the similarity between Java expressions and
 * DataScript expressions. So an emitter for Java could just patch things
 * together. Instead of having to write a full visitor for the AST tree, I hoped
 * to only implement this emitter with fewer methods.
 * 
 * I believe I should redo this once I write emitters for other targets. Or just
 * not use it for them. ]
 * 
 * @see ExpressionTypeCheckVisitor
 */
public interface ExpressionEmitter
{
    /**
     * push (current expression)
     */
    public void push();

    /**
     * current expression := value
     */
    public void value(Value value);

    /**
     * current expression := pop() binaryop current expression
     */
    public void binaryOperation(NodeToken n);

    /**
     * current expression := unaryop current expression
     */
    public void unaryOperation(NodeToken n);

    /**
     * current expression := tertiary (pop(), pop(), current expression)
     */
    public void ternaryOperation(NodeToken n0, NodeToken n1);

    /**
     * current expression := pop() [ current expression ]
     */
    public void arrayAccess(ArrayType atype, TypeInterface etype);

    /**
     * current expression := sizeof(current expression)
     */
    public void sizeof();

    public void lengthof();

    /**
     * current expression := parameter
     */
    public void localParameter(Parameter param);

    /**
     * current expression := (current expression).isChoice
     */
    public void choiceCheck(UnionType utype, Field choice);

    /**
     * current expression := reference to compound (using callchain)
     */
    public void compoundReference(CompoundType ctype);

    /**
     * current expression := reference to field (within context)
     */
    public void fieldAccess(Field field);

    /**
     * current expression := current expression.reference to field (fully
     * qualified) (current expression is base)
     */
    public void qualifiedFieldAccess(Field field);

    /**
     * current expression := condition
     * 
     */
    public void openConditionCall(Condition cond);

    /**
     * current expression := current expression.condition
     */
    public void openQualifiedConditionCall(Condition cond);

    /**
     * current expression := pop() "(" (pop())* ")". name of condition and
     * arguments have already been pushed.
     */
    public void closeConditionCall(Condition cond, int nargs);

    /**
     * current expression := (cast) current expression
     */
    public void castTo(TypeInterface srctype, TypeInterface dsttype);

    /**
     * current expression := forallcall (pop():array, pop():parameter)
     */
    public void forall(Node expr, ArrayType atype);

    /**
     * current expression := "(" current expression ")"
     */
    public void parenthesize();

    public class EmptyEmitter implements ExpressionEmitter
    {
        public void push()
        {
        }

        public void value(Value value)
        {
        }

        public void unaryOperation(NodeToken n)
        {
        }

        public void binaryOperation(NodeToken n)
        {
        }

        public void ternaryOperation(NodeToken n0, NodeToken n1)
        {
        }

        public void arrayAccess(ArrayType atype, TypeInterface etype)
        {
        }

        public void sizeof()
        {
        }

        public void lengthof()
        {
        }

        public void localParameter(Parameter param)
        {
        }

        public void choiceCheck(UnionType utype, Field choice)
        {
        }

        public void compoundReference(CompoundType ctype)
        {
        }

        public void fieldAccess(Field field)
        {
        }

        public void qualifiedFieldAccess(Field field)
        {
        }

        public void openConditionCall(Condition cond)
        {
        }

        public void openQualifiedConditionCall(Condition cond)
        {
        }

        public void closeConditionCall(Condition cond, int nargs)
        {
        }

        public void castTo(TypeInterface srctype, TypeInterface dsttype)
        {
        }

        public void forall(Node expr, ArrayType atype)
        {
        }

        public void parenthesize()
        {
        }
    }
}
