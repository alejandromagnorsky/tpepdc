#include <stdio.h>
#include <stdlib.h>

#define MAX_LENGTH	255
#define MAX_ROWS	100

int
main(void){
	int i, j;
	char ** body = malloc(sizeof(char *)*MAX_ROWS);
	for(i = 0; i < MAX_ROWS; i++)
		body[i] = malloc(sizeof(char)*(MAX_LENGTH+1));
	i = 0;
	while(i < MAX_ROWS && fgets(body[i], MAX_LENGTH, stdin) != NULL && body[i][0] != '.')
		i++;
	
	printf("-------------------------------\n");
	printf("Texto del mensaje:\n");
	for(j = 0; j < i; j++)
		printf("%s\n", body[j]);
	printf("-------------------------------\n");
	return 0;
}
