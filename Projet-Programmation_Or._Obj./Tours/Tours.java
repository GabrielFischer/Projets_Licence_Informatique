package Tours;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import Jeu.Case;
import Jeu.GamePanel;
import Jeu.Niveau;
import Jeu.Plateau;
import Personnages.Personnages;
import Personnages.Soldat;
import Personnages.Ennemis.Moblin;



public class Tours extends Thread {
    public int prix;
    private int degatsPhysiques;
    private int degatsMagiques;
    private int nvMax;
    private int vitesseAttaque;
    private int nv;
    private String nom;
    private int degatDeZone;
    private boolean ralentissement;
    private int range;
    public int x;
    public int y;
    public Plateau plateau;
    Thread toursThread;
    public Niveau niveau;

    int graphicX;
    int graphicY;

    GamePanel gp;
     

    BufferedImage image;
   
    

    public Tours(String nom, int p, int dP, int dM, int nbA, int vA,int nv,int dZ, boolean r,int range){
        this.nom = nom;
        this.prix=p;
        this.degatsPhysiques=dP;
        this.degatsMagiques=dM;
        this.nvMax=nbA;
        this.vitesseAttaque=vA;
        this.nv=nv;
        this.degatDeZone = dZ;
        this.ralentissement = r;
        this.range = range;
        
        //startToursThread();
    } 

    public void draw(Graphics2D g2){
        
    }
    
    public void startToursThread(){
        toursThread = new Thread(this);
        toursThread.start();
    }
    public void stopToursThread(){
        
        toursThread.interrupt();
        if (this instanceof Caserne){
            //this.tueSoldat();
        }
        System.out.println("thread interromptu :" + toursThread.isInterrupted());
    }
    public void continueToursThread(){
        toursThread.start();
    }

    public void supprimer(){
        stopToursThread();
        this.plateau.argent += (this.prix*nv) / 2; //Rembouse la moitié du prix de la tour
        this.plateau = null;
        this.x = -1;
        this.y = -1;
        //this = null;
    }
    
    public void run(){
        //Thread thread2 = new Thread(() -> {
            System.out.println("Tour créée");
            while (!Thread.interrupted()) {
                while (!niveau.gameOver()){
                    this.combat();
                    
                    try {
                        Thread.sleep((long)1000*vitesseAttaque);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                        // TODO Auto-generated catch block
                        //e.printStackTrace();
                    }
                    
                    
                }
                break;
            }
            //});
        //thread2.start();
    }


    public void augmenterDegatsP(int n){//augmente dégats physiques de n
        this.degatsPhysiques+=n;
    }
    public void augmenterDegatsM(int n){ //augmente dégats magiques de n
        this.degatsMagiques+=n;
    }
    public void augmenterVitesseAttaque(int n){
        if (vitesseAttaque+n <10){
            this.vitesseAttaque+=n;
        }else{
            this.vitesseAttaque = 10;
        }
    }
    public int getNv(){
        return this.nv;
    }
    public String getNom(){
        return this.nom;
    }

    public boolean nvMax(){ //regarde si la tour a atteint le niveau max
        if (this.nv == this.nvMax){
            return true;
        }
        return false;        
    }
    
    public boolean augmenterNv(){ //regarde si la tour a atteint son nv max, augmente son nv de 1 si ce n'est pas le cas
        if(this.nvMax()){
            return false;
        }
        this.nv++;
        System.out.println("Niveau de " + nom + "augmenté");
        this.afficher();
        return true;
    }

    public void afficher(){
        System.out.println(this.nom + "\nNiveau: "+this.nv + ": \nDégats Physiques: " + this.degatsPhysiques + "\nVitesseAttaque: " + this.vitesseAttaque);
        
    }

    public Personnages detecte(){
        for (int z = 0; z<=this.range; z++){
            for (int i=y-1-z;i<y+1+z;i++){
                for (int j=x-1-z;j<y+i+z;j++){
                    if (i>=0 && i<plateau.getTab().length && j>=0 && j<plateau.getTab()[0].length){
                        if (!plateau.getTab()[i][j].caseVide() && plateau.getTab()[i][j].p!=null && plateau.getTab()[i][j].p.type==false ){
                            return plateau.getTab()[i][j].p;
                        }
                    }
                }
            }
        }
        return null;
    }

    public void attaque(){
        Personnages p = this.detecte();
        int dP = this.degatsPhysiques;
        int dM = this.degatsMagiques;
        if (p!=null){
            if (!(p.type)){
                if(p.enVie){
                    int deP = dP-p.resistancePhysique; if (deP<1) deP = 1;
                    int deM = dM-p.resistanceMagique; if (deM<1) deM = 1;
                    p.prendreDegats(deP);
                    p.prendreDegats(deM);
                    System.out.println(this.nom + " a attaqué "+p.nom+", il lui reste "+p.getPv() + " pv.");
                    if (p.getPv()<=0){
                        p.enVie=false;
                        plateau.getTab()[p.y][p.x].setNull();
                        p.plateau.getTab()[p.y][p.x].p=null;
                        p.posChemin = null;
                        System.out.println(p.nom+" est mort");
                        this.plateau.argent+=p.valeur*p.nv;
                        if (niveau.marathon){
                            niveau.score += p.valeur * 100;
                        }
                    }
                    try {
                        Thread.sleep((long)10000/vitesseAttaque);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void combat(){
        while(plateau!=null){
            this.attaque();
        }
    }



    /* 
    public static void main(String[] args) {
        Base b = new Base();
        Archers a = new Archers();
        a.afficher();
        a.augmenterNv();
        a.augmenterNv();
        a.augmenterNv();
        System.out.println("nv augmenté");
        a.afficher();

        Plateau p = new Plateau(20, 20,b);
        Moblin m = new Moblin(1);
        p.getTab()[10][7].setPersonnage(m);
        p.getTab()[9][7].setTours(a);
        System.out.println(a.detecte());


    }*/
}
