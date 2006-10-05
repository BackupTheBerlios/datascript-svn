/**
 * 
 */
package datascript.test;

import java.io.OutputStreamWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.XMLFilterImpl;

/**
 * @author HWellmann
 *
 */
public class SerializeSax extends XMLFilterImpl
{
    private ContentHandler handler;
    private AttributesImpl noAttr = new AttributesImpl();
    
    private void startElement(String tag) throws SAXException
    {
        handler.startElement("", "", tag, noAttr);
    }
    
    private void endElement(String tag) throws SAXException
    {
        handler.endElement("", "", tag);
    }
    
    private void text(String s) throws SAXException
    {
        handler.characters(s.toCharArray(), 0, s.length());
    }
    
    public void parse(InputSource is) throws SAXException
    {
        handler = getContentHandler();
        handler.startDocument();
        
        startElement("root");
        startElement("item1");
        startElement("tag");
        text("mytag");
        endElement("tag");
        startElement("value");
        text("17");
        endElement("value");
        endElement("item1");
        endElement("root");
        handler.endDocument();
    }
    
    /**
     * @param args
     */
    public static void main(String[] args) throws Exception
    {
        TransformerFactory tf = TransformerFactory.newInstance();
        tf.setAttribute("indent-number", new Integer(2));
        Transformer t = tf.newTransformer();
        t.setOutputProperty(OutputKeys.INDENT, "yes");
        t.setOutputProperty(OutputKeys.METHOD, "xml");
        Source source = new SAXSource(new SerializeSax(), new InputSource());
        Result result = new StreamResult(new OutputStreamWriter(System.out));
        t.transform(source, result);
    }

}
