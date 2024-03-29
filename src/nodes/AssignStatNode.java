package nodes;

import Visitor.*;
import leafs.LeafID;
import org.w3c.dom.Element;

public class AssignStatNode implements StatNode, ISyntaxVisitable, ISemanticVisitable, ICVisitable {

    public String name=null;
    public LeafID leaf=null;
    public ExprNode expr=null;

    public AssignStatNode(String name,LeafID leaf,ExprNode expr) {
        this.name=name;
        this.leaf=leaf;
        this.expr=expr;
    }

    @Override
    public Element accept(ISyntaxVisitor v) {
        return v.visit(this);
    }

    @Override
    public void accept(ISemanticVisitor v) { v.visit(this) ;}

    @Override
    public void accept(ICVisitor v) { v.visit(this); }
}
