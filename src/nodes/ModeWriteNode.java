package nodes;

import Visitor.ISyntaxVisitable;
import Visitor.ISyntaxVisitor;
import org.w3c.dom.Element;

public class ModeWriteNode implements ISyntaxVisitable {

    public String name=null;

    public ModeWriteNode(String name){

        this.name = name;
    }

    @Override
    public Element accept(ISyntaxVisitor v) {
        return v.visit(this);
    }



}
