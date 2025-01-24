#include <stdio.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <unistd.h>
#include <dirent.h>
#include <string.h>
#include <errno.h>
#include <stdlib.h>
#include <linux/limits.h>

char OLDPWD[PATH_MAX] ="";

int changement_de_repertoire(char* path, char* absolute_path){
    char current_dir[PATH_MAX]="";

    //Utilisation de getcwd pour obtenir l'emplacement actuel
    if (getcwd(current_dir, sizeof(current_dir)) == NULL) {
        fprintf(stderr, "cd: No such file or directory\n");
        goto error;
    }

    //Lorsque path==NULL, on utiliser getenv(HOME) pour connaitre la var d'env home
    if (path==NULL){
        path = getenv("HOME");
        if (path==NULL){
            fprintf(stderr, "cd: No such file or directory\n");
            goto error;
        }
    }

    //Lorsque path=='-', on utilise OLDPWD
    else if (strcmp(path, "-")==0){
        if (strlen(OLDPWD)==0){
            fprintf(stderr, "cd: No such file or directory\n");
            goto error;
        }
        path = OLDPWD;
    }

    //Changement de répertoire avec chdir
    if (chdir(path)!=0){
        fprintf(stderr,"cd: No such file or directory\n");
        goto error;
    }

    //Mise à jour de OLDPWD
    strncpy(OLDPWD, current_dir, sizeof(OLDPWD)-1);
    OLDPWD[sizeof(OLDPWD)-1] = '\0';

    // Mettre à jour le répertoire courant
    if (getcwd(current_dir, sizeof(current_dir)) == NULL) {
        fprintf(stderr, "cd: No such file or directory\n");
        return 1;
    }

    //Mise à jour absolute_path global ici
    strncpy(absolute_path, current_dir, sizeof(absolute_path) - 1);
    absolute_path[sizeof(absolute_path) - 1] = '\0';

    return 0;

    error :
    return 1;

}