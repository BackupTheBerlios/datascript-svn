/**
 * 
 */
package datascript.test;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import bits.SignedBitField;
import bits.SignedBitFieldArray;
import datascript.runtime.DataScriptError;
import datascript.runtime.io.DataScriptIO;

/**
 * @author HWellmann
 *
 */
public class SignedBitFieldTest extends TestCase
{
    public void testSignedBitField1() throws Exception
    {
    	SignedBitField s = new SignedBitField();
    	s.setA((byte)-2);
    	s.setB((byte)-3);
    	byte[] blob = DataScriptIO.write(s);
    	SignedBitField verify = DataScriptIO.read(SignedBitField.class, blob);
    	assertEquals(s, verify);
    	assertEquals(-2, verify.getA());
    	assertEquals(-3, verify.getB());
    }

    public void testSignedBitField2() throws Exception
    {
    	SignedBitField s = new SignedBitField();
    	try
    	{
        	s.setA((byte)10);
        	s.setB((byte)13);
    		fail("expected DataScriptError");
    	}
    	catch (DataScriptError exc)
    	{
    		assertTrue(exc.getMessage().contains("exceeds the range of"));
    	}
    }
    
    public void testSignedBitFieldArray()
    {
    	SignedBitFieldArray array = new SignedBitFieldArray();
    	array.setNumItems((short)2);
    	List<SignedBitField> items = new ArrayList<SignedBitField>();
    	items.add(new SignedBitField((byte)-2, (byte)-3));
    	items.add(new SignedBitField((byte)-4, (byte)-5));
    	array.setItems(items);
    	byte[] blob = DataScriptIO.write(array);
    	SignedBitFieldArray verify = DataScriptIO.read(SignedBitFieldArray.class, blob);
    	assertEquals(array, verify);
    	SignedBitField item1 = verify.getItems().elementAt(1);
    	assertEquals(-4, item1.getA());
    	assertEquals(-5, item1.getB());
    }
}
