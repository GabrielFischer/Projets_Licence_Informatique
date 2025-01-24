#ifndef HEADER_H // garde d'inclusion permet de ne pas inclure header.h plusieurs fois
#define HEADER_H // défini HEADER_H si HEADER_H n'est pas défini
#define _DEFAULT_SOURCE

#include <stdlib.h>
#include <stdio.h>
#include <fcntl.h>
#include <unistd.h>
#include <sys/stat.h>
#include <sys/wait.h>
#include <dirent.h>
#include <sys/types.h>
#include <sys/dir.h>
#include <limits.h>
#include <string.h>
#include <errno.h>
#include <linux/limits.h>
#include <readline/readline.h>
#include <readline/history.h>
#include <signal.h>

#define BLUE "\033[34m"   // Code ANSI pour le bleu
#define RESET "\033[0m"   // Code ANSI pour réinitialiser la couleur
#define BOLD "\033[1m"  //Gras
#define GREEN "\033[32m"    //VERT
#define CYAN "\033[36m"
#define RED "\033[31m"

void mypwd(char *result);
void mywd(char *buf);
int myftype(const char *path);
int changement_de_repertoire(char* path, char* absolute_path);
int exit_perso(char val[],int statut_derniere_commande);
int myfor(char **input, int count, int STATUT_DERNIERE_COMMANDE, int active_processes);
int commandeExterne(char** bon,sig_atomic_t* signaled);
int pipeline(char** input);
char** split_string(const char* str, char delimiter, int* count, int mode);
int myif(char** args, int count);
int reconnaitRedirection(char** chaine);
int interpretateurDeCommande(char* command);
int procedure_main(char* input, int from_main);
int redirectionVerif(char** chaine);
int redirectionExit(char** bon, int statutDerniereCommande);
int redirectionPWD(char ** chaine, char* absolute_path);
int redirectionFTYPE(char** bon);
int execRedirection(char** bon);
int redirection(char ** chaine,int count);

#define MAX_PROMPT_LENGTH 31
 /*Variable globale qui doit stocker la valeur de retour
                                    de la dernière commande utilisée, et qui doit être mise à jour à chaque appel de commande qui renvoie une valeur de retour */

// écrire toutes les lignes supplémentaires avant celle-ci
#endif //le programme vient directement ici si HEADER_H est déjà défini