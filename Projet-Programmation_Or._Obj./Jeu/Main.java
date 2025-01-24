package Jeu;

import java.io.IOException;
import java.util.Scanner;

import javax.swing.JFrame;

public class Main{
    public static void main(String[] args) throws IOException {
            /* Boucle qui sert à lancer un gamePanel ou un Terminal */
            boolean flag=false;
            while(!flag){
                Scanner scan = new Scanner(System.in);
                System.out.println("Voulez-vous jouez en mode graphique (g) ou terminal (t)?");
                String reponse = scan.nextLine();
                if (reponse.equals("g")){
                    flag=true;
                    JFrame window = new JFrame();
                    window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    window.setTitle("Legends of Zelda : Tower Defense");
                    window.setResizable(false);

                    GamePanel gamePanel = new GamePanel();
                    window.add(gamePanel);
                    window.pack();

                    window.setLocationRelativeTo(null);
                    window.setVisible(true);
                    gamePanel.gameState = gamePanel.titelState;
                    break;
                }else if(reponse.equals("t")){
                    flag =true;
                    Joueur j = new Joueur();
                    Terminal t= new Terminal(j);
                    System.out.println("test");
                    t.run();
                }else{
                    System.out.println("Veuillez rentrer la lettre 'g' ou 't'");
                }
            }
            /* gamePanel.startGameThread();
            gamePanel.playMusic(0); */

        
    }
}