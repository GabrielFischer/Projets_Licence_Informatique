package Equipement;

import javax.imageio.ImageIO;

public class PetitCoeur extends Equipement {
    
    public PetitCoeur(){
        super("Petit Coeur", "Augmente les PV max du porteur de 2",5,1);
        getImage();
    }

    public void effet(){
        if (perso!=null){
            perso.setPdvMax(perso.getPdvMax()+2); //Augmente le nombre de pv max de 2
            perso.setPdv(perso.getPdv()+2); //Met a jour le nombre d'hp current
        }
    }

    public void annuleEffet(){
        if (perso!=null){
            perso.setPdvMax(perso.getPdvMax()-2);
            perso.setPdv(perso.getPdv()-2);
            if(perso.getPdv()<=0){
                perso.setPdv(1);
            }
        }
    }

    public void getImage(){
        try {
            this.image = ImageIO.read(getClass().getResourceAsStream("../../res/Images/Equipement/PetitCoeur.png"));
        } catch (Exception e) {
           
        }
    }

}
