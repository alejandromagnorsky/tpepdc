#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define MAX_LENGTH	78
#define MAX_ROWS	10000

int
main(void){
	int i, j;
	char ** msg = malloc(sizeof(char *)*MAX_ROWS);
	for(i = 0; i < MAX_ROWS; i++)
		msg[i] = malloc(sizeof(char)*(MAX_LENGTH+1));
	i = 0;
	while(i < MAX_ROWS && fgets(msg[i], MAX_LENGTH, stdin) != NULL)
		i++;
	
	for(j = 0; j < i; j++)
		if(!strstr(msg[j], "To:"))
			printf("%s", msg[j]);
	return 0;
}
