package Controller.ui.Custom;

import Graphics.scenes.Custom.*;
import main.*;

import javax.imageio.ImageIO;
import javax.swing.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class CustomHeroPanel {
    private InfoBarHome infoBarHome;
    private CustomHomePage homePage;
    private CustomFinalPage finalPage;
    private Custom custom;
    private Main main;
    private int x, y, id, width, height, windowX, windowY;
    private int xChanged, yChanged, widthChanged, heightChanged, xChanged2, yChanged2, widthChanged2, heightChanged2;
    private Rectangle bounds;


    private Image icon;
    private boolean mouseOver;
    private boolean mousePressed;
    //private String[] iconHeroNames = new String[5];
    private String heroSelected, chapeauSelected, armeSelected;
    private int compteurCustom;
    private String path;


    public CustomHeroPanel(CustomHomePage homePage) {  //classe pour le héros custom : gérer l'image au centre
        this.x = this.xChanged = this.xChanged2 = 570;
        this.y = this.yChanged = this.yChanged2 = 200;
        this.width = this.widthChanged = this.widthChanged2 = 200;
        this.height = this.heightChanged = this.heightChanged2 = 200;

        this.infoBarHome = homePage.getInfoBar();
        this.custom = homePage.getCustom();
        this.main = custom.getMain();
        this.finalPage = custom.getFinalPage();
        compteurCustom = 0;
        windowX = main.getX();
        windowY = main.getY();
        path = "";

        initBounds();
        //initIconHeroNames();
        heroSelected = chapeauSelected = armeSelected = "";
    }

/*     private void initIconHeroNames() {
        this.iconHeroNames = new String[] {"lion", "rhino", "wolf", "pingu", "snek"};
    } */

    private void initBounds() {
        this.bounds = new Rectangle(x,y,width,height);
    }

    public void initXY(int x, int y){
        this.x = this.xChanged = this.xChanged2 = x;
        this.y = this.yChanged = this.yChanged2 = y;
        initBounds();
    }

    public void draw(Graphics g){
        //body
        drawBody(g);

        //border
        drawBorder(g);

        //icon
        paintComponent(g);
    }

    private void drawBorder(Graphics g) {  //bordures neutres
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(new Color(107,111,127));
        g2d.drawRoundRect(x, y, width, height, 10, 10); // Adjust the arc width and height as needed
    }

    private void drawBody(Graphics g) {  //fond neutre
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(new Color(0x000000));
        g2d.fillRoundRect(x, y, width, height, 10, 10);
    }

    private void paintComponent(Graphics g){  //implémentation de l'image de l'animal
        if (heroSelected != ""){  //changer ici pour heroSelected
            String iconNameURL = "Images/HeroCustom/" + heroSelected + ".png"; //pareil
            java.net.URL iconURL = getClass().getClassLoader().getResource(iconNameURL);
            ImageIcon iconHero = new ImageIcon(iconURL);
            if (iconURL != null) {
                g.drawImage(iconHero.getImage(), x, y, width-10, height-10, null);  //changer width et height pour coller au panneau central
            }
        }
        if (chapeauSelected != ""){  //dessine le chapeau sélectionné sur le moment
            String iconNameURL = "Images/HeroCustom/" + chapeauSelected + ".png"; //pareil
            java.net.URL iconURL = getClass().getClassLoader().getResource(iconNameURL);
            ImageIcon iconAccessoire = new ImageIcon(iconURL);
            if (iconURL != null) {
                g.drawImage(iconAccessoire.getImage(), xChanged, yChanged, widthChanged-10, heightChanged-10, null);  //changer width et height pour coller au panneau central
            }
        }
        if (armeSelected != ""){   //dessine l'arme sélectionnée sur le moment
            String iconNameURL = "Images/HeroCustom/" + armeSelected + ".png"; //pareil
            java.net.URL iconURL = getClass().getClassLoader().getResource(iconNameURL);
            ImageIcon iconAccessoire = new ImageIcon(iconURL);
            if (iconURL != null) {
                g.drawImage(iconAccessoire.getImage(), xChanged2, yChanged2, widthChanged2-10, heightChanged2-10, null);  //changer width et height pour coller au panneau central
            }
        }
    }

    public void setSelection(String name, int id){  //change le héros, le chapeau et l'arme sélectionnés --> fonction à problèmes
        if (id == 0){
            this.heroSelected = name;
        }
        else if (id == 1){
            this.chapeauSelected = name;
        }
        else if (id == 2){
            this.armeSelected = name;
        }
        else{
            System.out.println("id non reconnu");
        }
    }

    //----------méthodes de navigation des chapeaux et armes-----------


    public void goingUp(boolean b){
        if (!b){
            if (yChanged > y + 2){
                this.yChanged -= 2;
                //System.out.println(yChanged);
            }
            return;
        }
        if (yChanged2 > y + 2){
            this.yChanged2 -= 2;
            //System.out.println(yChanged);
        }
        return;
    }

    public void goingDown(boolean b){
        if (!b){
            if (yChanged + heightChanged < y + height){
                this.yChanged += 2;
            }
            return;
        }
        if (yChanged2 + heightChanged2 < y + height){
            this.yChanged2 += 2;
        }
        return;
    }

    public void goingLeft(boolean b){
        if (!b){
            if (xChanged > x + 2){
                this.xChanged -= 2;
            }
            return;
        }
        if (xChanged2 > x + 2){
            this.xChanged2 -= 2;
        }
        return;
    }

    public void goingRight(boolean b){
        if (!b){
            if (xChanged + widthChanged < x + width){
                this.xChanged += 2;
            }
            return;
        }
        if (xChanged2 + widthChanged2 < x + width){
            this.xChanged2 += 2;
        }
        return;
    }

    public void minus(boolean b){
        if (!b){
            if (widthChanged > width/15 && heightChanged > width/15){
                widthChanged -= 5;
                heightChanged -= 5;
            }
            return;
        }
        if (widthChanged2 > width/15 && heightChanged2 > width/15){
            widthChanged2 -= 5;
            heightChanged2 -= 5;
        }
        return;
    }

    public void plus(boolean b){
        if (!b){
            if ((widthChanged < width) && (heightChanged < height) && (x + width > xChanged + widthChanged) && (y + height > yChanged + heightChanged)){
                widthChanged += 5;
                heightChanged += 5;
            }
            return;
        }
        if ((widthChanged2 < width) && (heightChanged2 < height) && (x + width > xChanged2 + widthChanged2) && (y + height > yChanged2 + heightChanged2)){
            widthChanged2 += 5;
            heightChanged2 += 5;
        }
        return;
    }


    public Rectangle getBounds(){
        return this.bounds;
    }

    public void setMouseOver(boolean b) {
        this.mouseOver = b;
    }

    public boolean getMouseOver(){
        return this.mouseOver;
    }

    public int getX(){
        return x;
    }
    public int getY(){
        return y;
    }
    public int getHeightWidth(){
        return width;
    }

    public void tabPixels(int x, int y, String name){  //capturer l'image customisée du héros et l'enregistrer
        //compteurCustom++;
        windowX = main.getX();
        windowY = main.getY();
        Rectangle zoneCapture = new Rectangle(x + windowX + 9, y + windowY + 32, width - 4, height - 4);  //zone de capture des pixels 
        try {
            Robot robot = new Robot();  //création d'un robot permettant de sélectionner la zone à capturer
            BufferedImage capture = robot.createScreenCapture(zoneCapture);

            /* File fichierSortie = new File("HeroCustom" + String.valueOf(compteurCustom) +".png");  //enregistrement dans une image
            ImageIO.write(capture, "png", fichierSortie); */

            File dossierDestination = new File("res/Images/Heros/");

            // Enregistrer l'image capturée dans le dossier de destination
            File fichierDestination = new File(dossierDestination, name +".png");
            ImageIO.write(capture, "png", fichierDestination);
            path = "res/Images/Heros/" + name +".png";

            System.out.println("Capture de l'écran enregistrée avec succès.");

        } catch (AWTException | IOException e) {
            e.printStackTrace();
        }
    }

    public String getPath(){
        return path;
    }
}
