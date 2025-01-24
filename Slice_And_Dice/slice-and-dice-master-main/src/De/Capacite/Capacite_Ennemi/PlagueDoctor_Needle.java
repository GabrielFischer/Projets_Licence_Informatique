package De.Capacite.Capacite_Ennemi;

import java.io.File;

import javax.imageio.ImageIO;

import De.Capacite.Capacite;

import java.awt.image.BufferedImage;

import Entites.*;

public class PlagueDoctor_Needle extends Capacite{
    private Ennemis ennemi;

    public PlagueDoctor_Needle(int niveau){
        super(0,"PlagueDoctor_Needle");

        this.setNom("PlagueDoctor_Needle");
        this.setNiveau(niveau);
        this.setDist(false);
        this.setType(1);
        if(niveau == 1){
            this.setDescription("Inflige "+ niveau + " dégat");
        }
        else{
            this.setDescription("Inflige "+ niveau + " dégats");
        }
        GIPDN();
    }

    public void GIPDN(){
        try {
            String path = "res/Images/Capacite/AttaqueEnnemis/PLAGUEDOCTOR_NEEDLE.png";
            this.image = ImageIO.read(new File(path));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void action(Heros h){
        attaqueEnnemi(h);
    }
}
