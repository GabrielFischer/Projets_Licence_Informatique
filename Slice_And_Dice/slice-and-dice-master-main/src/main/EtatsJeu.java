package main;

/* BUT
 * Fichier enum déclarant tous les états possibles du jeu
 */

public enum EtatsJeu{

    PLAYING,
    MENU,
    CUSTOM,
    CINEMATIQUE,
    BOUTIQUE,
    GUIDEMENU,
    GUIDEPLAYING,
    BOUTIQUEMENU,
    ACHIEVEMENT,
    INVENTAIRE,
    SETTINGSMENU,
    SETTINGSPLAYING,
    SAUVEGARDE;

    public static EtatsJeu etatJeu = MENU;
    public static void setEtatJeu(EtatsJeu etat){
        etatJeu = etat;
    }
}
