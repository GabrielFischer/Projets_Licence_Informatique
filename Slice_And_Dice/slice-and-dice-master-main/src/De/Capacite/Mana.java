package De.Capacite;

import main.Joueur;

import javax.imageio.ImageIO;


public class Mana extends Capacite{
    public Mana(int niveau){
        super(0,"Mana");

        this.setNom("Mana");;
        this.setNiveau(niveau);
        this.setDist(false);
        this.setType(4);
        this.setDescription("Donne "+ niveau + " mana");
    
        initImage();
        
    }

    public void action(Joueur j){
        mana(j);
    }
}
