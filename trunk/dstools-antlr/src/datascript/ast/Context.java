/*
 * Context.java
 *
 * @author: Godmar Back
 * @version: $Id: Context.java,v 1.1.1.1 2003/05/29 22:58:25 gback Exp $
 */
package datascript.ast;

import antlr.collections.AST;

public interface Context {
  public Context getParentScope();
  Object getSymbol(String symbol);
  void setSymbol(AST name, Object symbol);
  void replaceSymbol(String name, Object symbol);
  void postLinkAction(LinkAction act);
  public CompoundType getOwner();
}
