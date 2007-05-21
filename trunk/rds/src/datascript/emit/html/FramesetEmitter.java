package datascript.emit.html;

import antlr.collections.AST;
import datascript.jet.html.Index;

public class FramesetEmitter extends DefaultHTMLEmitter
{
    private Index indexTmpl = new Index();


    @Override
    public void beginTranslationUnit(AST rootNode, AST unitNode)
    {
        openOutputFile(directory, "index" + HTML_EXT);
        out.print(indexTmpl.generate(this));
        out.close();
    }


    @Override
    public void endTranslationUnit()
    {
    }

}
