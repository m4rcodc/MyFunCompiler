package nodes;

import Visitor.*;
import org.w3c.dom.Element;

public class TypeNode implements ISyntaxVisitable, ISemanticVisitable, ICVisitable {

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

    @Override
    public void accept(ISemanticVisitor v) { v.visit(this); }

    @Override
    public void accept(ICVisitor v) { v.visit(this); }
}
