package datascript.emit.html;

import datascript.ast.SequenceType;
import datascript.ast.UnionType;
import datascript.ast.EnumType;
import datascript.ast.Subtype;
import datascript.ast.SqlDatabaseType;
import datascript.ast.SqlTableType;
import datascript.ast.SqlPragmaType;
import datascript.ast.SqlMetadataType;
import datascript.ast.SqlIntegerType;

import datascript.jet.html.OverviewBegin;
import datascript.jet.html.OverviewEnd;
import datascript.jet.html.OverviewItem;

import antlr.collections.AST;


public class OverviewEmitter extends DefaultHTMLEmitter
{
    private OverviewBegin beginTmpl = new OverviewBegin();
    private OverviewItem itemTmpl = new OverviewItem();
    private OverviewEnd endTmpl = new OverviewEnd();



    @Override
    public void beginTranslationUnit()
    {
        setPackageName(getPackageNode());

        openOutputFile(directory, "overview" + HTML_EXT);
        out.print(beginTmpl.generate(this));
    }


    @Override
    public void endTranslationUnit()
    {
        out.print(endTmpl.generate(this));
        out.close();
    }


    @Override
    public void beginSequence(AST s)
    {
        setPackageName(getPackageNode());

        currentType = (SequenceType)s;
        out.print(itemTmpl.generate(this));
    }


    @Override
    public void endSequence(AST s)
    {
    }


    @Override
    public void beginSqlDatabase(AST s)
    {
        setPackageName(getPackageNode());

        currentType = (SqlDatabaseType)s;
        out.print(itemTmpl.generate(this));
    }


    @Override
    public void endSqlDatabase(AST s)
    {
    }


    @Override
    public void beginUnion(AST u)
    {
        setPackageName(getPackageNode());

        currentType = (UnionType)u;
        out.print(itemTmpl.generate(this));
    }


    @Override
    public void endUnion(AST u)
    {
    }


    @Override
    public void beginEnumeration(AST e)
    {
        setPackageName(getPackageNode());

        currentType = (EnumType)e;
        out.print(itemTmpl.generate(this));
    }


    @Override
    public void endEnumeration(AST e)
    {
    }


    @Override
    public void beginSubtype(AST s)
    {
        setPackageName(getPackageNode());

        currentType = (Subtype)s;
        out.print(itemTmpl.generate(this));
    }


    @Override
    public void endSubtype(AST s)
    {
    }


    @Override
    public void beginSqlTable(AST s)
    {
        setPackageName(getPackageNode());

        currentType = (SqlTableType)s;
        out.print(itemTmpl.generate(this));
    }


    @Override
    public void endSqlTable(AST s)
    {
    }


    @Override
    public void beginSqlPragma(AST s)
    {
        setPackageName(getPackageNode());

        currentType = (SqlPragmaType)s;
        out.print(itemTmpl.generate(this));
    }


    @Override
    public void endSqlPragma(AST s)
    {
    }


    @Override
    public void beginSqlMetadata(AST s)
    {
        setPackageName(getPackageNode());

        currentType = (SqlMetadataType)s;
        out.print(itemTmpl.generate(this));
    }


    @Override
    public void endSqlMetadata(AST s)
    {
    }


    @Override
    public void beginSqlInteger(AST s)
    {
        setPackageName(getPackageNode());

        currentType = (SqlIntegerType)s;
        out.print(itemTmpl.generate(this));
    }


    @Override
    public void endSqlInteger(AST s)
    {
    }


    @Override
    public void beginEnumItem(AST e)
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
    public void beginPackage(AST p)
    {
        // TODO Auto-generated method stub

    }


    @Override
    public void endEnumItem(AST e)
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
    public void endPackage(AST p)
    {
        // TODO Auto-generated method stub

    }

}
