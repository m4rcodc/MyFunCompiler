package nodes;

import Visitor.ISyntaxVisitable;
import Visitor.ISyntaxVisitor;
import org.w3c.dom.Element;

public class ReturnStatNode implements StatNode, ISyntaxVisitable {

    public String name = null;
    public ExprNode expr = null;

    public ReturnStatNode(String name,ExprNode expr){
        this.name=name;
        this.expr=expr;
    }

    @Override
    public Element accept(ISyntaxVisitor v) {
        return v.visit(this);
    }
}


