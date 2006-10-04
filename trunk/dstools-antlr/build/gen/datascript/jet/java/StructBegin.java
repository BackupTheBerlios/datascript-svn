package datascript.jet.java;

import datascript.ast.*;
import datascript.emit.java.*;

public class StructBegin
{
  protected static String nl;
  public static synchronized StructBegin create(String lineSeparator)
  {
    nl = lineSeparator;
    StructBegin result = new StructBegin();
    nl = null;
    return result;
  }

  protected final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = "// Automatically generated" + NL + "// DO NOT EDIT" + NL + "" + NL + "/* BSD License" + NL + " *" + NL + " * Copyright (c) 2006, Harald Wellmann, Harman/Becker Automotive Systems" + NL + " * All rights reserved." + NL + " * " + NL + " * This software is derived from previous work" + NL + " * Copyright (c) 2003, Godmar Back." + NL + " *" + NL + " * Redistribution and use in source and binary forms, with or without" + NL + " * modification, are permitted provided that the following conditions are" + NL + " * met:" + NL + " * " + NL + " *     * Redistributions of source code must retain the above copyright" + NL + " *       notice, this list of conditions and the following disclaimer." + NL + " * " + NL + " *     * Redistributions in binary form must reproduce the above" + NL + " *       copyright notice, this list of conditions and the following" + NL + " *       disclaimer in the documentation and/or other materials provided" + NL + " *       with the distribution." + NL + " * " + NL + " *     * Neither the name of Harman/Becker Automotive Systems or" + NL + " *       Godmar Back nor the names of their contributors may be used to" + NL + " *       endorse or promote products derived from this software without" + NL + " *       specific prior written permission." + NL + " * " + NL + " * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS" + NL + " * \"AS IS\" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT" + NL + " * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR" + NL + " * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT" + NL + " * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL," + NL + " * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT" + NL + " * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE," + NL + " * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY" + NL + " * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT" + NL + " * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE" + NL + " * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE." + NL + " */" + NL;
  protected final String TEXT_3 = NL + "package ";
  protected final String TEXT_4 = ";" + NL + "" + NL + "import datascript.runtime.*;" + NL + "import java.io.IOException;" + NL + "import java.math.BigInteger;" + NL + "import java.util.Vector;" + NL;
  protected final String TEXT_5 = NL + "public class ";
  protected final String TEXT_6 = NL + "{" + NL + "    long __fpos;" + NL;
  protected final String TEXT_7 = NL;

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

     
    StructEmitter e = (StructEmitter) argument;
    JavaEmitter global = e.getGlobal();
    StructType s = e.getSequenceType();
    String name = s.getName(); 

    stringBuffer.append(TEXT_1);
    stringBuffer.append(TEXT_2);
     String p = global.getPackageName();
   if (p != null)
   {
    stringBuffer.append(TEXT_3);
    stringBuffer.append(p);
    stringBuffer.append(TEXT_4);
     } 
    stringBuffer.append(TEXT_5);
    stringBuffer.append(name);
    stringBuffer.append(TEXT_6);
    stringBuffer.append(TEXT_7);
    return stringBuffer.toString();
  }
}
