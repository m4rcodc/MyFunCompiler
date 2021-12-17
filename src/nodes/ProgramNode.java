package nodes;
import Visitor.ISyntaxVisitable;
import Visitor.ISyntaxVisitor;
import org.w3c.dom.Element;

import java.util.ArrayList;

public class ProgramNode implements ISyntaxVisitable {

    public String name = null;
    public ArrayList<VarDeclNode> nodeArrayList;
    public ArrayList<FunNode> funNodeArrayList;
    public BodyNode bodyNode;




    public ProgramNode(String name,ArrayList<VarDeclNode> nodeArrayList,ArrayList<FunNode> funNodeArrayList,BodyNode bodyNode){
        this.name = name;
        this.nodeArrayList = nodeArrayList;
        this.funNodeArrayList = funNodeArrayList;
        this.bodyNode = bodyNode;
    }

    @Override
    public Element accept(ISyntaxVisitor v) {
        return v.visit(this);
    }

}