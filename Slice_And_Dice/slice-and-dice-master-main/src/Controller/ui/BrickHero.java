package Controller.ui;

import javax.swing.*;

import Entites.Heros;

import java.awt.*;

/* BUT:
*  Ce code définit un élément d'interface utilisateur appelé BrickHero, représentant les héros d'un jeu. Chaque brique correspond
*  à un héros spécifique, affichant son nom, ses points de vie, ses points de bouclier et une icône. De plus, il indique si le héros
*  est sélectionné et restitue la face sélectionnée des dés du héros.
*/

public class BrickHero {
    private int x, y, id, width, height;
    private Rectangle bounds;
    private Image icon;
    private String text;
    private boolean mouseOver;
    private boolean mousePressed;
    private Heros hero;
    public boolean selected=false;

    public BrickHero(Heros h,int x, int y, int id, int width, int height) {
        this.hero = h;
        this.x = x;
        this.y = y;
        this.id = id;
        this.width = width;
        this.height = height;

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
        drawDice(g);


        //Pv
        drawPV(g);

        //Bouclier
        drawBouclier(g);
    }

    private void drawXP(Graphics g) {
        int pdv = (int)this.hero.getPdv();
        java.net.URL iconURL = getClass().getClassLoader().getResource("xpPoint.png");
        ImageIcon iconHero = new ImageIcon(iconURL);
        for(int i = 0; i < pdv; i++){
            if (iconURL != null) {
                g.drawImage(iconHero.getImage(), x+80, y+40, 10, 10, null);
            }
        }
    }

    private void drawPV(Graphics g) {
        int pvPerdus = (int) hero.getPdvMax() - (int) hero.getPdv(); //Nb de coeurs perdus
        int pvMenace = Math.min((int)hero.getPdv(),(int)hero.getPdvMenaces()); //Nb de coeurs menacés
        int pvRestants = (int) hero.getPdv() - pvMenace; //Nb coeurs restants
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
    

    private void drawBouclier(Graphics g){
        // Calcul de la position de départ pour l'affichage des points de bouclier
        int startX=x+240;
        /* if(selected){
            startX = x+250;
        } */
        int startY = y+5;
        int bouclier = hero.getBouclier();

        if(bouclier>0){
            java.net.URL BB = getClass().getClassLoader().getResource("BouclierBronze.png");
            java.net.URL BA = getClass().getClassLoader().getResource("BouclierArgent.png");
            java.net.URL BO = getClass().getClassLoader().getResource("BouclierOr.png");
            ImageIcon iconBB = new ImageIcon(BB);
            ImageIcon iconBA = new ImageIcon(BA);
            ImageIcon iconBO = new ImageIcon(BO);

            if (bouclier <=2) {
                g.drawImage(iconBB.getImage(), startX, startY, 80, 60, null);
            } else if (bouclier<=4) {
                g.drawImage(iconBA.getImage(), startX, startY, 80, 60, null);
            } else{
                g.drawImage(iconBO.getImage(), startX, startY, 80, 60, null);
            }

            // Dessiner la valeur du bouclier a droite de la brick
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(Color.WHITE); // Couleur du texte
            g2d.setFont(new Font("Courier", Font.BOLD, 18)); // Police du texte
            if (bouclier<10){
                g2d.drawString(""+bouclier, startX+35, startY+35);
            }else if(bouclier >=10 && bouclier <100){
                g2d.drawString(""+bouclier, startX+30, startY+35);
            }else if (bouclier >=100){
                g2d.drawString("99+", startX+26, startY+35);
            }
        }


    }

    private void drawDice(Graphics g) { // fonction qui dessine les des selectionnés des heros
        Graphics2D g2d = (Graphics2D) g;
        if(hero.aFaceSelectionnee()){//Dessine la face selectionnée si une face est selectionnée
            hero.getFaceSelectionnee().draw(g2d, x+180, y+10,60); 
        }
        else{//Dessine un carré noir si aucune face n'est selectionnée
            g2d.setColor(Color.BLACK);
            g2d.fillRoundRect(x+180, y+10, 60, 60, 10, 10);
        }
        g2d.setColor(hero.getColor());
        g2d.drawRoundRect(x+180, y+10, 60, 60, 10, 10);

        //g2d.dispose();
    }

    private void drawText(Graphics g) { // Dessine le nom des héros présents
        Graphics2D g2 = (Graphics2D) g;
        String text = hero.getNomEntite();
        g2.setColor(hero.getColor());
        g2.setFont(new Font("Courier", Font.BOLD, 15));
        g2.drawString(text,x+80, y + 30);
    }

    private void drawBorder(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(hero.getColor());
        g2d.drawRoundRect(x, y, width, height, 10, 10); // Adjust the arc width and height as needed
    }

    private void drawBody(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        if (mouseOver) {
            if(hero.getDe().used || !hero.enVie()){
                g2d.setColor(new Color(0x565656));
            }else{
                g2d.setColor(new Color(0x5A0202));
            }
        } else {
            g2d.setColor(new Color(0x000000));
        }
        g2d.fillRoundRect(x, y, width, height, 10, 10);
    }

    private void paintComponent(Graphics g){
        if(hero.getNomEntite()!=null){
            String iconNameURL = "Images/Heros/" + hero.getNomEntite() + ".png";
            java.net.URL iconURL = getClass().getClassLoader().getResource(iconNameURL);
            ImageIcon iconHero = new ImageIcon(iconURL);
            if (iconURL != null) {
                g.drawImage(iconHero.getImage(), x+10, y+10, 60, 60, null);
            }
        }
        
        
    }

    public Rectangle getBounds(){
        return this.bounds;
    }

    public Heros getHero(){
        return this.hero;
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

}
