#include "header.h"

char* transformToSingleString(char **chaine) {
    int totalLength = 0;
    
    // Calculer la longueur totale de la chaîne résultante
    for (int i = 0; chaine[i] != NULL; i++) {
        totalLength += strlen(chaine[i]) + 1; // +1 pour le séparateur (espace ou autre)
    }

    // Allouer de la mémoire pour la nouvelle chaîne
    char *result = (char*)malloc(totalLength * sizeof(char));
    if (result == NULL) {
        perror("malloc");
        return NULL;
    }

    // Copier chaque chaîne de `chaine` dans la nouvelle chaîne
    result[0] = '\0'; // Initialiser la chaîne vide
    for (int i = 0; chaine[i] != NULL; i++) {
        strcat(result, chaine[i]);
        if (chaine[i + 1] != NULL) {
            strcat(result, " "); // Ajouter un espace entre les mots
        }
    }

    return result;
}


int reconnaitRedirection(char** chaine)
{
    for(int i =0; chaine[i] != NULL; i++)
    {
        if(strcmp(chaine[i], ">")==0 || strcmp(chaine[i], ">>")==0 || strcmp(chaine[i], ">|")==0 || strcmp(chaine[i], "<")==0 || strcmp(chaine[i], "2>")==0
        || strcmp(chaine[i], "2>|")==0 || strcmp(chaine[i], "2>>") == 0)
        {
            return 0;
        }
    }
    return 1;
}

void supprimerRedirection(char** chaine, int* count) {
    int i = 0;

    // Parcourir la chaîne de commande pour trouver une redirection
    while (chaine[i] != NULL) {
        // Si une redirection est trouvée
        if (strcmp(chaine[i], ">") == 0 || strcmp(chaine[i], ">>") == 0 || strcmp(chaine[i], ">|") == 0 ||
            strcmp(chaine[i], "<") == 0 || strcmp(chaine[i], "2>") == 0 || strcmp(chaine[i], "2>>") == 0 || 
            strcmp(chaine[i], "2>|") == 0) {

            free(chaine[i]);       // Libérer la redirection elle-même
            free(chaine[i + 1]); 
            // Supprimer la redirection et le fichier associé
            for (int j = i; chaine[j] != NULL; j++) {
                chaine[j] = chaine[j + 2];  // Décaler les éléments de 2 places
            }
            (*count) -= 2;  // Réduire le nombre d'arguments de 2
            return;  // Arrêter dès qu'une redirection est supprimée
        }
        i++;
    }
}

int faireRedirection(char ** chaine , int count){
    int fd;
    for(int i = 0; i <count ; i ++){
        if(strcmp(chaine[i], "<")==0){
        fd = open(chaine[i+1],O_RDONLY);
        if(fd<0){
            printf("%s\n",chaine[i+1]);
            perror("redirection < : open");
            
            return 1;
        }
        dup2(fd, STDIN_FILENO); //dredirection de la sortie/entrée/sortie erreur standard sur le fichier ouvert
        close(fd);
        return 0;

        }else if (strcmp(chaine[i], ">") == 0) {
            // Redirection de la sortie standard
            if (access(chaine[i+1], F_OK) != -1) {
                dprintf(2,"pipeline_run: File exists\n");
                return 1; 
            }
            fd = open(chaine[i+1], O_CREAT | O_EXCL | O_WRONLY | O_TRUNC, 0644);
            if (fd == -1) {
                perror("open");
                return 1;
            }
            dup2(fd, STDOUT_FILENO);
            close(fd);
            return 0;
        }
            
        else if(strcmp(chaine[i], ">|")==0){
            fd = open(chaine[i+1], O_WRONLY | O_CREAT | O_TRUNC, 0644);
            if(fd == -1)
            {
                perror("redirection : open");
                return 1;
            }
            dup2(fd, STDOUT_FILENO);
            close(fd);
            return 0;
            
        }
        else if (strcmp(chaine[i], ">>") == 0) {
            // Redirection de la sortie standard (append)
            fd = open(chaine[i+1], O_WRONLY | O_CREAT | O_APPEND, 0644);
            if (fd == -1) {
                perror("open");
                return 1;
            }
            dup2(fd, STDOUT_FILENO);
            close(fd);
            return 0;
        }
        else if (strcmp(chaine[i], "2>") == 0) {
            // Redirection de la sortie d'erreur
            fd = open(chaine[i+1],  O_EXCL | O_WRONLY | O_CREAT | O_TRUNC, 0644);
            if (fd == -1) {
                dprintf(2,"pipeline_run: File exists\n");
                return 1;
            }
            dup2(fd, STDERR_FILENO);
            close(fd);
            return 0;
        }
        else if (strcmp(chaine[i], "2>>") == 0) {
            // Redirection de la sortie d'erreur (append)
            fd = open(chaine[i+1], O_WRONLY | O_CREAT | O_APPEND, 0644);
            if (fd == -1) {
                perror("open");
                return 1;
            }
            dup2(fd, STDERR_FILENO);
            close(fd);
            return 0;
        }
        else if(strcmp(chaine[i], "2>|") == 0){
            fd = open(chaine[i+1], O_WRONLY | O_CREAT | O_TRUNC, 0644);
            if(fd == -1)
            {
                perror("redirection : open");
                return 1;
            }
            dup2(fd, STDERR_FILENO);
            close(fd);
            return 0;
        }
        
    }
    return 1;
    
}
int redirection(char ** chaine,int count){
    int res = 0;
    int pid = -1;
    int tube[2];
    pipe(tube);
    pid = fork();
        
    if(pid == 0){
        close(tube[0]);

        if(faireRedirection(chaine,count)!=0){
            res = 1;
            write(tube[1], &res, sizeof(res));
            close(tube[1]);
            exit_perso(NULL,res);
        }
        if(strcmp(chaine[0],"exit")==0){
            res = 2;
            write(tube[1], &res, sizeof(res));
            close(tube[1]);
            res = 0;
            if(count -2 != 0 ){
                exit_perso(chaine[1],res);
            }
            else{
                exit_perso(NULL,res);
            }
        }

        supprimerRedirection(chaine,&count);
        
        
        char * tmp = transformToSingleString(chaine);
        interpretateurDeCommande(tmp);

        free(tmp);
        write(tube[1], &res, sizeof(res));
        
        close(tube[1]);
        exit_perso(NULL,res);
        
    }
    else{
        close(tube[1]);
        read(tube[0],&res,sizeof(res));
        if(res == 2){
            res = 0;
            exit_perso(NULL,res);
        }
        wait(NULL);
        close(tube[0]);
        return res;
    }
    return res;
    
}