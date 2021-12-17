package leafs;

import Visitor.ISyntaxVisitable;
import Visitor.ISyntaxVisitor;
import org.w3c.dom.Element;

public class LeafIntegerConst implements ISyntaxVisitable {

    public String name;
    public Integer value;

    public LeafIntegerConst(Integer value) {
        this.value = value;
        this.name = "LeafIntegerConst";
    }

    @Override
    public Element accept(ISyntaxVisitor v) {
        return v.visit(this);
    }

}
