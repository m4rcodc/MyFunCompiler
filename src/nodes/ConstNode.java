package nodes;

import Visitor.ISyntaxVisitable;
import Visitor.ISyntaxVisitor;
import org.w3c.dom.Element;

public class ConstNode implements ISyntaxVisitable {

    public String name = null;
    public Object value1 = null;

    public ConstNode(String name, Object value1){

        this.name=name;
        this.value1 = value1;
    }

    @Override
    public Element accept(ISyntaxVisitor v) {
        return v.visit(this);
    }
}
