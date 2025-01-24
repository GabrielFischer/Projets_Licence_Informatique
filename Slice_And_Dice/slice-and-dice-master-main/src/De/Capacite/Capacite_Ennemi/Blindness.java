package De.Capacite.Capacite_Ennemi;

import java.io.File;

import javax.imageio.ImageIO;

import De.Capacite.Capacite;

import java.awt.image.BufferedImage;

import Entites.*;

public class Blindness extends Capacite{ 
    private Ennemis ennemi;

    public Blindness(int niveau){
        super(0,"Blindness");

        this.setNom("Blindness");
        this.setNiveau(niveau);
        this.setDist(false);
        this.setType(1);
        if(niveau == 1){
            this.setDescription("Inflige "+ niveau + " dégat");
        }
        else{
            this.setDescription("Inflige "+ niveau + " dégats");
        }
        GIB();
  
    }
    public void GIB(){ //A voir comment faire pour détecter la phase et le type d'enum du boss...
        try {
            String path = "res/Images/Capacite/AttaqueEnnemis/KITTEN_BLINDNESS.png";
            this.image = ImageIO.read(new File(path));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void action(Heros h){
        attaqueEnnemi(h);
    }
}
