/**
 * 
 */
package datascript.ast;

import antlr.BaseAST;
import antlr.CommonToken;
import antlr.Token;
import antlr.collections.AST;

/**
 * @author HWellmann
 *
 */
public class TokenAST extends BaseAST
{
    private Token token;

    public TokenAST() 
    {
    }

    public TokenAST(Token tok) 
    {
        token = tok;
    }

    public void initialize(int t, String txt)
    {
        token = new CommonToken(t, txt);
    }

    public void initialize(AST t)
    {
        token = ((TokenAST)t).token;
    }

    public void initialize(Token t)
    {
        token = (CommonToken) t;
    }
///////////////////////////////
    
    public int getLine()
    {
        return token.getLine();
    }
    
    public int getColumn()
    {
        return token.getColumn();
    }

    /** Get the token text for this node */
    public String getText() {
        return token.getText();
    }

    /** Get the token type for this node */
    public int getType() {
        return token.getType();
    }

    /** Set the token text for this node */
    public void setText(String text) {
        token.setText(text);
    }

    /** Set the token type for this node */
    public void setType(int ttype) {
        token.setType(ttype);
    }

    public void accept(Visitor v)
    {
        v.visit(this);
    }
}


