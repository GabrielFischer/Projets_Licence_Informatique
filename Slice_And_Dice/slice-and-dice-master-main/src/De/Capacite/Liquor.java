package De.Capacite;

import Entites.Entites;
import Entites.Heros;
public class Liquor extends CapaciteSpe{
/*Sort disponible si le Miracleur est en vie*/
    public Liquor()
    {
        super(0, 4,"Liquor");
        this.setNom("Liquor");
        this.setDist(false);
        this.setNiveau(10);//on dit que le "niveau" est égal à 10 car ce sort soigne toujours 10 pdv
        this.setDescription("soigne 10 pdv à tous les alliés et donne 1 mana");
        this.setType(24);
        this.cible=0;
    }

    public void action(Heros e)
    {
        this.soin(e);
        /*Cette fonction action ne prend en compte que la partie soin de cette capacité, le fait que le soin doive s'appliquer à tous les héros disponibles
        * ainsi que le don de 1 mana devra être géré dans la classe qui gère le combat*/
    }
}
