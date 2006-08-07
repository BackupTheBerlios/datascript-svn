/*
 * ComputeError.java
 *
 * @author: Godmar Back
 * @version: $Id: ComputeError.java,v 1.1.1.1 2003/05/29 22:58:25 gback Exp $
 */
package datascript.ast;

public class ComputeError extends LineError
{
    public ComputeError()
    {
        super();
    }

    public ComputeError(String s)
    {
        super(s);
    }
    
    public ComputeError(TokenAST ast, String s)
    {
        super(ast, s);
    }
}
