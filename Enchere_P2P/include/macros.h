#ifndef MACROS_H
#define MACROS_H

//Include
#include <arpa/inet.h>
#include <errno.h>
#include <ifaddrs.h>
#include <inttypes.h>
#include <net/if.h>
#include <openssl/bio.h>
#include <openssl/err.h>
#include <openssl/evp.h>
#include <openssl/pem.h>
#include <openssl/ssl.h>
#include <poll.h>
#include <pthread.h>
#include <stddef.h>
#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/select.h>
#include <sys/socket.h>
#include <sys/stat.h>
#include <sys/time.h>
#include <sys/types.h>
#include <unistd.h>





//Connexions
#define port_connexion 9393
#define port_consensus 4444
#define MAX_PEERS 4
#define BUFFER_SIZE 1024
#define ADDR_MULTICAST "FF12::93:93" //FF=multicast ; 02=locale au lien ; ::93:93 = juste pour en avoir une en particulier
#define ADDR_ENCHERES "FF12::75:75"

/*Définitions pour les chemmins des certificats et clés*/
#define SERVER_CERT_PATH "certs/server.crt"
#define SERVER_KEY_PATH "certs/server.key"
#define CA_CERT_PATH "certs/ca.crt"

#ifndef IFF_LOOPBACK
#define IFF_LOOPBACK 0x8
#endif



//Enchere
#define temps 10



//Msg
#define MSG_SIG_LEN   64   // signature
#define MSG_CLE_LEN 113

// Codes de messages
#define CODE_VAL  1  // validation
#define CODE_CONS 2  // consensus final
#define CODE_JOIN_REQ 3     //code pour demander de join
#define CODE_JOIN_RESP 4    //code pour réponse à demande de join
#define CODE_JOIN_TCP 5     //envoie de message de P à Q
#define CODE_TRANSMISSION 6 //
#define CODE_INFOS_SYST 7   //envoie 
#define CODE_START_ENCH 8   //début de une enchere
#define CODE_ENCH_P 9       //Paire P qui propose l'enchère 
#define CODE_ENCH_S 10      //Paire qui suppervise l'enchère



//keys
// Tailles standard ED25519 (OpenSSL)
#define ED25519_PK_LEN 32
#define ED25519_SK_LEN 64
#define MSG_SIG_LEN    64


#endif // KEYS_H