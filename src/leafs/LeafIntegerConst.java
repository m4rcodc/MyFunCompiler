package leafs;

import Visitor.*;
import org.w3c.dom.Element;
import support.SymbolTable;
import support.ValueType;

public class LeafIntegerConst implements ISyntaxVisitable, ISemanticVisitable, ICVisitable {

    public String name;
    public Integer value;

    public LeafIntegerConst(Integer value) {
        this.value = value;
        this.name = "LeafIntegerConst";
    }

    //Semantic part
    public ValueType type = null;

    public void setType(String t){
        try{
            this.type = SymbolTable.StringToType(t);
        } catch(Exception e) {
            System.exit(0);
            e.printStackTrace();
        }
    }

    public ValueType getType(){

        return this.type;

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
