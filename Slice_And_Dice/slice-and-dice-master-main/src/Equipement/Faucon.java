package Equipement;

import javax.imageio.ImageIO;

import De.Face;

public class Faucon extends Equipement{
    
    public Faucon(){
        super("Faucon","Toutes les attaques du porteur peuvent atteindre la ligne arrière",10,2);
        getImage();
    }
    public void getImage(){
        try {
            this.image = ImageIO.read(getClass().getResourceAsStream("../../res/Images/Equipement/Faucon.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void effet(){
        if (perso!=null){
            for (Face face : perso.getDe().face){
                if (face.attaque != null){
                    face.attaque.setDist(true);
                }
            }
        }
    }

    public void annuleEffet(){
        if (perso!=null){
            for (Face face : perso.getDe().face){
                if (face.attaque != null){
                    face.attaque.setDist(false);
                }
            }
        }
    }
}
