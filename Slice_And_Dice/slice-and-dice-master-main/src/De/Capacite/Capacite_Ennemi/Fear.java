package De.Capacite.Capacite_Ennemi;

import java.io.File;
import java.util.Random;

import javax.imageio.ImageIO;

import De.Capacite.Capacite;

import java.awt.image.BufferedImage;

import Entites.*;

public class Fear extends Capacite{  //Turkey Fear et Goose Fear
    private Ennemis ennemi;

    public Fear (int niveau){
        super(0,"Fear");

        this.setNom("Fear");
        this.setNiveau(niveau);
        this.setDist(false);
        this.setType(1);
        if(niveau == 1){
            this.setDescription("Inflige "+ niveau + " dégat");
        }
        else{
            this.setDescription("Inflige "+ niveau + " dégats");
        }
        GIGF();
    }

    public void GIGF(){
        try {
            String path = "res/Images/Capacite/AttaqueEnnemis/GOOSE_FEAR.png";
            this.image = ImageIO.read(new File(path));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void action(Heros h){
        attaqueEnnemi(h);
        
    }
}
