package leafs;

import Visitor.ISyntaxVisitable;
import Visitor.ISyntaxVisitor;
import org.w3c.dom.Element;

public class LeafRealConst implements ISyntaxVisitable {

    public String name;
    public Float value;

    public LeafRealConst(Float value){
        this.name="LeafRealConst";
        this.value=value;
    }

    @Override
    public Element accept(ISyntaxVisitor v) {
        return v.visit(this);
    }


}
