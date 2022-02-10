package nodes;

import Visitor.*;
import org.w3c.dom.Element;
import support.SymbolTable;
import support.ValueType;

public class ConstNode implements ISyntaxVisitable, ISemanticVisitable, ICVisitable {

    public String name = null;
    public Object value1 = null;

    public ConstNode(String name, Object value1){

        this.name=name;
        this.value1 = value1;
    }

    //Semantic part
    public ValueType type = null;

    public void setType(String type) {
        try {
            this.type = SymbolTable.StringToType(type);
        } catch (Exception e) {
            System.exit(0);
            e.printStackTrace();
        }
    }

    public void setType(ValueType t) {
        try {
            this.type = t;
        } catch (Exception e) {
            System.exit(0);
            e.printStackTrace();
        }
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
