package datascript.emit.html;

import java.io.PrintStream;


public abstract class ItemEmitter
{
    protected PrintStream out;

    
    public void setOutputStream(PrintStream out)
    {
        this.out = out;
    }
}
