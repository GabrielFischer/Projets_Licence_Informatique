package Entites;

import De.*;
import De.Capacite.*;
import De.Capacite.Capacite_Ennemi.*;
import De.Capacite.*;

import java.awt.Color;
import java.awt.color.*;
import java.util.Random;


public class Sbire extends Ennemis{

    //TypeSbire enumSbire;
    String [] nomPossibles = {"Amalgame Noire","Moustique","Oie","Taureau","Crapeau","Squelette","Loup-Garou","Sbire"};
    public Sbire(String nom){
        super(7, importeDeSbire(nom), new Color(127,212,147), nom);
    }


    public void attribuerType(Sbire s){
        Random rd = new Random();
        int randomNumber = rd.nextInt()%nomPossibles.length;
        randomNumber = Math.abs(randomNumber);
        s.setNomEntite(nomPossibles[randomNumber]);
        s.setNomImage(nomPossibles[randomNumber].replace(' ', '_'));
        s.setDe(importeDeSbire(nomPossibles[randomNumber]));
    }

    protected static De importeDeSbire(String nom){ //sert à créer le dé du Sbire : reste à voir les attaques possibles.
        if (nom!=null){
            switch (nom){
                case "Amalgame Noire" : De deAmalgame = new De(new Fear(2),new Griffures(2),new Griffures(2),new Blindness(2), null, null);
                return deAmalgame;
                case "Moustique" : De deMoustique = new De(new Confusion(1),new Blindness(2),null,new Confusion(2), new Blindness(2), null);
                return deMoustique;
                case "Oie" : De deOie = new De(new Goose_Pinch(2),new Goose_Pinch(1),new Griffures(2),new Griffures(1), new Confusion(2), null);
                return deOie;
                case "Taureau" : De deTaureau = new De(new Destruction(3),new Blindness(1),new Blindness(1),new Fear(2), new Fear(2),null); 
                return deTaureau;
                case "Crapeau" : De deCrapeau = new De(new Fear(2),new Blindness(2),new Blindness(1),new Destruction(2), null, null); 
                return deCrapeau;
                case "Squelette" : De deSquelette = new De(new Confusion(2),new Destruction(2),new Fear(1),new Griffures(2), new Fear(2), null); 
                return deSquelette;
                case "Loup-Garou" : De deLoupGarou = new De(new Griffures(2),new Griffures(2),new Fear(2),new Fear(1), new Griffures(1), null); 
                return deLoupGarou;
                default: De deSbire = new De(new Griffures(2),new Griffures(2),null,new Griffures(2), new Griffures(2), null); 
                return deSbire;
            }
        }else{
            De deSbire = new De(new Griffures(2),new Griffures(1),new Griffures(1),new Griffures(1), new Griffures(1), new Griffures(1)); return deSbire;
        }
    }

    public int monnaieLachee(){
        switch (this.getNomEntite()){
            case "Amalgame Noire" : return 100;
            case "Moustique" : return 150;
            case "Oie" : return 75;
            case "Taureau" : return 50;
            case "Crapeau" : return 50;
            case "Squelette" : return 50;
            case "Loup-Garou" : return 50;
            default: return 70;
        }
    }

    public Sbire genererSbire(){
        Sbire s =new Sbire (null);
        attribuerType(s);
        return s;
    }
    

}
