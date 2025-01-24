package Entites;

import De.De;
import De.Capacite.Griffures;
import De.Capacite.Capacite_Ennemi.Blindness;
import De.Capacite.Capacite_Ennemi.Confusion;
import De.Capacite.Capacite_Ennemi.Destruction;
import De.Capacite.Capacite_Ennemi.Fear;
import De.Capacite.Capacite_Ennemi.OursGarou_AlmightyPaw;
import De.Capacite.Capacite_Ennemi.Turkey_Wing;

import java.awt.Color;
import java.awt.color.*;
import java.util.Random;

public class SousBoss extends Ennemis{

    String [] nomPossibles = {"Zombie Sorcier","Cauchemar","Ours-Garou","Spectre"};

    public SousBoss (String nom){
        super(20,importeDeSousBoss(nom),new Color(127,212,147),nom);
    }
    

    public void attribuerType(SousBoss sb){
        Random rd = new Random();
        int randomNumber = rd.nextInt()%nomPossibles.length;
        randomNumber = Math.abs(randomNumber);
        sb.setNomEntite(nomPossibles[randomNumber]);
        sb.setNomImage(nomPossibles[randomNumber].replace(' ', '_'));
        sb.setDe(importeDeSousBoss(nomPossibles[randomNumber]));
    }

    protected static De importeDeSousBoss(String nom){ //sert à créer le dé du Sous Boss : reste à voir les attaques possibles.
        if (nom!=null){
            switch (nom){
                case "Zombie Sorcier" : De deZS = new De(new Destruction(3),new Destruction(4),new Destruction(3),new Blindness(3), new Blindness(3), null);
                return deZS;
                case "Cauchemar" : De deCauchemar = new De(new Blindness(3),new Blindness(4),new Blindness(3),new Fear(4), new Fear(3), null);
                return deCauchemar;
                case "Ours-Garou" : De deOG = new De(new OursGarou_AlmightyPaw(4),new Griffures(3),new Griffures(4),new OursGarou_AlmightyPaw(3), new OursGarou_AlmightyPaw(4), null);
                return deOG;
                case "Spectre" : De deSpectre = new De(new Turkey_Wing(3),new Turkey_Wing(4),new Turkey_Wing(4),new Confusion(3), new Confusion(4),null); 
                return deSpectre;
                default: De deSbire = new De(new Griffures(1),new Griffures(1),new Griffures(1),new Griffures(1), new Griffures(1), new Griffures(1)); 
                return deSbire;
            }
        }else{
            De deSbire = new De(new Griffures(2),new Griffures(1),new Griffures(1),new Griffures(1), new Griffures(1), new Griffures(1)); return deSbire;
        }
    }

    public int monnaieLachee(){
        switch (this.getNomEntite()){
            case "Zombie Sorcier" : return 100;
            case "Cauchemar" : return 150;
            case "Ours-Garou" : return 75;
            case "Spectre" : return 50;
            default: return 70;
        }
    }

    public SousBoss genererSousBoss(){
        SousBoss sb = new SousBoss(null);
        attribuerType(sb);
        return sb;
    }

}
