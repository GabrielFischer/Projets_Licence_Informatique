package De.Capacite.Capacite_Ennemi;

import java.io.File;

import javax.imageio.ImageIO;

import De.Capacite.Capacite;

import java.awt.image.BufferedImage;

import Entites.*;

public class Goose_Pinch extends Capacite{
    private Ennemis ennemi;

    public Goose_Pinch(int niveau){
        super(0,"Goose_Pinch");

        this.setNom("Goose Pinch");
        this.setNiveau(niveau);
        this.setDist(false);
        this.setType(1);
        if(niveau == 1){
            this.setDescription("Inflige "+ niveau + " dégat");
        }
        else{
            this.setDescription("Inflige "+ niveau + " dégats");
        }
        GIGP();
    }

    public void GIGP(){
        try {
            String path = "res/Images/Capacite/AttaqueEnnemis/GOOSE_PINCH.png";
            this.image = ImageIO.read(new File(path));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void action(Heros h){
        attaqueEnnemi(h);
    }
}
