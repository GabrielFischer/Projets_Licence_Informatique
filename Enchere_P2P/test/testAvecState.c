#include "../include/state.h"
#include "../include/connexions.h"
#include "../include/msg.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <arpa/inet.h>
#include <pthread.h>
#include <stdint.h>
#include <net/if.h>
#include <sys/select.h>
#include <sys/time.h>
#include <time.h>

void connectToPeer(AppState *state);
void sendEntryRequest(AppState *state);
void* listenToPeer(void *arg);
void tryJoinAuctionSystem(AppState *state);
int network_init(AppState *state);

void handle_join_request(AppState *state, const uint8_t* buffer, size_t len, const struct sockaddr_in6* src_addr);/*gère CODE 3*/
void handle_join_response(AppState *state, const uint8_t* buffer, size_t len, const struct sockaddr_in6* src_addr);/*gère CODE 4*/


/*Fonction qui initialise le réseau*/
int network_init(AppState *state)
{
    if (state == NULL)
    {
        fprintf(stderr, "network_init: Erreur: Pointeur AppState est NULL.\n");
        return -1;
    }
    printf("network_init: Début de l'initialisation réseau...\n");
    /*Création et initialisation du socket UDP général*/
    state->udp_socket_general = socket(AF_INET6, SOCK_DGRAM, 0);
    if (state->udp_socket_general < 0)
    {
        perror("network_init socket UDP general");
        return -1; /*Échec critique, impossible de continuer sans socket UDP*/
    }
     printf("network_init: Socket UDP général créé (descripteur %d).\n", state->udp_socket_general);
    int ok = 1;
    /*Permet de réutiliser l'adresse et le port rapidement après la fermeture du programme*/
    if (setsockopt(state->udp_socket_general, SOL_SOCKET, SO_REUSEADDR, &ok, sizeof(ok)) < 0)
    {
        perror("network_init setsockopt SO_REUSEADDR UDP");
    }
    /* S'assurer que le socket n'écoute qu'en IPv6, pas de mappage IPv4*/
    if (setsockopt(state->udp_socket_general, IPPROTO_IPV6, IPV6_V6ONLY, &ok, sizeof(ok)) < 0)
    {
        perror("network_init setsockopt IPV6_V6ONLY UDP");
    }
    struct sockaddr_in6 addr;
    memset(&addr, 0, sizeof(addr));
    addr.sin6_family = AF_INET6;
    /*Lier le socket UDP au port personnel du pair, en écoutant sur toutes les interfaces (in6addr_any)*/
    addr.sin6_port = htons(state->moi.port_perso); /*Convertir le port en network byte order*/
    addr.sin6_addr = in6addr_any; /*Écouter sur toutes les adresses IPv6 locales*/
    if (bind(state->udp_socket_general, (struct sockaddr*)&addr, sizeof(addr)) < 0)
    {
        perror("network_init bind UDP general");
        close(state->udp_socket_general);
        state->udp_socket_general = -1; /* Marquer comme invalide dans l'état*/
        return -1;/*erreur critique donc fin*/
    }
     printf("network_init: Socket UDP général lié à [::]:%u.\n", ntohs(addr.sin6_port));
    /* Abonnement au groupe multicast de liaison*/
    /*Ce socket UDP général sera utilisé pour le multicast*/
    struct ipv6_mreq mreq;
    /* On utilise l'adresse de liaison multicast stockée dans l'état (définie par app_state_init/app_state_init2
    jsp encore c'est lequel qu'on va devoir utiliser au final)*/
    memcpy(&mreq.ipv6mr_multiaddr, &(state->adresse_liaison_multicast), sizeof(struct in6_addr));
    /*Je mets interface eth0 parce c'est celle par défaut sur linux*/
    mreq.ipv6mr_interface = if_nametoindex("eth0");
    if(mreq.ipv6mr_interface == 0 && IN6_IS_ADDR_MULTICAST(&state->adresse_liaison_multicast))
    {
        /*Avertissement si l'index d'interface n'a pas pu être déterminé ou si l'interface 0 n'est pas idéale pour le multicast*/
        fprintf(stderr, "network_init: Avertissement: Impossible de déterminer l'index de l'interface 'eth0' ou interface 0 potentiellement inadéquate pour le multicast de liaison.\n");
        /* On continue en utilisant l'interface 0 par défaut, l'appel setsockopt pourrait échouer*/
        mreq.ipv6mr_interface = 0; /*interface par défaut (0) si non spécifiée*/
    }
    else
    {
        printf("network_init: Utilisation de l'interface index %u pour le multicast de liaison.\n", mreq.ipv6mr_interface);
    }
    if(setsockopt(state->udp_socket_general, IPPROTO_IPV6, IPV6_JOIN_GROUP, &mreq, sizeof(mreq)) < 0)
    {
        perror("network_init setsockopt abonnement multicast liaison");
        fprintf(stderr, "network_init: Avertissement: Impossible de rejoindre le groupe multicast de liaison %s. Le pair ne recevra pas les requêtes de jonction (CODE 3) ni les réponses (CODE 4) envoyées en multicast.\n",
               inet_ntop(AF_INET6, &state->adresse_liaison_multicast, (char[INET6_ADDRSTRLEN]){0}, INET6_ADDRSTRLEN));
        /*L'application peut potentiellement continuer si elle est le premier pair ou si elle ne compte pas recevoir/envoyer en multicast de liaison.
        Ce n'est généralement pas un échec fatal pour le démarrage, mais cela limite la fonctionnalité.*/
    }
    else
    {
        printf("network_init: Abonné au groupe multicast de liaison %s:%u sur socket %d.\n",
               inet_ntop(AF_INET6, &state->adresse_liaison_multicast, (char[INET6_ADDRSTRLEN]){0}, INET6_ADDRSTRLEN),
               ntohs(state->port_liaison), state->udp_socket_general);
    }
    /*Création et initialisation du socket TCP d'écoute (pour gérer les connexions entrantes)*/
    /*Ce socket permettra aux autres pairs de se connecter à CE pair via TCP/TLS*/
    state->tcp_listen_socket = socket(AF_INET6, SOCK_STREAM, 0);
    if(state->tcp_listen_socket < 0)
    {
        perror("network_init socket TCP listen");
        state->tcp_listen_socket = -1; /*Marquer comme invalide*/
        fprintf(stderr, "network_init: Échec critique: Impossible de créer le socket TCP d'écoute. Nettoyage et sortie.\n");
        if (state->udp_socket_general != -1)
        {
            close(state->udp_socket_general);
            state->udp_socket_general = -1;
        }
        return -1; /*Échec critique*/
    }
    printf("network_init: Socket TCP d'écoute créé (descripteur %d).\n", state->tcp_listen_socket);
    /*Mettre le socket TCP en mode écoute pour accepter les connexions entrantes*/
    if (listen(state->tcp_listen_socket, 10) < 0)
    { /*Le backlog (10) est le nombre max de connexions en attente*/ 
        perror("network_init listen TCP");
        close(state->tcp_listen_socket);
        state->tcp_listen_socket = -1;
        if (state->udp_socket_general != -1)
        {
            close(state->udp_socket_general);
            state->udp_socket_general = -1;
        }
        return -1; // Échec critique
    }
    printf("network_init: Socket TCP d'écoute mis en mode écoute.\n");
    printf("network_init: Initialisation réseau terminée avec succès.\n");
    /*Succès de l'initialisation réseau*/
    return 0;
}

void* listen_for_peers(void *arg) {
    AppState *state = (AppState*)arg;

    /*Utiliser le socket stocké dans l'état (créé par network_init)*/
    int sockfd = state->udp_socket_general;
    if (sockfd == -1)
    {
        fprintf(stderr, "listen_for_peers: Erreur: Socket UDP général non initialisé dans l'état. Quitter le thread.\n");
        pthread_exit(NULL);
        return NULL;
    }

    printf("Thread d'écoute UDP démarré, écoute sur socket %d.\n", sockfd);

    char buffer[BUFFER_SIZE];

    while(1)
    {
        struct sockaddr_in6 src_addr;
        socklen_t tailleaddr = sizeof(src_addr);
        ssize_t taille = recvfrom(sockfd, buffer, BUFFER_SIZE, 0, (struct sockaddr *)&src_addr, &tailleaddr);

        /*Gérer les erreurs recvfrom*/
        if (taille < 0)
        {
            if (errno == EINTR)
            {
                continue; /*interrompu par signal, juste reprendre la boucle*/
            }
            perror("listen_for_peers recvfrom error");
            /*Si jamais on veut arrêter en cas d'erreur persistante, alors on peut mettre break à la place de continue*/
            // break; // Sortir de la boucle en cas d'erreur grave
            continue; // Pour la plupart des erreurs, on peut tenter de continuer
        }
        else if (taille == 0)
        {
            /*recvfrom retourne 0 pour UDP si l'expéditeur envoie un datagramme de taille 0.
            Dans ce protocole, cela ne devrait pas arriver. On ignore.*/
            fprintf(stderr, "listen_for_peers: recvfrom a reçu 0 octet (datagramme vide ?). Ignoré.\n");
            continue;
        }

        if (taille < 1)
        {
             fprintf(stderr, "listen_for_peers: Message reçu trop court (%zu octets). Ne peut pas lire le code. Ignoré.\n", taille);
             continue;
        }
        uint8_t code = buffer[0];

        /*On renvoie à un handler en fonction du code reçu*/
        /*Chaque handler est responsable de parser le reste du message et de mettre à jour l'état si nécessaire.*/
        switch (code)
        {
            case CODE_JOIN_REQ: // CODE 3
                /*Le handler gère le print "Reçu message CODE_JOIN_REQ (3)"*/
                handle_join_request(state, buffer, taille, &src_addr);
                break;

            case CODE_JOIN_RESP: // CODE 4
                /*Le handler gère le print "Reçu message CODE_JOIN_RESP (4)"*/
                handle_join_response(state, buffer, taille, &src_addr);
                break;
            
            case CODE_AUCTION_START: /*CODE 8*/ 
                // handle_auction_start(state, buffer, taille, &src_addr);
                break;

            case CODE_BID: /*CODE 9*/
                // handle_bid(state, buffer, taille, &src_addr);
                break;

            case CODE_BID_VALIDATED: /*CODE 10*/
            //  handle_bid_validated(state, buffer, taille, &src_addr);
                break;

            case CODE_CONS: /*CODE 2 (Consensus Final)*/
                // handle_consensus(state, buffer, taille, &src_addr);
                break;

            // ... Ajoutez tous les codes UDP que votre protocole utilise ...

            default:
                /*Si le code n'est pas reconnu, afficher un avertissement*/
                fprintf(stderr, "listen_for_peers: Reçu message avec code inconnu (%u, taille %zu). Ignoré.\n", code, taille);
                break;
        }
    }
    pthread_exit(NULL);
    return NULL;
}

/*Les fonctions handler qui suivent sont là pour gérer le traitement de messages spécifiques*/
/**
 @brief Gère la réception d'un message CODE 3 (Join Request).
 Un pair existant recevant ce message doit répondre avec un CODE 4 s'il est prêt.
 @param state Pointeur vers l'état global de l'application.
 @param buffer Buffer contenant le message reçu (commence par CODE_JOIN_REQ).
 @param len Longueur du message reçu.
 @param src_addr Adresse de l'expéditeur.
*/

void handle_join_request(AppState* state, const uint8_t* buffer, size_t len, const struct sockaddr_in6* src_addr)
{
    printf("handle_join_request: Reçu message CODE_JOIN_REQ (3).\n");

    /*Vérification que le message CODE 3 a la taille attendue (1 octet)*/
    if (len != 1)
    {
        fprintf(stderr, "handle_join_request: Message CODE 3 de taille incorrecte (%zu, attendu 1). Ignoré.\n", len);
        return;
    }

    if (state->join_state == JOIN_STATE_JOINED)
    {
        printf("handle_join_request: Je suis joint, j'envoie une réponse CODE_JOIN_RESP (4).\n");

        /*build_join_response de msg.c pour construire le message CODE 4*/
        uint8_t reponse[21]; /*CODE(1) + ID(2) + IP(16) + PORT(2) = 21*/
        size_t rep_len = build_join_response(state->moi.id, (const uint8_t*)&state->moi.ip_perso, state->moi.port_perso, reponse);
        /*l'id, l'ip binaire, le port personnel (host byte order, build_ le convertit)*/

        /*On vérifie la taille*/
        if (rep_len == 21)
        {
            /*La destination est l'adresse source de la requête CODE 3*/
            struct sockaddr_in6 dest_addr = *src_addr;

            /*Si l'adresse source est link-local, on doit s'assurer que le scope_id est correct pour l'envoi*/
            if (IN6_IS_ADDR_LINKLOCAL(&dest_addr.sin6_addr))
            {
                dest_addr.sin6_scope_id = if_nametoindex("eth0"); /*vérifie si l'interface est corrrecte*/
                if (dest_addr.sin6_scope_id == 0)
                {
                    fprintf(stderr, "handle_join_request: Impossible de déterminer l'index de l'interface pour réponse link-local.\n");
                    /*Bon là jsp quoi faire dans ce cas*/
                }
            }
            /*On utilise le socket UDP général de l'état pour envoyer la réponse*/
            if(sendto(state->udp_socket_general, reponse, rep_len, 0, (struct sockaddr*)&dest_addr, sizeof(dest_addr)) < 0)
            {
                perror("handle_join_request sendto CODE_JOIN_RESP");
            }
            else
            {
                 printf("handle_join_request: Réponse CODE_JOIN_RESP (4) envoyée à [%s]:%u\n",
                        inet_ntop(AF_INET6, &dest_addr.sin6_addr, (char[INET6_ADDRSTRLEN]){0}, INET6_ADDRSTRLEN),
                        ntohs(dest_addr.sin6_port));
            }
        }
        else
        {
            fprintf(stderr, "handle_join_request: Erreur: Taille du message CODE 4 construit incorrecte (%zu).\n", rep_len);
        }
    }
    else
    {
         printf("handle_join_request: Je ne suis pas en état de répondre aux demandes de jonction (état: %u). Ignoré.\n", state->join_state);
         // Le pair n'est pas prêt à accepter de nouveaux pairs (il est en train de joindre lui-même, ou n'est pas le premier).
    }
}

/**
 * @brief Gère la réception d'un message CODE 4 (Join Response).
 * Ce message est pertinent si le pair est en train de joindre et attend des réponses.
 *
 * @param state Pointeur vers l'état global de l'application.
 * @param buffer Buffer contenant le message reçu (commence par CODE_JOIN_RESP).
 * @param len Longueur du message reçu.
 * @param src_addr Adresse de l'expéditeur.
 */

void handle_join_response(AppState* state, const uint8_t* buffer, size_t len, const struct sockaddr_in6* src_addr)
{
    /*Ce message n'est pertinent que si le pair est en état d'attente de réponse CODE 4
    On accepte SENT_REQUEST car le timeout t2s peut ne pas être encore écoulé au moment de la réception.*/
    if (state->join_state == JOIN_STATE_WAITING_RESP || state->join_state == JOIN_STATE_SENT_REQUEST)
    {
         printf("handle_join_response: Je suis en état de jonction, je traite ce CODE 4.\n");

        /*Vérifiez la taille du message reçu (CODE 4 = 21 octets)*/
        if (len != 21)
        {
            fprintf(stderr, "handle_join_response: Message CODE 4 de taille incorrecte (%zu, attendu 21). Ignoré.\n", len);
            return;
        }
        /*parse_join_response de msg.c*/
        uint16_t resp_id;
        uint8_t resp_ip_raw[16];
        uint16_t resp_port;

        int parse_rc = parse_join_response(buffer, len, &resp_id, resp_ip_raw, &resp_port);
        if (parse_rc == 0)
        {
            printf("handle_join_response: Parsed CODE 4: ID=%u, IP=%s, Port=%u\n",
                    resp_id,
                    inet_ntop(AF_INET6, resp_ip_raw, (char[INET6_ADDRSTRLEN]){0}, INET6_ADDRSTRLEN),
                    ntohs(resp_port));

            /**Important :** il faut que le pair ignore sa propre réponse CODE 4 si jamais il la reçoit en multicast.
            il peut donc comparer l'ID ou l'adresse source avec sa propre PeerInfo.
            Une vérification plus robuste (IP et Port) en plus de l'ID.*/
            if (resp_id == state->moi.id && memcmp(&src_addr->sin6_addr, &state->moi.ip_perso, 16) == 0 && ntohs(src_addr->sin6_port) == ntohs(state->moi.port_perso))
            {
                printf("handle_join_response: Reçu ma propre réponse CODE 4. Ignoré.\n");
                return;
            }
            /*Ajoutez ce pair potentiel à la liste temporaire dans l'état*/
            struct in6_addr resp_ip_bin;
            memcpy(&resp_ip_bin, resp_ip_raw, sizeof(struct in6_addr));

            /*fonction de state.c pour ajouter le pair potentiel*/
            int add_rc = add_potential_peer(&state->potential_peers, resp_id, &resp_ip_bin, resp_port);
            if (add_rc == 0)
            {
                printf("handle_join_response: Pair potentiel ID %u ajouté à la liste temporaire. Total: %zu\n", resp_id, state->potential_peers.count);
            }
            else if (add_rc == -2)
            {
                printf("handle_join_response: Pair potentiel ID %u déjà dans la liste temporaire.\n", resp_id);
            }
            else
            {
                fprintf(stderr, "handle_join_response: Erreur lors de l'ajout du pair potentiel ID %u (%d).\n", resp_id, add_rc);
            }
        }
        else/*Message invalide, ignoré.*/
        {
            fprintf(stderr, "handle_join_response: Erreur lors du parsing du message CODE 4 (%d). Ignoré.\n", parse_rc);
            
        }
    }
    else
    {
         /*Le pair n'est pas en train de joindre (état: IDLE, JOINED, etc.). Un message CODE 4 reçu est inattendu.
         On l'ignore.*/
         printf("handle_join_response: Je ne suis pas en état d'attente de réponse CODE 4 (état: %u). Ignoré.\n", state->join_state);
    }
}


// void* listenToPeer(void *arg)
// {
//     AppState *state = (AppState*)arg;/*On caste le pointeur d'état reçu en argument*/
//     int sockfd;
//     struct sockaddr_in6 addr;
//     struct ipv6_mreq mreq;
//     char buffer[BUFFER_SIZE];
//     sockfd = socket(AF_INET6, SOCK_DGRAM, 0);
//     if(sockfd < 0)
//     {
//         perror("listenToPeer socket");
//         pthread_exit(NULL);
//     }
//     int ok =1;
//     setsockopt(sockfd, SOL_SOCKET, SO_REUSEADDR, &ok, sizeof(ok));
//     memset(&addr, 0, sizeof(addr));
//     addr.sin6_family = AF_INET6;
//     addr.sin6_port=htons(state->moi.port_perso);
//     addr.sin6_addr=in6addr_any;
//     if(bind(sockfd, (struct sockaddr*)&addr, sizeof(addr)) < 0)
//     {
//         perror("listenToPeer bind");
//         close(sockfd);
//         pthread_exit(NULL);
//     }
//     /*On stocke le descripteur de socket dans l'état pour pouvoir l'utiliser facilement plus tard*/
//     state->udp_socket_general = sockfd;
//     /*S'abonner au groupe multicast (adresse liaison)*/
//     memcpy(&mreq.ipv6mr_multiaddr, &(state->adresse_liaison_multicast), sizeof(struct in6_addr));
//     mreq.ipv6mr_interface = if_nametoindex("eth0"); /*On met l'interface par défaut pour l'instant*/
//     if(setsockopt(sockfd, IPPROTO_IPV6, IPV6_JOIN_GROUP, &mreq, sizeof(mreq)) < 0)
//     {
//         perror("listentToPeer setsockopt abonnement multicast");
//         fprintf(stderr, "Avertissement : Impossible de rejoindre le groupe multicast de la liaison.\n");
//     }
//     printf("Thread d'écoute UDP lancé sur [::]:%u, abonné à %s\n", ntohs(state->moi.port_perso), ADDR_MULTICAST);


//     while(1)
//     {
//         struct sockaddr_in6 src_addr;
//         socklen_t tailleaddr = sizeof(src_addr);
//         ssize_t taille = recvfrom(sockfd, buffer, BUFFER_SIZE, 0, (struct sockaddr *)&src_addr, &tailleaddr);

//         if (taille<0){
//             perror("listen_for_peers recvfrom");
//             continue; /*Pour l'instant, on continue pour les erreurs non critiques*/
//         } else if (taille == 0) {
//              /*Connexion fermée côté distant pour UDP, ne devrait normalement pas arriver pour DGRAM*/
//             fprintf(stderr, "recvfrom a reçu 0 octet, comportement inattendu pour UDP.\n");
//             continue;
//         }

//         printf("Message reçu de [%s]:%u (%zu octets)\n",
//                inet_ntop(AF_INET6, &src_addr.sin6_addr, (char[INET6_ADDRSTRLEN]){0}, INET6_ADDRSTRLEN),
//                ntohs(src_addr.sin6_port), taille);


//         uint8_t code = buffer[0];

//         if (code == CODE_JOIN_REQ)/*CODE=3: demande d'entrée*/
//         {
//              printf("Reçu message CODE_JOIN_REQ (3)\n");
//             // Ici, vous devrez gérer le cas où CE pair (l'instance actuelle)
//             // EST un pair existant recevant une demande de jonction.
//             // Vous devriez vérifier si vous êtes déjà joint au système.

//             // Si vous êtes joint, construisez et envoyez un message CODE 4.
//             // Utilisez build_join_response de msg.c et les informations de VOTRE pair (state->moi).

//             // Exemple basique (à remplacer par la vraie logique):
//             if (state->join_state == JOIN_STATE_JOINED) { // Si ce pair est déjà joint au système
//                  printf("Je suis déjà joint, j'envoie une réponse CODE_JOIN_RESP (4).\n");
//                 uint8_t reponse[21]; // CODE(1) + ID(2) + IP(16) + PORT(2) = 21
//                 // Utilisez build_join_response de msg.c
//                 size_t rep_len = build_join_response(
//                     state->moi.id,
//                     (const uint8_t*)&state->moi.ip_perso, // IP binaire
//                     state->moi.port_perso,
//                     reponse
//                 );

//                 if (rep_len == 21) { // Vérifiez la taille attendue
//                      // L'adresse source de la requête est la destination de la réponse
//                      // Assurez-vous que le scope_id est correct pour les adresses link-local si nécessaire
//                      src_addr.sin6_scope_id = if_nametoindex("eth0"); // Assurez-vous que l'interface est correcte

//                     if (sendto(sockfd, reponse, rep_len, 0, (struct sockaddr*)&src_addr, tailleaddr) < 0) {
//                         perror("listen_for_peers sendto CODE_JOIN_RESP");
//                     } else {
//                          printf("Réponse CODE_JOIN_RESP (4) envoyée à [%s]:%u\n",
//                                 inet_ntop(AF_INET6, &src_addr.sin6_addr, (char[INET6_ADDRSTRLEN]){0}, INET6_ADDRSTRLEN),
//                                 ntohs(src_addr.sin6_port));
//                     }
//                 } else {
//                     fprintf(stderr, "Erreur: Taille du message CODE 4 construit incorrecte (%zu).\n", rep_len);
//                 }
//             } else {
//                  printf("Je ne suis pas encore joint, j'ignore la demande CODE 3.\n");
//                  // Si le pair n'est pas encore joint, il peut ignorer les requêtes de jonction.
//             }
//         }

//         // Le pair qui TENTE de rejoindre reçoit des réponses CODE 4 en multicast
//         // Ce traitement devrait idéalement se faire dans try_join_auction_system
//         // ou une fonction appelée par la boucle principale, pas dans ce thread d'écoute générale,
//         // mais pour l'instant, on le laisse ici pour montrer qu'il reçoit des messages CODE 4.
//         // **Attention :** Ce pair reçoit aussi ses PROPRES messages CODE 4 s'il en envoie !
//         // Il faut donc les ignorer ou les gérer correctement.
//         else if (code == CODE_JOIN_RESP){ // CODE=4: réponse à demande d'entrée
//              printf("Reçu message CODE_JOIN_RESP (4)\n");

//              // Vérifiez si CE message est destiné à CE pair en comparant les adresses.
//              // Si le pair est en état JOIN_STATE_WAITING_RESP, il doit traiter ce message.
//              if (state->join_state == JOIN_STATE_WAITING_RESP) {
//                  printf("Je suis en état d'attente de réponse, je traite ce CODE 4.\n");
//                  // Utilisez parse_join_response de msg.c
//                  uint16_t resp_id;
//                  uint8_t resp_ip_raw[16];
//                  uint16_t resp_port;

//                  // Vérifiez la taille du message reçu avant de parser (taille minimale + payload si variable)
//                  // Le sujet dit que CODE 4 fait 1 (code) + 2 (ID) + 16 (IP) + 2 (PORT) = 21 octets
//                  if (taille == 21) {
//                      int parse_rc = parse_join_response(buffer, taille, &resp_id, resp_ip_raw, &resp_port);

//                      if (parse_rc == 0) {
//                           printf("Parsed CODE 4: ID=%u, IP=%s, Port=%u\n",
//                                  resp_id,
//                                  inet_ntop(AF_INET6, resp_ip_raw, (char[INET6_ADDRSTRLEN]){0}, INET6_ADDRSTRLEN),
//                                  resp_port);

//                          // Ajoutez ce pair potentiel à la liste temporaire dans l'état
//                          struct in6_addr resp_ip_bin;
//                          memcpy(&resp_ip_bin, resp_ip_raw, sizeof(struct in6_addr));

//                          // Utilisez la fonction de state.c pour ajouter le pair potentiel
//                          int add_rc = add_potential_peer(
//                              &state->potential_peers,
//                              resp_id,
//                              &resp_ip_bin,
//                              resp_port
//                          );

//                          if (add_rc == 0) {
//                               printf("Pair potentiel ID %u ajouté à la liste temporaire.\n", resp_id);
//                          } else if (add_rc == -2) {
//                               printf("Pair potentiel ID %u déjà dans la liste temporaire.\n", resp_id);
//                          } else {
//                              fprintf(stderr, "Erreur lors de l'ajout du pair potentiel ID %u.\n", resp_id);
//                          }

//                          // Dans la version simple, on connecte tout de suite au premier.
//                          // Pour une version plus robuste, il faudrait collecter toutes les réponses pendant t2s.
//                          // Pour l'instant, si on reçoit une réponse, on peut passer à l'étape de sélection/connexion.
//                          // Cela implique de signaler à la boucle principale que des réponses ont été reçues.
//                          // Une manière simple pour l'instant est de changer l'état de jonction.

//                          // state->join_state = JOIN_STATE_SELECTING_Q; // Signalez qu'on a reçu des réponses
//                          // La boucle principale gérera le reste.

//                      } else {
//                          fprintf(stderr, "Erreur lors du parsing du message CODE 4 (%d).\n", parse_rc);
//                      }
//                  } else {
//                      fprintf(stderr, "Reçu message CODE 4 avec taille incorrecte (%zu, attendu 21).\n", taille);
//                  }

//              } else {
//                   printf("Je ne suis pas en état d'attente de réponse, j'ignore ce CODE 4.\n");
//              }
//         }

//         // Ajoutez ici la gestion des autres codes de message UDP (enchères, etc.)
//         // else if (code == CODE_AUCTION_START) { ... }
//         // else if (code == CODE_BID) { ... }
//         // ...

//         else {
//              printf("Reçu message avec code inconnu (%u).\n", code);
//         }
//     }
//     // Le code ne devrait normalement jamais atteindre ce point dans un programme serveur typique
//     // close(sockfd); // Ne fermez pas le socket ici si il est géré par l'état
//     // pthread_exit(NULL);
//     return NULL; // Ajout d'un retour NULL pour satisfaire le prototype
// }