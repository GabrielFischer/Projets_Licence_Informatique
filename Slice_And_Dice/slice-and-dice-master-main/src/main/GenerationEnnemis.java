package main;

import java.util.ArrayList;
import java.util.Random;

import Entites.Boss;
import Entites.BossFinal;
import Entites.Capitaine;
import Entites.Ennemis;
import Entites.Entites;
import Entites.Heros;
import Entites.Sbire;
import Entites.SousBoss;
import Graphics.scenes.Playing;

public class GenerationEnnemis {
    /*
     * Classe qui permet la génération des ennemis en fonction de la difficulté 
     */
    private Jeu combat;
    private Playing playing;
    private Ennemis[] ennemisPlaying;
    private Joueur joueur;
    private Main main;
    private Difficulte difficulte;
    //public Niveau niveau = Niveau.NIVEAU1; //initialise a niveau1 au debut, car preparationDuJeu() n'est jamais utilise pour l'instant, donc niveau n'est pas initialise pour l'instant
    public int niveau = 1;
    //private ArrayList<Ennemis> ennemisActuels;  //ArrayList contenant les ennemis du niveau actuel --> problème arraylist et tableau, à régler
    private Sbire sbire; private Capitaine cap; private SousBoss sousboss; private Boss boss;private BossFinal bossfinal;    //pour induire les fonctions de leurs classes
    private FinDeJeu findejeu;


    GenerationEnnemis(Playing p,Difficulte d){
        playing=p;
        ennemisPlaying=playing.getListeEnnemis();
        combat=p.getCombat();
        joueur=p.getJoueur();
        main=p.getMain();
        difficulte=d;
        preparationDuJeu();
    }


    public void preparationDuJeu(){  //à ajouter au tout début de la sélection d'un bouton du menu, à reconfigurer car on ne peut pas get comme ça les pdv
        
        initialisationEnnemis();
        genererEnnemis();
        switch(difficulte){
            case FACILE: //pv des héros plus haute, pv des ennemis plus bas, plus de proba qu'un ennemi fasse peu de dégâts
                
                for (Heros h : playing.getListeHeros()){
                    h.setPdv(h.getPdv()+3);
                }
                for (Ennemis e : playing.getListeEnnemis()){
                    e.setPdv(e.getPdv()-3);
                }
                break;

            case MOYEN: //pv des héros et ennemis à la bonne mesure
                break;

            case DIFFICILE: //pv des ennemis plus haute, plus de proba qu'un ennemi fasse bcp de dégâts, plus faible chance pour le joueur de faire des oneshot
                for (Heros h : playing.getListeHeros()){
                    h.setPdv(h.getPdv()-1);
                }
                for (Ennemis e : playing.getListeEnnemis()){
                    e.setPdv(e.getPdv()+2);
                }
                break;

            case INFERNO: //pv des ennemis encore plus haut, les sbires ont bcp plus de pv que les boss
                
                for (Heros h : playing.getListeHeros()){
                    h.setPdv(h.getPdv()-2);
                }
                for (Ennemis e : playing.getListeEnnemis()){
                    if (e instanceof Boss || e instanceof BossFinal){
                        e.setPdv(e.getPdv()+2);
                    }else if(e instanceof Sbire || e instanceof Capitaine || e instanceof SousBoss){
                        e.setPdv(e.getPdv()+7);
                    }
                }
                break;
        }   
    }

    //TEST POUR L'INSTANT SVP
    public void genererEnnemis(){
        switch(niveau){
            case 1 : Ennemis[] e1 = {sbire.genererSbire(),sbire.genererSbire(),sbire.genererSbire()}; 
            playing.setListeEnnemis(e1); break;

            case 2 : Ennemis[] e2 = {sbire.genererSbire(),sbire.genererSbire(),sbire.genererSbire(),sbire.genererSbire(),sbire.genererSbire()}; 
            playing.setListeEnnemis(e2); break;
            
            case 3 : Ennemis[] e3 = {sbire.genererSbire(),cap.genererCap(),sbire.genererSbire()}; 
            playing.setListeEnnemis(e3); break;

            case 4 : Ennemis[] e4 = {cap.genererCap(),sbire.genererSbire(),cap.genererCap()}; 
            playing.setListeEnnemis(e4); break;

            case 5 : Ennemis[] e5 = {sbire.genererSbire(),sbire.genererSbire(),sousboss.genererSousBoss(),sbire.genererSbire(),sbire.genererSbire()}; 
            playing.setListeEnnemis(e5); break;

            case 6 : Ennemis[] e6 = {cap.genererCap(),boss.genererBoss(),cap.genererCap()}; 
            playing.setListeEnnemis(e6); break;

            case 7 : Ennemis[] e7 = {sbire.genererSbire(),cap.genererCap(),sousboss.genererSousBoss(),cap.genererCap(),sbire.genererSbire()}; 
            playing.setListeEnnemis(e7); break;

            case 8 : Ennemis[] e8 = {sousboss.genererSousBoss(),sousboss.genererSousBoss(),sousboss.genererSousBoss()}; 
            playing.setListeEnnemis(e8); break;

            case 9 : Ennemis[] e9 = {cap.genererCap(),cap.genererCap(),cap.genererCap(),cap.genererCap(),cap.genererCap()}; 
            playing.setListeEnnemis(e9); break;

            case 10 : Ennemis[] e10 = {cap.genererCap(),sousboss.genererSousBoss(),cap.genererCap(),sousboss.genererSousBoss(),cap.genererCap()}; 
            playing.setListeEnnemis(e10); break;

            case 11 : Ennemis[] e11 = {sousboss.genererSousBoss(),boss.genererBoss(),sousboss.genererSousBoss()}; 
            playing.setListeEnnemis(e11); break;

            case 12 : Ennemis[] e12 = {sousboss.genererSousBoss(),bossfinal.genererBossFinal(),sousboss.genererSousBoss()}; 
            playing.setListeEnnemis(e12); break;
        }
    }

    public void initialisationEnnemis(){
        sbire = new Sbire(null);
        cap= new Capitaine(null);
        sousboss = new SousBoss(null);
        boss = new Boss(null);
        bossfinal = new BossFinal( null);
    }

    

    
}
