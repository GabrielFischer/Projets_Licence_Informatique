#include "../include/msg.h"

//---------------------- FONCTIONS HELPER -------------------------------------
// Helper pour écrire un octet v à l'adresse pointé par *p et avancer le pointeur
static void put_u8(uint8_t **p, uint8_t v) {
    *(*p)++ = v;
}

// Helper pour écrire 16 bits (v) à l'adresse pointé par *p et avancer le pointeur
static void put_u16(uint8_t **p, uint16_t v) {
    uint16_t be = htons(v);         // conversion en network -> pour les build dessous
    memcpy(*p, &be, sizeof(be));    // copie des 2 octets
    *p += sizeof(be);               // avance pointeur
}
// Helper pour lire un entier 16 bits en big-endian depuis *p et avance p
uint16_t get_u16(const uint8_t **p) {
    uint16_t be;
    memcpy(&be, *p, sizeof(be));    //lecture 2 octets
    *p += sizeof(be);               //avance pointeur
    return ntohs(be);               //conversion en host -> pour parsing
}
//----------------------------------FUNCTIONS--------------------------------------------



//==================================CODE 1: Validation===============================================
/*Construit message de validation CODE 1
 [1 byte CODE_VAL]
 [2 byte peer_id]
 [1 byte payload_len]
 [payload_len bytes payload]
 [1 byte sig_len]
 [MSG_SIG_LEN bytes signature ED25519]
 -> return taille totale du message 
*/
size_t build_validation(uint16_t peer_id,
                        const uint8_t *payload,uint8_t payload_len,
                        const uint8_t sk[MSG_SIG_LEN],
                        uint8_t *out_buf){
    uint8_t *p = out_buf;
    put_u8(&p, CODE_VAL);               //code (1)
    put_u16(&p, peer_id);               //id du pair qui valide le message
    put_u8(&p, payload_len);            
    memcpy(p, payload, payload_len);    //données à signer -> HERE
    p += payload_len;

    //signature ED25519 du message
    size_t siglen = MSG_SIG_LEN;
    put_u8(&p, (uint8_t)siglen);        //longueur de la signature //HERE WHY USE U8
    if (ed25519_sign(payload, payload_len, p, &siglen, sk) != 0) {
        //on utilise fake key car pas réussi à implementer les clés
        memset(p, 0, MSG_SIG_LEN);
        siglen = MSG_SIG_LEN;
    }
    p += siglen;
    return p - out_buf;                 //taille du message dans out_buf
}

/*Extrait et vérifie un message CODE_VAL
-> retourne 0 si succes, -1 si erreur
*/
int parse_validation(const uint8_t *buf, size_t buf_len,
                     uint16_t *out_id,
                     uint8_t *out_payload, uint8_t *out_len,
                     uint8_t out_sig[MSG_SIG_LEN]){
    const uint8_t *p = buf;
    // Vérifier taille minimale du message HERE what is minimal 
    if (buf_len < 1 + 2 + 1 + 1 + MSG_SIG_LEN) return -1;
    
    if (*p++ != CODE_VAL) return -2;    //code
    *out_id = get_u16(&p);              //id
    *out_len = *p++;                    //longueur payload
    memcpy(out_payload, p, *out_len);   //payload extrait
    p += *out_len;

    //Signature
    uint8_t lsig = *p++;                //longueur de la signature
    if (lsig != MSG_SIG_LEN) return -3;
    memcpy(out_sig, p, MSG_SIG_LEN);    //extraction de la signature
    return 0;
}


//==================================CODE 2: Consensus===============================================
/*Construit un message de Code 2
[1 byte CODE_CONS]
[2 bytes sup_id]
[1 byte payload_len]
[payload_len bytes payload]
[1 byte sig_len (=MSG_SIG_LEN)]
[MSG_SIG_LEN bytes signature ED25519 du superviseur]
[1 byte nb_peers]
Pour chaque validation:
    [2 bytes peer_id]
    [1 byte sig_len (=MSG_SIG_LEN)]
    [MSG_SIG_LEN bytes signature de chaque peer]
-> return taille totale du message ou (size_t)-1 si échec de signature
*/
size_t build_consensus(const uint8_t *payload, uint8_t payload_len,
                       uint16_t sup_id, 
                       const uint8_t sk[MSG_SIG_LEN],
                       const uint16_t peer_ids[], 
                       const uint8_t *peer_sigs[],
                       uint8_t nb_peers, 
                       uint8_t *out_buf){
    uint8_t *p = out_buf;
    put_u8(&p, CODE_CONS);              //code
    put_u16(&p, sup_id);                //id superviseur
    put_u8(&p, payload_len);            //longueur payload 
    memcpy(p, payload, payload_len);    //payload 
    p += payload_len;

    //Signature du superviseur
    size_t slen = MSG_SIG_LEN;
    put_u8(&p, (uint8_t)slen);           //longueur de la signature
    if (ed25519_sign(payload, payload_len, p, &slen, sk) != 0) {
        //on utilise fake key car pas réussi à implementer les clés
        memset(p, 0, MSG_SIG_LEN);
        slen = MSG_SIG_LEN;
        return -1;
    }
    p += slen;

    put_u8(&p, nb_peers);               // nombre de validations reçues

    // validations des messages reçus (peer_id + LSIG + SIG)
    for (uint8_t i = 0; i < nb_peers; i++) {
        put_u16(&p, peer_ids[i]);           //id de chaque peer
        put_u8(&p, MSG_SIG_LEN);            
        memcpy(p, peer_sigs[i], MSG_SIG_LEN);  //signature du peer
        p += MSG_SIG_LEN;
    }
    return (size_t)(p - out_buf);
}

/*Extrait et vérifie un message CODE_CONS
-> retourne 0 si succes, -1 si erreur
*/
int parse_consensus(const uint8_t *buf, size_t buf_len,
                    uint16_t *out_sup_id,
                    uint8_t *out_payload, uint8_t *out_len,
                    uint8_t out_sup_sig[MSG_SIG_LEN],
                    uint16_t out_peer_ids[], uint8_t *out_peer_sigs[],
                    uint8_t *out_nb_peers){
    const uint8_t *p = buf;
    // Vérifier taille minimale du message avant d'accéder
    if (buf_len < 1 + 2 + 1 + 1 + MSG_SIG_LEN + 1) return -1;
    
    if (*p++ != CODE_CONS) return -2;       //code
    *out_sup_id = get_u16(&p);              //id superviseur
    *out_len = *p++;
    memcpy(out_payload, p, *out_len);       //payload
    p += *out_len;
    uint8_t ls = *p++;                      
    if (ls != MSG_SIG_LEN) return -3;       //longueur de la singature du superviseur
    memcpy(out_sup_sig, p, MSG_SIG_LEN);    //copier signature
    p += MSG_SIG_LEN;


    *out_nb_peers = *p++;    // nombre de peers validés

    // extraire chaque validation
    for (uint8_t i = 0; i < *out_nb_peers; i++) {
        out_peer_ids[i] = get_u16(&p);       //id du peer
        uint8_t ls2 = *p++;                  
        if (ls2 != MSG_SIG_LEN) return -4;
        out_peer_sigs[i] = (uint8_t*)p;      //pointeur vers signature   
        p += MSG_SIG_LEN;                    //avance pointeur
    }
    return 0;
}

//==================================CODE 3: Demande pour rejoindre ===============================================
/*Construit message pour demander d'entrer dans le système
[1 byte CODE_JOIN_REQ]
*/
size_t build_join_request(uint8_t *out_buf) {
    uint8_t *p = out_buf;
    put_u8(&p, CODE_JOIN_REQ);      //écrit code
    return (size_t)(p - out_buf);  
}

/*Désérialisation du Code 3
-> return 0 si buf[0]==CODE_JOIN_REQ, <0 sinon
*/
int parse_join_request(const uint8_t *buf, size_t buf_len) {
    const uint8_t *p = buf;
    if (buf_len < 1) return -1;                       // message trop petit
    if (*p++ != CODE_JOIN_REQ) return -2;             // verifie bon code
    return 0;                                         // OK: requête reconnue
}

//==================================CODE 4: Réponse à Demande ===============================================
/*Construire message code 4 CODE_JOIN_RESP
[1 byte CODE_JOIN_RESP]  
[2 bytes id du pair]  
[16 bytes adresse IPv6]  
[2 bytes port (network-order)]  
-> retourne taille totale 
*/
size_t build_join_response(uint16_t id,
                           const uint8_t ip[16],
                           uint16_t port,
                           uint8_t *out_buf){
    uint8_t *p = out_buf;
    put_u8(&p, CODE_JOIN_RESP);     //code
    put_u16(&p, id);                //id
    memcpy(p, ip, 16);              //ipv6 
    p += 16;
    put_u16(&p, port);              //port 
    return (size_t)(p - out_buf);
}

/*Désérialisation du Code 4
-> return 0 si succes, <0 si erreur
*/
int parse_join_response(const uint8_t *buf, size_t buf_len,
                        uint16_t *out_id,
                        uint8_t out_ip[16],
                        uint16_t *out_port){
    if (buf_len < 21) return -1; //verif taille -> 1 + 2 + 16 + 2 = 21 octets minimum
    const uint8_t *p = buf;
    if (*p++ != CODE_JOIN_RESP) return -2;  //verif code
    *out_id = get_u16(&p);                  //id
    memcpy(out_ip, p, 16);                  //copie ip
    p += 16;                                
    *out_port = get_u16(&p);                //récupère port
    return 0;
}

//==================================CODE 5, 50 et 51: ===============================================

/*Construit message pour code 5 
[1 byte CODE_JOIN_TCP]  
[2 bytes id]  
[16 bytes IPv6]  
[2 bytes port]  
[113 bytes clé publique ED25519]  
*/
size_t build_join_tcp(uint16_t id,
                      const uint8_t ip[16],
                      uint16_t port,
                      const uint8_t cle[MSG_CLE_LEN],
                      uint8_t *out_buf)
{
    uint8_t *p = out_buf;
    put_u8(&p, CODE_JOIN_TCP);
    put_u16(&p, id);
    memcpy(p, ip, 16);      
    p += 16;
    put_u16(&p, port);
    memcpy(p, cle, MSG_CLE_LEN);
    p += MSG_CLE_LEN;
    return (size_t)(p - out_buf);
}

/*
Désérialisation du Code 5
*/
int parse_join_tcp(const uint8_t *buf, size_t buf_len,
                   uint16_t *out_id,
                   uint8_t out_ip[16],
                   uint16_t *out_port,
                   uint8_t out_cle[MSG_CLE_LEN])
{
    
    if (buf_len < 134) return -1;           // 1 + 2 + 16 + 2 + 113 = 134 octets minimum
    const uint8_t *p = buf;
    if (*p++ != CODE_JOIN_TCP) return -2;
    *out_id = get_u16(&p);
    memcpy(out_ip, p, 16); p += 16;
    *out_port = get_u16(&p);
    memcpy(out_cle, p, MSG_CLE_LEN);
    return 0;
}

//CODE 50: si ID choisit par P est disponible
size_t build_id_ok(uint8_t *out_buf) {
    uint8_t *p = out_buf;
    put_u8(&p, 50);
    return (size_t)(p - out_buf); 
}
int parse_id_ok(const uint8_t *buf, size_t buf_len) {
    if (buf_len < 1) return -1;
    if (buf[0] != 50) return -2;
    return 0;
}

//CODE 51: si ID choisit par P n'est pas disponible
size_t build_new_id(uint16_t new_id, uint8_t *out_buf) {
    uint8_t *p = out_buf;
    put_u8(&p, 51);
    put_u16(&p, new_id);
    return (size_t)(p - out_buf);  
}
int parse_new_id(const uint8_t *buf, size_t buf_len, uint16_t *out_id) {
    if (buf_len < 3) return -1;
    if (buf[0] != 51) return -2;
    const uint8_t *p = buf + 1;
    *out_id = get_u16(&p);
    return 0;
}

//==================================CODE 6: Transmission d'information de nouveau paire===============================================
/*Construction de message pour transmetre infos de un nouveau pair aux anciens.
 * Utilise le même format que CODE_JOIN_TCP.
 */
size_t build_send_new_peer(uint16_t id,
                      const uint8_t ip[16],
                      uint16_t port,
                      const uint8_t cle[MSG_CLE_LEN],
                      uint8_t *out_buf)
{
    uint8_t *p = out_buf;
    put_u8(&p, CODE_TRANSMISSION);
    put_u16(&p, id);
    memcpy(p, ip, 16);      
    p += 16;
    put_u16(&p, port);
    memcpy(p, cle, MSG_CLE_LEN);
    p += MSG_CLE_LEN;
    return (size_t)(p - out_buf);
}

int parse_send_new_peer(const uint8_t *buf, size_t buf_len,
                   uint16_t *out_id,
                   uint8_t out_ip[16],
                   uint16_t *out_port,
                   uint8_t out_cle[MSG_CLE_LEN]){
    if (buf_len < 134) return -1;       // 1 + 2 + 16 + 2 + 113 = 134 octets minimum
    const uint8_t *p = buf;
    if (*p++ != CODE_TRANSMISSION) return -2;
    *out_id = get_u16(&p);
    memcpy(out_ip, p, 16); p += 16;
    *out_port = get_u16(&p);
    memcpy(out_cle, p, MSG_CLE_LEN);
    return 0;
}

//==================================CODE 7: Infos sur le système===============================================
/*Construit un message d'informations système (CODE 7).
[1 byte CODE_INFOS_SYST]
[16 bytes addr_ench]      // adresse IPv6 du groupe d'enchères
[2 bytes port]            // port multicast
[2 bytes nb_pairs]        // nombre total de pairs connus
  Pour chaque pair i:     // infos personnelles des pairs
     [2 bytes peer_id]
     [16 bytes ip]
     [2 bytes port]
     [MSG_CLE_LEN bytes public key]
*/
size_t build_infos_system(const uint8_t addr_ench[16], uint16_t port,
                                  uint16_t nb_pairs,
                                  const uint16_t *ids,
                                  const uint8_t ips[][16],
                                  const uint16_t *ports,
                                  const uint8_t cle_pubs[][113],
                                  uint8_t *out_buf) {
    uint8_t *p = out_buf;
    put_u8(&p, CODE_INFOS_SYST);            //code             
    memcpy(p, addr_ench, 16); p += 16;      // adresse encheres
    put_u16(&p, port);                      // port encheres
    put_u16(&p, nb_pairs);                  // nombre de pairs

    for (uint16_t i = 0; i < nb_pairs; ++i) { //pour ajouter les infos personelles de chaque pair
        put_u16(&p, ids[i]);                    
        memcpy(p, ips[i], 16); p += 16;
        put_u16(&p, ports[i]);
        memcpy(p, cle_pubs[i], 113); p += 113;
    }
    return (size_t)(p - out_buf);  // taille totale
}

int parse_infos_system(const uint8_t *buf, size_t len,
                               uint8_t addr_ench[16], 
                               uint16_t *port,
                               uint16_t *nb_pairs,
                               uint16_t *ids,
                               uint8_t ips[][16],
                               uint16_t *ports,
                               uint8_t cle_pubs[][113]) {
    if (len < 21 || buf[0] != CODE_INFOS_SYST) return -1; //au moins les 1+16+2+2 octets initiaux
    const uint8_t *p = buf + 1;

    memcpy(addr_ench, p, 16); 
    p += 16;        
    *port = get_u16(&p);        //récupére port multicast
    *nb_pairs = get_u16(&p);    //récupére nb de pairs

    //Taille attendue au total 
    size_t expected_len = 21 + (*nb_pairs) * 133;
    if (len < expected_len) return -2; //vérifier qu'on a assez de place pour le expected

    //Boucle d'extraction 
    for (uint16_t i = 0; i < *nb_pairs; ++i) {
        ids[i] = get_u16(&p);
        memcpy(ips[i], p, 16); p += 16;
        ports[i] = get_u16(&p);
        memcpy(cle_pubs[i], p, 113); p += 113;
    }
    return 0;
}

//==================================CODE 8: Début de l'enchère===============================================
/*construit un message de début d'enchère (CODE 8).
Format binaire:
[1] CODE_START_ENCH (8)
[2] vendeur_id
[4] numero_vente
[4] prix_initial
->return taille totale du message (11 octets)
*/
size_t build_auction_start(uint16_t id, uint32_t numv, uint32_t prix, uint8_t *out_buf) {
    uint8_t *p = out_buf;
    put_u8(&p, CODE_START_ENCH);       // CODE
    put_u16(&p, id);           // ID du vendeur
    uint32_t numv_be = htonl(numv);
    memcpy(p, &numv_be, 4);    // Numero de vente
    p += 4;
    uint32_t prix_be = htonl(prix);
    memcpy(p, &prix_be, 4);    // prix-initial
    p += 4;
    return p - out_buf;
}

int parse_auction_start(const uint8_t *buf, size_t buf_len,
                        uint16_t *out_id, uint32_t *out_numv, uint32_t *out_prix) {
    if (buf_len < 11) return -1; // Vérifier la taille minimale: code(1)+id(2)+numv(4)+prix(4) = 11
    const uint8_t *p = buf;
    if (*p++ != CODE_START_ENCH) return -2; //vérif le code

    *out_id = get_u16(&p);
    uint32_t numv_be, prix_be;
    memcpy(&numv_be, p, 4); p += 4;
    memcpy(&prix_be, p, 4); p += 4;
    *out_numv = ntohl(numv_be);
    *out_prix = ntohl(prix_be);
    return 0;
}

//==================================CODE 9 et 10: Enchère (Demande et Réponse)===============================================
/*Construit une requête de surenchère (CODE 9) ou confirmation de surenchère (CODE 10)
 [1] CODE_START_SURCHE (9)
 [2] peer_id (celui qui fait l'offre)
 [4] numero_vente
 [4] prix_proposé
-> retourne taille totale  11 octets
*/
size_t build_auction_bid(uint8_t code, uint16_t id, uint32_t numv, uint32_t prix, uint8_t *out_buf) {
    uint8_t *p = out_buf;
    put_u8(&p, code);          // CODE = 9 ou 10
    put_u16(&p, id);           // ID
    uint32_t numv_be = htonl(numv);
    memcpy(p, &numv_be, 4); p += 4;
    uint32_t prix_be = htonl(prix);
    memcpy(p, &prix_be, 4); p += 4;
    return p - out_buf;
}

int parse_auction_bid(const uint8_t *buf, size_t buf_len,
                      uint8_t expected_code,
                      uint16_t *out_id, uint32_t *out_numv, uint32_t *out_prix) {
    if (buf_len < 11) return -1;
    const uint8_t *p = buf;
    if (*p++ != expected_code) return -2;

    *out_id = get_u16(&p);
    uint32_t numv_be, prix_be;
    memcpy(&numv_be, p, 4); p += 4;
    memcpy(&prix_be, p, 4); p += 4;
    *out_numv = ntohl(numv_be);
    *out_prix = ntohl(prix_be);
    return 0;
}

/*=================================CODE 11=====================================================*/
size_t build_auction_finalization(uint16_t id, uint32_t numv, uint32_t prix, uint8_t *out_buf){
    uint8_t *p = out_buf;
    put_u8(&p, 11);                         /*CODE =11*/
    put_u16(&p, id);                        /*ID du superviseur*/
    uint32_t numv_be = htonl(numv);
    memcpy(p, &numv_be, sizeof(numv_be));   /*NUMV en big-endian*/
    p += sizeof(numv_be);
    uint32_t prix_be = htonl(prix);
    memcpy(p, &prix_be, sizeof(prix_be));   /*PRIX également en big-endian*/
    p += sizeof(prix_be);
    return (size_t)(p - out_buf);           /*Longueur totale du message*/
}

int parse_auction_finalization(const uint8_t *buf, size_t buf_len, uint16_t *out_id, uint32_t *out_numv, uint32_t *out_prix){
    if (buf_len < 11){
        return -1;
    }
    const uint8_t *p = buf;
    /*Vérification du code du message*/
    if (*p++ != 11) return -2;
    /*Récupération de l'ID du superviseur*/
    *out_id = get_u16(&p);
    /*Récupération NUMV*/
    uint32_t numv_be;
    memcpy(&numv_be, p, sizeof(numv_be));
    p += sizeof(numv_be);
    *out_numv = ntohl(numv_be);

    /*Récupéreration du PRIX*/
    uint32_t prix_be;
    memcpy(&prix_be, p, sizeof(prix_be));
    p += sizeof(prix_be);
    *out_prix = ntohl(prix_be);

    return 0;
}

/*=====================================CODE 12========================================*/

size_t build_auction_end(uint16_t winner_id_or_supervisor_id, uint32_t numv, uint32_t final_price, uint8_t *out_buf)
{
    uint8_t *p = out_buf;
    put_u8(&p, 12);                             /*CODE =12*/
    put_u16(&p, winner_id_or_supervisor_id);    /*Ici l'ID du gagnant ou du superviseur*/
    uint32_t numv_be = htonl(numv);
    memcpy(p, &numv_be, sizeof(numv_be));       /*NUMV en big-endian*/
    p += sizeof(numv_be);
    uint32_t final_price_be = htonl(final_price);
    memcpy(p, &final_price_be, sizeof(final_price_be)); /*PRIX final en big-endian*/
    p += sizeof(final_price_be);
    return (size_t)(p - out_buf);               /*Longueur totale du message*/
}

int parse_auction_end(const uint8_t *buf, size_t buf_len, uint16_t *out_winner_id_or_supervisor_id, uint32_t *out_numv, uint32_t *out_final_price)
{
    if (buf_len < 11){
        return -1;
    }
    const uint8_t *p = buf;
    if (*p++ != 12){
        return -2;
    }
    /*Cette fois on peut récupérer l'ID du gagnant ou du superviseur*/
    *out_winner_id_or_supervisor_id = get_u16(&p);
    /*Récupérer NUMV*/
    uint32_t numv_be;
    memcpy(&numv_be, p, sizeof(numv_be));
    p += sizeof(numv_be);
    *out_numv = ntohl(numv_be);
    /*Récupérer PRIX*/
    uint32_t final_price_be;
    memcpy(&final_price_be, p, sizeof(final_price_be));
    p += sizeof(final_price_be);
    *out_final_price = ntohl(final_price_be);
    return 0;
}

/*==================================================CODE 13====================================*/
size_t build_leave_system(uint16_t peer_id, uint8_t *out_buf)
{
    uint8_t *p = out_buf;
    put_u8(&p, 13);/*CODE = 13*/
    put_u16(&p, peer_id);    /*ID du pair qui quitte*/ 
    return (size_t)(p - out_buf); /*Longueur totale du message = 3 octets*/ 
}

int parse_leave_system(const uint8_t *buf, size_t buf_len,uint16_t *out_peer_id)
{
    if(buf_len < 1 + 2){
        return -1;
    }
    const uint8_t *p = buf;
    if(*p++ != 13){
        return -2;
    }
    /*On récupère l'ID du pair*/
    *out_peer_id = get_u16(&p);
    return 0;
}

/*===============================CODE 14 & 15==========================================*/
size_t build_auction_rejected_concurrent_or_invalid_price(uint8_t code, uint16_t peer_id, uint32_t numv, uint32_t proposed_price, uint8_t *out_buf)
{
    uint8_t *p = out_buf;
    put_u8(&p, code); /*CODE 14 ou 15*/
    put_u16(&p, peer_id); /*ID du pair dont l'enchère est rejetée*/ 
    uint32_t numv_be = htonl(numv);
    memcpy(p, &numv_be, sizeof(numv_be));
    p += sizeof(numv_be);
    uint32_t proposed_price_be = htonl(proposed_price);
    memcpy(p, &proposed_price_be, sizeof(proposed_price_be));
    p += sizeof(proposed_price_be);

    return (size_t)(p - out_buf);
}

int parse_auction_rejected_concurrent_or_invalid_price(const uint8_t *buf, size_t buf_len, uint8_t expected_code, uint16_t *out_peer_id, uint32_t *out_numv, uint32_t *out_proposed_price)
{
    if (buf_len < 11){
        return -1;
    }
    const uint8_t *p = buf;
    if (*p++ != expected_code){
        return -2;
    }
    *out_peer_id = get_u16(&p);
    uint32_t numv_be;
    memcpy(&numv_be, p, sizeof(numv_be));
    p += sizeof(numv_be);
    *out_numv = ntohl(numv_be);
    uint32_t proposed_price_be;
    memcpy(&proposed_price_be, p, sizeof(proposed_price_be));
    p += sizeof(proposed_price_be);
    *out_proposed_price = ntohl(proposed_price_be);
    return 0;
}

/*+++++++++++++++++++++++++++++++++CODE 16 & 17==================================*/
size_t build_supervisor_disparu_ou_annulation_requete(uint8_t code, uint16_t disappeared_supervisor_id, uint32_t numv, uint8_t *out_buf)
{
    uint8_t *p = out_buf;
    put_u8(&p, code);/*CODE = 16 ou 17*/
    put_u16(&p, disappeared_supervisor_id); /*ID du superviseur disparu*/
    uint32_t numv_be = htonl(numv);
    memcpy(p, &numv_be, sizeof(numv_be));
    p += sizeof(numv_be);
    return (size_t)(p - out_buf); /*Longueur totale du message (1 + 2 + 4 = 7 octets)*/
}

int parse_supervisor_disparu_ou_annulation_requete(const uint8_t *buf, size_t buf_len, uint8_t expected_code, uint16_t *out_disappeared_supervisor_id, uint32_t *out_numv)
{
    /*On vérifie la taille minimale du message égale à 7*/
    if (buf_len < 1 + 2 + 4){
        return -1;
    }
    const uint8_t *p = buf;
    if (*p++ != expected_code){
        return -2;
    } 
    /*Récupérer l'ID du superviseur disparu*/
    *out_disappeared_supervisor_id = get_u16(&p);
    uint32_t numv_be;
    memcpy(&numv_be, p, sizeof(numv_be));
    p += sizeof(numv_be);
    *out_numv = ntohl(numv_be);
    return 0; // Succès
}

/*============================================CODE 18==================================*/
size_t build_remove_absent_peers(uint16_t initiating_peer_id, uint8_t nb_absent_peers, const uint16_t absent_peer_ids[], uint8_t *out_buf)
{
    uint8_t *p = out_buf;
    put_u8(&p, 18); /*CODE = 18*/
    put_u16(&p, initiating_peer_id); /*ID du pair initiateur*/
    put_u8(&p, nb_absent_peers);  /*Nombre de pairs absents*/

    /*On concaténe les identifiants des pairs absents*/
    for (uint8_t i = 0; i < nb_absent_peers; ++i)
    {
        put_u16(&p, absent_peer_ids[i]);
    }
    return (size_t)(p - out_buf);
}

int parse_remove_absent_peers(const uint8_t *buf, size_t buf_len, uint16_t *out_initiating_peer_id, uint8_t *out_nb_absent_peers, uint16_t out_absent_peer_ids[])
{
    // Taille minimale: 1 (CODE) + 2 (ID) + 1 (NB) = 4 octets
    if (buf_len < 1 + 2 + 1){
        return -1;
    }
    const uint8_t *p = buf;
    // Vérifier le code du message
    if (*p++ != 18){
        return -2;
    }
     // Récupérer l'ID du pair initiateur
    *out_initiating_peer_id = get_u16(&p);
    // Récupérer le nombre de pairs absents
    *out_nb_absent_peers = *p++;
    /*Calculer la taille attendue totale avec les IDs*/
    size_t expected_len = (size_t)(1 + 2 + 1) + (size_t)(*out_nb_absent_peers * sizeof(uint16_t));
    if (buf_len < expected_len){
        return -3; /*Taille insuffisante pour tous les IDs annoncés*/
    }
    /*Extraire les identifiants des pairs absents*/
    for (uint8_t i = 0; i < *out_nb_absent_peers; ++i){
        out_absent_peer_ids[i] = get_u16(&p);
    }
    return 0;
}