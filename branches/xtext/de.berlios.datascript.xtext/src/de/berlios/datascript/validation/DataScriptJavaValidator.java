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
package de.berlios.datascript.validation;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.xtext.validation.Check;

import de.berlios.datascript.dataScript.ChoiceMember;
import de.berlios.datascript.dataScript.ChoiceType;
import de.berlios.datascript.dataScript.DataScriptPackage;
import de.berlios.datascript.dataScript.Element;
import de.berlios.datascript.dataScript.EnumMember;
import de.berlios.datascript.dataScript.EnumType;
import de.berlios.datascript.dataScript.Field;
import de.berlios.datascript.dataScript.Function;
import de.berlios.datascript.dataScript.Model;
import de.berlios.datascript.dataScript.Parameter;
import de.berlios.datascript.dataScript.ParameterList;
import de.berlios.datascript.dataScript.SequenceType;

public class DataScriptJavaValidator extends AbstractDataScriptJavaValidator
{

    @Check
    public void checkUniqueElements(Model model)
    {
        Set<String> names = new HashSet<String>();
        for (Element member : model.getElements())
        {
           if (!names.add(member.getName()))
           {
               error("Duplicate member " + member.getName(), member, DataScriptPackage.ELEMENT__NAME);
           }
        }
    }
    
    @Check
    public void checkUniqueAlternatives(ChoiceType type)
    {
        Set<String> names = new HashSet<String>();
        for (ChoiceMember member : type.getMembers())
        {
            String memberName = member.getAlternative().getName();
            if (!names.add(memberName))
            {
                error("Duplicate member " + memberName,
                        DataScriptPackage.CHOICE_TYPE__NAME);
            }
        }
        for (Function member : type.getFunctions())
        {
            String memberName = member.getName();
            if (!names.add(memberName))
            {
                error("Duplicate member " + memberName,
                        DataScriptPackage.CHOICE_TYPE__NAME);
            }
        }
    }

    @Check
    public void checkUniqueMembers(SequenceType type)
    {
        Set<String> names = new HashSet<String>();
        for (Field member : type.getMembers())
        {
           if (!names.add(member.getName()))
           {
               error("Duplicate member " + member.getName(), member, DataScriptPackage.SEQUENCE_TYPE__NAME);
           }
        }
        for (Function member : type.getFunctions())
        {
            String memberName = member.getName();
            if (!names.add(memberName))
            {
                error("Duplicate member " + memberName, member,
                        DataScriptPackage.SEQUENCE_TYPE__NAME);
            }
        }
    }

    @Check
    public void checkUniqueMembers(EnumType type)
    {
        Set<String> names = new HashSet<String>();
        for (EnumMember member : type.getMembers())
        {
           if (!names.add(member.getName()))
           {
               error("Duplicate member " + member.getName(), DataScriptPackage.ENUM_MEMBER__NAME);
           }
        }
    }

    @Check
    public void checkUniqueParameters(ParameterList paramList)
    {
        Set<String> names = new HashSet<String>();
        for (Parameter param : paramList.getParameters())
        {
           if (!names.add(param.getName()))
           {
               error("Duplicate member " + param.getName(), DataScriptPackage.PARAMETER__NAME);
           }
        }
    }
    
}
