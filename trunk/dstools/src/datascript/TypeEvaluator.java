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

import datascript.syntaxtree.*;
import datascript.visitor.*;
import java.util.*;
import java.math.BigInteger;
import datascript.parser.DSConstants;

public class TypeEvaluator extends ObjectDepthFirst
{

    TypeEvaluator(CompoundType c)
    {
        this.currentCompound = c;
    }

    // these are internal variable that must remain set between visits
    private Node fieldLabel = null;
    private Node fieldGlobalLabel = null;
    private Node fieldCondition;
    private Node fieldOptionalClause;
    private Node fieldInitializer;

    private boolean isBitMask;
    private int byteOrder;
    private BigInteger defaultValue;
    private SetType currentEnum;
    private Node lowerBound, upperBound;

    TypeInterface getType()
    {
        return lastType;
    }

    // these are internal variables that must be stored and restored
    private Condition currentCondition;
    private CompoundType currentCompound;
    private TypeInterface lastType;
    private Vector lastTypeArguments;
    private Stack nestedCompounds = new Stack();
    private Stack typeStack = new Stack();

    /**
     * <PRE>
     * 
     * nodeOptional -> [ GlobalLabel() ] expression -> Expression() nodeToken ->
     * ":"
     * 
     * </PRE>
     */
    public Object visit(Label n, Object argu)
    {
        n.nodeOptional.accept(this, argu);
        fieldLabel = n.expression;
        return null;
    }

    /**
     * <PRE>
     * 
     * expression -> Expression() nodeToken -> "::"
     * 
     * </PRE>
     */
    public Object visit(GlobalLabel n, Object argu)
    {
        fieldGlobalLabel = n.expression;
        return null;
    }

    /**
     * <PRE>
     * 
     * nodeToken -> "." nodeToken1 -> &lt;IDENTIFIER&gt;
     * 
     * </PRE>
     */
    public Object visit(DotOperand n, Object argu)
    {
        if (qualifiedName != null)
        {
            qualifiedName.addElement(n.nodeToken1);
        }
        return null;
    }

    private Vector qualifiedName;

    public Object visit(TypeSymbol n, Object argu)
    {
        qualifiedName = new Vector();
        qualifiedName.addElement(n.nodeToken);
        n.nodeListOptional.accept(this, argu);
        lastType = new TypeReference((Context) argu, qualifiedName);
        qualifiedName = null;
        return null;
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
    public Object visit(ConditionDefinition n, Object argu)
    {
        Scope s = (Scope) argu;
        String name = n.nodeToken1.tokenImage;

        Scope ns = new Scope(s); // open new scope to (maybe later) allow
        // let-style bindings in expressions
        parameters = new Vector();
        expressions = new Vector();
        currentCondition = new Condition(name, parameters, expressions, n);
        s.setSymbol(name, currentCondition);
        n.nodeOptional.accept(this, ns);

        n.nodeListOptional.accept(this, ns);
        currentCondition.setScope(ns);
        currentCompound.addCondition(currentCondition);
        return null;
    }

    private Vector parameters;
    private Vector expressions;

    /**
     * <PRE>
     * 
     * typeDeclaration -> TypeDeclaration() nodeToken -> &lt;IDENTIFIER&gt;
     * 
     * </PRE>
     */
    public Object visit(ParameterDefinition n, Object argu)
    {
        Scope s = (Scope) argu;
        String name = n.nodeToken.tokenImage;
        s.setSymbol(name, n); // insert this a place holder

        if (lastType != null)
        {
            typeStack.push(lastType);
        }
        lastType = null;
        n.typeDeclaration.accept(this, argu);
        Parameter p = new Parameter(name, lastType);
        Main.debug("parameter " + p + " is " + lastType);
        parameters.addElement(p);
        s.replaceSymbol(name, p);

        if (!typeStack.isEmpty())
        {
            lastType = (TypeInterface) typeStack.pop();
        }
        return null;
    }

    /**
     * <PRE>
     * 
     * expression -> Expression()
     * 
     * </PRE>
     */
    public Object visit(ConditionExpression n, Object argu)
    {
        n.expression.accept(this, argu);
        expressions.addElement(n.expression);
        return null;
    }

    /**
     * <PRE>
     * 
     * nodeChoice -> ( &lt;ENUM&gt; | &lt;BITMASK&gt; ) builtinType ->
     * BuiltinType() nodeOptional -> [ &lt;IDENTIFIER&gt; ] nodeToken -> "{"
     * enumItem -> EnumItem() nodeListOptional -> ( "," EnumItem() )* nodeToken1 ->
     * "}"
     * 
     * </PRE>
     */
    public Object visit(EnumDeclaration n, Object argu)
    {
        Scope s = (Scope) argu;
        String name = getId(n.nodeOptional);
        isBitMask = n.nodeChoice.which == 1;

        n.builtinType.accept(this, argu);

        defaultValue = BigInteger.ZERO;
        if (!(lastType instanceof BuiltinType))
        {
            throw new SemanticError(n.nodeToken, lastType
                    + " is not a builtin type");
        }
        if (isBitMask)
        {
            currentEnum = new BitmaskType(name, lastType);
        }
        else
        {
            currentEnum = new EnumType(name, lastType);
        }

        if (name != null)
        {
            s.setSymbol(name, currentEnum);
        }

        currentCompound.addSetType(currentEnum);
        currentEnum.setCompound(currentCompound);

        // the items in an enumeration share the same scope
        n.enumItem.accept(this, argu);
        n.nodeListOptional.accept(this, argu);
        lastType = currentEnum;
        currentEnum = null;
        return null;
    }

    /**
     * <PRE>
     * 
     * nodeToken -> &lt;FORALL&gt; nodeToken1 -> &lt;IDENTIFIER&gt; nodeToken2 ->
     * &lt;IN&gt; unaryExpression -> UnaryExpression() nodeToken3 -> ":"
     * 
     * </PRE>
     */
    public Object visit(Quantifier n, Object argu)
    {
        Scope s = (Scope) argu;
        String name = n.nodeToken1.tokenImage;
        s.setSymbol(name, new Parameter(name, IntegerType.integerType));

        n.unaryExpression.accept(this, argu);
        return null;
    }

    /**
     * <PRE>
     * 
     * nodeToken -> &lt;IDENTIFIER&gt; nodeOptional -> [ "="
     * ConstantExpression() ]
     * 
     * </PRE>
     */
    public Object visit(EnumItem n, Object argu)
    {
        Scope s = (Scope) argu;
        String name = n.nodeToken.tokenImage;
        boolean hasDefinition = n.nodeOptional.present();

        // if it's a enum (not a bitmask) and does not have a definition
        // we add a default definition
        if (!hasDefinition && !isBitMask)
        {
            s.setSymbol(name, new IntegerValue(defaultValue));
        }

        Value lastValue;
        if (hasDefinition)
        {
            try
            {
                ExpressionEvaluator evalVisitor = new ExpressionEvaluator(
                        currentCompound);
                n.nodeOptional.accept(evalVisitor);
                lastValue = evalVisitor.value();
                s.setSymbol(name, lastValue);
            }
            catch (ComputeError e)
            {
                String sw = Main.printNode(((NodeSequence) n.nodeOptional.node)
                        .elementAt(1));
                if (Main.debug)
                {
                    e.printStackTrace(System.err);
                }
                throw new SemanticError("cannot compute constant " + name
                        + " as `" + sw + "' " + e);
            }
        }
        else
        {
            // checking this here in this first pass means that an enum value
            // must be computed from stuff further up in a
            // compilation unit
            lastValue = (Value) s.getSymbol(name);
            if (isBitMask && lastValue == null)
            {
                throw new SemanticError(n.nodeToken, name + " not defined");
            }
            if (!(lastValue instanceof IntegerValue))
            {
                throw new SemanticError(n.nodeToken, name
                        + " is not an integer value");
            }
            Main.debug("retrieved " + name + "=" + lastValue);
        }
        currentEnum.addItem(name, lastValue);

        try
        {
            defaultValue = lastValue.integerValue().add(BigInteger.ONE);
        }
        catch (ComputeError e)
        {
            throw new SemanticError(e.toString());
        }
        return null;
    }

    /**
     * <PRE>
     * 
     * nodeToken -> &lt;CONST&gt; typeDeclaration -> TypeDeclaration()
     * nodeToken1 -> &lt;IDENTIFIER&gt; nodeToken2 -> "=" typeValue ->
     * TypeValue()
     * 
     * </PRE>
     */
    public Object visit(ConstDeclaration n, Object argu)
    {
        Scope s = (Scope) argu;
        String name = n.nodeToken1.tokenImage;

        lastType = null;
        n.typeDeclaration.accept(this, argu);

        Value lastValue;
        try
        {
            if (lastType instanceof BuiltinType)
            {
                ExpressionEvaluator evalVisitor = new ExpressionEvaluator(
                        currentCompound);
                n.typeValue.accept(evalVisitor);
                lastValue = evalVisitor.value();
                if (!lastType.isMember(s, lastValue))
                {
                    throw new SemanticError(n.nodeToken2, lastValue
                            + " is not of type " + lastType);
                }
                s.setSymbol(name, lastValue);
            }
        }
        catch (ComputeError e)
        {
            throw new SemanticError("cannot compute constant `"
                    + Main.printNode(n.typeValue) + "': " + e.getMessage());
        }
        return null;
    }

    /** get a identifier string from [ <IDENTIFIER> ] */
    private String getId(NodeOptional n)
    {
        return n.present() ? ((NodeToken) n.node).tokenImage : null;
    }

    /**
     * Returns the documentation (javadoc style comment) for the current node.
     * Comment delimiters and whitespace (including one asterisk) on
     * continuation lines are stripped. The stripping is already done by the
     * tokenizer.
     * 
     * @param n
     *            syntax tree node for type name
     * @return documentation
     */
    private String getDoc(NodeOptional n)
    {
        String result = null;
        if (n.present())
        {
            StringBuffer buffer = new StringBuffer();
            NodeToken token = (NodeToken) n.node;
            if (token.numSpecials() > 0)
            {
                result = token.getSpecialAt(0).tokenImage;
                for (int i = 0; i < token.numSpecials(); i++)
                {
                    buffer.append(token.getSpecialAt(i));
                }
                result = buffer.toString();
            }
        }
        return result;
    }

    /**
     * <PRE>
     * 
     * nodeToken -> "=" typeValue -> TypeValue()
     * 
     * </PRE>
     */
    public Object visit(FieldInitializer n, Object argu)
    {
        n.nodeToken.accept(this, argu);
        n.typeValue.accept(this, argu);
        fieldInitializer = n.typeValue;
        return null;
    }

    /**
     * <PRE>
     * 
     * nodeToken -> ":" expression -> Expression()
     * 
     * </PRE>
     */
    public Object visit(FieldCondition n, Object argu)
    {
        n.nodeToken.accept(this, argu);
        n.expression.accept(this, argu);
        fieldCondition = n.expression;
        return null;
    }

    /**
     * <PRE>
     * 
     * nodeToken -> "if" expression -> Expression()
     * 
     * </PRE>
     */
    public Object visit(FieldOptionalClause n, Object argu)
    {
        n.nodeToken.accept(this, argu);
        n.expression.accept(this, argu);
        fieldOptionalClause = n.expression;
        return null;
    }

    /**
     * <PRE>
     * 
     * assignmentExpression -> AssignmentExpression()
     * 
     * </PRE>
     */
    public Object visit(FunctionArgument n, Object argu)
    {
        lastTypeArguments.addElement(n.assignmentExpression);
        return null;
    }

    /**
     * <PRE>
     * 
     * nodeToken -> "(" nodeOptional -> [ FunctionArgument() ( ","
     * FunctionArgument() )* ] nodeToken1 -> ")"
     * 
     * </PRE>
     */
    public Object visit(TypeArgumentList n, Object argu)
    {
        lastTypeArguments = new Vector();
        // we must save/restore lastType because there might be
        // for instance TypeSymbols in the expressions in the ArgumentList
        if (lastType != null)
        {
            typeStack.push(lastType);
        }
        n.nodeOptional.accept(this, argu);
        if (!typeStack.isEmpty())
        {
            lastType = (TypeInterface) typeStack.pop();
        }
        lastType = new TypeInstantiation(lastType, lastTypeArguments);
        return null;
    }

    /**
     * <PRE>
     * 
     * nodeToken -> "(" nodeOptional -> [ FunctionArgument() ( ","
     * FunctionArgument() )* ] nodeToken1 -> ")"
     * 
     * </PRE>
     */
    public Object visit(FunctionArgumentList n, Object argu)
    {
        n.nodeToken.accept(this, argu);
        lastTypeArguments = new Vector();
        n.nodeOptional.accept(this, argu);
        n.nodeToken1.accept(this, argu);
        return null;
    }

    /**
     * <PRE>
     * 
     * nodeOptional -> [ Label() ] typeDeclaration -> TypeDeclaration()
     * nodeOptional1 -> [ ArgumentList() ] nodeOptional2 -> [ &lt;IDENTIFIER&gt; ]
     * nodeListOptional -> ( ArrayRange() )* nodeOptional3 -> [
     * FieldInitializer() ] nodeOptional4 -> [ FieldOptionalClause() ]
     * nodeOptional5 -> [ FieldCondition() ]
     * 
     * </PRE>
     */
    public Object visit(FieldDefinition n, Object argu)
    {
        Scope s = (Scope) argu;
        String name = getId(n.nodeOptional2);
        TypeInterface fieldType;

        // get label
        fieldLabel = null;
        fieldGlobalLabel = null;
        n.nodeOptional.accept(this, argu);
        Node thisLabel = fieldLabel;
        Node thisGlobalLabel = fieldGlobalLabel;
        if (thisGlobalLabel != null && thisLabel == null)
        {
            throw new SemanticError(ClosestToken.find(n.nodeOptional),
                    "global label cannot stand alone");
        }

        n.typeDeclaration.accept(this, argu);
        n.nodeOptional1.accept(this, argu);
        n.nodeListOptional.accept(this, argu);
        fieldType = lastType;

        fieldCondition = fieldOptionalClause = fieldInitializer = null;
        n.nodeOptional3.accept(this, argu);
        n.nodeOptional4.accept(this, argu);
        n.nodeOptional5.accept(this, argu);

        Field f = new Field(fieldType, name, thisLabel, thisGlobalLabel);
        if (fieldCondition != null)
        {
            f.setCondition(fieldCondition);
        }
        if (fieldInitializer != null)
        {
            f.setInitializer(fieldInitializer);
        }

        if (fieldOptionalClause != null)
        {
            if (currentCompound instanceof UnionType)
            {
                throw new SemanticError(ClosestToken.find(n.nodeOptional4),
                        "if clause may not be used within union");
            }
            f.setOptionalClause(fieldOptionalClause);
        }

        if (name != null)
        {
            s.setSymbol(name, f);
        }

        if (fieldType instanceof CompoundType
                && ((CompoundType) fieldType).isAnonymous() && name == null)
        {
            throw new SemanticError(ClosestToken.find(n.typeDeclaration),
                    "an anonymous type must be named");
        }

        /*
         * this is slightly tricky: when is a type declaration also a field
         * definition? I say: if it is named, it defines a field . If it's not
         * named, then it only defines an (anonymous) field if it refers to an
         * existing type. If it's defining a new type and is not named, it's not
         * a field
         */
        if (name != null
                || !(fieldType instanceof CompoundType || fieldType instanceof SetType))
        {

            currentCompound.addField(f);
            f.setCompound(currentCompound);

            Main.debug("adding " + f + " to " + currentCompound);

            /* now compute "contained-in" relationship */
            while (fieldType instanceof ArrayType)
            {
                fieldType = ((ArrayType) fieldType).getElementType();
            }

            /*
             * XXX fix this: we should probably resolve both TypeInstantiations
             * and TypeReferences here - or maybe even unify TypeInstantiations
             * and TypeReferences? Maybe not, since TypeReferences are removed
             * during link phase
             */
            while (fieldType instanceof TypeReference
                    && ((TypeReference) fieldType).referent != null)
            {
                fieldType = ((TypeReference) fieldType).referent;
            }

            if (fieldType instanceof TypeInstantiation)
            {
                TypeInstantiation ti = (TypeInstantiation) fieldType;
                fieldType = ti.baseType;
            }

            if (fieldType instanceof CompoundType)
            {
                CompoundType fieldCompound = (CompoundType) fieldType;
                fieldCompound.addContainer(currentCompound);
            }
            else if (fieldType instanceof TypeReference)
            {
                ((TypeReference) fieldType).addContainer(currentCompound);
            }
        }
        return null;
    }

    /**
     * <PRE>
     * 
     * nodeChoice -> &lt;BIG&gt; | &lt;LITTLE&gt;
     * 
     * </PRE>
     */
    public Object visit(ByteOrderModifier n, Object argu)
    {
        byteOrder = (n.nodeChoice.which == 0) ? TypeInterface.BIG_ENDIAN
                : TypeInterface.LITTLE_ENDIAN;
        return null;
    }

    /**
     * <PRE>
     * 
     * nodeOptional -> [ ByteOrderModifier() ] nodeOptional1 -> [ &lt;UNION&gt; ]
     * nodeOptional2 -> [ &lt;IDENTIFIER&gt; ] nodeOptional3 -> [ "("
     * ParameterDefinition() ( "," ParameterDefinition() )* ")" ] nodeToken ->
     * "{" declarationList -> DeclarationList() nodeToken1 -> "}"
     * 
     * </PRE>
     */
    public Object visit(StructDeclaration n, Object argu)
    {
        Scope s = (Scope) argu;
        byteOrder = TypeInterface.NO_BYTE_ORDER;
        n.nodeOptional.accept(this, argu);

        boolean isUnion = n.nodeOptional1.present();
        String name = getId(n.nodeOptional2);
        String doc = getDoc(n.nodeOptional2);
        CompoundType ctype;
        if (isUnion)
        {
            ctype = new UnionType(name, doc, currentCompound);
        }
        else
        {
            ctype = new StructType(name, doc, currentCompound);
        }
        // inherit byte order of parent unless specified
        ctype.setByteOrder(byteOrder != TypeInterface.NO_BYTE_ORDER ? byteOrder
                : currentCompound.getByteOrder());

        if (name != null)
        {
            s.setSymbol(name, ctype);
        }

        Scope ns = new Scope(s);
        ns.setSymbol("this", new Parameter("this", ctype)); // !?

        Main.assertThat(currentCompound != null);
        nestedCompounds.push(currentCompound);
        currentCompound = ctype;
        ctype.setScope(ns);

        /* parse parameters for this compound if any */
        if (n.nodeOptional3.present())
        {
            parameters = new Vector();
            currentCompound.addParameters(parameters);
            n.nodeOptional3.accept(this, ns);
        }

        n.declarationList.accept(this, ns);
        lastType = ctype;
        Main.assertThat(!nestedCompounds.isEmpty());
        currentCompound = (CompoundType) nestedCompounds.pop();
        currentCompound.addNestedType(ctype);
        return null;
    }

    /**
     * <PRE>
     * 
     * expression -> Expression() nodeOptional -> [ UpperBoundExpression() ]
     * 
     * </PRE>
     */
    public Object visit(RangeExpression n, Object argu)
    {
        upperBound = n.expression;
        n.nodeOptional.accept(this, argu);
        return null;
    }

    /**
     * <PRE>
     * 
     * nodeToken -> &lt;RANGE&gt; expression -> Expression()
     * 
     * </PRE>
     */
    public Object visit(UpperBoundExpression n, Object argu)
    {
        lowerBound = upperBound;
        upperBound = n.expression;
        return null;
    }

    /**
     * <PRE>
     * 
     * nodeToken -> "[" nodeOptional -> [ RangeExpression() ] nodeToken1 -> "]"
     * 
     * </PRE>
     */
    public Object visit(ArrayRange n, Object argu)
    {
        lowerBound = upperBound = null;
        n.nodeOptional.accept(this, argu);
        lastType = new ArrayType(lastType, lowerBound, n, upperBound);
        return null;
    }

    /**
     * <PRE>
     * 
     * nodeOptional -> [ ( &lt;BIG&gt; | &lt;LITTLE&gt; ) ] nodeChoice -> ( (
     * &lt;UINT8&gt; | &lt;UINT16&gt; | &lt;UINT32&gt; | &lt;UINT64&gt; ) | (
     * &lt;INT8&gt; | &lt;INT16&gt; | &lt;INT32&gt; | &lt;INT64&gt; ) |
     * &lt;STRING&gt; )
     * 
     * </PRE>
     */
    public Object visit(datascript.syntaxtree.BuiltinType n, Object argu)
    {
        byteOrder = TypeInterface.NO_BYTE_ORDER;
        n.nodeOptional.accept(this, argu);

        lastType = null;
        n.nodeChoice.accept(this, argu);
        if (lastType != null)
        { // if BitField
            return null;
        }

        int tokenKind = ((NodeToken) (n.nodeChoice.choice)).kind;

        // inherit byte order if none is specified
        if (byteOrder == TypeInterface.NO_BYTE_ORDER)
        {
            byteOrder = currentCompound.byteOrder;
        }
        else if (tokenKind == DSConstants.UINT8)
        {
            throw new SemanticError(ClosestToken.find(n.nodeOptional),
                    "byte order modifier cannot be applied to uint8");
        }

        if (tokenKind == DSConstants.STRING)
        {
            lastType = StringType.stringType;
        }
        else if (tokenKind == DSConstants.UINT8
                || tokenKind == DSConstants.INT8)
        {
            lastType = BuiltinType.getTypeByTokenKind(tokenKind);
        }
        else if (byteOrder == TypeInterface.BIG_ENDIAN)
        {
            switch (tokenKind)
            {
                case DSConstants.UINT16:
                case DSConstants.UINT32:
                case DSConstants.UINT64:
                case DSConstants.INT16:
                case DSConstants.INT32:
                case DSConstants.INT64:
                    lastType = BuiltinType.getTypeByTokenKind(tokenKind);
                    break;

                default:
                    throw new InternalError("invalid builtin type");
            }
        }
        else
        {
            switch (tokenKind)
            {
                case DSConstants.UINT16:
                    lastType = BuiltinType
                            .getTypeByTokenKind(DSConstants.LEUINT16);
                    break;
                case DSConstants.UINT32:
                    lastType = BuiltinType
                            .getTypeByTokenKind(DSConstants.LEUINT32);
                    break;
                case DSConstants.UINT64:
                    lastType = BuiltinType
                            .getTypeByTokenKind(DSConstants.LEUINT64);
                    break;
                case DSConstants.INT16:
                    lastType = BuiltinType
                            .getTypeByTokenKind(DSConstants.LEINT16);
                    break;
                case DSConstants.INT32:
                    lastType = BuiltinType
                            .getTypeByTokenKind(DSConstants.LEINT32);
                    break;
                case DSConstants.INT64:
                    lastType = BuiltinType
                            .getTypeByTokenKind(DSConstants.LEINT64);
                    break;

                default:
                    throw new InternalError("invalid builtin type");
            }
        }

        return null;
    }

    /*
     * Original production: <PRE> nodeToken -> &lt;BIT&gt; nodeToken1 -> ":"
     * nodeToken2 -> &lt;INTEGER_LITERAL&gt; </PRE>
     */
    /**
     * Grammar production:
     * 
     * <PRE>
     * 
     * nodeToken -> &lt;BIT&gt; nodeChoice -> ( ":" &lt;INTEGER_LITERAL&gt; |
     * "[" Expression() "]" )
     * 
     * </PRE>
     */

    public Object visit(BitField n, Object argu)
    {
        Node choice = n.nodeChoice.choice;
        choice.accept(this, argu);
        if (n.nodeChoice.which == 0)
        {
            Node literal = ((NodeSequence) choice).elementAt(1);
            int length = Integer.parseInt(((NodeToken) literal).tokenImage);
            lastType = new BitFieldType(length, null);
        }
        else
        {
            Node expr = ((NodeSequence) choice).elementAt(1);
            lastType = new BitFieldType(-1, expr);
        }
        return null;
    }
}
