/**
 * 
 */
package datascript.test;

import gdb.Gdb;
import gdb.GdbInDbFile;
import gdb.__XmlDumper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;

import org.xml.sax.InputSource;

/**
 * @author HWellmann
 *
 */
public class GdbDumper
{
    private Gdb gdb;
    
    public GdbDumper(String fileName) throws IOException
    {
        GdbInDbFile db = new GdbInDbFile(fileName);
        gdb = db.getGdb();
    }
    
    public void dump(String outputFileName) throws Exception
    {
        TransformerFactory tf = TransformerFactory.newInstance();
        tf.setAttribute("indent-number", new Integer(2));
        Transformer t = tf.newTransformer();
        t.setOutputProperty(OutputKeys.INDENT, "yes");
        t.setOutputProperty(OutputKeys.METHOD, "xml");
        Source source = new SAXSource(new __XmlDumper(gdb), new InputSource());
        OutputStream os;
        if (outputFileName != null)
        {
            os = new FileOutputStream(outputFileName);
        }
        else
        {
            os = System.out;
        }
        Result result = new StreamResult(new OutputStreamWriter(os));
        t.transform(source, result);
        
    }

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception
    {
        String inputFileName = args[0];
        String outputFileName = null;
        if (args.length >= 2)
        {
            outputFileName = args[1];
        }
        GdbDumper gd = new GdbDumper(inputFileName);        
        gd.dump(outputFileName);

    }

}
