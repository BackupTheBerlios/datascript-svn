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

import java.util.ArrayList;
import java.util.List;

import antlr.collections.AST;
import datascript.antlr.DataScriptParserTokenTypes;


@SuppressWarnings("serial")
public class ChoiceType extends CompoundType
{
    List<ChoiceMember> choiceMembers = null;
    ChoiceDefault choiceDefault = null;


    @Override
    public IntegerValue sizeof(Context ctxt)
    {
        IntegerValue size = ((Field) fields.get(0)).sizeof(ctxt);
        for (int i = 1; i < fields.size(); i++)
        {
            if (size.compareTo(((Field) fields.get(i)).sizeof(ctxt)) != 0)
            {
                throw new ComputeError("sizeof is ambiguous");
            }
        }
        return size;
    }


    public IntegerValue bitsizeof(Context ctxt)
    {
        IntegerValue size = ((Field) fields.get(0)).bitsizeof(ctxt);
        for (int i = 1; i < fields.size(); i++)
        {
            if (size.compareTo(((Field) fields.get(i)).bitsizeof(ctxt)) != 0)
            {
                throw new ComputeError("bitsizeof is ambiguous");
            }
        }
        return size;
    }


    @Override
    public boolean isMember(Context ctxt, Value val)
    {
        // do something like
        // if val.getType() == this
        throw new ComputeError("isMember not implemented");
    }


    @Override
    public String toString()
    {
        return "CHOICE";
    }


    public AST getSelectorAST()
    {
        AST node = getFirstChild().getNextSibling();
        while (node != null && !(node instanceof Expression))
        {
            node = node.getNextSibling();
        }

        return node;
    }


    public List<ChoiceMember> getChoiceMembers()
    {
        if (choiceMembers != null)
            return choiceMembers;

        choiceMembers = new ArrayList<ChoiceMember>();

        // get CHOICE_MEMBERS
        AST node = getFirstChild().getNextSibling().getNextSibling();
        while (node != null && node.getType() != DataScriptParserTokenTypes.MEMBERS)
            node = node.getNextSibling();
        
        if (node == null)
            return choiceMembers;

        node = node.getFirstChild();
        while(node != null)
        {
            switch (node.getType())
            {
            case DataScriptParserTokenTypes.CASE:
                choiceMembers.add((ChoiceCase)node);
                break;
            case DataScriptParserTokenTypes.DEFAULT:
                choiceMembers.add((ChoiceDefault)node);
                break;
            }
            node = node.getNextSibling();
        }
        return choiceMembers;
    }
}
