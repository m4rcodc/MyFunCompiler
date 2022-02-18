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


//Fun isPrimo
bool isPrimo(int num, int div_cor) {
if (div_cor < num) {
int divisione = num / div_cor;
int rest = num - divisione * div_cor;
if (rest != 0) {
return isPrimo(num, div_cor + 1);
}
else {
return false;
}
}
else {
return true;
}
}


int main () {

int num;
char* nome_programma = malloc(sizeof(char) * MAXCHAR);
strcpy(nome_programma, "\'Calcolo numeri primi\'");
printf("%s\n", StringConcat( "Benvenuto in ", nome_programma));
printf("\n");
printf("%s", "Inserire un numero positivo: ");
scanf("%d", &num);
while (num < 0) {
printf("%s\n", StringConcat( IntConcat( "Il numero ", num), " non e\' positivo, riprova"));
printf("\n");
printf("%s", "Inserire numero: ");
scanf("%d", &num);
}
if (num > 1) {
if (isPrimo(num, 2)) {
printf("%d\n", num);
printf("%s\n", " e\' primo");
printf("\n");
}
else {
printf("%d\n", num);
printf("%s\n", " non e\' primo");
printf("\n");
}
}
return 0;

}
