#ifndef STATE_H
#define STATE_H

#include <stdint.h>
#include <arpa/inet.h>
#include <netinet/in.h>
#include <sys/socket.h>
#include <stdbool.h>
#include <pthread.h>
#include <time.h>
#include "msg.h" /*On fait cet include pour accéder à MSG_CLE_LEN et les codes de message*/

#ifndef MAX_PEERS
#define MAX_PEERS 4
#endif /*Ces instructions servent à s'assurer que si jamais MAX_PEERS n'est pas bien défini, alors ici il est mis à 4,
        comme le demande le sujet*/



#define MAX_ACTIVE_AUCTIONS 10 /*On met à 10 le nombre max d'enchères possibles en même temps*/
#define MAX_POTENTIAL_PEERS 4/*Limite temporaire de pairs possibles*/

/*Etats possible du processus de jonction*/
typedef enum{
    JOIN_STATE_IDLE, /*Le pair n'essaie pas de rejoindre*/
    JOIN_STATE_SENT_REQUEST, /*Le pair a envoyé un message CODE 3*/
    JOIN_STATE_WAITING_RESP,/*Le pair attend les réponses CODE 4*/
    JOIN_STATE_SELECTING_Q,/* Le pair sélectionne un pair Q parmi les pairs qui ont répondu*/
    JOIN_STATE_CONNECTING_TO_Q, /*Le pair tente d'établir une connexion TCP/TLS avec Q*/
    JOIN_STATE_EXCHANGING_INFO,/* Le pair échange les messages CODE 5, 50/51, 7 avec Q*/
    JOIN_STATE_JOINED/*Le pair rejoint le système avec succès*/
}JoinState;

/*Définition des structures nécessaires à la représentation de l'état du protocole*/

/*La structure suivante donne toutes les informations nécessaires (normalement) sur un l'instance d'un pair*/
typedef struct{
    uint16_t id; /*Identifiant unique du pair, sur 2 octets*/
    struct in6_addr ip_perso; /*Adresse IPV6 binaire, sur 16 octets*/
    uint16_t port_perso;
    uint8_t cle_publique[MSG_CLE_LEN]; /*Clé publique ED25519 du pair, sur 113 octets*/
    uint32_t compteur_ventes_initiees; /*Compteur des ventes initiées par lal pair associé, cpt sur 4 octets*/
    bool est_actif; /* Vrai si le pair est considéré comme actif sur le système*/
}PeerInfo;

/*Structure pour la liste des pairs actuellement connus par cette instance de l'application
Y' a déjà un variable peer_list dans connexions.c mais je ne sais pas si c'est suffisant, et pratique... j'en sais vraiment rien*/
typedef struct{
    PeerInfo listePairs[MAX_PEERS]; /*La fameuse liste de pairs*/
    size_t count; /*Nombre de pairs actuellement dans la liste*/
}PeerList;

/*Structure qui donne toutes les informations nécessaires (normalement) sur une vente aaux enchères*/
typedef struct{
    uint32_t num_vente; /*Numéro unique de la vente, j'crois c'est un truc genre : ID superviseur + cpt, sur 4 octets*/
    uint16_t id_superviseur; /*Identifiant du pair qui supervise cette vente*/
    uint32_t prix_actuel; /*Dernier prix validé, sur 4 octets*/
    uint16_t id_meilleur_offrant; /*Pour stocker l'identifiant de celui qui possède la meilleure offre d'achat*/
    time_t timestamp_derniere_activite; /*Moment de la dernière enchère validée, ou début de vente*/
    bool est_terminee; /*Va servir comme un flag pour savoir si cette vente est terminée ou non*/
}VenteAuxEncheres;

/*Structure qui contient la liste des ventes aux enchères actuellement actives et connues par cette instance*/
typedef struct{
    VenteAuxEncheres liste_ventes[MAX_ACTIVE_AUCTIONS]; /*La liste des ventes actives et connues*/
    size_t count; /*Nb de ventes actuellement dans la liste*/
}ActiveAuctionList;

/*Structure temporaire pour stocker les informations des pairs répondant à une requête de CODE 3
Utile pendant la phase JOIN_STATE_WAITING_RESP*/
typedef struct{
    PeerInfo liste[MAX_POTENTIAL_PEERS];
    size_t count;
} PotentialPeerList;

/*Strcuture pour l'état global de l'application du pair
La strcuture suivante regroupe toutes les informations nécessaires (normalement)*/
typedef struct{
    PeerInfo moi; /*Donc les informations sur le pair lui-même (son ID final, ses adresses, clés, cpt)*/
    PeerList pairs_connus; /*Liste de tous les pairs connus dans le système*/
    ActiveAuctionList ventes_actives; /*Liste des ventes d'enchères en cours*/
    struct in6_addr adresse_liaison_multicast; /*Adresse du groupe de liaison*/
    uint16_t port_encheres; /* Port du groupe d'enchères*/
    struct in6_addr adresse_encheres_multicast; /*Adresse du groupe d'enchères*/
    /*Descripteurs de socket importants*/
    int udp_socket_general; /*Socket UDP pour écoute générale (liaison, enchères, unicast personnel)*/
    int tcp_listen_socket; /*Socket TCP/TLS pour écoute personnelle*/
    pthread_t network_thread; /*Thread pour la gestion réseau si séparé*/
    JoinState join_state; /*Etat actuel du pair dans le processus de jonction*/
    PotentialPeerList potential_peers; /*Liste temporaire des pairs répondant CODE 4*/
    time_t join_request_sent_time; /*Timestamp de l'envoi du premier message CODE 3 pour gérer le timeout $t_2s$*/
    uint16_t port_liaison; /*Port du groupe de liaison*/
    /*Faudra probablement ajouter d'autres champs plus tard mais pour l'instant ça devrait être bon, j'espère...*/
}AppState;

/*Intitalise la structure globale*/
void app_state_init(AppState *state);


void app_state_init2(AppState *state,
                    uint16_t id,
                    const char *ip_str,
                    uint16_t port);

/*Ajoute un pair à la liste des pairs connus (vérifie les doublons/limite MAX_PEERS)*/
int add_known_peer(PeerList *peer_list, const PeerInfo *peer);

/*Recherche un pair dans la liste par son ID*/
PeerInfo *find_peer_by_id(PeerList *peer_list, uint16_t id);

/*Supprime de la liste le pair qui correspond à l'ID*/
int remove_known_peer(PeerList *peer_list, uint16_t id);

/*Ajoute un pair potentiel à la liste temporaire (même propriétés que pour add_known_peer)*/
int add_potential_peer(PotentialPeerList *potential_peers, uint16_t id, const struct in6_addr *ip, uint16_t port);

/*Réinitialise la liste des pairs potentiels*/
void clear_potential_peers(PotentialPeerList *potential_peers);

/*Ajoute une vente à la liste des ventes actives (même propriétés que les autres fonctions d'ajouts mais utilise MAX_ACTIVE_AUCTIONS)*/
int add_active_auction(ActiveAuctionList *auction_list, const VenteAuxEncheres *auction);

/*Recherche une vente active par son numéro*/
VenteAuxEncheres *find_auction_by_num(ActiveAuctionList *auction_list, uint32_t num_vente);

/*Supprime une vente de la liste via son numéro*/
int remove_active_auction(ActiveAuctionList *auction_list, uint32_t num_vente);

/*Faudra peut-être ajouter d'autres fonctions par la suite mais pour l'instant c'est tout ce que j'ai trouvé*/

#endif