#ifndef ENCHERE_H
#define ENCHERE_H //A quoi Ã§a sert svp ??
#include "connexions.h"

typedef struct {
    uint16_t vendeur_id;
    uint16_t meneur_actuel;
    uint32_t numero_vente;
    uint32_t prix_actuel;
} Enchere;

typedef struct {
    uint32_t numero_vente;
    uint32_t prix_initial;
    uint16_t meneur_actuel;
} ParamFinalisationThread;

extern uint16_t compteur_encheres;
extern Enchere liste_encheres[100];
extern pthread_mutex_t mutex_encheres;


void* thread_enchere(void* arg);
void* reception_encheres(void* arg);

#endif