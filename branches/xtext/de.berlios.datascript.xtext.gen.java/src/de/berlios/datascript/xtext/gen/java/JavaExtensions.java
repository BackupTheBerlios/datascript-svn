package de.berlios.datascript.xtext.gen.java;

import de.berlios.datascript.dataScript.ComplexType;
import de.berlios.datascript.dataScript.Expression;
import de.berlios.datascript.dataScript.IntegerLiteral;
import de.berlios.datascript.dataScript.Model;
import de.berlios.datascript.dataScript.StringLiteral;

public class JavaExtensions
{
    public static String getJavaFileName(ComplexType type)
    {
        String name = type.getName();
        Model model = (Model) type.eContainer();
        String pkg  = model.getPackage().getName();
        StringBuilder builder = new StringBuilder(pkg.replaceAll("\\.", "/"));
        builder.append("/");
        builder.append(name);
        builder.append(".java");
        return builder.toString();
    }
    
    public static String printExpr(Expression expr)
    {
        String result = "dummyExpr";
        if (expr instanceof IntegerLiteral)
        {
            int value = ((IntegerLiteral) expr).getValue();
            result = Integer.toString(value);
        }
        else if (expr instanceof StringLiteral)
        {
            result = ((StringLiteral) expr).getValue();
        }
        return result;
    }
}
