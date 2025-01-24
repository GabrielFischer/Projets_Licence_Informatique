package De.Capacite;

import Entites.Ennemis;
import Entites.Entites;

public class Drop extends CapaciteSpe{
    /*Sort disponible si l'Arcaniste est sur le terrain*/
    public Drop()
    {
        super(0, 3,"Drop");
        this.setNom("Drop");
        this.setDist(true);
        this.setNiveau(4);//on dit que le "niveau" est égal à 4 car ce sort inflige toujours 4 dégâts
        this.setDescription("4 dégâts à l'ennemi qui possède le plus de pdv");
        this.setType(1);
        this.cible=0;
    }

    public void action(Ennemis e)
    {
        this.attaque(e);
        /*cette fonction ne prend en compte que les dégâts infligés à l'ennemi, le fait que ces dégpats doivent être infligés à l'ennemi possèdant
        * le plus de points de vie devra être géré dans la classe qui gère le combat*/
    }

}
