package Entites;

import De.De;
//import Entites.SousBoss.TypeSousBoss;
import De.Capacite.Griffures;
import De.Capacite.Capacite_Ennemi.Blindness;
import De.Capacite.Capacite_Ennemi.Confusion;
import De.Capacite.Capacite_Ennemi.Destruction;
import De.Capacite.Capacite_Ennemi.Fear;
import De.Capacite.Capacite_Ennemi.Turkey_Wing;

import java.awt.Color;
import java.awt.color.*;
import java.math.*;
import java.util.Random;
import java.util.Random.*;

public class Boss extends Ennemis{


    String [] nomPossibles = {"Golem de Foudre","Golem de l'Eau","Atronach de Feu","Spriggan"};
    public Boss(String nom){
        super(25, importeDeBoss(nom), new Color(127,212,147), nom);
    }

    public enum TypeBoss{
        GOLEMDEFOUDRE,
        GOLEMDELEAU,
        ATRONACHDEFEU,
        SPRIGGAN
    }

    protected static De importeDeBoss(String nom){ //sert à créer le dé du Boss : reste à voir les attaques possibles.
        if (nom!=null){
            switch (nom){
                case "Golem de Foudre" : De deGF = new De(new Destruction(5),new Destruction(4),new Destruction(5),new Confusion(4), new Confusion(4), null);
                return deGF;
                case "Golem de l'Eau" : De deGE = new De(new Destruction(5),new Destruction(4),new Destruction(5),new Confusion(4), new Confusion(4), null);
                return deGE;
                case "Atronach de Feu" : De deAF = new De(new Destruction(5),new Destruction(4),new Destruction(5),new Confusion(4), new Confusion(4), null);
                return deAF;
                case "Spriggan" : De deSpriggan = new De(new Destruction(5),new Destruction(4),new Destruction(5),new Confusion(4), new Confusion(4), null); 
                return deSpriggan;
                default: De deSbire = new De(new Griffures(1),new Griffures(1),new Griffures(1),new Griffures(1), new Griffures(1), new Griffures(1)); 
                return deSbire;
            }
        }else{
            De deSbire = new De(new Griffures(2),new Griffures(1),new Griffures(1),new Griffures(1), new Griffures(1), new Griffures(1)); return deSbire;
        }
    }

    public void attribuerType(Boss b){
        Random rd = new Random();
        int randomNumber = rd.nextInt()%nomPossibles.length;
        randomNumber = Math.abs(randomNumber);
        b.setNomEntite(nomPossibles[randomNumber]);
        b.setNomImage(nomPossibles[randomNumber].replace(' ', '_'));
        b.setDe(importeDeBoss(nomPossibles[randomNumber]));
    }
    public int monnaieLachee(){
        switch (this.getNomEntite()){
            case "Golem de Foudre" : return 175;
            case "Golem de l'Eau" : return 200;
            case "Atronach de Feu" : return 160;
            case "Spriggan" : return 190;
            default: return 150;
        }
    }

    public Boss genererBoss(){
        Boss b = new Boss(null);
        attribuerType(b);
        return b;
    }
}

    

