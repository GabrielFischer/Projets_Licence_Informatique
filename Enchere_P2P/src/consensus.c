#include <stdio.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include "msg.h"
#include "keys.h"
#include "enchere.h"

// Clé de signature bidon (fake key)
static uint8_t dummy_sk[MSG_SIG_LEN] = {0};

static int sock_serveur = -1;  // Pour le superviseur (écoute les CODE=1)
static int sock_client = -1;   // Pour les clients (envoie CODE=1 et reçoit CODE=2)

//-------------------------------------------------------------------------------
// Initialisation des sockets utilisées par le superviseur et les clients
//-------------------------------------------------------------------------------
int init_consensus_sockets() {
    // Socket pour le serveur (superviseur)
    sock_serveur = socket(AF_INET6, SOCK_DGRAM, 0);
    if (sock_serveur < 0) {
        perror("socket serveur consensus");
        return -1;
    }

    // Socket pour le client (non-superviseur)
    sock_client = socket(AF_INET6, SOCK_DGRAM, 0);
    if (sock_client < 0) {
        perror("socket client consensus");
        close(sock_serveur);
        return -1;
    }

    // Configuration du socket serveur 
    struct sockaddr_in6 addr_serveur = {0};
    addr_serveur.sin6_family = AF_INET6;
    addr_serveur.sin6_port = htons(port_consensus);  // Port différent du multicast
    addr_serveur.sin6_addr = in6addr_any;  // Écoute sur toutes les interfaces

    if (bind(sock_serveur, (struct sockaddr*)&addr_serveur, sizeof(addr_serveur))) {
        perror("bind serveur consensus");
        close(sock_serveur);
        close(sock_client);
        return -1;
    }

    return 0;
}


//-------------------------------------------------------------------------------
// Fonction serveur de consensus (superviseur)
//-------------------------------------------------------------------------------
/**
 * Fonction serveur de consensus (superviseur)
 * Recolte les validations (CODE=1), puis envoie le message de consensus (CODE=2) en multidiffusion.
 */
void consensus_serveur(uint16_t vendeur_id, uint32_t numero_vente, uint32_t prix) {
    if (sock_serveur < 0) {
        fprintf(stderr, "Erreur: socket consensus non initialisé\n");
        return;
    }

    printf("[serveur] Démarrage du consensus pour vente #%u au prix %u\n", numero_vente, prix);

    // Préparation du payload à valider (bytes id 2 + bytes num 4 + 4 bytes prix)
    //construit tableau avec id vendeur, num vente et prix 
    //en Big endian
    //on fait signer après 
    uint8_t payload[10];
    uint8_t *pp = payload;
    *(uint16_t*)pp = htons(vendeur_id); pp += 2;
    *(uint32_t*)pp = htonl(numero_vente); pp += 4;
    *(uint32_t*)pp = htonl(prix); pp += 4;

    // préparer le stockage des validations reçues
    uint16_t peer_ids[MAX_PEERS];
    uint8_t peer_sigs_data[MAX_PEERS][MSG_SIG_LEN];
    const uint8_t *peer_sigs[MAX_PEERS];
    uint8_t nb_valid = 0;
    struct sockaddr_in6 src;
    socklen_t src_len = sizeof(src);
    uint8_t buf[512];

    // Timeout pour ne pas bloquer 
    struct timeval tv = {5, 0}; 
    setsockopt(sock_serveur, SOL_SOCKET, SO_RCVTIMEO, &tv, sizeof(tv));

    //Boucle de Reception
    while (nb_valid < peer_count - 1) {
        //!!!Potentiel point d'erreur!!!
        //len est petit et donc parse plane car les tailles sont pas bonnes
        ssize_t len = recvfrom(sock_serveur, buf, sizeof(buf), 0,(struct sockaddr*)&src, &src_len);
        
        char ipstr[INET6_ADDRSTRLEN];
        inet_ntop(AF_INET6, &src.sin6_addr, ipstr, sizeof(ipstr));
        uint16_t port = ntohs(src.sin6_port);
        printf("[recvfrom] %zd octets reçus de %s:%u\n", len, ipstr, port);
        
        /*
        printf("LENNNNNNNN %lu\n", len);
        printf("!!!!!!%"PRIu8"\n",buf[0] );
        */
        if (len <= 0){ 
            break;
        }
        if(buf[0]==CODE_VAL){
            printf("A");
        }
        else{
            printf("B");
        }

        // parser la validation
        uint16_t vid;
        uint8_t pl[16], pl_len, sig[MSG_SIG_LEN];
        if (parse_validation(buf, len, &vid, pl, &pl_len, sig) == 0) {
            peer_ids[nb_valid] = vid;
            memcpy(peer_sigs_data[nb_valid], sig, MSG_SIG_LEN);
            peer_sigs[nb_valid] = peer_sigs_data[nb_valid];
            nb_valid++;
            printf("[serveur] Validation #%u reçue de pair %u\n", nb_valid, vid);
        }
    }
    //on passe ici quand on a n-1 validations (on s'exclue)
    printf("[serveur] %u validations reçues, génération du message de consensus...\n", nb_valid);

    // Construire et envoyer CODE=2
    uint8_t out[512];
    size_t out_len = build_consensus(
        payload, sizeof(payload),
        vendeur_id, dummy_sk,
        peer_ids, peer_sigs,
        nb_valid, out
    );

    // Envoi multidiffusion sur ADDR_ENCHERES:8080
    struct sockaddr_in6 dest = {0};
    dest.sin6_family = AF_INET6;
    dest.sin6_port   = htons(8080);
    inet_pton(AF_INET6, ADDR_ENCHERES, &dest.sin6_addr);
    
    ssize_t sent = sendto(sock_client, out, out_len, 0, (struct sockaddr*)&dest, sizeof(dest));
    printf("[serveur] Message consensus envoyé (%zd octets) en multidiffusion\n", sent);
}


//-------------------------------------------------------------------------------
// Fonction client de consensus (non-superviseur)
//-------------------------------------------------------------------------------
/**
 * Fonction client de consensus (non-superviseur)
 * Envoie sa validation (CODE=1) et attend le message de consensus (CODE=2).
 */
void consensus_client(uint16_t sup_id, uint32_t numero_vente, uint32_t prix) {

    if (sock_client < 0) {
        fprintf(stderr, "Erreur: socket consensus non initialisé\n");
        return;
    }

    printf("[client] Préparation de la validation pour vente #%u au prix %u\n", numero_vente, prix);

    // Reconstruction du payload signé
    //Sera utilisé pour construire le code 1
    uint8_t payload[10];
    uint8_t *pp = payload;
    *(uint16_t*)pp = htons(sup_id); pp += 2;
    *(uint32_t*)pp = htonl(numero_vente); pp += 4;
    *(uint32_t*)pp = htonl(prix); pp += 4;

    // Construire CODE_VAL
    uint8_t buf_val[128];
    size_t len_val = build_validation(
        peer_list[0].id, //"c'est moi qui signe!"
        payload, sizeof(payload),
        dummy_sk,
        buf_val
    );

    printf("[client] Validation générée (%zu octets)\n", len_val);

    // Résolution de l'adresse du superviseur
    //à partir du sup_id donné en argument
    struct sockaddr_in6 sup_addr = {0};
    sup_addr.sin6_family = AF_INET6;
    for(int i = 0; i < peer_count; i++) {
        if (peer_list[i].id == sup_id) {
            inet_pton(AF_INET6, peer_list[i].ip, &sup_addr.sin6_addr);
            sup_addr.sin6_port = htons(peer_list[i].PORT);
            break;
        }
    }

    //log avant envoi
    char _ipbuf[INET6_ADDRSTRLEN];
    printf("[client] Envoi de la validation au superviseur %u [%s]:%u\n",
       sup_id,
       inet_ntop(AF_INET6, &sup_addr.sin6_addr, _ipbuf, sizeof _ipbuf),
       ntohs(sup_addr.sin6_port));

    // Envoi unicast CODE_VAL
    //!Ici aussi peut poser problème
    sleep(5);
    ssize_t sent = sendto(sock_client, buf_val, len_val, 0,(struct sockaddr*)&sup_addr, sizeof(sup_addr));
    printf("[client] %zd octets envoyés au superviseur\n", sent);
    printf("[client] CODE_VAL envoyé à %u\n", sup_id);


    // Attendre CODE=2
    uint8_t buf2[512];
    while (1) {
        ssize_t len2 = recvfrom(sock_client, buf2, sizeof(buf2), 0, NULL, NULL);
        if (len2 > 0 && buf2[0] == CODE_CONS) {
            printf("[client] Message de consensus reçu (%zd octets)\n", len2);
            
            //variables pour récupérer tout ce que parse va extraire
            uint16_t out_sup; //id superviseir
            uint8_t pl2[16], pl2_len;
            uint8_t sup_sig[MSG_SIG_LEN];
            uint16_t peer_ids_rcv[MAX_PEERS];
            uint8_t *peer_sigs_rcv[MAX_PEERS]; //pointeur vers les signatures
            uint8_t nb_rcv;
            int ok=parse_consensus(buf2, len2,&out_sup,pl2, &pl2_len,sup_sig,peer_ids_rcv,peer_sigs_rcv,&nb_rcv);
            if(ok==0){
                //"désérialise" payload
                const uint8_t *q = pl2; //pointeur vers payload
                uint16_t vid   = ntohs(*(uint16_t*)pl2);    //id en host
                uint32_t nv    = ntohl(*(uint32_t*)(pl2+2)); //num vente
                uint32_t pr    = ntohl(*(uint32_t*)(pl2+6)); //prix
                printf("[client] Consensus final: superviseur=%u, vente=%u, prix=%u, %u validations\n",out_sup, nv, pr, nb_rcv);

                //mise a jour dela structure d'enchere
                pthread_mutex_lock(&mutex_encheres);
                for (int i = 0; i < compteur_encheres; i++) {
                    //cherche enchère correspondante 
                    if (liste_encheres[i].numero_vente == nv && liste_encheres[i].vendeur_id == vid) {
                        //update new prix et enchereur
                        liste_encheres[i].prix_actuel = pr;
                        liste_encheres[i].meneur_actuel = out_sup;
                        break;
                    }
                }
                pthread_mutex_unlock(&mutex_encheres);
                break;
            }
        }
    }
}