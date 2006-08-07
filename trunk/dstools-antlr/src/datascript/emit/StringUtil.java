package datascript.emit;

public class StringUtil
{
    public static String firstToUpper(String s)
    {
        String first = s.substring(0, 1);
        StringBuilder buffer  = new StringBuilder(first.toUpperCase());
        buffer.append(s.substring(1, s.length()));
        return buffer.toString();
    }
}
