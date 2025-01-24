package De.Capacite;

import javax.imageio.ImageIO;

import Entites.Heros;

public class Soin extends Capacite{
    public Soin(int niveau){
        super(0,"Soin");

        this.setNom("Soin");
        this.setNiveau(niveau);
        this.setDist(false);
        this.setType(2);
        
        this.setDescription("Soigne "+ niveau + " Pv");
        initImage();
        
    }
    
    public void action(Heros h){
        soin(h);
    }
}
