package support;


import jdk.internal.org.objectweb.asm.tree.analysis.Value;
import nodes.ModeParNode;

import java.util.ArrayList;
import java.util.HashMap;
//La symbol table è implementata con un HashMap dove il parametro String rappresenta il nome della tabella (Ad esempio "Global") e symboltableentry è l'entry della tabella
public class SymbolTable extends HashMap<String,SymbolTableEntry> {

        public String symbolTableName;
        public SymbolTable pointerToFather;

        //setta la symbol table padre
        public void setFatherSymTab(SymbolTable symbolTable){

            this.pointerToFather = symbolTable;
        }


        //verifica se esiste la symbol table padre
        public boolean hasFatherSymTab() {
            return pointerToFather != null;
        }
        //ritorna il puntatore alla symbol table padre
        public SymbolTable getFatherSymTab(){
            return pointerToFather;
        }

        //Controllo se la variabile è presente nello scope(nella symbol table), altrimenti la inserisco, type indica il tipo della variabile
        public void createEntry_variable(String id, String type) throws Exception {
            if(super.containsKey(id)) throw new Exception("Semantic error in \" + symbolTableName + \": identifier '\" + id + \"' already declared in the actual scope");
            else super.put(id,new SymbolTableEntry(id,StringToType(type)));
        }



        //Controlla se la funzione è gia presente nello scope(nella symbol table), altrimenti la inscerisce
        public void createEntry_function(String id, ArrayList<ModeParNode> modeParams, ArrayList<ValueType> inputParams, ValueType returnParams) throws Exception {
            if (super.containsKey(id)) throw new Exception("Semantic error in " + symbolTableName + ": identifier " + id + " already declared in the actual scope");
            else super.put(id, new SymbolTableEntry(id, modeParams, inputParams, returnParams));
        }

        //Fa la stessa cosa di containsKey ma crea e ritorna l'entrata nella tabella specifica per l' id passato come parametro al metodo
        public SymbolTableEntry containsEntry(String id) throws Exception {
            SymbolTableEntry symbolTableEntry = null;
            if(super.containsKey(id)) {
                symbolTableEntry = super.get(id);
            } else if(hasFatherSymTab()) {//Verifica se è presente all'interno della tabella padre
                symbolTableEntry = getFatherSymTab().containsEntry(id);
            } else {
                throw new Exception("Semantic error: variable " + id + " is not declared");
            }
            return symbolTableEntry;
        }

        //Ritorna true se trova l'id specificato come parametro nella tabella corrente o in quella del padre
        public Boolean containsKey(String id) throws Exception {
            if(super.containsKey(id)){
                return true;
            } else if (hasFatherSymTab()) {
                return getFatherSymTab().containsKey(id);
            } else {
                throw new Exception("Semantic error: variable or function "  + id + " is not delcared");
            }
        }

        //Ritorna l'entry associata alla funzione cercata nella tabella
        public SymbolTableEntry containsFunctionEntry(String id) throws Exception {
            SymbolTableEntry symbolTableEntry = null;
            if(super.containsKey(id) && !super.get(id).isVariable()) {
                symbolTableEntry = super.get(id);
            } else if (hasFatherSymTab()) {
                symbolTableEntry = getFatherSymTab().containsFunctionEntry(id);
            } else {
                throw new Exception("Semantic error: fun " + id + " is not defined");
            }
            return symbolTableEntry;
        }

        //Converte in ValueType
        public static ValueType StringToType(String type) throws Exception {
            if(type.equalsIgnoreCase("integer")) return ValueType.Integer;
            if(type.equalsIgnoreCase("string")) return ValueType.String;
            if(type.equalsIgnoreCase("real")) return ValueType.Real;
            if(type.equalsIgnoreCase("bool")) return ValueType.Bool;
            if(type.equalsIgnoreCase("void")) return ValueType.Void;
            if(type.equalsIgnoreCase("var")) return ValueType.Var;
            throw new Exception("Semantic error: type " + type + " does not exists");
        }

}
