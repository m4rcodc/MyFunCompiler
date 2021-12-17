package nodes;

import Visitor.ISyntaxVisitable;
import Visitor.ISyntaxVisitor;
import org.w3c.dom.Element;

public class TypeNode implements ISyntaxVisitable {

    public String name = null;
    public String type = null;

    public TypeNode(String name,String type) {

        this.name = name;
        this.type = type;
    }

    @Override
    public Element accept(ISyntaxVisitor v) {
        return v.visit(this);
    }



}
