/**
 * 
 */
package datascript.ast;

import datascript.antlr.util.TokenAST;

/**
 * @author HWellmann
 *
 */
public class SqlIntegerType extends CompoundType
{
    private TokenAST sqlConstraint;
    
    public SqlIntegerType()
    {        
    }
    
    public void setSqlConstraint(TokenAST s)
    {
        sqlConstraint = s;
    }
    
    public TokenAST getSqlConstraint()
    {
        return sqlConstraint;
    }

    public IntegerValue sizeof(Context ctxt)
    {
        //throw new UnsupportedOperationException("sizeof not implemented in " + this.getClass().getName());
        IntegerValue size = new IntegerValue(0);
        IntegerValue eight = new IntegerValue(8);

        for (int i = 0; i < fields.size(); i++)
        {
            Field fi = (Field) fields.elementAt(i);
            /* TODO:
            try
            {
                StdIntegerType b = StdIntegerType.getBuiltinType(fi.getFieldType());
                if (b instanceof BitFieldType)
                {
                    size = size.add(new IntegerValue(((BitFieldType) b)
                            .getLength()));
                    continue;
                }
            }
            catch (ClassCastException _)
            {
            }
                */
            size = size.add(fi.sizeof(ctxt).multiply(eight));
        }
        return size.divide(eight);
    }

    public boolean isMember(Context ctxt, Value val)
    {
        throw new UnsupportedOperationException("isMember not implemented in " + this.getClass().getName());
    }

    public String toString()
    {
        return "SQL_INTEGER";
    }
}
