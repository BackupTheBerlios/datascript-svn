package datascript.emit.html;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import datascript.ast.DataScriptException;
import datascript.ast.EnumItem;
import datascript.ast.EnumType;
import datascript.ast.SetType;
import datascript.ast.TypeInterface;
import datascript.jet.html.Comment;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;


public class EnumerationEmitter extends DefaultHTMLEmitter
{
	protected Configuration cfg = new Configuration();
	private Map<String, LinkedType> typeMap = new TreeMap<String, LinkedType>();

    private HashSet<String> packageNames = new HashSet<String>();
    
    private EnumType enumeration;
    private List<EnumItem> items = new ArrayList<EnumItem>();

    
    public EnumerationEmitter()
    {
    	cfg.setClassForTemplateLoading(getClass(), "../../..");
    	cfg.setObjectWrapper(new DefaultObjectWrapper());    	
    }
    
    public Set<String> getPackageNames()
    {
        return packageNames;
    }


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
    
    public String getDocumentation(SetType settype)
    {
        return getDocumentation(settype.getDocumentation());
    }
    
    public String getDocumentation(EnumItem item)
    {
        return getDocumentation(item.getDocumentation());
    }
    
    private String getDocumentation(String doc)
    {
        if (doc.equals(""))
            return doc;

        Comment commentGenerator = new Comment();
        return commentGenerator.generate(doc);
    }
    
    public EnumerationEmitter getSelf()
    {
    	return this;
    }
    
    public List<EnumItem> getItems()
    {
    	return items;
    }
}
