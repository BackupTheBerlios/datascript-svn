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
    public void beginTranslationUnit()
    {
        setPackageName(getPackageNode());

        openOutputFile(directory, "packages" + HTML_EXT);
        out.print(beginTmpl.generate(this));
    }


    @Override
    public void endTranslationUnit()
    {
        out.print(endTmpl.generate(this));
        out.close();
    }


    @Override
    public void beginPackage(AST p)
    {
        setPackageName(p);

        out.print(itemTmpl.generate(this));
    }


    @Override
    public void endPackage(AST p)
    {
    }


    @Override
    public void beginEnumItem(AST e)
    {
        // TODO Auto-generated method stub

    }


    @Override
    public void beginEnumeration(AST e)
    {
        // TODO Auto-generated method stub

    }


    @Override
    public void beginField(AST f)
    {
        // TODO Auto-generated method stub

    }


    @Override
    public void beginImport(AST rootNode)
    {
        // TODO Auto-generated method stub

    }


    @Override
    public void beginSequence(AST s)
    {
        // TODO Auto-generated method stub

    }


    @Override
    public void beginSqlDatabase(AST s)
    {
        // TODO Auto-generated method stub

    }


    @Override
    public void beginSqlInteger(AST s)
    {
        // TODO Auto-generated method stub

    }


    @Override
    public void beginSqlMetadata(AST s)
    {
        // TODO Auto-generated method stub

    }


    @Override
    public void beginSqlPragma(AST s)
    {
        // TODO Auto-generated method stub

    }


    @Override
    public void beginSqlTable(AST s)
    {
        // TODO Auto-generated method stub

    }


    @Override
    public void beginSubtype(AST s)
    {
        // TODO Auto-generated method stub

    }


    @Override
    public void beginUnion(AST u)
    {
        // TODO Auto-generated method stub

    }


    @Override
    public void endEnumItem(AST e)
    {
        // TODO Auto-generated method stub

    }


    @Override
    public void endEnumeration(AST e)
    {
        // TODO Auto-generated method stub

    }


    @Override
    public void endField(AST f)
    {
        // TODO Auto-generated method stub

    }


    @Override
    public void endImport()
    {
        // TODO Auto-generated method stub

    }


    @Override
    public void endSequence(AST s)
    {
        // TODO Auto-generated method stub

    }


    @Override
    public void endSqlDatabase(AST s)
    {
        // TODO Auto-generated method stub

    }


    @Override
    public void endSqlInteger(AST s)
    {
        // TODO Auto-generated method stub

    }


    @Override
    public void endSqlMetadata(AST s)
    {
        // TODO Auto-generated method stub

    }


    @Override
    public void endSqlPragma(AST s)
    {
        // TODO Auto-generated method stub

    }


    @Override
    public void endSqlTable(AST s)
    {
        // TODO Auto-generated method stub

    }


    @Override
    public void endSubtype(AST s)
    {
        // TODO Auto-generated method stub

    }


    @Override
    public void endUnion(AST u)
    {
        // TODO Auto-generated method stub

    }

}
