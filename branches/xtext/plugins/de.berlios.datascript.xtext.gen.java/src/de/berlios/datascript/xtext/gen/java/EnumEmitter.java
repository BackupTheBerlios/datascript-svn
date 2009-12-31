package de.berlios.datascript.xtext.gen.java;

import java.util.ArrayList;
import java.util.List;

import de.berlios.datascript.dataScript.EnumMember;
import de.berlios.datascript.dataScript.EnumType;
import de.berlios.datascript.dataScript.Expression;

public class EnumEmitter
{
    public static List<Integer> values(EnumType enumeration)
    {
        boolean hasValue = false;
        List<Integer> values = new ArrayList<Integer>(enumeration.getMembers().size());
        int value = 0;
        for (EnumMember member :enumeration.getMembers())
        {
            Expression expr = member.getValue();
            if (hasValue && expr != null)
            {
                //ExpressionEvaluator.getConstantValue();
                
            }
            else
            {
                values.add(value++);
            }
        }
        return values;
    }
    
}
