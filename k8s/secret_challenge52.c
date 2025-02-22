#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>

#define SECRET_SIZE 32

char secret[SECRET_SIZE];

void generate_secret() {
    //rand() for a random secret
    strncpy(secret, "K8S_DEBUG_SECRET", SECRET_SIZE - 1);
    secret[SECRET_SIZE - 1] = '\0';
}

void handle_request() {
    sleep(5);  // Simulating a network call
}

int main() {
    generate_secret();
    while (1) {
        handle_request();
    }

    return 0;
}
