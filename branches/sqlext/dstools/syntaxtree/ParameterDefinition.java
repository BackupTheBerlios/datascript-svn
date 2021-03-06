//
// Generated by JTB 1.2.2
//

package datascript.syntaxtree;

/**
 * Grammar production:
 * <PRE>
 * typeDeclaration -> TypeDeclaration()
 * nodeToken -> &lt;IDENTIFIER&gt;
 * </PRE>
 */
public class ParameterDefinition implements Node {
   public TypeDeclaration typeDeclaration;
   public NodeToken nodeToken;

   public ParameterDefinition(TypeDeclaration n0, NodeToken n1) {
      typeDeclaration = n0;
      nodeToken = n1;
   }

   public void accept(datascript.visitor.Visitor v) {
      v.visit(this);
   }
   public Object accept(datascript.visitor.ObjectVisitor v, Object argu) {
      return v.visit(this,argu);
   }
}

