package nodes;

import Visitor.*;
import leafs.LeafID;
import org.w3c.dom.Element;
import support.SymbolTable;
import support.ValueType;

import java.util.ArrayList;

public class CallFunNode implements StatNode, ISyntaxVisitable,ISemanticVisitable, ICVisitable {

    public String name = null;
    public LeafID leafID = null;
    public ArrayList<ExprNode> exprList = null;


    public CallFunNode (String name, LeafID leafID){

        this.name = name;
        this.leafID = leafID;
    }

    public CallFunNode (String name, LeafID leafID, ArrayList<ExprNode> exprList){

        this.name = name;
        this.leafID = leafID;
        this.exprList = exprList;
    }

    //Semantic check
    public ArrayList<ValueType> types = new ArrayList<>();

    public void setTypes(ArrayList t) {
        try {
            for (int i = 0; i < t.size(); i++) {
                if (t.get(i) instanceof String)
                    this.types.add(SymbolTable.StringToType((String) t.get(i)));
                else this.types.add((ValueType) t.get(i));
            }
        } catch (Exception e) {
            System.exit(0);
            e.printStackTrace();
        }
    }

    public void setType(String t) {
        try {
            this.types.add(SymbolTable.StringToType(t));
        } catch (Exception e) {
            System.exit(0);
            e.printStackTrace();
        }
    }

    public void setType(ValueType t) {
        try {
            this.types.add(t);
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
    public void accept(ISemanticVisitor v) { v.visit(this);}

    @Override
    public void accept(ICVisitor v) { v.visit(this); }


}
