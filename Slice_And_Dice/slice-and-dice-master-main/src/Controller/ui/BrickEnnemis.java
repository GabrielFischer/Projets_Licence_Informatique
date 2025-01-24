package Controller.ui;

import javax.swing.*;

import Entites.Ennemis;
import De.Capacite.*;

import java.awt.*;

/* BUT:
*  Ce code Java définit des briques représentants les ennemis dans le jeu. Chaque brique correspond à un ennemi spécifique,
*  affichant son nom, ses points de vie et une icône. De plus, il indique la cible d'attaque de l'ennemi avec une ligne visuelle
*  lorsqu'il est survolé.
*/

public class BrickEnnemis{
    private int x, y, id, width, height;
    private Rectangle bounds;
    private Image icon;
    private String text;
    private boolean mouseOver;
    private boolean mousePressed;
    private String[][] iconNames = new String[5][7];
    private Ennemis ennemi;
    private Griffures griffures = new Griffures(1);

    private float dashPhase = 0f;


    public BrickEnnemis(Ennemis e,int x, int y, int id, int width, int height) {
        this.ennemi = e;
        this.x = x;
        this.y = y;
        this.id = id;
        this.width = width;
        this.height = height;

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
        drawDice(g);

        //xp
        //drawXP(g);

        //pv
        drawPV(g);

        //lignesAttaque
        if(mouseOver){
            dessineLignesAttaque(g);
        }
    }

    private void drawXP(Graphics g) {
        int pdv = (int)this.ennemi.getPdv();
        java.net.URL iconURL = getClass().getClassLoader().getResource("xpPoint.png");
        ImageIcon iconEnnemi = new ImageIcon(iconURL);
        for(int i = 0; i < pdv; i++){
            if (iconURL != null) {
                g.drawImage(iconEnnemi.getImage(), x+80, y+40, 10, 10, null);
            }
        }
    }

    private void drawPV(Graphics g) {
        int pvPerdus = (int) ennemi.getPdvMax() - (int) ennemi.getPdv(); //Nb de coeurs perdus
        int pvMenace = Math.min((int)ennemi.getPdv(),(int)ennemi.getPdvMenaces()); //Nb de coeurs menacés
        int pvRestants = (int) ennemi.getPdv() - pvMenace; //Nb coeurs restants
        int pvTotaux = pvRestants + pvMenace + pvPerdus;
        int width = 15;
        int spacing = 5;
        int coeursParLigne = 5;
        int nbLignes = Math.min(pvTotaux / coeursParLigne + 1, 3); // 3 lignes max
    
        //position de départ
        int startX = x + 80;
        int startY = y + 30;
    
        java.net.URL PVR = getClass().getClassLoader().getResource("PVRouge.png");
        java.net.URL PVG = getClass().getClassLoader().getResource("PVGris.png");
        java.net.URL PVJ = getClass().getClassLoader().getResource("PVJaune.png");
        java.net.URL Plus = getClass().getClassLoader().getResource("plus.jpg");
        ImageIcon iconPVR = new ImageIcon(PVR);
        ImageIcon iconPVG = new ImageIcon(PVG);
        ImageIcon iconPVJ = new ImageIcon(PVJ);
        ImageIcon iconPlus = new ImageIcon(Plus);
    
        for (int i = 0; i < nbLignes; i++) {
            int nbCoeurs = Math.min(coeursParLigne, pvTotaux - i * coeursParLigne);
            for (int j = 0; j < nbCoeurs; j++) {
                if (pvRestants > 0) {
                    g.drawImage(iconPVR.getImage(), startX + (width + spacing) * j, startY + (width) * i, width, width, null);
                    pvRestants--;
                } else if (pvMenace > 0) {
                    g.drawImage(iconPVJ.getImage(), startX + (width + spacing) * j, startY + (width) * i, width, width, null);
                    pvMenace--;
                } else if (pvPerdus > 0) {
                    g.drawImage(iconPVG.getImage(), startX + (width + spacing) * j, startY + (width) * i, width, width, null);
                    pvPerdus--;
                }
            }
            //affiche l'icône "plus" si nécessaire
            if (i == nbLignes - 1 && pvTotaux > coeursParLigne * (i + 1)) {
                g.drawImage(iconPlus.getImage(), startX + (width + spacing) * (coeursParLigne - 1), startY + (width) * i, width, width, null);
            }
        }
    }

    private void drawDice(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        if(ennemi.getFaceSelectionnee()!=null){
            ennemi.getFaceSelectionnee().draw(g2d, x+180, y+10,60);
        }
        g2d.setColor(ennemi.getColor());
        g2d.drawRoundRect(x+180, y+10, 60, 60, 10, 10);
    }

    private void drawText(Graphics g) { // Dessine le nom des ennemis présents
        Graphics2D g2 = (Graphics2D) g;
        String text = ennemi.getNomEntite();
        g2.setColor(ennemi.getColor());
        if (ennemi.getNomEntite().length()>12){
            g2.setFont(new Font("Courier", Font.BOLD, 13));
            if (ennemi.getNomEntite().length()>15){
                text = text.substring(0,14)+".";
            }
            g2.drawString(text,x+73, y + 30);
        }else{
            g2.setFont(new Font("Courier", Font.BOLD, 15));
            g2.drawString(text,x+80, y + 30);
        }
    }

    private void drawBorder(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(ennemi.getColor());
        g2d.drawRoundRect(x, y, width, height, 10, 10); // Adjust the arc width and height as needed
        if (mousePressed) {
            g2d.drawRoundRect(x + 1, y + 1, width - 2, height - 2, 10, 10); // Adjust the arc width and height as needed
        }
    }

    private void drawBody(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        if (mouseOver) {
            g2d.setColor(new Color(0x2A0202));
        } else {
            g2d.setColor(new Color(0x000000));
        }
        g2d.fillRoundRect(x, y, width, height, 10, 10);
    }

    private void paintComponent(Graphics g){
        if (iconNames[id][0] != null){
            String iconNameURL = "Images/ImagesEnnemisRediFinal/" + ennemi.getClass().getSimpleName() +"/" + ennemi.getNomImage() +".png"; 
            java.net.URL iconURL = getClass().getClassLoader().getResource(iconNameURL);
            if (iconURL != null) {
                ImageIcon iconEnnemi = new ImageIcon(iconURL);
                g.drawImage(iconEnnemi.getImage(), x+10, y+10, 60, 60, null);
            }
        }
    }

    public void dessineLignesAttaque(Graphics g) {
        if(ennemi.getFaceSelectionnee().getCapacite()!=null){
            if(ennemi.getHeroCible()!=null){
                int x1 = this.x - 10;
                int x2 = ennemi.getHeroCible().getBrickHero().getX() + ennemi.getHeroCible().getBrickHero().getWidth() + 10;
                int y1 = this.y + this.height/2;
                int y2 = ennemi.getHeroCible().getBrickHero().getY() + ennemi.getHeroCible().getBrickHero().getHeight() / 2;

                Graphics2D g2d = (Graphics2D) g.create();
                Stroke dashed = new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, dashPhase);
                dashPhase += 0.5f;
                g2d.setStroke(dashed);
                g2d.drawLine(x2, y2, x1, y1);
                g2d.dispose();
            }
        }
    }

    public Rectangle getBounds(){
        return this.bounds;
    }

    public Ennemis getEnnemis(){
        return this.ennemi;
    }


    public void setMouseOver(boolean b) {
        this.mouseOver = b;
    }

    public boolean getMouseOver(){
        return this.mouseOver;
    }
}
