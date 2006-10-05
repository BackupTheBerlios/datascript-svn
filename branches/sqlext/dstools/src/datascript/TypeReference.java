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
import java.util.Vector;

public class TypeReference implements TypeInterface, LinkAction
{
    TypeInterface referent;
    Vector qualifiedName;
    String name;
    Vector containers = new Vector();

    void addContainer(CompoundType ctype)
    {
        containers.addElement(ctype);
    }

    public TypeInterface tryLink(Context ctxt)
    {
        Object obj = null;
        for (int i = 0; i < qualifiedName.size(); i++)
        {
            NodeToken token = (NodeToken) qualifiedName.elementAt(i);
            String name = token.tokenImage;
            obj = ctxt.getSymbol(name);
            if (obj == null)
            {
                throw new LinkError(token, name + " is undefined");
            }
            if (!(obj instanceof TypeInterface))
            {
                throw new LinkError(token, obj + " is not a type");
            }
            if (obj instanceof CompoundType)
            {
                CompoundType ctype = (CompoundType) obj;
                ctxt = ctype.getScope();
            }
        }
        return (TypeInterface) obj;
    }

    public void link(Context ctxt)
    {
        referent = tryLink(ctxt);

        if (referent instanceof CompoundType
                || referent instanceof TypeInstantiation)
        {
            CompoundType creferent;
            if (referent instanceof CompoundType)
            {
                creferent = (CompoundType) referent;
            }
            else
            {
                creferent = (CompoundType) ((TypeInstantiation) referent).baseType;
            }
            for (int i = 0; i < containers.size(); i++)
            {
                creferent.addContainer((CompoundType) containers.elementAt(i));
            }
        }
    }

    TypeReference(Context ctxt, Vector /* of NodeToken */qualifiedName)
    {
        this.qualifiedName = qualifiedName;

        // compute name, only used for printing right now
        StringBuffer b = new StringBuffer();
        for (int i = 0; i < qualifiedName.size(); i++)
        {
            b.append(((NodeToken) qualifiedName.elementAt(i)).tokenImage);
            if (i < qualifiedName.size() - 1)
            {
                b.append(".");
            }
        }
        name = b.toString();

        try
        {
            referent = tryLink(ctxt);
        }
        catch (LinkError _)
        {
            ; // ignore
        }
        if (referent == null)
        {
            ctxt.postLinkAction(this);
        }
    }

    public IntegerValue sizeof(Context ctxt)
    {
        return referent.sizeof(ctxt);
    }

    public boolean isMember(Context ctxt, Value val)
    {
        return referent.isMember(ctxt, val);
    }

    public Value castFrom(Value val)
    {
        return referent.castFrom(val);
    }

    public String toString()
    {
        return "TypeReference name='" + name + "' referent='" + referent + "'";
    }

    static TypeInterface resolveType(TypeInterface type)
    {
        while (type instanceof TypeReference)
        {
            type = ((TypeReference) type).referent;
        }
        return type;
    }
}
