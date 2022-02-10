package leafs;

import Visitor.*;
import org.w3c.dom.Element;
import support.SymbolTable;
import support.ValueType;

public class LeafBool implements ISyntaxVisitable, ISemanticVisitable, ICVisitable {

    public String name;
    public boolean value;

    public LeafBool(boolean value) {
        this.name = "LeafBool";
        this.value = value;
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

    @Override
    public Element accept(ISyntaxVisitor v) {
        return v.visit(this);
    }

    @Override
    public void accept(ISemanticVisitor v) { v.visit(this); }

    @Override
    public void accept(ICVisitor v) { v.visit(this); }
}
