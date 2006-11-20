//
// Generated by JTB 1.2.2
//

package datascript.syntaxtree;

/**
 * Grammar production:
 * <PRE>
 * nodeOptional -> [ Quantifier() ]
 * conditionalExpression -> ConditionalExpression()
 * </PRE>
 */
public class QuantifiedExpression implements Node {
   public NodeOptional nodeOptional;
   public ConditionalExpression conditionalExpression;

   public QuantifiedExpression(NodeOptional n0, ConditionalExpression n1) {
      nodeOptional = n0;
      conditionalExpression = n1;
   }

   public void accept(datascript.visitor.Visitor v) {
      v.visit(this);
   }
   public Object accept(datascript.visitor.ObjectVisitor v, Object argu) {
      return v.visit(this,argu);
   }
}

