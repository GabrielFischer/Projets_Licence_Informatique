package Equipement;

import javax.imageio.ImageIO;

import De.Face;

public class BouclierDivin extends Equipement{

    public BouclierDivin(){
        super("Bouclier divin","Toutes les attaques donnant de l'amure gagnent 1 niveau",8,1);
        getImage();
    }

    public void getImage(){
        try {
            this.image = ImageIO.read(getClass().getResourceAsStream("../../res/Images/Equipement/BouclierDivin.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void effet(){
        if (perso!=null){
            for (Face face : perso.getDe().face){ 
                if(face.attaque!=null){
                   if (face.attaque.getType()==3||face.attaque.getType()==13||face.attaque.getType()==23||face.attaque.getType()==34){
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
                    if ( face.attaque.getType()==3||face.attaque.getType()==13||face.attaque.getType()==23||face.attaque.getType()==34){
                    face.attaque.setNiveau(face.attaque.getNiveau()-1);
                }
                }
                
            }
        }
    }
}
