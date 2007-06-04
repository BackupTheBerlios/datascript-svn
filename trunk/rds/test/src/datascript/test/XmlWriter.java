/**
 * 
 */
package datascript.test;

import java.io.OutputStreamWriter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;

import org.xml.sax.InputSource;

import bits.__Visitor;
import bits.__XmlDumper;

/**
 * @author HWellmann
 *
 */
public class XmlWriter
{
    public static void print(__Visitor.Acceptor u)
    {
        try
        {
            TransformerFactory tf = TransformerFactory.newInstance();
            tf.setAttribute("indent-number", new Integer(2));
            Transformer t = tf.newTransformer();
            t.setOutputProperty(OutputKeys.INDENT, "yes");
            t.setOutputProperty(OutputKeys.METHOD, "xml");
            Source source = new SAXSource(new __XmlDumper(u), new InputSource());
            Result result = new StreamResult(new OutputStreamWriter(System.out));
            t.transform(source, result);
        }
        catch (Exception exc)
        {
            exc.printStackTrace();
        }
    }


}
