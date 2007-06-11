package datascript.emit.html;

import java.io.File;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import datascript.ast.CompoundType;
import datascript.ast.DataScriptException;
import datascript.ast.EnumType;
import datascript.ast.Field;
import datascript.ast.SequenceType;
import datascript.ast.SqlDatabaseType;
import datascript.ast.SqlIntegerType;
import datascript.ast.SqlMetadataType;
import datascript.ast.SqlPragmaType;
import datascript.ast.SqlTableType;
import datascript.ast.TypeInterface;
import datascript.ast.TypeReference;
import datascript.ast.UnionType;
import freemarker.template.Template;


public class CompoundEmitter extends DefaultHTMLEmitter
{
    private CompoundType compound;
    private List<FieldEmitter> fields = new ArrayList<FieldEmitter>();
    
    public static class FieldEmitter
    {
    	private Field field;
    	private TypeNameEmitter tne;
    	
    	FieldEmitter(Field f)
    	{
    		this.field = f;
    		this.tne = new TypeNameEmitter();
    	}
    	
    	public String getName()
    	{
    		return field.getName();
    	}
    	
    	public LinkedType getType()
    	{
            TypeInterface type = field.getFieldType();
            type = TypeReference.resolveType(type);
            String pkg = null;
            if (type instanceof CompoundType || type instanceof EnumType)
            {
                pkg = type.getScope().getPackage().getPackageName();           	
            }
            LinkedType linkedType = new LinkedType(pkg, type);
    		return linkedType;
    	}
    	
    	public String getConstraint()
    	{
    		return tne.getConstraint(field);
    	}

    	public String getArrayRange()
    	{
    		return tne.getArrayRange(field);
    	}

    	public String getOptionalClause()
    	{
    		return tne.getOptionalClause(field);
    	}
    	
    	public Comment getDocumentation()
    	{
        	Comment comment = new Comment();
        	String doc = field.getDocumentation();
        	if (doc != null && doc.length() > 0)
        		comment.parse(doc);
            return comment;
    	}
    }

    
    public void emit(CompoundType compound)
    {
    	this.compound = compound;
    	fields.clear();
    	for (Field field : compound.getFields())
    	{
    		FieldEmitter fe = new FieldEmitter(field);
    		fields.add(fe);
    	}
    	try
    	{
    		Template tpl = cfg.getTemplate("html/compound.html.ftl");

    		directory = new File(directory, contentFolder);
            setCurrentFolder(contentFolder);        	
    		openOutputFile(directory, compound.getName() + HTML_EXT);

    		Writer writer = new PrintWriter(out);
    		tpl.process(this, writer);
    		writer.close();
    	}
    	catch (Exception exc)
    	{
    		throw new DataScriptException(exc);
    	}
    }
    
    
    public String getCategoryPlainText()
    {
        if (compound instanceof SequenceType)
        {
            return "Sequence";
        }
        else if (compound instanceof UnionType)
        {
            return "Union";
        }
        else if (compound instanceof SqlDatabaseType)
        {
            return "SQL Database";
        }
        else if (compound instanceof SqlMetadataType)
        {
            return "SQL Matadata";
        }
        else if (compound instanceof SqlTableType)
        {
            return "SQL Table";
        }
        else if (compound instanceof SqlPragmaType)
        {
            return "SQL Pragma";
        }
        else if (compound instanceof SqlIntegerType)
        {
            return "SQL Integer";
        }
        throw new RuntimeException("unknown category " 
                  + compound.getClass().getName());
    }

    public String getCategoryKeyword()
    {
        if (compound instanceof SequenceType)
        {
            return "";
        }
        else if (compound instanceof UnionType)
        {
            return "union ";
        }
        else if (compound instanceof SqlDatabaseType)
        {
            return "sql_database ";
        }
        else if (compound instanceof SqlMetadataType)
        {
            return "sql_metadata ";
        }
        else if (compound instanceof SqlTableType)
        {
            return "sql_table ";
        }
        else if (compound instanceof SqlPragmaType)
        {
            return "sql_pragma ";
        }
        else if (compound instanceof SqlIntegerType)
        {
            return "sql_integer ";
        }
        throw new RuntimeException("unknown category " 
                  + compound.getClass().getName());
    }

    

    public String getPackageName()
    {
    	return compound.getScope().getPackage().getPackageName();
    }
    
    public CompoundType getType()
    {
    	return compound;
    }
    
    
    public Comment getDocumentation()
    {
    	Comment comment = new Comment();
    	String doc = compound.getDocumentation();
    	if (doc.length() > 0)
    		comment.parse(doc);
        return comment;
    }
    
    public List<FieldEmitter> getFields()
    {
    	return fields;
    }
}
