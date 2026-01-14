#ifndef MSG_H
#define MSG_H

#include "keys.h"
#include "macros.h"

/**
 * build_validation : sérialise un message CODE=1 (validation) 
 * la fonction prend le payload, le signe, et construit une trame UDP (format binaire qu’on envoie sur le réseau)
 * 
 * @param peer_id      Identifiant du peer (uint16)
 * @param payload      Données à valider --> contenu du message d'intérêt (ex: numéro d'enchêre et prix proposé)
 * @param payload_len  Taille du payload
 * @param sk           Clé privée ED25519 (SIG_LEN bytes)
 * @param out_buf      Buffer de sortie (assez grand)
 * @return             Nombre d’octets écrits
 */
size_t build_validation(uint16_t peer_id,
                        const uint8_t *payload,
                        uint8_t payload_len,
                        const uint8_t sk[ED25519_SK_LEN],
                        uint8_t *out_buf);

/**
 * parse_validation : désérialise un buffer CODE=1
 * 
 * @param buf          Buffer reçu
 * @param buf_len      Taille du buffer
 * @param out_id       -> Identifiant du peer valideur
 * @param out_payload  -> copie du payload
 * @param out_len      -> longueur du payload
 * @param out_sig      -> signature (MSG_SIG_LEN octets)
 * @return             0 si OK, <0 sinon
 */
int parse_validation(const uint8_t *buf, size_t buf_len,
                     uint16_t *out_id,
                     uint8_t *out_payload, uint8_t *out_len,
                     uint8_t out_sig[MSG_SIG_LEN]);

/**
 * Sérialise un message de consensus final (CODE=2)
 * 
 * @param peer_ids   tableau des IDs de validateurs (hors superviseur)
 * @param peer_sigs  tableau de pointeurs sur chacune des signatures (MSG_SIG_LEN)
 * @param nb_peers   nombre d'entrées dans peer_ids / peer_sigs
 * @return nombre d'octets écrits dans out_buf
 */
size_t build_consensus(const uint8_t *payload, uint8_t payload_len,uint16_t sup_id, const uint8_t sk[ED25519_SK_LEN],const uint16_t peer_ids[], const uint8_t *peer_sigs[],uint8_t nb_peers, uint8_t *out_buf);

/**
 * Désérialise un buffer CODE=2
 * 
 * @param out_peer_sigs doit être un tableau de pointeurs (taille ≥ nb_peers)
 * @return 0 si OK, <0 sinon
 */
int parse_consensus(const uint8_t *buf, size_t buf_len,uint16_t *out_sup_id,uint8_t *out_payload, uint8_t *out_len,uint8_t out_sup_sig[MSG_SIG_LEN],uint16_t out_peer_ids[], uint8_t *out_peer_sigs[],uint8_t *out_nb_peers);

/**
 * Sérialise un message de « join request » (CODE=3), sans payload.
 * @param out_buf   Buffer de sortie (au moins 1 octet)
 * @return          Nombre d’octets écrits (toujours 1)
 */
size_t build_join_request(uint8_t *out_buf);        

/**
 * Désérialise un buffer CODE=3.
 * @param buf       Buffer reçu
 * @param buf_len   Taille du buffer
 * @return          0 si c’est bien un CODE_JOIN_REQ, <0 sinon
 */
int parse_join_request(const uint8_t *buf, size_t buf_len);
     
/**
 * Sérialise un message de « join response » (CODE=4) contenant :
 *   - ID (2 octets)
 *   - IP en IPv6 (16 octets)
 *   - PORT (2 octets)
 *
 * @param id        Identifiant du pair
 * @param ip        Adresse IPv6 brute (16 octets)
 * @param port      Numéro de port
 * @param out_buf   Buffer de sortie (au moins 1+2+16+2 octets)
 * @return          Nombre d’octets écrits
 */
size_t build_join_response(uint16_t id,const uint8_t ip[16],uint16_t port,uint8_t *out_buf);

/**
 * Désérialise un buffer CODE=4.
 *
 * @param buf        Buffer reçu
 * @param buf_len    Taille du buffer
 * @param out_id     -> Identifiant du pair
 * @param out_ip     -> Adresse IPv6 (doit être un tableau de 16 octets)
 * @param out_port   -> Numéro de port
 * @return           0 si OK, <0 sinon
 */
int parse_join_response(const uint8_t *buf, size_t buf_len,uint16_t *out_id,uint8_t out_ip[16],uint16_t *out_port);

size_t build_join_tcp(uint16_t id,const uint8_t ip[16],uint16_t port,const uint8_t cle[MSG_CLE_LEN],uint8_t *out_buf);

int parse_join_tcp(const uint8_t *buf, size_t buf_len,uint16_t *out_id,uint8_t out_ip[16],uint16_t *out_port,uint8_t out_cle[MSG_CLE_LEN]);

size_t build_id_ok(uint8_t *out_buf);
size_t build_new_id(uint16_t new_id, uint8_t *out_buf);
int parse_id_ok(const uint8_t *buf, size_t buf_len);
int parse_new_id(const uint8_t *buf, size_t buf_len, uint16_t *out_id);

size_t build_infos_system(const uint8_t addr_ench[16], uint16_t port,uint16_t nb_pairs,const uint16_t *ids,const uint8_t ips[][16],const uint16_t *ports,const uint8_t cle_pubs[][113],uint8_t *out_buf);

int parse_infos_system(const uint8_t *buf, size_t len,uint8_t addr_ench[16], uint16_t *port,uint16_t *nb_pairs,uint16_t *ids,uint8_t ips[][16],uint16_t *ports,uint8_t cle_pubs[][113]);

size_t build_auction_start(uint16_t id, uint32_t numv, uint32_t prix, uint8_t *out_buf);

int parse_auction_start(const uint8_t *buf, size_t buf_len,uint16_t *out_id, uint32_t *out_numv, uint32_t *out_prix);


size_t build_auction_bid(uint8_t code, uint16_t id, uint32_t numv, uint32_t prix, uint8_t *out_buf);

int parse_auction_bid(const uint8_t *buf, size_t buf_len,uint8_t expected_code,uint16_t *out_id, uint32_t *out_numv, uint32_t *out_prix);

int parse_auction_finalization(const uint8_t *buf, size_t buf_len, uint16_t *out_id, uint32_t *out_numv, uint32_t *out_prix);
size_t build_auction_finalization(uint16_t id, uint32_t numv, uint32_t prix, uint8_t *out_buf);

size_t build_auction_end(uint16_t winner_id_or_supervisor_id, uint32_t numv, uint32_t final_price, uint8_t *out_buf);
int parse_auction_end(const uint8_t *buf, size_t buf_len, uint16_t *out_winner_id_or_supervisor_id, uint32_t *out_numv, uint32_t *out_final_price);

size_t build_leave_system(uint16_t peer_id, uint8_t *out_buf);
int parse_leave_system(const uint8_t *buf, size_t buf_len,uint16_t *out_peer_id);

size_t build_auction_rejected_concurrent_or_invalid_price(uint8_t code, uint16_t peer_id, uint32_t numv, uint32_t proposed_price, uint8_t *out_buf);
int parse_auction_rejected_concurrent_or_invalid_price(const uint8_t *buf, size_t buf_len, uint8_t expected_code, uint16_t *out_peer_id, uint32_t *out_numv, uint32_t *out_proposed_price);

size_t build_supervisor_disparu_ou_annulation_requete(uint8_t code, uint16_t disappeared_supervisor_id, uint32_t numv, uint8_t *out_buf);
int parse_supervisor_disparu_ou_annulation_requete(const uint8_t *buf, size_t buf_len, uint8_t expected_code, uint16_t *out_disappeared_supervisor_id, uint32_t *out_numv);

size_t build_remove_absent_peers(uint16_t initiating_peer_id, uint8_t nb_absent_peers, const uint16_t absent_peer_ids[], uint8_t *out_buf);
int parse_remove_absent_peers(const uint8_t *buf, size_t buf_len, uint16_t *out_initiating_peer_id, uint8_t *out_nb_absent_peers, uint16_t out_absent_peer_ids[]);

size_t build_send_new_peer(uint16_t id,const uint8_t ip[16],uint16_t port,const uint8_t cle[MSG_CLE_LEN],uint8_t *out_buf);
int parse_send_new_peer(const uint8_t *buf, size_t buf_len,uint16_t *out_id,uint8_t out_ip[16],uint16_t *out_port,uint8_t out_cle[MSG_CLE_LEN]);

#endif // MSG_H