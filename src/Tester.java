import Visitor.CVisitor;
import Visitor.SemanticVisitor;
import Visitor.SyntaxVisitor;
import java_cup.runtime.Symbol;
import nodes.ProgramNode;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;


public class Tester {
    public static void main(String[] args) throws Exception {

        Path path = Paths.get(args[0]);
        //System.out.println(path.toString());
        FileReader rd = new FileReader(args[0]);
        Path filename = path.getFileName();
        //System.out.println(filename.toString());
        Lexer l = new Lexer(rd);
        parser p = new parser(l);
        Symbol s = p.parse(); // l'uso di p.debug_parse() al posto di p.parse() produce tutte le azioni del parser durante il riconoscimento
        ProgramNode programNode = (ProgramNode) s.value;
        SyntaxVisitor syntaxVisitor = new SyntaxVisitor();
        syntaxVisitor.visit(programNode);
        syntaxVisitor.saveXML(filename.toString());
        System.out.println("\nSyntax tree generated.");
        SemanticVisitor semanticVisitor = new SemanticVisitor();
        semanticVisitor.visit(programNode);
        System.out.println("\nSemantic analysis completed.");
        //System.out.println("\nGenerating C code...");
        CVisitor.FILE_NAME = filename.toString().substring(0,filename.toString().lastIndexOf('.')) + ".c";
        CVisitor cVisitor = new CVisitor();
        cVisitor.visit(programNode);
        System.out.println("\nC code generated.");
    }
}

