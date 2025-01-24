package Controller.ui.Custom;

import De.Face;
import De.De;
import Graphics.scenes.Custom.Custom;
import Graphics.scenes.Custom.CustomDicePage;
import Controller.ui.MyButton;

import javax.imageio.ImageIO;
import javax.swing.*;

import Graphics.scenes.Custom.CustomHomePage;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

public class InfoBarDice {
    private CustomDicePage dicePage;
    private int WIDTH = 4 * CustomDicePage.getWIDTH() / 5 - 20;
    private int HEIGHT = 250;
    private int x = 260;  //coordinate starting point
    private int y = CustomHomePage.getHEIGHT() - HEIGHT;    //coordinate starting point
    private int xOffset = 90;

    private int pointsCompetences;
    private MyButton[] select = new MyButton[6];
    private boolean selectActivated;
    private boolean[] selectActivatedIndex = new boolean[6];

    private final Color backgroundColor = new Color(0, 23, 30);
    private final Color fillColor = new Color(0, 43, 58, 232);
    private final Color borderColor = new Color(107, 111, 127);
    private final Color TextColor = new Color(255,252,240);


    private String[] capacites = {"Soin", "Attaque", "Defense", "Magique"};
    private String[] soinNoms = {"Soin", "SoinsAccrus", "HacheSoin", "BouclierSoin"};
    private ArrayList<MyButton> soins = new ArrayList<>();
    private String[] attaqueNoms = {"Arc", "CoupEpee", "HacheSoin", "MortSubite", "Poison"};
    private ArrayList<MyButton> attaques = new ArrayList<>();
    private String[] defenseNoms = {"Bouclier", "BouclierSoin"};
    private ArrayList<MyButton> defenses = new ArrayList<>();
    private String[] magiqueNoms = {"Mana", "Poison", "MortSubite"};
    private ArrayList<MyButton> magiques = new ArrayList<>();

    private String[] capaciteSelect = new String[6];  // met en mémoire l'objet qui est sélectionné
    private int[] nivCapaciteSelect = new int[6]; // garde en mémoire le niveau de scapacités choisies
    private ArrayList<String> selectMemory; //met en mémoire tous les objets précemment choisi
    private De de;
    private ArrayList<Face> faces = new ArrayList<>();

    public InfoBarDice(CustomDicePage dicePage) {
        this.dicePage = dicePage;
        pointsCompetences = dicePage.getPointsCompetences();
        selectMemory = new ArrayList<>();
        selectActivated = false;

        for (int i = 0; i < nivCapaciteSelect.length; i++){
            nivCapaciteSelect[i] = 1;
        }

        initButtons();
    }

    private void initButtons() {
        for (int i = 0; i < soinNoms.length; i++) {
            java.net.URL iconUrl1 = getClass().getClassLoader().getResource("Images/Capacite/" + soinNoms[i] + ".png");
            ImageIcon icon = new ImageIcon(iconUrl1);
            soins.add(i, new MyButton(icon, x + 20 + xOffset * i, y + 20, 70, 70, fillColor, backgroundColor, soinNoms[i]));
        }
        for (int i = 0; i < attaqueNoms.length; i++) {
            java.net.URL iconUrl1 = getClass().getClassLoader().getResource("Images/Capacite/" + attaqueNoms[i] + ".png");
            ImageIcon icon = new ImageIcon(iconUrl1);
            attaques.add(i, new MyButton(icon, x + 20 + xOffset * i, y + 20, 70, 70, fillColor, backgroundColor, attaqueNoms[i]));
        }
        for (int i = 0; i < defenseNoms.length; i++) {
            java.net.URL iconUrl1 = getClass().getClassLoader().getResource("Images/Capacite/" + defenseNoms[i] + ".png");
            ImageIcon icon = new ImageIcon(iconUrl1);
            defenses.add(i, new MyButton(icon, x + 20 + xOffset * i, y + 20, 70, 70, fillColor, backgroundColor, defenseNoms[i]));
        }
        for (int i = 0; i < magiqueNoms.length; i++) {
            java.net.URL iconUrl1 = getClass().getClassLoader().getResource("Images/Capacite/" + magiqueNoms[i] + ".png");
            ImageIcon icon = new ImageIcon(iconUrl1);
            magiques.add(i, new MyButton(icon, x + 20 + xOffset * i, y + 20, 70, 70, fillColor, backgroundColor, magiqueNoms[i]));
        }
        int xOffset2 = WIDTH / select.length;
        for (int i = 0; i < select.length; i++){
            select[i] = new MyButton("SELECT", (x + (xOffset2 - 90) / 2 + i * xOffset )+ i * 67 , y + 205, 90, 30, fillColor, TextColor, backgroundColor);
        }
    }

    public void render(Graphics2D g) {
        g.setColor(borderColor);
        g.drawRect(x, y, WIDTH, HEIGHT);
        if (dicePage.getSideMenu().isDrawSoin()) {
            drawSoin(g);
        }
        else if (dicePage.getSideMenu().isDrawAttaque()) {
            drawAttaque(g);
        }
        else if (dicePage.getSideMenu().isDrawDefense()) {
            drawDefense(g);
        }
        else if (dicePage.getSideMenu().isDrawMagique()) {
            drawMagique(g);
        }
        else{
            drawInfo(g);
            for (int i = 0; i < select.length; i++){
                select[i].draw(g);
            }
        }
    }

    private void drawSoin(Graphics2D g) {
        for (MyButton b : soins) {
            b.draw(g);
        }
    }

    private void drawAttaque(Graphics2D g) {
        for (MyButton b : attaques) {
            b.draw(g);
        }
    }

    private void drawDefense(Graphics2D g) {
        for (MyButton b : defenses) {
            b.draw(g);
        }
    }

    private void drawMagique(Graphics2D g) {
        for (MyButton b : magiques) {
            b.draw(g);
        }
    }

    private void drawInfo(Graphics2D g) {
        int[] sides = {0,1,2,3,4,5};
        for(int i = 0; i < capaciteSelect.length; i++){
            drawInfoItem(g, capaciteSelect[sides[i]], i, String.valueOf(i+1));
        }
    }

    private void drawInfoItem(Graphics2D g, String capaciteSelected, int index, String text) {
        g.setFont(new Font("Courier", Font.BOLD, 18));
        int length = (int)g.getFontMetrics().getStringBounds(text,g).getWidth();

        int rectangleSize = 90;
        int arcSize = 10;
        int xOffset = WIDTH / capaciteSelect.length;
        int rectanglePosition = x + (xOffset - rectangleSize) / 2 + index * xOffset;
        int stringPosition = x + (xOffset - length) / 2 + index * xOffset;

        int imagePadding = 10;
        int imageSize = rectangleSize - 2 * imagePadding;

        g.drawString(text, stringPosition, y + 190);
        g.drawRoundRect(rectanglePosition, y+50, rectangleSize, rectangleSize, arcSize,arcSize);

        if(capaciteSelected != null && capaciteSelected != ""){
            java.net.URL iconUrl = getClass().getClassLoader().getResource("Images/Capacite/" + capaciteSelected + ".png");
            BufferedImage icon;
            try {
                icon = ImageIO.read(iconUrl);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            g.drawImage(icon, rectanglePosition + imagePadding, y+60, imageSize, imageSize, null);
        }
        if(nivCapaciteSelect[index] > 1){
            java.net.URL iconUrl = getClass().getClassLoader().getResource("Images/Icons/niveau" + String.valueOf(nivCapaciteSelect[index]) + ".png");
            BufferedImage icon;
            try {
                icon = ImageIO.read(iconUrl);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            g.drawImage(icon, rectanglePosition + imagePadding, y+60, imageSize, imageSize, null);
        }
    }


/*     private boolean verifCout(int cout){
        if(cout > pointsCompetences){
            return false;
        }
        pointsCompetences -= cout;
        return true;

    } */

    public void mouseClicked(int x, int y) {

        for (int i = 0; i < select.length; i++){
            if (select[i].getBounds().contains(x,y)){
                //fonction pour augmenter le niveau de la capacité
                if (!selectActivated){
                    setThemFalseSelectActivated();
                    selectActivated = true;
                    setSelectActivated(i, true);
                }
                else {
                    selectActivated = false;
                    setThemFalseSelectActivated();
                }
        }
        if (dicePage.getSideMenu().isDrawSoin()) {
            for (MyButton b : soins) {
                if (b.getBounds().contains(x, y)) {
                    selectFace(b);
                }
            }
        } else if (dicePage.getSideMenu().isDrawAttaque()) {
            for (MyButton b : attaques) {
                if (b.getBounds().contains(x, y)) {
                    selectFace(b);
                }
            }
        }
        else if (dicePage.getSideMenu().isDrawDefense()) {
            for (MyButton b : defenses) {
                if (b.getBounds().contains(x, y)) {
                    selectFace(b);
                }
            }
        }
        else if (dicePage.getSideMenu().isDrawMagique()) {
            for (MyButton b : magiques) {
                if (b.getBounds().contains(x, y)) {
                    selectFace(b);
                }
            }
        }
        
        }
    }

    private void selectFace(MyButton b) {
        if(dicePage.getDiceSelected() != -1){
            capaciteSelect[dicePage.getDiceSelected()] = b.getName();
            nivCapaciteSelect[dicePage.getDiceSelected()] = 1;
            selectMemory.add(b.getName());
            dicePage.setDiceSelected(-1); //resets the selected dice value so the button doesn't show up highlighted anymore
        }
    }

    public void mouseMoved(int x, int y) {
        for (MyButton b : soins) {
            b.setMouseOver(false);
        }
        for (MyButton b : attaques) {
            b.setMouseOver(false);
        }
        for (MyButton b : defenses) {
            b.setMouseOver(false);
        }
        for (MyButton b : magiques) {
            b.setMouseOver(false);
        }
        for (MyButton b : select) {
            if (b.getBounds().contains(x, y)) {
                b.setMouseOver(true);
            }
        }
        

        if (dicePage.getSideMenu().isDrawSoin()) {
            for (MyButton b : soins) {
                if (b.getBounds().contains(x, y)) {
                    b.setMouseOver(true);
                }
            }
        } else if (dicePage.getSideMenu().isDrawAttaque()) {
            for (MyButton b : attaques) {
                if (b.getBounds().contains(x, y)) {
                    b.setMouseOver(true);
                }
            }
        }
        else if (dicePage.getSideMenu().isDrawDefense()) {
            for (MyButton b : defenses) {
                if (b.getBounds().contains(x, y)) {
                    b.setMouseOver(true);
                }
            }
        }
        else if (dicePage.getSideMenu().isDrawMagique()) {
            for (MyButton b : magiques) {
                if (b.getBounds().contains(x, y)) {
                    b.setMouseOver(true);
                }
            }
        }
    }

    public void mousePressed(int x, int y) {
        for (MyButton b : select) {
            if (b.getBounds().contains(x, y)) {
                b.setMousePressed(true);
            }
        }
        if (dicePage.getSideMenu().isDrawSoin()) {
            for (MyButton b : soins) {
                if (b.getBounds().contains(x, y)) {
                    b.setMousePressed(true);
                }
            }
        } else if (dicePage.getSideMenu().isDrawAttaque()) {
            for (MyButton b : attaques) {
                if (b.getBounds().contains(x, y)) {
                    b.setMousePressed(true);
                }
            }
        }
        else if (dicePage.getSideMenu().isDrawDefense()) {
            for (MyButton b : defenses) {
                if (b.getBounds().contains(x, y)) {
                    b.setMousePressed(true);
                }
            }
        }
        else if (dicePage.getSideMenu().isDrawMagique()) {
            for (MyButton b : magiques) {
                if (b.getBounds().contains(x, y)) {
                    b.setMousePressed(true);
                }
            }
        }
    }

    public void mouseReleased(int x, int y) {
        for (MyButton b : soins) {
            b.resetBooleans();
        }
        for (MyButton b : attaques) {
            b.resetBooleans();
        }
        for (MyButton b : defenses) {
            b.resetBooleans();
        }
        for (MyButton b : magiques) {
            b.resetBooleans();
        }
        for (MyButton b : select) {
            b.resetBooleans();
        }
        
    }

    public int getY() {
        return this.y;
    }

    public int getHEIGHT(){
        return this.HEIGHT;
    }

    public String[] getCapaciteSelect() {
        return capaciteSelect;
    }
    

    public String[] getSoinList(){
        return soinNoms;
    }
    public String[] getMagieList(){
        return magiqueNoms;
    }
    public String[] getAttaqueList(){
        return attaqueNoms;
    }
    public String[] getDefenseList(){
        return defenseNoms;
    }

    public boolean getSelectActivated(){
        return selectActivated;
    }
    
    public int getSelectActivatedIndex(){
        for (int i = 0; i < selectActivatedIndex.length; i++){
            if (selectActivatedIndex[i]){
                return i;
            }
        }
        return -1;
    }

    public int[] getNiveauCap(){
        return nivCapaciteSelect;
    }

    //setters

    private void setSelectActivated(int index, boolean b){
        selectActivatedIndex[index] = b;
    }
    private void setThemFalseSelectActivated(){
        for (int i = 0; i < selectActivatedIndex.length; i++){
            setSelectActivated(i, false);
        }
    }
}
