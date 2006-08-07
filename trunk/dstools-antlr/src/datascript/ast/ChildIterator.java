/**
 * 
 */
package datascript.ast;

import java.util.Iterator;

import antlr.collections.AST;

/**
 * @author HWellmann
 *
 */
public class ChildIterator<E> implements Iterator<E>
{
    private AST child;
    
    public ChildIterator(AST parent)
    {
        this.child = parent.getFirstChild();
    }
    
    public boolean hasNext()
    {
        return child != null;
    }
    
    public E next()
    {
        child = child.getNextSibling();
        return (E)child;
    }
    
    public void remove()
    {
        throw new UnsupportedOperationException();
    }
    
}
