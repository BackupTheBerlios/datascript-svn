/**
 * 
 */
package datascript.test;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author HWellmann
 *
 */
public class CreateDomTree
{
    private Document doc;
    private Element root;

    public void create()
    {
        DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
        try
        {
            DocumentBuilder builder = f.newDocumentBuilder();
            doc = builder.newDocument();
            root = doc.getDocumentElement();
            Element e1 = doc.createElement("root");
            doc.appendChild(e1);
            Element e2 = doc.createElement("item1");
            e1.appendChild(e2);
            Element e3 = doc.createElement("tag");
            e3.setTextContent("mytag");
            e2.appendChild(e3);
            Element e4 = doc.createElement("value");
            e4.setTextContent("17");
            e2.appendChild(e4);
            
            Element e5 = doc.createElement("item2");
            e1.appendChild(e5);

            Element e6 = doc.createElement("tag");
            e6.setTextContent("mytag");
            e5.appendChild(e6);
            Element e7 = doc.createElement("value");
            e7.setTextContent("17");
            e5.appendChild(e7);
            
            
            
           TransformerFactory tf = TransformerFactory.newInstance();
           Transformer t = tf.newTransformer();
           t.setOutputProperty(OutputKeys.INDENT, "yes");
           t.setOutputProperty("indenting", "2");
           t.setOutputProperty(OutputKeys.METHOD, "xml");
           Source source = new DOMSource(doc);
           Result result = new StreamResult(System.out);
           t.transform(source, result);

            
        }
        catch (Exception exc)
        {
            exc.printStackTrace();
        }        
    }
    
    /**
     * @param args
     */
    public static void main(String[] args)
    {
        new CreateDomTree().create();
    }

}
