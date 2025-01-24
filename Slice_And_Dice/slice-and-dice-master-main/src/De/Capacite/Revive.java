package De.Capacite;

import Entites.Entites;
import Entites.Heros;
public class Revive extends CapaciteSpe{
/*Sort disponible si le Starseer est sur le terrain*/
    public Revive()
    {
        super(0, 4,"Revive");
        this.setNom("Revive");
        this.setDist(false);
        this.setNiveau(0);//on dit que le "niveau" est égal à 0 car pas d'utilisation du niveau pour ce sort
        this.setDescription("ranime complètement l'allié le plus en haut de la liste");
        this.setType(2);
        this.cible=0;
    }

    public void action(Heros e)
    {
        e.setPdv(e.getPdvMax());
    }

}
