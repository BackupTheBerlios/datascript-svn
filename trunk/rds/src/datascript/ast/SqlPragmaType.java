/**
 * 
 */


package datascript.ast;


import datascript.antlr.util.TokenAST;



/**
 * @author HWellmann
 * 
 */
public class SqlPragmaType extends CompoundType
{
    @SuppressWarnings("unused")
    private TokenAST sqlConstraint;


    public void setSqlConstraint(TokenAST s)
    {
        sqlConstraint = s;
    }


    public IntegerValue sizeof(Context ctxt)
    {
        throw new UnsupportedOperationException("sizeof not implemented");
    }


    public IntegerValue bitsizeof(Context ctxt)
    {
        throw new UnsupportedOperationException("bitsizeof not implemented");
    }


    public boolean isMember(Context ctxt, Value val)
    {
        throw new UnsupportedOperationException("isMember not implemented");
    }


    public String toString()
    {
        return "SQL_PRAGMA";
    }
}
