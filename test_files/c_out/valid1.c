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
int c=1;


//Fun sommac
double sommac(int a, double b, char* *size) {
double result;
result = a + b + c;
if (result > 100) {
char* valore = malloc(sizeof(char) * MAXCHAR);
strcpy(valore, "grande");
*size = valore;
}
else {
char* valore = malloc(sizeof(char) * MAXCHAR);
strcpy(valore, "piccola");
*size = valore;
}
return result;
}



//Fun stampa
void stampa(char* messaggio) {
int i = 1;
while (i <= 4) {
int incremento = 1;
printf("%s\n", "");
printf("\n");
i = i + incremento;
}
printf("%s\n", messaggio);
printf("\n");
}


int main () {

int a = 1;
double b = 2.2;
char* taglia = malloc(sizeof(char) * MAXCHAR);
char* ans = malloc(sizeof(char) * MAXCHAR);
strcpy(ans, "no");
double risultato = sommac(a, b, &taglia);
stampa(StringConcat( StringConcat( IntConcat( StringConcat( DoubleConcat( StringConcat( IntConcat( "la somma di ", a), " e "), b), " incrementata di "), c), " è "), taglia));
stampa(DoubleConcat( "ed è pari a ", risultato));
printf("%s\n", "vuoi continuare? (si/no)");
printf("\t");
scanf("%s", ans);
while (strcmp(ans, "si") == 0) {
printf("%s", "inserisci un intero:");
scanf("%d", &a);
printf("%s", "inserisci un reale:");
scanf("%lf", &b);
risultato = sommac(a, b, &taglia);
stampa(StringConcat( StringConcat( IntConcat( StringConcat( DoubleConcat( StringConcat( IntConcat( "la somma di ", a), " e "), b), " incrementata di "), c), " è "), taglia));
stampa(DoubleConcat( " ed è pari a ", risultato));
printf("%s", "vuoi continuare? (si/no):\t");
scanf("%s", ans);
}
printf("%s\n", "");
printf("\n");
printf("%s\n", "ciao");
return 0;

}
