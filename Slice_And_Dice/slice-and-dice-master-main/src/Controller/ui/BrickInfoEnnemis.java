package Controller.ui;

import Entites.Ennemis;

import javax.swing.*;
import java.awt.*;

import static javax.swing.SwingUtilities.paintComponent;

/* BUT:
*  Ce code Java définit un élément d'interface utilisateur appelé BrickInfoEnnemis, représentant des informations sur les ennemis
*  dans un jeu. Chaque brique correspond à un ennemi spécifique, affichant son nom, ses points de vie et des icônes représentants ses
*  faces de dés. Cette brique est afichée au milieu de l'écran lorsque l'on clique sur certaine entité et fournit des informations
*  plus détaillées sur l'entité.
*/

public class BrickInfoEnnemis {
    private int x, y, id, width, height;
    private Rectangle bounds;
    private Image icon;
    private String text;
    private boolean mouseOver;
    private boolean mousePressed;
    private Ennemis ennemi;
    private String[][] iconNames = new String[5][7];

    public BrickInfoEnnemis(int x, int y, int id, int width, int height, Ennemis ennemi) {
        this.x = x;
        this.y = y;
        this.id = id;
        this.width = width;
        this.height = height;
        this.ennemi = ennemi;

        initBounds();
        initIconNames();

    }

    private void initIconNames() {
        //chaque ligne: ennemi de même classe
        this.iconNames = new String[][] {
                {"SQUELETTE_1", "AMALGAME_NOIR", "LOUP_GAROU", "CRAPAUD_TAUREAU", "OIE_SBIRE", "TAUREAU", "MOUSTIQUE", null},
                {"Capitaine Squelette", "Lion", "Echoué", "Apprenti Mage Noir", null, null, null, null},
                {"Zombie Sorcier", "Ours-Garou", "Spectre", "Cauchemar", null, null, null, null},
                {"Golem de Foudre", "Golem de l'Eau", "Atronach de Feu", "Spriggan", null, null, null, null},
                {"Chaton", "Dindon", "Lapin", "Oie", "Cthulhu", "Plague Doctor", "Lich", "Grand Mage Noir"}
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

        //icon
        paintComponent(g);

        //dice
        drawPatronDice(g);
    }

    private void paintComponent(Graphics g) {
        if (iconNames[id][0] != null){
            String iconNameURL = "Images/ImagesEnnemisRediFinal/" + ennemi.getClass().getSimpleName() +"/" + ennemi.getNomImage() +".png";
            java.net.URL iconURL = getClass().getClassLoader().getResource(iconNameURL);
            if (iconURL != null) {
                ImageIcon iconEnnemi = new ImageIcon(iconURL);
                g.drawImage(iconEnnemi.getImage(), x+10, y+10, 60, 60, null);
            }
        }
        g.setColor(ennemi.getColor());
        g.drawRect(x+10, y+10, 70, 70);

    }

    private void drawPatronDice(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        int xStart = x+50;
        int yStart = y+130;
        int xOffset = 70;
        for(int i = 0; i < 4; i++){
            g.setColor(ennemi.getColor());
            g.drawRect(xStart + xOffset*i, yStart, 70, 70);
            ennemi.getDe().getFace(i).draw(g2d, xStart + xOffset*i + 5, yStart + 5, 60);
            if(this.ennemi.getDe().getFace(i) == this.ennemi.getDe().getFaceDessus()){
                g.setColor(Color.YELLOW);
                g.drawRect(xStart + xOffset*i, yStart, 69, 70);
            }
        }
        //to draw the upper one
        g.drawRect(xStart + 2*xOffset, yStart-70, 70, 70);
        ennemi.getDe().getFace(4).draw(g2d, xStart + xOffset*2 + 5, yStart-70 + 5, 60);
        if(this.ennemi.getDe().getFace(4) == this.ennemi.getDe().getFaceDessus()){
            g.setColor(Color.YELLOW);
            g.drawRect(xStart + xOffset*2, yStart, 69, 70);
        }

        //to draw the lower one
        g.drawRect(xStart + 2*xOffset, yStart+70, 70, 70);
        ennemi.getDe().getFace(5).draw(g2d, xStart + xOffset*2 + 5, yStart+70 + 5, 60);
        if(this.ennemi.getDe().getFace(5) == this.ennemi.getDe().getFaceDessus()){
            g.setColor(Color.YELLOW);
            g.drawRect(xStart + xOffset*2, yStart, 69, 70);
        }
    }

    private void drawText(Graphics g) {
        String nom = ennemi.getNomEntite();
        int pdv = (int)ennemi.getPdv();
        int pdvMax = (int)ennemi.getPdvMax();
        String text = nom + " " + pdv + "/" + pdvMax;
        g.setColor(ennemi.getColor());
        g.setFont(new Font("Courier", Font.BOLD, 18));
        g.drawString(text,x + 100, y + 30);
    }

    private void drawBorder(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(ennemi.getColor());
        g2d.drawRoundRect(x, y, width, height, 10, 10);
    }

    private void drawBody(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.BLACK);
        g2d.fillRoundRect(x, y, width, height, 10, 10);
    }

    public Ennemis getEnnemis() {
        return ennemi;
    }
}

