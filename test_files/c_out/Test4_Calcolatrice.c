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

//Dichiarazione variabili globali
bool lp = true;

//Fun menu
void menu() {
printf("%s\n", "Benvenuto in questo programma, quale operazione vuoi svolgere ?");
printf("\n");
printf("%s\n", "1: Somma");
printf("\n");
printf("%s\n", "2: Sottrazione");
printf("\n");
printf("%s\n", "3: Divisione");
printf("\n");
printf("%s\n", "4: Moltiplicazione");
printf("\n");
printf("%s\n", "5: Exit");
printf("\n");
}



//Fun sum
double sum(int a, double b) {
double result;
result = a + b;
return result;
}



//Fun sub
double sub(int a, double b) {
double result;
result = a - b;
return result;
}



//Fun divid
double divid(int a, double b) {
double result;
result = a / b;
return result;
}



//Fun mol
double mol(int a, double b) {
double result;
result = a * b;
return result;
}


int main () {

int x;
double y;
int ans = 0;
double result;
while (lp == true) {
menu();
printf("%s\n", "Fai la tua scelta:");
printf("\t");
scanf("%d", &ans);
if (ans == 1) {
printf("%s", "Inserisci primo numero da sommare:");
scanf("%d", &x);
printf("%s", "Inserisci secondo numero da sommare:");
scanf("%lf", &y);
result = sum(x, y);
printf("%s\n", StringConcat( DoubleConcat( "Il risultato è: ", result), "."));
printf("\n");
}
if (ans == 2) {
printf("%s", "Inserisci primo numero:");
scanf("%d", &x);
printf("%s", "Inserisci numero da sottrarre al primo:");
scanf("%lf", &y);
result = sub(x, y);
printf("%s\n", StringConcat( DoubleConcat( "Il risultato è: ", result), "."));
printf("\n");
}
if (ans == 3) {
printf("%s", "Inserisci primo numero da dividere:");
scanf("%d", &x);
printf("%s", "Inserisci secondo numero da dividere al primo:");
scanf("%lf", &y);
result = divid(x, y);
printf("%s\n", StringConcat( DoubleConcat( "Il risultato è: ", result), "."));
printf("\n");
}
if (ans == 4) {
printf("%s", "Inserisci primo numero da moltiplicare:");
scanf("%d", &x);
printf("%s", "Inserisci secondo numero da moltiplicare:");
scanf("%lf", &y);
result = mol(x, y);
printf("%s\n", StringConcat( DoubleConcat( "Il risultato è: ", result), "."));
printf("\n");
}
if (ans == 5) {
lp = false;
}
}
printf("%s\n", "");
printf("\n");
printf("%s\n", "Grazie di aver utilizzato questo programma :>");
return 0;

}
