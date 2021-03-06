package datascript.emit.html;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import antlr.collections.AST;
import datascript.ast.DataScriptException;
import freemarker.template.Template;

public class PackageEmitter extends DefaultHTMLEmitter
{
    private List<String> packages = new ArrayList<String>();

    @Override
    public void endRoot()
    {
        try
        {
            Template tpl = cfg.getTemplate("html/package.html.ftl");
            openOutputFile(directory, "packages" + HTML_EXT);
            Writer writer = new PrintWriter(out);
            tpl.process(this, writer);
            writer.close();
        }
        catch (Exception exc)
        {
            throw new DataScriptException(exc);
        }
    }

    @Override
    public void endPackage(AST p)
    {
        packages.add(getPackageName());
    }

    public List<String> getPackages()
    {
        return packages;
    }

}
