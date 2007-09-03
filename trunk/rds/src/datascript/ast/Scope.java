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

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import antlr.collections.AST;
import datascript.antlr.util.TokenAST;
import datascript.antlr.util.ToolContext;

/**
 * This class implements the Context interface and thus provides a lexical 
 * scope for symbols. In addition, it stores link actions to be resolved within
 * this scope after parsing all translation units.
 */
public class Scope implements Context, LinkAction
{
	/** 
	 * Symbol table containing local symbols defined within the current scope.
	 * Each symbol is mapped to an Object.
	 */
	private HashMap<String, Object> symbolTable = new HashMap<String, Object>();

	/**
	 * Scope containing the current one. null for the package scope.
	 */
    private Scope parentScope;
    
    /**
     * If this scope is defined by a compound or enumeration type, owner
     * is the corresponding type.
     */
    private TypeInterface owner;
    
    /**
     * Current field of the compound containing an expression to be evaluated.
     * To be used in cases where the result depends on the field, and not on
     * the compound type alone.
     * 
     * (Currently only used for the array$index operator.)
     */
    private Field currentField;

    /**
     * List of link actions to be executed within this scope.
     * All children of this scope post a link action for themselved on creation.
     * Thus when linking this scope, all subscopes will be linked automatically.
     */
    private List<LinkAction> linkActions = new Vector<LinkAction>();

    /**
     * Constructs scope without parent.
     */
    public Scope()
    {
        this(null);
    }

    /**
     * Constructs scope with given parent and posts a link action for this scope
     * with the parent.
     * @param parentScope       parent of current scope
     */
    public Scope(Scope parentScope)
    {
        this.parentScope = parentScope;
        if (parentScope != null)
        {
            parentScope.postLinkAction(this);
        }
    }

    public TypeInterface getOwner()
    {
        return owner;
    }
    
    /** 
     * Sets owner of current scope. The owner is a compound or enumeration type.
     * 
     * @param owner   type defining this scope
     */
    public void setOwner(TypeInterface owner)
    {
        this.owner = owner;
    }
    
    public Field getCurrentField()
    {
        return currentField;
    }
    
    public void setCurrentField(Field f)
    {
        currentField = f;
    }
    
    /**
     * get this symbol from this or a parent scope
     */
    public Object getSymbol(String name)
    {
        Object obj = symbolTable.get(name);
        if (obj == null && parentScope != null)
            return parentScope.getSymbol(name);
        return obj;
    }
    
    public TypeInterface getType(String name)
    {
        Object obj = getSymbol(name);
        if (obj == null)
        {
            if (parentScope != null)
            {
                obj = parentScope.getType(name);
            }
        }
        return (obj instanceof TypeInterface) ? (TypeInterface)obj : null;
    }


    /**
     * Get the owner in whose scope this symbol is defined.
     */
    public CompoundType getDefiningType(String name)
    {
        Object obj = symbolTable.get(name);
        if (obj == null)
        {
            if (parentScope == null)
            {
                return null;
            }
            else
            {
                return ((Scope)parentScope).getDefiningType(name);
            }
        }
        else
        {
            return (CompoundType) owner;
        }
    }

    /**
     * Get this symbol from this scope, without recursion to parent scope.
     * @param name      symbol to be looked up
     */
    public Object getSymbolFromThis(String name)
    {
        Object obj = symbolTable.get(name);
        return obj;
    }

    /**
     * Adds an entry to the symbol table of this scope. Logs an error if a
     * a symbol with the given name is already defined in the current scope.
     * @param node  syntax tree node containing the symbol (getText())
     * @param obj	corresponding object
     */
    public void setSymbol(AST node, Object obj)
    {
        Object o = symbolTable.put(node.getText(), obj);
        if (o != null)
            ToolContext.logError((TokenAST)node, "'" + node.getText()
                    + "' is already defined in this scope");
    }

    /**
     * Same as setSymbol. Only for package scopes, types receive a special
     * handling.
     * @param node  syntax tree node containing the symbol (getText())
     * @param obj	corresponding object
     */
    public void setTypeSymbol(AST node, Object type)
    {
        setSymbol(node, type);        
        getPackage().setTypeSymbol(node, type);
    }
    
    public Context getParentScope()
    {
        return parentScope;
    }

    public void postLinkAction(LinkAction act)
    {
        linkActions.add(act);
    }

    public void link(Context ctxt)
    {
        for (LinkAction l : linkActions)
        {
            l.link(this);
        }
    }

    /**
     * Returns the package containing this scope.
     * @return enclosing package, or null.
     */
    public Package getPackage()
    {
        if (parentScope == null)
            return null;
        return parentScope.getPackage();
    }

    /**
     * Looks up a type with a given name in the current package and in imported
     * packages. If no such type exists, another lookup will be performed within
     * the current package for any objects which are not types.
     * 
     * @param name    symbol to be looked up
     * @return a type from the current or any imported package, or any object
     *         from the current package.
     */
    public Object getTypeOrSymbol(String name)
    {
    	Object obj = getType(name);
    	if (obj == null)
    	{
    		obj = getSymbol(name);
    	}
    	return obj;
    }
    
    
}
