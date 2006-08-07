/**
 * 
 */
package datascript.emit.java;

import datascript.ast.ArrayType;
import datascript.ast.Expression;
import datascript.ast.TypeInterface;
import datascript.ast.Value;

/**
 * @author HWellmann
 *
 */
public class ArrayEmitter
{
    private String fieldName;
    private ArrayType array;
    private String elTypeName;
    
    public ArrayEmitter(String field, ArrayType array, String elTypeName)
    {
        this.fieldName = field;
        this.array = array;
        this.elTypeName = elTypeName;
    }
    
    public String getFieldName()
    {
        return fieldName;
    }
    
    public String getElementTypeName()
    {
        return elTypeName;
    }
    
    public String getLengthExpr()
    {
/*
        Expression expr = array.getLengthExpression();
        Value value = expr.getValue();
        int length = (value == null) ? 0 : value.integerValue().intValue();
        
        return Integer.toString(length);
*/
        ExpressionEmitter ee = new ExpressionEmitter();
        return ee.emit(array.getLengthExpression());
    }
}
