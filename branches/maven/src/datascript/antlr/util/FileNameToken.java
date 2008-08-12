/**
 * 
 */


package datascript.antlr.util;


import antlr.CommonHiddenStreamToken;



/**
 * @author HWellmann
 * 
 */
public class FileNameToken extends CommonHiddenStreamToken
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


    @Override
    public String getFilename()
    {
        return fileName;
    }


    @Override
    public void setFilename(String name)
    {
        fileName = name;
    }
}
