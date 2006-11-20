/*
 * JavaEmitMembershipTests.java
 *
 * @author: Godmar Back
 * @version: $Id: JavaEmitMembershipTests.java,v 1.1.1.1 2003/05/29 22:58:25 gback Exp $
 */
package datascript;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.File;
import java.util.Iterator;
import datascript.syntaxtree.Node;
import datascript.parser.DSConstants;

class JavaBytesEmitMembershipTests {
   String basename;
   static JavaBytesEmitter JavaBytesEmitter;

   // basename for compound type, used during traversal
   private String cbasename;	

   JavaBytesEmitMembershipTests(JavaBytesEmitter JavaBytesEmitter) {
      this.JavaBytesEmitter = JavaBytesEmitter;
   }
   
  
   void emit(CompoundType ctype) throws IOException {
      PrintWriter out = Main.openFile(JavaBytesEmitter.dirname + File.separator + JavaBytesEmitter.CHECKTHAT + ".java");

      if (out == null) {
	 return;
      }
      JavaBytesEmitter.writeHeader(out);
      out.println("public class " + JavaBytesEmitter.CHECKTHAT + " {");
      emitChildren(out, ctype);
      out.println("}");
      out.close();
   }

   void emitChildren(PrintWriter out, CompoundType ctype) throws IOException {
      emitSetTypeMembershipTests(out, ctype);
      Iterator children = ctype.getNestedTypes();
      while (children.hasNext()) {
	 ctype = (CompoundType)children.next();
	 emitChildren(out, ctype);
      }
   }

   private void emitEnumTypeMembershipTests(PrintWriter out, EnumType etype, CompoundType ctype) {
      Value [] val = etype.getValues();
      String ename = cbasename + etype.getName();
      String etname = JavaBytesEmitter.printType(etype.getType());
      String mname = ename + "_members";

      out.println("  private static " + etname + " []"
            + mname + " = new " + etname + "[] {\n    ");
      for (int i = 0; i < val.length; i++) {
	 JavaExpressionEmitter em = new JavaExpressionEmitter(ctype);
         em.value(val[i]);
         em.castTo(BuiltinType.getTypeByTokenKind(DSConstants.INT32),
		   etype.getType());
	 out.print(em.result());
         if (i < val.length - 1) {
            out.print(", ");
         }
      }
      out.println("  };");
      out.println("  static boolean isValid_" + ename
            + "(" + etname + " x) {");
      out.println("    return java.util.Arrays.binarySearch("
            + mname + ", x) >= 0;");
      out.println("  }");
   }

   private void emitBitmaskTypeMembershipTests(PrintWriter out, BitmaskType btype) {
      IntegerValue mask = btype.getMask();
      String bname = cbasename + btype.getName();
      String etname = JavaBytesEmitter.printType(btype.getType());

      String mname = bname + "_mask";
      out.println("  private static " + etname + " " + mname
            + " = 0x" + mask.integerValue().toString(16) + ";");
      out.println("  static boolean isValid_" + bname
            + "(" + etname + " x) {");
      out.println("    return (" + etname + ")("
                                + mname + " | x) == " + mname + ";");
      out.println("  }");
   }

   static String getFullPath(CompoundType ctype) {
      String fname = JavaBytesEmitter.fullyQualifiedTypeName(ctype);
      return fname.replace('.', '_') + "_";
   }

   /**
    * emit test methods for membership to an enum
    */
   private void emitSetTypeMembershipTests(PrintWriter out, CompoundType ctype) {
      Iterator stypes = ctype.getSetTypes();
      if (!stypes.hasNext()) {
         return;
      }
      cbasename = getFullPath(ctype);
      out.println("\n  /* emitting checks for " + cbasename + " */");

      while (stypes.hasNext()) {
         SetType stype = (SetType)stypes.next();
	 Main.assertThat(stype.getCompound() == ctype);
         if (stype instanceof EnumType) {
            emitEnumTypeMembershipTests(out, (EnumType)stype, ctype);
         } else {
            emitBitmaskTypeMembershipTests(out, (BitmaskType)stype);
         }
      }
   }
}
