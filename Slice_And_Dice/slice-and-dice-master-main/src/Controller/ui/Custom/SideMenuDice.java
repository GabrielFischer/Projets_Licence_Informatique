package Controller.ui.Custom;

import Graphics.scenes.Custom.CustomDicePage;
import Controller.ui.MyButton;

import javax.swing.*;
import java.awt.*;

public class SideMenuDice {
    CustomDicePage customDicePage;
    private MyButton buttonMenu;
    private MyButton buttonSoin, buttonAttaque, buttonDefense, buttonMagique;
    private MyButton buttonContinue, buttonPrevious;
    private String[] buttonNames = {"Soin", "Attaque", "Defense", "Magique"};
    private boolean drawSoin, drawAttaque, drawDefense, drawMagique = false;
    int x = 0;
    int y = 0;
    int WIDTH = CustomDicePage.getWIDTH() / 5;
    int HEIGHT = CustomDicePage.getHEIGHT();

    private final Color backgroundColor = new Color(0,23,30);
    private final Color fillColor = new Color(0, 43, 58, 232);
    private final Color borderColor = new Color(107,111,127);
    private final Color textColor = new Color(255,252,240);

    public SideMenuDice(CustomDicePage customDicePage){
        this.customDicePage = customDicePage;
        initSideButtons();
    }

    private void initSideButtons() {
        java.net.URL iconUrl2 = getClass().getClassLoader().getResource("Images/Icons/menu.png");
        ImageIcon menuIcon = new ImageIcon(iconUrl2);
        this.buttonMenu = new MyButton(menuIcon, 15, 20, 50, 50, fillColor, backgroundColor);
        int yOffset = 60;
        this.buttonSoin = new MyButton(buttonNames[0], x+5, y+130+yOffset*0, WIDTH-10, 50, backgroundColor, textColor, fillColor);
        this.buttonAttaque = new MyButton(buttonNames[1], x+5, y+130+yOffset*1, WIDTH-10, 50, backgroundColor, textColor, fillColor);
        this.buttonDefense = new MyButton(buttonNames[2], x+5, y+130+yOffset*2, WIDTH-10, 50, backgroundColor, textColor, fillColor);
        this.buttonMagique = new MyButton(buttonNames[3], x+5, y+130+yOffset*3, WIDTH-10, 50, backgroundColor, textColor, fillColor);
        this.buttonPrevious = new MyButton("PRECEDENT", x+5, y+HEIGHT-120, WIDTH-10, 50, backgroundColor, textColor, fillColor);
        this.buttonContinue = new MyButton("SUIVANT", x+5, y+HEIGHT-60, WIDTH-10, 50, backgroundColor, textColor, fillColor);
    }

    public void render(Graphics2D g2d) {
        g2d.setColor(borderColor);
        if(customDicePage.getOpen()){
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
        buttonSoin.draw(g2d);
        buttonAttaque.draw(g2d);
        buttonDefense.draw(g2d);
        buttonMagique.draw(g2d);
        buttonContinue.draw(g2d);
        buttonPrevious.draw(g2d);
    }

    public boolean contains(int p, int q){ //Checks if the pointer is within the bounds of the side menu
        if(customDicePage.getOpen()){
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
            if(!customDicePage.getOpen()){
                customDicePage.setOpen(true);
            }
            else if(customDicePage.getOpen()){
                customDicePage.setOpen(false);
                resetBooleans();
            }
        }
        if (buttonSoin.getBounds().contains(x, y)) {
            drawSoin = true;
            drawAttaque = false;
            drawDefense = false;
            drawMagique = false;
        }
        else if(buttonAttaque.getBounds().contains(x,y)){
            drawAttaque = true;
            drawSoin = false;
            drawDefense = false;
            drawMagique = false;
        }
        else if(buttonDefense.getBounds().contains(x,y)){
            drawDefense = true;
            drawSoin = false;
            drawAttaque = false;
            drawMagique = false;
        }
        else if(buttonMagique.getBounds().contains(x,y)){
            drawDefense = false;
            drawSoin = false;
            drawAttaque = false;
            drawMagique = true;
        }
        else if(buttonContinue.getBounds().contains(x,y) && customDicePage.verifDeNonVide()) {
            customDicePage.fabriqueDeCustom();
            customDicePage.getCustom().setState("Name");
            customDicePage.getCustom().getHomePage().getHeroPanel().initXY(CustomDicePage.getWIDTH()/2-customDicePage.getCustom().getHomePage().getHeroPanel().getHeightWidth()/2, 200);
            customDicePage.setOpen(false);
        }
        else if(buttonPrevious.getBounds().contains(x,y)){
            customDicePage.getCustom().setState("Home");
            customDicePage.getCustom().getHomePage().getHeroPanel().initXY(570, 200);
            customDicePage.setOpen(false);
        }
    }

    public void mouseMoved(int x, int y) {
        buttonSoin.setMouseOver(false);
        buttonAttaque.setMouseOver(false);
        buttonDefense.setMouseOver(false);
        buttonMagique.setMouseOver(false);
        buttonMenu.setMouseOver(false);
        buttonContinue.setMouseOver(false);
        buttonPrevious.setMouseOver(false);
        if (buttonMenu.getBounds().contains(x, y)) {
            buttonMenu.setMouseOver(true);
        }
        else if (buttonSoin.getBounds().contains(x, y)) {
            buttonSoin.setMouseOver(true);
        }
        else if (buttonAttaque.getBounds().contains(x, y)) {
            buttonAttaque.setMouseOver(true);
        }
        else if (buttonDefense.getBounds().contains(x, y)) {
            buttonDefense.setMouseOver(true);
        }
        else if (buttonMagique.getBounds().contains(x, y)) {
            buttonMagique.setMouseOver(true);
        }
        else if (buttonContinue.getBounds().contains(x, y)) {
            buttonContinue.setMouseOver(true);
        }
        else if (buttonPrevious.getBounds().contains(x, y)) {
            buttonPrevious.setMouseOver(true);
        }
    }

    public void mousePressed(int x, int y) {
        if (buttonMenu.getBounds().contains(x, y)) {
            buttonMenu.setMousePressed(true);
        }
        else if (buttonSoin.getBounds().contains(x, y)) {
            buttonSoin.setMousePressed(true);
        }
        else if (buttonAttaque.getBounds().contains(x, y)) {
            buttonAttaque.setMousePressed(true);
        }
        else if (buttonDefense.getBounds().contains(x, y)) {
            buttonDefense.setMousePressed(true);
        }
        else if (buttonMagique.getBounds().contains(x, y)) {
            buttonMagique.setMousePressed(true);
        }
        else if (buttonContinue.getBounds().contains(x, y)) {
            buttonContinue.setMousePressed(true);
        }
        else if (buttonPrevious.getBounds().contains(x, y)) {
            buttonPrevious.setMousePressed(true);
        }
    }

    public void mouseReleased(int x, int y) {
        buttonMenu.resetBooleans();
        buttonSoin.resetBooleans();
        buttonAttaque.resetBooleans();
        buttonDefense.resetBooleans();
        buttonMagique.resetBooleans();
        buttonContinue.resetBooleans();
        buttonPrevious.resetBooleans();
    }

    public void resetBooleans(){
        this.drawSoin = false;
        this.drawAttaque = false;
        this.drawDefense = false;
        this.drawMagique = false;
    }

    public boolean isDrawSoin() {
        return drawSoin;
    }

    public boolean isDrawAttaque() {
        return drawAttaque;
    }

    public boolean isDrawDefense() {
        return drawDefense;
    }
    public boolean isDrawMagique() {
        return drawMagique;
    }

    public int getWIDTH(){
        return WIDTH;
    }

    public MyButton getButtonMenu() { return buttonMenu; }

}

