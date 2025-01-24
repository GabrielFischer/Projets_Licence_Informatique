package Controller.ui;

import Entites.Heros;

import javax.swing.*;
import java.awt.*;

import static javax.swing.SwingUtilities.paintComponent;

/* BUT:
 *  Ce code Java définit un élément d'interface utilisateur représentant des informations sur les héros dans le jeu. Chaque brique
 *  correspond à un héros spécifique, affichant son nom, ses points de vie et des icônes représentants ses faces de dés. Cette
 *  brique est afichée au milieu de l'écran lorsque l'on clique sur certaine entité et fournit des informations plus détaillées sur
 *  l'entité.
 */

public class BrickInfoHero {
    private int x, y, id, width, height;
    private Rectangle bounds;
    private Image icon;
    private String text;
    private boolean mouseOver;
    private boolean mousePressed;
    private Heros hero;

    public BrickInfoHero(int x, int y, int id, int width, int height, Heros hero) {
        this.x = x;
        this.y = y;
        this.id = id;
        this.width = width;
        this.height = height;
        this.hero = hero;

        initBounds();
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

        //icon
        paintComponent(g);

        //dice
        drawPatronDice(g);
    }

    private void paintComponent(Graphics g) {
        String iconNameURL = "Images/Heros/" + hero.getNomEntite() + ".png";
        java.net.URL iconURL = getClass().getClassLoader().getResource(iconNameURL);
        ImageIcon iconHero = new ImageIcon(iconURL);
        if (iconURL != null) {
            g.drawImage(iconHero.getImage(), x+10, y+10, 70, 70, null);
        }
        g.setColor(hero.getColor());
        g.drawRect(x+10, y+10, 70, 70);
    }

    private void drawPatronDice(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        int xStart = x+50;
        int yStart = y+130;
        int xOffset = 70;
        for(int i = 0; i < 4; i++){
            g.setColor(hero.getColor());
            g.drawRect(xStart + xOffset*i, yStart, 70, 70);
            hero.getDe().getFace(i).draw(g2d, xStart + xOffset*i + 5, yStart + 5, 60);
            if(this.hero.getDe().getFace(i) == this.hero.getDe().getFaceDessus()){
                g.setColor(Color.YELLOW);
                g.drawRect(xStart + xOffset*i, yStart, 69, 70);
            }
        }

        //to draw the upper one
        hero.getDe().getFace(4).draw(g2d, xStart + xOffset*2 + 5, yStart-70 + 5, 60);
        if(this.hero.getDe().getFace(4) == this.hero.getDe().getFaceDessus()){
            g.setColor(Color.YELLOW);
            g.drawRect(xStart + xOffset*2, yStart-70, 69, 70);
        }
        else{
            g.setColor(hero.getColor());
            g.drawRect(xStart + 2*xOffset, yStart-70-1, 70, 70);
        }

        //to draw the lower one
        hero.getDe().getFace(5).draw(g2d, xStart + xOffset*2 + 5, yStart+70 + 5, 60);
        if(this.hero.getDe().getFace(5) == this.hero.getDe().getFaceDessus()){
            g.setColor(Color.YELLOW);
            g.drawRect(xStart + xOffset*2, yStart+70, 69, 70);
        }
        else{
            g.setColor(hero.getColor());
            g.drawRect(xStart + xOffset*2, yStart+70+1, 69, 70);
        }
    }

    private void drawText(Graphics g) {
        String nom = hero.getNomEntite();
        int pdv = (int)hero.getPdv();
        int pdvMax = (int)hero.getPdvMax();
        String text = nom + " " + pdv + "/" + pdvMax;
        g.setColor(hero.getColor());
        g.setFont(new Font("Courier", Font.BOLD, 18));
        g.drawString(text,x + 100, y + 30);
    }

    private void drawBorder(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(hero.getColor());
        g2d.drawRoundRect(x, y, width, height, 10, 10);
    }

    private void drawBody(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.BLACK);
        g2d.fillRoundRect(x, y, width, height, 10, 10);
    }

    public Heros getHero() {
        return hero;
    }
}
