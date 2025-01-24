package De.Capacite;

import javax.imageio.ImageIO;

import Entites.Ennemis;

public class Arc extends Capacite {
    public Arc(int niveau){
        super(1,"Arc");
        this.setNiveau(niveau);
        this.setDist(true);
        this.setType(1);

        if(niveau == 1){
            this.setDescription("Inflige "+ niveau + " dégat");
        }
        else{
            this.setDescription("Inflige "+ niveau + " dégats");
        }

        initImage();
    }
    
    public void action(Ennemis e){
        attaque(e);
    }
}
