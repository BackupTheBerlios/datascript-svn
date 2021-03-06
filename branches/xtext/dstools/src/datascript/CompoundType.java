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

import java.util.Vector;
import java.util.Stack;
import java.util.Iterator;

abstract public class CompoundType implements TypeInterface
{
    Vector fields = new Vector();
    Vector nestedTypes = new Vector();
    Vector conditions = new Vector();

    // / enum and bitmasks lexically contained in this compound
    Vector settypes = new Vector();
    Vector parameters = null;

    // / set of compound types that can contain this type
    Vector containers = new Vector();

    // / one of TypeInterface.NOBYTEORDER, BIGENDIAN, LITTLEENDIAN
    int byteOrder;
    private Scope scope;
    private String name;
    private String doc;
    private CompoundType parent;

    private boolean isAnonymous = false;
    private static int anonTypeCounter = 0;

    abstract public IntegerValue sizeof(Context ctxt);

    abstract public boolean isMember(Context ctxt, Value val);

    protected boolean bfoComputed = false;

    abstract void computeBitFieldOffsets(int offset);

    CompoundType(String name, String doc, CompoundType parent)
    {
        this.parent = parent;
        this.doc = doc;
        if (name != null)
        {
            this.name = name;
        }
        else
        {
            isAnonymous = true;
            this.name = "anontype_" + anonTypeCounter++;
        }
    }

    CompoundType getParent()
    {
        return parent;
    }

    boolean isAnonymous()
    {
        return isAnonymous;
    }

    void addParameters(Vector parameters)
    {
        this.parameters = parameters;
    }

    Iterator getParameters()
    {
        return parameters.iterator();
    }

    boolean hasParameters()
    {
        return parameters != null;
    }

    boolean isEmpty()
    {
        return fields.size() == 0 && conditions.size() == 0
                && nestedTypes.size() == 0;
    }

    public String getName()
    {
        return name;
    }

    public String getDocumentation()
    {
        return doc;
    }

    void addContainer(CompoundType f)
    {
        if (!containers.contains(f))
        {
            if (Main.containedgraph != null)
            {
                Main.containedgraph.println("\t" + f.getName() + " -> "
                        + getName() + ";");
            }
            Main.debug(f + " is a container of " + this);
            containers.addElement(f);
        }
    }

    void resolveFieldTypes()
    {
        for (int i = 0; i < fields.size(); i++)
        {
            ((Field) fields.elementAt(i)).resolveFieldType();
        }
        if (hasParameters())
        {
            for (int i = 0; i < parameters.size(); i++)
            {
                ((Parameter) parameters.elementAt(i)).resolveType(this);
            }
        }

        for (int i = 0; i < nestedTypes.size(); i++)
        {
            ((CompoundType) nestedTypes.elementAt(i)).resolveFieldTypes();
        }
    }

    /**
     * @return true if 'this' is contained in compound type 'f'
     */
    boolean isContained(CompoundType f)
    {
        return isContained(f, new Stack());
    }

    /**
     * The "is contained" relationship may contain cycles use a stack to avoid
     * them. This is a simple DFS path finding algorithm that finds a path from
     * 'this' to 'f'.
     */
    boolean isContained(CompoundType f, Stack seen)
    {
        Main.debug("is " + this + " contained in " + f);
        if (containers.contains(f))
        {
            return true;
        }

        /* check whether any container of 'this' is contained in 'f' */
        for (int i = 0; i < containers.size(); i++)
        {
            CompoundType c = (CompoundType) containers.elementAt(i);
            if (seen.search(c) == -1)
            {
                seen.push(c);
                if (c.isContained(f, seen))
                {
                    return true;
                }
                seen.pop();
            }
        }
        return false;
    }

    public Iterator getFields()
    {
        return fields.iterator();
    }

    public Iterable<Field> getFields_()
    {
        return fields;
    }

    void addField(Field f)
    {
        Main.assertThat(!fields.contains(f));
        fields.addElement(f);
    }

    Field getField(int i)
    {
        return (Field) fields.elementAt(i);
    }

    Iterator getSetTypes()
    {
        return settypes.iterator();
    }

    void addSetType(SetType stype)
    {
        Main.assertThat(!settypes.contains(stype));
        settypes.addElement(stype);
    }

    Iterator getNestedTypes()
    {
        return nestedTypes.iterator();
    }

    Iterable<TypeInterface> getNestedTypes_()
    {
        return nestedTypes;
    }

    void addNestedType(CompoundType c)
    {
        nestedTypes.addElement(c);
    }

    Iterator getConditions()
    {
        return conditions.iterator();
    }

    void addCondition(Condition c)
    {
        conditions.addElement(c);
    }

    int getByteOrder()
    {
        return byteOrder;
    }

    void setByteOrder(int byteOrder)
    {
        this.byteOrder = byteOrder;
    }

    public Value castFrom(Value val)
    {
        throw new CastError("casting compounds not implemented");
    }

    Scope getScope()
    {
        return scope;
    }

    void setScope(Scope scope)
    {
        this.scope = scope;
    }

    void link()
    {
        scope.link(null);
    }

    public String toString()
    {
        return name;
    }

    public abstract Field.LookAhead getLookAhead();
}
