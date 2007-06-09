package datascript.emit.html;

import antlr.collections.AST;
import datascript.jet.html.CSS;

public class CssEmitter extends DefaultHTMLEmitter
{
    private CSS cssTmpl = new CSS();


    @Override
    public void beginRoot(AST rootNode)
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
