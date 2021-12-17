package nodes;

import Visitor.ISyntaxVisitable;
import Visitor.ISyntaxVisitor;
import org.w3c.dom.Element;

public class WriteStatNode implements StatNode, ISyntaxVisitable {

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
}
