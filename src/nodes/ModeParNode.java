package nodes;

import Visitor.ISyntaxVisitable;
import Visitor.ISyntaxVisitor;
import org.w3c.dom.Element;

public class ModeParNode implements ISyntaxVisitable {

    public String mod=null;

    public ModeParNode(String mod){
        this.mod=mod;
    }


    @Override
    public Element accept(ISyntaxVisitor v) {
        return v.visit(this);
    }

}
