/**
 * 
 */
package datascript.emit;

import antlr.collections.AST;

/**
 * @author HWellmann
 *
 */
public interface Emitter
{
    public void beginTranslationUnit();
    public void endTranslationUnit();
    public void beginField(AST f);
    public void endField(AST f);
    public void beginSequence(AST s);
    public void endSequence(AST s);
    public void beginUnion(AST u);
    public void endUnion(AST u);
    public void beginEnumeration(AST e);
    public void endEnumeration(AST e);
    public void beginEnumItem(AST e);
    public void endEnumItem(AST e);
}
