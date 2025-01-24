package De.Capacite.Capacite_Ennemi;

import java.io.File;

import javax.imageio.ImageIO;

import De.Capacite.Capacite;

import java.awt.image.BufferedImage;

import Entites.*;

public class Cthulhu_Rlyehs_Curse extends Capacite{
    private Ennemis ennemi;

    public Cthulhu_Rlyehs_Curse(int niveau){
        super(0,"Cthulhu_Rlyehs_Curse");

        this.setNom("Cthulhu_Rlyehs_Curse");
        this.setNiveau(niveau);
        this.setDist(false);
        this.setType(1);
        if(niveau == 1){
            this.setDescription("Inflige "+ niveau + " dégat");
        }
        else{
            this.setDescription("Inflige "+ niveau + " dégats");
        }
        GICRC();
    }

    public void GICRC(){
        try {
            String path = "res/Images/Capacite/AttaqueEnnemis/CTHULHU_R'LYEH'S_CURSE.png";
            this.image = ImageIO.read(new File(path));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void action(Heros h){
        attaqueEnnemi(h);
    }
}
