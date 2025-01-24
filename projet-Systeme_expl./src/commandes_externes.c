 #include "header.h"

int commandeExterne(char **bon, sig_atomic_t* signaled)
{ 
    int statutRep = 1;
    pid_t child = fork();
    if(child <0)
    {
        perror("fork");
        return 1;//erreur avec le fork, la commande a échoué donc on retourne 1
    }
    else if(child == 0) //processus fils
    {
        struct sigaction sa;
        sa.sa_handler = SIG_DFL; //Remet le gestionnaire par défaut chez les enfants
        sa.sa_flags = 0;
        sigemptyset(&sa.sa_mask);

        if (sigaction(SIGTERM, &sa,NULL)!=0){
            perror("Erreur de rétablissement sigaction pour SIGTERM");
        }
        if (sigaction(SIGINT, &sa,NULL)!=0){
            perror("Erreur de rétablissement sigaction pour SIGTERM");
        }

        execvp(bon[0], bon);
        perror("redirect_exec");
        exit(1);
    }
    else // processus père
    {
        waitpid(child, &statutRep, 0);//on stocke la valeur  !!! BRUTE !!! de retour dans STATUT_DERNIERE_COMMANDE
        if (WIFSIGNALED(statutRep)){
            signaled[0]=1;
            return 255;
        }
        if (WIFEXITED(statutRep)) { //verifie si le processus s'est terminé normalement
            return WEXITSTATUS(statutRep);  //extrait le code de retour du processus
        } else {
            return 1;  // Code d'erreur générique si le processus s'est terminé anormalement
        }
    }
}

