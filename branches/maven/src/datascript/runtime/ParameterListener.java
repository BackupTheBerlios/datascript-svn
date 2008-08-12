package datascript.runtime;


public interface ParameterListener
{
    public Object getParameterValue(Class<?> clazz, String paramName, long key);
}
