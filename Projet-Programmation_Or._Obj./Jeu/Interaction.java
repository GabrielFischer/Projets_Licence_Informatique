package Jeu;

import java.util.Scanner;

public class Interaction{ //CLASSE NON UTILISEE
    Scanner comm;
    public Interaction(){

    }

    public void menu(){
        System.out.println("1. Campagne");
        System.out.println("2. Marathon");
        System.out.println("3. Jouer en mode Graphisme");
        comm = new Scanner(System.in);
        int n = comm.nextInt();
        if (n == 1){
            campagne();
        }
    }
    public void campagne(){

    }
}