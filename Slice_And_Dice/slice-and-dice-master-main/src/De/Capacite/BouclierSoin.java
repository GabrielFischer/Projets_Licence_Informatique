package De.Capacite;

import Entites.Heros;

public class BouclierSoin extends Capacite{

    public BouclierSoin(int niveau)
    {
        super(0,"BouclierSoin");

        this.setPrixDachat(20);
        this.setNiveau(niveau);
        this.setDist(false);


        this.setDescription("Soigne et protège le héros sélectionné");
        initImage();
    }



    public void action(Heros h)
    {
        soin(h);
        bouclier(h);
    }
}
