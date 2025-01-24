package De.Capacite.Capacite_Ennemi;

import java.io.File;

import javax.imageio.ImageIO;

import De.Capacite.Capacite;

import java.awt.image.BufferedImage;

import Entites.*;

public class GrandMageNoir_Conjuration extends Capacite{
    private Ennemis ennemi;

    public GrandMageNoir_Conjuration(int niveau){
        super(0,"GrandMageNoir_Conjuration");

        this.setNom("GrandMageNoir_Conjuration");
        this.setNiveau(niveau);
        this.setDist(false);
        this.setType(1);
        if(niveau == 1){
            this.setDescription("Inflige "+ niveau + " dégat");
        }
        else{
            this.setDescription("Inflige "+ niveau + " dégats");
        }
        GIGMNC();
    }

    public void GIGMNC(){
        try {
            String path = "res/Images/Capacite/AttaqueEnnemis/GRANDMAGENOIR_CONJURATION.png";
            this.image = ImageIO.read(new File(path));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void action(Heros h){
        attaqueEnnemi(h);
    }
}
