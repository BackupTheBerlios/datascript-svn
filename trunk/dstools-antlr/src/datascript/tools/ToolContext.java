/**
 * 
 */
package datascript.tools;

import datascript.ast.TokenAST;

/**
 * @author HWellmann
 *
 */
public class ToolContext
{
    private String fileName;
    private int numWarnings;
    private int numErrors;
    private static ToolContext singleton;
    
    private ToolContext()
    {       
    }
    
    public static ToolContext getInstance()
    {
        if (singleton == null)
        {
            singleton = new ToolContext();
        }
        return singleton;
    }
    
    public void setFileName(String fileName)
    {
        this.fileName = fileName;
    }
    
    public String getFileName()
    {
        return fileName;
    }
    public int getErrorCount()
    {
        return numErrors;
    }
    
    public int getWarningCount()
    {
        return numWarnings;
    }
    
    public void warning(TokenAST token, String text)
    {
        numWarnings++;
        StringBuffer message = new StringBuffer();
        appendLocation(message, token);
        message.append("warning: ");
        message.append(text);
        System.err.println(message);
    }
    
    public void error(TokenAST token, String text)
    {
        numErrors++;
        StringBuffer message = new StringBuffer();
        appendLocation(message, token);
        message.append(text);
        System.err.println(message);
    }

    public static void logError(TokenAST token, String text)
    {
        getInstance().error(token, text);
    }
    
    public static void logWarning(TokenAST token, String text)
    {
        getInstance().warning(token, text);
    }
    
    private void appendLocation(StringBuffer message, TokenAST token)
    {
        if (fileName != null)
        {
            message.append(fileName);
            message.append(":");
        }
        if (token != null)
        {
            message.append(token.getLine());
            message.append(":");
            message.append(token.getColumn());
            message.append(": ");
        }
    }
}
