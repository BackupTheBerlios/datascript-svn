package datascript.emit.java;

import antlr.collections.AST;
import datascript.ast.StructType;
import datascript.ast.TypeInterface;
import datascript.emit.Emitter;

public class JavaEmitter implements Emitter
{
    private String packageName;
    private StructEmitter sequenceEmitter = new StructEmitter(this);
    private TypeNameEmitter typeEmitter = new TypeNameEmitter(this);
    
    public void setPackageName(String packageName)
    {
        this.packageName = packageName;
    }

    public String getPackageName()
    {
        return packageName;
    }

    public String getTypeName(TypeInterface type)
    {
        return typeEmitter.getTypeName(type);
    }
    
    public void beginTranslationUnit()
    {
        // TODO Auto-generated method stub

    }

    public void endTranslationUnit()
    {
        // TODO Auto-generated method stub

    }

    public void beginField(AST f)
    {
        // TODO Auto-generated method stub

    }

    public void endField(AST f)
    {
        // TODO Auto-generated method stub

    }

    public void beginSequence(AST s)
    {
        StructType sequence = (StructType)s;
        sequenceEmitter.begin(sequence);
    }

    public void endSequence(AST s)
    {
        StructType sequence = (StructType)s;
        sequenceEmitter.end(sequence);
    }

    public void beginUnion(AST u)
    {
        // TODO Auto-generated method stub

    }

    public void endUnion(AST u)
    {
        // TODO Auto-generated method stub

    }

    public void beginEnumeration(AST e)
    {
        // TODO Auto-generated method stub

    }

    public void endEnumeration(AST e)
    {
        // TODO Auto-generated method stub

    }

    public void beginEnumItem(AST e)
    {
        // TODO Auto-generated method stub

    }

    public void endEnumItem(AST e)
    {
        // TODO Auto-generated method stub

    }

}
