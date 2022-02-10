package nodes;

import Visitor.*;
import leafs.LeafID;
import org.w3c.dom.Element;

import java.util.ArrayList;

public class FunNode implements ISyntaxVisitable, ISemanticVisitable, ICVisitable {

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

    public void setType(String type){
        this.typeNode.type = type;
    }

    public TypeNode getTypeNode(){
        return this.typeNode;
    }

    @Override
    public Element accept(ISyntaxVisitor v) {
        return v.visit(this);
    }

    @Override
    public void accept(ISemanticVisitor v) { v.visit(this);}

    @Override
    public void accept(ICVisitor v) { v.visit(this); }



}
