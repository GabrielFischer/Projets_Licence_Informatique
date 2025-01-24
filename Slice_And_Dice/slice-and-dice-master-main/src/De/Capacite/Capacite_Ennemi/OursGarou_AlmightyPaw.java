package De.Capacite.Capacite_Ennemi;

import java.io.File;

import javax.imageio.ImageIO;

import De.Capacite.Capacite;

import java.awt.image.BufferedImage;

import Entites.*;

public class OursGarou_AlmightyPaw extends Capacite{  //Turkey Fear et Goose Fear
    private Ennemis ennemi;

    public OursGarou_AlmightyPaw (int niveau){
        super(0,"OursGarou_AlmightyPaw");

        this.setNom("OursGarou_AlmightyPaw");
        this.setNiveau(niveau);
        this.setDist(false);
        this.setType(1);
        if(niveau == 1){
            this.setDescription("Inflige "+ niveau + " dégat");
        }
        else{
            this.setDescription("Inflige "+ niveau + " dégats");
        }
        GIOGAP();
    }

    public void GIOGAP(){
        try {
            String path = "res/Images/Capacite/AttaqueEnnemis/OURSGAROU_ALMIGHTYPAW.png";
            this.image = ImageIO.read(new File(path));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void action(Heros h){
        attaqueEnnemi(h);
    }
}

