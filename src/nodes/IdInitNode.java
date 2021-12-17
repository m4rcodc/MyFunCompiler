package nodes;

import Visitor.ISyntaxVisitable;
import Visitor.ISyntaxVisitor;
import leafs.LeafID;
import org.w3c.dom.Element;

public class IdInitNode implements ISyntaxVisitable {

    public String name = null;
    public LeafID leaf = null;
    public ExprNode expr = null;
    public ConstNode c = null;

    public IdInitNode(LeafID leaf) {
        this.name = "IdInitOp";
        this.leaf = leaf;
        this.expr = null;
        this.c=null;
    }

    public IdInitNode(LeafID leaf, ExprNode expr){
        this.name = "IdInitOp";
        this.leaf = leaf;
        this.expr=expr;
        this.c=null;
    }

    public IdInitNode(LeafID leaf, ConstNode c){
        this.name = "IdInitOpObbl";
        this.leaf = leaf;
        this.c=c;
    }

    @Override
    public Element accept(ISyntaxVisitor v) {
        return v.visit(this);
    }




}
