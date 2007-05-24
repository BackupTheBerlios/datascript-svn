package datascript.emit.html;

import java.util.HashSet;
import java.util.Set;

import antlr.collections.AST;
import datascript.ast.TokenAST;
import datascript.ast.TypeInterface;
import datascript.jet.html.OverviewBegin;
import datascript.jet.html.OverviewEnd;
import datascript.jet.html.OverviewItem;


public class OverviewEmitter extends DefaultHTMLEmitter
{
    private OverviewBegin beginTmpl = new OverviewBegin();
    private OverviewItem itemTmpl = new OverviewItem();
    private OverviewEnd endTmpl = new OverviewEnd();

    private HashSet<String> packageNames = new HashSet<String>();
    private String packageName;

    public Set<String> getPackageNames()
    {
        return packageNames;
    }

    

    @Override
    public void beginRoot(AST rootNode)
    {
    	typeMap.clear();
        openOutputFile(directory, "overview" + HTML_EXT);
        out.print(beginTmpl.generate(this));
    }

    public void endPackage(AST p)
    {
		String pkgName = currentPackage.getPackageName();
    	for (String typeName : currentPackage.getLocalTypeNames())
    	{
    		TypeInterface t = currentPackage.getLocalType(typeName);
    		TokenAST type = (TokenAST) t;
    		Pair<String, TokenAST> entry = new Pair<String, TokenAST>(pkgName, type);    		
    		typeMap.put(typeName, entry);
    	}
    	packageNames.add(pkgName);
    }

    @Override
    public void endRoot()
    {
        for (Pair<String, TokenAST> p : typeMap.values())
        {
            packageName = p.getFirst();
            currentType = (TypeInterface)p.getSecond();
            out.print(itemTmpl.generate(this));
        }
        out.print(endTmpl.generate(this));
        out.close();
    }

    public String getPackageName()
    {
    	return packageName;
    }

}
