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
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.scoping.IScope;
import org.eclipse.xtext.scoping.IScopedElement;
import org.eclipse.xtext.scoping.impl.AbstractDeclarativeScopeProvider;
import org.eclipse.xtext.scoping.impl.ScopedElement;
import org.eclipse.xtext.scoping.impl.SimpleScope;

import de.berlios.datascript.dataScript.ChoiceMember;
import de.berlios.datascript.dataScript.ChoiceType;
import de.berlios.datascript.dataScript.EnumMember;
import de.berlios.datascript.dataScript.EnumType;
import de.berlios.datascript.dataScript.Field;
import de.berlios.datascript.dataScript.Parameter;
import de.berlios.datascript.dataScript.SequenceType;

/**
 * This class contains custom scoping description.
 * 
 * see : http://www.eclipse.org/Xtext/documentation/latest/xtext.html#scoping on
 * how and when to use it
 * 
 */
public class DataScriptScopeProvider extends AbstractDeclarativeScopeProvider
{
    public IScope scope_EnumMember(EnumType enumeration, EClass type)
    {
        System.out.println("creating scope for " + enumeration.getName());
        EList<EnumMember> members = enumeration.getMembers();
        List<IScopedElement> scopedElems = new ArrayList<IScopedElement>(members.size());
        for (EnumMember member : members)
        {
            scopedElems.add(ScopedElement.create(member.getName(), member));
        }
        SimpleScope scope = new SimpleScope(scopedElems);
        return scope;
    }
    
    public IScope scope_Value(SequenceType compound, EClass type)
    {
        System.out.println("creating scope for " + compound.getName());
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
        EObject container = compound.eContainer();
        IScope parentScope = getScope(container, type);
        SimpleScope scope = new SimpleScope(parentScope, scopedElems);
        return scope;
    }

    public IScope scope_Value(ChoiceType choice, EClass type)
    {
        System.out.println("creating scope for " + choice.getName());
        List<ChoiceMember> members = choice.getMembers();
        List<IScopedElement> scopedElems = new ArrayList<IScopedElement>(members.size());
        for (ChoiceMember member : members)
        {
            scopedElems.add(ScopedElement.create(member.getAlternative().getName(), member));
        }
        if (choice.getParameters() != null)
        {
            for (Parameter parameter : choice.getParameters().getParameters())
            {
                scopedElems.add(ScopedElement.create(parameter.getName(), parameter));
            }
        }
        EObject container = choice.eContainer();
        IScope parentScope = getScope(container, type);
        SimpleScope scope = new SimpleScope(parentScope, scopedElems);
        return scope;
    }
}
