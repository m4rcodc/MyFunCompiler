package support;

import nodes.ModeParNode;

import java.util.ArrayList;

public class SymbolTableEntry {
    public String id;
    public ValueType valueType;
    public Type type;
    public ArrayList<ModeParNode> modeParams;
    public ArrayList<ValueType> functionParams;
    public ValueType returnFunParam;

    //Entry variable
    public SymbolTableEntry(String id,ValueType valueType) {
        this.type = Type.Variable;
        this.id = id;
        this.valueType = valueType;
    }
    //Entry function
    public SymbolTableEntry(String id,ArrayList<ModeParNode> modeParams,ArrayList<ValueType> functionParams, ValueType returnFunParam){
        this.type = Type.Function;
        this.id = id;
        this.modeParams = modeParams;
        this.functionParams = functionParams;
        this.returnFunParam = returnFunParam;
    }

    public boolean isVariable() {
        return this.type == Type.Variable;
    }

    public ValueType getValueType(){
        return this.valueType;
    }

    public void setValueType(ValueType t){
        this.valueType = t;
    }



/*
    public String toString() {
        if(isVariable()){
            return "Entry of type Variable : " + id + " | " + valueType;
        }
        else {
            String inputs = "";
            for(int i = 0; i < functionParams.size(); i++) {
                if ( i == )
            }
        }

    }
    */
}
