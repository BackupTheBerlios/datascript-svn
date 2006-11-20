//
// Generated by JTB 1.2.2
//

package datascript.syntaxtree;

/**
 * Grammar production:
 * <PRE>
 * nodeChoice -> ConstantExpression()
 *       | "{" TypeValueList() "}"
 * </PRE>
 */
public class TypeValue implements Node {
   public NodeChoice nodeChoice;

   public TypeValue(NodeChoice n0) {
      nodeChoice = n0;
   }

   public void accept(datascript.visitor.Visitor v) {
      v.visit(this);
   }
   public Object accept(datascript.visitor.ObjectVisitor v, Object argu) {
      return v.visit(this,argu);
   }
}

