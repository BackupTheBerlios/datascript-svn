package datascript.instance;

import java.io.IOException;

public interface DataScriptInstanceParser
{
    
   public void setInstanceHandler(DataScriptInstanceHandler handler); 
   public void parse(String typeName) throws IOException;
}
