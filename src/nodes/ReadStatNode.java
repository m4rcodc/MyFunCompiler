package nodes;

import Visitor.*;
import leafs.LeafID;
import org.w3c.dom.Element;

import java.util.ArrayList;

public class ReadStatNode implements StatNode, ISyntaxVisitable, ISemanticVisitable, ICVisitable {

    public String name = null;
    public ArrayList<LeafID> idlist = null;
    public ExprNode expr = null;

    public ReadStatNode(String name, ArrayList<LeafID> idlist, ExprNode expr) {
        this.name=name;
        this.idlist=idlist;
        this.expr=expr;
    }

    public ReadStatNode(String name, ArrayList<LeafID> idlist){
        this.name=name;
        this.idlist=idlist;
        this.expr=null;
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
