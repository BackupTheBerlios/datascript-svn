package datascript.runtime.io;

import java.lang.reflect.Constructor;

import datascript.runtime.CallChain;

public class DataScriptIO
{
    public static <E extends Writer> byte[] write(E obj)
    {
        try
        {
            ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
            obj.write(writer, new CallChain());
            writer.close();
            return writer.toByteArray();
        }
        catch (Exception exc)
        {
            throw new RuntimeException(exc);
        }
    }

    public static <E> E read(Class<E> clazz, byte[] byteArray)
    {
        try
        {
            ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(
                    byteArray);
            Constructor<E> constructor = clazz
                    .getConstructor(BitStreamReader.class);
            E e = constructor.newInstance(reader);
            return e;
        }
        catch (Exception exc)
        {
            throw new RuntimeException(exc);
        }
    }
}
