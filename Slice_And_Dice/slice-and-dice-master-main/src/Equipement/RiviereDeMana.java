package Equipement;

import De.Face;

public class RiviereDeMana extends Equipement{
    
    public RiviereDeMana(){
        super("Rivière de mana","Toutes les attaques donnant de la mana gagnent 1 niveau",8,1);
    }

    public void effet(){
        if (perso!=null){
            for (Face face : perso.getDe().face){ 
                if(face.attaque!=null){
                  if (face.attaque.getType()==4||face.attaque.getType()==14||face.attaque.getType()==24||face.attaque.getType()==34){
                    face.attaque.setNiveau(face.attaque.getNiveau()+1);
                }  
                }
                
            }
        }
    }

    public void annuleEffet(){
        if (perso!=null){
            for (Face face : perso.getDe().face){ 
                if (face.attaque!=null){
                    if (face.attaque.getType()==4||face.attaque.getType()==14||face.attaque.getType()==24||face.attaque.getType()==34){
                    face.attaque.setNiveau(face.attaque.getNiveau()-1);
                }
                }
                
            }
        }
    }
}
