package datascript.templates;

import datascript.*;

public class XmlStructTemplate
{
  protected static String nl;
  public static synchronized XmlStructTemplate create(String lineSeparator)
  {
    nl = lineSeparator;
    XmlStructTemplate result = new XmlStructTemplate();
    nl = null;
    return result;
  }

  protected final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "    public void visit(";
  protected final String TEXT_2 = " n)" + NL + "    {" + NL + "        try" + NL + "        {" + NL + "            startElement();";
  protected final String TEXT_3 = "       " + NL + " \t    if (n.has";
  protected final String TEXT_4 = "())" + NL + " \t    {" + NL + "                nameStack.push(\"";
  protected final String TEXT_5 = "\");";
  protected final String TEXT_6 = NL + "                ";
  protected final String TEXT_7 = ";" + NL + "            }";
  protected final String TEXT_8 = "                        " + NL + "            nameStack.push(\"";
  protected final String TEXT_9 = "\");";
  protected final String TEXT_10 = NL + "            ";
  protected final String TEXT_11 = ";";
  protected final String TEXT_12 = "            " + NL + "            endElement();" + NL + "        }" + NL + "        catch (SAXException exc)" + NL + "        {" + NL + "            exc.printStackTrace();" + NL + "        }" + NL + "    }" + NL;
  protected final String TEXT_13 = NL;

  public String generate(Object argument)
  {
    StringBuffer stringBuffer = new StringBuffer();
     
    XmlDumperEmitter e = (XmlDumperEmitter) argument;
    CompoundType t = (CompoundType) e.getType(); 

    stringBuffer.append(TEXT_1);
    stringBuffer.append(JavaEmitter.printType(t));
    stringBuffer.append(TEXT_2);
      for (Field field : t.getFields_()) 
    { 
        boolean optional = (field.getOptionalClause() != null);
        String fieldName = field.getName();
        String accessor  = JavaEmitter.makeAccessor(fieldName);
	if (optional)
	{   

    stringBuffer.append(TEXT_3);
    stringBuffer.append(accessor);
    stringBuffer.append(TEXT_4);
    stringBuffer.append(fieldName);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(TEXT_6);
    stringBuffer.append( e.emitStructFieldVisitor(field) );
    stringBuffer.append(TEXT_7);
    
	}
        else
        {

    stringBuffer.append(TEXT_8);
    stringBuffer.append(fieldName);
    stringBuffer.append(TEXT_9);
    stringBuffer.append(TEXT_10);
    stringBuffer.append( e.emitStructFieldVisitor(field) );
    stringBuffer.append(TEXT_11);
     
	} 
    }	

    stringBuffer.append(TEXT_12);
    stringBuffer.append(TEXT_13);
    return stringBuffer.toString();
  }
}
