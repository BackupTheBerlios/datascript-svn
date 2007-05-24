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

import antlr.collections.AST;

/**
 * A Context is a lexical scope which maps names to objects. A name must be
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
public interface Context
{
    /**
     * Each compound type defines its own lexical scope. The parent of this is
     * the one owned by the enclosing compound or by the defining package 
     * otherwise
     * @return the compound type owning this scope, or null, if this scope does
     * not belong to a compound.
     */
    /** Returns the parent scope, or null. */
    Context getParentScope();

    /** 
     * Returns the object for the given name, or null if no such name is 
     * visible. If the name is not defined in the current scope, it is
     * recursively looked up in the parent scope. The recursion terminates
     * at the enclosing package scope, which has no parent.
     * @param symbol   name to be looked up
     * @return corresponding object, or null
     */
    Object getSymbol(String symbol);

    /**
     * Adds a name with its corresponding object to the current scope.
     * @pre The name is not yet defined in the current scope.
     * @param symbol    name to be added
     * @param obj       object with this name
     */
    void setSymbol(AST symbol, Object obj);

    /**
     * Adds a name with its corresponding object to the current scope and
     * marks it as a type name.
     * @pre The name is not yet defined in the current scope.
     * @pre The object is a TypeInterface.
     * @param symbol    name to be added
     * @param obj       object with this name
     */
    void setTypeSymbol(AST symbol, Object obj);

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
    void postLinkAction(LinkAction act);

    /**
     * Each compound type defines its own lexical scope. The parent of this is
     * the one owned by the enclosing compound or by the defining package 
     * otherwise
     * @return the compound type owning this scope, or null, if this scope does
     * not belong to a compound.
     */
    TypeInterface getOwner();
    
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
    TypeInterface getType(String name);
    
    /**
     * Looks up a type with a given name in the current package and in imported
     * packages. If no such type exists, another lookup will be performed within
     * the current package for any objects which are not types.
     * @param name    symbol to be looked up
     * @return a type from the current or any imported package, or any object
     *         from the current package.
     */
    Object getTypeOrSymbol(String name);
    
}
