package datascript.templates;

import datascript.*;
import java.util.Iterator;

public class StructTemplate
{
  protected static String nl;
  public static synchronized StructTemplate create(String lineSeparator)
  {
    nl = lineSeparator;
    StructTemplate result = new StructTemplate();
    nl = null;
    return result;
  }

  protected final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "  <struct name=\"";
  protected final String TEXT_2 = "\">";
  protected final String TEXT_3 = NL + "    <doc>";
  protected final String TEXT_4 = "</doc> ";
  protected final String TEXT_5 = NL + "    <field name=\"";
  protected final String TEXT_6 = "\" type=\"";
  protected final String TEXT_7 = "\">" + NL + "    </field> ";
  protected final String TEXT_8 = NL + "  </struct>";
  protected final String TEXT_9 = NL;

  public String generate(Object argument)
  {
    StringBuffer stringBuffer = new StringBuffer();
     StructType struct = (StructType) argument; 
    stringBuffer.append(TEXT_1);
    stringBuffer.append( struct.getName() );
    stringBuffer.append(TEXT_2);
     String doc = struct.getDocumentation();
     if (doc != null)
     { 
    stringBuffer.append(TEXT_3);
    stringBuffer.append(doc);
    stringBuffer.append(TEXT_4);
     }
     Iterator it = struct.getFields();
     while (it.hasNext())
     {
         Field field = (Field) it.next(); 
     
    stringBuffer.append(TEXT_5);
    stringBuffer.append( field.getName());
    stringBuffer.append(TEXT_6);
    stringBuffer.append(field.getType());
    stringBuffer.append(TEXT_7);
     } 
    stringBuffer.append(TEXT_8);
    stringBuffer.append(TEXT_9);
    return stringBuffer.toString();
  }
}
