#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>
#include <math.h>
#include <unistd.h>
#include <string.h>
#define MAXCHAR 512


// Funzioni di concatenazione
char *IntConcat(char *string, int toConcat) {
int length = snprintf(NULL, 0,"%d", toConcat);
char *converted = (char *) malloc(length + 1);
sprintf(converted, "%d", toConcat);
char *concat = (char *) malloc(1 + strlen(string)+ strlen(converted));
strcpy(concat, string);
strcat(concat, converted);
return concat;
}
char *DoubleConcat(char *string, float toConcat) {
int length = snprintf(NULL, 0,"%f", toConcat);
char *converted = (char *) malloc(length + 1);
sprintf(converted, "%f", toConcat);
char *concat = (char *) malloc(1 + strlen(string)+ strlen(converted));
strcpy(concat, string);
strcat(concat, converted);
return concat;
}
char *BoolConcat(char *string, int toConcat) {
char *converted = (char *) malloc(6);
if(toConcat == 1) strcpy(converted, "true");
else strcpy(converted, "false");
char *concat = (char *) malloc(1 + strlen(string)+ strlen(converted));
strcpy(concat, string);
strcat(concat, converted);
return concat;
}
char *StringConcat(char *string, char *toConcat) {
char *concat = (char *) malloc(1 + strlen(string)+ strlen(toConcat));
strcpy(concat, string);
strcat(concat, toConcat);
return concat;
}



//Fun print_menu
int print_menu() {
int choose;
printf("%s\n", "Scegli l'operazione da svolgere per continuare");
printf("\n");
printf("%s\n", "\t(1) Somma di due numeri");
printf("\n");
printf("%s\n", "\t(2) Moltiplicazione di due numeri");
printf("\n");
printf("%s\n", "\t(3) Divisione intera fra due numeri positivi");
printf("\n");
printf("%s\n", "\t(4) Elevamento a potenza");
printf("\n");
printf("%s\n", "\t(5) Successione di Fibonacci (ricorsiva)");
printf("\n");
printf("%s\n", "\t(6) Successione di Fibonacci (iterativa)");
printf("\n");
printf("%s\n", "\t(0) Esci");
printf("\n");
printf("%s", "--> ");
scanf("%d", &choose);
return choose;
}



//Fun do_sum
void do_sum() {
double op1;
double op2;
printf("%s\n", "\n(1) SOMMA\n");
printf("\n");
printf("%s", "Inserisci il primo operando: ");
scanf("%lf", &op1);
printf("%s", "Inserisci il secondo operando: ");
scanf("%lf", &op2);
printf("%s\n", "");
printf("\n");
printf("%s\n", DoubleConcat( StringConcat( DoubleConcat( StringConcat( DoubleConcat( "La somma tra ", op1), " e "), op2), " vale "), op1 + op2));
printf("\n");
}



//Fun do_mul
void do_mul() {
double op1;
double op2;
printf("%s\n", "\n(2) MOLTIPLICAZIONE");
printf("\n");
printf("%s", "\nInserisci il primo operando: ");
scanf("%lf", &op1);
printf("%s", "Inserisci il secondo operando: ");
scanf("%lf", &op2);
printf("%s\n", "");
printf("\n");
printf("%s\n", DoubleConcat( StringConcat( DoubleConcat( StringConcat( DoubleConcat( "La moltiplicazione tra ", op1), " e "), op2), " vale "), op1 * op2));
printf("\n");
}



//Fun do_div_int
void do_div_int() {
int op1;
int op2;
printf("%s\n", "\n(3) DIVISIONE INTERA");
printf("\n");
printf("%s", "\nInserisci il primo operando: ");
scanf("%d", &op1);
printf("%s", "Inserisci il secondo operando: ");
scanf("%d", &op2);
printf("%s\n", "");
printf("\n");
printf("%s\n", IntConcat( StringConcat( IntConcat( StringConcat( IntConcat( "La divisione intera tra ", op1), " e "), op2), " vale "), op1 / op2));
printf("\n");
}



//Fun do_pow
void do_pow() {
double op1;
double op2;
printf("%s\n", "\n(4) POTENZA");
printf("\n");
printf("%s", "\nInserisci la base: ");
scanf("%lf", &op1);
printf("%s", "Inserisci l'esponente: ");
scanf("%lf", &op2);
printf("%s\n", "");
printf("\n");
printf("%s\n", DoubleConcat( StringConcat( DoubleConcat( StringConcat( DoubleConcat( "La potenza di ", op1), " elevato a "), op2), " vale "),  pow(op1,op2)));
printf("\n");
}



//Fun recursive_fib
int recursive_fib(int n) {
if (n == 1) {
return 0;
}
if (n == 2) {
return 1;
}
return recursive_fib(n - 1) + recursive_fib(n - 2);
}



//Fun iterative_fib
int iterative_fib(int n) {
if (n == 1) {
return 0;
}
if (n == 2) {
return 1;
}
if (n > 2) {
int i = 3;
int res = 1;
int prev = 0;
while (i <= n) {
int tmp = res;
res = res + prev;
prev = tmp;
i = i + 1;
}
return res;
}
return -1;
}



//Fun do_fib
void do_fib(bool recursive) {
int n;
char* message = malloc(sizeof(char) * MAXCHAR);
printf("%s\n", "\n(5) FIBONACCI");
printf("\n");
printf("%s", "\nInserisci n: ");
scanf("%d", &n);
printf("%s\n", "");
printf("\n");
message = StringConcat( IntConcat( "Il numero di Fibonacci in posizione ", n), " vale ");
if (recursive) {
message = IntConcat( message, recursive_fib(n));
}
else {
message = IntConcat( message, iterative_fib(n));
}
printf("%s\n", message);
printf("\n");
}



//Fun do_operation
void do_operation(int choose) {
if (choose == 1) {
do_sum();
}
else {
if (choose == 2) {
do_mul();
}
else {
if (choose == 3) {
do_div_int();
}
else {
if (choose == 4) {
do_pow();
}
else {
if (choose == 5) {
do_fib(true);
}
else {
if (choose == 6) {
do_fib(false);
}
}
}
}
}
}
}



//Fun print_continue
void print_continue(bool *flagContinue) {
char* in = malloc(sizeof(char) * MAXCHAR);
printf("%s", "Vuoi continuare? (s/n) --> ");
scanf("%s", in);
if (strcmp(in, "s") == 0) {
*flagContinue = true;
}
else {
*flagContinue = false;
}
}


int main () {

int choose = 0;
bool flagContinue = true;
while (flagContinue) {
choose = print_menu();
if (choose == 0) {
flagContinue = false;
}
else {
do_operation(choose);
print_continue(&flagContinue);
}
}
return 0;

}
