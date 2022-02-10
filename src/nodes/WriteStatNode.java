package nodes;

import Visitor.*;
import org.w3c.dom.Element;

public class WriteStatNode implements StatNode, ISyntaxVisitable, ISemanticVisitable, ICVisitable {

    public String name=null;
    public ModeWriteNode node=null;
    public ExprNode expr=null;

    public WriteStatNode(String name,ModeWriteNode node,ExprNode expr){
        this.name=name;
        this.node=node;
        this.expr=expr;
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
