#ifndef CONSENSUS_H
#define CONSENSUS_H

#include <stdint.h>

/**
 * init_consensus_sockets:
 *   Crée et bind les sockets UDP pour le superviseur et les clients
 *   sur le port 'port_consensus'. Doit être appelé avant tout consensus.
 * @return 0 en succès, -1 en cas d’erreur.
 */
int init_consensus_sockets(void);

/**
 * consensus_serveur:
 *   Côté superviseur : collecte les validations CODE 1, puis diffuse
 *   le consensus final CODE 2 pour l’enchère spécifiée.
 * @param vendeur_id    ID du nœud superviseur.
 * @param numero_vente  numéro unique de l’enchère.
 * @param prix          prix proposé validé.
 */
void consensus_serveur(uint16_t vendeur_id,
                       uint32_t numero_vente,
                       uint32_t prix);

/**
 * consensus_client:
 *   Côté client non-superviseur : envoie sa validation CODE 1 au superviseur
 *   puis attend et traite le consensus final CODE 2.
 * @param sup_id        ID du superviseur qui coordonne.
 * @param numero_vente  numéro de l’enchère en cours.
 * @param prix          prix reçu dans le CODE 10.
 */
void consensus_client(uint16_t sup_id,
                      uint32_t numero_vente,
                      uint32_t prix);

#endif // CONSENSUS_H
