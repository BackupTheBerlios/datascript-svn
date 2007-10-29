package datascript.test;

import junit.framework.TestCase;
import datascript.runtime.Util;

public class UtilTest extends TestCase
{
    public void testLittleEndian()
    {
        assertEquals("7766554433221100", 
                     Long.toHexString(Util.leLong(0x0011223344556677L)));
        assertEquals("f899aabbccddeeff", 
                     Long.toHexString(Util.leLong(0xffeeddccbbaa99f8L)));
        assertEquals("33221100", Integer.toHexString(Util.leInt(0x00112233)));
        assertEquals("faddeeff", Integer.toHexString(Util.leInt(0xffeeddfa)));
        assertEquals("1100", Integer.toHexString(Util.leShort((short) 0x0011)));
        assertEquals("ffffeeff", Integer.toHexString(Util.leShort((short) 0xffee)));        
    }
}
