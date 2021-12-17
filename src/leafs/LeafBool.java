package leafs;

import Visitor.ISyntaxVisitable;
import Visitor.ISyntaxVisitor;
import org.w3c.dom.Element;

public class LeafBool implements ISyntaxVisitable {

    public String name;
    public boolean value;

    public LeafBool(boolean value) {
        this.value = value;
        this.name = "LeafBool";
    }
    @Override
    public Element accept(ISyntaxVisitor v) {
        return v.visit(this);
    }
}
