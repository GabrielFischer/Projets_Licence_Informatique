package Graphics.scenes;

import java.awt.Graphics2D;
import java.awt.Rectangle;

import De.Capacite.*;
import main.Main;
import Controller.ui.MyButton;

/* BUT:
*  Cette classe contient du code présentant une boutique accessible depuis le menu. Là, le joueur peut acheter différentes capacités
*  qui pourront être utilisées plus tard tout au long de la partie.
*/

public class BoutiqueMenu extends MenuMethods{
    Capacite [] c = {new BouclierSoin(1), new Poison(1), new HacheSoin(1), new SoinsAccrus(), new MortSubite()};

    
    Menu menu;
    Main main;


    int xBoxes;
    int yBoxes;
    int boxHeight;
    int boxWidth;

    Rectangle[] boxes = new Rectangle[5];
    Rectangle[] itemBox = new Rectangle[5];
    MyButton[] buyBox = new MyButton[5];


    boolean hover = false;
    int xMouse = 0;
    int yMouse = 0;
    int hoverIndex = -1;

    public BoutiqueMenu(Menu menu){
        super(menu.getMain(), 360, 850, "BOUTIQUE");
        this.menu = menu;
        this.main = menu.getMain();
        initBoxes();
        initItemBoxes();
    }


    public void initBoxes(){

        yBoxes = y + height/2 - height/6;
        boxHeight = height/4 + height/3;
        boxWidth = width/6;

    }

    public void initItemBoxes(){
        int espace = width / 30;
        int offset = (width - (boxWidth * itemBox.length) - (espace * (itemBox.length - 1))) / 2;
        for(int i = 0;i<itemBox.length;i++){
            int currentX = this.x + (boxWidth * i) + (espace * i) + offset;
            boxes[i] = new Rectangle(currentX,yBoxes,boxWidth,boxHeight);
            itemBox[i] = new Rectangle(currentX + boxWidth /9,yBoxes + boxWidth /9,boxWidth - boxWidth /9 *2,boxWidth - boxWidth /9 *2);
            buyBox[i] = new MyButton("Acheter",currentX ,yBoxes + (boxHeight - boxHeight/5),boxWidth,boxHeight/5,borderColor, TextColor);
        }
    }

    public void draw(Graphics2D g2){

        super.draw(g2);

        String text = String.valueOf(menu.getJoueur().getGold()) ;
        int textLenght = (int)g2.getFontMetrics().getStringBounds(text,g2).getWidth();
        g2.drawImage(icons[1], x + textLenght, y + barHeight,40,40, null);
        g2.drawString(text , x + (int)strokeWidth+1, y + barHeight + barHeight/2);


        drawItems(g2);

        if(hover){
            c[hoverIndex].drawDescription(g2, xMouse, yMouse, (int)(itemBox[hoverIndex].getHeight()), borderColor, fillColor,TextColor);
        }
    }


    public void drawItems(Graphics2D g2){ // méthode qui dessine tous les items en appelant une méthode qui dessine un item
        g2.setColor(borderColor); //met la couleur à border color
        //int ajoutX = width/2;
        for(int i = 0; i<c.length ; i++){
            g2.setColor(borderColor);
            drawItem(g2,i);
        }
    }

    public void drawItem(Graphics2D g2,int i){ //méthode qui dessine un seul item
        //grand rectangle item
        g2.draw(boxes[i]);

        int x = (int) itemBox[i].getX();

        //carré dans lequel se trouve l'item
        c[i].draw(g2, x, yBoxes + boxWidth /9, boxWidth - boxWidth /9 *2);
        g2.draw(itemBox[i]);


        String text = String.valueOf(c[i].getPrixDachat());
        int textLenght = (int)g2.getFontMetrics().getStringBounds(text,g2).getWidth();
        g2.drawString(text, x, yBoxes-10);
        g2.drawImage(icons[1], x+textLenght,yBoxes-28,30,30,null );

        buyBox[i].draw(g2);//dessine le bouton "acheter"

    }


    public void mouseClicked(int x, int y){
        super.mouseClicked(x, y);
        main.getAudio().playCashRegisterSound();
        for (int i = 0; i < c.length;i++){
            if(buyBox[i].getBounds().contains(x,y)){
                if (c[i].getPrixDachat() <=menu.getJoueur().getGold()){
                    menu.getJoueur().setGold(menu.getJoueur().getGold()- c[i].getPrixDachat());
                    buyBox[i].setText("Acheté");
                    //TODO: add what happens when we click on the button to buy
                }
            }
        }
    }

    public void mouseMoved(int x, int y){
        for (int i = 0; i<c.length;i++){
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
 
}
