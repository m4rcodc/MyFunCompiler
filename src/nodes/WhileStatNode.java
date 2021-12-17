package nodes;

import Visitor.ISyntaxVisitable;
import Visitor.ISyntaxVisitor;
import org.w3c.dom.Element;

public class WhileStatNode implements StatNode, ISyntaxVisitable {

    public String name = null;
    public ExprNode expr = null;
    public BodyNode bn = null;

    public WhileStatNode(String name,ExprNode expr,BodyNode bodynode){
        this.name=name;
        this.expr=expr;
        this.bn=bodynode;
    }

    @Override
    public Element accept(ISyntaxVisitor v) {
        return v.visit(this);
    }

}
