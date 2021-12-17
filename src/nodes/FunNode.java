package nodes;

import Visitor.ISyntaxVisitable;
import Visitor.ISyntaxVisitor;
import leafs.LeafID;
import org.w3c.dom.Element;

import java.util.ArrayList;

public class FunNode implements ISyntaxVisitable {

    public String name;
    public LeafID leaf;
    public ArrayList<ParDeclNode> pardecl;
    public TypeNode typeNode;
    public BodyNode bodyNode;

    public FunNode(String name, LeafID leaf, ArrayList<ParDeclNode> pardecl,TypeNode typeNode, BodyNode bodyNode){
        this.name=name;
        this.leaf=leaf;
        this.pardecl=pardecl;
        this.typeNode=typeNode;
        this.bodyNode=bodyNode;
    }

    public FunNode(String name, LeafID leaf, ArrayList<ParDeclNode> pardecl, BodyNode bodyNode){
        this.name=name;
        this.leaf=leaf;
        this.pardecl=pardecl;
        this.typeNode=null;
        this.bodyNode=bodyNode;
    }

    @Override
    public Element accept(ISyntaxVisitor v) {
        return v.visit(this);
    }



}
