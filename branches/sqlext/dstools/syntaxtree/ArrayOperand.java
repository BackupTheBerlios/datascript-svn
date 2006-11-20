//
// Generated by JTB 1.2.2
//

package datascript.syntaxtree;

/**
 * Grammar production:
 * <PRE>
 * nodeToken -> "["
 * expression -> Expression()
 * nodeToken1 -> "]"
 * </PRE>
 */
public class ArrayOperand implements Node {
   public NodeToken nodeToken;
   public Expression expression;
   public NodeToken nodeToken1;

   public ArrayOperand(NodeToken n0, Expression n1, NodeToken n2) {
      nodeToken = n0;
      expression = n1;
      nodeToken1 = n2;
   }

   public ArrayOperand(Expression n0) {
      nodeToken = new NodeToken("[");
      expression = n0;
      nodeToken1 = new NodeToken("]");
   }

   public void accept(datascript.visitor.Visitor v) {
      v.visit(this);
   }
   public Object accept(datascript.visitor.ObjectVisitor v, Object argu) {
      return v.visit(this,argu);
   }
}

