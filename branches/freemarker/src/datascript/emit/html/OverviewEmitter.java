package datascript.emit.html;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import antlr.collections.AST;
import datascript.antlr.util.TokenAST;
import datascript.ast.DataScriptException;
import datascript.ast.TypeInterface;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;


public class OverviewEmitter extends DefaultHTMLEmitter
{
	protected Configuration cfg = new Configuration();
	private List<String> packages = new ArrayList<String>();
	private Map<String, LinkedType> typeMap = new TreeMap<String, LinkedType>();

    private HashSet<String> packageNames = new HashSet<String>();
    
    private String packageName;

    public Set<String> getPackageNames()
    {
        return packageNames;
    }

    @Override
    public void beginRoot(AST rootNode)
    {
    	cfg.setClassForTemplateLoading(getClass(), "../../..");
    	cfg.setObjectWrapper(new DefaultObjectWrapper());
    }

    @Override
    public void endRoot()
    {
    	try
    	{
    		Template tpl = cfg.getTemplate("html/overview.html.ftl");
    		openOutputFile(directory, "overview" + HTML_EXT);
    		Writer writer = new PrintWriter(out);
    		tpl.process(this, writer);
    		writer.close();
    	}
    	catch (Exception exc)
    	{
    		throw new DataScriptException(exc);
    	}
    }

    

    public void endPackage(AST p)
    {
		String pkgName = currentPackage.getPackageName();
    	for (String typeName : currentPackage.getLocalTypeNames())
    	{
    		TypeInterface t = currentPackage.getLocalType(typeName);
    		//TokenAST type = (TokenAST) t;
    		LinkedType linkedType = new LinkedType(pkgName, t);
    		typeMap.put(typeName, linkedType);
    	}
    	pkgName = pkgName.replace('.', '_');
    	packageNames.add(pkgName);
    }


    public String getPackageName()
    {
    	return packageName;
    }
    
    public Collection<LinkedType> getTypes()
    {
    	return typeMap.values();
    }
    
    public Set<String> getPackages()
    {
    	return packageNames;
    }    
}
