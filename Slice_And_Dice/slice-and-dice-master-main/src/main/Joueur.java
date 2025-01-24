package main;



import De.Capacite.*;
import Entites.*;
import Graphics.scenes.Achievement;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

public class Joueur implements Serializable{
    private ArrayList<Heros> listeHerosDisponibles = new ArrayList<>();
    /** La liste ci-dessus contient tous les héros dont le joueur dispose avant de lancer une partie.
    * C'est donc à partir de cette liste que le joueur choisit les héros qui composeront son équipe avant le début d'une partie*/

    private int monnaiePartie;/*monnaie qui sera incrémentée à chaque ennemi vaincu, et permettra au joueur d'acheter des
    équipements ainsi que des bonus dans la boutique durant la partie*/
    private int gold;

    private int mana;/*contient le nombre de mana*/

    private double xp;
    private int attaque;

    public Achievement achievement;

    public Main main;
    private ArrayList<CapaciteSpe> listeSortsDisponibles = new ArrayList<>();//liste qui contient les sorts disponibles durant la partie

    private ArrayList<Capacite> capacitesPossedees= new ArrayList<>();//liste qui contient toutes les capacités que le joueur peut donner à son héros custom
    public Joueur(Main main)
     {
        this.main = main;
        this.achievement = new Achievement(this);
         //il faut ajouter les héros disponibles dès le lancement du jeu
        /*this.listeHerosDisponibles.add(new Heros1());
        * this.listeHerosDisponibles.add(new Heros2());
        * etc. etc. On partira du principe que 5 héros sont disponibles donc on fera des listeHerosDisponibles.add()
        * avec en paramètre new HerosX() jusqu'à 5
        * les héros qui seront débloqués seront ajoutés à listeHerosDisponibles de cette instance de Joueur lorsqu'ils
        *  seront débloqués*/
        this.listeHerosDisponibles.add(new Archer());
        this.listeHerosDisponibles.add(new Tank());
        this.listeHerosDisponibles.add((new Epeiste()));
        this.listeHerosDisponibles.add(new Mage());
        this.listeHerosDisponibles.add(new Guerisseur());
        this.monnaiePartie=0;
        this.gold=0;
        this.mana=0;
        this.xp=0;
         this.listeSortsDisponibles.add(new Burst());//burst sera toujours en tête de liste car toujours disponible
         this.listeSortsDisponibles.add(new Flare());//Flare et Renew sont ajoutées dans le constructeur car chaque partie démarre forcément avec le mage (pour Flare) et le guérisseur(pour Renew)
         this.listeSortsDisponibles.add(new Renew());
         this.listeSortsDisponibles.add(new Balance());
         this.listeSortsDisponibles.add(new Drop());
         this.listeSortsDisponibles.add(new Scald());
         this.listeSortsDisponibles.add(new Infuse());
         this.listeSortsDisponibles.add(new Liquor());
         this.listeSortsDisponibles.add(new Revive());
         this.listeSortsDisponibles.add(new Bind());
         this.listeSortsDisponibles.add(new Blaze());
         //on ajoute ici les capacités qui seront disponibles dès le départ pour le joueur afin qu'il puisse les attribuer à son propore héros custom
         //les autres capacités seront ajoutés à cette liste dès leur achat
         this.capacitesPossedees.add(new Bouclier(0));
         this.capacitesPossedees.add(new Soin(0));
         this.capacitesPossedees.add(new Mana(0));
         this.capacitesPossedees.add(new Arc(0));
         this.capacitesPossedees.add(new CoupEpee(0));
    }

    // Méthode pour sauvegarder le joueur dans un fichier

    /*++++++++++++++++++++++++++++++++++++++++++++ G E S T I O N   D E S   S O R T S ++++++++++++++++++++++++++++++++++++++++++*/
    public void suppSortSiHerosMort(Heros herosQuiVientDeMourir)//cette fonction doit être appelée dans la fonction verifierMortHeros(), car cette fonction va vérifier quel héros vient de mourir et supprimer un sort si nécessaire
    {
        if(herosQuiVientDeMourir instanceof Guerisseur)
        {
            verifQuelNivGuerisseurPourSupprimerLeBonSort(herosQuiVientDeMourir);
        }
        else if(herosQuiVientDeMourir instanceof Mage)
        {
            verifQuelNivMagePourSupprimerLeBonSort(herosQuiVientDeMourir);
        }
    }
    /*Les fonctions qui suivent servent juste à supprimer les sorts de la listeSortsDisponibles*/
    private void verifQuelNivMagePourSupprimerLeBonSort(Heros mage)
    {
        switch (mage.getNomEntite()) {
            case "Mage" : suppSortMage();
            case "Sorcier" : suppSortSorcier();
            case "Arcaniste" : suppSortArcaniste();
            case "Shaman" : suppSortShaman();
            case "Demoniste" : suppSortDemoniste();
        }
    }
    private void verifQuelNivGuerisseurPourSupprimerLeBonSort(Heros guerisseur)
    {
        switch (guerisseur.getNomEntite()) {
            case "Guerisseur" : suppSortGuerisseur();
            case "Pretre" : suppSortPretre();
            case "Apothicaire" : suppSortApothicaire();
            case "Starseer" : suppSortStarseer();
            case "Miracleur" : suppSortMiracleur();
        }
    }
    private void suppSortGuerisseur()
    {
        for(CapaciteSpe c : this.listeSortsDisponibles)
        {
            if(c.getNom().equals("Renew"))
            {
                this.listeSortsDisponibles.remove(c);
            }
        }
    }
    private void suppSortApothicaire()
    {
        for(CapaciteSpe c : this.listeSortsDisponibles)
        {
            if(c.getNom().equals("Infuse"))
            {
                this.listeSortsDisponibles.remove(c);
            }
        }
    }
    private void suppSortPretre()
    {
        for(CapaciteSpe c : this.listeSortsDisponibles)
        {
            if(c.getNom().equals("Balance"))
            {
                this.listeSortsDisponibles.remove(c);
            }
        }
    }
    private void suppSortStarseer()
    {
        for(CapaciteSpe c : this.listeSortsDisponibles)
        {
            if(c.getNom().equals("Revive"))
            {
                this.listeSortsDisponibles.remove(c);
            }
        }
    }
    private void suppSortMiracleur()
    {
        for(CapaciteSpe c : this.listeSortsDisponibles)
        {
            if(c.getNom().equals("Liquor"))
            {
                this.listeSortsDisponibles.remove(c);
            }
        }
    }
    private void suppSortMage()
    {
        for(CapaciteSpe c : this.listeSortsDisponibles)
        {
            if(c.getNom().equals("Flare"))
            {
                this.listeSortsDisponibles.remove(c);
            }
        }
    }
    private void suppSortSorcier()
    {
        for(CapaciteSpe c : this.listeSortsDisponibles)
        {
            if(c.getNom().equals("Scald"))
            {
                this.listeSortsDisponibles.remove(c);
            }
        }
    }
    private void suppSortArcaniste()
    {
        for(CapaciteSpe c : this.listeSortsDisponibles)
        {
            if(c.getNom().equals("Drop"))
            {
                this.listeSortsDisponibles.remove(c);
            }
        }
    }
    private void suppSortShaman()
    {
        for(CapaciteSpe c : this.listeSortsDisponibles)
        {
            if(c.getNom().equals("Blaze"))
            {
                this.listeSortsDisponibles.remove(c);
            }
        }
    }
    private void suppSortDemoniste()
    {
        for(CapaciteSpe c : this.listeSortsDisponibles)
        {
            if(c.getNom().equals("Bind"))
            {
                this.listeSortsDisponibles.remove(c);
            }
        }
    }
    /*++++++++++++++++++++++++++++++++ F I N  D E  L A  G E S T I O N  D E S  S O R T S +++++++++++++++++++++++++++++++++++++++++++++++*/

    public double getXp() {
        return xp;
    }

    public int getMana() {
        return mana;
    }

    public int getAttaque() {
        return attaque;
    }

    public int getMonnaiePartie() {
        return monnaiePartie;
    }

    public void setXp(double xp) {
        this.xp = xp;
    }

    public void setAttaque(int attaque) {
        this.attaque = attaque;
    }

    public void setMana(int mana) {
        this.mana = mana;
    }

    public void setMonnaiePartie(int monnaiePartie) {
        this.monnaiePartie = monnaiePartie;
    }

    public ArrayList<Heros> getListeHerosDisponibles() {
        return listeHerosDisponibles;
    }
    public ArrayList<CapaciteSpe> getListeSortsDisponibles()
    {
        return this.listeSortsDisponibles;
    }
    public ArrayList<Capacite> getCapacitesPossedees(){
        return capacitesPossedees;
    }

    public int getGold() {
        return gold;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }
    
}
