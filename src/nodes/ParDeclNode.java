package nodes;

import Visitor.*;
import leafs.LeafID;
import org.w3c.dom.Element;

public class ParDeclNode implements ISyntaxVisitable,ISemanticVisitable, ICVisitable {

    public String name = null;
    public TypeNode typeNode = null;
    public LeafID leaf = null;
    public ModeParNode mod = null;

    public ParDeclNode(String name, ModeParNode mod, TypeNode typeNode, LeafID leaf){
        this.name=name;
        this.mod=mod;
        this.typeNode=typeNode;
        this.leaf=leaf;
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
