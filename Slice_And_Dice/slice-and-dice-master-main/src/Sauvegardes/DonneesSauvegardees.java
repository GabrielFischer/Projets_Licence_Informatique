package Sauvegardes;

import java.io.Serializable;
import java.util.ArrayList;

import Equipement.Equipement;

public class DonneesSauvegardees implements Serializable{
    //Classe qui permet le stockage des données d'une partie
    // INFOS PARTIE
    int niveau;
    int nbDesLancesRestants;
    int mana;
    int argent;

    //HEROS
    String [] HerosName = new String[5];
    int [] niveauHeros = new int[5];
    String [] ameliorationChoisie = new String[5];
    int [] pvHeros = new int[5];
    int [][] equipementHero = new int[5][2];
    int [] faceDuHaut = new int[5];
    Boolean [] faceSelectionee = new Boolean[5];
    Boolean [] used = new Boolean[5];
    int [] pvMenaces = new int[5];

    //ENNEMIS
    ArrayList<String> ennemi = new ArrayList<String>();
    ArrayList<String> ennemiType = new ArrayList<String>(); //enregistrer le nom de l'ennemi
    ArrayList<Integer> pvennemis = new ArrayList<Integer>();
    ArrayList<Integer> faceDuHautEnnemis = new ArrayList<Integer>();
    ArrayList<Integer> heroCible = new ArrayList<Integer>();



    //INVENTAIRE ET BOUTIQUE
    ArrayList<String> inventaire = new ArrayList<String>();
    String [] boutique = new String[3];

    
    
}
