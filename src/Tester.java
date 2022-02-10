// Circuit.java


import Visitor.CVisitor;
import Visitor.SemanticVisitor;
import Visitor.SyntaxVisitor;
import java_cup.runtime.Symbol;
import nodes.ProgramNode;

import java.io.FileReader;


public class Tester {
    public static void main(String[] args) throws Exception {
        String filePath = "test_files/somma.mf.txt";
        FileReader rd=new FileReader(filePath);
        Lexer l = new Lexer(rd);
        parser p = new parser(l);
        System.out.println("Sono qui");
        Symbol s = p.debug_parse(); // l'uso di p.debug_parse() al posto di p.parse() produce tutte le azioni del parser durante il riconoscimento
        System.out.println("Sono qui 1");
        ProgramNode programNode = (ProgramNode) s.value;
        SyntaxVisitor syntaxVisitor = new SyntaxVisitor();
        syntaxVisitor.visit(programNode);
        String filePathXml = "test_files/prova.xml";
        syntaxVisitor.saveXML(filePathXml);
        SemanticVisitor semanticVisitor = new SemanticVisitor();
        semanticVisitor.visit(programNode);
        System.out.println("Semantic analysis completed.");
        System.out.println("\nGenerating C code...");
        CVisitor cVisitor = new CVisitor(filePath);
        cVisitor.visit(programNode);
        System.out.println("\nC code generated");

    }
}

// End of file
