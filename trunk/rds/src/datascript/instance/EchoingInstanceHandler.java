package datascript.instance;

import java.math.BigInteger;

public class EchoingInstanceHandler implements DataScriptInstanceHandler
{

    @Override
    public void bigIntegerField(int fieldId, BigInteger value)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void endArray(int fieldId)
    {
        System.out.println("end array " + fieldId);

    }

    @Override
    public void endArrayElement(int fieldId)
    {
        System.out.println("end array element " + fieldId);

    }

    @Override
    public void endCompound(int fieldId)
    {
        System.out.println("end compound " + fieldId);

    }

    @Override
    public void endInstance(int typeId)
    {
        System.out.println("start instance");

    }

    @Override
    public void enumField(int fieldId, int value)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void integerField(int fieldId, long value)
    {
        System.out.println("integerField = " + value);
    }

    @Override
    public void startArray(int fieldId)
    {
        System.out.println("start array " + fieldId);

    }

    @Override
    public void startArrayElement(int fieldId)
    {
        System.out.println("start array element " + fieldId);
    }

    @Override
    public void startCompound(int fieldId)
    {
        System.out.println("start compound " + fieldId);
    }

    @Override
    public void startInstance(int typeId)
    {
        System.out.println("start instance ");
    }

    @Override
    public void stringField(int fieldId, String value)
    {
        // TODO Auto-generated method stub

    }

}
