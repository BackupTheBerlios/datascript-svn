/**
 * 
 */
package datascript.ast;

import datascript.antlr.DataScriptParserTokenTypes;
import datascript.antlr.util.TokenAST;
import antlr.collections.AST;

/**
 * @author HWellmann
 *
 */
public class SqlDatabaseType extends CompoundType
{
    private TokenAST sqlConstraint;
    
    public void setSqlConstraint(TokenAST s)
    {
        sqlConstraint = s;
    }

    public IntegerValue sizeof(Context ctxt)
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
        return (SqlPragmaType)node;
    }
    
    public SqlMetadataType getMetadata()
    {
        AST node = findFirstChildOfType(DataScriptParserTokenTypes.SQL_METADATA);
        return (SqlMetadataType)node;        
    }

    public String getDocumentation()
    {
        String result = "";
        AST n = findFirstChildOfType(DataScriptParserTokenTypes.DOC);
        if (n != null)
        {
            result = n.getText();
        }
        return result;
    }

    public String toString()
    {
        return "SQL_DATABASE";
    }
}
