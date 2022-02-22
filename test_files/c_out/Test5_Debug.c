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



//Fun prova
int prova(int value1, char* *value2) {
char* a = malloc(sizeof(char) * MAXCHAR);
strcpy(a, "ciao");
int b = 2;
return b;
}


int main () {

char* s1 = malloc(sizeof(char) * MAXCHAR);
strcpy(s1, "ciao");
while (true) {
char* s1 = malloc(sizeof(char) * MAXCHAR);
}
return 0;

}
