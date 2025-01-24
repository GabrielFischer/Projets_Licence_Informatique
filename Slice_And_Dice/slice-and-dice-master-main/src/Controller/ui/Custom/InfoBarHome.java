package Controller.ui.Custom;

import Graphics.scenes.Custom.Custom;
import Controller.ui.MyButton;

import javax.imageio.ImageIO;
import javax.swing.*;

import Graphics.scenes.Custom.CustomHomePage;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

public class InfoBarHome {
    private CustomHomePage homePage;
    private CustomHeroPanel heroPanel;
    private int WIDTH = 4 * CustomHomePage.getWIDTH() / 5 - 20;
    private int HEIGHT = 250;
    private int x = 260;  //coordinate starting point
    private int xOffset = 90;
    private int y = CustomHomePage.getHEIGHT() - HEIGHT - 10;    //coordinate starting point

    private String[] animauxNoms = {"lion", "rhino", "wolf"};
    private String[] accessoires = {"Animal", "Chapeau", "Arme"};  //names for the accessories, to print at the info bar below the icon
    private ArrayList<MyButton> animaux = new ArrayList<>();
    private String[] chapeauxNoms = {"Chapeau1", "Chapeau2", "Chapeau1_scaled"};
    private ArrayList<MyButton> chapeaux = new ArrayList<>();
    private String[] armesNoms = {"Arme1", "Arme2"};
    private ArrayList<MyButton> armes = new ArrayList<>();

    private String[] accessoireSelect = new String[3];  // met en mémoire l'objet qui est sélectionné, indice 0 pour animal, indice 1 pour chapeaux, indice 2 pour armes
    private CustomArrayList selectMemory; //met en mémoire tous les objets précemment utilisés dans la photo
    private int memoryCount1, memoryCount2, memoryCount3; //comptages de combien il y a de chaque type de restant dans selectMemory

    private MyButton[] select = new MyButton[2];
    private boolean selectActivated;

    private final Color backgroundColor = new Color(0, 23, 30);
    private final Color fillColor = new Color(0, 43, 58, 232);
    private final Color borderColor = new Color(107, 111, 127);
    private final Color TextColor = new Color(255,252,240);

    public InfoBarHome(CustomHomePage homePage) {
        this.homePage = homePage;
        initButtons();
        selectMemory = new CustomArrayList();
        this.heroPanel = homePage.getHeroPanel();
        memoryCount1 = memoryCount2 = memoryCount3 = 0;
        selectActivated = false;

        abortSelect();
        
    }

    private void initButtons() {
        for (int i = 0; i < animauxNoms.length; i++) {
            java.net.URL iconUrl1 = getClass().getClassLoader().getResource("Images/HeroCustom/" + animauxNoms[i] + ".png");
            ImageIcon icon = new ImageIcon(iconUrl1);
            animaux.add(i, new MyButton(icon, x + 20 + xOffset * i, y + 20, 70, 70, fillColor, backgroundColor, animauxNoms[i]));
        }
        for (int i = 0; i < chapeauxNoms.length; i++) {
            java.net.URL iconUrl1 = getClass().getClassLoader().getResource("Images/HeroCustom/" + chapeauxNoms[i] + ".png");
            ImageIcon icon = new ImageIcon(iconUrl1);
            chapeaux.add(i, new MyButton(icon, x + 20 + xOffset * i, y + 20, 70, 70, fillColor, backgroundColor, chapeauxNoms[i]));
        }
        for (int i = 0; i < armesNoms.length; i++) {
            java.net.URL iconUrl1 = getClass().getClassLoader().getResource("Images/HeroCustom/" + armesNoms[i] + ".png");
            ImageIcon icon = new ImageIcon(iconUrl1);
            armes.add(i, new MyButton(icon, x + 20 + xOffset * i, y + 20, 70, 70, fillColor, backgroundColor, armesNoms[i]));
        }
        int xOffset2 = WIDTH / select.length;
        for (int i = 0; i < select.length; i++){
            select[i] = new MyButton("SELECT", 235 * (i+1) + (x + (xOffset2  - 90) / 2 + i * xOffset ) - i * 5 , y + 215, 90, 30, fillColor, TextColor, backgroundColor);
        }
    }

    public void render(Graphics2D g) {
        g.setColor(borderColor);
        g.drawRect(x, y, WIDTH, HEIGHT);
        if (homePage.getSideMenu().isDrawAnimaux()) {
            drawAnimaux(g);
        }
        else if (homePage.getSideMenu().isDrawChapeaux()) {
            drawChapeaux(g);
        }
        else if (homePage.getSideMenu().isDrawArmes()) {
            drawArmes(g);
        }
        else{
            drawInfo(g);
            for (int i = 0; i < select.length; i++){
                select[i].draw(g);
            }
        }
    }

    private void drawInfo(Graphics2D g){
        for(int i = 0; i < accessoires.length; i++){
            drawInfoItem(g, accessoireSelect[i], i, accessoires[i]);
        }
    }

    private void drawInfoItem(Graphics2D g, String accessoireSelected, int index, String text){

        g.setFont(new Font("Courier", Font.BOLD, 18));
        int length = (int)g.getFontMetrics().getStringBounds(text,g).getWidth();

        int rectangleSize = 130;
        int arcSize = 10;
        int xOffset = WIDTH / (accessoires.length);
        int rectanglePosition = x + (xOffset - rectangleSize) / 2 + index * xOffset;
        int stringPosition = x + (xOffset - length) / 2 + index * xOffset;

        int imagePadding = 10;
        int imageSize = rectangleSize - 2 * imagePadding;

        g.drawString(text, stringPosition, y + 210);
        g.drawRoundRect(rectanglePosition, y+50, rectangleSize, rectangleSize, arcSize,arcSize);

        if(accessoireSelected != null && accessoireSelected != ""){
            //System.out.println(accessoireSelected);
            java.net.URL iconUrl = getClass().getClassLoader().getResource("Images/HeroCustom/" + accessoireSelected + ".png");
            BufferedImage icon;
            try {
                icon = ImageIO.read(iconUrl);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            g.drawImage(icon, rectanglePosition + imagePadding, y+60, imageSize, imageSize, null);
        }
    }

    public void undo(){  //fonction qui gère le undo
        compteurSelectMemory();
        selectMemory.print();
        if(selectMemory.size() == 1){
            accessoireSelect[0] = accessoireSelect[1] = accessoireSelect[2] = "";
            selectMemory.remove(selectMemory.size() - 1);
            for (int i = 0; i < 3; i++){
                heroPanel.setSelection(accessoireSelect[i], i);
            }
            return;
        }
        selectMemory.remove(selectMemory.size() - 1);
        String last = selectMemory.get(selectMemory.size() - 1);
        if (appartientClasse(last) == 0){
            if (memoryCount1 > 1){
                accessoireSelect[0] = last;
            }
            else{
                accessoireSelect[0] = "";
                //System.out.println(accessoireSelect[0]);
            }
            heroPanel.setSelection(accessoireSelect[0], 0);
            memoryCount1--;
            return;
        }
        else if (appartientClasse(last) == 1){
            if (memoryCount2 > 1){
                accessoireSelect[1] = last;
            }
            else{
                accessoireSelect[1] = "";
            }
            heroPanel.setSelection(accessoireSelect[1], 1);
            memoryCount2--;
            return;
        }
         else if (appartientClasse(last) == 2){
            if (memoryCount3 > 1){
                accessoireSelect[2] = last;
            }
            else{
                accessoireSelect[2] = "";
            }
            heroPanel.setSelection(accessoireSelect[2], 2);
            memoryCount3--;
            return;
        }
        else if (appartientClasse(last) == 99){
            return; //le nom n'a pas été retrouvé --> erreur de synthaxe ou entrée non valide
        }
        return;
    }


    public int appartientClasse(String s){  //fonction permettant de chercher si le String s appartient dans l'une des listes de props
        for (int i = 0; i < 3; i++){
            if (s == animauxNoms[i]){
                return 0;  //s se trouve dans animaux
            }
        }
        for (int i = 0; i < 2; i++){
            if (s == chapeauxNoms[i]){
                return 1;  //s se trouve dans chapeaux
            }
            if (s == armesNoms[i]){
                return 2;  //s se trouve dans armes
            }
        }
        return 99;  //s n'est pas retrouvé
    }

    private void compteurSelectMemory(){
        for (int i = 0; i < selectMemory.size(); i++){
            if (appartientClasse(selectMemory.get(i)) == 0){
                memoryCount1++;
            }
            else if (appartientClasse(selectMemory.get(i)) == 1){
                memoryCount2++;
            }
             else if (appartientClasse(selectMemory.get(i)) == 2){
                memoryCount3++;
            }
        }

    }

    //classe interne pour modifier le comportement de l'arraylist selectmemory


    private class CustomArrayList extends ArrayList<String> {
        private static final int MAX_SIZE = 20;

        @Override
        public boolean add(String e) {
            if (this.size() >= MAX_SIZE) {
                this.remove(0);
            }
            return super.add(e);
        }

        @Override
        public String remove(int index) {
            if (this.size() == 0) {
                return null;
            }
            return super.remove(index);
        }

        @Override
        public boolean remove(Object o) {
            if (this.size() == 0) {
                return false;
            }
            return super.remove(o);
        }

        public void print(){
            for (int i = 0; i < this.size(); i++){
                //System.out.println(selectMemory.get(i));
            }
        }
    }

    private void drawAnimaux(Graphics2D g) {
        for (MyButton b : animaux) {
            b.draw(g);
        }
    }

    private void drawChapeaux(Graphics2D g) {
        for (MyButton b : chapeaux) {
            b.draw(g);
        }
    }

    private void drawArmes(Graphics2D g) {
        for (MyButton b : armes) {
            b.draw(g);
        }
    }

    public void mouseClicked(int x, int y) {
        if (select[1].getBounds().contains(x,y)){
            if (!selectActivated){
                selectActivated = true;
            }
            else{
                selectActivated = false;
            }
        }
        if (select[0].getBounds().contains(x,y)){
            selectActivated = false;
        }
        if (homePage.getSideMenu().isDrawAnimaux()) {
            for (MyButton b : animaux) {
                if (b.getBounds().contains(x, y)) {
                    accessoireSelect[0] = b.getName();
                    selectMemory.add(b.getName());
                    heroPanel.setSelection(b.getName(), 0);
                }
            }
        } else if (homePage.getSideMenu().isDrawChapeaux()) {
            for (MyButton b : chapeaux) {
                if (b.getBounds().contains(x, y)) {
                    accessoireSelect[1] = b.getName();
                    selectMemory.add(b.getName());
                    heroPanel.setSelection(b.getName(), 1);
                }
            }
        } else if (homePage.getSideMenu().isDrawArmes()) {
            for (MyButton b : armes) {
                if (b.getBounds().contains(x, y)) {
                    accessoireSelect[2] = b.getName();
                    selectMemory.add(b.getName());
                    heroPanel.setSelection(b.getName(), 2);
                }
            }
        }
        }
    

    public void mouseMoved(int x, int y) {

        for (MyButton b : select) {
            if (b.getBounds().contains(x, y)) {
                b.setMouseOver(true);
            }
        }

        for (MyButton b : animaux) {
            b.setMouseOver(false);
        }
        for (MyButton b : chapeaux) {
            b.setMouseOver(false);
        }
        for (MyButton b : armes) {
            b.setMouseOver(false);
        }

        if (homePage.getSideMenu().isDrawAnimaux()) {
            for (MyButton b : animaux) {
                if (b.getBounds().contains(x, y)) {
                    b.setMouseOver(true);
                }
            }
        } else if (homePage.getSideMenu().isDrawChapeaux()) {
            for (MyButton b : chapeaux) {
                if (b.getBounds().contains(x, y)) {
                    b.setMouseOver(true);
                }
            }
        }
        else if (homePage.getSideMenu().isDrawArmes()) {
            for (MyButton b : armes) {
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
        if (homePage.getSideMenu().isDrawAnimaux()) {
            for (MyButton b : animaux) {
                if (b.getBounds().contains(x, y)) {
                    b.setMousePressed(true);
                }
            }
        } else if (homePage.getSideMenu().isDrawChapeaux()) {
            for (MyButton b : chapeaux) {
                if (b.getBounds().contains(x, y)) {
                    b.setMousePressed(true);
                }
            }
        }
        if (homePage.getSideMenu().isDrawArmes()) {
            for (MyButton b : armes) {
                if (b.getBounds().contains(x, y)) {
                    b.setMousePressed(true);
                }
            }
        }
    }

    public void mouseReleased(int x, int y) {

        for (MyButton b : select) {
            b.resetBooleans();
        }
        for (MyButton b : animaux) {
            b.resetBooleans();
        }
        for (MyButton b : chapeaux) {
            b.resetBooleans();
        }
        for (MyButton b : armes) {
            b.resetBooleans();
        }
    }

    public int getY() {
        return this.y;
    }

    public String[] getAccessoiresSelect(){
        return this.accessoireSelect;
    }
    public ArrayList<String> getSelectMemory(){
        return this.selectMemory;
    }

    public boolean getSelectActivated(){
        return selectActivated;
    }


    //setters

    public void abortSelect(){
        for (int i = 0; i < accessoireSelect.length; i++){
            accessoireSelect[i] = "";
        }
        selectMemory.clear();
    }
}
