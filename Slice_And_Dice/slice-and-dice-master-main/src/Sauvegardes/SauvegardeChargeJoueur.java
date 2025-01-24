package Sauvegardes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Constructor;
import java.awt.Color;

import De.De;
import De.Capacite.Capacite;
import De.Capacite.CapaciteSpe;
import Entites.Heros;
import Equipement.Equipement;
import Graphics.scenes.Custom.Custom;
import Graphics.scenes.Custom.CustomHero;
import main.Joueur;
import main.Main;


public class SauvegardeChargeJoueur {
    Main main;
    Color c = new Color(100,123,30);
    public SauvegardeChargeJoueur(Main main){
        this.main = main;
    }

    public void sauvegarde(Joueur j){


        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File("joueur.dot")));
            
            DonneesSauvegardeesJoueur dsj = new DonneesSauvegardeesJoueur();
            
            sauvegardeJoueur(dsj, j);
            sauvegardeAchievements(dsj, j);
            sauvegardeHeroCustom(dsj, j);
            oos.writeObject(dsj);
        
        } catch (Exception e) {
        } 
    }

    public void sauvegardeHeroCustom(DonneesSauvegardeesJoueur dsj, Joueur j){
        for(int i = 0;i<main.getHeroChoix().getListeHeros().size();i++){
            Heros tmp = main.getHeroChoix().getListeHeros().get(i);
            if(tmp instanceof CustomHero){
                CustomHero h =(CustomHero) main.getHeroChoix().getListeHeros().get(i);
                dsj.heroCustomNom.add(h.getNomEntite());
                dsj.heroCustomClasse.add(h.getClasse());
                dsj.heroCustomPdv.add((int)h.getPdv());
                dsj.heroCustomImagePath.add(h.getPath());
                int [] nvDe = new int[6];
                String [] De = new String[6];
                for(int n = 0;n<6;n++){
                    nvDe[n] = h.getDe().getFace(n).getCapacite().getNiveau();
                    De[n] = h.getDe().getFace(n).getCapacite().getClass().getName();
                }
                dsj.heroCustomDe.add(De);
                dsj.heroCustomDeNiveau.add(nvDe);
            }
        }
    }
    public void sauvegardeJoueur(DonneesSauvegardeesJoueur dsj,Joueur j){
        dsj.gold = j.getGold();
        dsj.xp = j.getXp();
        for (int i = 0; i < j.getListeSortsDisponibles().size();i++){
            dsj.listeSortsDisponibles.add(j.getListeSortsDisponibles().get(i).getClass().getName());
        }
        for (int i = 0; i < j.getCapacitesPossedees().size();i++){
            dsj.capacitesPossedees.add(j.getCapacitesPossedees().get(i).getClass().getName());
        }
        for(int i = 0;i<j.getListeHerosDisponibles().size();i++){
            dsj.listeHerosDisponibles.add(j.getListeHerosDisponibles().get(i).getClass().getName());
        }
    }
    public void sauvegardeAchievements(DonneesSauvegardeesJoueur dsj,Joueur j){
        dsj.monstresTués = j.achievement.getMonstresTués();
        dsj.manchesFinies = j.achievement.getManchesFinies();
        dsj.itemsAchetés = j.achievement.getItemsAchetés();
        dsj.attaquesDébloquées = j.achievement.getAttaquesDébloquées();
        dsj.facileDone = j.achievement.isFacileDone();
        dsj.moyenDone = j.achievement.isMoyenDone();
        dsj.difficileDone = j.achievement.isDifficileDone();
        dsj.infernoDone = j.achievement.isInfernoDone();
        dsj.achievementDone = j.achievement.getAchievementDone();
    }


    public Joueur chargeJoueur(){
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File("joueur.dot")));
            DonneesSauvegardeesJoueur dsj = (DonneesSauvegardeesJoueur)ois.readObject();
            
            Joueur j = new Joueur(main);
            chargeJoueur(dsj, j);
            chargeAchievement(dsj, j);
            chargeHeroCustom(dsj,j);
            return j;
             
        } catch (FileNotFoundException e) {
            Joueur j = new Joueur(main);
            return j;
        } catch (Exception e) {
            return null;
        }
    }

    public void chargeHeroCustom(DonneesSauvegardeesJoueur dsj, Joueur j){
        for(int i = 0;i<dsj.heroCustomNom.size();i++){

            Capacite c1 = creerCapacite(dsj.heroCustomDe.get(i)[0], dsj.heroCustomDeNiveau.get(i)[0]);
            Capacite c2 = creerCapacite(dsj.heroCustomDe.get(i)[1], dsj.heroCustomDeNiveau.get(i)[1]);
            Capacite c3 = creerCapacite(dsj.heroCustomDe.get(i)[2], dsj.heroCustomDeNiveau.get(i)[2]);
            Capacite c4 = creerCapacite(dsj.heroCustomDe.get(i)[3], dsj.heroCustomDeNiveau.get(i)[3]);
            Capacite c5 = creerCapacite(dsj.heroCustomDe.get(i)[4], dsj.heroCustomDeNiveau.get(i)[4]);
            Capacite c6 = creerCapacite(dsj.heroCustomDe.get(i)[5], dsj.heroCustomDeNiveau.get(i)[5]);

            De de = new De(c1,c2,c3,c4,c5,c5);
            CustomHero h = new CustomHero(dsj.heroCustomPdv.get(i), de, c, dsj.heroCustomNom.get(i), dsj.heroCustomClasse.get(i), dsj.heroCustomImagePath.get(i));
            j.main.getHeroChoix().getListeHeros().add(h);
        }
    }

    public void chargeJoueur(DonneesSauvegardeesJoueur dsj, Joueur j){
        j.setGold(dsj.gold);
        j.setXp(dsj.xp);
        for(int i = 2; i<dsj.listeSortsDisponibles.size();i++){
            j.getListeSortsDisponibles().add(creerSort(dsj.listeSortsDisponibles.get(i)));
        }
        for(int i = 5;i<dsj.capacitesPossedees.size();i++){
            j.getCapacitesPossedees().add(creerCapacite(dsj.capacitesPossedees.get(i),0));
        }
    }

    public void chargeAchievement(DonneesSauvegardeesJoueur dsj, Joueur j){
        j.achievement.setMonstresTués(dsj.monstresTués);
        j.achievement.setManchesFinies(dsj.manchesFinies);
        j.achievement.setItemsAchetés(dsj.itemsAchetés);
        j.achievement.setAttaquesDébloquées(dsj.attaquesDébloquées);
        j.achievement.setFacile(dsj.facileDone);
        j.achievement.setMoyen(dsj.moyenDone);
        j.achievement.setDifficile(dsj.difficileDone);
        j.achievement.setInferno(dsj.infernoDone);
        j.achievement.setAchievementsDone(dsj.achievementDone);
    }

    public CapaciteSpe creerSort(String capaciteSpe){
        try {
            // Charger dynamiquement la classe en utilisant son nom
            Class<?> clazz = Class.forName(capaciteSpe);
            // Récupérer le constructeur sans paramètres de la classe
            Constructor<?> constructor = clazz.getConstructor();
            // Instancier la classe et la renvoyer
            return (CapaciteSpe) constructor.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Capacite creerCapacite(String capacite,int niveau) { // fonction qui creer une capacit à partir de son nom et de son niveau
        try {
            // Charge dynamiquement la classe en utilisant son nom
            Class<?> clazz = Class.forName(capacite);
            // Récupère le constructeur avec un paramètre de type int
            Constructor<?> constructor = clazz.getConstructor(int.class);
            // Instancie la classe avec le paramètre et la renvoyer
            return (Capacite) constructor.newInstance(niveau);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
