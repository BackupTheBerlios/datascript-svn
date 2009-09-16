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

import antlr.Token;
import antlr.collections.AST;
import datascript.antlr.DataScriptParserTokenTypes;



abstract public class CompoundType 
    extends Container 
    implements TypeInterface, Comparable<CompoundType>
{
    private static final long serialVersionUID = -3176164167667658185L;

    protected int id;

    protected final List<Field> fields = new ArrayList<Field>();
    protected boolean bfoComputed;
    private final List<FunctionType> functions = new ArrayList<FunctionType>();
    private final List<Parameter> parameters = new ArrayList<Parameter>();

    private Scope scope;
    private Package pakkage;
    private String name;
    private CompoundType parent;
    private Token doc;


    protected CompoundType()
    {
        id = TypeRegistry.registerType(this);
    }


    abstract public IntegerValue sizeof(Scope ctxt);


    abstract public boolean isMember(Scope ctxt, Value val);


    public CompoundType getParent()
    {
        return parent;
    }


    /*
     * public void addParameter(String param) { if (parameters == null) {
     * parameters = new Vector<String>(); } parameters.addElement(param); }
     */
    public boolean isParameter(String param)
    {
        return (parameters != null) && parameters.contains(param);
    }


    public List<Parameter> getParameters()
    {
        return parameters;
    }


    public Parameter getParameterAt(int index)
    {
        return parameters.get(index);
    }


    public int getParameterCount()
    {
        return parameters.size();
    }


    public boolean isEmpty()
    {
        return fields.size() == 0;
    }


    public String getName()
    {
        if (name == null)
        {
            AST node = getFirstChild();
            if (node == null)
            {
                name = "<anonymous>";
            }
            else
            {
                name = node.getText();
            }
        }
        return name;
    }


    public String getDocumentation()
    {
        String result = "";
        if (doc != null && doc.getType() == DataScriptParserTokenTypes.DOC)
        {
            result = doc.getText();
        }
        return result;
    }


    public void setDocumentation(Token t)
    {
        doc = t;
    }


    public List<Field> getFields()
    {
        return fields;
    }


    public int getNumFields()
    {
        return fields.size();
    }


    public void addField(Field f)
    {
        // TODO: Main.assertThat(!fields.contains(f));
        fields.add(f);
    }


    public Field getField(int i)
    {
        return fields.get(i);
    }


    public Scope getScope()
    {
        return scope;
    }


    public void setScope(Scope scope, Package pkg)
    {
        this.scope = scope;
        this.pakkage = pkg;
        scope.setOwner(this);
    }


    @Override
    public String toString()
    {
        return name;
    }


    public Value castFrom(Value val)
    {
        throw new Error("casting compounds not implemented in "
                + this.getClass().getName());
    }


    public void storeParameters()
    {
        AST node = getFirstChild().getNextSibling();
        if (node.getType() == DataScriptParserTokenTypes.PARAMLIST)
        {
            parameters.clear();
            AST p = node.getFirstChild();
            while (p != null)
            {
                storeParameter(p);
                p = p.getNextSibling();
            }
        }
    }


    private void storeParameter(AST param)
    {
        AST type = param.getFirstChild();
        AST paramId = type.getNextSibling();
        Parameter p = new Parameter(paramId.getText(), (TypeInterface) type);
        parameters.add(p);
        scope.setSymbol(paramId, p);
    }


    public int getLength()
    {
        throw new InternalError("getLength() not implemented in "
                + this.getClass().getName());
    }


    public Expression getLengthExpression()
    {
        throw new InternalError("getLengthExpression() not implemented in "
                + this.getClass().getName());
    }


    public Package getPackage()
    {
        return pakkage;
    }


    public void addFunction(AST function)
    {
        functions.add((FunctionType) function);
    }


    public List<FunctionType> getFunctions()
    {
        return functions;
    }


    public int getId()
    {
        return id;
    }
    

    @Override
    public int compareTo(CompoundType o)
    {
        return getName().compareTo(o.getName());
    }
    
    public boolean hasLabels()
    {
        for (Field field : getFields())
        {
            if (field.getLabel() != null)
            {
                return true;
            }
        }
        return false;
    }
}
