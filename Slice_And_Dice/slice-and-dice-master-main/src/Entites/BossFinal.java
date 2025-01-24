package Entites;

import De.De;
import De.Capacite.Griffures;
import De.Capacite.Capacite_Ennemi.*;

import java.awt.Color;
import java.awt.color.*;
import java.util.Random;

public class BossFinal extends Ennemis{

    TypeBossFinal enumBossFinal;
    Phase1 enumPhase1;
    Phase2 enumPhase2;

    boolean phase1Done=false;
    boolean phase2Done=false;

    String [] nomPossibles1 = {"Boss Chaton","Boss Dindon","Boss Lapin","Boss Oie"};
    String [] nomPossibles2 = {"CTHULHU","PLAGUE DOCTOR"};
    String [] nomPossibles3 = {"Grand Mage Noir","LICH"};
    public BossFinal (String nom){
        super(40,importeDeBossFinal(nom),new Color(127,212,147),nom);
    }

    public enum TypeBossFinal{
        LICH,
        GRANDMAGENOIR
    }

    public enum Phase1{
        LAPIN,
        OIE,
        CHATON,
        DINDON
    }

    public enum Phase2{
        CTHULHU,
        PLAGUEDOCTOR
    }

    public void attribuerType(BossFinal bf){
        Random rd = new Random();
        int randomNumber = rd.nextInt()%nomPossibles3.length;
        randomNumber = Math.abs(randomNumber);
        bf.setNomEntite(nomPossibles3[randomNumber]);
        bf.setNomImage(nomPossibles3[randomNumber].replace(' ', '_'));
        bf.setDe(importeDeBossFinal(nomPossibles3[randomNumber]));
    }

    public void attribuerPhasesBoss(){  //configurer les trois phases d'un boss, l'intermédiaire étant lich ou grand mage noir
        Random rd = new Random();
        double randomNumber1 = rd.nextDouble();
        switch(enumBossFinal) {
            case LICH : 
                if (randomNumber1 <= 0.7){
                    enumPhase1 = Phase1.LAPIN;
                }
                if (randomNumber1 > 0.7 && randomNumber1 <= 1){
                    enumPhase1 = Phase1.OIE;
                }
                enumPhase2 = Phase2.CTHULHU;
                case GRANDMAGENOIR : 
                if (randomNumber1 <= 0.7){
                    enumPhase1 = Phase1.CHATON;
                }
                if (randomNumber1 > 0.7 && randomNumber1 <= 1){
                    enumPhase1 = Phase1.DINDON;
                }
                enumPhase2 = Phase2.PLAGUEDOCTOR;
        }
    }

    protected static De importeDeBossFinal(String nom){ //sert à créer le dé du Boss Final : reste à voir les attaques possibles.
        if (nom!=null){
            switch (nom){
                case "Grand Mage Noir" : De deGMN = new De(new Destruction(5),new Destruction(4),new Destruction(5),new Confusion(4), new Confusion(4), null);
                return deGMN;
                case "LICH" : De deLICH = new De(new Destruction(5),new Destruction(4),new Destruction(5),new Confusion(4), new Confusion(4), null);
                return deLICH;
                default: De deSbire = new De(new Griffures(1),new Griffures(1),new Griffures(1),new Griffures(1), new Griffures(1), new Griffures(1)); 
                return deSbire;
            }
        }else{
            De deSbire = new De(new Griffures(2),new Griffures(1),new Griffures(1),new Griffures(1), new Griffures(1), new Griffures(1)); return deSbire;
        }
    }

    public int monnaieLachee(){
        switch(this.getNomEntite()){
            case "LICH": return 500;
            case "Grand Mage Noir" : return 400;
            default: return 450;
        }
    }

    //Voir comment faire pour les phases
    public BossFinal genererBossFinal(){
        BossFinal bf = new BossFinal(null);
        attribuerType(bf);
      
        return bf;
    }
}
