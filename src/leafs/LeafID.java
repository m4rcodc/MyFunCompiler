package leafs;

import Visitor.*;
import org.w3c.dom.Element;
import support.SymbolTable;
import support.ValueType;

public class LeafID implements ISyntaxVisitable,ISemanticVisitable, ICVisitable {

    public String name;
    public String value;

    public LeafID(String value) {
        this.value = value;
        this.name = "LeafID";
    }


    public ValueType type = null;

    public ValueType getType() {
        return type;
    }

    public void setType(String t) {
        try {
            this.type = SymbolTable.StringToType(t);
        } catch (Exception e) {
            System.exit(0);
            e.printStackTrace();
        }
    }

    public void setType(ValueType type) {
        try{
            this.type = type;
        } catch (Exception e) {
            System.exit(0);
            e.printStackTrace();
        }
    }

    public String getName(){
        return this.name;
    }

    @Override
    public Element accept(ISyntaxVisitor v) {
        return v.visit(this);
    }

    @Override
    public void accept(ISemanticVisitor v) { v.visit(this);}

    @Override
    public void accept(ICVisitor v) { v.visit(this);}

}
