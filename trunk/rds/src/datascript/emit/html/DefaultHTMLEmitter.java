package datascript.emit.html;

import java.io.File;
import java.util.SortedMap;
import java.util.TreeMap;

import antlr.collections.AST;
import datascript.antlr.DataScriptParserTokenTypes;
import datascript.antlr.util.TokenAST;
import datascript.ast.TypeInterface;
import datascript.emit.DefaultEmitter;
import datascript.ast.Package;

abstract public class DefaultHTMLEmitter extends DefaultEmitter
{
    protected class Pair<A, B>
    {
        private A first;
        private B second;

        Pair(A a, B b)
        {
            first = a;
            second = b;
        }
        
        public A getFirst()
        {
            return first;
        }
        
        public B getSecond()
        {
            return second;
        }
    }


    public static final String contentFolder = "content";
    protected static final String HTML_EXT = ".html";
    protected File directory = new File("html");
    protected TypeInterface currentType;
    protected SortedMap<String, Pair<String, TokenAST> > typeMap = 
        new TreeMap<String, Pair<String, TokenAST> >();
    private String packageName;
    private String currentFolder = "/";
    protected Package currentPackage;


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


    public void setPackageName(String packageName)
    {
        this.packageName = packageName;
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
    
}
