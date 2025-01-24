package De.Capacite;

import Entites.Entites;
import Entites.Heros;
public class Infuse extends CapaciteSpe{
    /*Sort disponible si l'Apothicaire est en vie*/
    public Infuse()
    {
        super(0, 3,"Infuse");
        this.setNom("Infuse");
        this.setDist(false);
        this.setNiveau(6);//on dit que le "niveau" est égal à 6 car ce sort soigne toujours 6 pdv
        this.setDescription("soigne 6 pdv à l'allié qui en possède le moins");
        this.setType(2);
        this.cible=0;
    }
 
    public void action(Heros e)
    {
        this.soin(e);
        /*Cette fonction ne prend en compte que le soin du héros, le fait que ce soit le héros qui possède le moins de points de vie
        * qui doive recevoir le soin sera géré dans la classe qui gère le combat*/
    }
}
