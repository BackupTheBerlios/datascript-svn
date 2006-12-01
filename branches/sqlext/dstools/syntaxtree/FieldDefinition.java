//
// Generated by JTB 1.2.2
//

package datascript.syntaxtree;

/**
 * Grammar production:
 * <PRE>
 * nodeOptional -> [ Label() ]
 * typeDeclaration -> TypeDeclaration()
 * nodeOptional1 -> [ TypeArgumentList() ]
 * nodeOptional2 -> [ &lt;IDENTIFIER&gt; ]
 * nodeListOptional -> ( ArrayRange() )*
 * nodeOptional3 -> [ FieldInitializer() ]
 * nodeOptional4 -> [ FieldCondition() ]
 * nodeOptional5 -> [ SqlCondition() ]
 * nodeOptional6 -> [ FieldOptionalClause() ]
 * nodeListOptional1 -> ( &lt;STRING&gt; )*
 * </PRE>
 */
public class FieldDefinition implements Node {
   public NodeOptional nodeOptional;
   public TypeDeclaration typeDeclaration;
   public NodeOptional nodeOptional1;
   public NodeOptional nodeOptional2;
   public NodeListOptional nodeListOptional;
   public NodeOptional nodeOptional3;
   public NodeOptional nodeOptional4;
   public NodeOptional nodeOptional5;
   public NodeOptional nodeOptional6;
   public NodeListOptional nodeListOptional1;

   public FieldDefinition(NodeOptional n0, TypeDeclaration n1, NodeOptional n2, NodeOptional n3, NodeListOptional n4, NodeOptional n5, NodeOptional n6, NodeOptional n7, NodeOptional n8, NodeListOptional n9) {
      nodeOptional = n0;
      typeDeclaration = n1;
      nodeOptional1 = n2;
      nodeOptional2 = n3;
      nodeListOptional = n4;
      nodeOptional3 = n5;
      nodeOptional4 = n6;
      nodeOptional5 = n7;
      nodeOptional6 = n8;
      nodeListOptional1 = n9;
   }

   public void accept(datascript.visitor.Visitor v) {
      v.visit(this);
   }
   public Object accept(datascript.visitor.ObjectVisitor v, Object argu) {
      return v.visit(this,argu);
   }
}
