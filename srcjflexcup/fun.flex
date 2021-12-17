// Tester.flex
//
// CS2A Language Processing
//
// Description of lexer for circuit description language.
//C:\JFLEX\bin\jflex -d src srcjflexcup\fun.flex
// Ian Stark
import java_cup.runtime.*; //This is how we pass tokens to the parser
%%
%class Lexer									// Declarations for JFlex
%unicode 								// We wish to read text files
%cupsym sym
%cup 									// Declare that we expect to use Java CUP
%line
%column

%{
StringBuffer string = new StringBuffer();

%}

// Abbreviations for regular expressions
LineTerminator= \r|\n|\r\n|\n
WhiteSpace= {LineTerminator} | [ \t\f]
digit = [0-9]
number = {digit}+
Integer_Const = {number}
Real_Const = [+-]?{number}(\.{number})?
Identifier = [$_A-Za-z][$_@A-Za-z0-9]*
StringInit = \"|\'
CommentInit = \#|\/\/
CommentBlock = \#\*



%state STRING
%state COMMENT
%state COMMENTBLOCK

%%

<YYINITIAL> {

"main" {return new Symbol(sym.MAIN);}
"integer" {return new Symbol(sym.INTEGER);}
"string" {return new Symbol(sym.STRING);}
"real" {return new Symbol(sym.REAL);}
"bool" {return new Symbol(sym.BOOL);}
"var" {return new Symbol(sym.VAR);}
"(" {return new Symbol(sym.LPAR);}
")" {return new Symbol(sym.RPAR);}
":" {return new Symbol(sym.COLON);}
"fun" {return new Symbol(sym.FUN);}
"end" {return new Symbol(sym.END);}
"if" {return new Symbol(sym.IF);}
"then" {return new Symbol(sym.THEN);}
"else" {return new Symbol(sym.ELSE);}
"while" {return new Symbol(sym.WHILE);}
"loop" {return new Symbol(sym.LOOP);}//
"%" {return new Symbol(sym.READ);}
"?" {return new Symbol(sym.WRITE);}
"?." {return new Symbol(sym.WRITELN);}
"?," {return new Symbol(sym.WRITEB);}
"?:" {return new Symbol(sym.WRITET);}
":=" {return new Symbol(sym.ASSIGN);}
"+" {return new Symbol(sym.PLUS);}
"-" {return new Symbol(sym.MINUS);}
"*" {return new Symbol(sym.TIMES);}
"div" {return new Symbol(sym.DIVINT);}
"/"  {return new Symbol(sym.DIV);}
"^"  {return new Symbol(sym.POW);}
"&"  {return new Symbol(sym.STR_CONCAT);}
"="  {return new Symbol(sym.EQ);}
"!=" {return new Symbol(sym.NE);}
"<"  {return new Symbol(sym.LT);}
"<=" {return new Symbol(sym.LE);}
">" {return new Symbol(sym.GT);}
">=" {return new Symbol(sym.GE);}
"and" {return new Symbol(sym.AND);}
"or" {return new Symbol(sym.OR);}
"not" {return new Symbol(sym.NOT);}
"true" {return new Symbol(sym.TRUE);}
"false" {return new Symbol(sym.FALSE);}
";" {return new Symbol(sym.SEMI);}
"," {return new Symbol(sym.COMMA);}
"return" {return new Symbol(sym.RETURN);}
"@" {return new Symbol(sym.OUTPAR);}

/*Whitespace*/
{WhiteSpace} {/* empty */}

/*IntegerConst*/
{Integer_Const} { return new Symbol(sym.INTEGER_CONST, Integer.parseInt(yytext())); }

/*RealConst*/
{Real_Const} { return new Symbol(sym.REAL_CONST, Float.parseFloat(yytext())); }

/*StartString*/
{StringInit} { string.setLength(0); yybegin(STRING); }

/*Identifier*/
{Identifier} { return new Symbol(sym.ID, yytext()); }

/*StartComment*/
{CommentInit} { string.setLength(0); yybegin(COMMENT); }

/*StartCommentBlock*/
{CommentBlock} { string.setLength(0); yybegin(COMMENTBLOCK); }


}

<STRING>  {
/*String case*/

\" { yybegin(YYINITIAL); return new Symbol(sym.STRING_CONST,string.toString());}

\' { yybegin(YYINITIAL); return new Symbol(sym.STRING_CONST,string.toString());}

[^\n\r\"\']  { string.append(yytext());}
[\n\r]       { /* nothing */ }
<<EOF>> { return new Symbol(sym.EOF,"Stringa costante non completata"); }

}

<COMMENT>   {

[^\r\n] { }
[\r\n] {yybegin(YYINITIAL);}

}

<COMMENTBLOCK> {

[^\#] { }
\# {yybegin(YYINITIAL);}

<<EOF>> { return new Symbol(sym.EOF,"Commento non completato");}

}

/* error fallback */
[^] {return new Symbol(sym.error,"Illegal character");}



