package Jeu;

import Personnages.Personnages;
import Tours.Tours;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class Case{
    public int type; 
    public Personnages p;
    public Tours t ;
    public int x;
    public int y;
    public BufferedImage image;
    
    /*
        0 : case de départ (spawn)
        1 : case d'arrivée
        2 : case chemin
        3 : case d'emplacement tourelle
        autre : autre case 
    */

    public Case(int n){
        this.type = n;
    }
    public void draw(Graphics2D g2){
        try {
            
            if (type == 1){
                BufferedImage zelda = ImageIO.read(getClass().getResourceAsStream("../Images/Personnages/Zelda/Zelda1.png"));
                g2.drawImage(zelda,x*48,y*48,48,48,null);
            }
            else if (this.isChemin()){
            if (!this.caseVide()){
                p.draw(g2);
                p.update();
            }
            
            }
            else if (t!=null){
                t.draw(g2);
            }
            
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        
    }
    public boolean isDepart(){
        if (type == 0) return true;
        return false;
    }

    public boolean isArrivee(){
        if (type == 1) return true;
        return false;
    }

    public boolean isChemin(){
        if (type == 2) return true;
        return false;
    }

    public boolean isEmplacementTourelle(){
        if (type == 3) return true;
        return false;
    }

    public boolean isAutre(){
        if (type > 3 || type < 0) return true;
        return false;
    }

    public boolean setPersonnage(Personnages p){
        if (this.isChemin() && this.p==null){
            this.p = p;
            return true;
        }
        else{
            System.out.println("La case n'est pas un chemin ou est déjà occupée.");
            p.plateau=null;
            p.x=0;
            p.y=0;
            return false;
        }
    }

     public boolean setTours(Tours t){
        if (this.isEmplacementTourelle()&&this.t==null){
            this.t = t;
            return true;
        }
        else{
            System.out.println("La case n'est pas un emplacement pour tourelle ou est déjà occupée.");
            t.plateau=null;
            t.x=0;
            t.y=0;
            return false;
        }
    }

    public void setCaseType(int n){
        this.type = n;
    }

    public void setNull(){
        this.t = null;
        this.p = null;
    }

    public boolean caseVide(){
        if (this.t!=null || this.p != null){
            return false;
        }
        return true;
    }

    public String toString(){
        if (this.isArrivee()){
            return "A";
        }
        if (this.isDepart()){
            return "D";
        }
        if (this.isChemin()){
            if (!this.caseVide()){
                return Character.toString(this.p.nom.charAt(0));
            }
            return ".";
        }
        if (this.isEmplacementTourelle()){
            if (!this.caseVide()){
                return Character.toString(this.t.getNom().charAt(0));
            }
            return "*";
        }
        
        return "#";
        
    }

    
}
