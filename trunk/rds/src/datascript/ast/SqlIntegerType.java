/**
 * 
 */


package datascript.ast;


import antlr.Token;
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
        IntegerValue eight = new IntegerValue(8);
        IntegerValue size = bitsizeof(ctxt);
        if (size.remainder(eight).compareTo(new IntegerValue(0)) != 0) 
        {
            throw new RuntimeException("sizeof not integer: " + size);
        }
        return size.divide(eight);
    }


    public IntegerValue bitsizeof(Context ctxt)
    {
        IntegerValue size = new IntegerValue(0);

        for (int i = 0; i < fields.size(); i++)
        {
            Field fi = (Field) fields.elementAt(i);
            /*
             * TODO: try { StdIntegerType b =
             * StdIntegerType.getBuiltinType(fi.getFieldType()); if (b
             * instanceof BitFieldType) { size = size.add(new
             * IntegerValue(((BitFieldType) b) .getLength())); continue; } }
             * catch (ClassCastException _) { }
             */
            size = size.add(fi.bitsizeof(ctxt));
        }
        return size;
    }


    public boolean isMember(Context ctxt, Value val)
    {
        throw new UnsupportedOperationException("isMember not implemented in "
                + this.getClass().getName());
    }


    public String toString()
    {
        return "SQL_INTEGER";
    }


    public String getDocumentation()
    {
        String result = "";
        Token t = getHiddenBefore();
        if (t != null)
        {
            result = t.getText();
        }
        return result;
    }
}
