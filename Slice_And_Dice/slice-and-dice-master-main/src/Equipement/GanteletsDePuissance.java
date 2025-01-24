package Equipement;

import javax.imageio.ImageIO;

import De.Face;

public class GanteletsDePuissance extends Equipement{
    
    public GanteletsDePuissance(){
        super("Gantelets de puissance","Toutes les attaques du porteur gagnent 1 niveau",11,2);
        getImage();
    }

    public void effet(){
        if (perso!=null){
            for (Face face : perso.getDe().face){ 
                if(face.attaque!=null) {
                    if (face.attaque.getType()!=0){
                    face.attaque.setNiveau(face.attaque.getNiveau()+1);
                }
                }
                
            }
        }
    }

    public void annuleEffet(){
        if (perso!=null){
            for (Face face : perso.getDe().face){
                if(face.attaque!=null){
                    if (face.attaque.getType()!=0){
                    face.attaque.setNiveau(face.attaque.getNiveau()-1);
                }
                } 
                
            }
        }
    }

    public void getImage(){
        try {
            this.image = ImageIO.read(getClass().getResourceAsStream("../../res/Images/Equipement/GanteletsDePuissance.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
