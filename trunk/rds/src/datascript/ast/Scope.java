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
import java.util.HashMap;
import java.util.List;

import antlr.collections.AST;
import datascript.antlr.util.TokenAST;
import datascript.antlr.util.ToolContext;

/**
 * A Scope is a lexical scope which maps names to objects. A name must be
 * unique in its scope. This is also true for different object categories:
 * e.g. a type name cannot be reused as a field name in the same scope.
 * <p> 
 * A scope may have a parent scope. Names of the parent
 * scope are visible in the current scope, but may be shadowed.
 * <p>
 * Packages have their own scope. All top level types defined in the package
 * are within the package scope. The package scope has no parent scope. 
 * A top level compound or enumeration type defines its own scope. The field or
 * item names of this type are contained in this scope.
 * <p>
 * A compound type B defined within a compound type A is located in the scope
 * of A. It defines its own scope B with parent scope A.
 * <p>
 * Note: Scope is the only class implementing the Context interface. Context
 * and Scope are inherited from datascript 0.1. I am not sure if the 
 * distinction makes sense - maybe we should stick with the Scope terminology
 * and simply drop this Context interface.
 * 
 * @author HWellmann
 */
public class Scope implements LinkAction
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
    private List<LinkAction> linkActions = new ArrayList<LinkAction>();

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

    /**
     * Each compound type defines its own lexical scope. The parent of this is
     * the one owned by the enclosing compound or by the defining package 
     * otherwise
     * @return the compound type owning this scope, or null, if this scope does
     * not belong to a compound.
     */
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
     * Returns the object for the given name, or null if no such name is 
     * visible. If the name is not defined in the current scope, it is
     * recursively looked up in the parent scope. The recursion terminates
     * at the enclosing package scope, which has no parent.
     * @param symbol   name to be looked up
     * @return corresponding object, or null
     */
    public Object getSymbol(String name)
    {
        Object obj = symbolTable.get(name);
        if (obj == null && parentScope != null)
            return parentScope.getSymbol(name);
        return obj;
    }
    
    /**
     * Looks up a type with a given name in the current scope (including
     * recursion to enclosing scopes). If the type name is not visible at
     * in the current package scope, it will also be looked up in imported
     * packages.
     * <p>
     * 
     * The result may be null if
     * <ul>
     * <li>The name is not defined.</li>
     * <li>The name is not visible.</li>
     * <li>The name does not belong to a type.</li>
     * </ul>
     * @param name
     * @return a type with the given name, or null
     */
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
     * Adds a name with its corresponding object to the current scope.
     * @pre The name is not yet defined in the current scope.
     * @param node    name to be added
     * @param obj       object with this name
     */
    public void setSymbol(AST node, Object obj)
    {
        Object o = symbolTable.put(node.getText(), obj);
        if (o != null)
            ToolContext.logError((TokenAST)node, "'" + node.getText()
                    + "' is already defined in this scope");
    }

    /**
     * Adds a name with its corresponding object to the current scope and
     * marks it as a type name.
     * @pre The name is not yet defined in the current scope.
     * @pre The object is a TypeInterface.
     * @param node    name to be added
     * @param obj       object with this name
     */
    public void setTypeSymbol(AST node, Object type)
    {
        setSymbol(node, type);        
        getPackage().setTypeSymbol(node, type);
    }
    
    /**
     * Each compound type defines its own lexical scope. The parent of this is
     * the one owned by the enclosing compound or by the defining package 
     * otherwise
     * @return the compound type owning this scope, or null, if this scope does
     * not belong to a compound.
     */
    public Scope getParentScope()
    {
        return parentScope;
    }

    /**
     * Registers a link action to be resolved in this scope at a later stage.
     * A link action is posted for each type reference. The reference will 
     * be resolved to the type object of that name.
     * 
     * Note that for a field definition {@code Foo myFoo;}, the name 
     * {@code myFoo} maps to a type reference, whereas the defining occurrence
     * of the name {@code Foo} maps to the type object for {@code Foo}. 
     * 
     * @param act       link action
     */
    public void postLinkAction(LinkAction act)
    {
        linkActions.add(act);
    }

    /**
     * Executes all link actions in the given scope.
     */
    public void link(Scope ctxt)
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
