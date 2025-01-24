package Entites;

import De.De;
import De.Face;

import java.awt.Color;
import java.awt.Graphics2D;

public abstract class Entites{
    private String nomEntite;
    private De de; //Dé de l'entité
    private double pdv; //pdv de l'entité
    private double pdvMax; //pdv max de l'entité
    private double pdvMenaces; //pdv menacés par une attaque
    private Color couleur; //couleur du panel de l'entité
    private int hauteur;  //hauteur du Dé/largeur/longueur
    private Face faceSelectionnee; // Face selectionnée lors d'une partie
    private int bouclier; //Valeur du bouclier
    private boolean isFront;
    private int estEmpoisonne;//contient le nombre de tour durant lequel l'entité est empoisonnée, ainsi si il est égal à 0, alors l'entité n'est pas/plus empoisonnée. Si il est supérieur à 0, alors l'entité est empoisonnée pendant encore la valeur de cette variable tour(s)

    

    public Entites(double pdv, De de, Color couleur, String nomEntite)
    {
        this.pdvMax=pdv;
        this.pdv=pdv;
        this.de=de;
        this.couleur=couleur;
        this.nomEntite=nomEntite;
        this.faceSelectionnee = null;
        isFront = false;
        this.estEmpoisonne=0;
    }

    public void drawPatron(Graphics2D g2,int x, int y, int width){
        int size = width/3;
        
        for(int i = 0; i < 4; i++){
            g2.setColor(this.couleur);
            
            this.de.getFace(i).draw(g2, x + size*i, y + size, size);
            g2.drawRect(x + size*i, y+size, size, size);
        }
        //to draw the upper one
        
        this.de.getFace(4).draw(g2, x + size*2, y, size);
        g2.drawRect(x + 2*size, y, size, size);

        //to draw the lower one
        
        this.de.getFace(5).draw(g2, x + size*2 , y+ 2 *size , size);
        g2.drawRect(x + 2*size, y+2*size, size, size);
    
}

//getters
    public double getPdv(){
        return this.pdv;
    }
    public double getPdvMax(){
        return this.pdvMax;
    }
    public double getPdvMenaces(){
        return this.pdvMenaces;
    }
    public De getDe(){
        return this.de;
    }
    public Color getColor(){
        return this.couleur;
    }
    public int getHauteur(){
        return this.hauteur;
    }
    public String getNomEntite()
    {
        return this.nomEntite;
    }
    public Face getFaceSelectionnee(){
        return this.faceSelectionnee;
    }
    public int getBouclier(){
        return this.bouclier;
    }

    public boolean getIsFront(){
        return isFront;
    }

//setters
    public void setHauteur(int hauteur){
        this.hauteur=hauteur;
    }
    public void setDe(De de){
        this.de=de;
    }
    public void setPdv(double pdv){
        this.pdv=pdv;
    }
    public void setPdvMax(double pdvMax){
        this.pdvMax = pdvMax;
    }
    public void setPdvMenaces(double pdvMenaces){
        this.pdvMenaces = pdvMenaces;
    }
    public void setColor(Color color){
        this.couleur=color;
    }

    public void setNomEntite(String nomEntite) {
        this.nomEntite = nomEntite;
    }
    public void setFaceSelectionnee(Face f){
        this.faceSelectionnee = f;
    }
    public void setBouclier(int n){
        this.bouclier = n;
    }

    public boolean enVie(){
        return this.pdv>0;
    }
    public boolean aFaceSelectionnee(){
        return this.faceSelectionnee!=null;
    }

    public void setHimFront(){// met l'entité devant
        isFront = true;
    }
    public void setHimBack(){  //met l'entité derrière
        isFront = false;
    }

    public void setEstEmpoisonne(int estEmpoisonne) {
        this.estEmpoisonne = estEmpoisonne;
    }

    public int getEstEmpoisonne() {
        return estEmpoisonne;
    }
}