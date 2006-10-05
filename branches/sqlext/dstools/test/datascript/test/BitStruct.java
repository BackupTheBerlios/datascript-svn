/**
 * 
 */
package datascript.test;

/**
 * @author HWellmann
 *
 */
public class BitStruct
{
    public byte a;
    public short b;
    public byte c;
    
    public BitStruct(int a, int b, int c)
    {
        if (a > 16)
            throw new IllegalArgumentException("a > 16");
        if (b > 256)
            throw new IllegalArgumentException("a > 256");
        if (c > 16)
            throw new IllegalArgumentException("c > 16");
        this.a = (byte)a;
        this.b = (short)b;
        this.c = (byte)c;
    }

}
