package nodes;

import Visitor.ISyntaxVisitable;
import Visitor.ISyntaxVisitor;
import leafs.LeafID;
import org.w3c.dom.Element;

import java.util.ArrayList;

public class CallFunNode implements StatNode, ISyntaxVisitable {

    public String name = null;
    public LeafID leafID = null;
    public ArrayList<ExprNode> exprList = null;


    public CallFunNode (String name, LeafID leafID){

        this.name = name;
        this.leafID = leafID;
    }

    public CallFunNode (String name, LeafID leafID, ArrayList<ExprNode> exprList){

        this.name = name;
        this.leafID = leafID;
        this.exprList = exprList;
    }

    @Override
    public Element accept(ISyntaxVisitor v) {
        return v.visit(this);
    }


}
