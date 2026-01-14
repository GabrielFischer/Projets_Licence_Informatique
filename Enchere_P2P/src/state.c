// state.c
#include "../include/state.h"
#include <string.h> // Pour memset, memcpy
#include <stdio.h> // Pour printf (débogage)
#include <arpa/inet.h> // Pour inet_pton, inet_ntop (si nécessaire pour l'initialisation/affichage des adresses)
#include <unistd.h> // Pour close (si on gère la fermeture des sockets ici à la fin)


// --- Implémentation des fonctions de gestion de l'état ---

/**
 * @brief Initialise la structure d'état global de l'application.
 *
 * @param state Pointeur vers la structure AppState à initialiser.
 */
void app_state_init(AppState *state) {
    if (state == NULL) {
        fprintf(stderr, "Erreur: Pointeur AppState est NULL lors de l'initialisation.\n");
        return;
    }
    memset(state, 0, sizeof(AppState));

    // Initialiser les compteurs de listes
    state->pairs_connus.count = 0;
    state->ventes_actives.count = 0;
    state->potential_peers.count = 0;

    // Initialiser les descripteurs de sockets à une valeur invalide
    state->udp_socket_general = -1;
    state->tcp_listen_socket = -1;

    // Initialiser l'état de jonction
    state->join_state = JOIN_STATE_IDLE;

    // Initialiser les adresses multicast (selon le sujet)
    // Adresse liaison: FF12::93:93 (port 9393)
    inet_pton(AF_INET6, "FF12::93:93", &(state->adresse_liaison_multicast));
    state->port_liaison = 9393; // Port spécifié dans le sujet pour la liaison

    // Adresse encheres: doit être obtenue via CODE 7 ou configurée pour le premier pair
    // On peut l'initialiser à une valeur non valide ou à une valeur par défaut si le pair est le premier
    memset(&(state->adresse_encheres_multicast), 0, sizeof(state->adresse_encheres_multicast)); // Initialisation à zéro
    state->port_encheres = 0; // Port inconnu initialement

    // Initialiser les informations sur le pair local (sera complété après génération ID/clés/adresses)
    state->moi.id = 0; // ID initial inconnu
    memset(&(state->moi.ip_perso), 0, sizeof(state->moi.ip_perso));
    state->moi.port_perso = 0;
    memset(&(state->moi.cle_publique), 0, sizeof(state->moi.cle_publique));
    state->moi.compteur_ventes_initiees = 0;
    state->moi.est_actif = false; // Pas encore actif dans un système

    // Initialiser les timestamps
    state->join_request_sent_time = 0; // Pas de requête envoyée initialement

    // Autres initialisations si nécessaire...
}

void app_state_init2(AppState *state,
                    uint16_t id,
                    const char *ip_str,
                    uint16_t port)
{
    if (state == NULL) {
        fprintf(stderr, "Erreur: pointeur AppState NULL\n");
        return;
    }

    // 0) Zéro sur toute la structure
    memset(state, 0, sizeof(AppState));

    // 1) Identité du pair local
    state->moi.id = id;
    state->moi.port_perso = port;
    // Convertit la chaîne IPv6 en binaire
    if (inet_pton(AF_INET6, ip_str, &state->moi.ip_perso) != 1) {
        fprintf(stderr, "Error: invalid local IPv6 '%s'\n", ip_str);
        // on peut exit(1) ou laisser l'IP à ::0 et traiter l'erreur plus tard
    }
    // La clé publique sera chargée ou générée plus tard, on la laisse à 0
    state->moi.est_actif = false;
    state->moi.compteur_ventes_initiees = 0;

    // 2) Listes vides
    state->pairs_connus.count    = 0;
    state->ventes_actives.count  = 0;
    state->potential_peers.count = 0;

    // 3) Sockets non ouvertes pour l’instant
    state->udp_socket_general  = -1;
    state->tcp_listen_socket   = -1;
    state->network_thread      = (pthread_t)0;

    // 4) États initiaux
    state->join_state = JOIN_STATE_IDLE;
    state->join_request_sent_time = 0;

    // 5) Adresses multicast fixes
    // Liaison (CODE 3/4)
    inet_pton(AF_INET6, "FF12::93:93",
              &state->adresse_liaison_multicast);
    state->port_liaison = 9393;
    // Enchères (CODE 7+)
    // On met à zéro : sera configuré lors du join initial
    memset(&state->adresse_encheres_multicast, 0,
           sizeof(state->adresse_encheres_multicast));
    state->port_encheres = 0;

    // 6) Prêt pour appeler network_init(state) qui ouvrira sockets, joindra
    //    les groupes et fera le bind approprié.
}

/**
 * @brief Ajoute un pair à la liste des pairs connus.
 * Vérifie les doublons basés sur l'ID et la limite MAX_PEERS.
 *
 * @param peer_list Pointeur vers la liste des pairs connus.
 * @param peer Pointeur vers les informations du pair à ajouter.
 * @return 0 en cas de succès, -1 si la liste est pleine, -2 si le pair existe déjà.
 */
int add_known_peer(PeerList *peer_list, const PeerInfo *peer) {
    if (peer_list == NULL || peer == NULL) return -1;

    // Vérifier si la liste est pleine
    if (peer_list->count >= MAX_PEERS) {
        fprintf(stderr, "Erreur: Liste des pairs connus pleine.\n");
        return -1; // Liste pleine
    }

    // Vérifier si un pair avec le même ID existe déjà
    if (find_peer_by_id(peer_list, peer->id) != NULL) {
        // Optionnel: vérifier aussi par IP/Port si l'ID n'est pas censé être unique à ce stade
        fprintf(stderr, "Attention: Pair avec ID %u existe déjà dans la liste.\n", peer->id);
        // Selon le comportement désiré, on peut retourner une erreur ou mettre à jour
        return -2; // Pair déjà existant
    }

    // Ajouter le pair à la fin de la liste
    memcpy(&(peer_list->listePairs[peer_list->count]), peer, sizeof(PeerInfo));
    peer_list->count++;

    // fprintf(stdout, "Pair ID %u ajouté à la liste des pairs connus. Total: %zu\n", peer->id, peer_list->count);

    return 0; // Succès
}

/**
 * @brief Recherche un pair dans la liste des pairs connus par son identifiant.
 *
 * @param peer_list Pointeur vers la liste des pairs connus.
 * @param id L'identifiant du pair à rechercher.
 * @return Pointeur vers la structure PeerInfo si trouvée, NULL sinon.
 */
PeerInfo* find_peer_by_id(PeerList *peer_list, uint16_t id) {
    if (peer_list == NULL) return NULL;

    for (size_t i = 0; i < peer_list->count; ++i) {
        if (peer_list->listePairs[i].id == id) {
            return &(peer_list->listePairs[i]); // Pair trouvé
        }
    }

    return NULL; // Pair non trouvé
}

/**
 * @brief Supprime un pair de la liste des pairs connus par son identifiant.
 *
 * @param peer_list Pointeur vers la liste des pairs connus.
 * @param id L'identifiant du pair à supprimer.
 * @return 0 en cas de succès, -1 si la liste est NULL ou pair non trouvé.
 */
int remove_known_peer(PeerList *peer_list, uint16_t id) {
    if (peer_list == NULL) return -1;

    for (size_t i = 0; i < peer_list->count; ++i) {
        if (peer_list->listePairs[i].id == id) {
            // Pair trouvé, décaler les éléments suivants pour combler le trou
            for (size_t j = i; j < peer_list->count - 1; ++j) {
                memcpy(&(peer_list->listePairs[j]), &(peer_list->listePairs[j+1]), sizeof(PeerInfo));
            }
            peer_list->count--;
            // Optionnel: effacer le dernier élément maintenant redondant
            // memset(&(peer_list->liste[peer_list->count]), 0, sizeof(PeerInfo));

            // fprintf(stdout, "Pair ID %u supprimé de la liste des pairs connus. Total: %zu\n", id, peer_list->count);

            return 0; // Succès
        }
    }

    return -1; // Pair non trouvé
}


/**
 * @brief Ajoute un pair à la liste temporaire des pairs potentiels (réponses CODE 4).
 * Vérifie les doublons basés sur l'ID et la limite MAX_POTENTIAL_PEERS.
 * Note: Ici, un pair potentiel n'a pas encore de clé publique connue.
 * On stocke les infos minimales reçues dans le CODE 4.
 *
 * @param potential_peers Pointeur vers la liste des pairs potentiels.
 * @param id L'identifiant du pair potentiel.
 * @param ip L'adresse IP personnelle du pair potentiel.
 * @param port Le port personnel du pair potentiel.
 * @return 0 en cas de succès, -1 si la liste est pleine, -2 si le pair existe déjà.
 */
int add_potential_peer(PotentialPeerList *potential_peers, uint16_t id, const struct in6_addr *ip, uint16_t port) {
    if (potential_peers == NULL || ip == NULL) return -1;

    // Vérifier si la liste est pleine
    if (potential_peers->count >= MAX_POTENTIAL_PEERS) {
        fprintf(stderr, "Erreur: Liste des pairs potentiels pleine.\n");
        return -1; // Liste pleine
    }

    // Vérifier si un pair avec le même ID ou la même adresse existe déjà dans la liste potentielle
    for (size_t i = 0; i < potential_peers->count; ++i) {
        if (potential_peers->liste[i].id == id ||
            (memcmp(&(potential_peers->liste[i].ip_perso), ip, sizeof(struct in6_addr)) == 0 &&
             potential_peers->liste[i].port_perso == port)) {
            // fprintf(stderr, "Attention: Pair potentiel avec ID %u ou adresse déjà dans la liste.\n", id);
            return -2; // Pair déjà existant dans la liste potentielle
        }
    }

    // Ajouter le pair potentiel à la fin de la liste
    potential_peers->liste[potential_peers->count].id = id;
    memcpy(&(potential_peers->liste[potential_peers->count].ip_perso), ip, sizeof(struct in6_addr));
    potential_peers->liste[potential_peers->count].port_perso = port;
    // Les autres champs de PeerInfo (clé publique, cpt, est_actif) ne sont pas connus à ce stade pour un pair potentiel
    // Ils seront remplis quand il rejoindra formellement le système et qu'on recevra son CODE 5/6.
    memset(&(potential_peers->liste[potential_peers->count].cle_publique), 0, MSG_CLE_LEN); // Initialiser la clé publique
    potential_peers->liste[potential_peers->count].compteur_ventes_initiees = 0;
    potential_peers->liste[potential_peers->count].est_actif = false;

    potential_peers->count++;

    // char ip_str[INET6_ADDRSTRLEN];
    // inet_ntop(AF_INET6, ip, ip_str, sizeof(ip_str));
    // fprintf(stdout, "Pair potentiel ID %u (%s:%u) ajouté. Total: %zu\n", id, ip_str, port, potential_peers->count);

    return 0; // Succès
}


/**
 * @brief Réinitialise la liste temporaire des pairs potentiels.
 *
 * @param potential_peers Pointeur vers la liste des pairs potentiels.
 */
void clear_potential_peers(PotentialPeerList *potential_peers) {
    if (potential_peers == NULL) return;
    potential_peers->count = 0;
    // Pas besoin de memset pour un tableau statique si on gère la taille avec 'count'
    // Si c'était alloué dynamiquement, il faudrait free.
    // fprintf(stdout, "Liste des pairs potentiels effacée.\n");
}


/**
 * @brief Ajoute une vente à la liste des ventes actives.
 * Vérifie les doublons basés sur le numéro de vente et la limite MAX_ACTIVE_AUCTIONS.
 *
 * @param auction_list Pointeur vers la liste des ventes actives.
 * @param auction Pointeur vers les informations de la vente à ajouter.
 * @return 0 en cas de succès, -1 si la liste est pleine, -2 si la vente existe déjà.
 */
int add_active_auction(ActiveAuctionList *auction_list, const VenteAuxEncheres *auction) {
    if (auction_list == NULL || auction == NULL) return -1;

    // Vérifier si la liste est pleine
    if (auction_list->count >= MAX_ACTIVE_AUCTIONS) {
        fprintf(stderr, "Erreur: Liste des ventes actives pleine.\n");
        return -1; // Liste pleine
    }

    // Vérifier si une vente avec le même numéro existe déjà
    if (find_auction_by_num(auction_list, auction->num_vente) != NULL) {
        // fprintf(stderr, "Attention: Vente avec numéro %u existe déjà dans la liste.\n", auction->num_vente);
        return -2; // Vente déjà existante
    }

    // Ajouter la vente à la fin de la liste
    memcpy(&(auction_list->liste_ventes[auction_list->count]), auction, sizeof(VenteAuxEncheres));
    auction_list->count++;

    // fprintf(stdout, "Vente numéro %u ajoutée à la liste des ventes actives. Total: %zu\n", auction->num_vente, auction_list->count);

    return 0; // Succès
}

/**
 * @brief Recherche une vente active dans la liste par son numéro.
 *
 * @param auction_list Pointeur vers la liste des ventes actives.
 * @param num_vente Le numéro de la vente à rechercher.
 * @return Pointeur vers la structure VenteAuxEncheres si trouvée, NULL sinon.
 */
VenteAuxEncheres* find_auction_by_num(ActiveAuctionList *auction_list, uint32_t num_vente) {
    if (auction_list == NULL) return NULL;

    for (size_t i = 0; i < auction_list->count; ++i) {
        if (auction_list->liste_ventes[i].num_vente == num_vente) {
            return &(auction_list->liste_ventes[i]); // Vente trouvée
        }
    }

    return NULL; // Vente non trouvée
}

/**
 * @brief Supprime une vente de la liste des ventes actives par son numéro.
 *
 * @param auction_list Pointeur vers la liste des ventes actives.
 * @param num_vente Le numéro de la vente à supprimer.
 * @return 0 en cas de succès, -1 si la liste est NULL ou vente non trouvée.
 */
int remove_active_auction(ActiveAuctionList *auction_list, uint32_t num_vente) {
     if (auction_list == NULL) return -1;

    for (size_t i = 0; i < auction_list->count; ++i) {
        if (auction_list->liste_ventes[i].num_vente == num_vente) {
            // Vente trouvée, décaler les éléments suivants pour combler le trou
            for (size_t j = i; j < auction_list->count - 1; ++j) {
                memcpy(&(auction_list->liste_ventes[j]), &(auction_list->liste_ventes[j+1]), sizeof(VenteAuxEncheres));
            }
            auction_list->count--;
            // Optionnel: effacer le dernier élément maintenant redondant
            // memset(&(auction_list->liste[auction_list->count]), 0, sizeof(VenteAuxEncheres));

            // fprintf(stdout, "Vente numéro %u supprimée de la liste des ventes actives. Total: %zu\n", num_vente, auction_list->count);

            return 0; // Succès
        }
    }

    return -1; // Vente non trouvée
}

