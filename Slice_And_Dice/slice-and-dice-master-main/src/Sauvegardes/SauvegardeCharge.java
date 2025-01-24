package Sauvegardes;

import java.awt.Graphics2D;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Constructor;

import Entites.Ennemis;
import Entites.Heros;
import Equipement.*;
import main.Jeu;
import Graphics.scenes.MenuMethods;
import Graphics.scenes.Playing;

import Controller.ui.MyButton;

public class SauvegardeCharge extends MenuMethods{
    /* Classe qui permet de sauvegarder et charger un partie */
    Playing p;
    Jeu c;

    MyButton save;
    MyButton load;
    public SauvegardeCharge(Playing p){
        super(p.getMain(),300,750,"SAUVEGARDE");
        this.p = p;
        this.c = p.getCombat();
        initButtons();
    }

    public void initButtons(){
        int width = 140;
        int height = 50;
        int x = p.getBoutique().x + p.getBoutique().width / 2 - width/2;
        int y = p.getBoutique().y + p.getBoutique().height/3;

        save = new MyButton("Sauvegarder", x, y, width, height);
        load = new MyButton("Charger", x, y + 2*height, width, height);
    }

    public void sauvegarde(){


        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File("save.dot")));
            DonneesSauvegardees ds = new DonneesSauvegardees();
            
            sauvegardeInfosPartie(ds);
            sauvegardeHeros(ds);
            sauvegardeInventaireEtBoutique(ds);
            sauvegardeEnnemis(ds);
            save.setText("Sauvegardé");
            oos.writeObject(ds);
        
        } catch (Exception e) {
        } 
    }
    

    public void sauvegardeInfosPartie(DonneesSauvegardees ds){
        ds.niveau = c.genEnnemis.niveau; //sauvegargde le niveau actuel
        ds.nbDesLancesRestants = p.getToolbar().getLancesDes(); //sauvegarde le nb de lancés de dé restants
        ds.argent = p.getJoueur().getMonnaiePartie(); //sauvegarde l'argent de la boutique
        ds.mana = p.getJoueur().getMana(); //sauvegarde le mana
    }
    public void sauvegardeHeros(DonneesSauvegardees ds){
        for (int i = 0;i<5;i++){ // sauvegarde infos héros
            Heros h = p.getListeHeros()[i];
            ds.HerosName[i] = h.getClass().getName();
            ds.niveauHeros[i] = h.getNiveauActuel();
            if(h.getNiveauActuel()==3){
                
                if(h.getNomEntite().equals(h.getMapAmeliorations().get("ameliorationUnUn")[0])){
                    ds.ameliorationChoisie[i] = h.getMapAmeliorations().get("nom")[0];
                    
                }
                else{
                    ds.ameliorationChoisie[i] = h.getMapAmeliorations().get("nom")[1];
                    
                }
            }
            else if (h.getNiveauActuel()==2){
                ds.ameliorationChoisie[i] = h.getNomEntite();
                
            }
            ds.pvHeros[i] = (int) h.getPdv(); //sauvegarde les pdv des héros
            ds.pvMenaces[i] = (int) h.getPdvMenaces();
            ds.faceDuHaut[i] = h.getDe().getIndice(h.getDe().getFaceDessus()); // enregistre les faces du haut des héros
            ds.faceSelectionee[i] = h.aFaceSelectionnee(); //enregistre si le héro a une face selectionée
            ds.used[i] = h.getDe().used;
            for (int j = 0;j<2;j++){
                ds.equipementHero[i][j] = EquipToInt(p.getListeHeros()[i].getInventaire()[j]); //sauvegarde des objets équipés
            }
        }
        
    }
    public void sauvegardeInventaireEtBoutique(DonneesSauvegardees ds){
        for(int i = 0; i<p.getInventaire().equipement.size();i++){
            ds.inventaire.add(p.getInventaire().equipement.get(i).getClass().getName()); // sauvegare l'inventaire
        }
        for(int i = 0 ; i<ds.boutique.length;i++){
            ds.boutique[i] = p.getBoutique().getEquipement(i).getClass().getName(); // sauvegarde le nom des équipements dans la boutique
        }
    }
    public void sauvegardeEnnemis(DonneesSauvegardees ds){
        for (int i = 0; i<p.getListeEnnemis().length;i++){
            if(p.getListeEnnemis()[i]!=null){
                Ennemis e = p.getListeEnnemis()[i];
                ds.ennemi.add(e.getClass().getName());  // enregistre les ennemis
                ds.ennemiType.add(e.getNomEntite());
                ds.pvennemis.add((int)e.getPdv()); // enregistre leur pdv
                ds.faceDuHautEnnemis.add(e.getDe().getIndice(e.getDe().getFaceDessus())); // enregistre leur face selectionnée
                ds.heroCible.add(getHeroIndice(e.getHeroCible())) ;
            }
        }
    }
    public int getHeroIndice(Heros h){
        for(int i =0;i<p.getListeHeros().length;i++){
            if(p.getListeHeros()[i].equals(h)){
                return i;
            }
        }
        return 0;
    }

    public void charge(){
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File("save.dot")));
            DonneesSauvegardees ds = (DonneesSauvegardees)ois.readObject();
            
            chargeInfosPartie(ds);
            chargeHero(ds);
            chargeInventaireEtBoutique(ds);
            chargeEnnemis(ds);

             
        } catch (FileNotFoundException e) {
        } catch (Exception e) {
        }
    }
    public void chargeInfosPartie(DonneesSauvegardees ds){
        c.genEnnemis.niveau = ds.niveau; //charge le nv de la partie
        p.getToolbar().setLancerDes(ds.nbDesLancesRestants); //charge le nb de lances de des restants de la manche
        p.getJoueur().setMonnaiePartie(ds.argent);
        p.getJoueur().setMana(ds.mana);
    }
    public void chargeHero(DonneesSauvegardees ds){
        Heros [] listH = new Heros[5];
        for (int i = 0; i<5;i++){
            listH[i] = creerHeros(ds.HerosName[i]);
        }
        p.setListeHeros(listH);
        p.initBrickHeroes();
        for (int i = 0;i<5;i++){ // sauvegarde infos héros

            Heros h = p.getListeHeros()[i];
            if(ds.niveauHeros[i] >1){
                h.ameliorationAuNiveau2(ds.ameliorationChoisie[i]);
                if (ds.niveauHeros[i] == 3){
                    h.ameliorationAuNiveau3();
                }
            }
            
            p.getCombat().remplacementSortLigneeGuerisseur(h.getNomEntite());
            p.getCombat().remplacementSortLigneeMage(h.getNomEntite());

            h.setPdv(ds.pvHeros[i]); // donne les pdv enregistrés aux héros
            h.setPdvMenaces(ds.pvMenaces[i]);
            h.getDe().setFaceDessus(ds.faceDuHaut[i]); // attribue les faces du haut aux héros
            h.getDe().used = ds.used[i];

            if(ds.faceSelectionee[i] && h.getDe().getFaceDessus()!=null){ //attribue les faces selectionées aux héros
                h.setFaceSelectionnee(h.getDe().getFaceDessus());             
            }
            
            for (int j = 0;j<2;j++){               
                if(ds.equipementHero[i][j]!=0){                    
                    p.getListeHeros()[i].equipe(IntToEquip(ds.equipementHero[i][j]), j);                    
                }
            }
        }
    }
    public void chargeInventaireEtBoutique(DonneesSauvegardees ds){
        for(int i = 0; i<ds.inventaire.size();i++){
            p.getInventaire().equipement.add(creerEquipement(ds.inventaire.get(i)));
        }
        for(int i = 0; i<ds.boutique.length;i++){
            Equipement e = creerEquipement(ds.boutique[i]);
            p.getBoutique().setEquipement(i, e);
        }
    }
    public void chargeEnnemis(DonneesSauvegardees ds){
        Ennemis[] e = new Ennemis [ds.ennemi.size()]; // creer une nouvelle liste ennemis
        
        for (int i = 0; i<ds.ennemi.size();i++){
            e[i] = creerEnnemi(ds.ennemi.get(i),ds.ennemiType.get(i));
            e[i].setPdv(ds.pvennemis.get(i));
            e[i].getDe().setFaceDessus(ds.faceDuHautEnnemis.get(i));
            e[i].setFaceSelectionnee(e[i].getDe().getFaceDessus());
            e[i].setHeroCible(p.getListeHeros()[ds.heroCible.get(i)]);
        }
        p.setListeEnnemis(e); // actualise la nouvelle liste ennemis
        p.initBrickEnnemis();

    }
    public Ennemis creerEnnemi(String nomClasse,String nomEnnemi) {
        try {
            // Charger dynamiquement la classe en utilisant son nom
            Class<?> clazz = Class.forName(nomClasse);
            // Récupérer le constructeur avec un paramètre de type String
            Constructor<?> constructor = clazz.getConstructor(String.class);
            // Instancier la classe avec le paramètre et la renvoyer
            return (Ennemis) constructor.newInstance(nomEnnemi);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public Equipement creerEquipement(String equipement){
        try {
            // Charger dynamiquement la classe en utilisant son nom
            Class<?> clazz = Class.forName(equipement);
            // Récupérer le constructeur sans paramètres de la classe
            Constructor<?> constructor = clazz.getConstructor();
            // Instancier la classe et la renvoyer
            return (Equipement) constructor.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Heros creerHeros(String heros){
        try {
            // Charger dynamiquement la classe en utilisant son nom
            Class<?> clazz = Class.forName(heros);
            // Récupérer le constructeur sans paramètres de la classe
            Constructor<?> constructor = clazz.getConstructor();
            // Instancier la classe et la renvoyer
            return (Heros) constructor.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public int EquipToInt(Equipement e){ //convertit un equipement en int pour le sauvegarder
        if(e == null){
            return 0;
        }
        switch (e.nom) {
            case "Bouclier divin":
                return 1;
            case "Faucon":
                return 2;
            case "Force Gauche":
                return 3;
            case "Gantelets de puissance":
                return 4;
            case "Pansements":
                return 5;
            case "Petit Coeur":
                return 6;
            case "Ricochets":
                return 7;
            case "Rivière de mana":
                return 8;
            default:
                return 0;
        }
    }

    public Equipement IntToEquip(int i){
        if(i>0){
            switch (i) {
                case 1:
                    return new BouclierDivin();
                case 2:
                    return new Faucon();
                case 3:
                    return new ForceGauche();
                case 4:
                    return new GanteletsDePuissance();
                case 5:
                    return new Pansements();
                case 6:
                    return new PetitCoeur();
                case 7:
                    return new Ricochets();
                case 8:
                    return new RiviereDeMana();
                default:
                    return null;
            }
        }
        return null;
    }

    public void draw(Graphics2D g2){
        super.draw(g2);
        save.draw(g2);
        load.draw(g2);
    }

    public void mouseClicked(int x, int y){
        super.mouseClicked(x, y);
        if(save.getBounds().contains(x,y)){
            p.getMain().getAudio().playMouseclickSound();
            sauvegarde();
        }
        else if(load.getBounds().contains(x,y)){
            p.getMain().getAudio().playMouseclickSound();
            charge();
            
        }
        else{
            save.setText("Sauvegarder");
        }
    }
}
