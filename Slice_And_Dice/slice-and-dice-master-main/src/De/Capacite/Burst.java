package De.Capacite;

import Entites.*;

public class Burst extends CapaciteSpe{
/*Sort toujours disponible*/
    public Burst() {
        super(0, 2,"Burst");
        this.setDist(true);
        this.setNiveau(3);//on dit que le "niveau" est égal à 3 car ce sort inflige toujours 3 dégâts
        this.setDescription("3 dégâts");
        this.setType(1);
        this.cible=-1;
    }

    @Override
    public void action(Ennemis e)
    {
        this.attaque(e);
    }

}
