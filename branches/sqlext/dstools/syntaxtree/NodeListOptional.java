//
// Generated by JTB 1.2.2
//

package datascript.syntaxtree;

import java.util.*;

/**
 * Represents an optional grammar list, e.g. ( A )*
 */
public class NodeListOptional implements NodeListInterface {
   public NodeListOptional() {
      nodes = new Vector();
   }

   public NodeListOptional(Node firstNode) {
      nodes = new Vector();
      addNode(firstNode);
   }

   public void addNode(Node n) {
      nodes.addElement(n);
   }

   public Enumeration elements() { return nodes.elements(); }
   public Node elementAt(int i)  { return (Node)nodes.elementAt(i); }
   public int size()             { return nodes.size(); }
   public boolean present()      { return nodes.size() != 0; }
   public void accept(datascript.visitor.Visitor v) {
      v.visit(this);
   }
   public Object accept(datascript.visitor.ObjectVisitor v, Object argu) {
      return v.visit(this,argu);
   }

   public Vector nodes;
}

