<#--
/* BSD License
 *
 * Copyright (c) 2006, Harald Wellmann, Harman/Becker Automotive Systems
 * All rights reserved.
 * 
 * This software is derived from previous work
 * Copyright (c) 2003, Godmar Back.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 * 
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 * 
 *     * Redistributions in binary form must reproduce the above
 *       copyright notice, this list of conditions and the following
 *       disclaimer in the documentation and/or other materials provided
 *       with the distribution.
 * 
 *     * Neither the name of Harman/Becker Automotive Systems or
 *       Godmar Back nor the names of their contributors may be used to
 *       endorse or promote products derived from this software without
 *       specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
-->
<#include "FileHeader.inc.ftl">
// DS-Import
${packageImports}

import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.XMLFilterImpl;



public class __XmlDumper extends XMLFilterImpl implements ${rootPackageName}.__Visitor
{
    private ContentHandler handler;
    private AttributesImpl noAttr = new AttributesImpl();
    private ${rootPackageName}.__Visitor.Acceptor acceptor;
    private CallChain __cc = new CallChain();    

    public __XmlDumper(${rootPackageName}.__Visitor.Acceptor acceptor)
    {
        this.acceptor = acceptor;
    }


    private void startElement(Object arg)
    {
        try
        {
            String tag = (String) arg;
            handler.startElement("", tag, tag, noAttr);
        }
        catch (SAXException exc)
        {
            exc.printStackTrace();
        }
    }


    private void endElement(Object arg)
    {
        try
        {
            String tag = (String) arg;
            handler.endElement("", tag, tag);
        }
        catch (SAXException exc)
        {
            exc.printStackTrace();
        }
    }


    private void text(String s) throws SAXException
    {
        handler.characters(s.toCharArray(), 0, s.length());
    }


    public void parse(InputSource is) throws SAXException
    {
        handler = getContentHandler();
        handler.startDocument();
        //startElement("root");
        acceptor.accept(this, "root");
        //endElement("root");
        handler.endDocument();
    }


    public void alignto(int n)
    {
    }


    public void visitInt8(byte n, Object arg)
    {
        try
        {
            startElement(arg);
            text(Short.toString(n));
            endElement(arg);
        }
        catch(SAXException exc)
        {
            exc.printStackTrace();
        }
    
    }


    public void visitInt16(short n, Object arg)
    {
        try
        {
            startElement(arg);
            text(Short.toString(n));
            endElement(arg);
        }
        catch(SAXException exc)
        {
            exc.printStackTrace();
        }
    }


    public void visitInt32(int n, Object arg)
    {
        try
        {
            startElement(arg);
            text(Integer.toString(n));
            endElement(arg);
        }
        catch(SAXException exc)
        {
            exc.printStackTrace();
        }
    }


    public void visitInt64(long n, Object arg)
    {
        try
        {
            startElement(arg);
            text(Long.toString(n));
            endElement(arg);
        }
        catch(SAXException exc)
        {
            exc.printStackTrace();
        }
    }


    public void visitUInt8(short n, Object arg)
    {
        try
        {
            startElement(arg);
            text(Short.toString(n));
            endElement(arg);
        }
        catch(SAXException exc)
        {
            exc.printStackTrace();
        }
    }


    public void visitUInt16(int n, Object arg)
    {
        try
        {
            startElement(arg);
            text(Integer.toString(n));
            endElement(arg);
        }
        catch(SAXException exc)
        {
            exc.printStackTrace();
        }
    }


    public void visitUInt32(long n, Object arg)
    {
        try
        {
            startElement(arg);
            text(Long.toString(n));
            endElement(arg);
        }
        catch(SAXException exc)
        {
            exc.printStackTrace();
        }
    }


    public void visitUInt64(BigInteger n, Object arg)
    {
        try
        {
            startElement(arg);
            text(n.toString());
            endElement(arg);
        }
        catch(SAXException exc)
        {
            exc.printStackTrace();
        }
    }


    public void visitBitField(byte n, int length, Object arg)
    {
        visitInt64(n, arg);
    }


    public void visitBitField(short n, int length, Object arg)
    {
        visitInt64(n, arg);
    }


    public void visitBitField(int n, int length, Object arg)
    {
        visitInt64(n, arg);
    }


    public void visitBitField(long n, int length, Object arg)
    {
        visitInt64(n, arg);
    }


    public void visitBitField(java.math.BigInteger n, int length, Object arg)
    {
        visitBitField(n, arg);
    }


    public void visitBitField(java.math.BigInteger n, Object arg)
    {
        try
        {
            startElement(arg);
            text(n.toString());
            endElement(arg);
        }
        catch(SAXException exc)
        {
            exc.printStackTrace();
        }    
    }


    public void visitString(String n, Object arg)
    {
        try
        {
            startElement(arg);
            text(n);
            endElement(arg);
        }
        catch(SAXException exc)
        {
            exc.printStackTrace();
        }    
    }


    public void visitArray(ObjectArray<?> n, Object arg)
    {
        int last = n.length();
        for (int i = 0; i < last; i++) 
        {
            ((__Visitor.Acceptor)(n.elementAt(i))).accept(this, arg);
        }
    }


    public void visitArray(ByteArray n, Object arg)
    {
        int last = n.length();
        for (int i = 0; i < last; i++) 
        {
            visitInt8(n.elementAt(i), arg);     
        }
    }


    public void visitArray(UnsignedByteArray n, Object arg)
    {
        int last = n.length();
        for (int i = 0; i < last; i++) 
        {
            visitUInt8(n.elementAt(i), arg);    
        }
    }


    public void visitArray(ShortArray n, Object arg)
    {
        int last = n.length();
        for (int i = 0; i < last; i++) 
        {
            visitInt16(n.elementAt(i), arg);    
        }
    }


    public void visitArray(UnsignedShortArray n, Object arg)
    {
        int last = n.length();
        for (int i = 0; i < last; i++) 
        {
            visitUInt16(n.elementAt(i), arg);   
        }
    }


    public void visitArray(IntArray n, Object arg)
    {
        int last = n.length();
        for (int i = 0; i < last; i++) 
        {
            visitInt32(n.elementAt(i), arg);    
        }
    }


    public void visitArray(UnsignedIntArray n, Object arg)
    {
        int last = n.length();
        for (int i = 0; i < last; i++) 
        {
            visitUInt32(n.elementAt(i), arg);   
        }
    }


    public void visitArray(LongArray n, Object arg)
    {
        int last = n.length();
        for (int i = 0; i < last; i++) 
        {
            visitInt64(n.elementAt(i), arg);    
        }
    }


    public void visitArray(BitFieldArray n, Object arg)
    {
        int last = n.length();
        for (int i = 0; i < last; i++) 
        {
            visitUInt64(n.elementAt(i), arg);   
        }
    }


    public void visitArray(StringArray n, Object arg)
    {
        int last = n.length();
        for (int i = 0; i < last; i++) 
        {
            visitString(n.elementAt(i), arg);   
        }
    }
