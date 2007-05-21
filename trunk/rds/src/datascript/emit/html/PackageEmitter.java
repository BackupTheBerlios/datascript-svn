package datascript.emit.html;

import antlr.collections.AST;
import datascript.ast.TokenAST;
import datascript.jet.html.PackageBegin;
import datascript.jet.html.PackageEnd;
import datascript.jet.html.PackageItem;


public class PackageEmitter extends DefaultHTMLEmitter
{
    private PackageBegin beginTmpl = new PackageBegin();
    private PackageItem itemTmpl = new PackageItem();
    private PackageEnd endTmpl = new PackageEnd();


    @Override
    public void beginTranslationUnit(AST rootNode, AST unitNode)
    {
        setPackageName(unitNode);

        openOutputFile(directory, "packages" + HTML_EXT);
        out.print(beginTmpl.generate(this));
    }


    @Override
    public void endTranslationUnit()
    {
        for (Pair<String, TokenAST> p : typeMap.values())
        {
            //TokenAST type = (TokenAST) p.getSecond();
            setPackageName(p.getFirst());
            out.print(itemTmpl.generate(this));
        }
        out.print(endTmpl.generate(this));
        out.close();
    }


    @Override
    public void beginPackage(AST pack)
    {
        //setPackageName(getPackageNode());
        setPackageName(pack);
        Pair<String, TokenAST> p = new Pair<String, TokenAST>(getPackageName(), (TokenAST)pack);
        typeMap.put(getPackageName(), p);
    }


    @Override
    public void endPackage(AST p)
    {
    }

}
