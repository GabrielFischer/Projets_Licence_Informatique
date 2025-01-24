package Jeu;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.*;

import Personnages.Ennemis.Moblin;
//import Tours.Base;

public class GamePanel extends JPanel implements Runnable{
    //Paramètres de l'écran

    final int originalTilesize = 16; // 16*16 tile
    final int scale = 3;

    public final int tileSize = originalTilesize * scale;
    public final int maxScreenCol = 16; //Longeur = 16*16*3 = 768
    public final int maxScreenRow = 12; //Largeur = 12*16*3 = 576
    public final int screenWidth = tileSize * maxScreenCol;
    public final int screenHeight = tileSize * maxScreenRow;


    //FPS
    int FPS = 30;

    KeyHandler key = new KeyHandler(this);
    public Ui ui = new Ui(this);
    public boolean choixNiveau = false;
    

    //Etats jeu
    public int gameState;
    public final int titelState = 0;
    public final int fileSelectState = 1;
    public final int playState = 2;
    public final int pauseState = 3;
    public final int terminalState = -1;

    BufferedImage [] background = new BufferedImage[30];
    BufferedImage [] titelScreenImage = new BufferedImage[30];

    Sound sound = new Sound();
    Thread gameThread;

    Plateau p = new Plateau(this, 0);
    Niveau niveau = new Niveau(this);
    public int nbNiveaux = 1;
    public int nvSelect;

    public GamePanel() throws IOException{

       
        
        this.setPreferredSize(new Dimension(screenWidth,screenHeight)); //defini la taille de l'ecran de jeu
        this.setBackground(Color.LIGHT_GRAY);
        this.setDoubleBuffered(true);
        this.addKeyListener(key); //permet de reconnaitre les entree de touches
        this.setFocusable(true);
        //playMusic(0);

        background[0]=ImageIO.read(getClass().getResourceAsStream("../Images/Title/Title_bg.png"));
        background[1]=ImageIO.read(getClass().getResourceAsStream("../Images/Title/fileSelection_bg.png"));
        titelScreenImage[0] = ImageIO.read(getClass().getResourceAsStream("../Images/Title/TheLegendOfZeldaTD.png"));
        titelScreenImage[1] = ImageIO.read(getClass().getResourceAsStream("../Images/Title/PressStart.png"));

        this.startGameThread();
        playMusic(0);
                

    }

    public void startGameThread(){
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void run(){
        double drawInterval = 1000000000/FPS; //temps entre lequel les frames sont dessinées et mises à jour
        double nextDrawTime = System.nanoTime() + drawInterval; // lorsque ce temps est atteint le programme doit redessiner et actualiser la frame
        long timer = 0;
        int drawCount = 0;
        long lastTime = System.nanoTime();

        while(gameThread!=null){
            
            //Met à jour la map
            update();
         
            drawCount++;
            //Redessine la map
            repaint();

            if(gameState == terminalState){
                try {
                    Thread.sleep((long) 1000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            else{
                try {
                    long currentTime = System.nanoTime();
                    double remainingTime = nextDrawTime - System.nanoTime(); // temps restant avant de devoir redessiner la frame     
                    remainingTime = remainingTime/1000000;
                    
                    if (remainingTime<0){
                        remainingTime = 0;
                    }

                    Thread.sleep((long)(remainingTime));

                    nextDrawTime += drawInterval;
                    timer += (currentTime - lastTime);
                    lastTime = currentTime;

                    if(timer >= 1000000000){
                        System.out.println("FPS: "+drawCount);
                        drawCount=0;
                        timer = 0;
                    }
                } catch (Exception e) {
                    // TODO: handle exception
                }
            }

            
            

        }
    }

    public void update(){
        
        if (gameState == titelState && key.spacePressed == true){
            stopMusic();
            playLoop(1);                
            gameState = 1;
            key.spacePressed = false;
        }

        if(niveau!=null){
            if(niveau.isRunning==false){
                gameState = fileSelectState; 
            }
        }
        
        
    }
    
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        
        if(gameState == terminalState){
            p.afficher();
        }
        
        

        
        if (gameState == titelState){
            // Draw background image
            g2.drawImage(background[0], 0, 0, getWidth(), getHeight(), this); 
            // Calculate new dimensions
            int newWidth = titelScreenImage[0].getWidth() * 3;
            int newHeight = titelScreenImage[0].getHeight() * 3;

            // Calculate center position
            int x = (getWidth() - newWidth) / 2;
            int y = 100;
            g2.drawImage(titelScreenImage[0],x,y,newWidth,newHeight,null);
            
            
            // Calculate new dimensions
            newWidth = titelScreenImage[1].getWidth() * 3;
            newHeight = titelScreenImage[1].getHeight() * 3;

            // Calculate center position
            x = (getWidth() - newWidth) / 2;
            y = 400;
            g2.drawImage(titelScreenImage[1],x,y,newWidth,newHeight,null);        
        }
        else if (gameState == fileSelectState){
            g2.drawImage(background[1], 0, 0, getWidth(), getHeight(), this);
            ui.draw(g2);
        }
        if(gameState == playState){
            //p.draw(g2);
            niveau.draw(g2);
            ui.draw(g2);
            //niveau.p.afficher();
            //System.out.println();
        }
    }

    public void playLoop(int i){
        sound.setFile(i);
        sound.play();
        sound.loop();
    }
    public void playMusic(int i){
        sound.setFile(i);
        sound.play();
    }
    public void stopMusic(){
        sound.stop();
    }

}