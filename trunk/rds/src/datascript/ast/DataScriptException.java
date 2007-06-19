/**
 * 
 */
package datascript.ast;

/**
 * @author HWellmann
 *
 */
public class DataScriptException extends RuntimeException
{
    public DataScriptException()
    {
        
    }
    
    public DataScriptException(String text)
    {
        super(text);
    }

    public DataScriptException(Throwable exc)
    {
        super(exc);
    }
}
