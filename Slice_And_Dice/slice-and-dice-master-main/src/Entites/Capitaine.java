package Entites;

import De.De;
import De.Capacite.Griffures;
import De.Capacite.Capacite_Ennemi.Blindness;
import De.Capacite.Capacite_Ennemi.Confusion;
import De.Capacite.Capacite_Ennemi.Destruction;
import De.Capacite.Capacite_Ennemi.Fear;
import De.Capacite.Capacite_Ennemi.Goose_Pinch;
import Entites.*;

import java.awt.Color;
import java.awt.color.*;
import java.util.Random;

public class Capitaine extends Ennemis{

    //TypeCap enumCap;

    String[] nomPossibles = {"Echoué","Apprenti Mage Noir","Lion","Capitaine Squelette"};

    public Capitaine (String nom){
        super(15,importeDeCap(nom),new Color(127,212,147),nom);
    }
    

    public void attribuerType(Capitaine c){
        Random rd = new Random();
        int randomNumber = rd.nextInt()%nomPossibles.length;
        randomNumber = Math.abs(randomNumber);
        c.setNomEntite(nomPossibles[randomNumber]);
        c.setNomImage(nomPossibles[randomNumber].replace(' ', '_'));
        c.setDe(importeDeCap(nomPossibles[randomNumber]));
    }

    protected static De importeDeCap(String nom){ //sert à créer le dé du Capitaine : reste à voir les attaques possibles.
        if (nom!=null){
            switch (nom){
                case "Echoué" : De deEchoue = new De(new Fear(3),new Confusion(2),new Confusion(3),new Confusion(3), new Fear(2), null);
                return deEchoue;
                case "Apprenti Mage Noir" : De deAMN = new De(new Destruction(3),new Blindness(2),new Blindness(3),new Destruction(2), new Blindness(2), null);
                return deAMN;
                case "Lion" : De deLion = new De(new Griffures(3),new Griffures(3),new Griffures(2),new Fear(3), new Fear(2), null);
                return deLion;
                case "Capitaine Squelette" : De deCapSquelette = new De(new Confusion(3),new Confusion(3),new Destruction(2),new Destruction(2), new Confusion(3),null); 
                return deCapSquelette;
                default: De deSbire = new De(new Griffures(1),new Griffures(1),new Griffures(1),new Griffures(1), new Griffures(1), new Griffures(1)); 
                return deSbire;
            }
        }else{
            De deSbire = new De(new Griffures(2),new Griffures(1),new Griffures(1),new Griffures(1), new Griffures(1), new Griffures(1)); return deSbire;
        }
    }

    public int monnaieLachee(){
        switch (this.getNomEntite()){
            case "Echoué" : return 80;
            case "Apprenti Mage Noir" : return 90;
            case "Lion" : return 50;
            case "Capitaine Squelette" : return 40;
            default: return 50;
        }
    }

    public Capitaine genererCap(){
        Capitaine c = new Capitaine(null);
        attribuerType(c);
        return c;
    }

}
