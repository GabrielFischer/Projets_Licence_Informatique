package De.Capacite.Capacite_Ennemi;

import java.io.File;

import javax.imageio.ImageIO;

import De.Capacite.Capacite;

import java.awt.image.BufferedImage;

import Entites.*;

public class Lich_Necromancie extends Capacite{  //Turkey Fear et Goose Fear
    private Ennemis ennemi;

    public Lich_Necromancie (int niveau){
        super(0,"Lich_Necromancie");

        this.setNom("Lich_Necromancie");
        this.setNiveau(niveau);
        this.setDist(false);
        this.setType(1);
        if(niveau == 1){
            this.setDescription("Inflige "+ niveau + " dégat");
        }
        else{
            this.setDescription("Inflige "+ niveau + " dégats");
        }
        GILN();
    }
    public void GILN(){
        try {
            String path = "res/Images/Capacite/AttaqueEnnemis/LICH_NECROMANCIE.png";
            this.image = ImageIO.read(new File(path));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void action(Heros h){
        attaqueEnnemi(h);
    }
}

