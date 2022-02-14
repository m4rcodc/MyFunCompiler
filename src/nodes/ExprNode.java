package nodes;

import Visitor.*;
import leafs.LeafID;
import org.w3c.dom.Element;
import support.SymbolTable;
import support.ValueType;

import java.util.ArrayList;

public class ExprNode implements ISyntaxVisitable, ISemanticVisitable, ICVisitable {

    public String name = null;
    public ModeParNode mod;
    public LeafID leaf= null;
    public Object value1 = null;
    public Object value2 = null;

    public ExprNode(String name, Object n) {
        this.name = name;
        this.value1 = n;
        this.mod = null;
        this.value2 = null;
        this.leaf=null;
    }

    public ExprNode(String name, Object n1, Object n2) {
        this.name = name;
        this.value1 = n1;
        this.value2 = n2;
        this.mod = null;
        this.leaf=null;
    }

    public ExprNode(String name,ModeParNode mod,LeafID leaf) {
        this.name =name;
        this.mod =mod;
        this.leaf=leaf;
        value1 = null;
        value2 = null;
    }


    //Semantic part
    public ValueType type = null;

    //Mi server per gestire l'expr di CallFunNode
    /*public void setTypes(ArrayList t) {
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
*/

    public void setType(String t) {
        try {
            this.type = SymbolTable.StringToType(t);
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

    public void setMode(String mod) {

        ModeParNode in = new ModeParNode(mod);
        this.mod = in;

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
