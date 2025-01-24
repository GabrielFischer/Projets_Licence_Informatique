package Equipement;

import javax.imageio.ImageIO;

import De.Face;
import De.Capacite.Capacite;
import De.Capacite.CoupEpee;

public class ForceGauche extends Equipement{
    Capacite ac0;
    Capacite ac1;
    
    public ForceGauche(){
        super("Force Gauche", "Remplace la partie gauche du dé avec deux épées de niveau 4", 10, 2);
        getImage();
    }

    public void effet(){
        if (this.perso!=null && this.perso.getDe()!=null){
            if (1<perso.getDe().face.size()){ //Vérifier que les cases 0-1 (gauche) existent
                ac0 = perso.getDe().getFace(0).getCapacite();
                ac1 = perso.getDe().getFace(1).getCapacite();
                Capacite nc0 = new CoupEpee(4); 
                Capacite nc1 = new CoupEpee(4); 
                perso.getDe().setFace(0, nc0);
                perso.getDe().setFace(1, nc1);

            }
        }
    }

    public void annuleEffet(){
        if (this.perso!=null && this.perso.getDe()!=null){
            perso.getDe().setFace(0, ac0);
            perso.getDe().setFace(1, ac1);
        }
    }

    public void getImage(){
        try {
            this.image = ImageIO.read(getClass().getResourceAsStream("../../res/Images/Equipement/ForceGauche.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
