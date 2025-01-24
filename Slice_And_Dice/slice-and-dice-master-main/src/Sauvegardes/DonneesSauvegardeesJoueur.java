package Sauvegardes;

import java.io.Serializable;
import java.util.ArrayList;

import Entites.Heros;

public class DonneesSauvegardeesJoueur implements Serializable{

    int gold;
    double xp;
    ArrayList<String> listeHerosDisponibles = new ArrayList<>();
    ArrayList<String> listeSortsDisponibles = new ArrayList<String>();
    ArrayList<String> capacitesPossedees = new ArrayList<String>();

    int monstresTués;
    int manchesFinies;
    int itemsAchetés;
    int attaquesDébloquées;
    boolean facileDone;
    boolean moyenDone;
    boolean difficileDone;
    boolean infernoDone;
    boolean[] achievementDone = new boolean[10];

    ArrayList<String[]> heroCustomDe = new ArrayList<String[]>();
    ArrayList<int[]> heroCustomDeNiveau = new ArrayList<int[]>();
    ArrayList<Integer> heroCustomPdv = new ArrayList<Integer>();
    ArrayList<String> heroCustomNom = new ArrayList<String>();
    ArrayList<String> heroCustomClasse = new ArrayList<String>();
    ArrayList<String> heroCustomImagePath = new ArrayList<String>();
}
