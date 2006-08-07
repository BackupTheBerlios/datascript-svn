/*
 * Scope.java
 *
 * @author: Godmar Back
 * @version: $Id: Scope.java,v 1.1.1.1 2003/05/29 22:58:25 gback Exp $
 */
package datascript.ast;

import java.util.HashMap;
import java.util.Vector;

import antlr.collections.AST;
import datascript.tools.ToolContext;

public class Scope implements Context, LinkAction
{
    private HashMap symbolTable = new HashMap();

    private Context parentScope;
    private CompoundType owner;

    private Vector<LinkAction> linkActions = new Vector();

    public Scope()
    {
        this(null);
    }

    public Scope(Context parentScope)
    {
        this.parentScope = parentScope;
        if (parentScope != null)
        {
            parentScope.postLinkAction(this);
        }
    }

    public CompoundType getOwner()
    {
        return owner;
    }
    
    void setOwner(CompoundType owner)
    {
        this.owner = owner;
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

    /**
     * get this symbol from this scope
     */
    public Object getSymbolFromThis(String name)
    {
        Object obj = symbolTable.get(name);
        return obj;
    }

    /**
     * set this symbol, but throw SemanticException is symbol was already set in
     * this scope
     */
    public void setSymbol(AST node, Object symbol)
    {
        // Main.debug(this + ": defining " + name + " to be " +
        // symbol.getClass() + ":" + symbol);
        Object o = symbolTable.put(node.getText(), symbol);
        if (o != null)
            ToolContext.logError((TokenAST)node, "'" + node.getText()
                    + "' is already defined in this scope");
    }

    /**
     * set this symbol, discarding any previous setting (if any)
     */
    public void replaceSymbol(String name, Object symbol)
    {
        // Main.debug(this + ": redefining " + name + " to be " +
        // symbol.getClass() + ":" + symbol);
        symbolTable.put(name, symbol);
    }

    public Context getParentScope()
    {
        return parentScope;
    }

    public void postLinkAction(LinkAction act)
    {
        linkActions.addElement(act);
    }

    public void link(Context ctxt)
    {
        for (LinkAction l : linkActions)
        {
            l.link(this);
        }
    }
}
