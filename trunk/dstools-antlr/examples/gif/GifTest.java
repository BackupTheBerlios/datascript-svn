package datascript.test;

import java.io.File;
import java.io.IOException;

import datascript.runtime.array.ObjectArray;
import datascript.runtime.array.UnsignedByteArray;

import junit.framework.TestCase;

import gif.*;


public class GifTest extends TestCase
{
    private String fileName = "D:\\Documents and Settings\\hwedekind\\My Documents\\1pix.gif";
    //private String fileName = "D:\\Documents and Settings\\hwedekind\\My Documents\\My Pictures\\Heidbergring\\COPYRIGHT_WARNING_blk.gif";
    //private String fileName = "D:\\Documents and Settings\\hwedekind\\My Documents\\My Pictures\\ich\\portrait.gif";
    //private String fileName = "D:\\Documents and Settings\\hwedekind\\My Documents\\My Pictures\\Niekohle\\nicoleparis.gif";
    //private String fileName = "D:\\Documents and Settings\\hwedekind\\My Documents\\My Pictures\\Niekohle\\nicole.gif";

    private String ident;

    private void printColorMap(ObjectArray<RGBColor> globalColorMap)
    {
        System.out.println(ident + "ColorMap:");
        int entry = 0;
        ident += "    ";
        for (int rowcnt = globalColorMap.length() / 16; rowcnt > 0; rowcnt--)
        {
            System.out.print(ident);
            for (int i = 0; i < 16; i++, entry++)
            {
                String sep = (i == 16-1) ? "%n" : ", ";
                RGBColor rgbColor = globalColorMap.elementAt(entry);
                System.out.format("#%1$02X%2$02X%3$02X" + sep, 
                        rgbColor.getRed(), rgbColor.getGreen(), rgbColor.getBlue());
            }
        }
        System.out.print(ident);
        for (int i = globalColorMap.length() % 16; i > 0; i--, entry++)
        {
            String sep = (i == 1) ? "%n" : ", ";
            RGBColor rgbColor = globalColorMap.elementAt(entry);
            System.out.format("#%1$02X%2$02X%3$02X" + sep, 
                    rgbColor.getRed(), rgbColor.getGreen(), rgbColor.getBlue());   
        }
        ident = ident.substring(0, ident.length()-4);
        System.out.println();
    }
    
    private void printComment(SubBlock block, short byteCount)
    {
        UnsignedByteArray commentData = block.getDataBytes();
        for (int i = 0; i < byteCount; i++)
            System.out.print((char)commentData.elementAt(i));

        byteCount = block.getBlockTerminator();
        if (byteCount > 0)
            printComment(block.getNextData(), byteCount);
    }

    private int calcBlockSize(SubBlock blockData)
    {
        int byteCount = blockData.getBlockTerminator();
        if (byteCount == 0)
            return 0;
        return calcBlockSize(blockData.getNextData()) + byteCount; 
    }

    private void printExtensionBlock(ExtensionBlock extensionBlock) throws IOException
    {
        switch(extensionBlock.getExtensionFunctionCode())
        {
            case PLAINTEXT_EXTENSION:
            {
                System.out.println(ident + "Plain text:");
                ident += "    ";
                PlainTextExtension plainText = extensionBlock.getExtension().getPlainTextData();
                short byteCount = plainText.getByteCount();
                if (byteCount > 0)
                    printComment(plainText.getPlainTextData(), byteCount);
                ident = ident.substring(0, ident.length()-4);
                break;
            }

            case GRAPHICCONTROL_EXTENSION:
            {
                System.out.println(ident + "Graphic control extension:");
                // TODO: output of GCE
                extensionBlock.getExtension().getControlData();
                break;
            }

            case COMMENT_EXTENSION:
            {
                System.out.print(ident + "Comment: ");
                CommentExtension comment = extensionBlock.getExtension().getCommentData();
                short byteCount = comment.getByteCount();
                if (byteCount > 0)
                    printComment(comment.getCommentData(), byteCount);
                System.out.println();
                break;
            }

            case APPLICATIONEXTENSION:
            {
                System.out.println(ident + "Application extension:");
                ident += "    ";
                ApplicationExtension appData = extensionBlock.getExtension().getApplicationData();

                System.out.print(ident + "Appl-ID: ");
                UnsignedByteArray appID = appData.getApplicationIdentifier();
                for (int i = 0; i < 8; i++)
                    System.out.print((char)appID.elementAt(i));
                System.out.println();

                UnsignedByteArray applCode = appData.getAuthenticationCode();
                System.out.format(ident + "Appl-Code: %1$c%2$c%3$c%n", 
                        applCode.elementAt(0), applCode.elementAt(1), applCode.elementAt(2));

                int applDataSize = 0;
                if (appData.getApplDataSize() > 0)
                    applDataSize = appData.getApplDataSize() + calcBlockSize(appData.getApplicationData());
                System.out.format(ident + "Appl-Data size: %1$d Bytes%n", applDataSize);
                ident = ident.substring(0, ident.length()-4);
                break;
            }

            default:
            {
                System.out.println(ident + "unknown extension.");
                extensionBlock.getExtension().getData();
                break;
            }
        }
        System.out.println();
    }

    private void printImageDescriptor(ImageDescriptor imgDesc)
    {
        System.out.println(ident + "Image descriptor:");
        ident += "    ";
        System.out.format(ident + "Layer size: %1$d x %2$d%n", imgDesc.getWidth(), imgDesc.getHeight());
        System.out.format(ident + "Layer pos: %1$d, %2$d%n", imgDesc.getTop(), imgDesc.getLeft());
        System.out.format(ident + "Layer interlaced: %1$s%n", (imgDesc.getInterlacedFormatted() == 1) ? "yes" : "no");
        System.out.format(ident + "Layer bits per pixel: %1$d%n", imgDesc.getBitsPerPixel());
        if (imgDesc.getLocalColorMapFollows() == 1)
        {
            System.out.print(ident + "local ");
            printColorMap(imgDesc.getLocalColorMap());
        }
        else
        {
            System.out.println(ident + "No local color map found.");
        }
        ident = ident.substring(0, ident.length()-4);
    }

    private void printRasterData(RasterData rasterData)
    {
        int raterDataSize = rasterData.getCodeSize();
        if (raterDataSize > 0)
        {
            if (rasterData.getData().getByteCount() > 0)
                raterDataSize += rasterData.getData().getByteCount() + calcBlockSize(rasterData.getData().getDataBytes());
        }
        System.out.format(ident + "Raster Data size: %1$d Bytes%n", raterDataSize);
    }

    private void printImageBlock(ImageBlock imageBlock)
    {
        System.out.println(ident + "Image block:");
        ident += "    ";
        printImageDescriptor(imageBlock.getImage());
        printRasterData(imageBlock.getData());
        ident = ident.substring(0, ident.length()-4);

        System.out.println();
    }
    
    public void testGif() throws IOException
    {
        ident = "";
        gifFile gif = new gifFile(fileName);

        UnsignedByteArray signature = gif.getSignature().getFormat();
        System.out.format(ident + "Header: %1$c%2$c%3$c", 
                signature.elementAt(0), signature.elementAt(1), signature.elementAt(2));
        UnsignedByteArray version = gif.getSignature().getVersion();
        System.out.format(ident + ", %1$c%2$c%3$c%n", 
                version.elementAt(0), version.elementAt(1), version.elementAt(2));

        System.out.format(ident + "Size: %1$d x %2$d%n", gif.getScreen().getWidth(), gif.getScreen().getHeight());
        System.out.format(ident + "Back color: %1$d%n", gif.getScreen().getBgColor());
        System.out.format(ident + "Color res.: %1$d%n", gif.getScreen().getBitsOfColorResulution());
        System.out.format(ident + "Bits per pixel: %1$d%n", gif.getScreen().getBitsPerPixel());
        if (gif.getScreen().getGlobalColorMapFollows() == 1)
        {
            System.out.print(ident + "global ");
            printColorMap(gif.getScreen().getGlobalColorMap());
        }
        else
        {
            System.out.println(ident + "No global color map found.");
        }
        
        GIFData gifData = gif.getBlocks();
        BLOCK_TYPE gifTag = BLOCK_TYPE.__INVALID;
        do
        {
            gifTag = gifData.getTag();
            switch(gifTag)
            {
                case EXTENSION_BLOCK:
                    printExtensionBlock(gifData.getBlock().getExtension());
                    break;
    
                case IMAGE_BLOCK:
                    printImageBlock(gifData.getBlock().getImages());
                    break;
    
                case TERMINATOR_BLOCK:
                    System.out.println(ident + "End of file reached.");
                    break;
    
                default:
                    System.out.println(ident + "unknown block.");
                    gifData.getBlock().getUnknownData();
                    break;
            }
            if (gifTag != BLOCK_TYPE.TERMINATOR_BLOCK)
                gifData = gifData.getNextBlock();
            
        } while (gifTag != BLOCK_TYPE.TERMINATOR_BLOCK);
        
        assertTrue(true);
    }
}
