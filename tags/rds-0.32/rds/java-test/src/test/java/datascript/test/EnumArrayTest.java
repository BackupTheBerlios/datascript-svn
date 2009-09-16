/**
 * 
 */
package datascript.test;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import bits.Colour;
import bits.arrays.EnumArray;
import datascript.runtime.io.DataScriptIO;

/**
 * @author HWellmann
 *
 */
public class EnumArrayTest extends TestCase
{
    public void testArray1() throws Exception
    {
    	EnumArray ea = new EnumArray();
    	List<Colour> colours = new ArrayList<Colour>();
    	colours.add(Colour.RED);
    	colours.add(Colour.YELLOW);
    	colours.add(Colour.RED);
    	ea.setNumItems(colours.size());
    	ea.setColours(colours);
    	
    	byte[] blob = DataScriptIO.write(ea);
    	EnumArray verify = DataScriptIO.read(EnumArray.class, blob);
    	assertEquals(ea, verify);
    }
}
