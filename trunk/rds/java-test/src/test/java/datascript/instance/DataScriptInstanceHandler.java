package datascript.instance;

import java.math.BigInteger;

public interface DataScriptInstanceHandler
{
    public void startInstance(int typeId);
    public void endInstance(int typeId);
    public void startCompound(int fieldId);
    public void endCompound(int fieldId);
    public void startArray(int fieldId);
    public void endArray(int fieldId);
    public void startArrayElement(int fieldId);
    public void endArrayElement(int fieldId);
    public void integerField(int fieldId, long value);
    public void bigIntegerField(int fieldId, BigInteger value);
    public void stringField(int fieldId, String value);
    public void enumField(int fieldId, int value);    
}
