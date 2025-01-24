#include "header.h"

void mypwd(char *buf){
    buf = getcwd(buf, PATH_MAX);

    if (buf == NULL) {
        dprintf(2, "Error: %s\n", strerror(errno));
    } 
    else {
        // Print the current working directory
        printf("%s\n", buf);  
    }
}

void mywd(char *buf){
    buf = getcwd(buf, PATH_MAX);

    if (buf == NULL) {
        dprintf(2, "Error: %s\n", strerror(errno));
    } 
}

