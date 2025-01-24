package De.Capacite.Capacite_Ennemi;

import java.io.File;

import javax.imageio.ImageIO;

import De.Capacite.Capacite;

import java.awt.image.BufferedImage;

import Entites.*;

public class PlagueDoctor_Peste extends Capacite{
    private Ennemis ennemi;

    public PlagueDoctor_Peste(int niveau){
        super(0,"PlagueDoctor_Peste");

        this.setNom("PlagueDoctor_Peste");
        this.setNiveau(niveau);
        this.setDist(false);
        this.setType(1);
        if(niveau == 1){
            this.setDescription("Inflige "+ niveau + " dégat");
        }
        else{
            this.setDescription("Inflige "+ niveau + " dégats");
        }
        GIPDP();
    }

    public void GIPDP(){
        try {
            String path = "res/Images/Capacite/AttaqueEnnemis/PLAGUEDOCTOR_PESTE.png";
            this.image = ImageIO.read(new File(path));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void action(Heros h){
        attaqueEnnemi(h);
    }
}