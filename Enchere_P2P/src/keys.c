#include "../include/keys.h"

void generer_cles(const char *fichier_priv, const char *fichier_pub) {
    // Crée un contexte pour la génération de clés ED25519
    EVP_PKEY_CTX *ctx = EVP_PKEY_CTX_new_id(EVP_PKEY_ED25519, NULL);
    if (!ctx) {
        fprintf(stderr, "Erreur création contexte\n");
        exit(EXIT_FAILURE);
    }

    // Initialise le contexte pour la génération
    if (EVP_PKEY_keygen_init(ctx) <= 0) {
        fprintf(stderr, "Erreur init génération\n");
        exit(EXIT_FAILURE);
    }

    // Alloue un EVP_PKEY* et génère la paire de clés
    EVP_PKEY *pkey = NULL;
    if (EVP_PKEY_generate(ctx, &pkey) <= 0) {
        fprintf(stderr, "Erreur génération de clé\n");
        exit(EXIT_FAILURE);
    }

    // Ouvre le fichier de sortie pour la clé privée et l’écrit en PEM
    FILE *fpriv = fopen(fichier_priv, "w");
    if (!fpriv || !PEM_write_PrivateKey(fpriv, pkey, NULL, NULL, 0, NULL, NULL)) {
        fprintf(stderr, "Erreur écriture clé privée\n");
        exit(EXIT_FAILURE);
    }
    fclose(fpriv);

    // Ouvre le fichier de sortie pour la clé publique et l’écrit en PEM
    FILE *fpub = fopen(fichier_pub, "w");
    if (!fpub || !PEM_write_PUBKEY(fpub, pkey)) {
        fprintf(stderr, "Erreur écriture clé publique\n");
        exit(EXIT_FAILURE);
    }
    fclose(fpub);

    // Nettoyage des structures OpenSSL
    EVP_PKEY_free(pkey);
    EVP_PKEY_CTX_free(ctx);
}

// SIGNATURE ED25519
/*
ed25519_sign:
- msg/msglen : données à signer
- sig/siglen : buffer de sortie (>=64 octets)
- sk          : clé privée ED25519 binaire (32 octets)

-> Retourne   0 si la signature a réussi
             -1 en cas d’erreur
*/
int ed25519_sign(const uint8_t *msg, size_t msglen,
                        uint8_t *sig /*64bytes*/, size_t *siglen,
                        const uint8_t sk[ED25519_SK_LEN])
{
    // Crée un EVP_PKEY* à partir de la clé privée brute sk
    EVP_PKEY *pkey = EVP_PKEY_new_raw_private_key(EVP_PKEY_ED25519, NULL, sk, ED25519_SK_LEN);
    if (!pkey) return -1;

    // Contexte de message digest pour signer
    EVP_MD_CTX *mctx = EVP_MD_CTX_new();
    if (!mctx) { EVP_PKEY_free(pkey); return -1; }

    // Initialise la signature (aucun algorithme de hachage séparé pour ED25519 -> NULL)
    if (EVP_DigestSignInit(mctx, NULL, NULL, NULL, pkey) != 1) goto err;
    
    //Signe directement le message msg 
    *siglen = 64;
    if (EVP_DigestSign(mctx, sig, siglen, msg, msglen) != 1) goto err;
    
    //Libération de mémoire
    EVP_MD_CTX_free(mctx);
    EVP_PKEY_free(pkey);
    return 0;

    //goto err -> nettoyage et return erreur (-1)
err:
    EVP_MD_CTX_free(mctx);
    EVP_PKEY_free(pkey);
    return -1;
}

//VÉRIFICATION ED25519
/*
ed25519_verify:
- msg/msglen : données signées
- sig/siglen : signature à vérifier
- pk          : clé publique ED25519 binaire (32 octets)
 
->Retourne   0 si la vérification réussit
            -1 sinon
*/
int ed25519_verify(const uint8_t *msg, size_t msglen,
                          const uint8_t *sig, size_t siglen,
                          const uint8_t pk[32])
{
    // Crée un EVP_PKEY* à partir de la clé publique brute pk
    EVP_PKEY *pkey = EVP_PKEY_new_raw_public_key(EVP_PKEY_ED25519, NULL, pk, ED25519_PK_LEN);
    if (!pkey) return -1;

    //Contexte pour vérification
    EVP_MD_CTX *mctx = EVP_MD_CTX_new();
    if (!mctx) { EVP_PKEY_free(pkey); return -1; }

    // Initialise le contexte pour la vérification
    if (EVP_DigestVerifyInit(mctx, NULL, NULL, NULL, pkey) != 1) goto err;

    //Compare signature sig à signature interne faite avec msg
    //vérification : renvoie 0 si égal, -1 sinon
    int ret = EVP_DigestVerify(mctx, sig, siglen, msg, msglen) == 1 ? 0 : -1;
    
    //libère mémoire
    EVP_MD_CTX_free(mctx);
    EVP_PKEY_free(pkey);
    return ret;

    //goto err -> nettoyage et return erreur (-1)
err:
    EVP_MD_CTX_free(mctx);
    EVP_PKEY_free(pkey);
    return -1;
}

//CHARGEMENT DES CLÉS DE FICHIERS PEM
/*
charger_cles_fichiers:
- cle_s et cle_p : buffers (32 et 32 octets) pour recevoir les clés binaires
- fpriv_name, fpub_name : chemins vers les fichiers PEM
 
Si les fichiers n’existent pas, les génère. 
Lit ensuite la clé privée et publique PEM, en extrait les clés brutes.
*/
void charger_cles_fichiers(uint8_t *cle_s, uint8_t *cle_p, const char *fpriv_name, const char *fpub_name) {
    FILE *fp_priv = fopen(fpriv_name, "r");
    FILE *fp_pub = fopen(fpub_name, "r");
    if (!fp_priv || !fp_pub) {
        printf("Clés inexistantes. Génération...\n");
        generer_cles(fpriv_name, fpub_name);
        fp_priv = fopen(fpriv_name, "r");
        fp_pub = fopen(fpub_name, "r");
        if (!fp_priv || !fp_pub) {
            fprintf(stderr, "Échec lecture des fichiers PEM après génération\n");
            exit(EXIT_FAILURE);
        }
    }
    printf("Clés trouvées : %s et %s\n", fpriv_name, fpub_name);

    // Lecture des objets PEM
    EVP_PKEY *priv_key = PEM_read_PrivateKey(fp_priv, NULL, NULL, NULL);
    EVP_PKEY *pub_key = PEM_read_PUBKEY(fp_pub, NULL, NULL, NULL);
    fclose(fp_priv);
    fclose(fp_pub);

    size_t sklen = ED25519_SK_LEN;
    size_t pklen = ED25519_PK_LEN;

    // Extraction des clés brutes ED25519
    if (EVP_PKEY_get_raw_private_key(priv_key, cle_s, &sklen) != 1 ||
        EVP_PKEY_get_raw_public_key(pub_key, cle_p, &pklen) != 1) {
        fprintf(stderr, "Erreur extraction des clés binaires\n");
        exit(EXIT_FAILURE);
    }

    EVP_PKEY_free(priv_key);
    EVP_PKEY_free(pub_key);
}

//CHARGEMENT D’UNE CLÉ PUBLIQUE (PEM en binaire direct)
/*
charger_cle_publique_pem:
-Lit exactement 113 octets depuis "src/keys/cle_publique.pem"
-Vérifie la taille du fichier et retourne -1 en cas d’erreur
*/
int charger_cle_publique_pem(uint8_t out[113]) {
    FILE *fp = fopen("src/keys/cle_publique.pem", "rb");
    if (!fp) {
        perror("Impossible d'ouvrir cle_publique.pem");
        return -1;
    }
    struct stat st;
    if (stat("src/keys/cle_publique.pem", &st) != 0 || st.st_size != 113) {
        fprintf(stderr, "cle_publique.pem invalide (taille %ld au lieu de 113)\n", st.st_size);
        fclose(fp);
        return -1;
    }
    if (fread(out, 1, 113, fp) != 113) {
        fprintf(stderr, "Erreur lecture cle_publique.pem\n");
        fclose(fp);
        return -1;
    }
    fclose(fp);
    return 0;
}

/*Charger_cle_publique_raw:
- Charge une clé publique PEM nommée "cle_publique_<port>.pem",
- et en extrait la clé ED25519 brute (32 octets).
 */
int charger_cle_publique_raw(uint8_t out_raw[ED25519_PK_LEN], int port) {
    char filepath[256];
    // Construire le chemin « src/keys/cle_publique_<PORT>.pem »
    snprintf(filepath, sizeof(filepath),"src/keys/cle_publique_%u.pem",port);
    FILE *fp = fopen(filepath, "r");
    if (!fp) {
        perror("fopen cle_publique.pem");
        return -1;
    }
    EVP_PKEY *pkey = PEM_read_PUBKEY(fp, NULL, NULL, NULL);
    fclose(fp);
    if (!pkey) {
        fprintf(stderr, "PEM_read_PUBKEY a échoué\n");
        return -1;
    }
    size_t len = ED25519_PK_LEN;
    if (EVP_PKEY_get_raw_public_key(pkey, out_raw, &len) != 1 || len != ED25519_PK_LEN) {
        fprintf(stderr, "EVP_PKEY_get_raw_public_key a échoué\n");
        EVP_PKEY_free(pkey);
        return -1;
    }
    EVP_PKEY_free(pkey);
    return 0;
}