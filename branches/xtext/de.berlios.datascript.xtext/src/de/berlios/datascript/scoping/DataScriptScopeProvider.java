/* BSD License
 *
 * Copyright (c) 2009, Harald Wellmann, Harman/Becker Automotive Systems
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
package de.berlios.datascript.scoping;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.scoping.IScope;
import org.eclipse.xtext.scoping.IScopedElement;
import org.eclipse.xtext.scoping.impl.AbstractDeclarativeScopeProvider;
import org.eclipse.xtext.scoping.impl.ImportUriUtil;
import org.eclipse.xtext.scoping.impl.ScopedElement;
import org.eclipse.xtext.scoping.impl.SimpleScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.berlios.datascript.dataScript.ChoiceAlternative;
import de.berlios.datascript.dataScript.ChoiceMember;
import de.berlios.datascript.dataScript.ChoiceType;
import de.berlios.datascript.dataScript.Element;
import de.berlios.datascript.dataScript.EnumMember;
import de.berlios.datascript.dataScript.EnumType;
import de.berlios.datascript.dataScript.Expression;
import de.berlios.datascript.dataScript.Field;
import de.berlios.datascript.dataScript.Function;
import de.berlios.datascript.dataScript.Import;
import de.berlios.datascript.dataScript.Model;
import de.berlios.datascript.dataScript.Parameter;
import de.berlios.datascript.dataScript.SequenceType;
import de.berlios.datascript.dataScript.Type;
import de.berlios.datascript.validation.TypeResolver;

/**
 * This class contains custom scoping description.
 * 
 * see : http://www.eclipse.org/Xtext/documentation/latest/xtext.html#scoping on
 * how and when to use it
 * 
 */
public class DataScriptScopeProvider extends AbstractDeclarativeScopeProvider
{
    private static Logger log = LoggerFactory.getLogger(DataScriptScopeProvider.class);
    
    public IScope scope_Value(EnumType enumeration, EClass type)
    {
        log.info("creating Value scope for " + enumeration.getName());
        EList<EnumMember> members = enumeration.getMembers();
        List<IScopedElement> scopedElems = new ArrayList<IScopedElement>(members.size());
        for (EnumMember member : members)
        {
            scopedElems.add(ScopedElement.create(member.getName(), member));
        }
        EObject container = enumeration.eContainer();
        IScope parentScope = getScope(container, type);
        SimpleScope scope = new SimpleScope(parentScope, scopedElems);
        return scope;
    }
    
    public IScope scope_Value(ChoiceMember member, EClass type)
    {
        ChoiceType choice = (ChoiceType) member.eContainer();
        Type selectorType = choice.getSelector().getType();
        Type parentType = choice; 
        if (selectorType instanceof EnumType)
        {
            parentType = selectorType;
        }
        IScope scope = getScope(parentType, type);
        return scope;
    }
    
    public IScope scope_Value(SequenceType compound, EClass type)
    {
        log.info("creating scope for " + compound.getName());
        List<Field> members = compound.getMembers();
        List<IScopedElement> scopedElems = new ArrayList<IScopedElement>(members.size());
        for (Field member : members)
        {
            scopedElems.add(ScopedElement.create(member.getName(), member));
        }
        if (compound.getParameters() != null)
        {
            for (Parameter parameter : compound.getParameters().getParameters())
            {
                scopedElems.add(ScopedElement.create(parameter.getName(), parameter));
            }
        }
        if (compound.getFunctions() != null)
        {
            for (Function function : compound.getFunctions())
            {
                scopedElems.add(ScopedElement.create(function.getName(), function));                
            }
        }
        EObject container = compound.eContainer();
        IScope parentScope = getScope(container, type);
        SimpleScope scope = new SimpleScope(parentScope, scopedElems);
        return scope;
    }

    public IScope scope_Value(ChoiceType choice, EClass type)
    {
        log.info("creating scope for " + choice.getName());
        List<ChoiceMember> members = choice.getMembers();
        List<IScopedElement> scopedElems = new ArrayList<IScopedElement>(members.size());
        for (ChoiceMember member : members)
        {
            ChoiceAlternative alternative = member.getAlternative();
            if (alternative != null)
            {
                scopedElems.add(ScopedElement.create(alternative.getName(), alternative));
            }
        }
        if (choice.getParameters() != null)
        {
            for (Parameter parameter : choice.getParameters().getParameters())
            {
                scopedElems.add(ScopedElement.create(parameter.getName(), parameter));
            }
        }
        if (choice.getFunctions() != null)
        {
            for (Function function : choice.getFunctions())
            {
                scopedElems.add(ScopedElement.create(function.getName(), function));                
            }
        }
        EObject container = choice.eContainer();
        IScope parentScope = getScope(container, type);
        SimpleScope scope = new SimpleScope(parentScope, scopedElems);
        return scope;
    }
    
    
    public IScope scope_Expression_ref(Expression expr, EReference ref)
    {
        log.info("creating scope for ref in PrimaryExpression");
        IScope scope;
        Expression parent = getParentExpression(expr);
        if (isRightOfDot(parent, expr))
        {
            Expression sibling = getLeftSibling(expr);
            Type siblingType = getType(sibling);
            scope = getScope(siblingType, ref);
        }
        else
        {
            scope = getScope(getNonExpressionContainer(expr), ref);
        }
        return scope;
    }
    
    private Type getType(Expression expr)
    {
        Type type;
        if (expr.getType() != null)
        {
            type = TypeResolver.resolve(expr.getType());
        }
        else if (expr.getRef() != null)
        {
            type = TypeResolver.getType(expr.getRef());
        }
        else
        {
            throw new IllegalStateException();
        }
        return type;
    }

    private boolean isRightOfDot(Expression parent, Expression expr)
    {
        return (parent != null && ".".equals(parent.getOperator()) && parent.getRight() == expr);
    }
    
    private Expression getLeftSibling(Expression expr)
    {
        Expression left = null;
        if (expr.eContainer() instanceof Expression)
        {
            Expression parent = (Expression) expr.eContainer();
            left = parent.getLeft();
        }
        return left;
    }
    
    private Expression getParentExpression(Expression expr)
    {
        Expression parent = null;
        if (expr.eContainer() instanceof Expression)
        {
            parent = (Expression) expr.eContainer();
        }
        return parent;
    }
    
    private EObject getNonExpressionContainer(Expression expr)
    {
        EObject container = expr;
        while (container instanceof Expression)
        {
            container = container.eContainer();
        }
        return container;
    }
    
    
    public IScope scope_Value(Model model, EClass type)
    {
        log.info("creating scope for " + model);        
        
        Set<String> uniqueImports = new HashSet<String>(10);
        List<String> orderedImports = new ArrayList<String>(10);        
        
        for (Import imported : model.getImports())
        {
            String uri = imported.getImportURI();
            if (uniqueImports.add(uri) && ImportUriUtil.isValid(model, uri));
            {
                orderedImports.add(uri);
            }
        }
        
        IScope scope = IScope.NULLSCOPE;
        for (int i = orderedImports.size() - 1; i >= 0; i--) 
        {
            String uri = orderedImports.get(i);
            Resource resource = ImportUriUtil.getResource(model.eResource(), uri);
            Model importedModel = (Model) resource.getContents().get(0);
            scope = new SimpleScope(scope, scopedElementsForValue(importedModel));
        }

        scope = new SimpleScope(scope, scopedElementsForValue(model));        
        return scope;
    }

    private List<IScopedElement> scopedElementsForValue(Model model)
    {
        List<IScopedElement> scopedElems = new ArrayList<IScopedElement>();
        for (Element element : model.getElements())
        {
            scopedElems.add(ScopedElement.create(element.getName(), element));
        }
        return scopedElems;
    }
}
