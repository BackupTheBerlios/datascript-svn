package datascript.emit.html;

import java.io.File;

import antlr.collections.AST;
import datascript.antlr.DataScriptParserTokenTypes;
import datascript.ast.TypeInterface;
import datascript.emit.DefaultEmitter;


abstract public class DefaultHTMLEmitter extends DefaultEmitter
{
    public static final String contentFolder = "content";
    protected static final String HTML_EXT = ".html";
    protected File directory = new File("html");
    protected TypeInterface currentType;
    private String packageName;
    private String currentFolder = "/";


    public void setCurrentFolder(String currentFolder)
    {
        this.currentFolder = currentFolder;
    }


    public String getCurrentFolder()
    {
        return currentFolder;
    }


    public void setCurrentType(TypeInterface type)
    {
        currentType = type;
    }


    public TypeInterface getCurrentType()
    {
        return currentType;
    }


    public void setPackageName(AST packageNode)
    {
        if (packageNode != null && packageNode.getType() == DataScriptParserTokenTypes.PACKAGE)
        {
            AST sibling = packageNode.getFirstChild();
            String fileName = sibling.getText();
            while (true)
            {
                sibling = sibling.getNextSibling();
                if (sibling == null)
                    break;
                
                fileName = fileName + "." + sibling.getText();
            }
            
            setPackageName(fileName);
        }
    }


    public void setPackageName(String packageName)
    {
        this.packageName = packageName;
    }


    public String getPackageName()
    {
        return packageName;
    }
}
