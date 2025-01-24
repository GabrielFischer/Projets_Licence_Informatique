package De.Capacite.Capacite_Ennemi;

import java.io.File;

import javax.imageio.ImageIO;

import De.Capacite.Capacite;

import java.awt.image.BufferedImage;

import Entites.*;

public class GrandMageNoir_MinionsCall extends Capacite{
    private Ennemis ennemi;

    public GrandMageNoir_MinionsCall(int niveau){
        super(0,"GrandMageNoir_MinionsCall");

        this.setNom("GrandMageNoir_MinionsCall");
        this.setNiveau(niveau);
        this.setDist(false);
        this.setType(1);
        if(niveau == 1){
            this.setDescription("Inflige "+ niveau + " dégat");
        }
        else{
            this.setDescription("Inflige "+ niveau + " dégats");
        }
        GIGMNMC();
    }

    public void GIGMNMC(){
        try {
            String path = "res/Images/Capacite/AttaqueEnnemis/GRANDMAGENOIR_MINIONSCALL.png";
            this.image = ImageIO.read(new File(path));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void action(Heros h){
        attaqueEnnemi(h);
    }
}
