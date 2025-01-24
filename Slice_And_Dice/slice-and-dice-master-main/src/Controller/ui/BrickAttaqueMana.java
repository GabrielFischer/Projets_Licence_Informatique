package Controller.ui;

import javax.swing.ImageIcon;

import De.Capacite.*;

import main.Joueur;

import java.awt.*;
import java.util.ArrayList;

/* BUT:
*  Ce code Java définit un élément d'interface représentant une option d'attaque basée sur le mana. Chaque brique correspond à une
*  capacité d'attaque spécifique basée sur le mana, affichant son icône et la quantité de mana requise pour l'activation. La brique
*  indique également visuellement si la capacité est disponible en fonction du statut de mana actuel du joueur.
*/

public class BrickAttaqueMana {
    private int x, y, id, width, height;
    private Rectangle bounds;
    private Image icon;
    private String name;
    private boolean mouseOver;
    private boolean mousePressed;
    private Joueur joueur;
    private CapaciteSpe mana;
    private boolean activated;
    private int nbMana;
    private String[][] iconNames = new String[3][0];
    private ArrayList<CapaciteSpe> sorts;

    public BrickAttaqueMana(CapaciteSpe mana, int x, int y, int id, int width, int height, int nbMana, Joueur joueur, String name) {
        this.joueur=joueur;
        this.sorts=joueur.getListeSortsDisponibles();
        this.name=name;
        this.mana = mana;
        this.x = x;
        this.y = y;
        this.id = id;
        this.width = width;
        this.height = height;
        activated = false; 
        this.nbMana = nbMana;
        initBounds();
        initIconNames();
    }

    private void initIconNames() {
        //each row is for a different hero, column 0 is for level 1, even column index (j % 2 == 0) is for one option of upgrade,
        //uneven index of column is for other option of upgrade
        this.iconNames = new String[][] {
                {"Flare"},
                {"Burst"},
                {"Renew"},
                {"Balance"},
                {"Drop"},
                {"Scald"},
                {"Infuse"},
                {"Liquor"},
                {"Revive"},
                {"Bind"},
                {"Blaze"}
        };
    }

    private void initBounds() {
        this.bounds = new Rectangle(x,y,width,height);
    }

    public void draw(Graphics g){
        //body
        drawBody(g);

        //border
        drawBorder(g);

        //text
        drawText(g);

        //mana
        drawMana(g);

        //icon
        paintComponent(g);

    }


    private void drawText(Graphics g) { // Dessine le nom des héros présents
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(new Color(127,212,147));
        g2.setFont(new Font("Courier", Font.BOLD, 20));
        g2.drawString(sorts.get(id).getNom(),x+80, y + 30);
    }

    private void drawMana(Graphics g) { 
        String iconNameURL = "Images/Capacite/ManaVide.png"; 
        String iconNameURL2 = "Images/Capacite/Mana.png"; 
        java.net.URL iconURL = getClass().getClassLoader().getResource(iconNameURL);
        java.net.URL iconURL2 = getClass().getClassLoader().getResource(iconNameURL2);
        ImageIcon iconMana = new ImageIcon(iconURL);
        ImageIcon iconMana2 = new ImageIcon(iconURL2);
        int manaRequis = nbMana;
        for (int i = 0; i < nbMana; i++) {
            if (i<joueur.getMana()) {
                if (i<manaRequis) {
                    if (iconURL2!= null) {
                        g.drawImage(iconMana2.getImage(), x+70+i*25, y+40, 30, 30,null);
                    }
                } else {
                    if (iconURL!=null) {
                        g.drawImage(iconMana.getImage(), x+70+i*25, y+40, 30, 30,null);
                    }
                }
            } else {
                if (iconURL!=null) {
                    g.drawImage(iconMana.getImage(), x+70+i*25, y+40, 30, 30,null);
                }
            }
        }
    }
    

    private void drawBorder(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(new Color(127,212,147));
        g2d.drawRoundRect(x, y, width, height, 10, 10); // Adjust the arc width and height as needed
        if (mousePressed) {
            g2d.drawRoundRect(x + 1, y + 1, width - 2, height - 2, 10, 10); // Adjust the arc width and height as needed
        }
    }

    private void drawBody(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        if (mouseOver) {  // changer activated!
            g2d.setColor(new Color(0x2A0202));
        } else {
            g2d.setColor(new Color(0x000000));
        }
        g2d.fillRoundRect(x, y, width, height, 10, 10);
    }

    private void paintComponent(Graphics g){
        String iconNameURL = "Images/Capacite/AttaqueMana/" + iconNames[id][0] + ".png"; 
        java.net.URL iconURL = getClass().getClassLoader().getResource(iconNameURL);
        ImageIcon iconMana = new ImageIcon(iconURL);
        if (iconURL != null) {
            g.drawImage(iconMana.getImage(), x+10, y+10, 60, 60, null);
        }
    }

    public Rectangle getBounds(){
        return this.bounds;
    }

    public CapaciteSpe getMana(){
        return this.mana;
    }

    public void setMouseOver(boolean b) {
        this.mouseOver = b;
    }

    public boolean getMouseOver(){
        return this.mouseOver;
    }

    public boolean getActivated(){
        return activated;
    }

    public int getX(){
        return this.x;
    }

    public int getY(){
        return this.y;
    }

    public int getHeight(){
        return this.height;
    }


    public int getWidth(){
        return this.width;
    }

    public void setWidth(int w){
        width=w;
    }
    public void setX(int x){
        this.x=x;
    }
    public void setY(int y){
        this.y=y;
    }
    public String getName()
    {
        return this.name;
    }

    public int getId() {
        return id;
    }
}

