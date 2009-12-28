package de.berlios.datascript.validation;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.validation.ValidationMessageAcceptor;

import de.berlios.datascript.dataScript.EnumType;
import de.berlios.datascript.dataScript.Expression;
import de.berlios.datascript.dataScript.IntegerLiteral;
import de.berlios.datascript.dataScript.StringLiteral;
import de.berlios.datascript.dataScript.Type;
import de.berlios.datascript.dataScript.Value;

public class ExpressionValidator
{
    private static Set<String> ARITHMETIC_OPS = 
        new HashSet<String>(Arrays.asList("+", "-", "*", "/", "%", "<<", ">>", "^", "|", "&", "~"));

    private static Set<String> BOOLEAN_OPS = 
        new HashSet<String>(Arrays.asList("&&", "||"));

    private static Set<String> RELATIONAL_OPS = 
        new HashSet<String>(Arrays.asList("<", "<=", ">=", ">"));

    private static Set<String> EQUALITY_OPS = 
        new HashSet<String>(Arrays.asList("==", "!="));

    private ValidationMessageAcceptor acceptor;


    public ExpressionValidator(ValidationMessageAcceptor acceptor)
    {
        this.acceptor = acceptor;
    }
    
    public boolean checkCompatibility(Type paramType, Type argType)
    {
        // TODO Auto-generated method stub
        return false;
    }

    public void checkExpression(Expression expr)
    {
        if (expr instanceof IntegerLiteral)
        {
            expr.setType(BuiltInTypes.INTEGER);
        }
        else if (expr instanceof StringLiteral)
        {
            expr.setType(BuiltInTypes.STRING);
        }
        else if (expr.getRef() != null)
        {
            checkIdentifier(expr);
        }
        else
        {
            checkOperatorExpression(expr);
        }
    }

    private void checkIdentifier(Expression expr)
    {
        Value ref = expr.getRef();
        Type type2 = TypeResolver.getType(ref);
        expr.setType(type2);
    }

    private void checkOperatorExpression(Expression expr)
    {
        Expression left = expr.getLeft();
        checkExpression(left);
        Expression middle = expr.getMiddle();
        if (middle != null)
        {
            checkExpression(middle);
        }
        Expression right = expr.getRight();
        if (right != null)
        {
            checkExpression(right);
        }
        
        String op = expr.getOperator();
        if (ARITHMETIC_OPS.contains(op))
        {
            checkInteger(left);
            checkInteger(right);
            expr.setType(BuiltInTypes.INTEGER);
        }
        else if (BOOLEAN_OPS.contains(op))
        {
            checkBoolean(left);
            checkBoolean(right);
            expr.setType(BuiltInTypes.BOOLEAN);
            
        }
        else if (RELATIONAL_OPS.contains(op))
        {
            checkInteger(left);
            checkInteger(right);
            expr.setType(BuiltInTypes.BOOLEAN);
            
        }
        else if (EQUALITY_OPS.contains(op))
        {
            checkComparable(left, right);
            expr.setType(BuiltInTypes.BOOLEAN);            
        }
        else if ("?".equals(op))
        {
            checkBoolean(left);
            expr.setType(right.getType());
        }        
        else if (".".equals(op))
        {
            checkMember(expr);
            expr.setType(right.getType());
        }
        else if ("(".equals(op))
        {
            expr.setType(left.getType());
        }
        else
        {
            warning("operator '" + expr.getOperator() + "' cannot be validated", expr, null);            
        }        
    }

    private void checkMember(Expression expr)
    {
        Expression left = expr.getLeft();
        Expression right = expr.getRight();
        
        Type type = left.getType();
        if (type instanceof EnumType)
        {
        }
    }

    private void checkComparable(Expression left, Expression right)
    {
        if (BuiltInTypes.isInteger(left.getType()))
        {
            checkInteger(right);
        }
        else if (BuiltInTypes.isBoolean(left.getType()))
        {
            checkBoolean(right);
        }
        else if (BuiltInTypes.isString(left.getType()))
        {
            checkString(right);
        }
        else if (left.getType() == right.getType())
        {
            // this is ok
        }
        else
        {
            error("type is incompatible with left-hand operand", right, null);
        }        
    }

    private void checkBoolean(Expression expr)
    {
        if (! BuiltInTypes.isBoolean(expr.getType()))
        {
            error("Boolean expression required", expr, null);
        }
    }

    private void checkInteger(Expression expr)
    {
        if (! BuiltInTypes.isInteger(expr.getType()))
        {
            error("integer expression required", expr, null);
        }
    }
    
    private void checkString(Expression expr)
    {
        if (! BuiltInTypes.isString(expr.getType()))
        {
            error("string expression required", expr, null);
        }
    }
    
    private void error(String message, EObject object, Integer feature)
    {
        acceptor.acceptError(message, object, feature);
    }
    
    private void warning(String message, EObject object, Integer feature)
    {
        acceptor.acceptWarning(message, object, feature);        
    }
}
