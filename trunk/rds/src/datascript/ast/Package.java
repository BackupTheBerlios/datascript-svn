/* BSD License
 *
 * Copyright (c) 2007, Harald Wellmann, Harman/Becker Automotive Systems
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
import java.util.Map;
import java.util.Set;

import antlr.collections.AST;
import datascript.antlr.util.TokenAST;
import datascript.antlr.util.ToolContext;

/**
 * This class represents a DataScript package which provides a separate lexical
 * scope for type names and other names.
 * 
 * By default, only type names defined in the current package are visible. Names
 * from other packages can be made visible with an <code>import</code>
 * declaration.
 * 
 * The root package is the one passed to the DataScript compiler. This package
 * is special in the sense that some generated Java classes (e.g. __Visitor)
 * which are required in all generated classes are put into the Java package
 * associated to the DataScript root package.
 * 
 * This design is somewhat awkward and may be changed in the future.
 * 
 * There is an important restriction regarding nested types. The most common
 * examples are sql_table types within an sql_database definition or union
 * types within a sequence definition.
 * 
 * At DataScript level, two outer types A and B may each contain a nested type
 * of the same name C, thus there are two distinct types A.C and B.C. To model
 * these adequately in the the code generators, we would have to generate 
 * inner classes or to make the class names unique by prefixing the enclosing
 * type, e.g. A__C and B__C.
 * 
 * To keep things simple and backward compatible, we require that the names of
 * nested types SHALL be unique within a package, and we include nested type
 * names in the list of local type names of a package.
 * @author HWellmann
 *
 */
public class Package extends Scope
{
    /** System package for built-in types. */
    public static Package BUILTIN;

    /** 
     * Default package for types without an explicit package declaration.
     * TODO: This was intended for backward compatibility. The implementation
     * is probably not complete, since all existing DataScript modules now have
     * a package declaration.  
     */
    public static Package DEFAULT;

    static
    {
        BUILTIN = new Package();
        BUILTIN.packagePath = new ArrayList<String>();
        BUILTIN.packagePath.add("__builtin__");
        DEFAULT = new Package();
        DEFAULT.packagePath = new ArrayList<String>();
        DEFAULT.packagePath.add("__default__");
    }
	
    /** Map of all packages in the project. */
    private static Map<String, Package> nameToPackage = new HashMap<String, Package>();
    
    /** Map of all packages in the project. */
    private static Map<TokenAST, Package> nodeToPackage = new HashMap<TokenAST, Package>();
    
    /** The root package, i.e. the one first parsed. */
    private static Package root = null;
    
    /** The PACKAGE node of the AST defining this package. */
    private TokenAST node;
    
    /** The fully qualified package name, e.g. "com.acme.foo.bar". */
    private String packageName;
    
    /** List of the package name parts, e.g. ["com", "acme", "foo", "bar"] */
    private List<String> packagePath;
    
    /** Maps all type names defined in this package to the corresponding type. */
    private Map<String, TypeInterface> localTypes;
    
    // TODO: implement single type imports: "import com.acme.foo.bar.MyType;"
    //private Map<String, TypeInterface> importedTypes;
    
    /** 
     * Maps the fully qualified name of each imported package to the 
     * corresponding package. At first, the value of each entry is null, just
     * to keep track of an imported package name. In the link phase, the null
     * value is replaced by the corresponding Package object.
     */
    private Map<String, Package> importedPackages;

    
    private Package()
    {   	
    }
    
    /**
     * Constructs a Package object for an AST node of type PACKAGE.
     * @param packageNode the AST node
     */
    public Package(AST packageNode)
    {
        localTypes = new HashMap<String, TypeInterface>();
        //importedTypes = new HashMap<String, TypeInterface>();
        importedPackages = new HashMap<String, Package>();
        setPackageName(packageNode);
        
        // If this is the first package, then it is root.
        if (root == null)
        {
            root = this;
        }
    }
    
    /**
     * Finds a package with a given name.
     * @param packageName   a fully qualified package name
     * @return the corresponding package, or null.
     */
    public static Package lookup(String packageName)
    {
        return nameToPackage.get(packageName);
    }
    
    /**
     * Finds a package for a given AST node of type PACKAGE.
     * @param node AST node
     * @return the corresponding package, or null.
     */
    public static Package lookup(AST node)
    {
        return nodeToPackage.get(node);
    }
    
    /**
     * Returns the root package, i.e. the one first seen by the parser.
     * @return root package
     */
    public static Package getRoot()
    {
        return root;
    }
    
    /**
     * Executes the link actions for all packages. In each package, the 
     * package imports must be resolved first.
     */
    public static void linkAll()
    {
        for (Package p : nameToPackage.values())
        {
            p.resolveImports();
            p.link(null);
        }
        //dumpAll();
    }
    
    // only for debugging
    public static void dumpAll()
    {
        for (Package p : nameToPackage.values())
        {
            System.out.println(p.getPackageName());
            for (String t : p.localTypes.keySet())
            {
                System.out.println("    " + t);
            }
        }
    }
    
    /**
     * Maps each imported package name to the corresponding Package object.
     */
    private void resolveImports()
    {
        for (String importedName : importedPackages.keySet())
        {
            Package importedPackage = nameToPackage.get(importedName);
            
            // The parser should have complained if there is no package for 
            // the given name. Thus this case should never occur, so we throw
            // an exception instead of logging an error.
            if (importedPackage == null)
            {
                //throw new RuntimeException("no package " + importedPackage);
                ToolContext.logError(this.node, "no package " + importedPackage);
            }
            importedPackages.put(importedName, importedPackage);
        }
    }

    /**
     * Returns all names of imported packages of the current package
     * @return set of names
     */
    public Set<String> getImportNames()
    {
        return importedPackages.keySet();
    }
    
    /**
     * Initializes the package name from the PACKAGE AST node.
     * @param n AST node.
     */
    private void setPackageName(AST n)
    {
        this.node = (TokenAST) n;
        String packageName = getPackageName();
        Package p = nameToPackage.get(packageName);
        if (p == null)
        {
            nameToPackage.put(packageName, this);
            nodeToPackage.put(node, this);
        }
        else
        {
            ToolContext.logError(node, "duplicate package " + packageName);
        }
    }
    
    /**
     * Stores a type symbol in the underlying Scope object <em>and</em> in the
     * local types map.
     * @param name      AST node for type name
     * @param typeNode  AST node for type definition
     */
    public void setTypeSymbol(AST name, Object typeNode)
    {   
        TypeInterface type = (TypeInterface) typeNode;
        localTypes.put(name.getText(), type);
        super.setSymbol(name, (TypeInterface) type);
    }
    
    /**
     * Finds a local type by name.
     * @param name   local name within the current package
     * @return type object
     */
    public TypeInterface getLocalType(String name)
    {
        return localTypes.get(name);
    }
    
    public Set<String> getLocalTypeNames()
    {
    	return localTypes.keySet();
    }

    /**
     * Adds a package import to the list of imported packages. A warning is 
     * emitted for multiple imports of the same package.
     * @param node  IMPORT AST node
     */
    public void addPackageImport(AST node)
    {
        boolean first = true;
        StringBuilder buffer = new StringBuilder();
        for (AST child = node.getFirstChild(); 
             child != null;
             child = child.getNextSibling())
        {
            if (first)
            {
                first = false;
            }
            else
            {
                buffer.append('.');
            }
            buffer.append(child.getText());
        }
        String packageName = buffer.toString();
        if (importedPackages.containsKey(packageName))
        {
            ToolContext.logWarning((TokenAST)node, "duplicate import of package " + packageName);
        }
        else
        {
            importedPackages.put(packageName, null);
        }
    }
    
    /**
     * Stores import of a single type (import com.acme.foo.MyType;).
     * TODO: Not yet implemented.
     * @param node IMPORT AST node
     */
    public void addSingleImport(TokenAST node)
    {
        
    }

    
    /**
     * Finds a type with a given name. If the type is not a local type, the
     * type will be looked up by its local name within each imported package.
     * 
     * An error is logged if the type name exists in more than one imported
     * package, but only if the type name does not exist in the current
     * package
     * @param name      local type name
     */
    public TypeInterface getType(String name)
    {
        TypeInterface result = localTypes.get(name);
        if (result == null)
        {
            for (Package p : importedPackages.values())
            {
                if (p == null)
                    continue;
                TypeInterface externalType = p.getLocalType(name);
                if (externalType != null)
                {
                    if (result == null)
                    {
                        result = externalType;
                    }
                    else
                    {
                        ToolContext.logError(null, "ambiguous type reference '"+ name + "'");
                        result = null;
                        break;
                    }
                }
            }
        }
        return result;
    }

    
    /**
     * Returns the fully qualified name of this package
     * @return e.g. "com.acme.foo.bar"
     */
    public String getPackageName()
    {
        if (packageName == null)
        {
            StringBuilder buffer = new StringBuilder();
            boolean first = true;
            for (String part : getPackagePath())
            {
                if (first)
                {
                    first = false;
                }
                else
                {
                    buffer.append('.');
                }
                buffer.append(part);
            }
            packageName = buffer.toString();
        }
        return packageName;
    }
    
    /**
     * Returns this list of subpackage names for the current package.
     * @return e.g. ["com", "acme", "foo", "bar"]
     */
    public List<String> getPackagePath()
    {
        if (packagePath == null)
        {
            packagePath = new ArrayList<String>();
            for (AST child = node.getFirstChild(); 
                child != null;  
                child = child.getNextSibling())
            {
                packagePath.add(child.getText());
            }
        }
        return packagePath;
    }
    
    public Package getPackage()
    {
        return this;
    }
    
    public boolean isUserDefined()
    {
    	return (this != BUILTIN) && (this != DEFAULT); 
    }
}
