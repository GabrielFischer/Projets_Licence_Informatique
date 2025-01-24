package Graphics.scenes;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import main.EtatsJeu;
import main.Main;

public class MenuMethods {
    /* cette classe gère l'affichage commun de toutes les boutique/menus du jeu */

    String nom;

    Main main;

    public int x;
    public int y;
    public int height;
    public int width;
    public int barHeight;

    public int WINDOWHEIGHT;
    public int WINDOWWIDTH;


    final Color fillColor = new Color(0,23,30);
    final Color borderColor = new Color(107,111,127);
    final Color TextColor = new Color(255,252,240);

    public Rectangle close; 

    public Image[] icons = new Image[5];

    float strokeWidth = 3.0f; // Épaisseur du trait en pixels
    BasicStroke stroke = new BasicStroke(strokeWidth);
    AlphaComposite alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f); //opacité du remplissage (transparant)
    AlphaComposite opaque = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f); //opacité du remplissage (opaque)

    public MenuMethods(Main main, int height, int width,String nom){
        this.main = main;
        this.width = width;
        this.height = height;
        this.barHeight = height/8;
        this.nom = nom;

        initIcons();
        initXY();
    }

    public void initIcons(){
        try {
            this.icons[0] = ImageIO.read(getClass().getResourceAsStream("/Images/Icons/croixRouge.png")); // or /Images/Icons/croixRouge.png
            this.icons[1] =  ImageIO.read(getClass().getResourceAsStream("/Images/Icons/argent.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }

    public void initXY(){
        //System.out.println(main.getFrame().getHeight());
        WINDOWHEIGHT = main.getFrame().getHeight();
        WINDOWWIDTH = main.getFrame().getWidth();
        x = (WINDOWWIDTH - width) / 2; // Coordonnée x du coin supérieur gauche du rectangle
        y = (WINDOWHEIGHT - height) / 2; 
        close = new Rectangle(x + (width - barHeight), y, barHeight, barHeight);

    }

    public void draw(Graphics2D g2){
        drawMainRect(g2);
        drawText(g2);
    }

    public void drawText(Graphics2D g2){
        g2.setColor(TextColor);
        
        int length = (int)g2.getFontMetrics().getStringBounds(this.nom,g2).getWidth();
        g2.drawString(this.nom, x + width / 2 - length/2, y + barHeight /2);
    }

    
    public int centerRectangleX(int totalWidth, int rectangleWidth) {
        return (totalWidth - rectangleWidth) / 2;
    }

    public void drawMainRect(Graphics2D g2){ // dessine le rectangle principal en incluant la barre du haut avec l'icône de la croix

        //dessine le filtre qui assoombri le fond
        g2.setComposite(alphaComposite);
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, WINDOWWIDTH, WINDOWHEIGHT);

        //Rectangle principal
        //Dessine le fond du rectangle
        g2.setComposite(opaque);
        g2.setColor(fillColor);
        g2.fillRect(x,y, width, height);

        //dessine la bordure du rectangle
        g2.setColor(borderColor);
        g2.setStroke(stroke);
        g2.drawRect(x, y, width, height);

        //dessine la barre du haut
        g2.drawRect(x, y, width, barHeight);

        //dessine l'icone de la croix pour fermer la boutique
        if(icons[0]!=null){
            g2.drawImage(icons[0], x + (width - barHeight), y,barHeight,barHeight, null);
        }
        if(close!=null){
            g2.draw(close);
        }
        
        
    }
    public void mouseClicked(int x, int y){
        if (close.getBounds().contains(x, y)) {
            main.getAudio().playMouseclickSound();
            if(EtatsJeu.etatJeu == EtatsJeu.BOUTIQUEMENU || EtatsJeu.etatJeu == EtatsJeu.ACHIEVEMENT || EtatsJeu.etatJeu == EtatsJeu.GUIDEMENU  || EtatsJeu.etatJeu == EtatsJeu.SETTINGSMENU){
                EtatsJeu.setEtatJeu(EtatsJeu.MENU);
            }
            else{
                EtatsJeu.setEtatJeu(EtatsJeu.PLAYING);
            }
            
        }
    }

    public Color getFillColor(){
        return this.fillColor;
    }
    public Color getTextColor(){
        return this.TextColor;
    }
    public Color getborderColor(){
        return this.borderColor;
    }
}
