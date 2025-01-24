package Controller.ui;

import Entites.Heros;
import De.*;

import javax.swing.*;
import java.awt.*;

/* BUT:
*  Ce code définit une brique d'amélioration dans le jeu. Chaque brique affiche des informations telles que le nom du héros, ses
*  points de vie et une icône représentant le héros, aussi que des dés associés au héros. Elle donne options de mise à niveau pour
*  les personnages héros et  c'est affiché à la fin du niveau.
*/

public class BrickAmelioration {
    private int x, y, id, width, height;
    private String nom;
    private Rectangle bounds;
    private Image icon;
    private String text;
    private boolean mouseOver;
    private boolean mousePressed;
    private Heros hero;
    private De de;

    public BrickAmelioration(int x, int y, int id, int width, int height, Heros hero, String nom, De de) {
        this.x = x;
        this.y = y;
        this.id = id;
        this.width = width;
        this.height = height;
        this.hero = hero;
        this.nom = nom;
        this.de = de;

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
        String iconNameURL = "Images/Heros/" + nom + ".png";
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
            de.getFace(i).draw(g2d, xStart + xOffset*i + 5, yStart + 5, 60);
        }

        //to draw the upper one
        de.getFace(4).draw(g2d, xStart + xOffset*2 + 5, yStart-70 + 5, 60);
        g.setColor(hero.getColor());
        g.drawRect(xStart + 2*xOffset, yStart-70-1, 70, 70);

        //to draw the lower one
        de.getFace(5).draw(g2d, xStart + xOffset*2 + 5, yStart+70 + 5, 60);
        g.setColor(hero.getColor());
        g.drawRect(xStart + xOffset*2, yStart+70+1, 69, 70);
    }

    private void drawText(Graphics g) {
        int pdvMax = (int)hero.getPdvMax(); //TODO value not good
        int pdv = pdvMax;

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

    public Heros getHero() {
        return hero;
    }
}

