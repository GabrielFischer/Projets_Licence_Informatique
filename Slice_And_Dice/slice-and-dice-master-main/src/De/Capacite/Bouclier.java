package De.Capacite;

import javax.imageio.ImageIO;

import Entites.Heros;

public class Bouclier extends Capacite {
    public Bouclier(int niveau){
        super(0,"Bouclier");
        this.setNiveau(niveau);
        this.setDist(false);
        this.setType(3);

        if(niveau == 1){
            this.setDescription("Protège de "+ niveau + " dégat");
        }
        else{
            this.setDescription("Protège de "+ niveau + " dégats");
        }

        initImage();
    }
    public void action(Heros h){
        bouclier(h);
    }
}
