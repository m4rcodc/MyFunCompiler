package support;
import nodes.ModeParNode;
import java.util.ArrayList;
import java.util.HashMap;
//La symbol table è implementata con un HashMap dove il parametro String rappresenta il nome della tabella (Ad esempio "Global") e symboltableentry è l'entry della tabella
public class SymbolTable extends HashMap<String,SymbolTableEntry> {

        //Nome della Symbol Table
        public String symbolTableName;
        //Padre della Symbol Table
        public SymbolTable pointerToFather;

        //------Metodi Getter e Setter-----

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

        //-----Costruttore----
        public SymbolTable(String name){
            this.symbolTableName = name;
        }

        // Aggiunge una variabile all'interno della tabella corrente
        public boolean createEntry_variable(String id, String type) {
            if(super.containsKey(id)){
                return false;
            }
            else {
                super.put(id,new SymbolTableEntry(id,StringToType(type)));
                return true;
            }
        }

        //------Funzioni di accesso/scrittura alla symbol table

        // Aggiunge una funzione all'interno della tabella corrente
        public boolean createEntry_function(String id, ArrayList<ModeParNode> modeParams, ArrayList<ValueType> inputParams, ValueType returnParams) {
            if (super.containsKey(id)){
                return false;
            }
            else{
                super.put(id, new SymbolTableEntry(id, modeParams, inputParams, returnParams));
                return true;
            }
        }

        // Ritorna la variabile con ID se contenuta altrimenti NULL
        public SymbolTableEntry containsEntry(String id) {
            SymbolTableEntry symbolTableEntry = null;
            if(super.containsKey(id)) {
                symbolTableEntry = super.get(id);
            } else if(hasFatherSymTab()) {//Verifica se è presente all'interno della tabella padre
                symbolTableEntry = getFatherSymTab().containsEntry(id);
            }
            return symbolTableEntry;
        }

        // Verifica se l'ID è contenuto ricorsivamente fino a tabella Global
        public Boolean containsKey(String id) {
            if(super.containsKey(id)){
                return true;
            } else if (hasFatherSymTab()) {
                return getFatherSymTab().containsKey(id);
            } else {
                return false;
            }
        }

        // Ritorna la funzione con ID se contenuta altrimenti NULL
        public SymbolTableEntry containsFunctionEntry(String id) {
            SymbolTableEntry symbolTableEntry = null;
            if(super.containsKey(id) && !super.get(id).isVariable()) {
                symbolTableEntry = super.get(id);
            } else if (hasFatherSymTab()) {
                symbolTableEntry = getFatherSymTab().containsFunctionEntry(id);
            }
            return symbolTableEntry;
        }

        //Converte in ValueType
        public static ValueType StringToType(String type)  {
            if(type.equalsIgnoreCase("integer")){
                return ValueType.Integer;
            }
            if(type.equalsIgnoreCase("string")){
                return ValueType.String;
            }
            if(type.equalsIgnoreCase("real")){
                return ValueType.Real;
            }
            if(type.equalsIgnoreCase("bool")){
                return ValueType.Bool;
            }
            if(type.equalsIgnoreCase("void")){
                return ValueType.Void;
            }
            if(type.equalsIgnoreCase("var")){
                return ValueType.Var;
            }
            else {
                System.err.println("Semantic error: type " + type + " does not exists");
                System.exit(1);
                return null;
            }
        }
}
