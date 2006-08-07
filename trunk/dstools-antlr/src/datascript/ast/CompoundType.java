/*
 * CompoundType.java
 *
 * @author: Godmar Back
 * @version: $Id: CompoundType.java,v 1.2 2003/06/19 19:53:34 gback Exp $
 */
package datascript.ast;

import java.util.Iterator;
import java.util.Stack;
import java.util.Vector;

import datascript.antlr.DataScriptParserTokenTypes;

import antlr.collections.AST;

abstract public class CompoundType extends TokenAST implements TypeInterface
{
    Vector<Field> fields = new Vector<Field>();

    Vector nestedTypes = new Vector();

    Vector conditions = new Vector();

    // / enum and bitmasks lexically contained in this compound
    Vector settypes = new Vector();

    Vector<String> parameters = null;

    // / set of compound types that can contain this type
    Vector containers = new Vector();

    // / one of TypeInterface.NOBYTEORDER, BIGENDIAN, LITTLEENDIAN
    int byteOrder;

    private Scope scope;

    private String name;

    private String doc;

    private CompoundType parent;

    abstract public IntegerValue sizeof(Context ctxt);

    abstract public boolean isMember(Context ctxt, Value val);

    protected boolean bfoComputed = false;


    protected CompoundType()
    {
        
    }
    
    CompoundType getParent()
    {
        return parent;
    }

    void addParameter(String param)
    {
        if (parameters == null)
        {
            parameters = new Vector<String>();
        }
        parameters.addElement(param);
    }

    Iterator getParameters()
    {
        return parameters.iterator();
    }

    public String getParameterAt(int index)
    {
        return parameters.elementAt(index);
    }
    
    int getParameterCount()
    {
        return (parameters == null) ? 0 : parameters.size();
    }
    
    

    boolean isEmpty()
    {
        return fields.size() == 0 && conditions.size() == 0
                && nestedTypes.size() == 0;
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
        return doc;
    }

    void addContainer(CompoundType f)
    {
        if (!containers.contains(f))
        {
            containers.addElement(f);
        }
    }

 
    /**
     * @return true if 'this' is contained in compound type 'f'
     */
    boolean isContainedIn(CompoundType f)
    {
        return isContainedIn(f, new Stack());
    }

    /**
     * The "is contained" relationship may contain cycles use a stack to avoid
     * them. This is a simple DFS path finding algorithm that finds a path from
     * 'this' to 'f'.
     */
    boolean isContainedIn(CompoundType f, Stack seen)
    {
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
                if (c.isContainedIn(f, seen))
                {
                    return true;
                }
                seen.pop();
            }
        }
        return false;
    }


    public Iterable<Field> getFields()
    {
        /*
        return new Iterable<Field>() {
            public Iterator<Field> iterator()
            {
                return new ChildIterator<Field>(CompoundType.this);
            }
        };
        */
        return fields;
    }

    public void addField(Field f)
    {
        // TODO: Main.assertThat(!fields.contains(f));
        fields.addElement(f);
    }

    public Field getField(int i)
    {
        return fields.elementAt(i);
    }

    public Scope getScope()
    {
        return scope;
    }

    public void setScope(Scope scope)
    {
        this.scope = scope;
        scope.setOwner(this);
    }

    public String toString()
    {
        return name;
    }
/*
    public void link()
    {
        scope.link(null);
        resolveParameters();
    }
    Iterator getSetTypes()
    {
        return settypes.iterator();
    }

    void addSetType(SetType stype)
    {
        // TODO: Main.assertThat(!settypes.contains(stype));
        settypes.addElement(stype);
    }


    Iterable<TypeInterface> getNestedTypes()
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

    void addCondition(Condition c) { conditions.addElement(c); }
    
    int getByteOrder()
    {
        return byteOrder;
    }

    void setByteOrder(int byteOrder)
    {
        this.byteOrder = byteOrder;
    }
*/
    public Value castFrom(Value val)
    {
        throw new Error("casting compounds not implemented");
    }

    
    public void storeParameters()
    {
        AST node = getFirstChild().getNextSibling();
        if (node.getType() == DataScriptParserTokenTypes.PARAM)
        {
            parameters = new Vector(node.getNumberOfChildren()/2);
            AST p = node.getFirstChild();
            while (p != null)
            {
                AST pname = p.getNextSibling();
                parameters.addElement(pname.getText());
                p = pname.getNextSibling();                
            }
        }
    }
    public int getLength()
    {
        throw new InternalError("not implemented");
    }
    
    public Expression getLengthExpression()
    {
        throw new InternalError("not implemented");
    }

}
