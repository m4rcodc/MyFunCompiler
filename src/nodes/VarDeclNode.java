package nodes;

import Visitor.ISyntaxVisitable;
import Visitor.ISyntaxVisitor;
import org.w3c.dom.Element;

import java.util.ArrayList;

public class VarDeclNode implements ISyntaxVisitable {

    public String name = null;
    public TypeNode type = null;
    public ArrayList<IdInitNode> identifiers = null;

    public VarDeclNode(String name,  TypeNode type, ArrayList<IdInitNode> identifiers) {
        this.name = name;
        this.type = type;
        this.identifiers = identifiers;
    }

    @Override
    public Element accept(ISyntaxVisitor v) {
        return v.visit(this);
    }


}
