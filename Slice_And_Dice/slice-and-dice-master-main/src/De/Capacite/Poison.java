package De.Capacite;

import Entites.Ennemis;
import Entites.Heros;

public class Poison extends Capacite{
    public Poison(int niveau)
    {
        super(0,"Poison");

        this.setNom("Poison");
        this.setPrixDachat(15);
        this.setNiveau(niveau);//le niveau de la capacité poison correspond au nombre de tours pendant lesquels la cible va subir le poison
        this.setDist(false);
        this.setType(1);

        this.setDescription("Empoisonne la cible");
        initImage();
    }


    public void action(Ennemis e)
    {
        poison(e);
    }
}
