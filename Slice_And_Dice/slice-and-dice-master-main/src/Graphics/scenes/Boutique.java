package Graphics.scenes;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.Random;

import Equipement.*;
import main.Main;
import Controller.ui.MyButton;

/* BUT:
*  Classe contenant le code de la boutique accessible pendant le jeu. Là, un joueur peut acheter différents équipements qui pourront
*  être utilisés plus tard tout au long du jeu. Il y a 6 équipements au total, mais le programme en choisit et en affiche au hasard
*  seulement 3.
*/

public class Boutique extends MenuMethods {
    Equipement [] e = new Equipement[3]; // liste des équipements

    Playing playing;
    Main main;
    
    int xBoxes;
    int yBoxes;
    int boxHeight;
    int boxWidth;

    Rectangle[] boxes = new Rectangle[3];
    Rectangle[] itemBox = new Rectangle[3];
    MyButton[] buyBox = new MyButton[3];

    
    boolean hover = false;
    int xMouse = 0;
    int yMouse = 0;
    int hoverIndex = -1;
    
    public Boutique(Playing playing){
        super(playing.getMain(),300,750,"BOUTIQUE");
        this.playing = playing;
        initEquipement();
        initBoxes();
        initItemBoxes();
    }


    public void initBoxes(){
        
        xBoxes = x + centerRectangleX(width/2,boxWidth); 
        yBoxes = y + height/3 - height/8;
        boxHeight = height/3 + height/3;
        boxWidth = width/5;
    }

    public void initItemBoxes(){
        int ajoutX = width/2;
        for(int i = 0;i<itemBox.length;i++){
            int currentX = this.x + centerRectangleX(ajoutX + ajoutX*i,boxWidth);
            boxes[i] = new Rectangle(currentX,yBoxes,boxWidth,boxHeight);
            itemBox[i] = new Rectangle(currentX + boxWidth /9,yBoxes + boxWidth /9,boxWidth - boxWidth /9 *2,boxWidth - boxWidth /9 *2);
            buyBox[i] = new MyButton("Acheter",currentX ,yBoxes + (boxHeight - boxHeight/5),boxWidth,boxHeight/5, borderColor, TextColor);
        }
    }


    public void initEquipement(){
        e[0] = randomEquip();

        boolean flag = false;
        while(!flag){
            e[1]=randomEquip();
            if(!e[1].nom.equals(e[0].nom)){flag=true;}
        }

        flag=false;
        while(!flag){
            e[2]=randomEquip();
            if(!e[2].nom.equals(e[0].nom) && !e[2].nom.equals(e[1].nom)){flag=true;} //Empeche d'avoir 2 fois le meme equip dans la boutique
        }
    }

    public void rerollBoutique(){
        initEquipement();
        for (int i =0;i<buyBox.length;i++){
            buyBox[i].setText("Acheter");
        }
    }

    public Equipement randomEquip(){
        Random random = new Random();
        int r = random.nextInt(6);
        switch(r){
            case 0 : return new BouclierDivin();
            case 1 : return new Faucon();
            case 2 : return new ForceGauche();
            case 3 : return new GanteletsDePuissance();
            case 4 : return new Pansements();
            case 5 : return new PetitCoeur();
        }
        return new Equipement("bug", "Aller voir fonction randomEquip de boutique", 0, 0); //Unreachable normalement
    }
    public void drawBoutique(Graphics2D g2){
        super.draw(g2);
        
        String text = String.valueOf(playing.getJoueur().getMonnaiePartie()) ;
        int textLenght = (int)g2.getFontMetrics().getStringBounds(text,g2).getWidth();
        g2.drawImage(icons[1], x + textLenght, y + barHeight,40,40, null);
        g2.drawString(text , x + (int)strokeWidth+1, y + barHeight + barHeight/2);


        drawItems(g2);

        if(hover){
            e[hoverIndex].drawDescription(g2, xMouse, yMouse, (int)(itemBox[hoverIndex].getHeight()), borderColor, fillColor,TextColor);
        }
    }
    
    public void drawItems(Graphics2D g2){ // méthode qui dessine tous les items en appelant une méthode qui dessine un item
        g2.setColor(borderColor); //met la couleur à border color
        //int ajoutX = width/2;
        for(int i = 0; i<e.length ; i++){
            g2.setColor(borderColor);
            drawItem(g2,i);
        }
    }

    public void drawItem(Graphics2D g2,int i){ //méthode qui dessine un seul item
        //grand rectangle item
        g2.draw(boxes[i]);

        int x = (int) itemBox[i].getX();

        //carré dans lequel se trouve l'item
        e[i].draw(g2, x, yBoxes + boxWidth /9, boxWidth - boxWidth /9 *2);
        g2.draw(itemBox[i]);

        
        String text = String.valueOf(e[i].prix);
        int textLenght = (int)g2.getFontMetrics().getStringBounds(text,g2).getWidth();
        g2.drawString(text, x, yBoxes-10);
        g2.drawImage(icons[1], x+textLenght,yBoxes-28,30,30,null );

        buyBox[i].draw(g2);//dessine le bouton "acheter"
        
    }

    public Rectangle getClose(){
        return close;
    }

    public int centerRectangleX(int totalWidth, int rectangleWidth) {
        return (totalWidth - rectangleWidth) / 2;
    }

    public void mouseClicked(int x, int y){
        super.mouseClicked(x, y);
        for (int i = 0; i < e.length;i++){
            if(buyBox[i].getBounds().contains(x,y)){
                if(!buyBox[i].getText().equals("Acheté")){

                
                if (e[i].prix<=playing.getJoueur().getMonnaiePartie()){
                    playing.getMain().getAudio().playCashRegisterSound();
                    playing.getJoueur().setMonnaiePartie(playing.getJoueur().getMonnaiePartie()-e[i].prix);
                    buyBox[i].setText("Acheté");
                    playing.getJoueur().achievement.setItemsAchetés(playing.getJoueur().achievement.getItemsAchetés()+1);
                    playing.getInventaire().addEquipement(e[i]);
                }}
            }
        }
    }

    public void mouseMoved(int x, int y){
        for (int i = 0; i<e.length;i++){
            if(itemBox[i].getBounds().contains(x,y)){
                
                this.hover = true;
                this.xMouse = x;
                this.yMouse = y;
                this.hoverIndex = i;
                break;
            }
            else{
                this.hover = false;
            }
        }
    }

    
    public Equipement getEquipement(int i){
        return this.e[i];
    }
    public void setEquipement(int i , Equipement equipement){
        this.e[i] = equipement;
    }

}
