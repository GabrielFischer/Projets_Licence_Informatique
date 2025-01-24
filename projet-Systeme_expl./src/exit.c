#include "header.h"

int exit_perso(char val[],int statut_derniere_commande)
{
    int statutRep;
    if(val != NULL) //si argument fourni, alors affectation comme nouvelle valeur de retour de exit
    {
        statutRep = atoi(val);
    }
    else //si aucun argument n'est fourni, alors reprise de la valeur de retour de la dernière commande exécutée
    {
        statutRep = statut_derniere_commande;
    }
    exit(statutRep);

    return 1;//pour le cas où exit ne fonctionnerait pas
}

