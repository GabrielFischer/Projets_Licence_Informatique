#ifndef CONNEXIONS_H
#define CONNEXIONS_H

#include "state.h"
#include "enchere.h"
#include "macros.h"
#include "msg.h"
#include "keys.h"


// #define port_connexion 9393
// #define MAX_PEERS 4
// #define BUFFER_SIZE 1024
// #define ADDR_MULTICAST "FF12::93:93" //FF=multicast ; 02=locale au lien ; ::93:93 = juste pour en avoir une en particulier
// #define ADDR_ENCHERES "FF12::75:75"

//Structure pour définir l'adresse perso de chaque pairs
typedef struct{
    char ip[INET6_ADDRSTRLEN];
    int PORT;
    uint16_t id;
    uint8_t cle_pub[MSG_CLE_LEN];
}AdressePerso;

//Structure pour gérer l'arrivée en TCP des nouveaux pairs, argument de handle_tcp_client
typedef struct {
    int client_sock;
    struct sockaddr_in6 client_addr;
} ThreadArg;

extern uint8_t ma_cle_sec[ED25519_SK_LEN];
extern int peer_count;
extern AdressePerso peer_list[MAX_PEERS];

extern int peer_count;

extern pthread_mutex_t mutex_fin_enchere;
extern pthread_cond_t cond_fin_enchere;
extern int enchere_terminee;


void initialiserAdressePerso(int argc, char *argv[]);

void afficher_liste(AdressePerso *liste, size_t taille);

int connect_to_peer();

void send_new_user();

uint16_t generate_unique_id();

void* handle_tcp_client(void* arg);

void* listen_for_peers(void* arg);

int send_entry_request();

void try_join_auction_system();

#endif