#ifndef KEYS_H
#define KEYS_H

#include "macros.h"

// Génère une paire de clés ED25519 et les sauvegarde dans deux fichiers PEM
void generer_cles(const char *fichier_priv, const char *fichier_pub);

// Charge les clés ED25519 brutes (publique puis privée) depuis les fichiers PEM par défaut
void charger_cles(uint8_t *cle_p, uint8_t *cle_s) ;

// Charge ou génère ensuite la clé privée/raw et la clé publique/raw depuis des chemins PEM
void charger_cles_fichiers(uint8_t *cle_s, uint8_t *cle_p, const char *fpriv_name, const char *fpub_name) ;

// Charge une clé publique ED25519 (raw) depuis "cle_publique_<port>.pem"
int charger_cle_publique_raw(uint8_t out_raw[ED25519_PK_LEN], int port);

// Signe un message avec une clé secrète ED25519 (32 octets) et produit une signature (<=64 octets)
int ed25519_sign(const uint8_t *msg, size_t msglen,uint8_t *sig, size_t *siglen,const uint8_t sk[ED25519_SK_LEN]);

// Vérifie la signature d'un message avec une clé publique ED25519 (32 octets)
int ed25519_verify(const uint8_t *msg, size_t msglen,const uint8_t *sig, size_t siglen,const uint8_t pk[ED25519_PK_LEN]);

#endif // KEYS_H
