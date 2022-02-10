package nodes;

import Visitor.*;
import org.w3c.dom.Element;

public class ModeParNode implements ISyntaxVisitable, ISemanticVisitable, ICVisitable {

    public String mod=null;

    public ModeParNode(String mod){

        this.mod=mod;
    }

    public String getMode(){

        return this.mod;

    }

    public void setMode(String mod) {

        this.mod = mod;
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
