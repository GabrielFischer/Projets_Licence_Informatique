package De.Capacite;

import java.io.File;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;

import Entites.Heros;

public class Griffures extends Capacite{
    public Griffures(int niveau){
        super(0,"Griffures");

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
            String path = "res/Images/Capacite/AttaqueEnnemis/GRIFFURES.png";
            this.image = ImageIO.read(new File(path));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void action(Heros h){
        attaqueEnnemi(h);
    }
}
