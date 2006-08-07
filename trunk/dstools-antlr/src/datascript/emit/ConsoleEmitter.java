/**
 * 
 */
package datascript.emit;

import antlr.collections.AST;
import datascript.ast.EnumItem;
import datascript.ast.EnumType;
import datascript.ast.Field;
import datascript.ast.StructType;
import datascript.ast.UnionType;

/**
 * @author HWellmann
 *
 */
public class ConsoleEmitter implements Emitter
{

    /* (non-Javadoc)
     * @see datascript.emit.Emitter#beginTranslationUnit()
     */
    public void beginTranslationUnit()
    {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see datascript.emit.Emitter#endTranslationUnit()
     */
    public void endTranslationUnit()
    {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see datascript.emit.Emitter#beginField(antlr.collections.AST)
     */
    public void beginField(AST f)
    {
        Field field = (Field)f;
        System.out.println("begin " + field.getName());
    }

    /* (non-Javadoc)
     * @see datascript.emit.Emitter#endField(antlr.collections.AST)
     */
    public void endField(AST f)
    {
        Field field = (Field)f;
        System.out.println("end " + field.getName());
    }

    /* (non-Javadoc)
     * @see datascript.emit.Emitter#beginSequence(antlr.collections.AST)
     */
    public void beginSequence(AST s)
    {
        StructType struct = (StructType)s;
        System.out.println("begin " + struct.getName());
    }

    /* (non-Javadoc)
     * @see datascript.emit.Emitter#endSequence(antlr.collections.AST)
     */
    public void endSequence(AST s)
    {
        StructType struct = (StructType)s;
        System.out.println("end " + struct.getName());
    }

    /* (non-Javadoc)
     * @see datascript.emit.Emitter#beginUnion(antlr.collections.AST)
     */
    public void beginUnion(AST u)
    {
        UnionType union = (UnionType)u;
        System.out.println("begin " + union.getName());
    }

    /* (non-Javadoc)
     * @see datascript.emit.Emitter#endUnion(antlr.collections.AST)
     */
    public void endUnion(AST u)
    {
        UnionType union = (UnionType)u;
        System.out.println("end " + union.getName());
    }

    public void beginEnumeration(AST e)
    {
        EnumType enumType = (EnumType)e;
        System.out.println("begin " + enumType.getName());
    }

    /* (non-Javadoc)
     * @see datascript.emit.Emitter#endUnion(antlr.collections.AST)
     */
    public void endEnumeration(AST e)
    {
        EnumType enumType = (EnumType)e;
        System.out.println("end " + enumType.getName());
    }

    public void beginEnumItem(AST e)
    {
        EnumItem item = (EnumItem)e;
        System.out.println("begin " + item.getName());
    }

    public void endEnumItem(AST e)
    {
        EnumItem item = (EnumItem)e;
        System.out.println("end " + item.getName());
    }
}
