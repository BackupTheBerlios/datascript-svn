/**
 * 
 */
package datascript.test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.stream.FileImageOutputStream;

import junit.framework.TestCase;
import label.Attributes;
import label.DataBlock;
import label.ExtraAttr;
import label.Link;
import label.LinkBlock;
import label.Tile;
import label.TileHeader;
import label.TileHeader2;
import label.TileWithHeader;
import label.TileWithOptionalBlocks;
import bits.GlobalLabelSeq;
import bits.Header;
import bits.ItemA;
import bits.LabelledType;
import datascript.runtime.io.DataScriptIO;

/**
 * @author HWellmann
 * 
 */
public class LabelTest extends TestCase
{
    private FileImageOutputStream os;

    private String fileName = "labeltest.bin";

    private File file = new File(fileName);

    /*
     * @see TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception
    {
        file.delete();
    }

    /*
     * Test method for 'datascript.library.BitStreamReader.readByte()'
     */
    public void testLabel1() throws Exception
    {
        os = new FileImageOutputStream(file);
        short numItems = 3;
        int magic = 0xFFFF;
        int x = 0x076543231;

        os.writeShort(magic);
        os.writeShort(numItems);
        int dataOffset = 6 + 4 * numItems;
        int globalOffset = 2 + dataOffset + 3 * numItems;
        os.writeShort(dataOffset);
        os.writeShort(globalOffset);

        for (int i = 0; i < numItems; i++)
        {
            os.writeShort(10 + i);
            os.writeShort(20 + i);
        }

        for (int i = 0; i < numItems; i++)
        {
            os.writeByte(1);
            os.writeShort(30 + i);
        }

        os.writeInt(x);
        int size = (int) os.getStreamPosition();
        os.close();

        GlobalLabelSeq seq = new GlobalLabelSeq(fileName);
        assertEquals(magic, seq.getMagic());
        LabelledType lt = seq.getLt();
        assertEquals(numItems, lt.getNumItems());
        assertEquals(dataOffset, lt.getDataOffset());
        assertEquals(globalOffset, lt.getGlobalOffset());
        for (int i = 0; i < numItems; i++)
        {
            Header header = lt.getHeaders().elementAt(i);
            assertEquals(10 + i, header.getLen());
            assertEquals(20 + i, header.getC());
        }
        for (int i = 0; i < numItems; i++)
        {
            ItemA a = lt.getA().elementAt(i);
            assertEquals(30 + i, a.getValue());
        }
        assertEquals(x, lt.getX());
        assertEquals(size, seq.sizeof());
    }
    
    public void testTile()
    {
        Tile tile = new Tile();
        DataBlock b1 = new DataBlock();
        b1.setA(17);
        b1.setB((short)18);
        tile.setBlock1(b1);

        DataBlock b2 = new DataBlock();
        b2.setA(19);
        b2.setB((short)20);
        tile.setBlock2(b2);

        DataBlock b3 = new DataBlock();
        b3.setA(30);
        b3.setB((short)31);
        tile.setBlock3(b3);
        byte[] blob = DataScriptIO.write(tile);
        
        assertEquals(6, tile.getOffset1());
        assertEquals(12, tile.getOffset2());
        assertEquals(18, tile.getOffset3());
        
        Tile tile2 = DataScriptIO.read(Tile.class, blob);
        assertEquals(tile2, tile);
    }
    
    public void testTileWithHeader()
    {
        TileWithHeader tile = new TileWithHeader();
        TileHeader header = new TileHeader();
        tile.setHeader(header);

        DataBlock b1 = new DataBlock();
        b1.setA(17);
        b1.setB((short)18);
        tile.setB1(b1);

        DataBlock b2 = new DataBlock();
        b2.setA(19);
        b2.setB((short)20);
        tile.setB2(b2);

        DataBlock b3 = new DataBlock();
        b3.setA(30);
        b3.setB((short)31);
        tile.setB3(b3);
        byte[] blob = DataScriptIO.write(tile);
        
        assertEquals(6, tile.getHeader().getOffset1());
        assertEquals(12, tile.getHeader().getOffset2());
        assertEquals(18, tile.getHeader().getOffset3());
        
        TileWithHeader tile2 = DataScriptIO.read(TileWithHeader.class, blob);
        assertEquals(tile2, tile);
    }

    public void testTileWithOptionalBlocks1()
    {
        TileWithOptionalBlocks tile = new TileWithOptionalBlocks();
        TileHeader2 header = new TileHeader2();
        tile.setHeader(header);
        header.setHasBlock1((byte)1);
        header.setHasBlock2((byte)0);
        header.setHasBlock3((byte)1);
        header.setOffset1(0);
        header.setOffset3(0);

        // offset: 5
        LinkBlock b1 = new LinkBlock();
        b1.setNumAttrs(2);
        ArrayList<Attributes> attrs = new ArrayList<Attributes>(2);
        attrs.add(new Attributes((short)11, (short)12));
        attrs.add(new Attributes((short)13, (short)14));
        b1.setAttrs(attrs);
        
        b1.setNumLinks(1);
        ArrayList<Link> links = new ArrayList<Link>(1);
        Link link = new Link();
        link.setLinkId(99);
        link.setAttrIndex(1);
        ExtraAttr extraAttr = new ExtraAttr();
        extraAttr.setExtraByte((byte)77);
		link.setExtra(extraAttr);
        links.add(link);
        b1.setLinks(links);
        
        tile.setB1(b1);

        DataBlock b3 = new DataBlock();
        b3.setA(30);
        b3.setB((short)31);
        tile.setB3(b3);
        byte[] blob = DataScriptIO.write(tile);
        
        assertEquals(5, tile.getHeader().getOffset1().intValue());
        assertEquals(28, tile.getHeader().getOffset3().intValue());
        
        TileWithOptionalBlocks tile2 = DataScriptIO.read(TileWithOptionalBlocks.class, blob);
        assertEquals(tile2, tile);
    }

    public void testTileWithOptionalBlocks2()
    {
        TileWithOptionalBlocks tile = new TileWithOptionalBlocks();
        TileHeader2 header = new TileHeader2();
        tile.setHeader(header);
        header.setHasBlock1((byte)1);
        header.setHasBlock2((byte)0);
        header.setHasBlock3((byte)1);
        header.setOffset1(0);
        header.setOffset3(0);

        // offset: 5
        LinkBlock b1 = new LinkBlock();
        b1.setNumAttrs(2);
        ArrayList<Attributes> attrs = new ArrayList<Attributes>(2);
        attrs.add(new Attributes((short)11, (short)12));
        attrs.add(new Attributes((short)1, (short)17));
        b1.setAttrs(attrs);
        
        b1.setNumLinks(1);
        ArrayList<Link> links = new ArrayList<Link>(1);
        Link link = new Link();
        link.setLinkId(99);
        link.setAttrIndex(1);
        ExtraAttr extraAttr = new ExtraAttr();
        extraAttr.setExtra(1234567);
        link.setExtra(extraAttr);
        links.add(link);
        b1.setLinks(links);
        
        tile.setB1(b1);

        DataBlock b3 = new DataBlock();
        b3.setA(30);
        b3.setB((short)31);
        tile.setB3(b3);
        byte[] blob = DataScriptIO.write(tile);
        
        assertEquals(5, tile.getHeader().getOffset1().intValue());
        assertEquals(31, tile.getHeader().getOffset3().intValue());
        
        TileWithOptionalBlocks tile2 = DataScriptIO.read(TileWithOptionalBlocks.class, blob);
        assertEquals(tile2, tile);
    }
    
    public void testTileWithOptionalBlocks3()
    {
        TileWithOptionalBlocks tile = new TileWithOptionalBlocks();
        TileHeader2 header = new TileHeader2();
        tile.setHeader(header);
        header.setHasBlock1((byte)1);
        header.setHasBlock2((byte)0);
        header.setHasBlock3((byte)1);
        header.setOffset1(0);
        header.setOffset3(0);

        // offset: 5
        LinkBlock b1 = new LinkBlock();
        b1.setNumAttrs(2);
        ArrayList<Attributes> attrs = new ArrayList<Attributes>(2);
        attrs.add(new Attributes((short)11, (short)12));
        attrs.add(new Attributes((short)1, (short)18));
        b1.setAttrs(attrs);
        
        b1.setNumLinks(1);
        ArrayList<Link> links = new ArrayList<Link>(1);
        Link link = new Link();
        link.setLinkId(99);
        link.setAttrIndex(1);
        ExtraAttr extraAttr = new ExtraAttr();
        extraAttr.setExtraByte((byte)59);
		link.setExtra(extraAttr);
        links.add(link);
        b1.setLinks(links);
        
        tile.setB1(b1);

        DataBlock b3 = new DataBlock();
        b3.setA(30);
        b3.setB((short)31);
        tile.setB3(b3);
        byte[] blob = DataScriptIO.write(tile);
        
        assertEquals(5, tile.getHeader().getOffset1().intValue());
        assertEquals(28, tile.getHeader().getOffset3().intValue());
        
        TileWithOptionalBlocks tile2 = DataScriptIO.read(TileWithOptionalBlocks.class, blob);
        assertEquals(tile2, tile);
    }
    
    public void testLinkBlock()
    {
        LinkBlock lb = new LinkBlock();
        lb.setNumAttrs(2);
        List<Attributes> attrs = new ArrayList<Attributes>();
        Attributes attr0 = new Attributes();
        attr0.setFoo((short)1);
        attr0.setBla((short)2);
        Attributes attr1 = new Attributes();
        attr1.setFoo((short)3);
        attr1.setBla((short)17);
        attrs.add(attr0);
        attrs.add(attr1);
        lb.setAttrs(attrs);
        lb.setNumLinks(1);
        Link link = new Link();
        link.setLinkId(4711);
        link.setAttrIndex(1);
        ExtraAttr extraAttr = new ExtraAttr();
        extraAttr.setExtra(100000);
		link.setExtra(extraAttr);
        ArrayList<Link> links = new ArrayList<Link>();
        links.add(link);
        lb.setLinks(links);
        byte[] blob = DataScriptIO.write(lb);
        LinkBlock verify = DataScriptIO.read(LinkBlock.class, blob);
        assertEquals(lb, verify);
    }
}
