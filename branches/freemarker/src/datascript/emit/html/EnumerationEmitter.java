package datascript.emit.html;

import java.io.File;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import datascript.ast.DataScriptException;
import datascript.ast.EnumItem;
import datascript.ast.EnumType;
import datascript.ast.TypeInterface;
import freemarker.template.Template;


public class EnumerationEmitter extends DefaultHTMLEmitter
{
    private EnumType enumeration;
    private List<EnumItem> items = new ArrayList<EnumItem>();

    
    public void emit(EnumType e)
    {
    	this.enumeration = e;
    	items.clear();
    	for (EnumItem item : e.getItems())
    	{
    		items.add(item);
    	}
    	try
    	{
    		Template tpl = cfg.getTemplate("html/enumeration.html.ftl");

    		directory = new File(directory, contentFolder);
            setCurrentFolder(contentFolder);        	
    		openOutputFile(directory, e.getName() + HTML_EXT);

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
    	return enumeration.getScope().getPackage().getPackageName();
    }
    
    public EnumType getType()
    {
    	return enumeration;
    }
    
    public String getBaseType()
    {
    	TypeInterface baseType = enumeration.getBaseType();
    	String baseTypeName = TypeNameEmitter.getTypeName(baseType);
    	return baseTypeName;
    }
    
    public Comment getDocumentation()
    {
    	Comment comment = new Comment();
    	String doc = enumeration.getDocumentation();
    	if (doc.length() > 0)
    		comment.parse(doc);
        return comment;
    }
    
    public Comment getItemDocumentation(EnumItem item)
    {
    	Comment comment = new Comment();
    	String doc = item.getDocumentation();
    	if (doc.length() > 0)
    		comment.parse(doc);
        return comment;
    }
    
    public List<EnumItem> getItems()
    {
    	return items;
    }
}
