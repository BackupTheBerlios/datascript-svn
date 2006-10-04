package datascript.jet.java;

import datascript.ast.*;
import datascript.emit.java.*;

public class SequenceRead
{
  protected static String nl;
  public static synchronized SequenceRead create(String lineSeparator)
  {
    nl = lineSeparator;
    SequenceRead result = new SequenceRead();
    nl = null;
    return result;
  }

  protected final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = " ";
  protected final String TEXT_2 = NL + "    // TODO: Emit constructor from member values." + NL + "    \t" + NL + "    public ";
  protected final String TEXT_3 = "(String __filename) throws IOException " + NL + "    {" + NL + "        this(new BitStreamReader(__filename), new CallChain());" + NL + "    }" + NL + "" + NL + "    public ";
  protected final String TEXT_4 = "(BitStreamReader __in) throws IOException " + NL + "    {" + NL + "    \tthis(__in, new CallChain());" + NL + "    }" + NL + "" + NL + "    public ";
  protected final String TEXT_5 = "(BitStreamReader __in, CallChain __cc) throws IOException " + NL + "    {" + NL + "        try " + NL + "        {" + NL + "            __cc.push(\"";
  protected final String TEXT_6 = "\", this);" + NL + "            __fpos = __in.getBitPosition();" + NL + "            try " + NL + "            {";
  protected final String TEXT_7 = "\t";
  protected final String TEXT_8 = NL + "                ";
  protected final String TEXT_9 = "                " + NL + "            } " + NL + "            catch (IOException __e1) " + NL + "            {" + NL + "        \t__in.setBitPosition(__fpos);" + NL + "                throw __e1;" + NL + "       \t    }" + NL + "        }" + NL + "        finally " + NL + "        { " + NL + "            __cc.pop(); " + NL + "        }" + NL + "    }" + NL;
  protected final String TEXT_10 = NL;

  public String generate(Object argument)
  {
    StringBuffer stringBuffer = new StringBuffer();
    
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

    stringBuffer.append(TEXT_1);
     
    StructEmitter e = (StructEmitter) argument;
    JavaEmitter global = e.getGlobal();
    StructType s = e.getSequenceType();
    String name = s.getName(); 

    stringBuffer.append(TEXT_2);
    stringBuffer.append(name);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(name);
    stringBuffer.append(TEXT_4);
    stringBuffer.append(name);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(name);
    stringBuffer.append(TEXT_6);
    
	for (Field field : s.getFields())
	{

    stringBuffer.append(TEXT_7);
    stringBuffer.append(TEXT_8);
    stringBuffer.append(e.readField(field));
    
	}

    stringBuffer.append(TEXT_9);
    stringBuffer.append(TEXT_10);
    return stringBuffer.toString();
  }
}
