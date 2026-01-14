#include "../include/connexions.h"

//Liste des pairs présents
AdressePerso peer_list[MAX_PEERS];
int peer_count = 0;

AdressePerso monAdresse;

pthread_mutex_t mutex_fin_enchere = PTHREAD_MUTEX_INITIALIZER;
pthread_cond_t cond_fin_enchere = PTHREAD_COND_INITIALIZER;
int enchere_terminee = 0;

SSL_CTX *server_ssl_ctx = NULL;
SSL_CTX *client_ssl_ctx = NULL;

uint8_t ma_cle_pub[ED25519_PK_LEN];
uint8_t ma_cle_sec[ED25519_SK_LEN];


char interface_name[IF_NAMESIZE] = "";  // pour getifaddrs


//Fonction de routine lancé dans le main pour vérifier si le thread d'enchère a terminé, si oui on exit
void* attente_fin_enchere(void* arg) {
    (void)arg;
    pthread_mutex_lock(&mutex_fin_enchere);
    while (!enchere_terminee) {
        pthread_cond_wait(&cond_fin_enchere, &mutex_fin_enchere);
    }
    pthread_mutex_unlock(&mutex_fin_enchere);

    printf("Enchère terminée, fermeture du programme.\n");
    //exit(EXIT_SUCCESS);
    pthread_exit(NULL);
}



/*Fonction d'aide pour gérer les erreurs OpenSSL*/
void handle_ssl_errors()
{
    ERR_print_errors_fp(stderr);
}

/*Fonction qui initialise les contextes SSL*/
void init_ssl_ctx()
{
    SSL_library_init();
    OpenSSL_add_all_algorithms();
    SSL_load_error_strings();
    /*On configure le contexte serveur*/
    server_ssl_ctx = SSL_CTX_new(TLS_server_method());
    if(!server_ssl_ctx){
        handle_ssl_errors();
        exit(EXIT_FAILURE);
    }
    if(SSL_CTX_use_certificate_file(server_ssl_ctx, SERVER_CERT_PATH, SSL_FILETYPE_PEM) <= 0){
        handle_ssl_errors();
        exit(EXIT_FAILURE);
    }
    if(SSL_CTX_use_PrivateKey_file(server_ssl_ctx, SERVER_KEY_PATH, SSL_FILETYPE_PEM) <= 0){
        handle_ssl_errors();
        exit(EXIT_FAILURE);
    }
    if(!SSL_CTX_check_private_key(server_ssl_ctx)){
        fprintf(stderr, "La clé privée ne correspond pas au certificat de la clé publique\n");
        exit(EXIT_FAILURE);
    }
    /*Optionnel, mais on demande et vérifie le certificat client*/
    SSL_CTX_set_client_CA_list(server_ssl_ctx, SSL_load_client_CA_file(CA_CERT_PATH));
    SSL_CTX_load_verify_locations(server_ssl_ctx, CA_CERT_PATH, NULL);
    SSL_CTX_set_verify(server_ssl_ctx, SSL_VERIFY_PEER | SSL_VERIFY_FAIL_IF_NO_PEER_CERT, NULL);
    /*On passe à la configuration du contexte client*/
    client_ssl_ctx = SSL_CTX_new(TLS_client_method());
    if(!client_ssl_ctx){
        handle_ssl_errors();
        exit(EXIT_FAILURE);
    }
    /*On charge le certificat "CA" pour vérifier le serveur*/
    if(!SSL_CTX_load_verify_locations(client_ssl_ctx, CA_CERT_PATH, NULL)){
        handle_ssl_errors();
        exit(EXIT_FAILURE);
    }
    /*C'est optionnel ci-dessous, mais on peut faire une authentification mutuelle
    en chargeant le certificat et la clé privée du client*/
    if(SSL_CTX_use_certificate_file(client_ssl_ctx, SERVER_CERT_PATH, SSL_FILETYPE_PEM) <= 0){
        handle_ssl_errors();
        /*C'est potentiellement non bloquant si le serveur n'exige pas l'authentification client
        mais pour un P2P, c'est préférable.*/
        fprintf(stderr, "Warning: Echec du chargement du certificat client pour l'authentification mutuelle.\n");
    }
    if(SSL_CTX_use_PrivateKey_file(client_ssl_ctx, SERVER_KEY_PATH, SSL_FILETYPE_PEM) <= 0){
        handle_ssl_errors();
        fprintf(stderr, "Warning: Echec du chargement de la clé privée du client pour l'authentification mutuelle.\n");
    }
    SSL_CTX_set_verify(client_ssl_ctx, SSL_VERIFY_PEER, NULL); /*On vérifie le certificat du serveur*/
}

void cleanup_ssl_ctx() {
    if(server_ssl_ctx)
    {
        SSL_CTX_free(server_ssl_ctx);
    }
    if(client_ssl_ctx)
    {
        SSL_CTX_free(client_ssl_ctx);
    }
    EVP_cleanup();
    ERR_free_strings();
}

//Fonction pour afficher la liste des pairs présents dans le système
void afficher_liste(AdressePerso *liste, size_t taille) {
    for (size_t i = 0; i < taille; ++i) {
        printf("Adresse %zu : IP = %s, PORT = %d, ID = %u\n",
               i, liste[i].ip, liste[i].PORT, liste[i].id);
    }
}

//Initialiser les champs de son adressePerso
void initialiserAdressePerso(int argc, char *argv[]) {
    mkdir("src/keys", 0755);  // Ne fait rien si le dossier existe déjà

    if (argc < 2) {
        fprintf(stderr, "Usage: %s <port>\n", argv[0]);
        exit(EXIT_FAILURE);
    }

    monAdresse.PORT = atoi(argv[1]);
    if (monAdresse.PORT <= 0 || monAdresse.PORT > 65535) {
        fprintf(stderr, "Port invalide : %s\n", argv[1]);
        exit(EXIT_FAILURE);
    }

    //Automatisation du remplissage de l'addresse IPv6 local link sur eth0
    struct ifaddrs *ifaddr, *ifa;
    char last_fe80_ip[INET6_ADDRSTRLEN] = "";
    char last_interface[IF_NAMESIZE] = "";

    if (getifaddrs(&ifaddr) == -1) {
        perror("getifaddrs");
        exit(EXIT_FAILURE);
    }

    for (ifa = ifaddr; ifa != NULL; ifa = ifa->ifa_next) {
        if (ifa->ifa_addr &&
            ifa->ifa_addr->sa_family == AF_INET6 &&
            !(ifa->ifa_flags & IFF_LOOPBACK)&&
            strcmp(ifa->ifa_name, "eth0") == 0) {

            struct sockaddr_in6 *sa = (struct sockaddr_in6 *)ifa->ifa_addr;
            const uint8_t *bytes = (const uint8_t *)&sa->sin6_addr;

            if (bytes[0] == 0xfe && (bytes[1] & 0xc0) == 0x80) {
                char temp_ip[INET6_ADDRSTRLEN];
                inet_ntop(AF_INET6, &sa->sin6_addr, temp_ip, sizeof(temp_ip));
                strncpy(last_fe80_ip, temp_ip, INET6_ADDRSTRLEN);
                strncpy(last_interface, ifa->ifa_name, IF_NAMESIZE);
            }
        }
    }

    freeifaddrs(ifaddr);

    if (strlen(last_fe80_ip) == 0) {
        fprintf(stderr, "Aucune adresse link-local IPv6 (fe80::) détectée.\n");
        exit(EXIT_FAILURE);
    }

    strncpy(monAdresse.ip, last_fe80_ip, INET6_ADDRSTRLEN);
    strncpy(interface_name, last_interface, IF_NAMESIZE);

    srand((unsigned) time(NULL));
    monAdresse.id = (uint16_t) (rand() % 65536);


    printf("\n==================== Mes informations personnelles ====================\n");
    printf("Adresse link-local : %s\n", monAdresse.ip);
    printf("Port               : %d\n", monAdresse.PORT);
    printf("Interface utilisée : etho0\n");
    printf("ID : %"PRIu16 "\n", monAdresse.id);

    printf("\n==================== Génération de clés  ====================\n");
    char fichier_priv[64], fichier_pub[64];
    snprintf(fichier_priv, sizeof(fichier_priv), "src/keys/cle_privee_%d.pem", monAdresse.PORT);
    snprintf(fichier_pub, sizeof(fichier_pub), "src/keys/cle_publique_%d.pem", monAdresse.PORT);
    // Temporaire : buffer de chargement
    uint8_t cle_sec[ED25519_SK_LEN] = {0};  // <- 64 octets
    uint8_t cle_pub[ED25519_PK_LEN] = {0};  // <- 32 octets
    // Charger ou générer les clés (fonction d/ keys.c)
    charger_cles_fichiers(cle_sec, cle_pub, fichier_priv, fichier_pub);
    // Copier la clé publique dans monAdresse
    memcpy(monAdresse.cle_pub, cle_pub, ED25519_PK_LEN);
    if(charger_cle_publique_raw(monAdresse.cle_pub, monAdresse.PORT)!=0){
        fprintf(stderr, "IMPOSSIBLE DE CHARGER CLE\n");
    }
    memcpy(ma_cle_sec, cle_sec, ED25519_SK_LEN);

    // DEBUG
    //printf("Clé secrète (début) = %02x %02x %02x %02x\n", ma_cle_sec[0], ma_cle_sec[1], ma_cle_sec[2], ma_cle_sec[3]);
    //printf("Clé publique (début) = %02x %02x %02x %02x\n", monAdresse.cle_pub[0], monAdresse.cle_pub[1],monAdresse.cle_pub[2], monAdresse.cle_pub[3]);

    //S'ajouter en première position de peer_list
    strcpy(peer_list[peer_count].ip, monAdresse.ip);
    peer_list[peer_count].PORT = monAdresse.PORT;
    peer_list[peer_count].id=monAdresse.id;
    memcpy(peer_list[peer_count].cle_pub, monAdresse.cle_pub, ED25519_PK_LEN);

    peer_count++; 
    printf("\n==================== Ajout de soi même dans peer_list ====================\n");
    printf("[init] Pair local ajouté à peer_list : IP=%s, PORT=%d, ID=%u\n",
       monAdresse.ip, monAdresse.PORT, monAdresse.id);
}

//Fonction qui génère un nouveau id unique dans le système si on a reçu le code 51 
uint16_t generate_unique_id(){
    uint16_t id;
    int flag=0;
    while(1){
        flag=0;
        id = (uint16_t)(rand() % 65536);
        for (int i =0; i<peer_count;i++){
            if (peer_list[i].id==id){
                flag=1;
            }
        }
        if (!flag){
            return id;
        }
    }
}



//Envoyer les infos du nouveau pair aux anciens pairs
void send_new_user(){
    printf("\n==== Envoi CODE=6 aux pairs existants ====\n");

    int indice_nvx_peer=peer_count-1;
    
    for (int i=1;i<peer_count-1;i++){ //1 pour exclure soit même //-1 pour exclure le nouveau
        // On ignore soi-même et le nouveau pair (au cas ou il y a eu un problème d'indexes...?)
        if (peer_list[i].id == monAdresse.id || peer_list[i].id == peer_list[indice_nvx_peer].id) {
            continue;
        }
        else{
            printf("Préparation de l'envoi à ID=%"PRIu16", IP=%s, PORT=%d\n",peer_list[i].id, peer_list[i].ip, peer_list[i].PORT);
            
            int tcp_transmission_socket = socket(AF_INET6, SOCK_STREAM, 0); 
            if (tcp_transmission_socket < 0) {
                perror("socket TCP");
                return;
            }
            int opt = 1;
            setsockopt(tcp_transmission_socket, SOL_SOCKET, SO_REUSEADDR, &opt, sizeof(opt));

            struct sockaddr_in6 server_addr;
            memset(&server_addr,0,sizeof(server_addr));
            server_addr.sin6_family = AF_INET6;
            server_addr.sin6_port = htons(peer_list[i].PORT+1); //+1 pour différencier avec le port qui écoutait le code 5
            server_addr.sin6_scope_id = if_nametoindex("eth0");
            
            if (inet_pton(AF_INET6, peer_list[i].ip, &server_addr.sin6_addr) != 1) {
                fprintf(stderr, "inet_pton invalide pour %s\n", peer_list[i].ip);
                close(tcp_transmission_socket);
                continue;
            }

            if (connect(tcp_transmission_socket,(struct sockaddr*)&server_addr,sizeof(server_addr))<0){
                perror("Connect send_new_user");
                close(tcp_transmission_socket);
                return;
            }

            /*Bug*/
            /*Initialisation et handshake du côté client*/
            /*SSL *ssl_conn = SSL_new(client_ssl_ctx);
            if(!ssl_conn)
            {
                handle_ssl_errors();
                close(tcp_transmission_socket);
                continue;
            }
            SSL_set_fd(ssl_conn, tcp_transmission_socket);
            if(SSL_connect(ssl_conn) <= 0)
            {
                fprintf(stderr, "Echec du TLS handshake dans send_new_user()\n");
                handle_ssl_errors();
                SSL_free(ssl_conn);
                close(tcp_transmission_socket);
                continue;
            }
            printf("TLS handshake réussi pour l'envoi du CODE 6\n");
            */
            
            uint8_t msg[134];

            struct in6_addr addr_bin;
            inet_pton(AF_INET6, peer_list[indice_nvx_peer].ip, &addr_bin);

            size_t len = build_send_new_peer(
                peer_list[indice_nvx_peer].id,
                (uint8_t*)&addr_bin,
                peer_list[indice_nvx_peer].PORT,
                peer_list[indice_nvx_peer].cle_pub,
                msg
            );

            /*
            if(SSL_write(ssl_conn, msg, len) <= 0)
            {
                perror("SSL_write send_new_user");
                handle_ssl_errors();
            }
            */

            if (send(tcp_transmission_socket,msg,len,0)<=0){
                perror("send send_new_user");
            }
            else
            {
                printf("CODE 6 envoyé à ID=%"PRIu16"\n", peer_list[i].id);
            }
            /*Libération de l'objet SSL*/
            //SSL_free(ssl_conn);
            close(tcp_transmission_socket);
        }
    }
}



//Recevoir les code 5 et envoyer les code 50/51, puis code 7 après avoir appelé la fonction send_new_user()
void* handle_tcp_client(void* arg) {
    (void)arg;  //pour éviter warning
    
        int handle_server_socket=socket(AF_INET6, SOCK_STREAM, 0);
        if (handle_server_socket < 0) {
            perror("socket handle_server_socket");
            pthread_exit(NULL);
        }

        int opt = 1;
        setsockopt(handle_server_socket, SOL_SOCKET, SO_REUSEADDR, &opt, sizeof(opt));

        struct sockaddr_in6 server_addr = {0};
        server_addr.sin6_family = AF_INET6;
        server_addr.sin6_addr = in6addr_any;
        server_addr.sin6_port = htons(monAdresse.PORT+2);

        if (bind(handle_server_socket, (struct sockaddr *)&server_addr, sizeof(server_addr)) < 0) {
            perror("bind handle_TCP");
            close(handle_server_socket);
            pthread_exit(NULL);
        }

        if (listen(handle_server_socket, MAX_PEERS) < 0) {
            perror("listen handle_TCP");
            close(handle_server_socket);
            pthread_exit(NULL);
        }


    while(1){

        int handle_client_sock;
        struct sockaddr_in6 handle_client_addr;
        socklen_t client_len = sizeof(handle_client_addr);

        //Accept bloquant, on ne fait rien tant qu'un nouveau pair ne nous choisi pas
        handle_client_sock = accept(handle_server_socket, (struct sockaddr*)&handle_client_addr, &client_len);
        if (handle_client_sock < 0) {
            perror("accept handle_tcp_client");
            return NULL;
        }

        /*Inititalisation et handshake du côté serveur*/
        SSL *ssl_conn = SSL_new(server_ssl_ctx);
        if(!ssl_conn)
        {
            handle_ssl_errors();
            close(handle_client_sock);
            continue;
        }
        SSL_set_fd(ssl_conn, handle_client_sock);
        if(SSL_accept(ssl_conn) <= 0)
        {
            fprintf(stderr, "Echec du handshake TLS dans handle_tcp_client\n");
            handle_ssl_errors();
            SSL_free(ssl_conn);
            close(handle_client_sock);
            continue;
        }
        printf("Handshake TLS réussi pour une connexion entrante sur le port %d\n", monAdresse.PORT+2);

        uint8_t buf[BUFFER_SIZE];

        ssize_t r = SSL_read(ssl_conn, buf, BUFFER_SIZE);
        if (r<0){
            perror("SSL_read code 5");
            handle_ssl_errors();
        }else if (r==0){
            printf("Connexion TLS fermée par le client\n");
        }else{
            printf("Données reçues (%ld octets), code = %d\n", r, buf[0]);

            if (r >= 134 && buf[0] == CODE_JOIN_TCP) {
                uint16_t id;
                uint8_t ip[16];
                uint16_t remote_port;
                uint8_t cle_pub[MSG_CLE_LEN];

                if (parse_join_tcp(buf, r, &id, ip, &remote_port, cle_pub) == 0) {
                    char ip_str[INET6_ADDRSTRLEN];
                    inet_ntop(AF_INET6, ip, ip_str, sizeof(ip_str));
                    printf("\n==================== CODE 5 reçu ====================\n");
                    printf("CODE=5 reçu : ID=%u, IP=%s, PORT=%u\n", id, ip_str, remote_port);
                    
                    int flag=0;
                    for (int i=0;i<peer_count;i++){
                        if (id==peer_list[i].id){
                            flag=1;
                        }
                    }
                    uint8_t buf_send[3];
                    //memset(buf_send,0,sizeof(buf_send));
                    size_t len_send=0;
                    if (flag){
                        uint16_t new_id = generate_unique_id();
                        len_send = build_new_id(new_id, buf_send);
                        id = new_id;  // on l'utilise ensuite pour l’ajout dans la peer_list
                        printf("ID déjà utilisé, envoi CODE=51 avec nouvel ID = %u\n", new_id);
                    }else{
                        len_send = build_id_ok(buf_send);
                        printf("ID disponible, envoi CODE=50\n");
                    }
                    
                    if(SSL_write(ssl_conn, buf_send, sizeof(uint16_t)*2) <= 0)
                    {
                        perror("Erreur SSL_write code 50/51\n");
                        handle_ssl_errors();
                    }
                    else
                    {
                        printf("CODE 50/51 envoyé avec succès (%zu octets)\n", len_send);
                    }
                    char new_ip_str[INET6_ADDRSTRLEN];
                    inet_ntop(AF_INET6, ip, new_ip_str, sizeof(new_ip_str));

                    AdressePerso nouveau_pair = {
                        .id = id,
                        .PORT = remote_port
                    };
                    strncpy(nouveau_pair.ip, new_ip_str, INET6_ADDRSTRLEN);
                    
                    //En fait il faudrait utiliser PeerInfo je crois
                    if(peer_count < MAX_PEERS)
                    {
                        peer_list[peer_count]=nouveau_pair;
                        peer_count++;
                        printf("Nouveau pair ID: %"PRIu16 " ajouté à peer_list\n", id);
                    }
                    else
                    {
                        fprintf(stderr, "Liste des pairs pleine, impossible d'ajouter le nouveau pair.\n");
                    }
                    

                    send_new_user();

                    
                    //Envoie de l'état complet du système au nouveau pair
                    printf("\n==== Envoi CODE=7 au nouveau pair ====\n");

                    uint16_t ids[MAX_PEERS];
                    uint8_t ips[MAX_PEERS][16];
                    uint16_t ports[MAX_PEERS];
                    uint8_t cle_pubs[MAX_PEERS][113];

                    for (int i = 0; i < peer_count; ++i) {
                        ids[i] = peer_list[i].id;

                        struct in6_addr addr_bin;
                        if (inet_pton(AF_INET6, peer_list[i].ip, &addr_bin) != 1) {
                            fprintf(stderr, "Erreur inet_pton : IP %s\n", peer_list[i].ip);
                            continue;
                        }
                        memcpy(ips[i], &addr_bin, 16);

                        ports[i] = peer_list[i].PORT;
                        memcpy(cle_pubs[i], peer_list[i].cle_pub, 113);  // À adapter si champ absent
                    }

                    uint8_t ench_addr[16];
                    inet_pton(AF_INET6, ADDR_ENCHERES, ench_addr);
                    uint8_t msg_code7[2048]; // buffer suffisant
                    size_t len_code7 = build_infos_system(
                        ench_addr,
                        monAdresse.PORT, // Ou le port multicast des enchères
                        peer_count,
                        ids,
                        ips,
                        ports,
                        cle_pubs,
                        msg_code7
                    );

                    if(SSL_write(ssl_conn, msg_code7, len_code7) <= 0)
                    {
                        perror("SSL_write CODE 7");
                        handle_ssl_errors();
                    }
                    else
                    {
                        printf("CODE 7 envoyé\n");
                    }
                } else {
                    fprintf(stderr, "Erreur de parsing du message CODE=5\n");
                }
            } else {
                fprintf(stderr, "Erreur de parsing du message CODE=5, fermeture de la connexion.\n");
                SSL_free(ssl_conn);
                close(handle_client_sock);
                continue; 
            }
        }
        SSL_free(ssl_conn);
        close(handle_client_sock); 
    }
    close(handle_server_socket);
    pthread_exit(NULL);
}


//Recevoir les infos sur les nouveaux pairs (code 6)
void* receive_tcp_client(void* arg){
    (void)arg;  // pour éviter warning

    int temp_sock = socket(AF_INET6, SOCK_STREAM, 0);
    if (temp_sock < 0) {
        perror("socket TCP receive_tcp_client");
        pthread_exit(NULL);
    } else {
        struct sockaddr_in6 temp_addr = {0};
        temp_addr.sin6_family = AF_INET6;
        temp_addr.sin6_addr = in6addr_any;
        temp_addr.sin6_port = htons(monAdresse.PORT + 1); //+1 pour différencier avec le port qui écoutait le code 5

        int opt = 1;
        setsockopt(temp_sock, SOL_SOCKET, SO_REUSEADDR, &opt, sizeof(opt));

        if (bind(temp_sock, (struct sockaddr*)&temp_addr, sizeof(temp_addr)) < 0) {
            perror("bind socket receive_tcp_client");
            close(temp_sock);
        } else if (listen(temp_sock, MAX_PEERS) < 0) {
            perror("listen socket receive_tcp_client");
            close(temp_sock);
        } else {

            while(1){
                printf("En écoute sur port %d pour recevoir les infos des nouveaux pairs\n", monAdresse.PORT + 1);

                struct sockaddr_in6 facteur;
                socklen_t addrlen = sizeof(facteur);
                int new_peer_sock = accept(temp_sock, (struct sockaddr*)&facteur, &addrlen);
                if (new_peer_sock < 0) {
                    perror("accept nouveau pair");
                    continue;
                } else {
                    
                    /*Partie TLS (bug)*/
                    /*Initialisation et handshake coté serveur*/
                    /*SSL *ssl_conn = SSL_new(server_ssl_ctx);
                    if (!ssl_conn)
                    {
                        handle_ssl_errors();
                        close(new_peer_sock);
                        continue;
                    }
                    SSL_set_fd(ssl_conn, new_peer_sock);
                    if (SSL_accept(ssl_conn) <= 0)
                    {
                        fprintf(stderr, "Echec du handshake TLS pour receive_tcp_client\n");
                        handle_ssl_errors();
                        SSL_free(ssl_conn);
                        close(new_peer_sock);
                        continue;
                    }
                    printf("TLS handshake réussi pour une connexion entrante sur le port %d.\n", monAdresse.PORT+1);



                    uint8_t buffer[BUFFER_SIZE];
                    ssize_t recvd = SSL_read(ssl_conn, buffer, BUFFER_SIZE);
                    printf("APRES SSL_read RECEIVE_TCP_CLIENT\n");
                    if (recvd > 0) {
                        uint16_t id;
                        uint8_t ip[16];
                        uint16_t remote_port;
                        uint8_t cle_pub[MSG_CLE_LEN];
                        if (parse_send_new_peer(buffer, sizeof(buffer), &id, ip, &remote_port, cle_pub) == 0) {

                            AdressePerso nouveau_pair;

                            //strncpy(nouveau_pair.ip, ip, INET6_ADDRSTRLEN);
                            //memcpy(nouveau_pair.ip, ip, 16);
                            inet_ntop(AF_INET6, ip, nouveau_pair.ip, INET6_ADDRSTRLEN);

                            nouveau_pair.PORT = remote_port;
                            nouveau_pair.id = id;
                            peer_list[peer_count]=nouveau_pair;
                            peer_count++;

                            printf("Nouveau pair ID : %"PRIu16 " ajouté a peer_list\n",id);
                            afficher_liste(peer_list,MAX_PEERS);
                        }
                    }else{
                        perror("SSL_read_new_peer_sock");
                        handle_ssl_errors();
                    }
                    SSL_free(ssl_conn);
                    close(new_peer_sock); */


                    uint8_t buffer[BUFFER_SIZE];
                    ssize_t recvd = recv(new_peer_sock, buffer, BUFFER_SIZE, 0);
                    
                    if (recvd > 0) {
                        uint16_t id;
                        uint8_t ip[16];
                        uint16_t remote_port;
                        uint8_t cle_pub[MSG_CLE_LEN];
                        if (parse_send_new_peer(buffer, sizeof(buffer), &id, ip, &remote_port, cle_pub) == 0) {
                            AdressePerso nouveau_pair;
                            inet_ntop(AF_INET6, ip, nouveau_pair.ip, INET6_ADDRSTRLEN);
                            nouveau_pair.PORT = remote_port;
                            nouveau_pair.id = id;
                            
                            if(peer_count < MAX_PEERS) {
                                peer_list[peer_count] = nouveau_pair;
                                peer_count++;
                                printf("Nouveau pair ID : %"PRIu16 " ajouté a peer_list\n", id);
                                afficher_liste(peer_list, peer_count);
                            }else{
                                printf("Nombre de pair maximal atteint ! \n");
                            }
                        }
                    } else {
                        perror("recv new_peer_sock");
                    }
                    close(new_peer_sock);
                }
            }
        }
        close(temp_sock);
        pthread_exit(NULL);
    }
}


//Crée un socket UDP IPv6 et s'abonne au groupe multicast
void* listen_for_peers(void* arg){
    (void)arg;  //pour éviter warning
    int sockfd;
    struct sockaddr_in6 addr;
    struct ipv6_mreq mreq; //Pour l'abonnement au groupe multicast
    char buffer[BUFFER_SIZE];

    sockfd= socket(AF_INET6, SOCK_DGRAM, 0); //DGRAM pour protocole UDP
    if (sockfd < 0) {
        perror("socket");
        pthread_exit(NULL);
    }

    int ok=1;
    setsockopt(sockfd,SOL_SOCKET, SO_REUSEADDR, &ok, sizeof(ok)); //Diapo 20, je pense que c'est utile

    memset(&addr, 0, sizeof(addr));
    addr.sin6_family=AF_INET6;
    addr.sin6_port=htons(port_connexion);
    addr.sin6_addr=in6addr_any;

    if (bind(sockfd, (struct sockaddr*)&addr, sizeof(addr)) < 0) {
        perror("bind");
        close(sockfd);
        pthread_exit(NULL);
    }
    
    //S'abonne au groupe multicast
    inet_pton(AF_INET6, ADDR_MULTICAST, &mreq.ipv6mr_multiaddr);
    mreq.ipv6mr_interface = 0; //interface par d"faut, fonctionne sur lulu
    if(setsockopt(sockfd,IPPROTO_IPV6, IPV6_JOIN_GROUP, &mreq, sizeof(mreq))<0){
        perror("setsockopt abonnement multicast");
        close(sockfd);
        pthread_exit(NULL); 
    }
    printf("En attente de requêtes UDP (CODE=3)...\n");


    //Thread pour gérer la réception du code 5, l'envoi du code 50/51, l'envoi des codes 6 et 7
    pthread_t handle_tcp_thread;
    if (pthread_create(&handle_tcp_thread, NULL, handle_tcp_client, NULL) != 0) {
        perror("pthread_create (handle_tcp_client)");
        close(sockfd);
        return NULL;
    }
    //Pour qu'il quitte tout seul quand il termine, pas besoin de bloquer sur un join
    pthread_detach(handle_tcp_thread);


    //Thread pour gérer la réception du code 6
    pthread_t receive_tcp_thread;
    if (pthread_create(&receive_tcp_thread, NULL, receive_tcp_client, NULL) != 0) {
        perror("pthread_create (receive_tcp_client)");
        close(sockfd);
        return NULL;
    }

    pthread_detach(receive_tcp_thread);


    //Thread redirigeant vers enchere.c
    pthread_t pthread_enchere;
    if (pthread_create(&pthread_enchere, NULL, thread_enchere, &monAdresse) != 0) {
        perror("pthread_create (pthread_enchere)");
        close(sockfd);
        return NULL;
    }

    pthread_detach(pthread_enchere);


    while(1){
        struct sockaddr_in6 src_addr;
        socklen_t tailleaddr = sizeof(src_addr);
        ssize_t taille = recvfrom(sockfd, buffer, BUFFER_SIZE, 0, (struct sockaddr *)&src_addr, &tailleaddr); 
    
        if (taille<=0){
            perror("recvfrom");
            continue;
        }else{
            printf("Message reçu\n");
        }

        if (parse_join_request((const uint8_t *)buffer, taille) == 0){ //3=demande d'entrée
            printf("Requête CODE=3 reçue\n");

            uint8_t reponse[21];
            struct in6_addr my_ip;
            if (inet_pton(AF_INET6, monAdresse.ip, &my_ip) != 1) {
                perror("inet_pton");
                continue;
            }

            if (peer_count<MAX_PEERS){
                size_t len = build_join_response(monAdresse.id, (uint8_t*)&my_ip, monAdresse.PORT, reponse);


                src_addr.sin6_scope_id = if_nametoindex("eth0");
                if (sendto(sockfd, reponse, len,0, (struct sockaddr*)&src_addr, tailleaddr)<0){
                    perror("sendto réponse CODE=4");
                }
                else{
                    printf("Réponse CODE=4 envoyée\n");
                }
            }else{
                printf("Nombre de pair maximal atteint ! \n");
            }

        }
    }
    close(sockfd);
    pthread_exit(NULL);
}



//Envoyer le code 5 à l'envoyeur du premier code 4 reçu
int connect_to_peer(AdressePerso peer_info) {
    int sockfd;
    struct sockaddr_in6 addr;
    AdressePerso peer = peer_info;

    //socket pour code 5 , 50/51 et 7
    sockfd = socket(AF_INET6, SOCK_STREAM, 0);
    if (sockfd < 0) {
        perror("socket");
        return -1;
    }

    memset(&addr, 0, sizeof(addr));
    addr.sin6_family = AF_INET6;
    addr.sin6_port = htons(peer.PORT+2);
    if (inet_pton(AF_INET6, peer.ip, &addr.sin6_addr)<=0){
        perror("inet_pton");
        close(sockfd);
        return -1;
    }
    addr.sin6_scope_id = if_nametoindex("eth0"); //Obligatoire pour que connect fonctionne

    if (connect(sockfd, (struct sockaddr*)&addr, sizeof(addr)) < 0) {
        perror("connect test");
        close(sockfd);
        return -1;
    }
    /*Initialisation et handshake TLS du côté du client*/
    SSL *ssl_conn = SSL_new(client_ssl_ctx);
    if (!ssl_conn)
    {
        handle_ssl_errors();
        close(sockfd);
        return -1;
    }

    //Association de sockfd a la couche TLS
    SSL_set_fd(ssl_conn, sockfd);
    if (SSL_connect(ssl_conn) <= 0)
    {
        fprintf(stderr, "Echec du handshake TLS pour connect_to_peer\n");
        handle_ssl_errors();
        SSL_free(ssl_conn);
        close(sockfd);
        return -1;
    }
    printf("TLS handshake réussi pour la connexion au pair ID=%u.\n", peer.id);

    //Création du message code 5
    uint16_t id=monAdresse.id;
    uint8_t msg[134];
    struct in6_addr my_ip;
    inet_pton(AF_INET6, monAdresse.ip, &my_ip);

    size_t len = build_join_tcp(id, (uint8_t*)&my_ip, monAdresse.PORT, monAdresse.cle_pub, msg);

    if(SSL_write(ssl_conn, msg, len) <=0)
    {
        perror("SSL_write code 5");
        handle_ssl_errors();
        SSL_free(ssl_conn);
        close(sockfd);
        return -1;
    }
    else {
        // printf("\n==== Connexion TCP au pair Q ====\n");
        printf("\n==== Connexion TCP sécurisée au pair Q ====\n");
        printf("Cible : ID=%d | IP=%s | PORT=%d\n",peer.id, peer.ip, peer.PORT);
        uint8_t buf_recv[3];
        // int r = recv(sockfd,buf_recv,3,0);
        int r = SSL_read(ssl_conn, buf_recv, sizeof(uint16_t)*2);
        if (r<=0){
            perror("Reception code 50/51\n");
            handle_ssl_errors();
        }else{
             if (buf_recv[0] == 51) {
                uint16_t new_id = 0;
                if (parse_new_id(buf_recv, r, &new_id) == 0) {
                    monAdresse.id = new_id;
                    printf("Nouveau ID attribué par le réseau : %"PRIu16"\n", new_id);
                } else {
                    fprintf(stderr, "Erreur parsing CODE=51\n");
                }
            } else if (buf_recv[0] == 50) {
                printf("ID accepté\n");
            } else {
                fprintf(stderr, "Réponse inattendue (pas 50/51)\n");
            }
        }
                
        //Réception du code 7
        uint8_t buffer_code7[2048];
        ssize_t len_code7 = SSL_read(ssl_conn, buffer_code7, sizeof(buffer_code7));
        if (len_code7 <= 0) {
            perror("SSL_read CODE=7");
            handle_ssl_errors();
            SSL_free(ssl_conn);
            close(sockfd);
            return -1;
        }

        if (buffer_code7[0] != 7)
        {
            fprintf(stderr, "Erreur : message reçu n'est pas un CODE=7 (valeur = %d)\n", buffer_code7[0]);
            SSL_free(ssl_conn);
            close(sockfd);
            return -1;
        }

        //Stockage des infos du code 7
        uint8_t ench_ip[16];
        uint16_t ench_port;
        uint16_t nb_pairs = 0;
        uint16_t ids[MAX_PEERS];
        uint8_t ips[MAX_PEERS][16];
        uint16_t ports[MAX_PEERS];
        uint8_t cle_pubs[MAX_PEERS][113];

        if (parse_infos_system(buffer_code7, len_code7,
                ench_ip, &ench_port, &nb_pairs,
                ids, ips, ports, cle_pubs) != 0) {
            fprintf(stderr, "Erreur de parsing CODE=7\n");
            close(sockfd);
            return -1;
        }

        printf("==== CODE=7 reçu : %u pairs reçus ====\n", nb_pairs);

        //Ajouter tous les pairs du système dans sa peer_list
        for (uint16_t i = 0; i < nb_pairs; ++i) {
            // Ignorer soi-même
            if (ids[i] == monAdresse.id) continue;

            // Vérifie doublon
            int present = 0;
            for (int j = 0; j < peer_count; ++j) {
                if (peer_list[j].id == ids[i]) {
                    present = 1;
                    break;
                }
            }
            if (present || peer_count >= MAX_PEERS) continue;

            // Ajoute le pair
            peer_list[peer_count].id = ids[i];

            char ip_str[INET6_ADDRSTRLEN];
            inet_ntop(AF_INET6, ips[i], ip_str, sizeof(ip_str));
            strcpy(peer_list[peer_count].ip, ip_str);

            peer_list[peer_count].PORT = ports[i];
            memcpy(peer_list[peer_count].cle_pub, cle_pubs[i], 113);

            printf("Pair ajouté : ID=%u | IP=%s | PORT=%u\n", ids[i], ip_str, ports[i]);
            peer_count++;
        }

        SSL_free(ssl_conn);
        close(sockfd);
        return 1;
    }
}








//Pour envoyer le code 3 et éventuellement recevoir des code 4
int send_entry_request() {
    int sockfd;
    struct sockaddr_in6 addr;

    //Socket UDP sur l'adresse de multicast pour envoyer le code 3
    sockfd = socket(AF_INET6, SOCK_DGRAM, 0);
    if (sockfd < 0) {
        perror("socket UDP");
        return -1;
    }

    memset(&addr, 0, sizeof(addr));
    addr.sin6_family = AF_INET6;
    addr.sin6_port = htons(port_connexion);
    inet_pton(AF_INET6, ADDR_MULTICAST, &addr.sin6_addr);
    addr.sin6_scope_id = if_nametoindex("eth0");

    struct pollfd pfd;
    pfd.fd = sockfd;
    pfd.events = POLLIN;

    uint8_t buf[1];
    size_t len=build_join_request(buf);
    if (sendto(sockfd, buf, len, 0, (struct sockaddr*)&addr, sizeof(addr)) < 0) {
        perror("sendto");
        close(sockfd);
        return -1;
    }
    
    printf("\n==== Envoi CODE=3 au groupe multicast ====\n");
    
    //Attente d'une réponse pendant 1 seconde
    int ready = poll(&pfd, 1, 1000);
    if (ready > 0 && (pfd.revents & POLLIN)) {
        struct sockaddr_in6 from;
        socklen_t fromlen = sizeof(from);
        uint8_t buffer[BUFFER_SIZE];
        ssize_t taille = recvfrom(sockfd, buffer, BUFFER_SIZE, 0, (struct sockaddr*)&from, &fromlen);
        if (taille < 0) {
            perror("recvfrom");
        } 
        uint16_t id;
        uint8_t ip_bin[16];
        uint16_t remote_port;

        if (parse_join_response(buffer, taille, &id, ip_bin, &remote_port) == 0) {
            char ip_str[INET6_ADDRSTRLEN];
            inet_ntop(AF_INET6, ip_bin, ip_str, sizeof(ip_str));


            if (peer_count < MAX_PEERS) {

                //On ne l'ajoute pas car c'est géré avec le code 7
                AdressePerso q_pair;
                strncpy(q_pair.ip, ip_str, INET6_ADDRSTRLEN);
                q_pair.PORT = remote_port;
                q_pair.id = id;

                printf("\n==================== CODE 4 reçu ====================\n");
                printf("Réponse CODE=4 reçue : IP = %s, Port = %d, ID = %d\n", ip_str, remote_port, id);

                close(sockfd);
                return connect_to_peer(q_pair);
            }
        }
    } else {
        printf("Aucune réponse\n");
    }

    close(sockfd);
    return -1;
}



//Fonction qui essaye 3 fois de rentrer dans un système d'enchère
void try_join_auction_system() {
    int attempts = 0;
    const int max_attempts = 3;
    int flag=0;


    while (attempts < max_attempts) {
        printf("\n==================== Tentative join ====================\n");
        printf("Tentative %d/3...\n", attempts + 1);
        int retour = send_entry_request();
        if (retour==1){
            printf("TEST FIN\n");
            flag=1;
            break;
        }
        attempts++;
    }

    //Si au bout de 3 fois pas de réponse alors on crée le système d'enchère
    if (!flag){
        printf("\n==================== Aucune réponse ====================\n");
        printf("Aucune réponse reçue. Ce pair va créer le système d’enchères.\n");
        //im_founder=1;
    }

    //Et on attend un nouveau pair dans listen_for_peers
    pthread_t server_thread;
    if (pthread_create(&server_thread, NULL, listen_for_peers, NULL) != 0) {
        perror("pthread_create");
        exit(1);
    }

}


int main(int argc, char* argv[]) {
    //init_consensus_sockets();
    initialiserAdressePerso(argc,argv);
    init_ssl_ctx(); /*Initialisation des contextes SSL*/
    try_join_auction_system();

    pthread_t thread_attente;
    if (pthread_create(&thread_attente, NULL, attente_fin_enchere, NULL) != 0) {
        perror("pthread_create (thread_attente)");
    }
    pthread_join(thread_attente,NULL);

    cleanup_ssl_ctx(); /*Nettoyer les contextes SSL à la fin*/

    return 0;
}