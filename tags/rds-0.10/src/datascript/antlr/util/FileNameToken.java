/**
 * 
 */
package datascript.antlr.util;

import antlr.CommonToken;

/**
 * @author HWellmann
 *
 */
public class FileNameToken extends CommonToken
{
    private String fileName;
    public FileNameToken()
    {
        fileName = ToolContext.getFileName();
    }

    public FileNameToken(int t, String txt)
    {
        super(t, txt);
        fileName = ToolContext.getFileName();
    }

    public String getFilename()
    {
        return fileName;
    }
    
    public void setFilename(String name)
    {
        fileName = name;
    }
}
