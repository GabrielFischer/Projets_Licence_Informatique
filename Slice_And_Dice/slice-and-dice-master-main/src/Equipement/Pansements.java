package Equipement;

import javax.imageio.ImageIO;

import De.Face;

public class Pansements extends Equipement{
    
    public Pansements(){
        super("Pansements","Toutes les attaques de soin gagnent 1 niveau",8,1);
        getImage();
    }
    public void getImage(){
        try {
            this.image = ImageIO.read(getClass().getResourceAsStream("../../res/Images/Equipement/Pansements.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void effet(){
        if (perso!=null){
            for (Face face : perso.getDe().face){ 
                if(face.attaque!=null){                
                    if (face.attaque.getType()==2||face.attaque.getType()==12||face.attaque.getType()==23||face.attaque.getType()==24){
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
                    if (face.attaque.getType()==2||face.attaque.getType()==12||face.attaque.getType()==23||face.attaque.getType()==24){
                        face.attaque.setNiveau(face.attaque.getNiveau()-1);
                    }
                }
                
            }
        }
    }
}
