package De.Capacite.Capacite_Ennemi;

import java.io.File;

import javax.imageio.ImageIO;

import De.Capacite.Capacite;

import java.awt.image.BufferedImage;

import Entites.*;

public class Cthulhu_DreamOfFishes extends Capacite{
    private Ennemis ennemi;

    public Cthulhu_DreamOfFishes(int niveau){
        super(0,"Cthulhu_DreamOfFishes");

        this.setNom("Cthulhu_DreamOfFishes");
        this.setNiveau(niveau);
        this.setDist(false);
        this.setType(1);
        if(niveau == 1){
            this.setDescription("Inflige "+ niveau + " dégat");
        }
        else{
            this.setDescription("Inflige "+ niveau + " dégats");
        }
        GICDOF();
  
    }

    public void GICDOF(){
        try {
            String path = "res/Images/Capacite/AttaqueEnnemis/CTHULHU_DREAM_OF_FISHES.png";
            this.image = ImageIO.read(new File(path));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void action(Heros h){
        attaqueEnnemi(h);
    }
}
