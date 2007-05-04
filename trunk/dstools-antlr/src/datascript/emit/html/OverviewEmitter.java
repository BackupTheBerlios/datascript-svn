package datascript.emit.html;

import java.util.HashSet;
import java.util.Set;

import datascript.ast.SequenceType;
import datascript.ast.TypeInterface;
import datascript.ast.TokenAST;
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

    private HashSet<String> packageNames = new HashSet<String>();



    public Set<String> getPackageNames()
    {
        return packageNames;
    }

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
        for (Pair<String, TokenAST> p : typeMap.values())
        {
            setPackageName(p.getFirst());
            currentType = (TypeInterface)p.getSecond();
            out.print(itemTmpl.generate(this));
        }
        out.print(endTmpl.generate(this));
        out.close();
    }


    @Override
    public void beginPackage(AST p)
    {
        packageNames.add(getIDName(p.getFirstChild()));
    }


    @Override
    public void endPackage(AST p)
    {
    }


    @Override
    public void beginSequence(AST s)
    {
        setPackageName(getPackageNode());
        Pair<String, TokenAST> p = new Pair<String, TokenAST>(getPackageName(), (TokenAST)s);
        typeMap.put(((SequenceType)s).getName(), p);
    }


    @Override
    public void endSequence(AST s)
    {
    }


    @Override
    public void beginSqlDatabase(AST s)
    {
        setPackageName(getPackageNode());
        Pair<String, TokenAST> p = new Pair<String, TokenAST>(getPackageName(), (TokenAST)s);
        typeMap.put(((SqlDatabaseType)s).getName(), p);
    }


    @Override
    public void endSqlDatabase(AST s)
    {
    }


    @Override
    public void beginUnion(AST u)
    {
        setPackageName(getPackageNode());
        Pair<String, TokenAST> p = new Pair<String, TokenAST>(getPackageName(), (TokenAST)u);
        typeMap.put(((UnionType)u).getName(), p);
    }


    @Override
    public void endUnion(AST u)
    {
    }


    @Override
    public void beginEnumeration(AST e)
    {
        setPackageName(getPackageNode());
        Pair<String, TokenAST> p = new Pair<String, TokenAST>(getPackageName(), (TokenAST)e);
        typeMap.put(((EnumType)e).getName(), p);
    }


    @Override
    public void endEnumeration(AST e)
    {
    }


    @Override
    public void beginSubtype(AST s)
    {
        setPackageName(getPackageNode());
        Pair<String, TokenAST> p = new Pair<String, TokenAST>(getPackageName(), (TokenAST)s);
        typeMap.put(((Subtype)s).getName(), p);
    }


    @Override
    public void endSubtype(AST s)
    {
    }


    @Override
    public void beginSqlTable(AST s)
    {
        setPackageName(getPackageNode());
        Pair<String, TokenAST> p = new Pair<String, TokenAST>(getPackageName(), (TokenAST)s);
        typeMap.put(((SqlTableType)s).getName(), p);
    }


    @Override
    public void endSqlTable(AST s)
    {
    }


    @Override
    public void beginSqlPragma(AST s)
    {
        setPackageName(getPackageNode());
        Pair<String, TokenAST> p = new Pair<String, TokenAST>(getPackageName(), (TokenAST)s);
        typeMap.put(((SqlPragmaType)s).getName(), p);
    }


    @Override
    public void endSqlPragma(AST s)
    {
    }


    @Override
    public void beginSqlMetadata(AST s)
    {
        setPackageName(getPackageNode());
        Pair<String, TokenAST> p = new Pair<String, TokenAST>(getPackageName(), (TokenAST)s);
        typeMap.put(((SqlMetadataType)s).getName(), p);
    }


    @Override
    public void endSqlMetadata(AST s)
    {
    }


    @Override
    public void beginSqlInteger(AST s)
    {
        setPackageName(getPackageNode());
        Pair<String, TokenAST> p = new Pair<String, TokenAST>(getPackageName(), (TokenAST)s);
        typeMap.put(((SqlIntegerType)s).getName(), p);
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

}
