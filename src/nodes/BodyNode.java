package nodes;

import Visitor.*;
import org.w3c.dom.Element;

import java.util.ArrayList;

public class BodyNode implements ISyntaxVisitable, ISemanticVisitable, ICVisitable {

    public String name = null;
    public ArrayList<VarDeclNode> vardecl = null;
    public StatListNode stats = null;

    public BodyNode(String name, ArrayList<VarDeclNode> vardecl, StatListNode stats) {
        this.name=name;
        this.vardecl=vardecl;
        this.stats=stats;
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
