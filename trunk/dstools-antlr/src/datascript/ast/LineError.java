/*
 * LineError.java
 *
 * @author: Godmar Back
 * @version: $Id: LineError.java,v 1.1.1.1 2003/05/29 22:58:25 gback Exp $
 */
package datascript.ast;


public class LineError extends Error
{
    private TokenAST n;

    private String filename;

    static String currentFile;

    public LineError()
    {
        super();
    }

    public LineError(String s)
    {
        super(s);
    }

    public LineError(TokenAST n, String s)
    {
        super(s);
        this.n = n;
        filename = currentFile;
    }

    public String toString()
    {
        if (n == null)
            return super.toString();
        return getClass().toString().substring("class datascript.ast.".length())
                + " in file " + filename + " at line " + n.getLine()
                + ", column " + n.getColumn() + ": " + getMessage();
    }
}
