#include "../include/enchere.h"
#include "../include/consensus.h"

//Compteur perso pour savoir combien de ventes on a initié (pour numv)
uint16_t compteur_ventes=0;

AdressePerso* monAdressePtr;

Enchere liste_encheres[100]={0};
uint16_t compteur_encheres=0; //Compteur pour connaitre la position dans liste_encheres

pthread_mutex_t mutex_encheres = PTHREAD_MUTEX_INITIALIZER;


void* reception_encheres(void* arg){
    (void)arg;
    int sockfd;
    struct ipv6_mreq mreq;
    uint8_t buffer[12];
    //uint8_t buffer2[133];

    sockfd = socket(AF_INET6, SOCK_DGRAM, 0);
    if (sockfd < 0) {
        perror("socket");
        fflush(stderr);
        pthread_exit(NULL);
    }
    // désactive la réception de ses propres paquets multicast
    /*unsigned int loop = 0;
    if (setsockopt(sockfd, IPPROTO_IPV6, IPV6_MULTICAST_LOOP,
                &loop, sizeof(loop)) < 0) {
        perror("setsockopt IPV6_MULTICAST_LOOP");
    }*/


    //Pour recevoir les messages 8 et 9
    struct sockaddr_in6 addr;
    memset(&addr, 0, sizeof(addr));
    addr.sin6_family = AF_INET6;
    addr.sin6_port = htons(8080);
    addr.sin6_addr = in6addr_any;

    //Pour envoyer les messages 10
    struct sockaddr_in6 dest;
    memset(&dest, 0, sizeof(dest));
    dest.sin6_family = AF_INET6;
    dest.sin6_port = htons(8080);
    inet_pton(AF_INET6, ADDR_ENCHERES, &dest.sin6_addr);


    int opt = 1;
    if (setsockopt(sockfd, SOL_SOCKET, SO_REUSEADDR, &opt, sizeof(opt)) < 0) {
        perror("setsockopt(SO_REUSEADDR)");
        fflush(stderr);
        close(sockfd);
        pthread_exit(NULL);
    }

    if (bind(sockfd, (struct sockaddr*)&addr, sizeof(addr)) < 0) {
        perror("bind aaaa");
        fflush(stderr);
        close(sockfd);
        pthread_exit(NULL);
    }

    inet_pton(AF_INET6, ADDR_ENCHERES, &mreq.ipv6mr_multiaddr);
    mreq.ipv6mr_interface = 0;

    if (setsockopt(sockfd, IPPROTO_IPV6, IPV6_JOIN_GROUP, &mreq, sizeof(mreq)) < 0) {
        perror("setsockopt");
        fflush(stderr);
        close(sockfd);
        pthread_exit(NULL);
    }

    while (1) {
        ssize_t len = recvfrom(sockfd, buffer, sizeof(buffer)-1, 0, NULL, NULL);
        if (len > 0) {

            uint8_t code = buffer[0];

            //Gérer l'arrivée d'une nouvelle enchère
            if (code==8){
                uint16_t out_id;
                uint32_t out_numv;
                uint32_t out_prix;
                int parse = parse_auction_start(buffer,sizeof(buffer),&out_id,&out_numv,&out_prix);

                if (parse!=0){
                    printf("Mauvais format d'enchère reçu\n");
                    continue;
                }else{

                    if (out_id==monAdressePtr->id){
                        continue; //Pour ignorer ses propres messages
                    }

                    printf("BON FORMAT D'ENCHERE RECU\n");
                    printf("Enchère numéro : %"PRIu32"  Prix : %"PRIu32"\n",out_numv, out_prix);
                
                    pthread_mutex_lock(&mutex_encheres);
                    Enchere e = {
                        .vendeur_id = out_id,
                        .numero_vente = out_numv,
                        .meneur_actuel = out_id, //Car si personne n'enchérit on dit que c'est le superviseur qui remporte son enchère
                        .prix_actuel = out_prix
                    };
                    liste_encheres[compteur_encheres] = e;
                    compteur_encheres++;
                    pthread_mutex_unlock(&mutex_encheres);

                }
            }
            
            //Gérer un message de surenchère
            else if (code==9){
                uint16_t id;
                uint32_t numv, prix;
                int parse = parse_auction_bid(buffer, len, 9, &id, &numv, &prix);
                if (parse != 0) {
                    printf("Mauvais format de surenchère reçu\n");
                    continue;
                }

                // Vérifier si on est superviseur
                int superviseur = 0;
                int surenchere_ok = 0;
                pthread_mutex_lock(&mutex_encheres);
                for (int i = 0; i < compteur_encheres; ++i) {
                    if (liste_encheres[i].numero_vente == numv &&
                        liste_encheres[i].vendeur_id == monAdressePtr->id) {
                        superviseur = 1;
                        if (liste_encheres[i].prix_actuel<prix){
                            surenchere_ok=1;
                        }
                        break;
                    }
                }
                pthread_mutex_unlock(&mutex_encheres);

                //Faire les vérifications nécéssaire + envoyer code 10 si on est superviseur
                if (superviseur) {
                    printf("Je suis superviseur, vérification de la surenchère %u à %u\n", numv, prix);
                    if (surenchere_ok){
                        printf("Prix supérieur à l'ancien prix, validation de la surenchère.\n");
                        // Envoyer message validation (code 10)
                        uint8_t msg_valide[12];
                        size_t len_valide = build_auction_bid(10, id, numv, prix, msg_valide);

                        if (sendto(sockfd, msg_valide, len_valide, 0, (struct sockaddr*)&dest, sizeof(dest)) < 0) {
                            perror("sendto validation");
                            fflush(stderr);
                        } else {
                            printf("Message de validation envoyé. CODE 10\n");
                            //fonction serveur
                            //consensus_serveur(monAdressePtr->id,numv, prix);
                            //reception des codes 1 
                            //validation des codes 1
                            //envoie de code 2 
                        }
                    }else{
                        printf("Nouveau prix inferieur à l'ancien, pas de surenchère.\n");
                    }
                } else {
                    //Ne rien faire si on est pas superviseur (attendre code 10)
                    //printf("Je ne suis pas superviseur, j'ignore la surenchère.\n");
                }
            
            }

            //Mettre à jour sa liste d'enchère si on reçoit le code 10
            else if (code==10){
                printf("Code 10 reçu\n");
                uint16_t id;
                uint32_t numv, prix;
                //uint16_t sup_id;
                //int superviseur = 0;
                int parse = parse_auction_bid(buffer, len, 10, &id, &numv, &prix);
                if (parse != 0) {
                    printf("Mauvais format de validation reçu\n");
                    continue;
                }
                /*pthread_mutex_lock(&mutex_encheres);
                for (int i = 0; i < compteur_encheres; ++i) {
                    
                    if (liste_encheres[i].numero_vente == numv ) {
                        sup_id=liste_encheres[i].vendeur_id;
                        if(liste_encheres[i].vendeur_id==monAdressePtr->id){
                            superviseur=1;
                        }
                        break;
                    }
                }
                pthread_mutex_unlock(&mutex_encheres);
                */
                //if(!superviseur) consensus_client(sup_id,numv, prix);
                //fonction client consensus
                //envoie code 1 au superviseur si pas superviseur 
                //reception code 2
                //tout le monde fait la suite
                
                pthread_mutex_lock(&mutex_encheres);
                int found = 0;
                for (int i = 0; i < compteur_encheres; i++) {
                    if (liste_encheres[i].numero_vente == numv) {
                        //Mise à jour du prix et du gagnant actuel
                        liste_encheres[i].prix_actuel = prix;
                        liste_encheres[i].meneur_actuel = id;  
                        found = 1;
                        break;
                    }
                }
                pthread_mutex_unlock(&mutex_encheres);
                if (found) {
                    printf("Mise à jour prix validée pour enchère %u : nouveau prix %u\n", numv, prix);
                } else {
                    printf("Validation reçue pour enchère inconnue %u\n", numv);
                }                

            }

            /*Annonce de fin d'enchère*/
            else if(code==11){
                uint16_t id_vendeur;
                uint32_t numv_annonce, prix_annonce;
                int parse= parse_auction_finalization(buffer, len, &id_vendeur, &numv_annonce, &prix_annonce);
                if(parse !=0)
                {
                    printf("Mauvais format d'annonce de fin CODE=11 reçue (parse_auction_finalization retour : %d)\n", parse);
                    continue;
                }
                printf("\n==== Annonce de fin d'enchère !====\n");
                printf("Enchère %"PRIu32" (Vendeur ID %"PRIu16") sur le point de se terminer !\n", numv_annonce, id_vendeur);
                printf("Prix actuel: %"PRIu32". Il reste %d secondes pour surenchérir !\n", prix_annonce, temps); /*temps c'est la variable globale dans enchere.h égale à 5*/
            }

            /*Fin de l'enchère et gestion de cette dernière*/
            else if(code==12){
                uint16_t id_gagnant;
                uint32_t numv_fin;
                uint32_t prix_final;
                int parse = parse_auction_end(buffer, len, &id_gagnant, &numv_fin, &prix_final);
                if(parse!=0)
                {
                    printf("Mauvais format de fin d'enchère CODE=12 reçue (parse_auction_end retour : %d)\n", parse);
                    continue;
                }
                printf("\n======FIN DEFINITIVE D'ENCHERE !======\n");
                printf("Enchère numéro %"PRIu32" est TERMINEE !\n", numv_fin);
                printf("Vendue au prix final de %"PRIu32" à l'ID : %"PRIu16"\n", prix_final, id_gagnant);

                /*Il faut maintenant retirer l'enchère de la liste*/
                pthread_mutex_lock(&mutex_encheres);
                int found_and_removed=0;
                for(int i=0; i<compteur_encheres; i++)
                {
                    if(liste_encheres[i].numero_vente==numv_fin)
                    {
                        /*Décalage des éléments de la liste pour supprimer l'enchère*/
                        for(int j=i; j<compteur_encheres-1; j++)
                        {
                            liste_encheres[j]=liste_encheres[j+1];
                        }
                        compteur_encheres--;

                        //Nettoyage de l'ancienne dernière case
                        memset(&liste_encheres[compteur_encheres], 0, sizeof(Enchere));

                        found_and_removed =1;
                        printf("Enchère %"PRIu32" retirée de la liste. (Il reste %u enchère(s))\n", numv_fin, compteur_encheres);
                        break;
                    }
                }
                if(!found_and_removed)
                {
                    printf("Enchère %"PRIu32" non trouvée dans la liste pour la suppression (CODE=12)\n", numv_fin);
                }
                pthread_mutex_unlock(&mutex_encheres);
            }

            //Gérer la disparition d'un pair
            else if (code==13){
                uint16_t id_leaving;
                int parse = parse_leave_system(buffer,len,&id_leaving);
                if (parse!=0){
                    printf("Mauvais format de CODE=13 reçu\n");
                    continue;
                }else{
                    if (id_leaving==monAdressePtr->id){
                        continue; //Pour ignorer ses propres messages
                    }
                    //On retire ce pair de peer_list
                    pthread_mutex_lock(&mutex_encheres);
                    int found_and_removed=0;
                    for(int i=0; i<peer_count; i++)
                    {
                        if(peer_list[i].id==id_leaving)
                        {
                            //On décale les autres pairs
                            for(int j=i; j<peer_count-1; j++)
                            {
                                peer_list[j]=peer_list[j+1];
                            }
                            peer_count--;

                            //Nettoyage de l'ancienne dernière case
                            memset(&peer_list[peer_count], 0, sizeof(AdressePerso));

                            found_and_removed =1;
                            printf("Pair %"PRIu16" retirée de la liste.\n", id_leaving);
                            printf("Liste des pairs restant\n");
                            afficher_liste(peer_list,MAX_PEERS);
                            break;
                        }
                    }
                    if(!found_and_removed)
                    {
                        printf("Pair %"PRIu16" non trouvée dans la liste pour la suppression (CODE=13)\n", id_leaving);
                    }
                    pthread_mutex_unlock(&mutex_encheres);
                }
            }
            else
            {
                printf("Code reçu non reconnu\n");
            }
        }
    }

    close(sockfd);
    pthread_exit(NULL);
}


//Thread qui gère la finalisation d'une enchère
void* finalisation_enchere(void* arg){
    ParamFinalisationThread* param = (ParamFinalisationThread*)arg;

    uint32_t numv = param->numero_vente;
    uint32_t prix = param->prix_initial;
    uint16_t meneur = param->meneur_actuel;

    int flagReset=0;

    int sockfd = socket(AF_INET6, SOCK_DGRAM, 0);
    if (sockfd < 0) {
        perror("socket");
        fflush(stderr);
        free(arg);
        pthread_exit(NULL);
    }

    //Pour envoyer les messages 11 et 12
    struct sockaddr_in6 dest;
    memset(&dest, 0, sizeof(dest));
    dest.sin6_family = AF_INET6;
    dest.sin6_port = htons(8080);
    if (inet_pton(AF_INET6, ADDR_ENCHERES, &dest.sin6_addr) != 1) {
        perror("inet_pton");
        close(sockfd);
        free(arg);
        pthread_exit(NULL);
    }


    while(1){
        flagReset=0;

        //Attendre 3*temps (au pire)
        for (int i=0;i<3;i++){

            //Permet de recommencer le compte à rebours de 0
            if (flagReset){
                break;
            }

            sleep(temps);

            pthread_mutex_lock(&mutex_encheres);
            for (int j = 0; j < compteur_encheres; j++) {
                if (liste_encheres[j].numero_vente == numv) {
                    if(liste_encheres[j].prix_actuel != prix){
                        prix=liste_encheres[j].prix_actuel;
                        meneur=liste_encheres[j].meneur_actuel;
                        flagReset = 1; //Nouveau prix
                    }
                }
            }
            pthread_mutex_unlock(&mutex_encheres);

        }
        if(!flagReset){
            printf("Prix inchangé pendant 3*%d secondes...\n",temps);
            uint8_t buf[12];

            //Construire et envoyer le code 11
            ssize_t len = build_auction_finalization(monAdressePtr->id,numv,prix,buf);
            if (sendto(sockfd, buf, len, 0, (struct sockaddr*)&dest, sizeof(dest)) < 0) {
                perror("sendto code 11");
                fflush(stderr);
            } else {
                printf("Il vous reste %d secondes pour surenchérir sur la vente %"PRIu32" actuellement au prix de %d !\n",temps,numv,prix);
                
                sleep(temps);
                
                pthread_mutex_lock(&mutex_encheres);
                for (int j = 0; j < compteur_encheres; j++) {
                    if (liste_encheres[j].numero_vente == numv) {
                        if(liste_encheres[j].prix_actuel != prix){
                            prix=liste_encheres[j].prix_actuel;
                            meneur=liste_encheres[j].meneur_actuel;
                            flagReset = 1; //Nouveau prix
                        }
                    }
                }
                pthread_mutex_unlock(&mutex_encheres);

                if (!flagReset){

                    //Construire et envoyer le code 12
                    uint8_t buf_code12[12];
                    size_t len = build_auction_end(meneur,numv,prix,buf_code12);

                    if (sendto(sockfd, buf_code12, len, 0, (struct sockaddr*)&dest, sizeof(dest)) < 0) {
                        perror("sendto code 12");
                        fflush(stderr);
                    } else {
                        printf("Enchère numéro %"PRIu32" terminée et vendue au prix de %d à l'ID : %"PRIu16" !\n",numv,prix,meneur); 
                        close(sockfd);
                        free(arg);
                        pthread_exit(NULL);
                    }
                }
            }
        }
    }
    close(sockfd);
    free(arg);
    pthread_exit(NULL);
}





void* thread_enchere(void* arg) {

    monAdressePtr = (AdressePerso *)arg;

    //Lancement du thread pour recevoir les enchères
    pthread_t reception_thread;
    if (pthread_create(&reception_thread, NULL, reception_encheres, NULL) != 0) {
        perror("pthread_create_reception");
        fflush(stderr);
        pthread_exit(NULL);
    }

    //Pour créer des enchères
    char buffer_affichage[100];
    uint8_t msg[12];
    char commande[10]; //Pourrait être 4 (new\0) mais on prend de la marge si on doit changer
    int prix;

    //Pour enchérir
    uint32_t numv;

    //Socket UDP pour envoi multicast
    int sockfd = socket(AF_INET6, SOCK_DGRAM, 0);
    if (sockfd < 0) {
        perror("socket");
        fflush(stderr);
        pthread_exit(NULL);
    }

    struct sockaddr_in6 dest;
    memset(&dest, 0, sizeof(dest));
    dest.sin6_family = AF_INET6;
    dest.sin6_port = htons(8080);
    inet_pton(AF_INET6, ADDR_ENCHERES, &dest.sin6_addr);

    while (1) {
        printf("Commandes :\n");
        printf(" - new <prix>          : créer une nouvelle enchère\n");
        printf(" - bid <numv> <prix>   : surenchérir sur une enchère existante\n");
        printf(" - list                : afficher les enchères reçues\n");
        printf(" - quit                : quitter le système d'enchères\n");

        if (fgets(buffer_affichage, sizeof(buffer_affichage), stdin) == NULL) {
            printf("Erreur de lecture.\n");
            continue;
        }

        buffer_affichage[strcspn(buffer_affichage, "\n")] = '\0';

        //Commande : new <prix>
        if (sscanf(buffer_affichage, "%9s %d", commande, &prix) == 2 && strcmp(commande, "new") == 0 && prix >= 0) {
            uint16_t compteur = compteur_ventes + 1;
            uint32_t numv_genere = ((uint32_t)monAdressePtr->id << 16) | compteur;
            size_t msg_len = build_auction_start(monAdressePtr->id, numv_genere, (uint32_t)prix, msg);

            if (sendto(sockfd, msg, msg_len, 0, (struct sockaddr*)&dest, sizeof(dest)) < 0) {
                perror("sendto code 8");
                fflush(stderr);
            } else {
                printf("Enchère envoyée !\n");
                compteur_ventes++;
                pthread_mutex_lock(&mutex_encheres);
                Enchere e = {
                    .vendeur_id = monAdressePtr->id,
                    .meneur_actuel = monAdressePtr->id, //Car si personne n'enchérit on dit que c'est le superviseur qui remporte son enchère
                    .numero_vente = numv_genere,
                    .prix_actuel = (uint32_t)prix
                };
                liste_encheres[compteur_encheres] = e;
                compteur_encheres++;
                pthread_mutex_unlock(&mutex_encheres);


                ParamFinalisationThread* param = malloc(sizeof(ParamFinalisationThread));
                if (!param) {
                    perror("malloc");
                    exit(EXIT_FAILURE);
                }
                param->numero_vente = numv_genere;
                param->prix_initial = prix;
                param->meneur_actuel = monAdressePtr->id;

                //Lancement d'un thread pour surveiller la finalisation de cette enchère
                pthread_t finalisation_thread;
                if (pthread_create(&finalisation_thread, NULL, finalisation_enchere, param) != 0) {
                    perror("pthread_create_finalisation");
                    pthread_exit(NULL);
                }

                pthread_detach(finalisation_thread);

            }

        //Commande : bid <numv> <prix>
        } else if (sscanf(buffer_affichage, "%9s %u %d", commande, &numv, &prix) == 3 && strcmp(commande, "bid") == 0 && prix >= 0) {
            int found = 0;

            //Pour savoir si le numéro de vente existe
            pthread_mutex_lock(&mutex_encheres);
            for (int i = 0; i < compteur_encheres; ++i) {
                if (liste_encheres[i].numero_vente == numv) {
                    found = 1;
                    break;
                }
            }
            pthread_mutex_unlock(&mutex_encheres);

            if (!found) {
                printf("Numéro d'enchère inconnu.\n");
                continue;
            }

            size_t msg_len = build_auction_bid(9,monAdressePtr->id, numv, (uint32_t)prix, msg);
            if (sendto(sockfd, msg, msg_len, 0, (struct sockaddr*)&dest, sizeof(dest)) < 0) {
                perror("sendto code 9");
                fflush(stderr);
            } else {
                printf("Surenchère envoyée sur %"PRIu32" à %d\n", numv, prix);
            }

        //Commande : list
        } else if (strcmp(buffer_affichage, "list") == 0) {
            pthread_mutex_lock(&mutex_encheres);
            printf("Liste des enchères reçues :\n");
            for (int i = 0; i < compteur_encheres; ++i) {
                printf(" - Numéro : %"PRIu32", Vendeur : %"PRIu16", Prix : %"PRIu32"\n",
                       liste_encheres[i].numero_vente,
                       liste_encheres[i].vendeur_id,
                       liste_encheres[i].prix_actuel);
            }
            pthread_mutex_unlock(&mutex_encheres);

        //Commande : quit
        } else if (strcmp(buffer_affichage,"quit")==0){
            size_t msg_len = build_leave_system(monAdressePtr->id,msg);
            if (sendto(sockfd, msg, msg_len, 0, (struct sockaddr*)&dest, sizeof(dest)) < 0) {
                perror("sendto code 13");
                fflush(stderr);
            } else {
                printf("Message pour quitter le système envoyé\n");

                close(sockfd);

                //Annuler le thread de réception des messages et le fermer avec join
                pthread_cancel(reception_thread);
                pthread_join(reception_thread, NULL);

                // Optionnel : nettoyer la liste locale
                // pthread_mutex_lock(&mutex_encheres);
                // compteur_encheres = 0;
                // memset(liste_encheres, 0, sizeof(liste_encheres));
                // pthread_mutex_unlock(&mutex_encheres);

                printf("Vous avez quitté le système d'enchères.\n");

                //Pour dire à connexions.c qu'on a terminé
                pthread_mutex_lock(&mutex_fin_enchere);
                enchere_terminee = 1;
                pthread_cond_signal(&cond_fin_enchere);
                pthread_mutex_unlock(&mutex_fin_enchere);


                pthread_exit(NULL);  // Fin propre du thread
            }
        }
        //Commande invalide 
        else {
            printf("Commande invalide.\n");
        }
    }

    close(sockfd);
    pthread_join(reception_thread,NULL);
    pthread_exit(NULL);
}