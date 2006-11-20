/*
 * Sqlite3Emitter.java
 *
 * @author: Duksu Han
 * @version: $Id: Sqlite3Emitter.java,v 1.5 2006/08/10 19:53:34 
 */

package datascript;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.File;
import java.io.StringWriter;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Vector;

import datascript.parser.DSConstants;
import datascript.runtime.ByteArray;
import datascript.syntaxtree.Node;
import datascript.visitor.TreeDumper;

//? this class support only database,table class and its inner structure. The
//other should be discard.

class Sqlite3Emitter {
	
	private static final String IO_EXCEPTION = "IOException";
	
	private static final String EXCEPTION = "SQLException";
	
	private HashMap ctype2armNameMapping = new HashMap();
	
	private HashMap ctype2armTypeMapping = new HashMap();
	
	PrintWriter out = null;
	
	
	private String pkgname; // by default taken from [basename].ds
	
	String dirname; // pkgname converted to dirname (. replaced with /)
	
	String filename;
	
	
	private static boolean is_unique; // for unique index
	
	
	/**
	 * @param dirname
	 * @param pkgname
	 * @param filename
	 */
	Sqlite3Emitter(String dirname, String pkgname, String filename) {
		this.pkgname = pkgname;
		this.dirname = dirname;
		this.filename = filename;
	}
	
	void writeHeader() {
		writeHeader(out);
	}
	
	void writeHeader(PrintWriter out) {
		out.println("package " + pkgname + ";");
		out.println("");
		out.println("import java.sql.*;");
		out.println("import java.io.IOException;");
		out.println("import datascript.runtime.*;");
		out.println("import java.io.ByteArrayOutputStream;");
		out.println("import java.io.DataOutputStream;");
		out.println("import java.math.BigInteger;");
		// out.println("Define Error code in SQLite3");
		out.println();
	}
	
	void emit(CompoundType type) {
		if (Main.emitSqlite3) {
			emitContainedTypes(type, "", /* istop= */true);
		}
	}
	
	void emitContainedTypes(CompoundType type, String indent) {
		emitContainedTypes(type, indent, false);
	}
	
	void emitContainedTypes(CompoundType type, String indent, boolean istop) {
		for (Iterator i = type.getNestedTypes(); i.hasNext();) {
			emit((CompoundType) i.next(), indent, istop);
		}
	}
	
	/**
	 * @param type
	 * @param indent
	 * @param istop
	 */
	void emit(CompoundType type, String indent, boolean istop) {
		if (type.isEmpty()) {
			return;
		}
		
		if (istop) {
			if (out != null) {
				out.close();
			}
			out = Main.openFile(dirname + File.separator + type.getName()
					+ ".java");
			if (out == null)
				return;
			writeHeader();
		}
		
		out.print(indent);
		indent += "		";
		if (type instanceof DatabaseType) {
			out.print("public class " + type.getName() + " extends " + filename + ".Bytes."
					+ printType(type));
			out.println(" {");
			out.println();
			out.println(indent + "public Connection conn;");
			out.println(indent + "public Statement  stmt;");
			
		} else if (type instanceof TableType) {
			
			out.print("public class " + type.getName() + " extends " + filename + ".Bytes."
					+ printType(type));
			out.println(" {");
			
		} else if (type.getName() == "sql_fields") {
			out.print("public class " + type.getName() + " extends " + filename + ".Bytes."
					+ printType(type));
			out.println(" {");
			
		}else {
			out.print("public class " + type.getName() + " extends " + filename + ".Bytes."
					+ printType(type));
			out.println(" {");
		}
		
//		out.println(getDefTypeNameList(type,indent));
		type.computeBitFieldOffsets(0);
		emitContainedTypes(type, indent);
		
		if (type instanceof DatabaseType) {
			out
			.println(indent
					+ "  /* this class is a ( struct, database or table type */");
			emitSourceConstructor(type, indent);
			
		} else if (type instanceof TableType) {
			emitTableSourceConstructor(type, indent);
		} else if (type.getName() == "sql_fields") {
			emitFieldsSourceConstructor(type, indent);
		} else if (type instanceof SqlIntegerType) {
			emitSqlInteger(type, indent);
		} else if (type instanceof SqlTextType) {
			emitSqlText(type, indent);
		} else if (type.getName() == "sql_metadata") {
			emitMetadata(type, indent);
			
		} else if (type.getName() == "sql_pragma") {
			emitPragma(type, indent);
			
		} else {
			
			emitOtherSourceConstructor(type, indent);
		}
		
		indent = indent.substring(2); 
		out.println(indent + "}");
		out.println();
		out.flush();
	}
	
	/**
	 * @param type
	 * @param indent
	 */
	private void emitSqlText(CompoundType type, String indent) {
		
		out
		.println(indent
				+ "  ByteArrayOutputStream byte_stream = new ByteArrayOutputStream();");
		out
		.println(indent
				+ "  DataOutputStream data_stream = new DataOutputStream(byte_stream);");
		
		out
		.println(indent
				+ "public void createSchema()  throws SQLException {");
		emitMetadataInsertQuery(type, indent);
		out.println(indent + "}");
		out.println();
		
		out.println(indent + "public " + "byte[] get_bytes()" + "throws "
				+ IO_EXCEPTION + ", " + EXCEPTION + "{");
		out.println(indent + " ");
		out.println(indent + "   byte_stream.reset();");
		out.println(indent + "   this.write(data_stream);");
		out.println(indent + "   return byte_stream.toByteArray();");
		
		out.println();
		out.println(indent + "}");
		out.println();
		out.println(indent + "public " + "String get_string_value()"
				+ "throws " + IO_EXCEPTION + ", " + EXCEPTION + "{");
		out.println(indent + " ");
		out.println(indent + "   byte_stream.reset();");
		out.println(indent + "   this.write(data_stream);");
		
		out
		.println(indent
				+ "   return (byte_stream.toString(\"utf-8\"));");
		
		out.println();
		out.println(indent + "}");
		out.println();
		
//		out.println(indent + "public void set_string_value(String value) throws IOException{");
//		out.println(indent + "byte_stream.reset();");
//		out.println(indent + "data_stream.writeBytes(value);");
//		out.println(indent + "read(byte_stream.toByteArray());");
//		out.println(indent + "}");
//		out.println();
		
	}
	
	/**
	 * @param type
	 * @param indent
	 */
	private void emitSqlInteger(CompoundType type, String indent) {
		
		out
		.println(indent
				+ "  ByteArrayOutputStream byte_stream = new ByteArrayOutputStream();");
		out
		.println(indent
				+ "  DataOutputStream data_stream = new DataOutputStream(byte_stream);");
		
		out
		.println(indent
				+ "public void createSchema()  throws SQLException {");
		out.println(indent + "}");
		out.println();
		
		out.println(indent + "  public " + "byte[] get_bytes()" + "throws "
				+ IO_EXCEPTION + ", " + EXCEPTION + "{");
		out.println(indent + " ");
		out.println(indent + "   byte_stream.reset();");
		out.println(indent + "   this.write(data_stream);");
		out.println(indent + "       return byte_stream.toByteArray();");
		
		out.println();
		out.println(indent + "  }");
		out.println();
		
		out.println(indent + "  public " + "long get_integer_value()"
				+ "throws " + IO_EXCEPTION + ", " + EXCEPTION + "{");
		out.println(indent + " ");
		out.println(indent + "   byte_stream.reset();");
		out.println(indent + "   this.write(data_stream);");
		
		out
		.println(indent
				+ "       return (new BigInteger(byte_stream.toByteArray())).longValue();");
		
		out.println();
		out.println(indent + "  }");
		
		out.println(indent + "  public void set_integer_value(long value) throws IOException{");
		out.println(indent + " ");
		out.println(indent + "   byte_stream.reset();");
		out.println(indent	+ "       this.read((new BigInteger(new Long(value).toString())).toByteArray());");
		out.println();
		out.println(indent + "  }");
		
		
		
		out.println();
		
	}
	
	/**
	 * @param type
	 * @param indent
	 */
	private void emitPragma(CompoundType type, String indent) {
		out
		.println(indent
				+ "public void createSchema()  throws SQLException {");
		emitPragmaCommand(type, indent);
		out.println(indent + "}");
		out
		.println(indent
				+ "public void sql_exec()  throws SQLException {");
		out.println(indent + "this.createSchema();");
		out.println(indent + "}");
		out.println();
		
	}
	
	/**
	 * @param ctype
	 * @param indent
	 * @return
	 */
	private String CreateMetadataUpdateStr(CompoundType ctype, String indent) {
		if (!ctype.hasFields()) {
			return "";
		}
		Iterator i = (Iterator) ctype.getFields();
		StringBuffer b = new StringBuffer();
		String type_name;
		String var_name;
		
		while (true) {
			Field f = (Field) i.next();
			type_name = printType(f.getType());
			var_name = f.getName();
			
			if(!(f.getType() instanceof StringType))
				b.append(indent + "stmt.execute(\"Update meta_data Set var_value = \" + " + f.getName()
						+ " + \" where var_name =  \\\"" + f.getName() +  "\\\" \");\n	");
			else 
				b.append(indent + "stmt.execute(\"Update meta_data Set var_value = \\\"\" + " + f.getName()
						+ " + \"\\\" where var_name = " + f.getName() +  " \");\n	");
			if (!(i.hasNext()))
				break;
		}
		return b.toString();
	}
	
	/**
	 * @param ctype
	 * @param indent
	 * @return
	 */
	private String getReadMetadataStr(CompoundType ctype, String indent) {
		if (!ctype.hasFields()) {
			return "";
		}
		Iterator i = (Iterator) ctype.getFields();
		StringBuffer b = new StringBuffer();
		String type_name;
		String var_name;
		
		b.append(indent + "ResultSet rs = stmt.executeQuery(\"Select * from meta_data \");\n	");
		
		b.append(getSetReadMetaList(ctype,indent));
		return b.toString();
	}
	
	
	
	private String emitPragmaCommand(CompoundType ctype, String indent) {
		if (!ctype.hasFields()) {
			return "";
		}
		Iterator i = (Iterator) ctype.getFields();
		StringBuffer b = new StringBuffer();
		String type_name;
		String var_name;
		
		while (true) {
			Field f = (Field) i.next();
			type_name = printType(f.getType());
			var_name = f.getName();
			
			if (var_name == "page") {
				out.println(indent + "stmt.execute(\"Pragma page_size = \" + "
						+ "getPage()" + ");");
			} else if (var_name == "compression") {
				out.println(indent + "stmt.execute(\"Pragma compr_type = \\\"\" + "
						+ "getCompression()" + " + \"\\\"\");");
				
			} else if (var_name == "percentageFree") {
				out.println(indent
						+ "stmt.execute(\"Pragma compr_pctfree = \" + "
						+ "getPercentageFree()" + ");");
				
			} else {
				System.out
				.println("[debug] -- error: Unknown varaible in sql_pragma");
			}
			b.append(indent + " public void" + " set_" + f.getName() + "("
					+ printType(f.getType()) + " value ){");
			b.append("_" + ctype.getName() + ".set");
			b.append(f.getName().toUpperCase().charAt(0));
			b.append(f.getName().substring(1));
			b.append("(value); }\n");
			
			if (!(i.hasNext()))
				break;
		}
		return b.toString();
	}
	
	private String emitMetadataInsertQuery(CompoundType ctype, String indent) {
		if (!ctype.hasFields()) {
			return "";
		}
		Iterator i = (Iterator) ctype.getFields();
		StringBuffer b = new StringBuffer();
		String type_name;
		String var_name;
		
		while (true) {
			Field f = (Field) i.next();
			type_name = printType(f.getType());
			var_name = f.getName();
			
			if (f.hasInitializer()) {
				if (printType(f.getType()).contains("String")) {
					out
					.println(indent
							+ "stmt.execute(\"Insert into meta_data values(\" + "
							+ "\"\\\"" + f.getName() + "\\\"\""
							+ " + \",\\\"\" + " + "get"
							+ f.getName().toUpperCase().charAt(0)
							+ f.getName().substring(1)
							+ "() + \"\\\")\");");
				} else
					out
					.println(indent
							+ "stmt.execute(\"Insert into meta_data values(\" + "
							+ "\"\\\"" + f.getName() + "\\\"\""
							+ " + \",\" + " + "get"
							+ f.getName().toUpperCase().charAt(0)
							+ f.getName().substring(1)
							+ "() + \")\");");
				
			} else {
				out.println(indent
						+ "stmt.execute(\"Insert into meta_data values(\" + "
						+ "\"\\\"" + f.getName() + "\\\"\"" + " + \",\\\"\" + "
						+ "\"NULL\"" + " + \"\\\")\");");
			}
			
			if (!(i.hasNext()))
				break;
		}
		return b.toString();
	}
	
	
	private void emitMetadata(CompoundType type, String indent) {
		String update_meta_data = CreateMetadataUpdateStr(type,indent);
		String read_meta_data   = getReadMetadataStr(type,indent);
		
		out
		.println(indent
				+ "public void createSchema()  throws SQLException {");
		emitMetadataInsertQuery(type, indent);
		
		out.println(indent + "}");
		out.println();
		// getter and setter for Metadata
		
		out.println(indent + "public void sql_update() throws SQLException{");
		out.println(update_meta_data);
		
		out.println(indent + "");
		out.println(indent + "}");
		
		out.println(indent + "public void sql_read() throws SQLException{");
		out.println(read_meta_data);
		out.println(indent + "");
		out.println(indent + "}");
		
		
	}
	
	private String getConstructorTypeNameList(CompoundType ctype, String indent) {
		if (!ctype.hasFields()) {
			return "";
		}
		// we will check whether the ctype has a fields
		Iterator i = (Iterator) ctype.getFields();
		StringBuffer b = new StringBuffer();
		String type_name;
		while (true) {
			Field f = (Field) i.next();
			type_name = printType(f.getType());
			
			if (!(f.getType() instanceof BuiltinType)
					&& !(f.getType() instanceof StringType)
					&& !(f.getType() instanceof ArrayType)
					&& !(f.getType() instanceof EnumType)	) {
				b.append(f.getName());
				
				b.append(" = new " + filename + ".sql3.");
				b.append(type_name);
				b.append("()");
				b.append(";\n");
			}
			
			if (!(i.hasNext()))
				break;
			
		}
		return b.toString();
	}
	
	private String getDefTypeNameList(CompoundType ctype, String indent) {
		if (!ctype.hasFields()) {
			return "";
		}
		// we will check whether the ctype has a fields
		Iterator i = (Iterator) ctype.getFields();
		StringBuffer b = new StringBuffer();
		String type_name;
		while (true) {
			Field f = (Field) i.next();
			type_name = printType(f.getType());
			
			if (!(f.getType() instanceof BuiltinType)
					&& !(f.getType() instanceof StringType)
					&& !(f.getType() instanceof ArrayType)
					&& !(f.getType() instanceof EnumType)	) {
				b.append(indent + "public " + filename + ".sql3.");
				
				b.append(type_name);
				b.append(" ");
				b.append(f.getName());
				
				b.append(" = new " + filename + ".sql3.");
				b.append(type_name);
				b.append("()");
				b.append(";\n");
			}
			
			if (!(i.hasNext()))
				break;
			
		}
		return b.toString();
	}
	
	private String getInitList(CompoundType ctype, String indent) {
		if (!ctype.hasFields()) {
			return "";
		}
		// we will check whether the ctype has a fields
		Iterator i = (Iterator) ctype.getFields();
		StringBuffer b = new StringBuffer();
		int j = 0;
		String type_name;
		while (true) {
			Field f = (Field) i.next();
			type_name = printType(f.getType());
			
			if ( f.getType() instanceof TableType ){
				b.append(indent + f.getName() + ".fields.sql_init();\n" );
			}
			
			if (!(i.hasNext()))
				break;
			
		}
		
		return b.toString();
	}
	
	private String getBaseKeyList(CompoundType ctype, String indent) {
		if (!ctype.hasFields()) {
			return "";
		}
		// we will check whether the ctype has a fields
		Iterator i = (Iterator) ctype.getFields();
		StringBuffer b = new StringBuffer();
		int j = 0;
		// f.getType has info.
		while (true) {
			Field f = (Field) i.next();
			++j;
			b.append(indent + "public boolean is_" + f.getName() + "_base_key;\n");
			if (!(i.hasNext())){
				b.append(indent + "private int num_key = " + j + ";\n");
				break;
			}
		}
		return b.toString();
	}
	
	private String getCreateUpdateStr(CompoundType ctype, String indent) {
		if (!ctype.hasFields()) {
			return "";
		}
		// we will check whether the ctype has a fields
		Iterator i = (Iterator) ctype.getFields();
		StringBuffer b = new StringBuffer();
		int j = 0;
		
		
		while (true) {
			Field f = (Field) i.next();
			++j;
			
			b.append(indent + "if(is_" + f.getName() + "_base_key){\n");
			b.append(indent + "    update_str = update_str.concat(\"" + f.getName() 
					+ " = ? AND \");\n");
			b.append(indent + "    base_key_exist = true;\n");
			b.append(indent + "}\n");
			if (!(i.hasNext()))	break;
			
		}
		return b.toString();
	}
	
	
	private String getCreateReadStr(CompoundType ctype, String indent) {
		if (!ctype.hasFields()) {
			return "";
		}
		// we will check whether the ctype has a fields
		Iterator i = (Iterator) ctype.getFields();
		StringBuffer b = new StringBuffer();
		int j = 0;
		
		
		while (true) {
			Field f = (Field) i.next();
			++j;
			
			b.append(indent + "if(is_" + f.getName() + "_base_key){\n");
			b.append(indent + "    select_str = select_str.concat(\"" + f.getName() 
					+ " = ? AND \");\n");
			b.append(indent + "    base_key_exist = true;\n");
			b.append(indent + "}\n");
			if (!(i.hasNext()))	break;
			
		}
		b.append(indent + "if(is_rowid_base_key){\n");
		b.append(indent + "   select_str = select_str.concat(\"rowid = \" + rowid + \" AND \");\n");
		b.append(indent + "    base_key_exist = true;\n");
		b.append(indent + "}\n");
		return b.toString();
	}
	
	private String getSetReadStmt(CompoundType ctype, String indent) {
		if (!ctype.hasFields()) {
			return "";
		}
		Iterator i = (Iterator) ctype.getFields();
		StringBuffer b = new StringBuffer();
		b.append(indent + "int num_select_key = 0;\n");
		
		while (true) {
			Field f = (Field) i.next();
			
			b.append(indent + "if(is_" + f.getName() + "_base_key){\n");
			b.append(indent + "    ++num_select_key;\n");
			
			if((f.getType() instanceof BuiltinType) || (f.getType() instanceof StringType)
					|| (f.getType() instanceof EnumType)){
				b.append(indent + "    ps_select.set");
				b.append(printType(f.getType()).toUpperCase().charAt(0)
						+ printType(f.getType()).substring(1) + "(" + "num_select_key" +", ");
				b.append( f.getName() + ");\n" );
			}else{
				b.append(indent + "byte_stream.reset();\n");
				b.append(indent + f.getName() + ".write(data_stream);\n");
				b.append(indent + "    ps_select.setBytes(num_select_key, ");
				b.append("byte_stream.toByteArray());\n");
			}
			
			
			b.append(indent + "}\n");
			if (!(i.hasNext()))	break;
			
		}
		return b.toString();
	}
	
	
	private String getSetUpdateStmt(CompoundType ctype, String indent) {
		if (!ctype.hasFields()) {
			return "";
		}
		Iterator i = (Iterator) ctype.getFields();
		StringBuffer b = new StringBuffer();
		b.append(indent + "int num_update_key = num_key;\n");
		
		while (true) {
			Field f = (Field) i.next();
			
			b.append(indent + "if(is_" + f.getName() + "_base_key){\n");
			b.append(indent + "    ++num_update_key;\n");
			
			if((f.getType() instanceof BuiltinType) || (f.getType() instanceof StringType)
					|| (f.getType() instanceof EnumType)){
				b.append(indent + "ps_update.set");
				b.append(printType(f.getType()).toUpperCase().charAt(0)
						+ printType(f.getType()).substring(1) + "(num_update_key, ");
				b.append( f.getName() + ");\n" );
			}else{
				b.append(indent + "byte_stream.reset();\n");
				b.append(indent + f.getName() + ".write(data_stream);\n");
				b.append(indent + "ps_update.setBytes(num_update_key, ");
				
				b.append("byte_stream.toByteArray());\n");
						
			}
			
			b.append(indent + "}\n");
			if (!(i.hasNext()))	break;
			
		}
		return b.toString();
	}
	
	private String getTypeNameUpdateList(CompoundType ctype, String indent) {
		if (!ctype.hasFields()) {
			return "";
		}
		
		// we will check whether the ctype has a fields
		Iterator i = (Iterator) ctype.getFields();
		StringBuffer b = new StringBuffer();
		// f.getType has info.
		while (true) {
			Field f = (Field) i.next();
			b.append(indent + f.getName());
			// column_constraint = printNode(f.getSqlCondition());
			b.append("=? ");
			
			
			if (!(i.hasNext()))
				break;
			b.append(", ");
		}
		return b.toString();
	}
	
	private String getReverseList(CompoundType ctype, String indent) {
		if (!ctype.hasFields()) {
			return "";
		}
		// we will check whether the ctype has a fields
		Iterator i = (Iterator) ctype.getFields();
		StringBuffer b = new StringBuffer();
		// f.getType has info.
		while (true) {
			Field f = (Field) i.next();
			// column_constraint = printNode(f.getSqlCondition());
//			b.append("  ");
			if (printType(f.getType()) == "short"){ 
				b.append(indent + f.getName() 
						+ " = Short.reverseBytes("+ f.getName() +");");
			}else if(printType(f.getType()) == "int"){ 
				b.append(indent + f.getName() 
						+ " = Integer.reverse("+ f.getName() +");");
			}else if(printType(f.getType()) == "long"){ 
				b.append(indent + f.getName() 
						+ " = Long.reverse("+ f.getName() +");");
			}else if( (f.getType() instanceof ArrayType) && ( printType(f.getType()) != "ByteArray"  )){
				
				b.append(indent + f.getName() 
						+ ".reverse();");
			}
			b.append("\n");
			
			if (!(i.hasNext()))
				break;
		}
		return b.toString();
	}
	
	private String getLittleEndianList(CompoundType ctype, String indent) {
		if (!ctype.hasFields()) {
			return "";
		}
		// we will check whether the ctype has a fields
		Iterator i = (Iterator) ctype.getFields();
		StringBuffer b = new StringBuffer();
		// f.getType has info.
		while (true) {
			Field f = (Field) i.next();
			
			if (f.getSqlBlobEncodingStr() != null && f.getSqlBlobEncodingStr().toUpperCase().contains("LITTLEENDIAN")){
				b.append( f.getName() + ".is_little_endian = true;\n" );
			}
			if (!(i.hasNext()))
				break;
			
			
		}
		return b.toString();
	}
	
	private String getBlobInfoInsertMeta(CompoundType ctype, String indent) {
		if (!ctype.hasFields()) {
			return "";
		}
		// we will check whether the ctype has a fields
		Iterator i = (Iterator) ctype.getFields();
		StringBuffer b = new StringBuffer();
		// f.getType has info.
		while (true) {
			Field f = (Field) i.next();
			b.append(f.getName());
			// column_constraint = printNode(f.getSqlCondition());
			b.append("  ");
			if (!(printType(f.getType()) == "byte"
				|| printType(f.getType()) == "int"
					|| printType(f.getType()) == "short"
						|| printType(f.getType()) == "long"
							|| (f.getType() instanceof SqlIntegerType)
							|| (f.getType() instanceof SqlTextType))) {
				
				if (f.getSqlBlobCompressionStr() != null)
					b.append(indent + "stmt.execute(Insert into meta_table values(BLOB_COMPRESS_" + ctype.getParent().getName()
							+ "_" + f.getName() + "," + f.getSqlBlobCompressionStr() + "));\n");
				if (f.getSqlBlobEncodingStr() != null)
					b.append(indent + "stmt.execute(Insert into meta_table values(BLOB_COMPRESS_" + ctype.getParent().getName()
							+ "_" + f.getName() + "," + f.getSqlBlobEncodingStr() + "));\n");
				if (!(i.hasNext()))
					break;
			}
			
		}
		return b.toString();
	}
	
	private String getTypeNameList(CompoundType ctype, String indent) {
		if (!ctype.hasFields()) {
			return "";
		}
		// we will check whether the ctype has a fields
		Iterator i = (Iterator) ctype.getFields();
		StringBuffer b = new StringBuffer();
		// f.getType has info.
		while (true) {
			Field f = (Field) i.next();
			b.append(indent + f.getName());
			// column_constraint = printNode(f.getSqlCondition());
			b.append("  ");
			if (printType(f.getType()) == "byte"
				|| printType(f.getType()) == "int"
					|| printType(f.getType()) == "short"
						|| printType(f.getType()) == "long"
							|| (f.getType() instanceof SqlIntegerType))
				b.append("INTEGER");
			else if (printType(f.getType()) == "String" || f.getType() instanceof SqlTextType ) {
				b.append("TEXT");
			} else
				b.append("BLOB");
			if (f.getSqlCondition() != null)
				b.append(" " + printNode(f.getSqlCondition()));
			if (!(i.hasNext()))
				break;
			b.append(",  ");
		}
		return b.toString();
	}
	
	private String getMetadataInputStr(CompoundType ctype) {
		if (!ctype.hasFields()) {
			return "";
		}
		// we will check whether the ctype has a fields
		Iterator i = (Iterator) ctype.getFields();
		StringBuffer b = new StringBuffer();
		// f.getType has info.
		while (true) {
			Field f = (Field) i.next();
			b.append(f.getName());
			// column_constraint = printNode(f.getSqlCondition());
			b.append("  ");
			if (printType(f.getType()) == "byte"
				|| printType(f.getType()) == "int"
					|| printType(f.getType()) == "short"
						|| printType(f.getType()) == "long")
				b.append("INTEGER");
			else
				b.append("BLOB");
			if (f.getSqlCondition() != null)
				b.append(" " + printNode(f.getSqlCondition()));
			if (!(i.hasNext()))
				break;
			b.append(",  ");
		}
		return b.toString();
	}
	
	
	private String getTypeList_with_schema(CompoundType ctype,String indent) {
		if (!ctype.hasFields()) {
			return "";
		}
		// we will check whether the ctype has a fields
		Iterator i = (Iterator) ctype.getFields();
		StringBuffer b = new StringBuffer();
		
		while (true) {
			Field f = (Field) i.next();
			b.append(indent + f.getName() + ".createSchema();\n");
			if (!(i.hasNext()))
				break;
		}
		return b.toString();
	}
	
	private String getTypeList_call_get_fields(CompoundType ctype, String indent ) {
		if (!ctype.hasFields()) {
			return "";
		}
		// we will check whether the ctype has a fields
		Iterator i = (Iterator) ctype.getFields();
		StringBuffer b = new StringBuffer();
		String type_name;
		
		while (true) {
			
			Field f = (Field) i.next();
			type_name = printType(f.getType());
			if (type_name == "int") {
				b.append(indent + "data_stream.write");
				b.append("Int");
				b.append("(");
				b.append(f.getName());
				b.append(");\n");
			} else if (type_name == "byte") {
				b.append(indent + "data_stream.write");
				b.append("Byte");
				b.append("(");
				b.append(f.getName());
				b.append(");\n");
			} else if (type_name == "short") {
				b.append(indent + "data_stream.write");
				b.append("Short");
				b.append("(");
				b.append(f.getName());
				b.append(");\n");
			} else if (type_name == "long") {
				b.append(indent + "data_stream.write");
				b.append("Long");
				b.append("(");
				b.append(f.getName());
				b.append(");\n");
			} else if (type_name == "String") {
				b.append(indent + "data_stream.write");
				b.append("Bytes");
				b.append("(");
				b.append(f.getName());
				b.append(");\n");
				
			} else {
				
				
				b.append(indent + "byte_stream.reset();\n");
				b.append(indent + f.getName() + ".write(data_stream);\n");
				
				b.append(indent + "data_stream.write");
				b.append("(");
				b.append("byte_stream.toByteArray());\n");
			}
			
			if (!(i.hasNext()))
				break;
		}
		return b.toString();
	}
	
	private String getTypeList_call_get_fields_union(CompoundType ctype, String indent) {
		if (!ctype.hasFields()) {
			return "";
		}
		
		StringBuffer b = new StringBuffer();
		b.append("_" + ctype.getName() + ".write(data_stream);");
		return b.toString();
	}
	
	private String getSetReadMetaList(CompoundType ctype, String indent) {
		if (!ctype.hasFields()) {
			return "";
		}
		// we will check whether the ctype has a fields
		Iterator i = (Iterator) ctype.getFields();
		StringBuffer b = new StringBuffer();
		String type_name;
		int field_num = 2;
		
		while (true) {
			
			Field f = (Field) i.next();
			type_name = printType(f.getType());
			if (type_name == "int"){ 
				b.append(indent + f.getName() + " = rs.get");
				b.append("Int");
				b.append("(");
				b.append(field_num);
				b.append(");\n");
			} else if (type_name == "byte") {
				b.append(indent + f.getName() + " =  rs.get");
				b.append("Byte");
				b.append("(");
				b.append(field_num);
				b.append(");\n");
			} else if (type_name == "short") {
				b.append(indent + f.getName() + " =  rs.get");
				b.append("Short");
				b.append("(");
				b.append(field_num);
				b.append(");\n");
			} else if (type_name == "long") {
				b.append(indent + f.getName() + " =  rs.get");
				b.append("Long");
				b.append("(");
				b.append(field_num);
				b.append(");\n");
			} else if (type_name == "String"){
				b.append(indent + f.getName() + " =  rs.get");
				b.append("String");
				b.append("(");
				b.append(field_num);
				b.append(");\n");
			} else if ( f.getType() instanceof SqlIntegerType) {
				
                b.append("byte_stream.reset();\n");	
                b.append(f.getName() + ".read((new BigInteger(new Long("); 
                b.append(f.getName() + " =  rs.get");
				b.append("Int");
				b.append("(");
				b.append(field_num);
				b.append("))");
				b.append(").toString())).toByteArray());\n");
			}else if( f.getType() instanceof SqlTextType ){
				
				b.append(indent + f.getName() + ".set_string_value("+ " rs.get");
				b.append("String");
				b.append("(");
				b.append(field_num);
				b.append("));\n");
				
			}else {
				b.append(indent + f.getName() + ".read("+ " rs.get");
				b.append("Bytes");
				b.append("(");
				b.append(field_num);
				b.append("));\n");
			}
			
			if (!(i.hasNext()))	break;
			
			b.append(indent + "rs.next();\n");
			
			
		}
		return b.toString();
	}
	
	private String getSetReadList(CompoundType ctype, String indent) {
		if (!ctype.hasFields()) {
			return "";
		}
		// we will check whether the ctype has a fields
		Iterator i = (Iterator) ctype.getFields();
		StringBuffer b = new StringBuffer();
		String type_name;
		int field_num = 1;
		
		while (true) {
			
			Field f = (Field) i.next();
			type_name = printType(f.getType());
			if (type_name == "int"){ 
				b.append(indent + f.getName() + " = rs.get");
				b.append("Int");
				b.append("(");
				b.append(field_num);
				b.append(");\n");
			} else if (type_name == "byte") {
				b.append(indent + f.getName() + " =  rs.get");
				b.append("Byte");
				b.append("(");
				b.append(field_num);
				b.append(");\n");
			} else if (type_name == "short") {
				b.append(indent + f.getName() + " =  rs.get");
				b.append("Short");
				b.append("(");
				b.append(field_num);
				b.append(");\n");
			} else if (type_name == "long") {
				b.append(indent + f.getName() + " =  rs.get");
				b.append("Long");
				b.append("(");
				b.append(field_num);
				b.append(");\n");
			} else if (type_name == "String"){
				b.append(indent + f.getName() + " =  rs.get");
				b.append("String");
				b.append("(");
				b.append(field_num);
				b.append(");\n");
			} else if ( f.getType() instanceof SqlIntegerType) {
//				id.read((new BigInteger(new Long(rs.getInt(1)).toString())).toByteArray());
				
				    b.append("byte_stream.reset();\n");	
	                b.append(f.getName() + ".read((new BigInteger(new Long("); 
	                b.append("rs.get");
					b.append("Int");
					b.append("(");
					b.append(field_num);
					b.append("))");
					b.append(".toString())).toByteArray());\n");
			}else if( f.getType() instanceof SqlTextType ){
				b.append(indent + f.getName() + ".set_string_value("+ " rs.get");
				b.append("String");
				b.append("(");
				b.append(field_num);
				b.append("));\n");
				
			}else {
				b.append(indent + f.getName() + ".read("+ " rs.get");
				b.append("Bytes");
				b.append("(");
				b.append(field_num);
				b.append("));\n");
			}
			
			++field_num;
			
			if (!(i.hasNext()))
				break;
		}
		return b.toString();
	}
	
	private String getSetRecList(CompoundType ctype, String indent) {
		if (!ctype.hasFields()) {
			return "";
		}
		// we will check whether the ctype has a fields
		Iterator i = (Iterator) ctype.getFields();
		StringBuffer b = new StringBuffer();
		String type_name;
		int field_num = 1;
		
		while (true) {
			
			Field f = (Field) i.next();
			type_name = printType(f.getType());
			if (type_name == "int"){ 
				b.append(indent + "ps_insert.set");
				b.append("Int");
				b.append("(");
				b.append(field_num + "," + f.getName());
				b.append(");\n");
			} else if (type_name == "byte") {
				b.append(indent + "ps_insert.set");
				b.append("Byte");
				b.append("(");
				b.append(field_num + "," + f.getName());
				b.append(");\n");
			} else if (type_name == "short") {
				b.append(indent + "ps_insert.set");
				b.append("Short");
				b.append("(");
				b.append(field_num + "," + f.getName());
				b.append(");\n");
			} else if (type_name == "long") {
				b.append(indent + "ps_insert.set");
				b.append("Long");
				b.append("(");
				b.append(field_num + "," + f.getName());
				b.append(");\n");
			} else if (type_name == "String"){
				b.append(indent + "ps_insert.set");
				b.append("String");
				b.append("(");
				b.append(field_num + "," + f.getName());
				b.append(");\n");
			} else if ( f.getType() instanceof SqlIntegerType) {
				
				b.append("byte_stream.reset();\n");	
                b.append(f.getName() + ".read((new BigInteger(new Long("); 
                b.append("rs.get");
				b.append("Int");
				b.append("(");
				b.append(field_num);
				b.append("))");
				b.append(".toString())).toByteArray());\n");
			}else if( f.getType() instanceof SqlTextType ){
				b.append(indent + "byte_stream.reset();\n");
				b.append(indent + f.getName() + ".write(data_stream);\n");
				
				b.append(indent + "ps_insert.set");
				b.append("String");
				b.append("(");
				b.append(field_num + "," + f.getName() +".get_string_value()");
				b.append(");\n");
				
			}else if ( f.getType() instanceof ArrayType){
				b.append(indent + "ps_insert.set");
				b.append("Bytes");
				b.append("(");
				b.append(field_num + "," + f.getName());
				b.append(".get_bytes()");
				b.append(");\n");
				
			}else {
				b.append(indent + "byte_stream.reset();\n");
				b.append(indent + f.getName() + ".write(data_stream);\n");
				
				b.append(indent + "ps_insert.set");
				b.append("Bytes");
				b.append("(");
				b.append(field_num + "," );
				b.append("byte_stream.toByteArray()");
				b.append(");\n");
			}
			
			++field_num;
			
			if (!(i.hasNext()))
				break;
		}
		return b.toString();
	}
	
	private String getSetUpdateVal(CompoundType ctype, String indent) {
		if (!ctype.hasFields()) {
			return "";
		}
		// we will check whether the ctype has a fields
		Iterator i = (Iterator) ctype.getFields();
		StringBuffer b = new StringBuffer();
		String type_name;
		int field_num = 1;
		
		while (true) {
			
			Field f = (Field) i.next();
			type_name = printType(f.getType());
			if (type_name == "int"){ 
				b.append(indent + "ps_update.set");
				b.append("Int");
				b.append("(");
				b.append(field_num + "," + f.getName());
				b.append(");\n");
			} else if (type_name == "byte") {
				b.append(indent + "ps_update.set");
				b.append("Byte");
				b.append("(");
				b.append(field_num + "," + f.getName());
				b.append(");\n");
			} else if (type_name == "short") {
				b.append(indent + "ps_update.set");
				b.append("Short");
				b.append("(");
				b.append(field_num + "," + f.getName());
				b.append(");\n");
			} else if (type_name == "long") {
				b.append(indent + "ps_update.set");
				b.append("Long");
				b.append("(");
				b.append(field_num + "," + f.getName());
				b.append(");\n");
			} else if (type_name == "String"){
				b.append(indent + "ps_update.set");
				b.append("String");
				b.append("(");
				b.append(field_num + "," + f.getName());
				b.append(");\n");
			} else if ( f.getType() instanceof SqlIntegerType) {
				b.append(indent + "byte_stream.reset();\n");
				b.append(indent + f.getName() + ".write(data_stream);\n");
				
				b.append(indent + "ps_update.set");
				b.append("Long");
				b.append("(");
				b.append(field_num + "," + "(new BigInteger(byte_stream.toByteArray())).longValue()");
				b.append(");\n");
			}else if( f.getType() instanceof SqlTextType ){
				b.append("String");
				b.append("(");
				b.append(field_num + "," + f.getName() +".get_string_value()");
				b.append(");\n");
				
			}else if( f.getType() instanceof ArrayType){
				b.append(indent + "ps_update.set");
				b.append("Bytes");
				b.append("(");
				b.append(field_num + "," + f.getName());
				b.append(".get_bytes()");
				b.append(");\n");
				
			}else{
				
				b.append(indent + "byte_stream.reset();\n");
				b.append(indent + f.getName() + ".write(data_stream);\n");
				
				b.append(indent + "ps_update.set");
				b.append("Bytes");
				b.append("(");
				b.append(field_num + "," );
				b.append("byte_stream.toByteArray()");
				b.append(");\n");
				
			}
			
			++field_num;
			
			if (!(i.hasNext()))
				break;
		}
		return b.toString();
	}
	
	private String getQmarks(CompoundType ctype) {
		if (!ctype.hasFields()) {
			return "";
		}
		// we will check whether the ctype has a fields
		Iterator i = (Iterator) ctype.getFields();
		StringBuffer b = new StringBuffer();
		String type_name;
		
		while (true) {
			i.next();
			b.append("?");
			if (!(i.hasNext()))
				break;
			b.append(",");
			
		}
		
		return b.toString();
	}
	
	private String getTypeList_call_set_fields(CompoundType ctype, String indent) {
		if (!ctype.hasFields()) {
			return "";
		}
		// we will check whether the ctype has a fields
		Iterator i = (Iterator) ctype.getFields();
		StringBuffer b = new StringBuffer();
		String type_name;
		
		// packet_num = parcel_analizer.getPacket_num();
		// content_length = parcel_analizer.getContent_length();
		// content = parcel_analizer.getContent();
		//		
		while (true) {
			Field f = (Field) i.next();
			type_name = printType(f.getType());
			
			b.append(indent + "get" + f.getName().toUpperCase().charAt(0));
			b.append(f.getName().substring(1));
			b.append("();\n");
			
			if (!(i.hasNext()))
				break;
		}
		return b.toString();
	}
	
	private String getTypeList_call_set_fields_union(CompoundType ctype,String indent) {
		if (!ctype.hasFields()) {
			return "";
		}
		// we will check whether the ctype has a fields
		Iterator i = (Iterator) ctype.getFields();
		StringBuffer b = new StringBuffer();
		String type_name;
		
		// packet_num = parcel_analizer.getPacket_num();
		// content_length = parcel_analizer.getContent_length();
		// content = parcel_analizer.getContent();
		//		
		
		while (true) {
			
			Field f = (Field) i.next();
			type_name = printType(f.getType());
			
			b.append(indent + "if(" + "_" + ctype.getName() + ".is");
			b.append(f.getName().toUpperCase().charAt(0));
			b.append(f.getName().substring(1));
			b.append("())");
			
			b.append(" " + f.getName() + " = ");
			b.append("get");
			b.append(f.getName().toUpperCase().charAt(0));
			b.append(f.getName().substring(1));
			b.append("();\n");
			
			if (!(i.hasNext()))
				break;
		}
		return b.toString();
	}
	
	private void emitParameterMembers(CompoundType ctype, String indent) {
		if (!ctype.hasParameters()) {
			return;
		}
		out
		.println(indent
				+ "/* additional parameters this type depends on */");
		Iterator i = (Iterator) ctype.getParameters();
		while (i.hasNext()) {
			Parameter p = (Parameter) i.next();
			out.println(indent + printType(p.getType()) + " " + p.getName()
					+ ";");
		}
	}
	
	
	
	private void emitSourceConstructor(CompoundType type, String indent) {
		
		String createTypeList_schema = getTypeList_with_schema(type,indent);
		String initList = getInitList(type,indent);
		
		// ? consructor() and schema().
		
		out.println(indent + "public " + type.getName() + "(String __filename" /*
		* +
		* aplistwtypes
		*/
				+ ")" + " throws " + EXCEPTION + ", ClassNotFoundException"
				+ " {");
		
		out.println(indent + "");
		out.println(indent + "   Class.forName(\"org.sqlite.JDBC\");");
		out
		.println(indent
				+ "   this.conn = DriverManager.getConnection(\"jdbc:sqlite:\" + __filename);");
		out.println(indent + "   this.stmt = conn.createStatement(); ");
		
		out.println(indent + "");
		out.println();
		out.println(indent + "  }");
		out.println();
		
		// ? emit schema() of Database type
		out.println(indent + "public " + " int  " + "createSchema()"
				+ " throws " + EXCEPTION + " {");
		out.println(indent + "");
		out.println(indent + " stmt.execute( \"CREATE TABLE "
				+ "meta_data" + "(var_name ,var_value)" + "\");");
		// you can make a var_name "unique" for avoiding duplication.
		out.println(indent + "");
		out.println(createTypeList_schema);
		out.println(indent + "");
		out.println(indent + " return 0; // for later use");
		out.println(indent + "}");
		out.println();
		out.println(indent + "public void sql_init() {");
		
		out.println(indent + "try {");
		out.println(indent + "    createSchema();");
		
		out.println(indent + "} catch (SQLException e) {");
		out.println(indent + "//			e.printStackTrace();");
		out.println(indent + "}");
		
		out.println(initList );
		
		out.println(indent + "}");
		
		// It's not needed any more but for just keep in mind...
		// out.println(indent + " public PreparedStatement
		// get_prepare_statement(String statement) throws SQLException{");
		// out.println(indent + " return conn.prepareStatement(statement);");
		// out.println(indent + " }");
		// out.println();
		//		
		// out.println(indent + " public Statement get_statement() throws
		// SQLException{");
		// out.println(indent + " return conn.createStatement();");
		// out.println(indent + " }");
		out.println();
		
	}
	
	private void emitOtherSourceConstructor(CompoundType type, String indent) {
		
		
//		String reverseList = getReverseList(type,indent);
		
		out
		.println(indent
				+ "  ByteArrayOutputStream byte_stream = new ByteArrayOutputStream();");
		out
		.println(indent
				+ "  DataOutputStream data_stream = new DataOutputStream(byte_stream);");
		out.println(indent + "boolean is_little_endian;");
		
		
		out.println(indent + "public " + "byte[] get_bytes()" + "throws "
				+ IO_EXCEPTION + ", " + EXCEPTION + "{");
		out.println(indent + " ");
		out.println(indent + "  byte_stream.reset();");
//		out.println(indent + "  if(!is_little_endian)  write(data_stream);");
//		out.println(indent + "  else{ ");
//		out.println(indent + "     reverse();");
		out.println(indent + "     this.write(data_stream);");
//		out.println(indent + "     reverse();");
//		out.println(indent + "     }");
		
		// out.println(indent + createTypeList_get_fields);
		out.println(indent + "       return byte_stream.toByteArray();");
		
		out.println();
		out.println(indent + "}");
		out.println();
		
		out.println(indent + "public " + "void set_bytes(byte[] value)"
				+ "throws " + IO_EXCEPTION + " {");
		out.println(indent + " ");
		out.println(indent + "   byte_stream.reset();");
		out.println(indent + "   this.read(value);");
//		out.println(indent + "   if(is_little_endian) reverse();");
		// out.println(indent + indent + createTypeList_set_fields);
		
		out.println();
		out.println(indent + "}");
		out.println();
		
//		out.println(indent + "private void reverse() { ");
//		out.println(indent + reverseList);
//		
//		
//		out.println();
//		out.println(indent + "}");
		out.println();
	}
	
	private void emitTableSourceConstructor(CompoundType type, String indent) {
		// String createList = getTypeNameList(type);
		String type_name = type.getName();
		String Constraint_list = printConstraint(type);
		String blobInfoInsertMeta = getBlobInfoInsertMeta(type,indent);
		
		
		// ? consructor() and schema().
		out.println(indent + "public " + "void "
				+ "createSchema() " + " throws " + EXCEPTION
				+ "{");
		out.println(indent + " ");
		if (type_name == null) {
			System.out.println("[debug] error in createSchema of "
					+ type.getName() + " : It have no elements at all");
		} else if (Constraint_list == "") {
			out.println(indent + "      stmt.execute( \"CREATE TABLE "
					+ type.getName() + "(\"   "
					+ "+ fields.create_schema_str +" + "\" )\");");
		} else {
			out.println(indent + "      stmt.execute( \"CREATE TABLE "
					+ type.getName() + "(\"   "
					+ "+ fields.create_schema_str +" + "\", "
					+ printConstraint(type) + " )\");");
		}
		printIndex(type, indent);
		out.println();
		printMetadata(type, indent);
		out.println();
		out.println(indent + "}");
	}
	
	private String printConstraint(CompoundType ctype) {
		if (!ctype.hasConstraint_sql_fields()) {
			return "";
		}
		Iterator i = (Iterator) ctype.getConstraint_sql_fields();
		StringBuffer b = new StringBuffer();
		
		while (true) {
			Field f = (Field) i.next();
			
			b.append(printNode(f.getSqlFieldContent()));
			if (!(i.hasNext()))
				break;
			b.append(",");
			
		}
		return b.toString();
		
	}
	
	private void printIndex(CompoundType ctype, String indent) {
		if (!ctype.hasIndex_fields()) {
			return;
		}
		Iterator i = ctype.getIndex_fields();
		String cur_index_str;
		while (i.hasNext()) {
			Field f = (Field) i.next();
			cur_index_str = printNodeIndex(f.getSqlFieldContent());
			
			if (is_unique == true) {
				out.println(indent + "     stmt.execute( \"CREATE "
						+ "UNIQUE INDEX " + f.getName() + " ON "
						+ ctype.getName() + "("
						+ printNodeIndex(f.getSqlFieldContent()) + ")\");");
				is_unique = false;
			} else {
				out.println(indent + "     stmt.execute( \"CREATE " + "INDEX "
						+ f.getName() + " ON " + ctype.getName() + "("
						+ printNodeIndex(f.getSqlFieldContent()) + ")\");");
			}
			
		}
	}
	
	private void printMetadata(CompoundType ctype, String indent) {
		
		if (!ctype.hasFields()) {
			return;
		}
		Iterator i = ctype.getFields();
		while (i.hasNext()) {
			Field f = (Field) i.next();
			if (f.getSqlBlobCompressionStr() != null) {
				out.println(indent + "     stmt.execute( \"INSERT INTO "
						+ "meta_data" + " VALUES( " + "\\\"BLOB_COMPRESS_"
						+ ctype.getName() + "_" + f.getName() + "\\\",\\\""
						+ f.getSqlBlobCompressionStr() + "\\\" )\");");
			}
			if (f.getSqlBlobEncodingStr() != null) {
				out.println(indent + "     stmt.execute( \"INSERT INTO "
						+ "meta_data" + " VALUES( " + "\\\"BLOB_ENCODE_"
						+ ctype.getName() + "_" + f.getName() + "\\\",\\\""
						+ f.getSqlBlobEncodingStr() + "\\\" )\");");
			}
			
		}
		
	}
	
	private void emitFieldsSourceConstructor(CompoundType type, String indent) {
		
		String createTypeList_fields = getTypeList_call_get_fields(type,indent);
		String createQmarks = getQmarks(type);
		String createSetRecList = getSetRecList(type,indent);
		String createList = getTypeNameList(type,indent);
		String createUpdateList = getTypeNameUpdateList(type,indent);
		String base_key_list = getBaseKeyList(type,indent);
		String createUpdateStr = getCreateUpdateStr(type,indent);
		String setUpdateStmt   = getSetUpdateStmt(type,indent);
		String setUpdateVal    = getSetUpdateVal(type,indent);
		String createReadStr   = getCreateReadStr(type,indent);
		String setReadStmt      = getSetReadStmt(type,indent);
		String setReadList     = getSetReadList(type,indent);
		String littleEndianList     = getLittleEndianList(type,indent);
//		String blobInfoInsertMeta    = getBlobInfoInsertMeta(type,indent);
//		String constructorTypeNameList = getConstructorTypeNameList(type,indent);
		
		// num_primary_key
		// ? consructor() and schema().
		
		out.println(indent
				+ "ByteArrayOutputStream byte_stream = new ByteArrayOutputStream();");
		out.println(indent
				+ "DataOutputStream data_stream = new DataOutputStream(byte_stream);");
		
		out.println(indent + "PreparedStatement ps_insert;");
		out.println(indent + "PreparedStatement ps_select;");
		out.println(indent + "PreparedStatement ps_update;");
		out.println(indent + "ResultSet rs;");
		out.println();
		out.println(base_key_list);
		out.println(indent + "public boolean is_rowid_base_key;");
		out.println(indent + "public int rowid;");
		
		out.println(indent + "String create_schema_str = \"" + createList
				+ "\";");
		out.println();
//		out.println(indent + type.getName() + "(){");
//		
//		out.println(constructorTypeNameList);
//		
//		out.println(indent + "}");
		
		out.println(indent + "public void sql_read() throws SQLException, IOException{");
		out.println(indent + "String select_str = \"" + "Select * from " + type.getParent().getName()
				+ " Where \";");
		out.println(indent + "boolean base_key_exist = false;");
		out.println(createReadStr);
		out.println(indent + "if(!base_key_exist) System.out.println(\"error - no based key exist \");");
		out.println(indent + "select_str = select_str.substring(0, select_str.length() - 4);");
		out.println(indent + "ps_select = conn.prepareStatement(select_str);");
		out.println();
		out.println(setReadStmt);
		out.println(indent + "rs = ps_select.executeQuery();");
		out.println(setReadList);
		out.println(indent + "ps_select.close();");
		out.println(indent + "}");
		out.println();
		
		out.println(indent + "public boolean sql_read_next() throws SQLException, IOException{");
		
		out.println(indent + "if(rs == null || rs.next() == false)");
		out.println(indent + "return false;");
		out.println(setReadList);
		out.println(indent + "ps_select.close();");
		out.println(indent + "return true;");
		out.println(indent + "}");
		out.println();
		
		
		out.println(indent + "public void sql_update() throws SQLException, IOException{");
		out.println();
		out.println(indent + "String update_str = \"" + "Update " + type.getParent().getName() + " Set " 
				+ createUpdateList + " Where " + "\";");
		out.println(indent + "boolean base_key_exist = false;");
		out.println();
		out.println(createUpdateStr);
		out.println(indent + "if(!base_key_exist) System.out.println(\"error - no based key exist \");");
		out.println(indent + "update_str = update_str.substring(0, update_str.length() - 4);");
		out.println(indent + "ps_update = conn.prepareStatement(update_str);");
		out.println();
		out.println(setUpdateStmt);
		out.println();
		out.println(setUpdateVal);
		out.println();
		out.println(indent + "ps_update.executeUpdate();");
		out.println(indent + "ps_update.close();");
		out.println(indent + "}");
		
		
		
		out.println(indent + "public void sql_init() {");
		
		out.println(indent + "  try {");
		
		out.println(indent + "       ps_insert = conn.prepareStatement(\"Insert into "
				+ type.getParent().toString() + " values(" + createQmarks + ");\");");
		
		// blob with meta_data input.
//		out.println(blobInfoInsertMeta);
		printMetadata(type,indent);
		out.println(indent + "  } catch (SQLException e) {");
		out.println(indent + "  e.printStackTrace();");
		out.println(indent + "  }");
		out.println(indent + littleEndianList);
		out.println(indent + "}");
		
		
		out.println(indent + "public " + "byte[] get_bytes()" + "throws "
				+ IO_EXCEPTION + ", " + EXCEPTION + "{");
		out.println(indent + " ");
		// out.println(indent + createTypeList_fields);
		out.println(indent + "byte_stream.reset();");
		out.println(indent + "this.write(data_stream);");
		out.println(indent + "       return byte_stream.toByteArray();");
		
		out.println();
		out.println(indent + "  }");
		out.println();
		
		out.println(indent + "public void sql_write()" + " throws "
				+ IO_EXCEPTION + ", " + EXCEPTION + " {");
		
		out.println(indent + createSetRecList);
		// out.println(indent + "read")
		out.println(indent + "ps_insert.executeUpdate();");
		out.println(indent + "ps_insert.close();");
		out.println();
		out.println(indent + "}");
		out.println();
		
	}
	
	/**
	 * returns symbol or formula for array length or null if array is
	 * variable-length
	 */
	String computeLengthExpression(Context ctxt, CompoundType ctype,
			ArrayType atype) {
		if (atype.upperBound == null)
			return "-1";
		return null;
	}
	
	/*
	 * get lower bound expression for a given array type hashes expressions, if
	 * not found, computes it for context and ctype BUG: will always return the
	 * first expression without checking whether ctxt/ctype remained the same
	 */
	static String getLowerBound(ArrayType atype, Context ctxt,
			CompoundType ctype) {
		String lowerBound = (String) lowerBoundHash.get(atype);
		
		return lowerBound;
	}
	
	// maps ArrayType to String for lowerBound
	// this shouldn't be static, but passed on to JavaExpressionEmitters
	// where necessary
	static HashMap lowerBoundHash = new HashMap();
	
	private String protect(String input) {
		StringBuffer output = new StringBuffer();
		for (int i = 0; i < input.length(); i++) {
			char c;
			switch (c = input.charAt(i)) {
			case '\n':
				output.append("\\n");
				break;
			case '\t':
				output.append("\\t");
				break;
			case '\\':
			case '"':
				output.append("\\" + c);
				break;
				
			default:
				output.append(c);
			}
		}
		return output.toString();
	}
	
	
	
	void emitStructConstructor(CompoundType type, String indent) {
		// null and basic constructor for class
		out.print(indent + "  " + type.getName() + "(");
		out.println(") { }");
		
		// non-null and basic constructor for class( only set the fields )
		out.print(indent + "  " + type.getName() + "(");
		for (Iterator i = type.getFields(); i.hasNext();) {
			Field f = (Field) i.next();
			out.print(printFieldType(f) + " ");
			out.print(f.getName());
			if (i.hasNext()) {
				out.print(",");
			}
		}
		out.println(") {");
		for (Iterator i = type.getFields(); i.hasNext();) {
			Field f = (Field) i.next();
			out.println(indent + "    this." + f.getName() + " = "
					+ f.getName() + ";");
		}
		out.println(indent + "  }");
	}
	
	static boolean useJavaBeansStyle = true;
	
	static String makeAccessor(String name) {
		if (useJavaBeansStyle) {
			return name.substring(0, 1).toUpperCase()
			+ name.substring(1, name.length());
		} else {
			throw new Error("don't know what style to use");
		}
	}
	
	void emitStructFieldDefinitions(CompoundType type, String indent) {
		for (Iterator i = type.getFields(); i.hasNext();) {
			Field f = (Field) i.next();
			String fname = f.getName();
			String mname = makeAccessor(fname);
			out.println(indent + "  " + printFieldType(f) + " " + fname + ";");
			out.println(indent + "  public " + printFieldType(f) + " get"
					+ mname + "() { return " + fname + "; }");
			out.println(indent + "  public void set" + mname + "("
					+ printFieldType(f) + " " + fname + ") { this." + fname
					+ " = " + fname + "; }");
			out.println();
		}
	}
	
	static String printFieldType(Field f) {
		TypeInterface ftype = f.getType();
		if (ftype instanceof TypeInstantiation) {
			ftype = ((TypeInstantiation) ftype).baseType;
		}
		return printType(ftype);
	}
	
	static String fullyQualifiedTypeName(CompoundType fctype) {
		String res = Sqlite3Emitter.printTypeBis(fctype);
		
		// not exactly very efficient...
		while (fctype.getParent() != null
				&& (fctype = fctype.getParent()).getParent() != null) {
			res = Sqlite3Emitter.printTypeBis(fctype) + "." + res;
		}
		return res;
	}
	
	static String printType(TypeInterface type) {
		if (type instanceof CompoundType) {
			return fullyQualifiedTypeName((CompoundType) type);
		} else {
			return printTypeBis(type);
		}
	}
	
	// This method can deal the array type but I think It is not possible to use
	// array in DB.
	static private String printTypeBis(TypeInterface type) {
		TypeInterface ftype = type;
		
		if (ftype instanceof ArrayType) {
			ArrayType atype = (ArrayType) ftype;
			TypeInterface etype = atype.getElementType();
			try {
				BuiltinType btype = BuiltinType.getBuiltinType(etype);
				switch (btype.kind) {
				case DSConstants.INT8:
				case DSConstants.UINT8:
					return "ByteArray";
					
				case DSConstants.INT16:
				case DSConstants.LEINT16:
				case DSConstants.UINT16:
				case DSConstants.LEUINT16:
					return "ShortArray";
					
				case DSConstants.INT32:
				case DSConstants.LEINT32:
				case DSConstants.UINT32:
				case DSConstants.LEUINT32:
					return "IntArray";
					
				case DSConstants.INT64:
				case DSConstants.LEINT64:
				case DSConstants.UINT64:
				case DSConstants.LEUINT64:
					return "LongArray";
					
				case DSConstants.BIT:
					return "Array /* of " + etype + " */";
					
				default:
					throw new InternalError("bad type found");
				}
			} catch (ClassCastException _) {
				return "ObjectArray /* of " + etype + " */";
			}
		} else if (ftype instanceof BuiltinType || ftype instanceof SetType) {
			BuiltinType btype = BuiltinType.getBuiltinType(ftype);
			return mapKindToType(btype.kind);
		}
		if (ftype instanceof StringType) {
			return "String";
		} else {
			return ftype.toString();
		}
	}
	
	static String mapKindToType(int kind) {
		
		
		
		
		switch (kind) {
//		case DSConstants.INT8:
//		case DSConstants.UINT8:
//		return "byte";
//		
//		case DSConstants.INT16:
//		case DSConstants.LEINT16:
//		case DSConstants.UINT16:
//		case DSConstants.LEUINT16:
//		return "short";
//		
//		case DSConstants.INT32:
//		case DSConstants.LEINT32:
//		case DSConstants.UINT32:
//		case DSConstants.LEUINT32:
//		return "int";
//		
//		case DSConstants.INT64:
//		case DSConstants.LEINT64:
//		case DSConstants.UINT64:
//		case DSConstants.LEUINT64:
//		return "long";
		
		case DSConstants.INT8:
			return "byte";
			
		case DSConstants.UINT8:
		case DSConstants.INT16:
		case DSConstants.LEINT16:
			return "short";
			
		case DSConstants.UINT16:
		case DSConstants.LEUINT16:
		case DSConstants.INT32:
		case DSConstants.LEINT32:
			return "int";
			
		case DSConstants.UINT32:
		case DSConstants.LEUINT32:
		case DSConstants.INT64:
		case DSConstants.LEINT64:
			return "long";
			
		case DSConstants.UINT64:
		case DSConstants.LEUINT64:
		case DSConstants.BIT:
			return "BigInteger";
			
//			case DSConstants.BIT:
//			return "BitField";
			
		default:
			throw new InternalError("bad type found " + kind);
		}
	}
	
	HashMap getArmNameMapping(CompoundType ctype) {
		return (HashMap) ctype2armNameMapping.get(ctype);
	}
	
	HashMap getArmTypeMapping(CompoundType ctype) {
		return (HashMap) ctype2armTypeMapping.get(ctype);
	}
	
	static String printNode(Node n) {
		StringWriter sw = new StringWriter();
		TreeDumper dumper = new datascript.visitor.TreeDumper(sw);
		dumper.startAtNextToken();
		n.accept(dumper);
		String out = sw.toString();
		
		while (out.startsWith(" ")) {
			out = out.substring(1);
		}
		
		out = out.replaceAll("\"", "");
		return out;
	}
	
	static String printNode(Vector n) {
		StringBuffer b = new StringBuffer();
		
		for (int i = 0; i < n.size(); i++) {
			b.append(" ");
			b.append(printNode((Node) n.elementAt(i)));
		}
		
		return b.toString();
	}
	
	static String printBlobNode(Vector n) {
		return null;
	}
	
	static String printNodeIndex(Node n) {
		StringWriter sw = new StringWriter();
		TreeDumper dumper = new datascript.visitor.TreeDumper(sw);
		dumper.startAtNextToken();
		n.accept(dumper);
		String out = sw.toString();
		while (out.startsWith(" ")) {
			out = out.substring(1);
		}
		out = out.toUpperCase();
		// It is not completely implemented yet. It need kind of parsing....
		if (out.contains("\"UNIQUE\"") == true) {
			if (out.contains("\"UNIQUE\",")) {
				out = out.replace("\"UNIQUE\",", "");
			} else
				out = out.replace(",\"UNIQUE\"", "");
			is_unique = true;
		}
		if (out.contains("\"UNIQUE\"") == true)
			System.out
			.println("ERROR: UNIQUE constraint keyword used twice or more.");
		
		out = out.replaceAll("\"", "");
		return out;
	}
	
}
