package datascript.emit.html;

import antlr.collections.AST;
import datascript.jet.html.Index;

public class FramesetEmitter extends DefaultHTMLEmitter
{
    private Index indexTmpl = new Index();


    @Override
    public void beginRoot(AST rootNode)
    {
        openOutputFile(directory, "index" + HTML_EXT);
        out.print(indexTmpl.generate(this));
        out.close();
    }


}
