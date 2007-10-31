/**
 * 
 */
package datascript.ast;

/**
 * @author HWellmann
 *
 */
public class ParserException extends DataScriptException
{
    public ParserException()
    {
        
    }
    
    public ParserException(String text)
    {
        super(text);
    }
}
