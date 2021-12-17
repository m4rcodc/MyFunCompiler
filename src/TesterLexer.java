import java_cup.runtime.Symbol;

import java.io.FileReader;

/**
 * Simple test driver for the java lexer. Just runs it on some input files and produces debug
 * output. Needs symbol class from parser.
 */
public class TesterLexer {

    /** some numerals to for lexer testing */

    public static void main(String[] argv) {

            try {
                //String filePath = argv[0];
                String filePath = "test_files/test1.txt";

                FileReader rd=new FileReader(filePath);

                Symbol s;

                Lexer l = new Lexer(rd);
                do {
                    s = l.next_token();
                    if(s.value == null){
                        System.out.println("token: " + s );
                    }
                    else{
                        System.out.println("token: " + s + " attribute: " + s.value);
                    }
                } while (!(s.sym == sym.EOF || s.sym == sym.error));
            } catch (Exception e) {
                e.printStackTrace(System.out);
                System.exit(1);
            }
        }
    }
