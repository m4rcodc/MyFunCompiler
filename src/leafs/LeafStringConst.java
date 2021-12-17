package leafs;

import Visitor.ISyntaxVisitable;
import Visitor.ISyntaxVisitor;
import org.w3c.dom.Element;

public class LeafStringConst implements ISyntaxVisitable {

    public String name;
    public String value;

    public LeafStringConst(String value){
        this.value=value;
        this.name = "LeafStringConst";
    }

    @Override
    public Element accept(ISyntaxVisitor v) {
        return v.visit(this);
    }

}
