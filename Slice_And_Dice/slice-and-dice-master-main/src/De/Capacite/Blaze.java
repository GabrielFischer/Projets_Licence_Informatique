package De.Capacite;

import Entites.Ennemis;
import Entites.Entites;

public class Blaze extends CapaciteSpe{
/*Sort disponible si le Chaman est sur le terrain*/
    public Blaze()
    {
        super(0, 6,"Blaze");
        this.setNom("Blaze");
        this.setDist(true);
        this.setNiveau(13);
        this.setDescription("inflige 13 dégâts");
        this.setType(1);
        this.cible=-1;
    }

    public void action(Ennemis e)
    {
        this.attaque(e);
    }
}
