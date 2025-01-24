package Tours;

import java.util.ArrayList;

import Jeu.Niveau;
import Jeu.Plateau;
import Personnages.Soldat;

public class Caserne extends Tours{
    public ArrayList <Soldat> listeSoldat = new ArrayList<Soldat>();
    public Character orientation;
    public Caserne(Niveau niveau ){
        super("Caserne",10,10,0,3,5,1,3,false,20);
        this.niveau = niveau;
        this.plateau = niveau.p;
    }

    public boolean augmenterNv(){
        if(super.augmenterNv()){
            this.augmenterDegatsP((10*this.getNv()));
            this.augmenterVitesseAttaque((2*this.getNv()));
            return true;
        }
        return false;
    }

    public void combat(){
        if (orientation!=null){
            System.out.println("orientation non nulle");
            int px = -1; //Positions du placement du soldat
            int py =-1;
            System.out.println("x : " + x);
            System.out.println("y : " + y);
            switch(orientation){
                case 'n' : px = x; py=y-1; break;
                case 's' : px = x; py=y+1; break;
                case 'w' : px = x-1; py=y; break;
                case 'e' : px = x+1; py=y; break;
            }
            System.out.println("px : " + px);
            System.out.println("py : " + py);
            System.out.println(plateau.hauteur);
            if (px >=0 && px<plateau.tab[0].length && py>=0 && py<plateau.hauteur){
                System.out.println(orientation);
                if(plateau!=null){
                    updateSoldatListe();
                    if (plateau.tab[py][px].p==null){
                        Soldat s = new Soldat();
                        listeSoldat.add(s);
                        
                        this.plateau.setPersonnage(s,px,py) ;
                        s.plateau=this.plateau;
                        this.niveau.setNiveau(s);
                        s.start();
                    }
                }
            }
        }
    }
    
    public void stopToursThread(){
        toursThread.interrupt();
        tueSoldat();
        System.out.println("thread interrompu :" + toursThread.isInterrupted());
    }
    
    public void continueToursThread(){
        super.continueToursThread();
        continueSoldats();
    }
    public void updateSoldatListe(){
        for(int i = 0; i< listeSoldat.size();i++){
            if(!listeSoldat.get(i).enVie){
                
                listeSoldat.remove(i);
            }
        }
    }
    public void tueSoldat(){
  
        for(int i = 0; i< listeSoldat.size();i++){
            if(listeSoldat.get(i).enVie){
                listeSoldat.get(i).mort();
            }
        }
        updateSoldatListe();
    }
   
    public void continueSoldats(){
        for(int i = 0; i< listeSoldat.size();i++){
            listeSoldat.get(i).start();
        }
    }
  
}