package De.Capacite.Capacite_Ennemi;

import java.io.File;

import javax.imageio.ImageIO;

import De.Capacite.Capacite;

import java.awt.image.BufferedImage;

import Entites.*;

public class Rabbit_Pinch extends Capacite{
    public Rabbit_Pinch(int niveau){
        super(0,"Rabbit_Pinch");

        this.setNiveau(niveau);
        this.setDist(false);
        this.setType(1);
        if(niveau == 1){
            this.setDescription("Inflige "+ niveau + " dégat");
        }
        else{
            this.setDescription("Inflige "+ niveau + " dégats");
        }
        
        getImageGriffures();
        
    }
    public void getImageGriffures(){
        try {
            String path = "res/Images/Capacite/AttaqueEnnemis/RABBIT_PINCH.png";
            this.image = ImageIO.read(new File(path));
        } catch (Exception e) {
            e.printStackTrace();
        }
        GIRP();
    }

    public void GIRP(){
        try {
            String path = "res/Images/Capacite/AttaqueEnnemis/RABBIT_PINCH.png";
            this.image = ImageIO.read(new File(path));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void action(Heros h){
        attaqueEnnemi(h);
    }
}
