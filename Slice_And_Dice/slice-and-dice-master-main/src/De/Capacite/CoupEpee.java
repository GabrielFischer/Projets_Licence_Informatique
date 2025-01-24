package De.Capacite;

import javax.imageio.ImageIO;

import Entites.Ennemis;

public class CoupEpee extends Capacite{
    public CoupEpee(int niveau){
        super(0,"CoupEpee");

        this.setNiveau(niveau);
        this.setDist(false);
        this.setType(1);
        if(niveau == 1){
            this.setDescription("Inflige "+ niveau + " dégat");
        }
        else{
            this.setDescription("Inflige "+ niveau + " dégats");
        }

        super.initImage();
        
    }
    public void action(Ennemis e){
        attaque(e);
    }
}
