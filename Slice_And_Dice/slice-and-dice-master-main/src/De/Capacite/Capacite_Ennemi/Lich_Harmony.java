package De.Capacite.Capacite_Ennemi;

import java.io.File;

import javax.imageio.ImageIO;

import De.Capacite.Capacite;

import java.awt.image.BufferedImage;

import Entites.*;

public class Lich_Harmony extends Capacite{  //Turkey Fear et Goose Fear
    private Ennemis ennemi;

    public Lich_Harmony (int niveau){
        super(0,"Lich_Harmony");

        this.setNom("Lich_Harmony");
        this.setNiveau(niveau);
        this.setDist(false);
        this.setType(1);
        if(niveau == 1){
            this.setDescription("Inflige "+ niveau + " dégat");
        }
        else{
            this.setDescription("Inflige "+ niveau + " dégats");
        }
        GILH();
    }

    public void GILH(){
        try {
            String path = "res/Images/Capacite/AttaqueEnnemis/LICH_HARMONY.png";
            this.image = ImageIO.read(new File(path));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void action(Heros h){
        attaqueEnnemi(h);
    }
}
