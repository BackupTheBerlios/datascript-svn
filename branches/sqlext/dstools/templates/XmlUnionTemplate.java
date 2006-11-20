package datascript.templates;

import datascript.*;
import java.util.Iterator;

public class XmlUnionTemplate
{
  protected static String nl;
  public static synchronized XmlUnionTemplate create(String lineSeparator)
  {
    nl = lineSeparator;
    XmlUnionTemplate result = new XmlUnionTemplate();
    nl = null;
    return result;
  }

  protected final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "    public void visit(";
  protected final String TEXT_2 = " n)" + NL + "    {" + NL + "        try" + NL + "        {" + NL + "            startElement();" + NL + "            switch (n.__choice)" + NL + "            {";
  protected final String TEXT_3 = "            " + NL + "\t\tcase ";
  protected final String TEXT_4 = ".CHOICE_";
  protected final String TEXT_5 = ":" + NL + "                    nameStack.push(\"";
  protected final String TEXT_6 = "\");";
  protected final String TEXT_7 = NL + "                    ";
  protected final String TEXT_8 = ";" + NL + "                    break;";
  protected final String TEXT_9 = NL + "\t    }" + NL + "            endElement();" + NL + "        }" + NL + "        catch (SAXException exc)" + NL + "        {" + NL + "            exc.printStackTrace();" + NL + "        }" + NL + "    }" + NL;
  protected final String TEXT_10 = NL;

  public String generate(Object argument)
  {
    StringBuffer stringBuffer = new StringBuffer();
     XmlDumperEmitter e = (XmlDumperEmitter) argument;
   UnionType t = (UnionType) e.getType(); 
    stringBuffer.append(TEXT_1);
    stringBuffer.append( JavaEmitter.printType(t) );
    stringBuffer.append(TEXT_2);
     for (Field field : t.getFields_()) { 
    stringBuffer.append(TEXT_3);
    stringBuffer.append(JavaEmitter.printType(t));
    stringBuffer.append(TEXT_4);
    stringBuffer.append(field.getName());
    stringBuffer.append(TEXT_5);
    stringBuffer.append(field.getName());
    stringBuffer.append(TEXT_6);
    stringBuffer.append(TEXT_7);
    stringBuffer.append( e.emitUnionFieldVisitor(field) );
    stringBuffer.append(TEXT_8);
     } 
    stringBuffer.append(TEXT_9);
    stringBuffer.append(TEXT_10);
    return stringBuffer.toString();
  }
}
