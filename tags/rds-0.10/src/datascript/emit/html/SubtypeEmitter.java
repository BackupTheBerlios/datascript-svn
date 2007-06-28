package datascript.emit.html;

import java.io.File;
import java.io.PrintWriter;
import java.io.Writer;

import datascript.ast.DataScriptException;
import datascript.ast.Subtype;
import datascript.ast.TypeInterface;
import freemarker.template.Template;


public class SubtypeEmitter extends DefaultHTMLEmitter
{
    private Subtype subtype;
    
    public void emit(Subtype s)
    {
    	this.subtype = s;
    	try
    	{
    		Template tpl = cfg.getTemplate("html/subtype.html.ftl");

    		directory = new File(directory, contentFolder);
            setCurrentFolder(contentFolder);        	
    		openOutputFile(directory, s.getName() + HTML_EXT);

    		Writer writer = new PrintWriter(out);
    		tpl.process(this, writer);
    		writer.close();
    	}
    	catch (Exception exc)
    	{
    		throw new DataScriptException(exc);
    	}
    }

    public String getPackageName()
    {
    	return subtype.getPackage().getPackageName();
    }
    
    public LinkedType getBaseType()
    {
    	TypeInterface baseType = subtype.getBaseType();
    	String pkg = baseType.getPackage().getPackageName();
    	LinkedType linkedType = new LinkedType(pkg, baseType);
    	return linkedType;
    }
    
    public Comment getDocumentation()
    {
    	Comment comment = new Comment();
    	String doc = subtype.getDocumentation();
    	if (doc.length() > 0)
    		comment.parse(doc);
        return comment;
    }
    
    public Subtype getType()
    {
    	return subtype;
    }
}
