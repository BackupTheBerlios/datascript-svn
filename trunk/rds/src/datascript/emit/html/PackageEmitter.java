package datascript.emit.html;

import antlr.collections.AST;
import datascript.jet.html.PackageBegin;
import datascript.jet.html.PackageEnd;
import datascript.jet.html.PackageItem;


public class PackageEmitter extends DefaultHTMLEmitter
{
    private PackageBegin beginTmpl = new PackageBegin();
    private PackageItem itemTmpl = new PackageItem();
    private PackageEnd endTmpl = new PackageEnd();


    @Override
    public void beginRoot(AST rootNode)
    {
        //setPackageName(rootNode.getFirstChild().getFirstChild());

        openOutputFile(directory, "packages" + HTML_EXT);
        out.print(beginTmpl.generate(this));
    }

    @Override
    public void endRoot()
    {
        out.print(endTmpl.generate(this));
        out.close();
    }

    @Override
    public void endPackage(AST p)
    {
        out.print(itemTmpl.generate(this));
    }

}
