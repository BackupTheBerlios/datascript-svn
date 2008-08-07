/* BSD License
 *
 * Copyright (c) 2006, Harald Wellmann, Henrik Wedekind Harman/Becker Automotive Systems
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


package datascript.emit.java;


import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import antlr.collections.AST;
import datascript.ast.ArrayType;
import datascript.ast.BitFieldType;
import datascript.ast.ChoiceCase;
import datascript.ast.ChoiceMember;
import datascript.ast.ChoiceType;
import datascript.ast.CompoundType;
import datascript.ast.DataScriptException;
import datascript.ast.EnumType;
import datascript.ast.Expression;
import datascript.ast.Field;
import datascript.ast.FunctionType;
import datascript.ast.Parameter;
import datascript.ast.StdIntegerType;
import datascript.ast.Subtype;
import datascript.ast.TypeInstantiation;
import datascript.ast.TypeInterface;
import datascript.ast.TypeReference;
import freemarker.template.Configuration;
import freemarker.template.Template;



public class ChoiceEmitter extends CompoundEmitter
{
    private final List<CompoundFunctionEmitter> functions = 
        new ArrayList<CompoundFunctionEmitter>();

    private final List<ChoiceMemberEmitter> members = 
        new ArrayList<ChoiceMemberEmitter>();

    ChoiceType choice;



    public static class ChoiceMemberEmitter
    {
        private final ChoiceEmitter global;
        protected final ChoiceMember member;

        private static Template tpl = null;


        public ChoiceMemberEmitter(ChoiceMember choiceMember, ChoiceEmitter choiceEmitter)
        {
            member = choiceMember;
            global = choiceEmitter;
        }


        public void emit(PrintWriter writer, Configuration cfg) throws Exception
        {
            if (tpl == null)
                tpl = cfg.getTemplate("java/ChoiceFieldAccessor.ftl");
            tpl.process(this, writer);
        }


        public ChoiceMember getMember()
        {
            return member;
        }


        public String getName()
        {
            return member.getField().getName();
        }


        public List<Expression> getCases()
        {
            if (member instanceof ChoiceCase)
                return ((ChoiceCase)member).getCases();
            return null;
        }


        public String getJavaTypeName()
        {
            Field field = member.getField();
            if (field == null)
                return "";
            return TypeNameEmitter.getTypeName(field.getFieldType());
        }


        public String getClassName()
        {
            return TypeNameEmitter.getClassName((TypeInterface) member.getField().getFirstChild());
        }


        public String getGetterName()
        {
            return AccessorNameEmitter.getGetterName(member.getField());
        }


        public String getSetterName()
        {
            return AccessorNameEmitter.getSetterName(member.getField());
        }


        public String getReadField()
        {
            Field field = member.getField();
            if (field == null)
                return ";";
            return global.readField(field);
        }


        public String getWriteField()
        {
            Field field = member.getField();
            if (field == null)
                return ";";
            return global.writeField(field);
        }
        

        public String getElementType()
        {
            Field field = member.getField();
            if (field.getFieldType() instanceof ArrayType)
            {
                ArrayType arrayType = (ArrayType) field.getFieldType();
                TypeInterface elType = arrayType.getElementType();
                if (elType instanceof CompoundType || 
                    elType instanceof EnumType ||
                    elType instanceof TypeInstantiation)
                {
                    return TypeNameEmitter.getTypeName(elType);
                }
            }
            return null;
        }


        public String getSelector()
        {
            return global.getSelector();
        }


        public boolean getSelectorIsSimple()
        {
            boolean result = false;
            
            Expression node = (Expression)global.choice.getSelectorAST();
            if (node != null)
            {
                TypeInterface type = node.getExprType();
                if (type instanceof TypeReference)
                {
                    type = TypeReference.resolveType(type);
                }
                if (type instanceof Subtype)
                {
                    type = ((Subtype)type).getBaseType();
                }
                if (type instanceof StdIntegerType)
                {
                    result = ((StdIntegerType)type).getType() != datascript.antlr.DataScriptParserTokenTypes.UINT64;            
                }
                else if (type instanceof BitFieldType)
                {
                    BitFieldType bitField = (BitFieldType) type;
                    result = 0 < bitField.getLength() && bitField.getLength() < 64;
                }
            }
            return result;
        }


        public String getSelectorType()
        {
            String selType = null;

            Expression node = (Expression)global.choice.getSelectorAST();
            if (node != null)
            {
                TypeInterface exprType = node.getExprType();
                selType = TypeNameEmitter.getTypeName(exprType);
            }

            return selType;
        }


        public boolean getEqualsCanThrowExceptions()
        {
            return global.getEqualsCanThrowExceptions();
        }
    }


    public ChoiceEmitter(JavaDefaultEmitter j, ChoiceType choice)
    {
        super(j);
        this.choice = choice;
    }


    public ChoiceType getChoiceType()
    {
        return choice;
    }


    @Override
    public CompoundType getCompoundType()
    {
        return choice;
    }


    public void begin(Configuration cfg)
    {
        members.clear();
        for (ChoiceMember choiceMember : choice.getChoiceMembers())
        {
            ChoiceMemberEmitter me = new ChoiceMemberEmitter(choiceMember, this);
            members.add(me);
        }
        params.clear();
        for (Parameter param : choice.getParameters())
        {
            CompoundParameterEmitter p = new CompoundParameterEmitter(param, this);
            params.add(p);
        }
        functions.clear();
        for (FunctionType func : choice.getFunctions())
        {
            CompoundFunctionEmitter f = new CompoundFunctionEmitter(func);
            functions.add(f);
        }
        

        try
        {
            Template tpl = cfg.getTemplate("java/ChoiceBegin.ftl");
            tpl.process(this, writer);

            for (ChoiceMemberEmitter choiceMember : members)
            {
                if (choiceMember.getMember().getField() != null)
                    choiceMember.emit(writer, cfg);
            }

            for (CompoundParameterEmitter param : params)
            {
                param.emit(writer, cfg);
            }

            for (CompoundFunctionEmitter func : functions)
            {
                func.emit(writer, cfg);
            }


            tpl = cfg.getTemplate("java/ChoiceRead.ftl");
            tpl.process(this, writer);

            tpl = cfg.getTemplate("java/ChoiceWrite.ftl");
            tpl.process(this, writer);
        }
        catch (Exception e)
        {
            throw new DataScriptException(e);
        }
    }


    public void end(Configuration cfg)
    {
        try
        {
            Template tpl = cfg.getTemplate("java/SequenceEnd.ftl");

            tpl.process(this, writer);
        }
        catch (Exception e)
        {
            throw new DataScriptException(e);
        }
    }


    public String getSelector()
    {
        String selector = null;

        AST node = choice.getSelectorAST();
        if (node != null)
        {
            ExpressionEmitter ee = new ExpressionEmitter();
            selector = ee.emit((Expression) node);
        }

//        if (selector == null)
//            throw new ComputeError("missing selector");
        return selector;
    }


    public List<ChoiceMemberEmitter> getMembers()
    {
        return members;
    }

}
