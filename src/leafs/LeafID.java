package leafs;

import Visitor.ISyntaxVisitable;
import Visitor.ISyntaxVisitor;
import org.w3c.dom.Element;

public class LeafID implements ISyntaxVisitable {

    public String name;
    public String value;

    public LeafID(String value) {
        this.value = value;
        this.name = "LeafID";
    }

    @Override
    public Element accept(ISyntaxVisitor v) {
        return v.visit(this);
    }
}
