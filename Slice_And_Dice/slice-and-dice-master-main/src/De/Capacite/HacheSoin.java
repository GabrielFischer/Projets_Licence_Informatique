package De.Capacite;

import Entites.Ennemis;
import Entites.Heros;

public class HacheSoin extends Capacite{

    public HacheSoin(int niveau)
    {
        super(0,"HacheSoin");

        this.setNom("HacheSoin");
        this.setPrixDachat(25);
        this.setNiveau(niveau);
        this.setDist(false);

        this.setDescription("Soigne l'utilisateur et inflige des dégâts");
        initImage();
    }


    public void action(Ennemis e, Heros h)
    {
        soin(h);
        attaque(e);
    }
}
