// src/main_msg.c
#include <stdio.h>
#include "../include/msg.h"
#include <string.h>
#include <openssl/evp.h>
#include <openssl/err.h>
#include "../include/state.h"


/*Pour compiler avec OpenSSL
gcc -I../include ../src/msg.c ../src/main_msg.c -o test_cons_openssl -lcrypto
./test_cons_openssl
*/

int main(void) {
    //=============TEST BUILD ET PARSING DE CODE 5 =========================
    uint8_t buf[256];

    // données fausses (exemple)
    uint16_t id = 1234;
    uint8_t ip[16] = {
        0x20,0x01,0x0d,0xb8, 0,0,0,0, 0,0,0,0, 0,0,0,1
    }; // équivalent à 2001:db8::1
    uint16_t port = 4242;
    uint8_t cle[MSG_CLE_LEN];
    // CLE remplie de 0xAA
    memset(cle, 0xAA, MSG_CLE_LEN);

    size_t n = build_join_tcp(id, ip, port, cle, buf);
    printf("Built JOIN_TCP of %zu bytes, code=%u\n", n, buf[0]);

    // parse
    uint16_t rid;
    uint8_t rip[16];
    uint16_t rport;
    uint8_t rcle[MSG_CLE_LEN];
    int rc = parse_join_tcp(buf, n, &rid, rip, &rport, rcle);
    if (rc != 0) {
        printf("parse_join_tcp error %d\n", rc);
        return 1;
    }
    // vérification
    if (rid != id || rport != port ||
        memcmp(rip, ip, 16) != 0 ||
        memcmp(rcle, cle, MSG_CLE_LEN) != 0) {
        printf("Data mismatch\n");
        return 1;
    }
    printf("parse_join_tcp OK\n");
    return 0;

    /*
    EXPECTED SORTIE:
    Built JOIN_TCP of 134 bytes, code=5
    parse_join_tcp OK
    */

    //=============FIN TEST BUILD ET PARSING DE CODE 5 =========================
    /*
    //=============TEST BUILD ET PARSING DE CODE 4 =========================
    uint8_t buf[32];
    uint16_t id = 42;
    // Exemple d'IPv6 ::1
    uint8_t ip[16] = {0};
    ip[15] = 1;
    uint16_t port = 4242;

    size_t n = build_join_response(id, ip, port, buf);
    printf("Built %zu bytes, code=%u\n", n, buf[0]);

    uint16_t rid; uint8_t rip[16]; uint16_t rport;
    int rc = parse_join_response(buf, n, &rid, rip, &rport);
    if (rc != 0) {
        printf("parse_join_response error %d\n", rc);
        return 1;
    }
    printf("Parsed id=%u, ip=…:%u, port=%u\n",
           rid, rip[15], rport);
    return 0;


    //=============FIN TEST BUILD ET PARSING DE CODE 4======================
    */
    /*
    //=============TEST BUILD ET PARSING DE CODE 3 =========================
    uint8_t buf[16];
    size_t n = build_join_request(buf);
    printf("Built %zu byte(s), buf[0]=%u\n", n, buf[0]);

    int rc = parse_join_request(buf, n);
    printf("parse_join_request returned %d\n", rc == 0 ? 0 : rc);
    return 0;
    //=============FIN TEST BUILD ET PARSING DE CODE 3 =====================
    */

    /*
    //=============TEST BUILD ET PARSING DE CODE 2 (CONSENCUS)=========================
    //Génération de deux paires ED25519 pour simuler deux validateurs
    EVP_PKEY_CTX *kctx1 = EVP_PKEY_CTX_new_id(EVP_PKEY_ED25519, NULL);
    EVP_PKEY_CTX *kctx2 = EVP_PKEY_CTX_new_id(EVP_PKEY_ED25519, NULL);
    EVP_PKEY *pkey1 = NULL, *pkey2 = NULL;

    if (EVP_PKEY_keygen_init(kctx1) != 1 ||
        EVP_PKEY_keygen(kctx1, &pkey1) != 1 ||
        EVP_PKEY_keygen_init(kctx2) != 1 ||
        EVP_PKEY_keygen(kctx2, &pkey2) != 1) {
        fprintf(stderr, "Key generation failed\n");
        return 1;
    }

    // Extraction des clés privées brutes (32 octets each)
    uint8_t sk1[ED25519_SK_LEN], sk2[ED25519_SK_LEN];
    size_t sk1len = sizeof(sk1), sk2len = sizeof(sk2);
    EVP_PKEY_get_raw_private_key(pkey1, sk1, &sk1len);
    EVP_PKEY_get_raw_private_key(pkey2, sk2, &sk2len);
    
    #define NB 2
    uint16_t peer_ids[NB]        = {5, 6};
    const uint8_t *peer_sigs[NB];  // on remplira juste après

    uint8_t payload[] = "ENCHERE#42=150"; // Payload simulé
    uint8_t payload_len = (uint8_t)strlen((char*)payload);

    // Signatures avec ed25519_sign des validateurs
    uint8_t sig1[MSG_SIG_LEN], sig2[MSG_SIG_LEN];
    size_t slen = MSG_SIG_LEN;
    if (ed25519_sign(payload, payload_len, sig1, &slen, sk1) != 0 ||
        ed25519_sign(payload, payload_len, sig2, &slen, sk2) != 0) {
        fprintf(stderr, "ed25519_sign failed\n");
        return 1;
    }
    peer_sigs[0] = sig1;
    peer_sigs[1] = sig2;

    // Clé privée du superviseur (ici, on prend un sk factice de 32 octets à zéro)
    uint8_t sk_sup[ED25519_SK_LEN] = {0};

    //Construire message concensus
    uint8_t buf[512];
    size_t len = build_consensus(
        payload, payload_len,
        2,       // ID du superviseur
        sk_sup,               // sa clé privée
        peer_ids, peer_sigs,  // IDs + signatures des validateurs
        NB,
        buf
    );
    printf("Built consensus message of %zu bytes\n", len);

    //DESERIALISAITION
    uint16_t out_sup_id;
    uint8_t out_payload[256], out_len;
    uint8_t out_sup_sig[MSG_SIG_LEN];
    uint16_t out_ids[NB];
    uint8_t *out_sigs[NB];
    uint8_t out_nb;

    int rc = parse_consensus(
        buf, len,
        &out_sup_id,
        out_payload, &out_len,
        out_sup_sig,
        out_ids, out_sigs, &out_nb
    );
    if (rc != 0) {
        printf("parse_consensus failed: %d :(\n", rc);
        return 1;
    }
    // Vérifications
    if (out_sup_id != 2 || out_len != payload_len || memcmp(out_payload, payload, payload_len) != 0) {
        printf("mismatch\n");
        return 1;
    }
    for (int i = 0; i < NB; i++) {
        if (out_ids[i] != peer_ids[i] ||
            memcmp(out_sigs[i], peer_sigs[i], MSG_SIG_LEN) != 0)
        {
            printf("Peer %d mismatch\n", i);
            return 1;
        }
    }
    printf("parse_consensus OK\n");
    return 0;

    

    EXPECTED RESULT:
    Built consensus message of 218 bytes
    parse_consensus OK

    218 car:
        1 octet CODE
        2 octets sup_id
        1 octet payload_len
        14 octets de payload ("ENCHERE#42=150")
        1 octet LSIG_sup
        64 octets de signature du superviseur
        1 octet NB
        pour les des 2 validateurs :
            2 octets peer_id
            1 octet LSIG
            64 octets de SIG
    */

    //=============FIN TEST BUILD ET PARSING DE CODE 2 (CONSENCUS)======================
    

    /*
    //=============TEST BUILD ET PARSING DE CODE 1 (VALIDATION)=========================
    // Clé privée factice (64 octets à zéro) pour tester
    uint8_t sk[MSG_SIG_LEN] = {0};
    // Payload de test
    uint8_t payload[] = "TEST";
    uint8_t buf[128];
    // 1) Build
    size_t len = build_validation(42, payload, sizeof(payload)-1, sk, buf);
    printf("Built %zu bytes\n", len);
    // 2) Parse
    uint16_t id;
    uint8_t pl[128], pl_len;
    uint8_t sig[MSG_SIG_LEN];
    int rc = parse_validation(buf, len, &id, pl, &pl_len, sig);
    if (rc == 0) {
        printf("Parsed id=%u, payload='%.*s', ok.\n", id, pl_len, pl);
    } else {
        printf("parse_validation error %d\n", rc);
    }
    return 0;
    //=============FIN TEST BUILD ET PARSING DE CODE 1 (VALIDATION)=========================
    */

}
