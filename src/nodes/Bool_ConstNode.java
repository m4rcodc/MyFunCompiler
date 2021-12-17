package nodes;

import Visitor.ISyntaxVisitable;
import Visitor.ISyntaxVisitor;
import leafs.LeafBool;
import org.w3c.dom.Element;

public class Bool_ConstNode implements ISyntaxVisitable {

    public String name = null;
    public LeafBool leaf = null;

    public Bool_ConstNode(String name, LeafBool leaf){

        this.name=name;
        this.leaf=leaf;
    }

    @Override
    public Element accept(ISyntaxVisitor v) {
        return v.visit(this);
    }
}
