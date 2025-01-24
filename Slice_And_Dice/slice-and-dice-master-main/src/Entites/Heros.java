package Entites;

import Equipement.Equipement;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

import De.Capacite.Capacite;
import De.De;
import Controller.ui.BrickHero;

public abstract class Heros extends Entites {
    private Equipement[] inventaireHeros = new Equipement[2];
    private ArrayList<Capacite> attaqueJoueur; //j'ai besoin de savoir quelle classe pour les attaques que possède un perso pour le donner au Joueur
    private int niveauActuel = 1;
    private boolean ricochets;

    private BrickHero brickHero;
    private HashMap<String, String[]> mapAmeliorations = new HashMap<String, String[]>();
    private HashMap<String, De> mapDe = new HashMap<String, De>();


    private De deAmeliorationUnUn, deAmeliorationUnDeux, deAmeliroationDeuxUn, deAmeliorationDeuxDeux;

    public Heros(double pdv, De de, Color couleur, String nom){
        super(pdv, de, couleur, nom);
        this.setHauteur(100); // à remplacer par une valeur commune de hauteur de dé
    }



    public Heros(double pdv, De de, Color couleur, String nom, String ameliorationUnUn, String ameliorationUnDeux, String ameliorationDeuxUn, String ameliorationDeuxDeux, De[] desAmelioration){
        super(pdv, de, couleur, nom);
        this.setHauteur(100); // à remplacer par une valeur commune de hauteur de de
        deAmeliorationUnUn = desAmelioration[0];
        deAmeliorationUnDeux = desAmelioration[1];
        deAmeliroationDeuxUn = desAmelioration[2];
        deAmeliorationDeuxDeux = desAmelioration[3];
        mapAmeliorations.put(nom, new String[]{ameliorationUnUn, ameliorationUnDeux});
        mapAmeliorations.put(ameliorationUnUn, new String[]{ameliorationDeuxUn});
        mapAmeliorations.put(ameliorationUnDeux, new String[]{ameliorationDeuxDeux});
        mapDe.put(ameliorationUnUn, deAmeliorationUnUn);
        mapDe.put(ameliorationUnDeux, deAmeliorationUnDeux);
        mapDe.put(ameliorationDeuxUn, deAmeliroationDeuxUn);
        mapDe.put(ameliorationDeuxDeux, deAmeliorationDeuxDeux);
    }

    public void drawPatron(Graphics2D g2,int x, int y, int size){
        super.drawPatron(g2, x, y, size);
        if(inventaireHeros[0]!=null){
            inventaireHeros[0].draw(g2, x, y+size, size);
        }
        if(inventaireHeros[1]!=null){
            inventaireHeros[1].draw(g2, x, y-size, size);
        }
    }

    public void ameliorationAuNiveau2(String ameliorationChoisie){}/*le paramètre amelioraationChoisie correspond au nom du héros en quoi cette istance de héros va être amélioré
                                                                    par exemple si le jeu propose d'améliorer un Archer, et que le joueur clique sur Chasseur, alors ici le paramètre doit contenir le String "Chasseur"*/
    public void ameliorationAuNiveau3() {
    }

    public Equipement[] getInventaire (){
        return inventaireHeros;
    }

    public void equipe(Equipement equip, int pos){
        if (inventaireHeros[pos]==null){
            inventaireHeros[pos]=equip;
            equip.perso = this;
            equip.effet();
        }else{
            //System.out.println("Cet emplacement n'est pas disponible.");
        }
    }

    public void desequipe(int pos){
        if (inventaireHeros[pos]!=null){
            inventaireHeros[pos].annuleEffet();
            inventaireHeros[pos].perso=null;
            inventaireHeros[pos]=null;
        }else{
            //System.out.println("impossible de déséquiper cet objet.");
        }
    }

    public boolean hasRicochets(){
        return ricochets;
    }
    public void setRicochets(boolean b){
        ricochets=b;
    }

    public void incrementationNiveauActuel()//fonction appelée lorsqu'un héros est amélioré
    {
        this.niveauActuel++;
    }
    public void decrementationNiveauActuel()//fonction appelée lorsque l'amélioration d'un héros est annuléé
    {
        this.niveauActuel--;
    }
    public void setNiveau(int nv){
        this.niveauActuel = nv;
    }


   
    public int getNiveauActuel() {
        return this.niveauActuel;
    }

    public BrickHero getBrickHero() {
        return brickHero;
    }

    public void setBrickHero(BrickHero brickHero) {
        this.brickHero = brickHero;
    }

    public HashMap<String, String[]> getMapAmeliorations() {
        return mapAmeliorations;
    }
    public HashMap<String, De> getMapDe() { return mapDe; }

    public De getDeAmeliorationUnUn() {
        return deAmeliorationUnUn;
    }

    public De getDeAmeliorationUnDeux() {
        return deAmeliorationUnDeux;
    }

    public De getDeAmeliorationDeuxDeux() {
        return deAmeliorationDeuxDeux;
    }
    public De getDeAmeliorationDeuxUn()
    {
        return deAmeliroationDeuxUn;
    }

    public void setDeAmeliorationDeuxDeux(De deAmeliorationDeuxDeux) {
        this.deAmeliorationDeuxDeux = deAmeliorationDeuxDeux;
    }

    public void setDeAmeliorationUnDeux(De deAmeliorationUnDeux) {
        this.deAmeliorationUnDeux = deAmeliorationUnDeux;
    }

    public void setDeAmeliorationUnUn(De deAmeliorationUnUn) {
        this.deAmeliorationUnUn = deAmeliorationUnUn;
    }

    public void setDeAmeliorationDeuxUn(De deAmeliroationDeuxUn) {
        this.deAmeliroationDeuxUn = deAmeliroationDeuxUn;
    }

}

