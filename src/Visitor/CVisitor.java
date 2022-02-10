package Visitor;

import jdk.internal.org.objectweb.asm.tree.analysis.Value;
import jdk.nashorn.internal.codegen.CompilerConstants;
import nodes.*;
import leafs.*;
import support.SymbolTable;
import support.SymbolTableEntry;
import support.ValueType;

import java.io.*;
import java.util.ArrayList;


public class CVisitor implements ICVisitor {

    private PrintWriter writer;
    //private static int argumentsMain = 0;
    private String actualFunName = "";
    private String actualName = "";
    ArrayList<String> outParName = new ArrayList();
    boolean flagInt = false;

    public CVisitor(String name) throws IOException {
        File file = new File(name.substring(0,name.length()-4).split("/")[1] + ".c");
        if(file.createNewFile()){
            //File non esistente, lo crea
            System.out.println("File " + file.getName() + " created");
        } else {
            //File esistente
            file.delete();
            file.createNewFile();
            System.out.println("File " + file.getName() + " created");
        }

        writer = new PrintWriter(file);
    }

    public void visit(ProgramNode node) {
        writer.println("#include <stdio.h>");
        writer.println("#include <stdlib.h>");
        writer.println("#include <stdbool.h>");
        writer.println("#include <math.h>");
        writer.println("#include <unistd.h>");
        writer.println("#include <string.h>");
        writer.println("#define MAXCHAR 512\n");

        writer.println("char* int_to_string(int number) {");
        writer.println("\tchar* buffer = malloc(sizeof(char));");
        writer.println("\tsprintf(buffer, \"%d\", number);");
        writer.println("\treturn buffer;");
        writer.println("}\n");

        writer.println("char* double_to_string(double number) {");
        writer.println("\tchar* buffer = malloc(sizeof(char));");
        writer.println("\tsprintf(buffer, \"%.2f\", number);");
        writer.println("\treturn buffer;");
        writer.println("}\n");

        writer.println("char* bool_to_string(bool flag) {");
        writer.println("\tchar* buffer = malloc(sizeof(char));");
        writer.println("\tif(flag == true)");
        writer.println("\t\tbuffer = \"true\";");
        writer.println("\telse");
        writer.println("\t\tbuffer = \"false\";");
        writer.println("\treturn buffer;");
        writer.println("}\n");

        writer.println("char* concat_string(char* str1, char* str2){");
        writer.println("\tchar* buffer = malloc(sizeof(char) * MAXCHAR);");
        writer.println(("\t*buffer = '\\0';"));
        writer.println(("\tstrcat(buffer, str1);"));
        writer.println(("\tstrcat(buffer, str2);"));
        writer.println("\treturn buffer;");
        writer.println("}\n");


        //ArrayList<VarDeclNode>
        if(node.nodeArrayList != null) {
            if(node.nodeArrayList.size() != 0) {
                writer.println("//Dichiarazione variabili globali");
            }
            for(VarDeclNode declNodo : node.nodeArrayList) {
                declNodo.accept(this);
            }
        }

        //ArrayList<FunNode>
        for(FunNode funNode : node.funNodeArrayList) {
            funNode.accept(this);
        }

        //BodyNode
        writer.println("int main () {\n");
        node.bodyNode.accept(this);
        writer.println("}");
        writer.close();

        //return argumentsMain;
    }

    public void visit(BodyNode node){

        //ArrayList<VarDeclNode>
        if(node.vardecl != null){
            for(VarDeclNode varDeclNode : node.vardecl){
                varDeclNode.accept(this);
            }
        }

        //StatListNode
        node.stats.accept(this);
    }

    public void visit(VarDeclNode node){


        //TypeNode
        //node.type.accept(this);

        //ArrayList<IdInitNode>
        for(IdInitNode idInitNode : node.identifiers) {

            node.setType(idInitNode.type.toString());

            //TypeNode
            node.type.accept(this);

            //IdInitNode
            idInitNode.accept(this);

        }

    }

    public void visit(TypeNode node) {
        try{
            if(SymbolTable.StringToType(node.type) == ValueType.Integer) {
                writer.print("int ");
            } else if(SymbolTable.StringToType(node.type) == ValueType.Real){
                writer.print("double ");
            } else if(SymbolTable.StringToType(node.type) == ValueType.String){
                writer.print("char* ");
            } else if(SymbolTable.StringToType(node.type) == ValueType.Bool){
                writer.print("bool ");
            }
            /*
            else {
                writer.print("void ");
            }
            */
        } catch (Exception e){
            e.printStackTrace();
            System.exit(0);
        }
    }

    public void visit(IdInitNode node) {

        boolean flag = false;

        //LeafID
        node.leaf.accept(this);
        /*
        if(node.type == ValueType.String) {
            writer.print("*");
        }
        */

        //ExprNode
        if(node.expr != null){
            writer.print(" = ");
            node.expr.accept(this);
            if(node.expr.value1 instanceof CallFunNode){
                flag = true;
            }
        }

        //ConstNode
        if(node.c != null){
            writer.print(" = ");
            node.c.accept(this);
        }

        if(flag){
        }
        else {
            writer.println(";");
        }
    }

    public void visit(ConstNode constNode){

        if(constNode.value1 instanceof LeafIntegerConst){
            ((LeafIntegerConst) constNode.value1).accept(this);
            //constNode.setType(((LeafStringConst) constNode.value1).type);
        }
        else if(constNode.value1 instanceof LeafStringConst){
            ((LeafStringConst) constNode.value1).accept(this);
            //constNode.setType(((LeafStringConst) constNode.value1).type);
        }
        else if(constNode.value1 instanceof LeafRealConst){
            ((LeafRealConst) constNode.value1).accept(this);
            //constNode.setType(((LeafRealConst) constNode.value1).type);
        }
        else {
            ((LeafBool) constNode.value1).accept(this);
            //constNode.setType(((LeafBool) constNode.value1).type);
        }
    }

    public void visit(FunNode node){
        writer.println("\n//Fun " + node.leaf.value);
        this.actualFunName = node.leaf.value;

        //node.typeNode.accept(this);
        //Controllo sul tipo di ritorno della funzione
        if(node.getTypeNode() == null){
            writer.print("void " + node.leaf.value);
            //Controllo nel caso in cui la funzione non abbia parametri
            if(node.pardecl.size() == 0){
                writer.println("() {");
            }
            //Caso in cui la funzione ha dei parametri
            else if(node.pardecl != null && node.pardecl.size() > 0) {
                writer.print("(");
                ParDeclNode lastParDeclNode = node.pardecl.get(node.pardecl.size() - 1);
                for(ParDeclNode parDeclNode : node.pardecl) {
                    parDeclNode.accept(this);
                    if(parDeclNode.mod.mod.equals("out")){
                      outParName.add(parDeclNode.leaf.value);
                    }
                    if(lastParDeclNode != parDeclNode){
                        writer.print(",");
                    }
                }
                writer.println(") {");
            }
        }
        //Caso in cui la funzione ha un tipo di ritorno diverso da void
        else {
            //Mi prendo il tipo di ritorno
            node.typeNode.accept(this);

            //Controllo se il tipo di ritorno è una stringa per inserire il puntatore (*)
            if(node.typeNode.type.equalsIgnoreCase("String")){
                writer.print("* ");
            }

            //Stampo il nome della funzione
            writer.print(node.leaf.value);

            //Parametri
            if(node.pardecl.size() == 0){
                writer.println("() {");
            }
            //Caso in cui ho i parametri
            else if(node.pardecl != null && node.pardecl.size() > 0) {
                writer.print("(");
                    for(ParDeclNode parDeclNode : node.pardecl){
                        parDeclNode.accept(this);
                        if(parDeclNode.mod.mod.equals("out")){
                            outParName.add(parDeclNode.leaf.value);
                        }
                        if(node.pardecl.get(node.pardecl.size() - 1 ) != parDeclNode) {
                            writer.print(",");
                        }
                    }
                    writer.println(") {");
            }
        }
        //Vado a gestire il body della funzione
        node.bodyNode.accept(this);
        outParName.clear();
        writer.println("}\n\n");
    }

    public void visit(ParDeclNode node) {

        //Mi prendo il tipo del parametro
        node.typeNode.accept(this);
        /*if(node.typeNode.type.equalsIgnoreCase("String")){
            writer.print("* ");
        }*/
        node.mod.accept(this);
        if(node.mod.mod.equals("out")){
            writer.print("*");
        }

        //Mi prendo il nome del parametro
        node.leaf.accept(this);

        //writer.print(",");
    }

    public void visit(ModeParNode node){

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

    public void visit(IfStatNode node) {
        writer.print("if (");
        //Gestisco la condizione dell'if (Cioè expr)
        node.expr.accept(this);
        writer.println(") {");
        //Gestisco il body dell'if
        node.bn.accept(this);
        writer.println("}");

        //Caso in cui c'è l'else
        if(node.elsebody != null){
            writer.println("else {");
            //Gestisco il body dell'else
            node.elsebody.accept(this);
            writer.println("}");
        }
    }

    public void visit(WhileStatNode node) {

        writer.print("while (");

        //Gestisco la condizione del while (Expr)
        node.expr.accept(this);
        writer.println(") {");

        //Gestisco il body del while
        node.bn.accept(this);
        writer.println("}");
    }

    public void visit(ReadStatNode node) {

        for(LeafID leafID : node.idlist) {
            if(leafID.getType().equals(ValueType.String)){
                writer.print("scanf(\"%s\", ");
                leafID.accept(this);
                writer.println(");");
            }
            else if (leafID.getType().equals(ValueType.Bool) || leafID.getType().equals(ValueType.Integer)) {
                writer.print("scanf(\"%d\", &");
                leafID.accept(this);
                writer.println(");");
            } else if (leafID.getType() == ValueType.Real) {
                writer.print("scanf(\"%lf\", &"); //per double
                //writer.printf("scanf(\"%f\", &"); per float
                leafID.accept(this);
                writer.println(");");
            }
        }
        //Gestisco l'exprNode
        //node.expr.accept(this);
    }

    public void visit(WriteStatNode node) {
        writer.print("printf(");
        if(!(node.expr.value1 instanceof ConstNode))
            writer.print("\"%s\", ");
        if(node.expr.value1 instanceof LeafID){
            writer.print("*");
        }
        node.expr.accept(this);
        writer.println(");");

        if(node.node.name.equals("writeln")){
            writer.println("printf(\"\\n\");");
        }
        else if(node.node.name.equals("writeb")){
            writer.println("printf(\" \");");
        }
        else if(node.node.name.equals("writet")){
            writer.println("printf(\"\\t\");");
        }


    }

    public void visit(ReturnStatNode node) {
        writer.print("return ");
        //ExprNode di ritorno
        node.expr.accept(this);
        writer.print(";\n");
    }


    public void visit(AssignStatNode node) {

        int i = 0;
        for(i = 0; i < outParName.size();){
            if(outParName.get(i).equals(node.leaf.value)){
                writer.print("*");
            }
            i++;
        }

        if(node.leaf.type.equals(ValueType.String)){
            node.leaf.accept(this);
            writer.print(" = malloc(sizeof(char) * strlen(");
            node.expr.accept(this);
            writer.print("));\n");
            writer.print("strcpy(");
            for(i = 0; i < outParName.size();){
                if(outParName.get(i).equals(node.leaf.value)){
                    writer.print("*");
                }
                i++;
            }
            node.leaf.accept(this);
            writer.print(", ");
            node.expr.accept(this);
            writer.println(");");
        }
        else {
            node.leaf.accept(this);
            writer.print(" = ");
            node.expr.accept(this);
            if(node.expr.value1 instanceof CallFunNode){
            }
            else {
                writer.println(";");
            }
        }
    }

    public void visit(CallFunNode node) {

        node.leafID.accept(this);

        if(node.exprList != null && node.exprList.size() > 0) {
            writer.print("(");
            ExprNode lastExprNode = node.exprList.get(node.exprList.size() - 1);
            for(ExprNode exprNode : node.exprList) {
                if(exprNode.mod.mod.equals("out")){
                    writer.print("&");
                    exprNode.leaf.accept(this);
                }
                exprNode.accept(this);
                if(exprNode != lastExprNode)
                    writer.print(", ");
            }
            writer.println(");");
        }
        else {
            writer.println("();");
        }

    }

    public void visit(ExprNode exprNode){
        if(exprNode.value1 != null && exprNode.value2 != null) {

            switch(exprNode.name) {
                case "AddOp":
                    ((ExprNode) exprNode.value1).accept(this);
                    writer.print(" + ");
                    ((ExprNode) exprNode.value2).accept(this);
                    break;
                case "DiffOp":
                    ((ExprNode) exprNode.value1).accept(this);
                    writer.print(" - ");
                    ((ExprNode) exprNode.value2).accept(this);
                    break;
                case "DivOp":
                case "DivIntOp":
                    ((ExprNode) exprNode.value1).accept(this);
                    writer.print(" / ");
                    ((ExprNode) exprNode.value2).accept(this);
                    break;
                case "MulOp":
                    ((ExprNode) exprNode.value1).accept(this);
                    writer.print(" * ");
                    ((ExprNode) exprNode.value2).accept(this);
                    break;
                case "PowOp":
                    writer.print(" pow(");
                    ((ExprNode) exprNode.value1).accept(this);
                    writer.print(",");
                    ((ExprNode) exprNode.value2).accept(this);
                    writer.print(")");
                    break;
                case "AndOp":
                    ((ExprNode) exprNode.value1).accept(this);
                    writer.print(" && ");
                    ((ExprNode) exprNode.value2).accept(this);
                    break;
                case "OrOp":
                    ((ExprNode) exprNode.value1).accept(this);
                    writer.print(" || ");
                    ((ExprNode) exprNode.value2).accept(this);
                    break;
                case "GTOp":
                    if(((ExprNode) exprNode.value1).types.get(0) == ValueType.String) {
                        writer.print("\tstrcmp(");
                        ((ExprNode) exprNode.value1).accept(this);
                        writer.print(", ");
                        ((ExprNode) exprNode.value2).accept(this);
                        writer.print(") > 0");
                    } else {
                        ((ExprNode) exprNode.value1).accept(this);
                        writer.print(" > ");
                        ((ExprNode) exprNode.value2).accept(this);
                    }
                    break;
                case "GEOp":
                    if(((ExprNode) exprNode.value1).types.get(0) == ValueType.String){
                        writer.print("strcmp(");
                        ((ExprNode) exprNode.value1).accept(this);
                        writer.print(", ");
                        ((ExprNode) exprNode.value2).accept(this);
                        writer.print(") >= 0");
                    } else {
                        ((ExprNode) exprNode.value1).accept(this);
                        writer.print(" >= ");
                        ((ExprNode) exprNode.value2).accept(this);
                    }
                    break;
                case "LTOp":
                    if(((ExprNode) exprNode.value1).types.get(0) == ValueType.String) {
                        writer.print("strcmp(");
                        ((ExprNode) exprNode.value1).accept(this);
                        writer.print(", ");
                        ((ExprNode) exprNode.value2).accept(this);
                        writer.print(") < 0");
                    } else {
                        ((ExprNode) exprNode.value1).accept(this);
                        writer.print(" < ");
                        ((ExprNode) exprNode.value2).accept(this);
                    }
                    break;
                case "LEOp":
                    if (((ExprNode) exprNode.value1).types.get(0) == ValueType.String) {
                        writer.print("strcmp(");
                        ((ExprNode) exprNode.value1).accept(this);
                        writer.print(", ");
                        ((ExprNode) exprNode.value2).accept(this);
                        writer.print(") <= 0");
                    } else {
                        ((ExprNode) exprNode.value1).accept(this);
                        writer.print(" <= ");
                        ((ExprNode) exprNode.value2).accept(this);
                    }
                    break;
                case "EQOp":
                    if (((ExprNode) exprNode.value1).types.get(0) == ValueType.String) {
                        writer.print("strcmp(");
                        ((ExprNode) exprNode.value1).accept(this);
                        writer.print(", ");
                        ((ExprNode) exprNode.value2).accept(this);
                        writer.print(") == 0");
                    } else {
                        ((ExprNode) exprNode.value1).accept(this);
                        writer.print(" == ");
                        ((ExprNode) exprNode.value2).accept(this);
                    }
                    break;
                case "NEOp":
                if (((ExprNode) exprNode.value1).types.get(0) == ValueType.String) {
                    writer.print("strcmp(");
                    ((ExprNode) exprNode.value1).accept(this);
                    writer.print(", ");
                    ((ExprNode) exprNode.value2).accept(this);
                    writer.print(") != 0");
                } else {
                    ((ExprNode) exprNode.value1).accept(this);
                    writer.print(" != ");
                    ((ExprNode) exprNode.value2).accept(this);
                }
                break;
                case "StrCatOp":
                                    writer.print("(");
                                    writer.print("concat_string(");
                                    //((ExprNode) exprNode.value1).accept(this);
                                    strConcatSupport((ExprNode) exprNode.value1);
                                    writer.print(",");
                                    //((ExprNode) exprNode.value2).accept(this);
                                    strConcatSupport((ExprNode) exprNode.value2);
                                    writer.print(")");
                                    writer.print(")");
                break;
                    }
            }
        else if(exprNode.value1 != null) {
            if(exprNode.name.equalsIgnoreCase("UminusOp")) {
                writer.print("-");
                ((ExprNode) exprNode.value1).accept(this);
            } else if(exprNode.name.equalsIgnoreCase("NotOp")){
                writer.print("!");
                ((ExprNode) exprNode.value1).accept(this);
            } else if(exprNode.value1 instanceof LeafIntegerConst){
                ((LeafIntegerConst) exprNode.value1).accept(this);
            } else if(exprNode.value1 instanceof LeafRealConst){
                ((LeafRealConst) exprNode.value1).accept(this);
            } else if(exprNode.value1 instanceof LeafStringConst){
                ((LeafStringConst) exprNode.value1).accept(this);
            } else if(exprNode.value1 instanceof LeafBool){
                ((LeafBool) exprNode.value1).accept(this);
            } else if(exprNode.value1 instanceof LeafID){
                ((LeafID) exprNode.value1).accept(this);
            } else if(exprNode.value1 instanceof CallFunNode) {
                ((CallFunNode) exprNode.value1).accept(this);
            } else if(exprNode.value1 instanceof ExprNode) {
                ((ExprNode) exprNode.value1).accept(this);
            }
        }
    }

    public void visit(LeafBool leaf) {
        writer.print(leaf.value);
    }

    public void visit(LeafRealConst leaf) {
        writer.print(leaf.value);
    }

    public void visit(LeafID leaf){
        writer.print(leaf.value);
    }

    public void visit(LeafIntegerConst leaf) {
        writer.print(leaf.value);
    }

    public void visit(LeafStringConst leaf) {
        writer.print("\"" + leaf.value + "\"");
    }

    private void strConcatSupport(ExprNode exprNode){
            if(exprNode.value1 instanceof LeafIntegerConst){
                writer.print("int_to_string(");
            }
            else if(exprNode.value1 instanceof LeafRealConst){
                writer.print("double_to_string(");
            }
            else if(exprNode.value1 instanceof LeafBool){
                writer.print("bool_to_string");
            }
            else if(exprNode.value1 instanceof LeafID){
               ValueType type = ((LeafID) exprNode.value1).getType();
               if(type.equals(ValueType.Integer)){
                   writer.print("int_to_string(");
               }
               if(type.equals(ValueType.Real)){
                   writer.print("double_to_string(");
               }
               if(type.equals(ValueType.Bool)){
                   writer.print("bool_to_string(");
               }
            }
            else if(exprNode.value1 instanceof CallFunNode){
                ValueType type = ((CallFunNode) exprNode.value1).types.get(0);
                if(type.equals(ValueType.Integer)){
                    writer.print("int_to_string(");
                }
                if(type.equals(ValueType.Real)){
                    writer.print("double_to_string(");
                }
                if(type.equals(ValueType.Bool)){
                    writer.print("bool_to_string(");
                }
            }
             exprNode.accept(this);
             writer.print(")");

    }

            /*if(exprNode.value1 instanceof LeafIntegerConst){
                writer.print("int_to_string(");

            }

            /*
            if( exprNode.value2 instanceof LeafIntegerConst){
                writer.print("int_to_string(");
                ((LeafIntegerConst) exprNode.value2).accept(this);
                writer.print(")");
            }
            /*
            if((((ExprNode) exprNode.value1).types.get(0).equals(ValueType.Integer)) && ((ExprNode) exprNode.value2).value1 instanceof LeafID){
                if(((LeafID) ((ExprNode) exprNode.value2).value1).getType().equals(ValueType.Integer)){
                    ((LeafStringConst) ((ExprNode) exprNode.value1).value1).accept(this);
                    writer.print(",");
                    writer.print("int_to_string(");
                    ((LeafID) ((ExprNode) exprNode.value2).value1).accept(this);
                    writer.print(")");
                }
            else if(((LeafID) ((ExprNode) exprNode.value2).value1).getType().equals(ValueType.Bool)) {
                ((LeafStringConst) ((ExprNode) exprNode.value1).value1).accept(this);
                writer.print(",");
                writer.print("bool_to_string(");
                ((LeafID) ((ExprNode) exprNode.value2).value1).accept(this);
                writer.print(")");
            }
            else if(((LeafID) ((ExprNode) exprNode.value2).value1).getType().equals(ValueType.Real)) {
                ((LeafStringConst) ((ExprNode) exprNode.value1).value1).accept(this);
                writer.print(",");
                writer.print("double_to_string(");
                ((LeafID) ((ExprNode) exprNode.value2).value1).accept(this);
                writer.print(")");
            }
            else if(((LeafID) ((ExprNode) exprNode.value2).value1).getType().equals(ValueType.String)) {
                ((LeafStringConst) ((ExprNode) exprNode.value1).value1).accept(this);
                writer.print(",");
                ((LeafID) ((ExprNode) exprNode.value2).value1).accept(this);
                writer.print(")");
            }
        }
        //Concat di Real con tutti gli altri tipi non costanti
        else if(((ExprNode) exprNode.value1).value1 instanceof LeafRealConst && ((ExprNode) exprNode.value2).value1 instanceof LeafID){
            if(((LeafID) ((ExprNode) exprNode.value2).value1).getType().equals(ValueType.Integer)) {
                writer.print("double_to_string(");
                ((LeafRealConst) ((ExprNode) exprNode.value1).value1).accept(this);
                writer.print(")");
                writer.print(",");
                writer.print("int_to_string(");
                ((LeafID) ((ExprNode) exprNode.value2).value1).accept(this);
                writer.print(")");
            }
            else if(((LeafID) ((ExprNode) exprNode.value2).value1).getType().equals(ValueType.String)) {
                writer.print("double_to_string(");
                ((LeafRealConst) ((ExprNode) exprNode.value1).value1).accept(this);
                writer.print(")");
                writer.print(",");
                ((LeafID) ((ExprNode) exprNode.value2).value1).accept(this);
            }
            else if(((LeafID) ((ExprNode) exprNode.value2).value1).getType().equals(ValueType.Bool)) {
                writer.print("double_to_string(");
                ((LeafRealConst) ((ExprNode) exprNode.value1).value1).accept(this);
                writer.print(")");
                writer.print(",");
                writer.print("bool_to_string(");
                ((LeafID) ((ExprNode) exprNode.value2).value1).accept(this);
                writer.print(")");
            }
            else if(((LeafID) ((ExprNode) exprNode.value2).value1).getType().equals(ValueType.Real)){
                writer.print("double_to_string(");
                ((LeafRealConst) ((ExprNode) exprNode.value1).value1).accept(this);
                writer.print(")");
                writer.print(",");
                writer.print("double_to_string(");
                ((LeafID) ((ExprNode) exprNode.value2).value1).accept(this);
                writer.print(")");
            }
        }
        //Concat di Integer con tutti gli altri tipi non costanti
        else if(((ExprNode) exprNode.value1).value1 instanceof LeafIntegerConst && ((ExprNode) exprNode.value2).value1 instanceof LeafID){
            if(((LeafID) ((ExprNode) exprNode.value2).value1).getType().equals(ValueType.Integer)) {
                writer.print("int_to_string(");
                ((LeafIntegerConst) ((ExprNode) exprNode.value1).value1).accept(this);
                writer.print(")");
                writer.print(",");
                writer.print("int_to_string(");
                ((LeafID) ((ExprNode) exprNode.value2).value1).accept(this);
                writer.print(")");
            }
            else if(((LeafID) ((ExprNode) exprNode.value2).value1).getType().equals(ValueType.String)) {
                writer.print("int_to_string(");
                ((LeafIntegerConst) ((ExprNode) exprNode.value1).value1).accept(this);
                writer.print(")");
                writer.print(",");
                ((LeafID) ((ExprNode) exprNode.value2).value1).accept(this);
            }
            else if(((LeafID) ((ExprNode) exprNode.value2).value1).getType().equals(ValueType.Bool)) {
                writer.print("int_to_string(");
                ((LeafIntegerConst) ((ExprNode) exprNode.value1).value1).accept(this);
                writer.print(")");
                writer.print(",");
                writer.print("bool_to_string(");
                ((LeafID) ((ExprNode) exprNode.value2).value1).accept(this);
                writer.print(")");
            }
            else if(((LeafID) ((ExprNode) exprNode.value2).value1).getType().equals(ValueType.Real)){
                writer.print("int_to_string(");
                ((LeafIntegerConst) ((ExprNode) exprNode.value1).value1).accept(this);
                writer.print(")");
                writer.print(",");
                writer.print("double_to_string(");
                ((LeafID) ((ExprNode) exprNode.value2).value1).accept(this);
                writer.print(")");
            }
        }
        //Concat di Bool con tutti gli altri tipi non costanti
        else if(((ExprNode) exprNode.value1).value1 instanceof LeafBool && ((ExprNode) exprNode.value2).value1 instanceof LeafID){
            if(((LeafID) ((ExprNode) exprNode.value2).value1).getType().equals(ValueType.Integer)) {
                writer.print("bool_to_string(");
                ((LeafRealConst) ((ExprNode) exprNode.value1).value1).accept(this);
                writer.print(")");
                writer.print(",");
                writer.print("int_to_string(");
                ((LeafID) ((ExprNode) exprNode.value2).value1).accept(this);
                writer.print(")");
            }
            else if(((LeafID) ((ExprNode) exprNode.value2).value1).getType().equals(ValueType.String)) {
                writer.print("bool_to_string(");
                ((LeafRealConst) ((ExprNode) exprNode.value1).value1).accept(this);
                writer.print(")");
                writer.print(",");
                ((LeafID) ((ExprNode) exprNode.value2).value1).accept(this);
            }
            else if(((LeafID) ((ExprNode) exprNode.value2).value1).getType().equals(ValueType.Bool)) {
                writer.print("bool_to_string(");
                ((LeafRealConst) ((ExprNode) exprNode.value1).value1).accept(this);
                writer.print(")");
                writer.print(",");
                writer.print("bool_to_string(");
                ((LeafID) ((ExprNode) exprNode.value2).value1).accept(this);
                writer.print(")");
            }
            else if(((LeafID) ((ExprNode) exprNode.value2).value1).getType().equals(ValueType.Real)){
                writer.print("bool_to_string(");
                ((LeafRealConst) ((ExprNode) exprNode.value1).value1).accept(this);
                writer.print(")");
                writer.print(",");
                writer.print("double_to_string(");
                ((LeafID) ((ExprNode) exprNode.value2).value1).accept(this);
                writer.print(")");
            }
        }
        //Concat di tutti i tipi di variabili con string const
        else if(((ExprNode) exprNode.value1).value1 instanceof LeafID && ((ExprNode) exprNode.value2).value1 instanceof LeafStringConst){
            if(((LeafID) ((ExprNode) exprNode.value1).value1).getType().equals(ValueType.String)){
                ((LeafID) ((ExprNode) exprNode.value1).value1).accept(this);
                writer.print(",");
                ((LeafStringConst) ((ExprNode) exprNode.value2).value1).accept(this);
            }
            else if(((LeafID) ((ExprNode) exprNode.value1).value1).getType().equals(ValueType.Integer)){
                writer.print("int_to_string(");
                ((LeafID) ((ExprNode) exprNode.value1).value1).accept(this);
                writer.print(")");
                writer.print(",");
                ((LeafID) ((ExprNode) exprNode.value2).value1).accept(this);

            }
            else if(((LeafID) ((ExprNode) exprNode.value1).value1).getType().equals(ValueType.Real)){
                writer.print("double_to_string(");
                ((LeafID) ((ExprNode) exprNode.value1).value1).accept(this);
                writer.print(")");
                writer.print(",");
                ((LeafID) ((ExprNode) exprNode.value2).value1).accept(this);

            }
            else if(((LeafID) ((ExprNode) exprNode.value1).value1).getType().equals(ValueType.Bool)){
                writer.print("bool_to_string(");
                ((LeafID) ((ExprNode) exprNode.value1).value1).accept(this);
                writer.print(")");
                writer.print(",");
                ((LeafID) ((ExprNode) exprNode.value2).value1).accept(this);
            }
        }
        //Concat di tutti i tipi di variabili con integer const
        else if(((ExprNode) exprNode.value1).value1 instanceof LeafID && ((ExprNode) exprNode.value2).value1 instanceof LeafIntegerConst){
            if(((LeafID) ((ExprNode) exprNode.value1).value1).getType().equals(ValueType.String)){
                ((LeafID) ((ExprNode) exprNode.value1).value1).accept(this);
                writer.print(",");
                writer.print("int_to_string(");
                ((LeafIntegerConst) ((ExprNode) exprNode.value2).value1).accept(this);
                writer.print(")");
            }
            else if(((LeafID) ((ExprNode) exprNode.value1).value1).getType().equals(ValueType.Integer)){
                writer.print("int_to_string(");
                ((LeafID) ((ExprNode) exprNode.value1).value1).accept(this);
                writer.print(")");
                writer.print(",");
                writer.print("int_to_string(");
                ((LeafID) ((ExprNode) exprNode.value2).value1).accept(this);
                writer.print(")");

            }
            else if(((LeafID) ((ExprNode) exprNode.value1).value1).getType().equals(ValueType.Real)){
                writer.print("double_to_string(");
                ((LeafID) ((ExprNode) exprNode.value1).value1).accept(this);
                writer.print(")");
                writer.print(",");
                writer.print("int_to_string(");
                ((LeafID) ((ExprNode) exprNode.value2).value1).accept(this);
                writer.print(")");

            }
            else if(((LeafID) ((ExprNode) exprNode.value1).value1).getType().equals(ValueType.Bool)){
                writer.print("bool_to_string(");
                ((LeafID) ((ExprNode) exprNode.value1).value1).accept(this);
                writer.print(")");
                writer.print(",");
                writer.print("int_to_string(");
                ((LeafID) ((ExprNode) exprNode.value2).value1).accept(this);
                writer.print(")");
            }
        }
        //Concat di tutti i tipi di variabili con real const
        else if(((ExprNode) exprNode.value1).value1 instanceof LeafID && ((ExprNode) exprNode.value2).value1 instanceof LeafRealConst){
            if(((LeafID) ((ExprNode) exprNode.value1).value1).getType().equals(ValueType.String)){
                ((LeafID) ((ExprNode) exprNode.value1).value1).accept(this);
                writer.print(",");
                writer.print("double_to_string(");
                ((LeafRealConst) ((ExprNode) exprNode.value2).value1).accept(this);
                writer.print(")");
            }
            else if(((LeafID) ((ExprNode) exprNode.value1).value1).getType().equals(ValueType.Integer)){
                writer.print("int_to_string(");
                ((LeafID) ((ExprNode) exprNode.value1).value1).accept(this);
                writer.print(")");
                writer.print(",");
                writer.print("double_to_string(");
                ((LeafID) ((ExprNode) exprNode.value2).value1).accept(this);
                writer.print(")");

            }
            else if(((LeafID) ((ExprNode) exprNode.value1).value1).getType().equals(ValueType.Real)){
                writer.print("double_to_string(");
                ((LeafID) ((ExprNode) exprNode.value1).value1).accept(this);
                writer.print(")");
                writer.print(",");
                writer.print("double_to_string(");
                ((LeafID) ((ExprNode) exprNode.value2).value1).accept(this);
                writer.print(")");

            }
            else if(((LeafID) ((ExprNode) exprNode.value1).value1).getType().equals(ValueType.Bool)){
                writer.print("bool_to_string(");
                ((LeafID) ((ExprNode) exprNode.value1).value1).accept(this);
                writer.print(")");
                writer.print(",");
                writer.print("double_to_string(");
                ((LeafID) ((ExprNode) exprNode.value2).value1).accept(this);
                writer.print(")");
            }
        }
        //Concat di tutti i tipi di variabili con bool const
        else if(((ExprNode) exprNode.value1).value1 instanceof LeafID && ((ExprNode) exprNode.value2).value1 instanceof LeafBool){
            if(((LeafID) ((ExprNode) exprNode.value1).value1).getType().equals(ValueType.String)){
                ((LeafID) ((ExprNode) exprNode.value1).value1).accept(this);
                writer.print(",");
                writer.print("bool_to_string(");
                ((LeafStringConst) ((ExprNode) exprNode.value2).value1).accept(this);
                writer.print(")");
            }
            else if(((LeafID) ((ExprNode) exprNode.value1).value1).getType().equals(ValueType.Integer)){
                writer.print("int_to_string(");
                ((LeafID) ((ExprNode) exprNode.value1).value1).accept(this);
                writer.print(")");
                writer.print(",");
                writer.print("bool_to_string(");
                ((LeafID) ((ExprNode) exprNode.value2).value1).accept(this);
                writer.print(")");

            }
            else if(((LeafID) ((ExprNode) exprNode.value1).value1).getType().equals(ValueType.Real)){
                writer.print("double_to_string(");
                ((LeafID) ((ExprNode) exprNode.value1).value1).accept(this);
                writer.print(")");
                writer.print(",");
                writer.print("bool_to_string(");
                ((LeafID) ((ExprNode) exprNode.value2).value1).accept(this);
                writer.print(")");

            }
            else if(((LeafID) ((ExprNode) exprNode.value1).value1).getType().equals(ValueType.Bool)){
                writer.print("bool_to_string(");
                ((LeafID) ((ExprNode) exprNode.value1).value1).accept(this);
                writer.print(")");
                writer.print(",");
                writer.print("bool_to_string(");
                ((LeafID) ((ExprNode) exprNode.value2).value1).accept(this);
                writer.print(")");
            }
        }
        ////Concat di tutti i tipi di variabili con tutti i tipi di variabili
        else if(((ExprNode) exprNode.value1).value1 instanceof LeafID && ((ExprNode) exprNode.value2).value1 instanceof LeafID){
            if(((LeafID) ((ExprNode) exprNode.value1).value1).getType().equals(ValueType.String) && ((LeafID) ((ExprNode) exprNode.value2).value1).getType().equals(ValueType.String)){
                ((LeafID) ((ExprNode) exprNode.value1).value1).accept(this);
                writer.print(",");
                ((LeafID) ((ExprNode) exprNode.value2).value1).accept(this);
            }
            else if(((LeafID) ((ExprNode) exprNode.value1).value1).getType().equals(ValueType.String) && ((LeafID) ((ExprNode) exprNode.value2).value1).getType().equals(ValueType.Integer)){
                ((LeafID) ((ExprNode) exprNode.value1).value1).accept(this);
                writer.print(",");
                writer.print("int_to_string(");
                ((LeafID) ((ExprNode) exprNode.value2).value1).accept(this);
                writer.print(")");
            }
            else if(((LeafID) ((ExprNode) exprNode.value1).value1).getType().equals(ValueType.String) && ((LeafID) ((ExprNode) exprNode.value2).value1).getType().equals(ValueType.Real)){
                ((LeafID) ((ExprNode) exprNode.value1).value1).accept(this);
                writer.print(",");
                writer.print("double_to_string(");
                ((LeafID) ((ExprNode) exprNode.value2).value1).accept(this);
                writer.print(")");
            }
            else if(((LeafID) ((ExprNode) exprNode.value1).value1).getType().equals(ValueType.String) && ((LeafID) ((ExprNode) exprNode.value2).value1).getType().equals(ValueType.Bool)){
                ((LeafID) ((ExprNode) exprNode.value1).value1).accept(this);
                writer.print(",");
                writer.print("bool_to_string(");
                ((LeafID) ((ExprNode) exprNode.value2).value1).accept(this);
                writer.print(")");
            }
            else if(((LeafID) ((ExprNode) exprNode.value1).value1).getType().equals(ValueType.Integer) && ((LeafID) ((ExprNode) exprNode.value2).value1).getType().equals(ValueType.String)){
                writer.print("int_to_string(");
                ((LeafID) ((ExprNode) exprNode.value1).value1).accept(this);
                writer.print(")");
                writer.print(",");
                ((LeafID) ((ExprNode) exprNode.value2).value1).accept(this);
            }
            else if(((LeafID) ((ExprNode) exprNode.value1).value1).getType().equals(ValueType.Integer) && ((LeafID) ((ExprNode) exprNode.value2).value1).getType().equals(ValueType.Integer)){
                writer.print("int_to_string(");
                ((LeafID) ((ExprNode) exprNode.value1).value1).accept(this);
                writer.print(")");
                writer.print(",");
                writer.print("int_to_string(");
                ((LeafID) ((ExprNode) exprNode.value2).value1).accept(this);
                writer.print(")");
            }
            else if(((LeafID) ((ExprNode) exprNode.value1).value1).getType().equals(ValueType.Integer) && ((LeafID) ((ExprNode) exprNode.value2).value1).getType().equals(ValueType.Bool)){
                writer.print("int_to_string(");
                ((LeafID) ((ExprNode) exprNode.value1).value1).accept(this);
                writer.print(")");
                writer.print(",");
                writer.print("bool_to_string(");
                ((LeafID) ((ExprNode) exprNode.value2).value1).accept(this);
                writer.print(")");
            }
            else if(((LeafID) ((ExprNode) exprNode.value1).value1).getType().equals(ValueType.Integer) && ((LeafID) ((ExprNode) exprNode.value2).value1).getType().equals(ValueType.Real)){
                writer.print("int_to_string(");
                ((LeafID) ((ExprNode) exprNode.value1).value1).accept(this);
                writer.print(")");
                writer.print(",");
                writer.print("double_to_string(");
                ((LeafID) ((ExprNode) exprNode.value2).value1).accept(this);
                writer.print(")");
            }
            else if(((LeafID) ((ExprNode) exprNode.value1).value1).getType().equals(ValueType.Real) && ((LeafID) ((ExprNode) exprNode.value2).value1).getType().equals(ValueType.String)){
                writer.print("double_to_string(");
                ((LeafID) ((ExprNode) exprNode.value1).value1).accept(this);
                writer.print(")");
                writer.print(",");
                ((LeafID) ((ExprNode) exprNode.value2).value1).accept(this);
            }
            else if(((LeafID) ((ExprNode) exprNode.value1).value1).getType().equals(ValueType.Real) && ((LeafID) ((ExprNode) exprNode.value2).value1).getType().equals(ValueType.Integer)){
                writer.print("double_to_string(");
                ((LeafID) ((ExprNode) exprNode.value1).value1).accept(this);
                writer.print(")");
                writer.print(",");
                writer.print("int_to_string(");
                ((LeafID) ((ExprNode) exprNode.value2).value1).accept(this);
                writer.print(")");
            }
            else if(((LeafID) ((ExprNode) exprNode.value1).value1).getType().equals(ValueType.Real) && ((LeafID) ((ExprNode) exprNode.value2).value1).getType().equals(ValueType.Bool)){
                writer.print("double_to_string(");
                ((LeafID) ((ExprNode) exprNode.value1).value1).accept(this);
                writer.print(")");
                writer.print(",");
                writer.print("bool_to_string(");
                ((LeafID) ((ExprNode) exprNode.value2).value1).accept(this);
                writer.print(")");
            }
            else if(((LeafID) ((ExprNode) exprNode.value1).value1).getType().equals(ValueType.Real) && ((LeafID) ((ExprNode) exprNode.value2).value1).getType().equals(ValueType.Real)){
                writer.print("double_to_string(");
                ((LeafID) ((ExprNode) exprNode.value1).value1).accept(this);
                writer.print(")");
                writer.print(",");
                writer.print("double_to_string(");
                ((LeafID) ((ExprNode) exprNode.value2).value1).accept(this);
                writer.print(")");
            }
            else if(((LeafID) ((ExprNode) exprNode.value1).value1).getType().equals(ValueType.Bool) && ((LeafID) ((ExprNode) exprNode.value2).value1).getType().equals(ValueType.String)){
                writer.print("bool_to_string(");
                ((LeafID) ((ExprNode) exprNode.value1).value1).accept(this);
                writer.print(")");
                writer.print(",");
                ((LeafID) ((ExprNode) exprNode.value2).value1).accept(this);
            }
            else if(((LeafID) ((ExprNode) exprNode.value1).value1).getType().equals(ValueType.Bool) && ((LeafID) ((ExprNode) exprNode.value2).value1).getType().equals(ValueType.Integer)){
                writer.print("bool_to_string(");
                ((LeafID) ((ExprNode) exprNode.value1).value1).accept(this);
                writer.print(")");
                writer.print(",");
                writer.print("int_to_string(");
                ((LeafID) ((ExprNode) exprNode.value2).value1).accept(this);
                writer.print(")");
            }
            else if(((LeafID) ((ExprNode) exprNode.value1).value1).getType().equals(ValueType.Bool) && ((LeafID) ((ExprNode) exprNode.value2).value1).getType().equals(ValueType.Bool)){
                writer.print("bool_to_string(");
                ((LeafID) ((ExprNode) exprNode.value1).value1).accept(this);
                writer.print(")");
                writer.print(",");
                writer.print("bool_to_string(");
                ((LeafID) ((ExprNode) exprNode.value2).value1).accept(this);
                writer.print(")");
            }
            else if(((LeafID) ((ExprNode) exprNode.value1).value1).getType().equals(ValueType.Bool) && ((LeafID) ((ExprNode) exprNode.value2).value1).getType().equals(ValueType.Real)){
                writer.print("bool_to_string(");
                ((LeafID) ((ExprNode) exprNode.value1).value1).accept(this);
                writer.print(")");
                writer.print(",");
                writer.print("double_to_string(");
                ((LeafID) ((ExprNode) exprNode.value2).value1).accept(this);
                writer.print(")");
            }
        }
        //Concat tutti i tipi di const con tutti i tipi di const
        else if(((ExprNode) exprNode.value1).value1 instanceof LeafStringConst && ((ExprNode) exprNode.value2).value1 instanceof LeafStringConst){
            ((LeafStringConst) ((ExprNode) exprNode.value1).value1).accept(this);
            writer.print(",");
            ((LeafStringConst) ((ExprNode) exprNode.value2).value1).accept(this);
        }
        else if(((ExprNode) exprNode.value1).value1 instanceof LeafStringConst && ((ExprNode) exprNode.value2).value1 instanceof LeafIntegerConst){
            ((LeafStringConst) ((ExprNode) exprNode.value1).value1).accept(this);
            writer.print(",");
            writer.print("int_to_string(");
            ((LeafStringConst) ((ExprNode) exprNode.value2).value1).accept(this);
            writer.print(")");
        }
        else if(((ExprNode) exprNode.value1).value1 instanceof LeafStringConst && ((ExprNode) exprNode.value2).value1 instanceof LeafRealConst){
            ((LeafStringConst) ((ExprNode) exprNode.value1).value1).accept(this);
            writer.print(",");
            writer.print("double_to_string(");
            ((LeafStringConst) ((ExprNode) exprNode.value2).value1).accept(this);
            writer.print(")");
        }
        else if(((ExprNode) exprNode.value1).value1 instanceof LeafStringConst && ((ExprNode) exprNode.value2).value1 instanceof LeafBool){
            ((LeafStringConst) ((ExprNode) exprNode.value1).value1).accept(this);
            writer.print(",");
            writer.print("bool_to_string(");
            ((LeafStringConst) ((ExprNode) exprNode.value2).value1).accept(this);
            writer.print(")");
        }
        else if(((ExprNode) exprNode.value1).value1 instanceof LeafIntegerConst && ((ExprNode) exprNode.value2).value1 instanceof LeafStringConst){
            writer.print("int_to_string(");
            ((LeafStringConst) ((ExprNode) exprNode.value1).value1).accept(this);
            writer.print(",");
            ((LeafStringConst) ((ExprNode) exprNode.value2).value1).accept(this);
        }
        else if(((ExprNode) exprNode.value1).value1 instanceof LeafIntegerConst && ((ExprNode) exprNode.value2).value1 instanceof LeafIntegerConst){
            writer.print("int_to_string(");
            ((LeafIntegerConst) ((ExprNode) exprNode.value1).value1).accept(this);
            writer.print(",");
            writer.print("int_to_string(");
            ((LeafIntegerConst) ((ExprNode) exprNode.value2).value1).accept(this);
            writer.print(")");
        }
        else if(((ExprNode) exprNode.value1).value1 instanceof LeafIntegerConst && ((ExprNode) exprNode.value2).value1 instanceof LeafRealConst){
            writer.print("int_to_string(");
            ((LeafStringConst) ((ExprNode) exprNode.value1).value1).accept(this);
            writer.print(",");
            writer.print("double_to_string(");
            ((LeafStringConst) ((ExprNode) exprNode.value2).value1).accept(this);
            writer.print(")");
        }
        else if(((ExprNode) exprNode.value1).value1 instanceof LeafIntegerConst && ((ExprNode) exprNode.value2).value1 instanceof LeafBool){
            writer.print("int_to_string(");
            ((LeafStringConst) ((ExprNode) exprNode.value1).value1).accept(this);
            writer.print(",");
            writer.print("bool_to_string(");
            ((LeafStringConst) ((ExprNode) exprNode.value2).value1).accept(this);
            writer.print(")");
        }
        else if(((ExprNode) exprNode.value1).value1 instanceof LeafRealConst && ((ExprNode) exprNode.value2).value1 instanceof LeafIntegerConst){
            writer.print("double_to_string(");
            ((LeafStringConst) ((ExprNode) exprNode.value1).value1).accept(this);
            writer.print(",");
            writer.print("int_to_string(");
            ((LeafStringConst) ((ExprNode) exprNode.value2).value1).accept(this);
            writer.print(")");
        }
        else if(((ExprNode) exprNode.value1).value1 instanceof LeafRealConst && ((ExprNode) exprNode.value2).value1 instanceof LeafStringConst){
            writer.print("double_to_string(");
            ((LeafStringConst) ((ExprNode) exprNode.value1).value1).accept(this);
            writer.print(",");
            ((LeafStringConst) ((ExprNode) exprNode.value2).value1).accept(this);
        }
        else if(((ExprNode) exprNode.value1).value1 instanceof LeafRealConst && ((ExprNode) exprNode.value2).value1 instanceof LeafRealConst){
            writer.print("double_to_string(");
            ((LeafStringConst) ((ExprNode) exprNode.value1).value1).accept(this);
            writer.print(",");
            writer.print("double_to_string(");
            ((LeafStringConst) ((ExprNode) exprNode.value2).value1).accept(this);
            writer.print(")");
        }
        else if(((ExprNode) exprNode.value1).value1 instanceof LeafRealConst && ((ExprNode) exprNode.value2).value1 instanceof LeafBool){
            writer.print("double_to_string(");
            ((LeafStringConst) ((ExprNode) exprNode.value1).value1).accept(this);
            writer.print(",");
            writer.print("bool_to_string(");
            ((LeafStringConst) ((ExprNode) exprNode.value2).value1).accept(this);
            writer.print(")");
        }
        else if(((ExprNode) exprNode.value1).value1 instanceof LeafBool && ((ExprNode) exprNode.value2).value1 instanceof LeafIntegerConst){
            writer.print("bool_to_string(");
            ((LeafStringConst) ((ExprNode) exprNode.value1).value1).accept(this);
            writer.print(",");
            writer.print("int_to_string(");
            ((LeafStringConst) ((ExprNode) exprNode.value2).value1).accept(this);
            writer.print(")");
        }
        else if(((ExprNode) exprNode.value1).value1 instanceof LeafBool && ((ExprNode) exprNode.value2).value1 instanceof LeafRealConst){
            writer.print("bool_to_string(");
            ((LeafStringConst) ((ExprNode) exprNode.value1).value1).accept(this);
            writer.print(",");
            writer.print("double_to_string(");
            ((LeafStringConst) ((ExprNode) exprNode.value2).value1).accept(this);
            writer.print(")");
        }
        else if(((ExprNode) exprNode.value1).value1 instanceof LeafBool && ((ExprNode) exprNode.value2).value1 instanceof LeafStringConst){
            writer.print("bool_to_string(");
            ((LeafStringConst) ((ExprNode) exprNode.value1).value1).accept(this);
            writer.print(",");
            ((LeafStringConst) ((ExprNode) exprNode.value2).value1).accept(this);
        }
        else if(((ExprNode) exprNode.value1).value1 instanceof LeafBool && ((ExprNode) exprNode.value2).value1 instanceof LeafBool){
            writer.print("bool_to_string(");
            ((LeafStringConst) ((ExprNode) exprNode.value1).value1).accept(this);
            writer.print(",");
            writer.print("bool_to_string(");
            ((LeafStringConst) ((ExprNode) exprNode.value2).value1).accept(this);
            writer.print(")");
        }
        */
}
