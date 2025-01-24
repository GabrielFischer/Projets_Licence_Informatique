package De.Capacite;

import javax.imageio.ImageIO;

import Entites.Ennemis;
import Entites.Entites;
import Entites.Heros;

public class CapaciteSpe extends Capacite{
    public int consommationMana;
    public int cible; 
    /* -1 si doit être suivi de la sélection d'un ennemi 
    0 si le clic suffit à lui même
    1 si doit être suivi de la sélection d'un héro */
    
    public CapaciteSpe(int id,int conso,String nom){
        super(id,nom);
        this.consommationMana = conso;
        initImage();
    }

    public void initImage(){
        try {
            String text = "/Images/Capacite/AttaqueMana/"+this.getNom()+".png";
            this.image = ImageIO.read(getClass().getResourceAsStream(text));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void action(){}
    public void action(Entites e){}
    public void action(Ennemis e){}
    public void action(Heros e){}
}
