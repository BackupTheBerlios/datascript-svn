/**
 * 
 */
package datascript.test;

import gdb.BlockDescriptor;
import gdb.ClusterExtInfo;
import gdb.ClusterInfo;
import gdb.Gdb;
import gdb.GdbPreludeAndHeader;
import gdb.LevelHeader;
import gdb.TileData;
import gdb.TileHeader;
import gdb.__XmlDumper;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Vector;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;

import org.xml.sax.InputSource;

import datascript.runtime.BitStreamReader;

/**
 * @author HWellmann
 *
 */
public class GdbTileDumper
{
    private Gdb gdb;
    private String inputFileName;
    private static final int PREFIX_SIZE = 2048;
    
    public GdbTileDumper(String fileName) throws IOException
    {
        this.inputFileName = fileName;
    }
    
    public void dump(long tileId) throws Exception
    {
        int x = getTileIndexX(tileId);
        int y = getTileIndexY(tileId);
        BitStreamReader in = new BitStreamReader(inputFileName);
        in.skipBytes(PREFIX_SIZE);
        GdbPreludeAndHeader ph = new GdbPreludeAndHeader(in);
        dumpXml(new __XmlDumper(ph));
        
        BlockDescriptor bd = (BlockDescriptor)ph.getHeader().
                                getBlockDescriptor().elementAt(0);
        int levelOffset = bd.getLevelOffset();
        in.seek(PREFIX_SIZE + levelOffset);
        LevelHeader lh = new LevelHeader(in);
        //dumpXml(new __XmlDumper(lh));
        
        ClusterInfo cli = (ClusterInfo) lh.getClusterInfo().elementAt(0);
        long clusterOffset = cli.getClusterOffset();
        ClusterExtInfo clxi = (ClusterExtInfo) lh.getClusterExtInfo().elementAt(0);
        
        in.seek(PREFIX_SIZE + clusterOffset);
        int numTiles = clxi.getNumTiles();
        Vector<TileHeader> tileHeaders = new Vector<TileHeader>(numTiles);
        for (int i=0; i < numTiles; i++)
        {
            System.out.println("Tile " + i);
            TileHeader th = new TileHeader(in);
            tileHeaders.addElement(th);
            dumpXml(new __XmlDumper(th));
        }
        for (int i=0; i < numTiles; i++)
        {
            System.out.println("Tile " + i);
            TileHeader th = tileHeaders.elementAt(i);
            long tileOffset = th.getDataOffset();
            in.seek(PREFIX_SIZE + tileOffset);
            TileData tile = new TileData(in, ph.getCurrentVersion(), th);
            dumpXml(new __XmlDumper(tile));
        }
    }
    
    private int getTileIndexX(long tileId)
    {
        long result = (tileId & 0x7FFF0000) >> 16;
        return (int)result;
    }
    
    private int getTileIndexY(long tileId)
    {
        long result = (tileId & 0x00007FFF);
        return (int)result;
    }
    
    private void dumpXml(__XmlDumper nodeDumper) throws Exception
    {
        TransformerFactory tf = TransformerFactory.newInstance();
        tf.setAttribute("indent-number", new Integer(2));
        Transformer t = tf.newTransformer();
        t.setOutputProperty(OutputKeys.INDENT, "yes");
        t.setOutputProperty(OutputKeys.METHOD, "xml");
        Source source = new SAXSource(nodeDumper, new InputSource());
        OutputStream os = System.out;
        /*
        if (outputFileName != null)
        {
            os = new FileOutputStream(outputFileName);
        }
        else
        {
            os = System.out;
        }
        */
        Result result = new StreamResult(new OutputStreamWriter(os));
        t.transform(source, result);
        
    }

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception
    {
        if (args.length != 2)
        {
            System.out.println("usage: GdbDumper <GDB file name> <tile id>");
            return;
        }
        
        String inputFileName = args[0];
        Long tileId = Long.parseLong(args[1]);
        GdbTileDumper gd = new GdbTileDumper(inputFileName);        
        gd.dump(tileId);

    }

}
