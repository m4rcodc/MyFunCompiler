package nodes;

import Visitor.*;
import leafs.LeafID;
import org.w3c.dom.Element;
import support.SymbolTable;
import support.ValueType;

public class IdInitNode implements ISyntaxVisitable, ISemanticVisitable, ICVisitable {

    public String name = null;
    public LeafID leaf = null;
    public ExprNode expr = null;
    public ConstNode c = null;

    public IdInitNode(LeafID leaf) {
        this.name = "IdInitOp";
        this.leaf = leaf;
        this.expr = null;
        this.c=null;
    }

    public IdInitNode(LeafID leaf, ExprNode expr){
        this.name = "IdInitOp";
        this.leaf = leaf;
        this.expr=expr;
        this.c=null;
    }

    public IdInitNode(LeafID leaf, ConstNode c){
        this.name = "IdInitOpObbl";
        this.leaf = leaf;
        this.c=c;
        this.expr = null;
    }

    //Semantic part
    public ValueType type = null;

    public void setType(String type) {
        try {
            this.type = SymbolTable.StringToType(type);
        } catch (Exception e) {
            System.exit(0);
            e.printStackTrace();
        }
    }

    public void setType(ValueType type) {
        try {
            this.type = type;
        } catch (Exception e) {
            System.exit(0);
            e.printStackTrace();
        }

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
