package De.Capacite.Capacite_Ennemi;

import java.io.File;

import javax.imageio.ImageIO;

import De.Capacite.Capacite;

import java.awt.image.BufferedImage;

import Entites.*;

public class Rabbit_Gnaw extends Capacite{
    private Ennemis ennemi;

    public Rabbit_Gnaw(int niveau){
        super(0,"Rabbit_Gnaw");

        this.setNom("Rabbit_Gnaw");
        this.setNiveau(niveau);
        this.setDist(false);
        this.setType(1);
        if(niveau == 1){
            this.setDescription("Inflige "+ niveau + " dégat");
        }
        else{
            this.setDescription("Inflige "+ niveau + " dégats");
        }
        GIRG();
    }

    public void GIRG(){
        try {
            String path = "res/Images/Capacite/AttaqueEnnemis/RABBIT_GNAW.png";
            this.image = ImageIO.read(new File(path));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void action(Heros h){
        attaqueEnnemi(h);
    }
}
