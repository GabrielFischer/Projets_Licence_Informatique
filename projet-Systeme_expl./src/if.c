
#include "header.h"

int myif(char** args, int count){
    
    if (count<4){ //Peut-être 5, à vérifier
        dprintf(2, "if: syntax error 1\n");
        return 1;
    }

    //Position des arguments du if
    int test_start=1;
    int test_end =0;
    int cmd1_start=0;
    int cmd2_start=0;
    int openbracket_counter = 0;

    //Modification surement nécéssaire pour vérifier les erreurs de syntaxe
    for (int i=1; i<count; i++){
        if (strcmp(args[i],"{")==0){
            if (test_end==0){
                test_end=i-1;
            }
            if (cmd1_start == 0){
                cmd1_start = i+1;
            }else if(openbracket_counter == 0){
                cmd2_start=i+1;
            }
            openbracket_counter++;
        }
        if(strcmp(args[i], "}") == 0){
            openbracket_counter -- ; 
            if(openbracket_counter < 0){
                openbracket_counter = 0;
            }
        }
    }

    //Debogage
    
    if (test_end==0 || cmd1_start == 0 || (cmd2_start > 0 && cmd2_start <= cmd1_start) || cmd2_start >= count){
        dprintf(2, "if: syntax error 2\n");
        return 1;
    }

   

    //Concaténation des arguments en une seule chaine de caractère pour pouvoir les retraiter avec procedure_main
    char test_cmd[1024] = "";
    for (int i = test_start; i <= test_end; i++) {
        strcat(test_cmd, args[i]);
        if (i != test_end){
            strcat(test_cmd, " ");
        }
    }

    char cmd1[1024] = "";
    for (int i = cmd1_start; i < (cmd2_start > 0 ? cmd2_start - 3 : count - 1); i++) {
        strcat(cmd1, args[i]);
        if (i != (cmd2_start > 0 ? cmd2_start - 3 : count - 2)){
            strcat(cmd1, " ");
        }
    }

    char cmd2[1024] = "";
    if (cmd2_start > 0) {
        for (int i = cmd2_start; i < count - 1; i++) {
            strcat(cmd2, args[i]);
            if (i != count - 2){
                strcat(cmd2, " ");
            }
        }
    }



    int test_result =procedure_main(test_cmd,0);
    if (test_result==0){
        return procedure_main(cmd1,0);
    }else if(cmd2_start>0){
        return procedure_main(cmd2,0);
    }

    return 0;


}