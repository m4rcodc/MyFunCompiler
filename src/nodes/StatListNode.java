package nodes;

import Visitor.*;
import org.w3c.dom.Element;

import java.util.ArrayList;

public class StatListNode implements ISyntaxVisitable, ISemanticVisitable, ICVisitable {

    public String name = null;
    public ArrayList<StatNode> statList;

    public StatListNode(String name, ArrayList<StatNode> statList) {
        this.name = name;
        this.statList = statList;
    }

    public void add(StatNode statNode) {

        this.statList.add(0,statNode);
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
