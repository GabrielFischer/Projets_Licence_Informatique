package main;

import javax.swing.JFrame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


import Sauvegardes.SauvegardeChargeJoueur;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.lang.Runnable;

import Controller.helpers.AudioPlayer;
import Graphics.scenes.Menu;
import Graphics.scenes.Custom.Custom;
import Graphics.GamePanel;
import Graphics.Render;
import Graphics.scenes.HeroChoix;
import Graphics.scenes.Cinematique;
import Graphics.scenes.Playing;

public class Main extends JFrame implements Runnable{
    private GamePanel gamePanel;
    private Point location;
    private int x,y;
    private Thread gameThread;
    private AudioPlayer audio;
    private final double UPS_SET = 30.0;
    private final double FPS_SET = 60.0;

    //CLASSES
    private Menu menu;
    private Render render;
    private Playing playing;
    private Custom custom;
    private HeroChoix heroChoix;
    private Joueur joueur;
    private SauvegardeChargeJoueur sc;
    private Cinematique cinematique;

    public Main(){
        initClasses();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setTitle("Slice & Dice");
        add(gamePanel);
        pack();
        setVisible(true);
        location = getLocation();   //permet de garder en mémoire la location première de la fenêtre. --> sert dans Custom
        x = (int) location.getX();
        y = (int) location.getY();
        addComponentListener(new ComponentAdapter() {   //cette portion de code permet d'update à tout moment la position de la fenêtre.
            @Override
            public void componentMoved(ComponentEvent e) {
                super.componentMoved(e);
                Point newLocation = getLocation();
                x = (int) newLocation.getX();
                y = (int) newLocation.getY();
            }
        });
    
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // Appeler la méthode pour sauvegarder les données du joueur
                sauvegarderJoueur();
            }
        });
    }
    
    public void sauvegarderJoueur(){
        try {
            sc.sauvegarde(joueur);
        } catch (Exception  e) {
            System.err.println("Erreur lors de la sauvegarde du joueur : " + e.getMessage());
            e.printStackTrace();
           
        }
    }


    private void initClasses() {
        this.audio = new AudioPlayer();
        this.heroChoix = new HeroChoix(this);
        this.cinematique = new Cinematique(this);
        this.render = new Render(this);
        this.gamePanel = new GamePanel(this);
        this.custom = new Custom(this);
        this.sc = new SauvegardeChargeJoueur(this);
        
        //initialisation joueur
        String cheminFichier = "joueur.dot";
        File fichier = new File(cheminFichier);
        if (fichier.exists()) {
            this.joueur = sc.chargeJoueur();
        } else {
            this.joueur = new Joueur(this);
        }

        if (this.joueur == null){
            this.joueur = new Joueur(this);
        }

        this.menu = new Menu(this);
        
    }

    private void start(){
        gameThread = new Thread(this);
        gameThread.start();
    }
    @Override
    public void run(){
        double timePerFrame = 1000000000.0 / FPS_SET;
        double timePerUpdate = 1000000000.0 / UPS_SET;
        long lastFrame = System.nanoTime();
        long lastUpdate = System.nanoTime();
        long lastTimeCheck = System.currentTimeMillis();

        int frames = 0;
        int updates = 0;

        long now;

        while(true) {
            now = System.nanoTime();
            if(now - lastFrame >= timePerFrame) {
                repaint();
                lastFrame = now;
                frames++;
            }
            if(System.currentTimeMillis() - lastTimeCheck >= 1000){
                frames = 0;
                updates = 0;
                lastTimeCheck = System.currentTimeMillis();
            }
        }
    }



    public static void main(String[] args) {
        Main main = new Main();
        main.gamePanel.initInputs();
        main.start();
    }

    public Menu getMenu(){
        return this.menu;
    }

    public Render getRender(){
        return this.render;
    }

    public Playing getPlaying() {
        return this.playing;
    }

    public Component getFrame() {
        return this.gamePanel;
    }

    public Joueur getJoueur(){
        return this.joueur;
    }

    public AudioPlayer getAudio(){return this.audio;}

    public Custom getCustom() {
        return this.custom;
    }
    public HeroChoix getHeroChoix(){
        return this.heroChoix;
    }
    public Cinematique getCinematique(){
        return this.cinematique;
    }

    public int getX(){
        return x;
    }
    public int getY(){
        return y;
    }
}
