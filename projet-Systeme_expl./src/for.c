    #include "header.h"

    char* parser(char ** input, int count);
    int check_arguments(char ** input);
    int my_error(char * err_msg, int ret_val);
    int handle_entry(char * entryname,  int entrytype, char ** input);
    char* create_entryname(char* dir, char * name, char ** input);
    char *replace_substring(const char *src, const char *search, const char *replace);
    char* create_variable(char* var);
    int recursion(int active_processes, char ** input, char * entry, int input_count, int MAX_RETURN_VALUE, int STATUT_DERNIERE_COMMANDE);
    int parallel(int active_processes, char *command, char *entry_name, int *MAX_RETURN_VALUE);

    int arg_a = 0, arg_r = 0, arg_e = 0, arg_t = 0, arg_p = 0;
    int cmd_start = -1, cmd_end = -1;
    int cmd_size;
    int STATUT;

    int my_error(char * err_msg, int ret_val){
        dprintf(2, "Error: %s\n", err_msg);
        return ret_val;
    }
    /*
    fonction qui vérifie la syntaxe de la commande puis extrait la partie de la commande qui est entre {}
    valeur de retour -> commande extraite de {}
    */
    char* parser(char ** input, int count){

        if(strcmp(input[0], "for") != 0 || strcmp(input[2], "in") != 0){
            my_error("Wrong input. Expected for F in dir { CMD }", 2);
            return NULL;
        }

        for(int i = 0; i < count; i++){
            if (strcmp("{", input[i]) == 0) {
                cmd_start = i;
                break;
            }
        }

        if(cmd_start == -1){
            my_error("Wrong input. Expected for F in dir { CMD }", 2);
            return NULL;
        }

        if(strcmp(input[count-1], "}") == 0){
            cmd_end = count - 1;
        }
        else{
            my_error("Wrong input. Expected for F in dir { CMD }", 2);
            return NULL;
        }

        cmd_size = cmd_end - cmd_start - 1;

        if(check_arguments(input) == 1){
            return NULL;
        }

        size_t total_length = 0;
        for (int i = cmd_start+1; i < cmd_end; i++) {
            total_length += strlen(input[i]) + 1; // Add space for the string and one space
        }

        char *commands = malloc(total_length);

        if (!commands) {
            my_error("Memory allocation failed", 1);
            return NULL;
        }

        commands[0] = '\0';

        for (int i = cmd_start + 1; i < cmd_end; i++) {
            strcat(commands, input[i]);
            if (i < cmd_end - 1) {
                strcat(commands, " "); // Add a space after each string except the last one
            }
        }

        return commands;

    }

    int check_arguments(char ** input){
        for(int i = 4; i < cmd_start; i++){
            if(strcmp(input[i], "-A") == 0){
                arg_a = 1;
                continue;
            }
            else if(strcmp(input[i], "-r") == 0){
                arg_r = 1;
                continue;
            }
            else if(strcmp(input[i], "-e") == 0){
                if(strcmp(input[i+1], "-e") == 0 || strcmp(input[i+1], "-t") == 0 || strcmp(input[i+1], "-r") == 0 || strcmp(input[i+1], "-A") == 0 || strcmp(input[i+1], "-p") == 0 || strcmp(input[i+1], "{") == 0){
                    return my_error("Wrong arguments, expected input: for F in REP [-e EXT] { CMD }", 2);
                }
                else{
                    arg_e = i+1; // to know at which position in input, we will need it later
                    i++;
                    continue;
                }
            }
            else if(strcmp(input[i], "-t") == 0){
                if(strcmp(input[i+1], "d") == 0 || strcmp(input[i+1], "f") == 0 || strcmp(input[i+1], "l") == 0 || strcmp(input[i+1], "p") == 0){
                    arg_t = i+1;
                    i++;
                    continue;
                }
                else{
                    return my_error("Wrong arguments, expected input: for F in REP [-A] [-r] [-e EXT] [-t TYPE] [-p MAX] { CMD }", 2);
                }
            }
            else if(strcmp(input[i], "-p") == 0){
                if(strcmp(input[i+1], "-e") == 0 || strcmp(input[i+1], "-t") == 0 || strcmp(input[i+1], "-r") == 0 || strcmp(input[i+1], "-A") == 0 || strcmp(input[i+1], "-p") == 0 || strcmp(input[i+1], "{") == 0){
                    return my_error("Wrong arguments, expected input: for F in REP [-A] [-r] [-e EXT] [-t TYPE] [-p MAX] { CMD }", 2);
                }
                else{
                    arg_p = atoi(input[i+1]); // to take MAX value
                    i++;
                    continue;
                }
            }
            else{
                return my_error("Wrong arguments, expected input: for F in REP [-A] [-r] [-e EXT] [-t TYPE] [-p MAX] { CMD }", 2);
            }
        }
        return 0;
    }

    /*
    La fonction effectue des vérifications sur l'entrée en fonction des arguments passés à la commande
    (par exemple, si des fichiers cachés doivent être ignorés, ou si le type de fichier doit correspondre à un critère spécifié).
    La fonction retourne une valeur qui indique si l'entrée doit être ignorée,
    traitée récursivement, ou si une erreur est survenue.
    */
    int handle_entry(char * entryname,  int entrytype, char ** input){

        if(strcmp(entryname, ".") == 0 || strcmp(entryname, "..") == 0) {
            return 1;
        }

        // If arg_a is 0, skip hidden files (those starting with '.')
        if (arg_a == 0 && entryname[0] == '.') {
            return 1;
        }

        if(arg_t){
            if((strcmp(input[arg_t], "d") == 0 && entrytype != DT_DIR) || (strcmp(input[arg_t], "f") == 0 && entrytype != DT_REG) ||
                (strcmp(input[arg_t], "p") == 0 && entrytype != DT_FIFO) || (strcmp(input[arg_t], "l") == 0 && entrytype != DT_LNK)){
                return 2;
            }
        }

        if(arg_e){
            size_t name_len = strlen(entryname);
            size_t arg_e_len = strlen(input[arg_e]) + 1;
            char *extension = malloc(strlen(input[arg_e]) + 2); //. and \0
            if(extension == NULL){
                return -1;
            }
            strcpy(extension, ".");
            strcat(extension, input[arg_e]);

            // Exclude files not ending with the substring arg_e
            if (arg_e_len > 0 && name_len >= arg_e_len) {
                if (strcmp(entryname + name_len - arg_e_len, extension) != 0) {
                    free(extension);
                    return 2;
                }
                free(extension);

            }
        }
        return 0;
    }

    char *replace_substring(const char *src, const char *search, const char *replace) {
        if (!src || !search || !replace) {
            return NULL; // Return NULL if any input is invalid
        }

        size_t srcLen = strlen(src);
        size_t searchLen = strlen(search);
        size_t replaceLen = strlen(replace);

        // If the search substring is empty, return a copy of the source string
        if (searchLen == 0) {
            char *result = malloc(srcLen + 1);
            if (result) {
                strcpy(result, src);
            }
            return result;
        }

        // Count occurrences of 'search' in 'src'
        size_t count = 0;
        const char *temp = src;
        while ((temp = strstr(temp, search)) != NULL) {
            count++;
            temp += searchLen;
        }

        // Calculate the length of the new string
        size_t newLen = srcLen + count * (replaceLen - searchLen);

        // Allocate memory for the new string
        char *result = malloc(newLen + 1);
        if (!result) {
            return NULL; // Return NULL if memory allocation fails
        }

        // Construct the new string
        char *dest = result;
        const char *current = src;
        while ((temp = strstr(current, search)) != NULL) {
            // Copy characters before the match
            size_t segmentLen = temp - current;
            memcpy(dest, current, segmentLen);
            dest += segmentLen;

            // Copy the replacement string
            memcpy(dest, replace, replaceLen);
            dest += replaceLen;

            // Move past the search substring in the source
            current = temp + searchLen;
        }

        // Copy the remaining part of the source string
        strcpy(dest, current);

        return result;
    }

    char* create_entryname(char* dir, char * name, char ** input){

        char* entry_name = malloc(strlen(dir) + strlen(name) + 2); // +2 for "/" and null terminator
        if (!entry_name) {
            my_error("Memory allocation failed.", 1);
            return NULL;
        }
        entry_name[0] = '\0'; // Initialize as an empty string
        strcat(entry_name, dir);
        strcat(entry_name, "/");
        strcat(entry_name, name);

        if(arg_e){
            entry_name[strlen(entry_name) - strlen(input[arg_e]) - 1] = '\0';
        }

        return entry_name;
    }

    char* create_variable(char* var){
        char *variable = malloc(strlen(var) + 2); // '$' and \0
        if(!variable){
            return NULL;
        }
        else{
            variable[0] = '\0';
            strcat(variable, "$");
            strcat(variable, var);
        }
        return variable;
    }

    int recursion(int active_processes, char ** input, char * entry, int input_count, int MAX_RETURN_VALUE, int STATUT_DERNIERE_COMMANDE){
        char full_path[PATH_MAX];
        snprintf(full_path, sizeof(full_path), "%s/%s", input[3], entry);

        char **modified_input = malloc(sizeof(char *) * input_count);
        if (!modified_input) {
            my_error("Error: Memory allocation failed for modified input.", 2);
            exit(1);
        }
        for (int i = 0; i < input_count; i++) {
            if (i == 3) {
                modified_input[i] = strdup(full_path);
            } else {
                modified_input[i] = strdup(input[i]);
            }
        }
        // if(arg_p > 0){
        //     int status;
        //     while (wait(&status) != -1) {
        //         if (WIFEXITED(status) && WEXITSTATUS(status) > MAX_RETURN_VALUE) {
        //             MAX_RETURN_VALUE = WEXITSTATUS(status);
        //         }
        //     }
        // }
        STATUT_DERNIERE_COMMANDE = myfor(modified_input, input_count, STATUT_DERNIERE_COMMANDE, active_processes);
        if(STATUT_DERNIERE_COMMANDE > MAX_RETURN_VALUE){
            MAX_RETURN_VALUE = STATUT_DERNIERE_COMMANDE;
        }
        //printf("%s\n", modified_input[3]);
        for (int i = 0; i < input_count; i++) {
            free(modified_input[i]);
        }
        free(modified_input);
        return MAX_RETURN_VALUE;
    }

    int parallel(int active_processes, char *command, char *entry_name, int *MAX_RETURN_VALUE){
        while(active_processes >= arg_p){
            int status;
            wait(&status);
            active_processes--;
            if (WIFEXITED(status) && WEXITSTATUS(status) > *MAX_RETURN_VALUE) {
                *MAX_RETURN_VALUE = WEXITSTATUS(status);
            }
        }
        pid_t pid = fork();
            if (pid == 0) { // Child process
                int cmd_status = procedure_main(command, 0);
                exit(cmd_status);
            } else if (pid > 0) { // Parent process
                active_processes++;
            } else {
                my_error("Fork failed.", 1);
                free(command);
                free(entry_name);
                return 1;
            }
        return active_processes;
    }

    int myfor(char ** input, int input_count, int STATUT_DERNIERE_COMMANDE, int active_processes){
        DIR *dir;
        struct dirent *entry;
        int MAX_RETURN_VALUE = 0;

        if ((dir = opendir(input[3])) == NULL) { // ouvre le dossier en paramètre (for f in DIR -> DIR ==input[3] )
            return my_error("Opendir", 1);
        }

        char *variable = create_variable(input[1]);
        if (variable == NULL) {
            return my_error("Memory allocation failed.", 1);
        }

        char *commands = parser(input, input_count);
        if(commands == NULL){
            free(variable);
            free(commands);
            return 1;
        }

        while ((entry = readdir(dir)) != NULL) {
            int entry_val = handle_entry(entry->d_name, entry->d_type, input);
            switch (entry_val){
                case 1:
                    continue;
                    break;
                case 2:
                    goto recursion;
                    break;
                case -1:
                    return my_error("Memory allocation failed.", 1);
                    break;
            }

            //entry_name cree le chemin de l'entree complet
            char* entry_name = create_entryname(input[3], entry->d_name, input);
            if (entry_name == NULL) {
                return 1;
            }
            //remplace toutes les occurrences de la variable par le chemin du fichier ou répertoire
            char *command = replace_substring(commands, variable, entry_name);
            if(command == NULL){
                return my_error("Memory allocation failed.", 1);
            }

            // Handle parallel execution with `-p MAX`
            if (arg_p > 0) {
                active_processes = parallel(active_processes, command, entry_name, &MAX_RETURN_VALUE);
            }
            else {
                // Execute the command sequentially if `-p` is not set
                STATUT_DERNIERE_COMMANDE = procedure_main(command, 0);
                if (STATUT_DERNIERE_COMMANDE > MAX_RETURN_VALUE) {
                    MAX_RETURN_VALUE = STATUT_DERNIERE_COMMANDE;
                }
            }

            free(command);

            recursion:
            if (arg_r && entry->d_type == DT_DIR) {
                MAX_RETURN_VALUE = recursion(active_processes, input, entry->d_name, input_count, MAX_RETURN_VALUE, STATUT_DERNIERE_COMMANDE);
            }
            if (entry_val != 2) {
                free(entry_name);
            }
        }

        // Wait for any remaining processes
        while (active_processes > 0) {
            int status;
            wait(&status);
            active_processes--;
            if (WIFEXITED(status) && WEXITSTATUS(status) > MAX_RETURN_VALUE) {
                MAX_RETURN_VALUE = WEXITSTATUS(status);
            }
        }

        free(variable);
        free(commands);

        closedir(dir);

        STATUT_DERNIERE_COMMANDE = MAX_RETURN_VALUE;
        return STATUT_DERNIERE_COMMANDE;
    }

