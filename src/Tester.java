// Circuit.java


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
        Symbol s = p.parse(); // l'uso di p.debug_parse() al posto di p.parse() produce tutte le azioni del parser durante il riconoscimento
        ProgramNode programNode = (ProgramNode) s.value;
        SyntaxVisitor syntaxVisitor = new SyntaxVisitor();
        syntaxVisitor.visit(programNode);
        String filePathXml = "test_files/prova.xml";
        syntaxVisitor.saveXML(filePathXml);

    }
}
// End of file
