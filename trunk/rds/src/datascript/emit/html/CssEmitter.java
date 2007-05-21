package datascript.emit.html;

import antlr.collections.AST;
import datascript.jet.html.CSS;

public class CssEmitter extends DefaultHTMLEmitter
{
    private CSS cssTmpl = new CSS();


    @Override
    public void beginTranslationUnit(AST rootNode, AST unitNode)
    {
        openOutputFile(directory, "webStyles.css");
        out.print(cssTmpl.generate(this));
        out.close();
    }


    @Override
    public void endTranslationUnit()
    {
    }

}
