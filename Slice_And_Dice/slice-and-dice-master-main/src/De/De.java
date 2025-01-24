package De;

import De.Capacite.Capacite;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class De {
    /* BUT:
    *  Classe des De, gère les lancés de des.
    */
    public ArrayList<Face> face;
    private Face faceDessus;
    public boolean selected;
    public boolean used;

    public De (Capacite a1, Capacite a2, Capacite a3, Capacite a4, Capacite a5, Capacite a6) {
        face = new ArrayList<Face>();
        face.add(new Face(a1));
        face.add(new Face(a2));
        face.add(new Face(a3));
        face.add(new Face(a4));
        face.add(new Face(a5));
        face.add(new Face(a6));

        this.faceDessus=face.get(0);
        selected=false;
        used=false;
    }

    public void setFace(int i, Capacite a){
        face.set(i, new Face(a));
    }
    public Face getFaceDessus(){
        return faceDessus;
    }
    public void setFaceDessus(int i){
        this.faceDessus = face.get(i);
    }

    public Face lancerDes(){
        Random random = new Random();
        int i = random.nextInt(6);
        this.faceDessus = face.get(i);
        return face.get(i);
    }

    public void draw(Graphics2D g2,int x, int y,int size){
        faceDessus.draw(g2, x, y,size);
    }
    

    public Face getFace(int i) {
        return face.get(i);
    }

    public Face aFaceDessus(){
        return this.faceDessus;
    }

    public int getIndice(Face faceRecherchee) {
        // Parcourir la liste pour trouver l'indice de l'épée spécifique
        for (int i = 0; i < face.size(); i++) {
            if (face.get(i).equals(faceRecherchee)) {
                return i; // Retourner l'indice si l'épée spécifique est trouvée
            }
        }
        return -1; // Retourner -1 si l'épée spécifique n'est pas trouvée dans la liste
    }
}
