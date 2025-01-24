package main;

import De.Capacite.Capacite;
import Entites.Ennemis;
import Entites.Heros;

public class Noeud { //Noeuds pour la pile de undo
    Jeu combat;


    boolean pvmax;
    Ennemis e;
    public Heros h1; // si capacité soin ou bouclier - hero donneur
    public Heros h2; // hero receveur
    Capacite c;
    int type; //0=undifined 1=degats 2=soin 3=bouclier 4=mana 12=degats+soin 13=degats+bouclier etc..
    //Toujours définir un double niveau avec le plus petit chiffre suivi du grand chiffre (ex 32 impossible, écrire 23)
    //Permet de pouvoir modifier les stats d'un type d'attaque particulier avec les équipement

    public Noeud(Jeu combat, Capacite c,Heros h,Ennemis e){ //Attaque sur un ennemi
        this.combat = combat;
        this.c  = c;
        this.h1 = h;
        this.e = e;
        this.type = c.getType();
    }
    public Noeud(Jeu combat, Capacite c,Heros h1,Heros h2, boolean pvmax){ //Soin/Bouclier h1 héro donneur, h2 héro receveur
        this.combat = combat;
        this.c  = c;
        this.h1 = h1;
        this.h2 = h2;
        this.type = c.getType(); 
        this.pvmax = pvmax;
    }
    public Noeud(Jeu combat, Capacite c,Ennemis e){ //capacité spé attaque 
        this.combat = combat;
        this.c  = c;
        this.e = e;
        this.type = c.getType();   
    }
    public Noeud(Jeu combat, Capacite c, Heros h1){//récupération de mana
        
        this.combat = combat;
        this.c = c;
        this.h1 = h1;
        this.type = c.getType();  
    }

    public void undo(){
        int[] typeTab = splitEntier(type);
        for(int i = 0;i<typeTab.length;i++){
            
            switch (typeTab[i]) {
                case 1: // dégats
                    this.e.setPdv(this.e.getPdv()+c.getNiveau());
                    h1.getDe().used=false;
                    break;
                case 2: // soin 
                    if(this.pvmax){
                        this.h2.setPdv(this.h2.getPdvMax());
                    }
                    else{
                       this.h2.setPdv(this.h2.getPdv()-c.getNiveau()); 
                    }
                    
                    h1.getDe().used=false;
                    break;
                case 3: // bouclier
                    this.h2.setBouclier(this.h2.getBouclier()-c.getNiveau());
                    h1.getDe().used=false;
                    break;
                case 4: // mana
                    this.combat.getPlaying().getJoueur().setMana(this.combat.getPlaying().getJoueur().getMana()-c.getNiveau());
                    h1.getDe().used=false;
                    break;
            
                default:
                    break;
            }
        }
        
    }
    public static int[] splitEntier(int nombre) {
        String nombreString = Integer.toString(nombre);
        int[] chiffres = new int[nombreString.length()];

        // Parcoure chaque caractère de la chaîne et le converti en int
        for (int i = 0; i < nombreString.length(); i++) {
            chiffres[i] = nombreString.charAt(i) - '0';
        }

        return chiffres;
    }

    public Heros getHeros1(){
        return this.h1;
    }
    public Heros sgetHeros2(){
        return this.h2;
    }
} 
