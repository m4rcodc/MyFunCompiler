package Visitor;
import leafs.*;
import nodes.*;
import support.SymbolTable;
import support.SymbolTableEntry;
import support.Type;
import support.ValueType;
import java.util.ArrayList;
import java.util.Stack;

//Visitor per la gestione dell'analisi semantica

public class SemanticVisitor implements ISemanticVisitor {

    public Stack<SymbolTable> stack = new Stack<>();
    //flag per gestire la dichiarazione delle variabili globali
    boolean flag = false;

    public void visit(ProgramNode programNode) {
        //Genero la tabella di scope globale
        SymbolTable symbolTable = new SymbolTable("Global scope");
        //symbolTable.symbolTableName = "Global Scope";

        stack.push(symbolTable);

        //Aggiungo le dichiarazioni di variabili allo scope globale
        if(programNode.nodeArrayList != null){
            for(VarDeclNode varDeclNode : programNode.nodeArrayList) {
                varDeclNode.accept(this);
            }
        }

        //Aggiungo le dichiarazioni di funzioni allo scope globale
        for(FunNode funNode : programNode.funNodeArrayList){
            funNode.accept(this);
        }

        //Gestisco lo scope del body (main)
        programNode.bodyNode.name = "MainBody";
        programNode.bodyNode.accept(this);

        stack.pop();
    }

    //Gestisce lo scope del Body
    public void visit(BodyNode bodyNode){
        //Il main lo gestisco come caso a parte poichè posso dichiararlo una sola volta, quindi la symbol table padre sarà sempre la tabella global
        if(bodyNode.name.equals("MainBody")){
            SymbolTable symbolTable = new SymbolTable(bodyNode.name);
            symbolTable.setFatherSymTab(stack.firstElement());
            stack.push(symbolTable);

            if(bodyNode.vardecl != null){
                for(VarDeclNode varDeclNode : bodyNode.vardecl){
                    varDeclNode.accept(this);
                }
            }
            bodyNode.stats.accept(this);
        }
        else {
            //Gestisco lo scope del body
            SymbolTable symbolTable = new SymbolTable(bodyNode.name);
            //symbolTable.symbolTableName = bodyNode.name;
            symbolTable.setFatherSymTab(stack.peek());
            stack.push(symbolTable);

            if (bodyNode.vardecl != null) {
                for (VarDeclNode varDeclNode : bodyNode.vardecl) {
                    varDeclNode.accept(this);
                }
            }

                bodyNode.stats.accept(this);
        }

        stack.pop();

    }

    //Gestisce l'inserimento nella symbol table delle dichiarazione di variabili
    //Effettua un controllo sul tipo di assegnamento alla dichiarazione
    //Effettua l'inferenza di tipo sulla dichiarazione di variabili var
    public void visit(VarDeclNode varDeclNode) {
        //VarDeclNode è formato da un tipo e a seguire dai nomi dei lessemi
        //Accedo al tipo della variabile dichiarata (Var)
        varDeclNode.type.accept(this);

        //Gestisco l'inizializzazione della variabile
        for(IdInitNode idInitNode : varDeclNode.identifiers){
                //Prendo la tabella dei simboli sul top dello stack in quel momento
                SymbolTable picked = stack.peek();
                //Se è già presente uno scope padre
                //Verifico se c'è una variabile con lo stesso nome di una funzione
                if(picked.getFatherSymTab() != null) {
                    SymbolTableEntry symbolTableEntry = picked.getFatherSymTab().get(idInitNode.leaf.value);
                    //Se symbolTableEntry != null cioè se la variabile è gia presente in una table padre e quindi se è associata al nome di una funzione lancio errore
                    if (symbolTableEntry != null && symbolTableEntry.type == Type.Function) {
                        System.err.println("Semantic error: Cannot declare a variable with ID:" + idInitNode.leaf.value + ". There is a function with same ID.");
                        System.exit(1);
                    }
                }
                    if(idInitNode.name.equalsIgnoreCase("IdInitOp")){
                        idInitNode.type = SymbolTable.StringToType(varDeclNode.type.type);
                        if(!picked.createEntry_variable(idInitNode.leaf.value,varDeclNode.type.type)){
                            System.err.println("Semantic error in " + stack.peek().symbolTableName + ": identifier " + idInitNode.leaf.value +" already declared in the actual scope");
                            System.exit(1);
                        }
                        idInitNode.accept(this);
                    }

                    else if(idInitNode.name.equalsIgnoreCase("IdInitOpObbl")){
                            idInitNode.c.accept(this);
                            idInitNode.type = idInitNode.c.type;
                        if(!picked.createEntry_variable(idInitNode.leaf.value,idInitNode.type.toString())){
                            System.err.println("Semantic error in " + stack.peek().symbolTableName + ": identifier " + idInitNode.leaf.value +" already declared in the actual scope");
                            System.exit(1);
                        }
                        idInitNode.accept(this);
                    }
             }
    }

    public void visit(TypeNode node) {

    }

    //Occorre gestire il tipo con cui deve essere dichiarato il lessema (inferenza)
    public void visit(IdInitNode idInitNode) {
        //LeafID
        idInitNode.leaf.accept(this);

        //ExprNode
        if(idInitNode.expr != null) {
            idInitNode.expr.accept(this);
            if(idInitNode.expr.value1 instanceof CallFunNode){
               ValueType returnType = ((CallFunNode) idInitNode.expr.value1).type;
               if(returnType.equals(ValueType.Void) && idInitNode.leaf.type != null){
                   System.err.println("Semantic error: type mismatch , wrong initialization for variable " + idInitNode.leaf.value + ", the second argument of assignment is a callfun that return void");
                   System.exit(1);
                }
               if(!returnType.equals(ValueType.Void) && idInitNode.leaf.type != null){
                   if (!checkAssignmentType(idInitNode.leaf.type, returnType)) {
                       System.err.println("Semantic error: type mismatch , wrong initialization for variable " + idInitNode.leaf.value + " ,required: " + idInitNode.leaf.type + " ,provided: " + returnType);
                       System.exit(1);
                   }
               }

            }
            if (!checkAssignmentType(idInitNode.leaf.type, idInitNode.expr.type)) {
                System.err.println("Semantic error: type mismatch , wrong initialization for variable " + idInitNode.leaf.value);
                System.exit(1);
            }
        }

        //ConstNode (VAR)
        if(idInitNode.c != null) {
            idInitNode.c.accept(this);
            idInitNode.setType(idInitNode.c.type);
        }
    }

    //ConstNode (VAR)
    public void visit(ConstNode constNode){

        if(constNode.value1 instanceof LeafIntegerConst){
            ((LeafIntegerConst) constNode.value1).accept(this);
            constNode.setType(((LeafIntegerConst) constNode.value1).type);
        }
        else if(constNode.value1 instanceof LeafStringConst){
            ((LeafStringConst) constNode.value1).accept(this);
            constNode.setType(((LeafStringConst) constNode.value1).type);
        }
        else if(constNode.value1 instanceof LeafRealConst){
            ((LeafRealConst) constNode.value1).accept(this);
            constNode.setType(((LeafRealConst) constNode.value1).type);
        }
        else {
            ((LeafBool) constNode.value1).accept(this);
            constNode.setType(((LeafBool) constNode.value1).type);
        }
    }

    //Gestisce lo scope di FunNode
    public void visit(FunNode funNode){
        // Controllo se la funzione dichiarata esiste già nel type environment, ovvero controllo la sua firma, parametri di input e valore di ritorno della funzione
        ValueType outType = null;
        ArrayList<ValueType> paramsType = new ArrayList<>();
        ArrayList<ModeParNode> modeParams = new ArrayList<>();

        SymbolTable symbolTable = new SymbolTable(funNode.leaf.value);
        symbolTable.setFatherSymTab(stack.firstElement());
        stack.push(symbolTable);

            //Mi salvo i tipi dei parametri della funzione nell'array paramsType, e la modalità dei parametri nell'array modeParam
            if (funNode.pardecl != null) {
                for (ParDeclNode parDeclNode : funNode.pardecl) {
                    ValueType valueType = SymbolTable.StringToType(parDeclNode.typeNode.type);//Utilizzo StringToType per convertire il tipo in ValueType, poichè tyèe in typeNode è di tipo stringa
                    paramsType.add(valueType);
                    ModeParNode mode = parDeclNode.mod;
                    modeParams.add(mode);
                }
            }

            //Tipo di ritorno della funzione
            if(funNode.typeNode == null) {
                outType = ValueType.Void;
            }
            else {
                outType = SymbolTable.StringToType(funNode.typeNode.type);
            }


            //Controllo se la funzione è presente nello scope, altrimenti la inserisco direttamente col metodo createEntry_function
            if(!stack.firstElement().createEntry_function(funNode.leaf.value, modeParams, paramsType, outType)){
                System.err.println("Semantic error in " + stack.peek().symbolTableName + ": identifier " + funNode.leaf.value + " already declared in the actual scope");
                System.exit(1);
            }


        if(funNode.pardecl != null){
            for(ParDeclNode parDeclNode : funNode.pardecl){
                parDeclNode.accept(this);
            }
        }


        if(funNode.bodyNode.vardecl != null){
            for(VarDeclNode varDeclNode : funNode.bodyNode.vardecl){
                varDeclNode.accept(this);
            }
        }

        funNode.bodyNode.stats.accept(this);

        //Gestione di casi specifici di funzioni void tramite dei flag
        ValueType returnType = null;
        for(StatNode statNode : funNode.bodyNode.stats.statList){
            if(statNode instanceof ReturnStatNode){
                returnType = ((ReturnStatNode) statNode).expr.type;
            }
        }

        if(outType.equals(ValueType.Void) && returnType == null){
            flag = true;
        }

        if(flag == true){
            flag = false;
        }
        else if(flag == false && returnType == null){
            System.err.println("Semantic error: wrong return value of function " + symbolTable.symbolTableName + ", required: " + outType);
            System.exit(1);
        }

        //Rimuovo la symboltable dallo stack
        stack.pop();
    }

    //Bisogna verificare:
    //La modalità di input del parametro
    //Il tipo del parametro
    //Il lessema del parametro
    public void visit(ParDeclNode node) {


        //Tipo del parametro
        node.typeNode.accept(this);


            SymbolTable picked = stack.peek();
            SymbolTableEntry symbolTableEntry = picked.getFatherSymTab().get(node.leaf.value);
            if(symbolTableEntry != null && symbolTableEntry.type == Type.Function){
                System.err.println("Semantic error: Cannot declare a variable with ID:" + node.leaf.value + ". There is a function with same ID.");
                System.exit(1);
            } else {
                if(!picked.createEntry_variable(node.leaf.value,node.typeNode.type)){
                    System.err.println("Semantic error in " + stack.peek().symbolTableName + ": identifier " + node.leaf.value + " already declared in the actual scope");
                    System.exit(1);
                }
            }

        //node.leaf.accept(this);
    }

    public void visit(ModeParNode node) {


    }

    public void visit(StatListNode nodeList) {
        for(StatNode statNode : nodeList.statList){
            switch(statNode.getClass().getSimpleName()) {
                case "IfStatNode":
                    IfStatNode ifStat = (IfStatNode) statNode;
                    ifStat.accept(this);
                    break;
                case "WhileStatNode":
                    WhileStatNode whileStatNode = (WhileStatNode) statNode;
                    whileStatNode.accept(this);
                    break;
                case "ReadStatNode":
                    ReadStatNode readStatNode = (ReadStatNode) statNode;
                    readStatNode.accept(this);
                    break;
                case "WriteStatNode":
                    WriteStatNode writeStatNode = (WriteStatNode) statNode;
                    writeStatNode.accept(this);
                    break;
                case "AssignStatNode":
                    AssignStatNode assignStatNode = (AssignStatNode) statNode;
                    assignStatNode.accept(this);
                    break;
                case "CallFunNode":
                    CallFunNode callFunNode = (CallFunNode) statNode;
                    callFunNode.accept(this);
                    break;
                case "ReturnStatNode":
                    ReturnStatNode returnStatNode = (ReturnStatNode) statNode;
                    returnStatNode.accept(this);
                    break;
            }
        }
    }

    //Controllo se la condizione è di tipo bool
    //Gestisco lo scope del body del'if
    //Gestisco lo scope dell'else
    public void visit(IfStatNode node){

        //Prendo il tipo dell'espressione dell'if
        node.expr.accept(this);

        //Controllo sul tipo della condizione (Se è booleano)
        if(node.expr.type != ValueType.Bool){
            System.err.println("Semantic error: condition type not allowed in if");
            System.exit(1);
        }

        //Gestisco lo scope del body dell'if
        node.bn.name = "IfStatNode";
        node.bn.accept(this);

        //Nel caso in cui ci sia la condizione else, gestisco lo scope del body dell'else
        if(node.elsebody != null){
            node.elsebody.name = "ElseStatNode";
            node.elsebody.accept(this);
        }

    }

    //Controllo se la condizione del while è di tipo bool
    //Gestisco lo scope del body del while
    public void visit(WhileStatNode node) {

        //ExprNode(Condition)
        node.expr.accept(this);
        //Controllo i tipi della condizione del while
        if(node.expr.type != ValueType.Bool){
            System.err.println("Semantic error: condition type not allowed in while");
            System.exit(1);
        }

        //Gestisco lo scope del body del while
        node.bn.name = "WhileStaNode";
        node.bn.accept(this);

    }

    //Controllo se il tipo dell'identificatore coincide con quello dell'espressione assegnata
    public void visit(AssignStatNode node) {

        //LeafID
        node.leaf.accept(this);

        //ExprNode (Controllo il tipo dell'espressione)
        node.expr.accept(this);
        //Check sulla compatibilità dei tipi per l'assegnamento
        if(!checkAssignmentType(node.leaf.type, node.expr.type)){
            System.err.println("Semantic Error: ID type does not match with Assign Value type in assign stat for id: " + node.leaf.value);
            System.exit(1);
        }
    }

    //Controllo se gli identificatori utilizzati sono stati dichiarati
    //Controllo se l'espressione da stampare è una stringa
    public void visit(ReadStatNode node) {

        //Il controllo che faccio è quello di verificare che la variabile letta (ans nell'esempio) sia di tipo string
        //ExprNode
        if(node.expr != null) {
            node.expr.accept(this);
            if (node.expr.type != ValueType.String) {
                System.err.println("Semantic Error: the content of expression must be a string, provided: " + node.expr.type);
                System.exit(1);
            }
        }

        //Controllo se gli identificatori utilizzati sono stati già dichiarati
        for(LeafID leafID : node.idlist){
            leafID.accept(this);
        }


    }

    public void visit(WriteStatNode node) {

        node.expr.accept(this);

    }

    //Controllo sul tipo di ritorno della funzione
    public void visit(ReturnStatNode node) {
        //Controllo il tipo dell'espressione del return
        node.expr.accept(this);

        SymbolTable symbolTable = stack.peek();
        SymbolTableEntry symbolTableEntry = symbolTable.containsFunctionEntry(symbolTable.symbolTableName);

        //Arrivo alla symbol table globale per prendere il tipo di ritorno della funzione (returnFunParam)
        while(symbolTableEntry == null){
            symbolTable = symbolTable.getFatherSymTab();
            symbolTableEntry = symbolTable.containsFunctionEntry(symbolTable.symbolTableName);
        }


        if(symbolTableEntry.returnFunParam != node.expr.type){
            System.err.println("Semantic error: wrong return value of function " + symbolTable.symbolTableName + ", required: " + symbolTableEntry.returnFunParam + " , provided: " + node.expr.type);
            System.exit(1);
        }

        flag = true;


    }

    //Controllo se la funzione chiamante esiste e verifico per il tipo di ritorno
    public void visit(CallFunNode node){

        //Controllo se la funzione chiamata è nel type environmnet,ovvero se è stata dichiarata, quindi se è nella tabella global
        SymbolTable symbolTable = stack.firstElement();
        SymbolTableEntry symbolTableEntry = null;
        //Mi prendo la firma associata alla funzione dichiarata in precedenza
        symbolTableEntry = symbolTable.containsFunctionEntry(node.leafID.value);
        if(symbolTableEntry == null){
            System.err.println("Semantic error: fun " + node.leafID.value + " is not defined");
        }
        //Controllo dei parametri con la symbol table
        if(node.exprList != null) {//caso in cui la funzione ha dei parametri
            //Controllo se i parametri sono giusti in numero
            if (symbolTableEntry.functionParams.size() != node.exprList.size()) {
                System.err.println("Semantic error: number of params doesn't match in function " + node.leafID.value + " call. Required : " + symbolTableEntry.functionParams.size() + ", provided: " + node.exprList.size());
                System.exit(1);
            }
            else {
                //Controllo i singoli parametri
                for (int i = 0; i < symbolTableEntry.functionParams.size(); i++) {
                    node.exprList.get(i).accept(this);
                    if(node.exprList.get(i).mod == null){
                         node.exprList.get(i).setMode("in");
                    }
                    //Controllo che il parametro sia giusto in tipo
                    if (!checkAssignmentType(node.exprList.get(i).type, symbolTableEntry.functionParams.get(i))) {
                        System.err.println("Semantic error: type mismatch for call fun " + node.leafID.value + ". Required: " + symbolTableEntry.functionParams + "  , provided: '" + node.exprList.get(i).type + "' in position " + i);
                        System.exit(1);
                    }
                    //Controllo che il parametro sia giusto per quanto riguarda la modalità (Out\in)
                    if(!node.exprList.get(i).mod.mod.equals(symbolTableEntry.modeParams.get(i).mod)) {
                        System.err.println("Semantic error: mode param mismatch for callfun: " + node.leafID.value);
                        System.exit(1);
                    }
                }
            }
        }
        //Caso in cui la funzione è senza parametri
        else {
            if(symbolTableEntry.functionParams.size() != 0) {
                System.err.println("Semantic error: type mismatch for input params of call proc " + node.leafID.value + ". Required: " + symbolTableEntry.functionParams);
                System.exit(1);
            }
        }

        // Setto il tipo della callFun come il tipo di ritorno della funzione
        if (symbolTableEntry.returnFunParam.equals(ValueType.Void)) {
            node.setType("void");
        }
        else {
            node.setType(symbolTableEntry.returnFunParam);
        }
    }

    //Controllo dei tipi sugli operatori matematici: TIMES,ADD,DIFF,DIV,DIVINT,POWOP
    //Controllo dei tipi su AND e OR
    //Controllo dei tipi sugli operatori relazionali: LT,LE,GT,GE,EQ,NE
    //Controllo dei tipi sugli operatori unari: Uminus e NotOp
    public void visit(ExprNode exprNode){
        if(exprNode.value1 != null && exprNode.value2 != null) {
            //Caso in cui ho due expr, ad esempio MUL,ADD.
            //Se ho 5 + 5
            ((ExprNode) exprNode.value1).accept(this);//Queste due visite mi settano il type dei due 5 ad integer
            ((ExprNode) exprNode.value2).accept(this);

            //Add,Diff,Times,Div,Pow
            if(
                            exprNode.name.equalsIgnoreCase("AddOp") ||
                            exprNode.name.equalsIgnoreCase("DiffOp")||
                            exprNode.name.equalsIgnoreCase("MulOp") ||
                            exprNode.name.equalsIgnoreCase("DivOp") ||
                            exprNode.name.equalsIgnoreCase("PowOp")
            ) {
                //Se takenType == null -> Errore per tipi non compatibili
                ValueType takenType = getType_Operations(((ExprNode) exprNode.value1).type,
                                                        ((ExprNode) exprNode.value2).type);
                if(takenType == null){
                    System.err.println("Semantic error: type not compatible with operation (" + exprNode.name + "). First type: " + ((ExprNode) exprNode.value1).type + ", second type: " + ((ExprNode) exprNode.value2).type);
                    System.exit(1);
                } else {
                    exprNode.setType(takenType);//Altrimenti setto il tipo di exprNode col tipo risultante dell'operazione (Assegno il tipo risultante ad expr, cioè all'espressione)
                 }
                }
            //DIVINT
            else if(exprNode.name.equalsIgnoreCase("DivIntOp")){
                ValueType takenType = getType_DivIntOp(((ExprNode) exprNode.value1).type,
                        ((ExprNode) exprNode.value2).type);
                if(takenType == null){
                    System.err.println("Semantic error: type not compatible with operation (" + exprNode.name + "). For this operation the first and second argument's types must be Integer");
                    System.exit(1);
                } else {
                    exprNode.setType(takenType);
                }
            }
            //AND,OR
            else if (exprNode.name.equalsIgnoreCase("AndOp") || exprNode.name.equalsIgnoreCase("OrOP")) {
                ValueType takenType = getType_AndOr(((ExprNode) exprNode.value1).type,
                                                    ((ExprNode) exprNode.value2).type);

                if(takenType == null) {
                    System.err.println("Semantic error: type not compatible with operation (" + exprNode.name + "). Required: [Boolean, Boolean], provided: [" + ((ExprNode) exprNode.value1).type + ", " + ((ExprNode) exprNode.value2).type + "]");
                    System.exit(1);
                } else {
                    exprNode.setType(takenType);
                }
            }
            //STR_CONCAT
            else if(exprNode.name.equalsIgnoreCase("StrCatOp")){
                ValueType takenType = getType_StrConcat(((ExprNode) exprNode.value1).type,
                        ((ExprNode) exprNode.value2).type);
                if(takenType == null) {
                    System.err.println("Semantic error: type not compatible with operation (" + exprNode.name + "). Required first type: " + ValueType.String);
                    System.exit(1);
                } else {
                    exprNode.setType(takenType);
                }
            }
            //Controllo sugli operatori EQ ed NE
            else if(exprNode.name.equalsIgnoreCase("EQOp") || exprNode.name.equalsIgnoreCase("NEOp")){
                ValueType takenType = getType_EQ_NE(((ExprNode) exprNode.value1).type,
                        ((ExprNode) exprNode.value2).type);
                if(takenType == null){
                    System.err.println("Semantic error: type not compatible with operation (" + exprNode.name + "). First type: " + ((ExprNode) exprNode.value1).type + ", second type: " + ((ExprNode) exprNode.value2).type);
                    System.exit(1);
                }
                else {
                    exprNode.setType(takenType);
                }

            }
            //Controllo su operatori relazionali: LT,GT,LE... (<, <=, >, >=)
            else {
                // Se takenType == null -> Errore per tipi non compatibili
                ValueType takenType = getType_Boolean(((ExprNode) exprNode.value1).type,
                        ((ExprNode) exprNode.value2).type);
                if(takenType == null){
                    System.err.println("Semantic error: type not compatible with logical operation (" + exprNode.name + "). First type: " + ((ExprNode) exprNode.value1).type + ", second type: " + ((ExprNode) exprNode.value2).type);
                    System.exit(1);
                } else {
                    exprNode.setType(takenType);
                }
            }
        }
        else if(exprNode.value1 != null && exprNode.value2 == null){
            //LeafIntegerConst
            if(exprNode.value1 instanceof LeafIntegerConst) {
                ((LeafIntegerConst) exprNode.value1).accept(this);
                exprNode.setType(((LeafIntegerConst) exprNode.value1).type);
            }
            //LeafRealConst
            else if (exprNode.value1 instanceof LeafRealConst) {
                ((LeafRealConst) exprNode.value1).accept(this);
                exprNode.setType(((LeafRealConst) exprNode.value1).type);
            }
            //LeafStringConst
            else if (exprNode.value1 instanceof LeafStringConst) {
                ((LeafStringConst) exprNode.value1).accept(this);
                exprNode.setType(((LeafStringConst) exprNode.value1).type);
            }
            //LeafBool
            else if (exprNode.value1 instanceof LeafBool) {
                ((LeafBool) exprNode.value1).accept(this);
                exprNode.setType(((LeafBool) exprNode.value1).type);
            }

            //LeafID
            else if (exprNode.value1 instanceof LeafID) {
                ((LeafID) exprNode.value1).accept(this);
                exprNode.setType(((LeafID) exprNode.value1).type);
            }

            //CallFun
            else if (exprNode.value1 instanceof CallFunNode) {
                ((CallFunNode) exprNode.value1).accept(this);
                exprNode.setType(((CallFunNode) exprNode.value1).type);
            }


            //Operatore unario MINUS
            else if(exprNode.name.equalsIgnoreCase("UminusOp")) {
                ((ExprNode) exprNode.value1).accept(this);
                if(((ExprNode) exprNode.value1).type == ValueType.Integer ||
                        ((ExprNode) exprNode.value1).type == ValueType.Real)
                    exprNode.setType(((ExprNode)exprNode.value1).type);
                else {
                    System.err.println("Semantic error: type mismatch for 'Uminus' operation. Required type: Integer or Real, provided: " + ((ExprNode) exprNode.value1).type);
                    System.exit(1);
                }
            }
            //Operatore unario NOT
            else if(exprNode.name.equalsIgnoreCase("NotOp")) {
                ((ExprNode) exprNode.value1).accept(this);
                if(((ExprNode) exprNode.value1).type == ValueType.Bool)
                    exprNode.setType(((ExprNode)exprNode.value1).type);
                else {
                    System.err.println("Semantic error: type mismatch for 'NOT' operation. Required type: Boolean, provided: " + ((ExprNode) exprNode.value1).type);
                    System.exit(1);
                }
            }
        }
        //Parametro out di CallFunNode
        else {
            exprNode.leaf.accept(this);
            exprNode.setType(exprNode.leaf.type);
        }
    }
    //Gestione tipo di un identificatore, mi prendo il tipo dell'identificatore
    public void visit(LeafID leaf){
        //Mi prendo la symbol table sul top dello stack
        SymbolTable symbolTable = stack.peek();
        SymbolTableEntry symbolTableEntry = symbolTable.containsEntry(leaf.value);
        //Check: controllo se la variabile è stata dichiarata, e nel caso mi prendo il tipo
        if(symbolTableEntry == null){
            System.err.println("Semantic error: variable " + leaf.value + " is not declared");
            System.exit(1);
        }
        else{
            leaf.type = symbolTableEntry.valueType;
        }

    }

    public void visit(LeafBool leaf){

        leaf.setType("bool");
    }

    public void visit(LeafIntegerConst leaf){

        leaf.setType("integer");
    }

    public void visit(LeafStringConst leaf){

        leaf.setType("string");
    }

    public void visit(LeafRealConst leaf){

        leaf.setType("real");
    }

    //Verifica se la variabile a cui si assegna un valore ed il valore assegnato hanno lo stesso tipo,
    //Utilizzato anche per verificare la corrispondenza dei tipi dei parametri in CallFunNode
    public static boolean checkAssignmentType(ValueType variable, ValueType assigned) {
        if (variable == ValueType.Integer && assigned == ValueType.Integer)
            return true;
        if(variable == ValueType.Real && assigned == ValueType.Real)
            return true;
        if(variable == ValueType.Bool && assigned == ValueType.Bool)
            return true;
        if(variable == ValueType.String && assigned == ValueType.String)
            return true;
        else
            return false;
    }


    //Semantic check per gli operatori binari PLUS,TIMES...
    public static ValueType getType_Operations(ValueType type1,ValueType type2) {
        if (type1 == ValueType.Integer && type2 == ValueType.Integer)
            return ValueType.Integer;
        if(type1 == ValueType.Integer && type2 == ValueType.Real)
            return ValueType.Real;
        if(type1 == ValueType.Real && type2 == ValueType.Integer)
            return ValueType.Real;
        if(type1 == ValueType.Real && type2 == ValueType.Real)
            return ValueType.Real;
        else
            return null;
    }

    //Semantic check per l'operatore divint(Divisione tra due interi)
    public static ValueType getType_DivIntOp(ValueType type1,ValueType type2){
        if(type1 == ValueType.Integer && type2 == ValueType.Integer){
            return ValueType.Integer;
        }
        else
            return null;
    }

    //Semantic check per gli operatori binari AND e OR
    public static ValueType getType_AndOr(ValueType type1, ValueType type2){
        if(type1 == ValueType.Bool && type2 == ValueType.Bool)
            return ValueType.Bool;
        else
            return null;
    }

    //Semantic check per gli operatori binari: LT,LE,GT...
    public static ValueType getType_Boolean(ValueType type1, ValueType type2){
        if(type1 == ValueType.Integer && type2 == ValueType.Integer)
            return ValueType.Bool;
        if(type1 == ValueType.Real && type2 == ValueType.Integer)
            return ValueType.Bool;
        if(type1 == ValueType.Integer && type2 == ValueType.Real)
            return ValueType.Bool;
        if(type1 == ValueType.Real && type2 == ValueType.Real)
            return ValueType.Bool;
        else
            return null;
    }

    //Semantic check per gli operatori binarI: EQ,NE
    public static ValueType getType_EQ_NE(ValueType type1, ValueType type2){
        if(type1 == ValueType.Integer && type2 == ValueType.Integer)
            return ValueType.Bool;
        if(type1 == ValueType.Real && type2 == ValueType.Real)
            return ValueType.Bool;
        if(type1 == ValueType.Integer && type2 == ValueType.Real)
            return ValueType.Bool;
        if(type1 == ValueType.Real && type2 == ValueType.Integer)
            return ValueType.Bool;
        if(type1 == ValueType.String && type2 == ValueType.String)
            return ValueType.Bool;
        if(type1 == ValueType.Bool && type2 == ValueType.Bool)
            return ValueType.Bool;
        else
            return null;

    }


    //Semantic check per string concat (&)
    public static ValueType getType_StrConcat(ValueType type1, ValueType type2){
        if(type1 == ValueType.String && type2 == ValueType.String){
            return ValueType.String;
        }
        if(type1 == ValueType.String && type2 == ValueType.Integer){
            return ValueType.String;
        }
        if(type1 == ValueType.String && type2 == ValueType.Real){
            return ValueType.String;
        }
        if(type1 == ValueType.String && type2 == ValueType.Bool){
            return ValueType.String;
        }
        else
            return null;
    }


}
