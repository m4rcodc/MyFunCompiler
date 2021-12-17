package nodes;

import Visitor.ISyntaxVisitable;
import Visitor.ISyntaxVisitor;
import leafs.LeafID;
import org.w3c.dom.Element;

public class ExprNode implements ISyntaxVisitable {

    public String name = null;
    public ModeParNode mod;
    public LeafID leaf= null;
    public Object value1 = null;
    public Object value2 = null;

    public ExprNode(String name, Object n) {
        this.name = name;
        this.value1 = n;
        this.mod = null;
        this.value2 = null;
        this.leaf=null;
    }

    public ExprNode(String name, Object n1, Object n2) {
        this.name = name;
        this.value1 = n1;
        this.value2 = n2;
        this.mod = null;
        this.leaf=null;
    }

    public ExprNode(String name,ModeParNode mod,LeafID leaf) {
        this.name =name;
        this.mod =mod;
        this.leaf=leaf;
        value1 = null;
        value2 = null;
    }


    @Override
    public Element accept(ISyntaxVisitor v) {
        return v.visit(this);
    }

}
