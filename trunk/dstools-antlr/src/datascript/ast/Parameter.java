/*
 * Parameter.java
 *
 * @author: Godmar Back
 * @version: $Id: Parameter.java,v 1.1.1.1 2003/05/29 22:58:25 gback Exp $
 */
package datascript.ast;

/**
 * This class represents a parameter inside the body of a condition definition
 * or for a compound type
 */
public class Parameter
{
    String name;

    TypeInterface type;

    Parameter(String name, TypeInterface type)
    {
        this.name = name;
        this.type = type;
    }

    TypeInterface getType()
    {
        return (type);
    }

    String getName()
    {
        return (name);
    }

}
