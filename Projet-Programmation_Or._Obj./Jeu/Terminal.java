package Jeu;

import java.util.Scanner;

public class Terminal extends Thread{
    Joueur j;
    Niveau niveau;
    int numNv;
    private static volatile boolean enJeu = true;

    Terminal(Joueur j){
        niveau = new Niveau();
        this.j=j;
        numNv=1;
    }

    // public void setupGameTerminal(){
    //     System.out.println("test");
    //     this.niveau.run();
    // }

    public void run() {

        //lance les threads 3 et 4
        this.niveau.run();

        // demarre le thread de l'entrée utilisateur
        //Pour l'instant que un test pour fermer lorsque game over, mais servira à +
        /* 
        Thread inputThread = new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            while (enJeu) {
                if(!this.niveau.isRunning){
                    boolean flag = false;
                    while(!flag){
                        System.out.println("Ecrivez 'stop' pour arrêter le jeu");
                        String reponse = scanner.nextLine();
                        if (reponse.equals("stop")){
                            flag =true;
                            enJeu = false;
                            break;
                        }
                    }
                }
                
            }
            scanner.close();
        });
        inputThread.start();*/
/* 
        try {
            //inputThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Le jeu est terminé.");*/
    }
}
