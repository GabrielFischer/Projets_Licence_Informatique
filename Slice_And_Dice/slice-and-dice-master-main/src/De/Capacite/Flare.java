package De.Capacite;

import Entites.Ennemis;
import Entites.Entites;

public class Flare extends CapaciteSpe{
/*Sort disponible si le mage est sur le terrain*/
    public Flare()
    {
        super(0, 4,"Flare");
        this.setNom("Flare");
        this.setDist(true);
        this.setNiveau(4);//on dit que le "niveau" est égal à 4 car ce sort inflige toujours 4 dégâts
        this.setDescription("4 dégâts");
        this.setType(1);
        this.cible=-1;
    }

    public void action(Ennemis e)
    {
        this.attaque(e);
    }

}
