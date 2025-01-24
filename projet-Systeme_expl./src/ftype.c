#include <stdio.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <unistd.h>
#include <string.h>
#include <errno.h>
#include <stdlib.h>

int myftype(const char *path);

int myftype(const char *path) {
    struct stat st;
    
    if(lstat(path, &st) < 0) {
        dprintf(2, "ftype: %s\n", strerror(errno));
        return 1;
    }
    if(S_ISREG(st.st_mode)) {
        printf("regular file\n");
    } 
    else if(S_ISDIR(st.st_mode)) {
        printf("directory\n");
    } 
    else if(S_ISLNK(st.st_mode)) {
        printf("symbolic link\n");
    } 
    else if(S_ISFIFO(st.st_mode)) {
        printf("named pipe\n");
    } 
    else {
        printf("other\n");
    }


    return 0;
}
