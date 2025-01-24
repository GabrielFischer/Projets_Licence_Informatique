package De.Capacite;

import Entites.Entites;
import Entites.Heros;
public class Renew extends CapaciteSpe{
/*Sort disponible si le Guerisseur est sur le terrain*/
    public Renew()
    {
        super(0, 2,"Renew");
        this.setNom("Renew");
        this.setDist(false);
        this.setNiveau(4);//on dit que le "niveau" est égal à 4 car ce sort soigne toujours 4 points de vie
        this.setDescription("4 pv soignés");
        this.setType(2);
        this.cible=1; //Necessite un clic sur le hero a soigner
    }

    public void action(Heros e)
    {
        this.soin(e);
    }
}
