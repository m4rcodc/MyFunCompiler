package nodes;

import Visitor.*;
import org.w3c.dom.Element;

public class IfStatNode implements StatNode, ISyntaxVisitable, ISemanticVisitable, ICVisitable {

    public String name = null;
    public ExprNode expr = null;
    public BodyNode bn = null;
    public BodyNode elsebody = null;

    public IfStatNode(String name, ExprNode expr, BodyNode bn, BodyNode elsebody){
        this.name=name;
        this.expr=expr;
        this.bn=bn;
        this.elsebody=elsebody;
    }

    public IfStatNode(String name, ExprNode expr, BodyNode bn){
        this.name=name;
        this.expr=expr;
        this.bn=bn;
        this.elsebody=null;
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
