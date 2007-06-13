package datascript.emit.html;

import java.io.File;
import java.io.PrintWriter;
import java.io.Writer;

import antlr.collections.AST;
import datascript.ast.DataScriptException;
import datascript.ast.Package;
import datascript.ast.TypeInterface;
import datascript.emit.DefaultEmitter;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;

abstract public class DefaultHTMLEmitter extends DefaultEmitter
{
    protected Configuration cfg = new Configuration();
	
    public static final String contentFolder = "content";
    protected static final String HTML_EXT = ".html";
    protected File directory = new File("html");
    protected TypeInterface currentType;
    private String currentFolder = "/";
    protected Package currentPackage;

    public DefaultHTMLEmitter()
    {
    	cfg.setClassForTemplateLoading(getClass(), "../../..");
    	cfg.setObjectWrapper(new DefaultObjectWrapper());    	
    }
    
    public void setCurrentFolder(String currentFolder)
    {
        this.currentFolder = currentFolder;
    }


    public String getCurrentFolder()
    {
        return currentFolder;
    }


    public String getPackageName()
    {
        return currentPackage.getPackageName();
    }
    
    public String getRootPackageName()
    {
        return Package.getRoot().getPackageName();
    }
    
    public void beginPackage(AST p)
    {
    	currentPackage = Package.lookup(p);
    }    
    
    public void emitStylesheet()
    {
        emit("html/webStyles.css.ftl", "webStyles.css");
    }

    public void emitFrameset()
    {
        emit("html/index.html.ftl", "index.html");
    }

    public void emit(String template, String outputName)
    {
        try
        {
            Template tpl = cfg.getTemplate(template);
            openOutputFile(directory, outputName);

            Writer writer = new PrintWriter(out);
            tpl.process(this, writer);
            writer.close();
        }
        catch (Exception exc)
        {
            throw new DataScriptException(exc);
        }
    }    
}
