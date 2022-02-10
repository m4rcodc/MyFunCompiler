package nodes;

import Visitor.*;
import org.w3c.dom.Element;

public class WhileStatNode implements StatNode, ISyntaxVisitable, ISemanticVisitable, ICVisitable {

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

    @Override
    public void accept(ISemanticVisitor v) { v.visit(this); }

    @Override
    public void accept(ICVisitor v) { v.visit(this); }

}
