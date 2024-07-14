#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdint.h>
#include <stdbool.h>
#include <ctype.h>
#include "lib/aes.c"

#define SIZE 16

char* hexa_to_str(uint8_t *)__attribute__((visibility("hidden")));
uint8_t *str_to_hex(char *)__attribute__((visibility("hidden")));
void usage(char *)__attribute__((visibility("hidden")));
void decrypt(uint8_t * , uint8_t * )__attribute__((visibility("hidden")));
void print_hex(uint8_t* );
char lowercase(char );

int main(int argc, char **argv)
{
	if(argc < 3)
	{
		usage(argv[0]);
	}

	str_to_hex(argv[1]);
	str_to_hex(argv[2]);

	uint8_t *secret = str_to_hex(argv[1]);
	uint8_t *key = str_to_hex(argv[2]);

	decrypt(secret,key);

	return EXIT_SUCCESS;
}
char lowercase(char c){
	return isalpha(c) ? tolower(c) : c;
}
void usage(char *string)
{
	fprintf(stderr,"Usage: %s [SECRET] [KEY]\n",string);
	fprintf(stderr,"Example: %s 0a3e4f210a3e4f21 f6f67ad107adf67ad1010\n",string);
	exit(EXIT_FAILURE);
}
uint8_t *str_to_hex(char *str)
{
	uint8_t *ret = (uint8_t *)calloc(SIZE , sizeof(uint8_t));
	char tmp[2];
	memset(tmp,'\0',2);
	short int count = 0;

	for(int a = 0; str[a]; a += 2)
	{
		tmp[0] = (uint8_t)lowercase(str[a]);
		tmp[1] = (uint8_t)lowercase(str[a+1]);
		ret[count++] = (uint8_t)strtol(tmp, NULL, 16);
	}
	return ret;
}
void print_hex(uint8_t* str)
{
    for (int i = 0; i < SIZE ; ++i)
        printf("%x", str[i]);
    printf("\n");
}
char* hexa_to_str(uint8_t *array)
{
	char *string = (char *)calloc(SIZE,sizeof(char));
	for(int a = 0; a < SIZE; a++){
		string[a] = (char)array[a];
	}
	return string;
}
void decrypt(uint8_t *secret, uint8_t *key )
{
	struct AES_ctx ctx;

	AES_init_ctx(&ctx, key);

	AES_ECB_decrypt(&ctx,secret);
	AES_ECB_decrypt(&ctx,secret);

    puts(hexa_to_str(secret));
}
