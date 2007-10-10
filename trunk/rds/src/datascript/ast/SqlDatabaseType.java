/**
 * 
 */


package datascript.ast;


import antlr.Token;
import antlr.collections.AST;
import datascript.antlr.DataScriptParserTokenTypes;
import datascript.antlr.util.TokenAST;



/**
 * @author HWellmann
 * 
 */
public class SqlDatabaseType extends CompoundType
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
        throw new UnsupportedOperationException("sizeof not implemented");
    }


    public boolean isMember(Context ctxt, Value val)
    {
        throw new UnsupportedOperationException("isMember not implemented");
    }


    public SqlPragmaType getPragma()
    {
        AST node = findFirstChildOfType(DataScriptParserTokenTypes.SQL_PRAGMA);
        return (SqlPragmaType) node;
    }


    public SqlMetadataType getMetadata()
    {
        AST node = findFirstChildOfType(DataScriptParserTokenTypes.SQL_METADATA);
        return (SqlMetadataType) node;
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


    public String toString()
    {
        return "SQL_DATABASE";
    }
}
