package De.Capacite;

import Entites.Entites;
import Entites.Ennemis;
import Entites.Heros;
public class Balance extends CapaciteSpe{
    /*Ce sort est rendu disponible si le Guerisseur est amélioré en Prêtre*/
    public Balance()
    {
        super(0, 4,"Balance");
        this.setNom("Balance");
        this.setDist(true);
        this.setNiveau(2);//on dit que le "niveau" est égal à 2 car ce sort inflige toujours 2 dégâts et soigne toujours 2 points de vie
        this.setDescription("2 dégâts à tous les ennemis + soin 2 pdv tous les alliés");
        this.setType(12);
        this.cible=0;
    }

    public void action(Ennemis e)
    {
        e.setPdv(e.getPdv()-this.getNiveau());
    }
    public void action(Heros e)
    {
        if(e.getPdv()+this.getNiveau()>e.getPdvMax())
        {
            e.setPdv(e.getPdvMax());
        }
        else
        {
            e.setPdv(e.getPdv()+this.getNiveau());
        }
    }
    
}
