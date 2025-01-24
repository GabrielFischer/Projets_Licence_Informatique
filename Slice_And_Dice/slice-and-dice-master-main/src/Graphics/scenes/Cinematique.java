package Graphics.scenes;
import java.awt.Graphics2D;
import java.awt.Graphics;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Color;

import main.EtatsJeu;
import main.Main;

public class Cinematique {
    /*
     * Classe qui gère l'affichage de la cinématique lors du lancement d'une nouvelle partie
     */
    Main main;
    public int frame;
    public BufferedImage [] frames = new BufferedImage[10];

    final int scale = 4;

    public Cinematique(Main main){
        this.main = main;
        frame = 0;
        initFrames();
    }

    public void nextFrame(){
        if (frames[frame+1]!=null){
            frame++;
        }
        else{
            frame = 0;
            EtatsJeu.setEtatJeu(EtatsJeu.PLAYING);
            main.getAudio().playBackgroundSound();
            main.getMenu().playCinematique = false;
            main.getRender().initJeu = false;
        }
    }

    public void initFrames(){
        try {
            this.frames[0] = ImageIO.read(getClass().getResourceAsStream("/Images/Cinematique/1.PNG"));
            this.frames[1] = ImageIO.read(getClass().getResourceAsStream("/Images/Cinematique/2.jpg"));
            this.frames[2] = ImageIO.read(getClass().getResourceAsStream("/Images/Cinematique/3.jpg"));
            this.frames[3] = ImageIO.read(getClass().getResourceAsStream("/Images/Cinematique/4.jpg"));
            this.frames[4] = ImageIO.read(getClass().getResourceAsStream("/Images/Cinematique/5.jpg"));
            this.frames[5] = ImageIO.read(getClass().getResourceAsStream("/Images/Cinematique/6.PNG"));
            this.frames[6] = ImageIO.read(getClass().getResourceAsStream("/Images/Cinematique/7.PNG"));
            this.frames[7] = ImageIO.read(getClass().getResourceAsStream("/Images/Cinematique/8.GIF"));
        } catch (Exception e) {
            
        }
        
    }

    public void render(Graphics g){
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.BLACK);
        g2.fillRect(0,0,main.getFrame().getWidth(),main.getFrame().getHeight());
        
        
        int height = 105 * scale;
        int width = 155 * scale;

        int x = (main.getFrame().getWidth() - width) / 2;
        int y = (main.getFrame().getHeight() - height) / 2;
        g2.drawImage(frames[frame], x, y,width,height,null);
    }
}
