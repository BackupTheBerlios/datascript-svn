package datascript.jet.java;

import datascript.ast.*;
import datascript.emit.java.*;

public class Enumeration
{
  protected static String nl;
  public static synchronized Enumeration create(String lineSeparator)
  {
    nl = lineSeparator;
    Enumeration result = new Enumeration();
    nl = null;
    return result;
  }

  protected final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = "// Automatically generated" + NL + "// DO NOT EDIT" + NL + "" + NL + "/* BSD License" + NL + " *" + NL + " * Copyright (c) 2006, Harald Wellmann, Harman/Becker Automotive Systems" + NL + " * All rights reserved." + NL + " * " + NL + " * This software is derived from previous work" + NL + " * Copyright (c) 2003, Godmar Back." + NL + " *" + NL + " * Redistribution and use in source and binary forms, with or without" + NL + " * modification, are permitted provided that the following conditions are" + NL + " * met:" + NL + " * " + NL + " *     * Redistributions of source code must retain the above copyright" + NL + " *       notice, this list of conditions and the following disclaimer." + NL + " * " + NL + " *     * Redistributions in binary form must reproduce the above" + NL + " *       copyright notice, this list of conditions and the following" + NL + " *       disclaimer in the documentation and/or other materials provided" + NL + " *       with the distribution." + NL + " * " + NL + " *     * Neither the name of Harman/Becker Automotive Systems or" + NL + " *       Godmar Back nor the names of their contributors may be used to" + NL + " *       endorse or promote products derived from this software without" + NL + " *       specific prior written permission." + NL + " * " + NL + " * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS" + NL + " * \"AS IS\" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT" + NL + " * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR" + NL + " * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT" + NL + " * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL," + NL + " * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT" + NL + " * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE," + NL + " * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY" + NL + " * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT" + NL + " * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE" + NL + " * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE." + NL + " */" + NL;
  protected final String TEXT_3 = NL + "package ";
  protected final String TEXT_4 = ";" + NL + "" + NL + "import datascript.runtime.*;" + NL + "import java.io.IOException;" + NL + "import java.math.BigInteger;" + NL + "import java.util.Vector;" + NL;
  protected final String TEXT_5 = NL + "public enum ";
  protected final String TEXT_6 = NL + "{";
  protected final String TEXT_7 = NL + "    ";
  protected final String TEXT_8 = "(";
  protected final String TEXT_9 = "),";
  protected final String TEXT_10 = "   " + NL + "    __INVALID(";
  protected final String TEXT_11 = "-1);" + NL + "" + NL + "    private ";
  protected final String TEXT_12 = " value;" + NL + "    ";
  protected final String TEXT_13 = NL + "    ";
  protected final String TEXT_14 = "(";
  protected final String TEXT_15 = " value)" + NL + "    {" + NL + "        this.value = value;" + NL + "    }" + NL + "    " + NL + "    public ";
  protected final String TEXT_16 = " getValue()" + NL + "    {" + NL + "        return value;" + NL + "    }" + NL + "    " + NL + "    public static ";
  protected final String TEXT_17 = " toEnum(";
  protected final String TEXT_18 = " v)" + NL + "    {" + NL + "        switch (v)" + NL + "        {";
  protected final String TEXT_19 = NL + "            case ";
  protected final String TEXT_20 = ":" + NL + "                return ";
  protected final String TEXT_21 = ";";
  protected final String TEXT_22 = "   " + NL + "            default:" + NL + "                throw new IllegalArgumentException();" + NL + "        }" + NL + "    }" + NL + "}" + NL + NL;
  protected final String TEXT_23 = NL;

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

     
    EnumerationEmitter e = (EnumerationEmitter) argument;
    JavaEmitter global = e.getGlobal();
    EnumType en = e.getEnumerationType();
    String name = en.getName();
    String baseType = e.getBaseType();
    String cast = baseType.equals("int") ? "" : ("(" + baseType + ")");

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
     
       for (EnumItem item : en.getItems()) 
       {
           String iname = item.getName();
           int value = item.getValue().integerValue().intValue();
    
    stringBuffer.append(TEXT_7);
    stringBuffer.append(iname);
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cast);
    stringBuffer.append(value);
    stringBuffer.append(TEXT_9);
    
       }
    
    stringBuffer.append(TEXT_10);
    stringBuffer.append(cast);
    stringBuffer.append(TEXT_11);
    stringBuffer.append(baseType);
    stringBuffer.append(TEXT_12);
    stringBuffer.append(TEXT_13);
    stringBuffer.append(name);
    stringBuffer.append(TEXT_14);
    stringBuffer.append(baseType);
    stringBuffer.append(TEXT_15);
    stringBuffer.append(baseType);
    stringBuffer.append(TEXT_16);
    stringBuffer.append(name);
    stringBuffer.append(TEXT_17);
    stringBuffer.append(baseType);
    stringBuffer.append(TEXT_18);
     
           for (EnumItem item : en.getItems()) 
           {
               String iname = item.getName();
               int value = item.getValue().integerValue().intValue();
        
    stringBuffer.append(TEXT_19);
    stringBuffer.append(value);
    stringBuffer.append(TEXT_20);
    stringBuffer.append(iname);
    stringBuffer.append(TEXT_21);
    
           }
        
    stringBuffer.append(TEXT_22);
    stringBuffer.append(TEXT_23);
    return stringBuffer.toString();
  }
}
