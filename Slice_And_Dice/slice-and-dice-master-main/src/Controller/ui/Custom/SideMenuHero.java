package Controller.ui.Custom;

import main.EtatsJeu;
import Graphics.scenes.Custom.CustomHomePage;
import Controller.ui.MyButton;

import javax.swing.*;
import java.awt.*;

public class SideMenuHero {
    CustomHomePage customHomePage;
    private CustomHelp help;
    private MyButton buttonMenu;
    private MyButton buttonAnimaux;
    private MyButton buttonChapeaux;
    private MyButton buttonArmes;
    private MyButton buttonContinue, buttonExit;
    private String[] buttonNames = {"Animaux", "Chapeaux", "Armes"};
    private boolean drawAnimaux, drawChapeaux, drawArmes = false;
    int x = 0;
    int y = 0;
    int WIDTH = CustomHomePage.getWIDTH() / 5;
    int HEIGHT = CustomHomePage.getHEIGHT();

    private final Color backgroundColor = new Color(0,23,30);
    private final Color fillColor = new Color(0, 43, 58, 232);
    private final Color borderColor = new Color(107,111,127);
    private final Color textColor = new Color(255,252,240);

    public SideMenuHero(CustomHomePage customHomePage){
        this.customHomePage = customHomePage;
        this.help = customHomePage.getHelp();
        initSideButtons();
    }

    private void initSideButtons() {
        java.net.URL iconUrl2 = getClass().getClassLoader().getResource("Images/Icons/menu.png");
        ImageIcon menuIcon = new ImageIcon(iconUrl2);
        this.buttonMenu = new MyButton(menuIcon, 15, 20, 50, 50, fillColor, backgroundColor);
        int yOffset = 60;
        this.buttonAnimaux = new MyButton(buttonNames[0], x+5, y+130+yOffset*0, WIDTH-10, 50, backgroundColor, textColor, fillColor);
        this.buttonChapeaux = new MyButton(buttonNames[1], x+5, y+130+yOffset*1, WIDTH-10, 50, backgroundColor, textColor, fillColor);
        this.buttonArmes = new MyButton(buttonNames[2], x+5, y+130+yOffset*2, WIDTH-10, 50, backgroundColor, textColor, fillColor);
        this.buttonExit = new MyButton("QUITTER", x+5, y+HEIGHT-120, WIDTH-10, 50, backgroundColor, textColor, fillColor);
        this.buttonContinue = new MyButton("SUIVANT", x+5, y+HEIGHT-60, WIDTH-10, 50, backgroundColor, textColor, fillColor);
    }

    public void render(Graphics2D g2d) {
        g2d.setColor(borderColor);
        if(customHomePage.getOpen()){
            g2d.drawRect(x, y, WIDTH, HEIGHT);
            g2d.setColor(fillColor);
            g2d.fillRect(x,y,WIDTH,HEIGHT);
            drawButtons(g2d);
            buttonMenu.setX(WIDTH-55);
        }
        else{
            g2d.drawRect(x, y, WIDTH/3, HEIGHT);
            g2d.setColor(fillColor);
            g2d.fillRect(x,y,WIDTH/3,HEIGHT);
            buttonMenu.setX(15);
        }
        buttonMenu.draw(g2d);
    }

    private void drawButtons(Graphics2D g2d){
        buttonAnimaux.draw(g2d);
        buttonChapeaux.draw(g2d);
        buttonArmes.draw(g2d);
        buttonContinue.draw(g2d);
        buttonExit.draw(g2d);
    }

    public boolean contains(int p, int q){ //Checks if the pointer is within the bounds of the side menu
        if(customHomePage.getOpen()){
            Rectangle bounds = new Rectangle(x, y, WIDTH, HEIGHT);
            return bounds.contains(p, q);
        }
        else{
            Rectangle bounds = new Rectangle(x, y, WIDTH/3, HEIGHT);
            return bounds.contains(p, q);
        }
    }

    public void mouseClicked(int x, int y) {
        if (buttonMenu.getBounds().contains(x, y)) {
            if(!customHomePage.getOpen()){
                customHomePage.setOpen(true);
            }
            else if(customHomePage.getOpen()){
                customHomePage.setOpen(false);
                resetBooleans();
            }
        }
        if (buttonAnimaux.getBounds().contains(x, y)) {
            drawAnimaux = true;
            drawChapeaux = false;
            drawArmes = false;
        }
        else if(buttonChapeaux.getBounds().contains(x,y)){
            drawChapeaux = true;
            drawAnimaux = false;
            drawArmes = false;
        }
        else if(buttonArmes.getBounds().contains(x,y)){
            drawArmes = true;
            drawAnimaux = false;
            drawChapeaux = false;
        }
        else if(buttonContinue.getBounds().contains(x,y)) {
            customHomePage.getCustom().setState("Dice");
            customHomePage.setOpen(false);
        }
        else if(buttonExit.getBounds().contains(x,y)){
            customHomePage.setOpen(false);
            resetBooleans();
            //help.resetNbActuel();
            EtatsJeu.setEtatJeu(EtatsJeu.MENU);
        }
    }

    public void mouseMoved(int x, int y) {
        buttonAnimaux.setMouseOver(false);
        buttonChapeaux.setMouseOver(false);
        buttonArmes.setMouseOver(false);
        buttonMenu.setMouseOver(false);
        buttonContinue.setMouseOver(false);
        buttonExit.setMouseOver(false);
        if (buttonMenu.getBounds().contains(x, y)) {
            buttonMenu.setMouseOver(true);
        }
        else if (buttonAnimaux.getBounds().contains(x, y)) {
            buttonAnimaux.setMouseOver(true);
        }
        else if (buttonChapeaux.getBounds().contains(x, y)) {
            buttonChapeaux.setMouseOver(true);
        }
        else if (buttonArmes.getBounds().contains(x, y)) {
            buttonArmes.setMouseOver(true);
        }
        else if (buttonContinue.getBounds().contains(x, y)) {
            buttonContinue.setMouseOver(true);
        }
        else if (buttonExit.getBounds().contains(x, y)) {
            buttonExit.setMouseOver(true);
        }
    }

    public void mousePressed(int x, int y) {
        if (buttonMenu.getBounds().contains(x, y)) {
            buttonMenu.setMousePressed(true);
        }
        else if (buttonAnimaux.getBounds().contains(x, y)) {
            buttonAnimaux.setMousePressed(true);
        }
        else if (buttonChapeaux.getBounds().contains(x, y)) {
            buttonChapeaux.setMousePressed(true);
        }
        else if (buttonChapeaux.getBounds().contains(x, y)) {
            buttonChapeaux.setMousePressed(true);
        }
        else if (buttonContinue.getBounds().contains(x, y)) {
            buttonContinue.setMousePressed(true);
        }
        else if (buttonExit.getBounds().contains(x, y)) {
            buttonExit.setMousePressed(true);
        }
    }

    public void mouseReleased(int x, int y) {
        buttonMenu.resetBooleans();
        buttonAnimaux.resetBooleans();
        buttonChapeaux.resetBooleans();
        buttonArmes.resetBooleans();
        buttonContinue.resetBooleans();
        buttonExit.resetBooleans();
    }

    public void resetBooleans(){
        this.drawAnimaux = false;
        this.drawChapeaux = false;
        this.drawArmes = false;
    }

    public boolean isDrawAnimaux() {
        return drawAnimaux;
    }

    public boolean isDrawChapeaux() {
        return drawChapeaux;
    }

    public boolean isDrawArmes() {
        return drawArmes;
    }

    public int getWIDTH(){
        return WIDTH;
    }

    public MyButton getButtonMenu() { return buttonMenu; }


}
