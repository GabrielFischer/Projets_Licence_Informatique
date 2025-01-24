package De.Capacite;

import Entites.Ennemis;
import Entites.Entites;

public class Scald extends CapaciteSpe{
    /*Sort disponible si le Sorcier est sur le terrain*/
        public Scald()
        {
            super(0, 3,"Scald");
            this.setNom("Scald");
            this.setDist(true);
            this.setNiveau(2);//on dit que le "niveau" est égal à 2 car ce sort inflige toujours 2 dégâts
            this.setDescription("2 dégâts à tous les ennemis qui ont déjà subi des dégâts");
            this.setType(0);
            this.cible=0;
        }

        public void action(Ennemis e)
        {
            this.attaque(e);
        }
}
