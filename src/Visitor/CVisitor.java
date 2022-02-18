package Visitor;
import nodes.*;
import leafs.*;
import support.SymbolTable;
import support.ValueType;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;


public class CVisitor implements ICVisitor {

    private PrintWriter writer;
    private String actualFunName = "";
    private String actualName = "";
    ArrayList<String> outParName = null;
    public static String FILE_NAME = "c_gen.c";
    private static File FILE;

    public CVisitor() throws IOException {
        /*int lastIndex = name.lastIndexOf(File.separator);

        File file = new File("test_files" + File.separator + "c_out" + File.separator + name.substring(lastIndex, name.length() - 4) + ".c");
        if (file.exists()) {
            file.delete();
        }
        file.createNewFile();
        System.out.print("File " + file.getName() + " creato nella cartella \"test_files" + File.separator + "c_out\" !!!");
        */
        // Inizializzo il file di scrittura
        if (!(new File("test_files" + File.separator + "c_out" + File.separator)).exists()) {
            Files.createDirectory(Paths.get("test_files" + File.separator + "c_out" + File.separator));
        }
        FILE = new File("test_files" + File.separator + "c_out" + File.separator + FILE_NAME);
        FILE.createNewFile();

        writer = new PrintWriter(FILE);
    }


    public void visit(ProgramNode node) {
        writer.println("#include <stdio.h>");
        writer.println("#include <stdlib.h>");
        writer.println("#include <stdbool.h>");
        writer.println("#include <math.h>");
        writer.println("#include <unistd.h>");
        writer.println("#include <string.h>");
        writer.println("#define MAXCHAR 512\n");

        writer.println("\n// Funzioni di concatenazione");
        writer.print("char *IntConcat(char *string, int toConcat) {\n");
        writer.print("int length = snprintf(NULL, 0,\"%d\", toConcat);\n");//ritorna il numero di caratteri scritti
        writer.print("char *converted = (char *) malloc(length + 1);\n");
        writer.print("sprintf(converted, \"%d\", toConcat);\n");
        writer.print("char *concat = (char *) malloc(1 + strlen(string)+ strlen(converted));\n");
        writer.print("strcpy(concat, string);\n");
        writer.print("strcat(concat, converted);\n");
        writer.print("return concat;\n}\n");

        writer.print("char *DoubleConcat(char *string, float toConcat) {\n");
        writer.print("int length = snprintf(NULL, 0,\"%f\", toConcat);\n");
        writer.print("char *converted = (char *) malloc(length + 1);\n");
        writer.print("sprintf(converted, \"%f\", toConcat);\n");
        writer.print("char *concat = (char *) malloc(1 + strlen(string)+ strlen(converted));\n");
        writer.print("strcpy(concat, string);\n");
        writer.print("strcat(concat, converted);\n");
        writer.print("return concat;\n}\n");

        writer.print("char *BoolConcat(char *string, int toConcat) {\n");
        writer.print("char *converted = (char *) malloc(6);\n");
        writer.print("if(toConcat == 1) strcpy(converted, \"true\");\n");
        writer.print("else strcpy(converted, \"false\");\n");
        writer.print("char *concat = (char *) malloc(1 + strlen(string)+ strlen(converted));\n");
        writer.print("strcpy(concat, string);\n");
        writer.print("strcat(concat, converted);\n");
        writer.print("return concat;\n}\n");

        writer.print("char *StringConcat(char *string, char *toConcat) {\n");
        writer.print("char *concat = (char *) malloc(1 + strlen(string)+ strlen(toConcat));\n");
        writer.print("strcpy(concat, string);\n");
        writer.print("strcat(concat, toConcat);\n");
        writer.print("return concat;\n}\n");
        writer.print("\n");

        //ArrayList<VarDeclNode>
        if (node.nodeArrayList != null) {
            if (node.nodeArrayList.size() != 0) {
                writer.println("//Dichiarazione variabili globali");
            }
            for (VarDeclNode declNodo : node.nodeArrayList) {
                declNodo.accept(this);
            }
        }

        //ArrayList<FunNode>
        for (FunNode funNode : node.funNodeArrayList) {
            funNode.accept(this);
        }

        //BodyNode
        writer.println("int main () {\n");
        node.bodyNode.accept(this);
        writer.println("return 0;\n");
        writer.println("}");
        writer.close();

    }

    public void visit(BodyNode node) {

        //ArrayList<VarDeclNode>
        if (node.vardecl != null) {
            for (VarDeclNode varDeclNode : node.vardecl) {
                varDeclNode.accept(this);
            }
        }

        //StatListNode
        node.stats.accept(this);
    }

    public void visit(VarDeclNode node) {


        //TypeNode
        //node.type.accept(this);

        //ArrayList<IdInitNode>
        for (IdInitNode idInitNode : node.identifiers) {

            node.setType(idInitNode.type.toString());

            //TypeNode
            node.type.accept(this);

            //IdInitNode
            idInitNode.accept(this);

        }

    }

    public void visit(TypeNode node) {
        if (SymbolTable.StringToType(node.type) == ValueType.Integer) {
            writer.print("int ");
        } else if (SymbolTable.StringToType(node.type) == ValueType.Real) {
            writer.print("double ");
        } else if (SymbolTable.StringToType(node.type) == ValueType.String) {
            writer.print("char* ");
        } else if (SymbolTable.StringToType(node.type) == ValueType.Bool) {
            writer.print("bool ");
        }
    }

    public void visit(IdInitNode node) {

        //LeafID
        node.leaf.accept(this);

        if (node.type == ValueType.String) {
            writer.print(" = malloc(sizeof(char) * MAXCHAR)");
        }


        //ExprNode
        if (node.expr != null) {
            if (node.type == ValueType.String) {
                writer.print(";\n");
                writer.print("strcpy(");
                node.leaf.accept(this);
                writer.print(", ");
                node.expr.accept(this);
                writer.print(")");
            } else {
                writer.print(" = ");
                node.expr.accept(this);
            }
        }
        //ConstNode (VAR)
        if (node.c != null) {
            if (node.type == ValueType.String) {
                writer.print(";\n");
                writer.print("strcpy(");
                node.leaf.accept(this);
                writer.print(", ");
                node.c.accept(this);
                writer.print(")");
            } else {
                writer.print(" = ");
                node.c.accept(this);
            }
        }

        writer.print(";\n");
    }

    public void visit(ConstNode constNode) {

        if (constNode.value1 instanceof LeafIntegerConst) {
            ((LeafIntegerConst) constNode.value1).accept(this);
        } else if (constNode.value1 instanceof LeafStringConst) {
            ((LeafStringConst) constNode.value1).accept(this);
        } else if (constNode.value1 instanceof LeafRealConst) {
            ((LeafRealConst) constNode.value1).accept(this);
        } else {
            ((LeafBool) constNode.value1).accept(this);
            //constNode.setType(((LeafBool) constNode.value1).type);
        }
    }

    public void visit(FunNode node) {

        writer.println("\n//Fun " + node.leaf.value);

        this.outParName = new ArrayList<>();

        if (node.getTypeNode() != null) {

            node.typeNode.accept(this);
        } else {
            writer.print("void ");
        }

        node.leaf.accept(this);

        writer.print("(");

        if (node.pardecl.size() != 0) {
            for (int i = 0; i < node.pardecl.size(); i++) {
                ParDeclNode parDeclNode = node.pardecl.get(i);
                parDeclNode.accept(this);
                if (i != node.pardecl.size() - 1) {
                    writer.print(", ");
                }
            }
        }
        writer.print(") {\n");

        node.bodyNode.accept(this);

        this.outParName = new ArrayList<String>();

        writer.println("}\n\n");


    }

    public void visit(ParDeclNode node) {

        //Mi prendo il tipo del parametro
        node.typeNode.accept(this);

        //node.mod.accept(this);
        if (node.mod.mod.equals("out")) {
            this.outParName.add(node.leaf.value);
            //writer.print("*");
        }

        //Mi prendo il nome del parametro
        node.leaf.accept(this);

    }

    public void visit(ModeParNode node) {

    }

    public void visit(StatListNode nodeList) {
        for (StatNode statNode : nodeList.statList) {
            switch (statNode.getClass().getSimpleName()) {
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
                    //Aggiunta per gestire il caso isolato di chiamata di funzione
                    writer.print(";\n");
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
        if (node.elsebody != null) {
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

        if (node.expr != null) {
            writer.print("printf(\"%s\", ");
            node.expr.accept(this);
            writer.println(");");
        }

        for (LeafID leafID : node.idlist) {
            if (leafID.getType().equals(ValueType.String)) {
                writer.print("scanf(\"%s\", ");
                leafID.accept(this);
                writer.println(");");
            } else if (leafID.getType().equals(ValueType.Bool) || leafID.getType().equals(ValueType.Integer)) {
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
    }

    public void visit(WriteStatNode node) {
        if (node.expr.type.equals(ValueType.Integer) || node.expr.type.equals(ValueType.Bool)) {
            writer.print("printf(\"%d\\n\", ");
        }
        if (node.expr.type.equals(ValueType.String)) {
            writer.print("printf(\"%s\\n\", ");
        }
        if (node.expr.type.equals(ValueType.Real)) {
            writer.print("printf(\"%f\\n\", ");
        }
        node.expr.accept(this);
        writer.print(");\n");
        //Case ?.
        if (node.node.name.equals("writeln")) {
            writer.println("printf(\"\\n\");");
        }
        //Case ?,
        else if (node.node.name.equals("writeb")) {
            writer.println("printf(\" \");");
        }
        //Case ?:
        else if (node.node.name.equals("writet")) {
            writer.println("printf(\"\\t\");");
        }
        //Case ? (Non faccio nulla perchè è una stampa normale)

    }

    public void visit(ReturnStatNode node) {
        writer.print("return ");
        //ExprNode di ritorno
        node.expr.accept(this);
        writer.print(";\n");
    }


    public void visit(AssignStatNode node) {

        //Gestisco il nome della variabile a cui assegnare l'espressione
        node.leaf.accept(this);
        writer.print(" = ");
        //Gestisco l'espressione
        node.expr.accept(this);
        writer.print(";\n");
    }

    public void visit(CallFunNode node) {

        node.leafID.accept(this);

        if (node.exprList != null && node.exprList.size() > 0) {
            writer.print("(");
            ExprNode lastExprNode = node.exprList.get(node.exprList.size() - 1);
            for (ExprNode exprNode : node.exprList) {
                if (exprNode.mod.mod.equalsIgnoreCase("out")) {
                    writer.print("&");
                    exprNode.leaf.accept(this);
                }
                exprNode.accept(this);
                if (exprNode != lastExprNode)
                    writer.print(", ");
            }
            writer.print(")");
        } else {
            writer.print("()");
        }

    }

    public void visit(ExprNode exprNode) {
        if (exprNode.value1 != null && exprNode.value2 != null) {

            switch (exprNode.name) {
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
                    ((ExprNode) exprNode.value1).accept(this);
                    writer.print(" > ");
                    ((ExprNode) exprNode.value2).accept(this);
                    break;
                case "GEOp":
                    ((ExprNode) exprNode.value1).accept(this);
                    writer.print(" >= ");
                    ((ExprNode) exprNode.value2).accept(this);
                    break;
                case "LTOp":
                    ((ExprNode) exprNode.value1).accept(this);
                    writer.print(" < ");
                    ((ExprNode) exprNode.value2).accept(this);
                    break;
                case "LEOp":
                    ((ExprNode) exprNode.value1).accept(this);
                    writer.print(" <= ");
                    ((ExprNode) exprNode.value2).accept(this);
                    break;
                case "EQOp":
                    if (((ExprNode) exprNode.value1).type == ValueType.String) {
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
                    if (((ExprNode) exprNode.value1).type == ValueType.String) {
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
                    if (((ExprNode) exprNode.value2).type == ValueType.Integer) {
                        writer.print("IntConcat( ");
                        ((ExprNode) exprNode.value1).accept(this);
                        writer.print(", ");
                        ((ExprNode) exprNode.value2).accept(this);
                        writer.print(")");
                    }
                    if (((ExprNode) exprNode.value2).type == ValueType.Real) {
                        writer.print("DoubleConcat( ");
                        ((ExprNode) exprNode.value1).accept(this);
                        writer.print(", ");
                        ((ExprNode) exprNode.value2).accept(this);
                        writer.print(")");
                    }
                    if (((ExprNode) exprNode.value2).type == ValueType.Bool) {
                        writer.print("BoolConcat( ");
                        ((ExprNode) exprNode.value1).accept(this);
                        writer.print(", ");
                        ((ExprNode) exprNode.value2).accept(this);
                        writer.print(")");
                    }
                    if (((ExprNode) exprNode.value2).type == ValueType.String) {
                        writer.print("StringConcat( ");
                        ((ExprNode) exprNode.value1).accept(this);
                        writer.print(", ");
                        ((ExprNode) exprNode.value2).accept(this);
                        writer.print(")");
                    }
                    break;
            }
        } else if (exprNode.value1 != null) {
            if (exprNode.name.equalsIgnoreCase("UminusOp")) {
                writer.print("-");
                ((ExprNode) exprNode.value1).accept(this);
            } else if (exprNode.name.equalsIgnoreCase("NotOp")) {
                writer.print("!");
                ((ExprNode) exprNode.value1).accept(this);
            } else if (exprNode.value1 instanceof LeafIntegerConst) {
                ((LeafIntegerConst) exprNode.value1).accept(this);
            } else if (exprNode.value1 instanceof LeafRealConst) {
                ((LeafRealConst) exprNode.value1).accept(this);
            } else if (exprNode.value1 instanceof LeafStringConst) {
                ((LeafStringConst) exprNode.value1).accept(this);
            } else if (exprNode.value1 instanceof LeafBool) {
                ((LeafBool) exprNode.value1).accept(this);
            } else if (exprNode.value1 instanceof LeafID) {
                ((LeafID) exprNode.value1).accept(this);
            } else if (exprNode.value1 instanceof CallFunNode) {
                ((CallFunNode) exprNode.value1).accept(this);
            } else if (exprNode.value1 instanceof ExprNode) {
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

    public void visit(LeafID leaf) {
        if (this.outParName != null) {
            if (this.outParName.contains(leaf.value)) {
                writer.print("*");
            }
        }
        writer.print(leaf.value);
    }

    public void visit(LeafIntegerConst leaf) {
        writer.print(leaf.value);
    }

    public void visit(LeafStringConst leaf) {

        if (leaf.value.length() != 0) {
            writer.print("\"" + leaf.value + "\"");
        } else {
            writer.print("\"\"");
        }

    }

}
