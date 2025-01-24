#include "header.h"

int STATUT_DERNIERE_COMMANDE = 0;
int count;
const char delimiter = ' ';
char * input;
char path_terminal[PATH_MAX] = "";
volatile sig_atomic_t retour_signaled = 0;

//pwd.c
char absolute_path[PATH_MAX] = "";
void initPathTerminal(char * absolutePath,char * pathTerminal){

    pathTerminal[0] = '\0';  // Vide correctement pathTerminal

    char tmp[MAX_PROMPT_LENGTH];
    tmp [0]= '\0';

    char status[10];  // Tableau pour contenir le statut sous forme de chaîne
    if (retour_signaled){
        sprintf(status, "[SIG]");
        retour_signaled=0;
    }else{
        sprintf(status, "[%d]", STATUT_DERNIERE_COMMANDE);
    }


    size_t maxPath = 30 - strlen(status) - 5;
    
        const char *premierSlash = strchr(absolutePath+1, '/');
        if(premierSlash){

            const char * secondSlash = strchr(premierSlash+1, '/');

            if(secondSlash){
                if (strlen(secondSlash)>maxPath){ //verifie que le chemin ne dépasse pas la limite affichable
                    
                    sprintf(tmp, "..."); //ajoute ... si le chemin est trop long
                    strncpy(tmp + 3, secondSlash + strlen(secondSlash) - maxPath, maxPath); //recopie seulement la fin du chemin
                    tmp[maxPath + 3] = '\0';
                    
                }
                else{
                    strncpy(tmp  ,secondSlash , strlen(secondSlash)); //recopie tout le chemin
                    tmp[strlen(secondSlash)] = '\0';
                    
                }
            }
        }
        else{
            strcat(tmp,absolutePath);
        }
    
    if (STATUT_DERNIERE_COMMANDE==0){//ajoute le status, chemin relatif et $ à la chaine pathTerminal avec les couleurs adaptés
        sprintf(pathTerminal,"%s%s%s%s%s$ ",GREEN,status,CYAN,tmp,RESET);
    }
    else{
        sprintf(pathTerminal,"%s%s%s%s%s$ ",RED,status,CYAN,tmp,RESET);
    }
    
     
    
    pathTerminal[strlen(pathTerminal)+1] = '\0';

    
}

/*
    Option mode describes the work mode of the function as follows:
    When mode is set to 0 the function works normally and splits the
    string on delimiter
    When mode is set to a value other than 0, it takes the brackets
    ('{' and '}') into account and only splits the string on delimiter
    if that delimiter is outside of ANY PAIR of brackets. This is useful
    when the function is called with delimiter ';' since the splitting
    inside of any pair of brackets MUST BE DONE in the according command   
*/

char** split_string(const char* str, const char delimiter, int* count, int mode) {
    int i, j = 0, k = 0;
    int length = strlen(str);
    *count = 0;  // Compte initialisé à 0 pour les sous-chaînes valides

    // Alloue de la mémoire pour le tableau de chaînes
    char** result = malloc((length + 1) * sizeof(char*));
    if (!result) return NULL;

    int in_word = 0;
    int openbracket_counter = 0; // Counts the number of open brackets '{' we've encountered so far
    
    for (i = 0; i <= length; i++) {
        if (mode) {
            if (str[i] == '{') openbracket_counter++;
            if (str[i] == '}') {
                openbracket_counter--;
                if (openbracket_counter < 0) openbracket_counter = 0;
            }
        }

        if ((str[i] == delimiter && openbracket_counter == 0 && mode != 0) || (str[i] == delimiter && mode == 0) || str[i] == '\0') {
            // Si on rencontre un délimiteur ou la fin de la chaîne
            if (in_word) {
                size_t len = i - k;
                if (len > 0) {
                    result[j] = malloc((len + 1) * sizeof(char));  // +1 pour le caractère nul de fin
                    if (!result[j]) {
                        perror("Erreur d'allocation mémoire");
                        while (j-- > 0) free(result[j]);
                        free(result);
                        return NULL;
                    }
                    strncpy(result[j], &str[k], len);
                    result[j][len] = '\0';  // Terminateur de chaîne
                    j++;
                }
                in_word = 0;
            }
            k = i + 1;
            if (mode) openbracket_counter = 0;
        } else {
            if (!in_word) in_word = 1;  // On est dans un mot
        }
    }

    *count = j;  // Nombre de sous-chaînes valides
    result[*count] = NULL;  // Marque la fin du tableau

    return result;
}

int isPipeline(char** inputStr, int count){ //fonction qui teste si une entrée est une pipeline ou non
    int pipeline = 0; //si une entree est une pipeline renvoie 1
    for(int i = 0;i<count; i ++){
        if(strcmp(inputStr[i],"|")==0){ // si un | est trouvé on regarde qu'il n'est pas en fin ou en début de chaine 
            if(i == 0 || i == count-1){
                return 0;
            }
            else{
                pipeline = 1;
            }
        }
    }
    return pipeline;
}

int interpretateurDeCommande(char* command){ //fonction qui permet d'interpreter des commandes 
    // était dans le main de fsh avant
    int count;
    char ** tableauInput = split_string(command,delimiter,&count,0);

    if (count <=0){
        for(int i = 0; i<count ; i++){
            free(tableauInput[i]);
        }
        free (tableauInput);
        return 1;
    }
    if (strcmp(tableauInput[0], "for") == 0 && count >= 7){
        STATUT_DERNIERE_COMMANDE = myfor(tableauInput, count, STATUT_DERNIERE_COMMANDE, 0);
        if (getcwd(absolute_path, sizeof(absolute_path)) != NULL) {
            initPathTerminal(absolute_path, path_terminal);
        }
    }
    else if(reconnaitRedirection(tableauInput)==0){
        STATUT_DERNIERE_COMMANDE =  redirection(tableauInput,count);
    }
    else if(strcmp(tableauInput[0],"pwd")==0){
        if(count > 1){
            dprintf(2, "exit: too many arguments\n");
            STATUT_DERNIERE_COMMANDE = 1;
        }
        else{
            printf("%s\n",absolute_path);   
            STATUT_DERNIERE_COMMANDE = 0;
        }
                          
    }
    else if (strcmp(tableauInput[0],"ftype") == 0){
        if(count != 2){
            dprintf(2, "ftype: Usage: ftype entry_name\n");
            STATUT_DERNIERE_COMMANDE = 1;
        }
        else{
            STATUT_DERNIERE_COMMANDE = myftype(tableauInput[1]);
        }
    }
    
    else if (strcmp(tableauInput[0], "if") ==0){
        STATUT_DERNIERE_COMMANDE = myif(tableauInput, count);
        if (getcwd(absolute_path, sizeof(absolute_path)) != NULL) {
            initPathTerminal(absolute_path, path_terminal);
        }
    }
    else if(strcmp(tableauInput[0],"cd") == 0 ){
        if(count > 2){
            dprintf(2, "cd: too many arguments\n");
            STATUT_DERNIERE_COMMANDE = 1;
        }
        if(count == 2 || count == 1){
            STATUT_DERNIERE_COMMANDE = changement_de_repertoire(tableauInput[1],absolute_path);
            if(STATUT_DERNIERE_COMMANDE ==0){
                if (getcwd(absolute_path, sizeof(absolute_path)) != NULL) {
                    initPathTerminal(absolute_path, path_terminal);
                }
            } 
        }
        
        initPathTerminal(absolute_path,path_terminal);
        
        
    }
    else if(strcmp(tableauInput[0], "exit") == 0)
    {
        if(count == 2){ /*Si l'appel à exit se fait avec un seul argument alors appel basique de exit */
            exit_perso(tableauInput[1],STATUT_DERNIERE_COMMANDE);
        }else if( count >1){
            dprintf(2, "exit: too many arguments\n");
            STATUT_DERNIERE_COMMANDE = 1;
        }else{ /*Si exit est appelé sans argument */
            exit_perso(NULL,STATUT_DERNIERE_COMMANDE);
        }
        
    }
    else{
        sig_atomic_t signaled[1]={0};
        STATUT_DERNIERE_COMMANDE = commandeExterne(tableauInput,signaled);
        if (signaled[0]==1){
            retour_signaled=1;
        }
    }

    for(int i = 0; i<count ; i++){
        free(tableauInput[i]);
    }
    free (tableauInput);
    
    return STATUT_DERNIERE_COMMANDE;

}

int executePipeline(char * commands) { // fonction qui permet d'executer une pipeline
    int countTraitVertical = 0;
    char ** pipelineCommands = split_string(commands,'|',&countTraitVertical,0);

    int pipe_fds[2 * (countTraitVertical - 1)];
    pid_t pid;

    // Crée un pipe pour chaque commande (sauf la dernière)
    for (int i = 0; i < countTraitVertical - 1; i++) {
        if (pipe(pipe_fds + i * 2) < 0) {
            perror("pipe");
            return -1;
        }
    }

    for (int i = 0; i < countTraitVertical; i++) {
        
        pid = fork();
        if (pid == -1) {
            perror("fork");
            return -1;
        }

        if (pid == 0) {  // Processus enfant
            // Si ce n'est pas la première commande, redirige l'entrée depuis le pipe précédent
            if (i > 0) {
                if (dup2(pipe_fds[(i - 1) * 2], STDIN_FILENO) < 0) {
                    perror("dup2 input");
                    exit(1);
                }
            }

            // Si ce n'est pas la dernière commande, redirige la sortie vers le pipe suivant
            if (i < countTraitVertical - 1) {
                if (dup2(pipe_fds[i * 2 + 1], STDOUT_FILENO) < 0) {
                    perror("dup2 output");
                    exit(1);
                }
            }

            // Ferme tous les descripteurs de pipe
            for (int j = 0; j < 2 * (countTraitVertical - 1); j++) {
                close(pipe_fds[j]);
            }

            // Appelle l'interpréteur de commande pour exécuter la commande
            
            interpretateurDeCommande(pipelineCommands[i]);
            
            
            exit(0);  // On s'assure que l'enfant termine après l'exécution
        }

        
    }

    // Libère la mémoire allouée pour chaque commande
    for (int i = 0; i < countTraitVertical; i++) {
        free(pipelineCommands[i]);
    }
    free(pipelineCommands);

    // Ferme tous les descripteurs de pipe dans le parent
    for (int i = 0; i < 2 * (countTraitVertical - 1); i++) {
        close(pipe_fds[i]);
    }

    // Attends tous les enfants
    for (int i = 0; i < countTraitVertical; i++) {
        wait(NULL);
    }

    return 0;
}
/*
Quand on detecte une rediraction :
fork()
redirection dans processus enfant
continuation de la fonction prodecure-main dans enfant
le parent attends l'enfant wait(null) échange du resultat par tube
*/
int procedure_main(char* input, int from_main){
        
    int cmd_count = 0; //Compteur de commandes séparées par ";"
    char** commands = split_string(input, ';', &cmd_count, 1);
    if(commands == NULL){
        return 1;
    }
    
    for (int cmd_index=0; cmd_index < cmd_count;cmd_index++){ //Execute les commandes séparées par ";" à la suite
        if (retour_signaled){
            for (int i=0;i<cmd_count;i++){
                free(commands[i]);
            }
            free(commands);
            return 1;
        }
        
        char* command = commands[cmd_index];
        
        if (command[0]=='\0') continue; //Permet de gérer les erreurs de syntaxe type CMD1 ;; CMD2
        //Possiblement d'autres exceptions de ce genre à gérer pendant les tests

        int count;
        char ** tableauInput = split_string(command,delimiter,&count, 0);

        if(count == 0){
            free(command);
            free (tableauInput);
            break;
        }
        
        // Ajoute la commande à l'historique (permet de mettre anciennes commandes avec fleche haut et bas)
        add_history(command);
        
        // Traitement de l'entrée
        if(isPipeline(tableauInput,count)){
            STATUT_DERNIERE_COMMANDE = executePipeline(command);
        }
        else{
            interpretateurDeCommande(command);
        }
        

        // Libération de la mémoire allouée pour l'entrée
        for(int i = 0; i<count ; i++){
            free(tableauInput[i]);
        }
        
        free (tableauInput);
    }

    if (from_main){
        free(input);
    }
    for (int i=0; i<cmd_count; i++){
        free(commands[i]);
    }
    free(commands);
    if (from_main){
        initPathTerminal(absolute_path,path_terminal);
    }
    return STATUT_DERNIERE_COMMANDE;
        
}

void handle_sigint() {
    retour_signaled=1;
}

void traitement_signaux(){
    struct sigaction sa;
    sa.sa_handler = handle_sigint; //pour SIGINT
    sa.sa_flags = 0;
    sigemptyset(&sa.sa_mask);

    if (sigaction(SIGINT, &sa, NULL) != 0) {
        perror("Erreur sigaction pour SIGINT");
    }

    sa.sa_handler = SIG_IGN; //pour SIGTERM
    if (sigaction(SIGTERM, &sa, NULL) != 0) {
        perror("Erreur sigaction pour SIGTERM");
    }
}




int main() {

    traitement_signaux();
    
    // Build the absolute path
    mywd(absolute_path);
    initPathTerminal(absolute_path,path_terminal);

    while (1) {
        debut:
        // Affiche le chemin absolu et lit l'entrée de l'utilisateur
        rl_outstream = stderr;
        input = readline(path_terminal);
        
        
        
        if (!input) { // Vérifie si l'entrée est NULL (CTRL+D)
            free(input);
            break;
        }
        if(input[0]=='\0'){
            free(input);
            goto debut;
        }
    
        procedure_main(input, 1);

    }
    return STATUT_DERNIERE_COMMANDE;
}

