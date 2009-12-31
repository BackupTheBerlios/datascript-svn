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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.berlios.datascript.dataScript.ArgumentList;
import de.berlios.datascript.dataScript.ChoiceMember;
import de.berlios.datascript.dataScript.ChoiceType;
import de.berlios.datascript.dataScript.ComplexType;
import de.berlios.datascript.dataScript.CompoundType;
import de.berlios.datascript.dataScript.DataScriptPackage;
import de.berlios.datascript.dataScript.Element;
import de.berlios.datascript.dataScript.EnumMember;
import de.berlios.datascript.dataScript.EnumType;
import de.berlios.datascript.dataScript.Expression;
import de.berlios.datascript.dataScript.Field;
import de.berlios.datascript.dataScript.Function;
import de.berlios.datascript.dataScript.Model;
import de.berlios.datascript.dataScript.Parameter;
import de.berlios.datascript.dataScript.ParameterList;
import de.berlios.datascript.dataScript.SequenceType;
import de.berlios.datascript.dataScript.Type;
import de.berlios.datascript.dataScript.TypeArgument;
import de.berlios.datascript.dataScript.TypeReference;

public class DataScriptJavaValidator extends AbstractDataScriptJavaValidator
{
    private static Logger log = LoggerFactory.getLogger(DataScriptJavaValidator.class);
    
    private ExpressionValidator exprValidator = new ExpressionValidator(this);
    
    @Check
    public void checkUniqueElements(Model model)
    {
        log.debug("checking uniqueness of elements in model {}", model.getPackage().getName());
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
        log.debug("checking uniqueness of choice alternatives in {}", type.getName());
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
        log.debug("checking uniqueness of sequence members in {}", type.getName());
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
        log.debug("checking uniqueness of enum members in {}", type.getName());
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
        log.debug("checking uniqueness of parameter names members");
        Set<String> names = new HashSet<String>();
        for (Parameter param : paramList.getParameters())
        {
           if (!names.add(param.getName()))
           {
               error("Duplicate member " + param.getName(), DataScriptPackage.PARAMETER__NAME);
           }
        }
    }
    
    @Check
    public void checkExpression(Expression expr)
    {
        log.debug("checking expression");
        if (! (expr.eContainer() instanceof Expression))
        {
            exprValidator.checkExpression(expr);
        }
    }
    
    //@Check
    public void checkArgumentList(Field field)
    {
        TypeReference typeRef = field.getType();
        ArgumentList args = typeRef.getArgs();
        if (args == null)
            return;
        
        ComplexType referencedType = typeRef.getRef();
        

        // lookup referenced type
        Type p = TypeResolver.resolve(referencedType);

        // this must be a compound type
        if (!(p instanceof CompoundType))
        {
            error("'" + referencedType.getName()
                    + "' is not a parameterized type", field, DataScriptPackage.FIELD__TYPE);
        }

        // and it must have a parameter list
        CompoundType compound = (CompoundType) p;
//        if (compound == null)
//        {
//            error("'" + compound.getName()
//                    + "' has no parameterized types");
//            return;
//        }
        int numParams = compound.getParameters().getParameters().size();
        if (numParams == 0)
        {
            error("'" + compound.getName()
                    + "' is not a parameterized type", DataScriptPackage.COMPOUND_TYPE__NAME);
        }

        int numArgs = args.getArguments().size();
        // Number of arguments and parameters must be equal.
        // The type reference is also a child of this node, this accounts for
        // the -1.
        if (numArgs != numParams)
        {
            error("wrong number of parameters", args, DataScriptPackage.ARGUMENT_LIST__ARGUMENTS);
        }

        // Iterate over arguments
        int pos = 0;
        for (TypeArgument arg : args.getArguments())
        {
            Expression expr = arg.getExpr();
            if (arg.getExplicit() == null)
            {
                // Get parameter corresponding to current argument
                Parameter param = compound.getParameters().getParameters().get(pos);
                // Resolve the type.
                Type paramType = param.getType().getRef();
                paramType = TypeResolver.resolve(paramType);

                // Types must be compatible.
                if (!exprValidator.checkCompatibility(paramType, expr
                        .getType()))
                {
                    error("type mismatch in argument " + (pos + 1), arg, DataScriptPackage.TYPE_ARGUMENT__EXPR);
                }
            }
            pos++;
        }
    }
    
    //@Check
    public void checkContainment(CompoundType type)
    {
        
    }
    
}
