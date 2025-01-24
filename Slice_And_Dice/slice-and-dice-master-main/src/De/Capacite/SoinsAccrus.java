package De.Capacite;

import Entites.Ennemis;
import Entites.Heros;

public class SoinsAccrus extends Capacite{

    public SoinsAccrus()
    {
        super(0,"SoinsAccrus");

        this.setNom("SoinsAccrus");
        this.setPrixDachat(50);
        this.setNiveau(1);
        this.setDist(false);

        this.setDescription("Soigne tous les points de vie du héros sélectionné");
        initImage();
    }



    public void action(Heros h)
    {
        soinAccrus(h);
    }
}
