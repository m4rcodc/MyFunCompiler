package nodes;

import Visitor.*;
import org.w3c.dom.Element;

public class ReturnStatNode implements StatNode, ISyntaxVisitable, ISemanticVisitable, ICVisitable {

    public String name = null;
    public ExprNode expr = null;

    public ReturnStatNode(String name,ExprNode expr){
        this.name=name;
        this.expr=expr;
    }

    @Override
    public Element accept(ISyntaxVisitor v) {
        return v.visit(this);
    }

    @Override
    public void accept(ISemanticVisitor v) {  v.visit(this);}

    @Override
    public void accept(ICVisitor v) { v.visit(this); }

}


