package Graphics.scenes.Custom;

import Controller.ui.Custom.*;
import De.*;
import Entites.Heros;

import java.awt.*;


//CustomHero, classe fille de Heros, sert à créer le héros ainsi customisé, afin de le stocker dans la liste des héros à choisir pour pouvoir l'utiliser dans le jeu.

public class CustomHero extends Heros{  //classe modèle pour la custom du héros
    private Custom custom;
    private CustomHomePage customHomePage;
    private CustomHeroPanel heropanel;
    private int compteurCustom = 0;

    private double pdv;
    private String nom, classe, imagePath;
    private De de;
    private Color couleur;

    public CustomHero(double pdv, De de, Color couleur, String nom, String classe, String imagePath){
        super(pdv, de, couleur, nom);
        this.classe = classe;
        this.imagePath = imagePath;
        
    }

    //getters et setters

    public void setName(String name){
        this.nom = name;
    }
    public void setClasse(String classeNew){
        this.classe = classeNew;
    }
    public void setPath(String newPath){
        this.imagePath = newPath;
    }


    public String getName(){
        return nom;
    }
    public String getClasse(){
        return classe;
    }
    public String getPath(){
        return imagePath;
    }

}
