/*
 * LinkError.java
 *
 * @author: Godmar Back
 * @version: $Id: LinkError.java,v 1.1.1.1 2003/05/29 22:58:25 gback Exp $
 */
package datascript.ast;

public class LinkError extends LineError {

    public LinkError () {
        super();
    }

    public LinkError (String s) {
        super(s);
    }

    public LinkError (TokenAST n, String s) {
        super(n, s);
    }
}
