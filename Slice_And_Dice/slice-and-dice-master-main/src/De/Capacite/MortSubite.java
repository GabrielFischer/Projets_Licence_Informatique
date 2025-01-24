package De.Capacite;

import Entites.Ennemis;
import Entites.Heros;

public class MortSubite extends Capacite{

    public MortSubite()
    {
        super(0,"MortSubite");

        this.setNom("MortSubite");
        this.setPrixDachat(70);
        this.setNiveau(1);
        this.setDist(true);

        this.setDescription("Tue l'ennemi sélectionné en une fois");
        initImage();
    }



    public void action(Ennemis e)
    {
        mortSubite(e);
    }
}
