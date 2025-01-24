package Entites;
import De.*;

import java.awt.Color;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class Ennemis extends Entites {
    double pdv;
    private Heros heroCible;
    private String nomImage;

    public Ennemis(double pdv, De de, Color couleur, String nom){
        super(pdv,de,couleur,nom);
        if(nom!=null){
            this.nomImage=nom.replace(' ','_');
        }
        
        
    }

    public int monnaieLachee(){
        return 6;
    }

    public Heros getHeroCible(){
        return heroCible;
    }

    public void setHeroCible(Heros h){
        heroCible=h;
    }
    public String getNomImage(){
        return nomImage;
    }
    public void setNomImage(String s){
        nomImage=s;
    }
}