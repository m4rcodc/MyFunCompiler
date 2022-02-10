package nodes;

import Visitor.*;
import org.w3c.dom.Element;
import support.ValueType;

import java.util.ArrayList;

public class VarDeclNode implements ISyntaxVisitable, ISemanticVisitable, ICVisitable {

    public String name = null;
    public TypeNode type = null;
    public ArrayList<IdInitNode> identifiers = null;

    public VarDeclNode(String name,  TypeNode type, ArrayList<IdInitNode> identifiers) {
        this.name = name;
        this.type = type;
        this.identifiers = identifiers;
    }

    public void setType(String type){
        this.type.type = type;
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
