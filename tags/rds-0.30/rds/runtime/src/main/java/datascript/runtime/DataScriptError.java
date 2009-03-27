package datascript.runtime;

public class DataScriptError extends Error
{

    /**
     * 
     */
    private static final long serialVersionUID = 921445202560692775L;
    
    public DataScriptError()
    {
        
    }
    
    public DataScriptError(String msg)
    {
        super(msg);
    }
    
    public DataScriptError(String msg, Throwable throwable)
    {
        super(msg, throwable);
    }
    
    public DataScriptError(Throwable throwable)
    {
        super(throwable);
    }
}
