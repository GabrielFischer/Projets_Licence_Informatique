package Graphics.scenes.Custom;

import Graphics.scenes.SceneMethodes;
import Controller.ui.*;
import Controller.ui.Custom.*;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import De.De;
import De.Face;
import De.Capacite.*;
import Entites.Ennemis;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;

public class CustomDicePage implements SceneMethodes {
    private static int WIDTH = 1200;
    private static int HEIGHT = 800;

    private Custom custom;
    private CustomHelp help;
    private SideMenuDice sideMenuDice;
    private InfoBarDice infoBarDice;
    private boolean helpOpen = false;
    private boolean isOpen = false;

    private MyButton buttonHelp;
    private ArrayList<MyButton> diceSelector = new ArrayList<>();
    private int diceSelected = -1;

    private int pointsCompetences, pdv;
    private boolean suppPC;

    private String[] soinNoms, magiqueNoms, attaqueNoms, defenseNoms;

    private final Color backgroundColor = new Color(0,23,30);
    private final Color fillColor = new Color(0, 43, 58, 232);
    private final Color borderColor = new Color(107,111,127);
    private final Color textColor = new Color(255,252,240);
    float strokeWidth = 3.0f; // Épaisseur du trait en pixels
    BasicStroke stroke = new BasicStroke(strokeWidth);

    private De decustom;
    private Capacite[] capacitesCustom;
    private int[] nivCap;
    private MyButton plus, minus;
    private int pvTotaux;


    public CustomDicePage(Custom custom) {
        this.custom = custom;
        this.help = custom.getCustomHelp();
        this.sideMenuDice = new SideMenuDice(this);
        this.infoBarDice = new InfoBarDice(this);

        this.soinNoms = infoBarDice.getSoinList();
        this.magiqueNoms = infoBarDice.getMagieList();
        this.attaqueNoms = infoBarDice.getAttaqueList();
        this.defenseNoms = infoBarDice.getDefenseList();

        capacitesCustom = new Capacite[6];
        nivCap = infoBarDice.getNiveauCap();
        pointsCompetences = 20;
        pvTotaux = 10;
        suppPC = true;

        initSmallButtons();
        initDiceSelector();
    }

    //partie vue

    private void initSmallButtons() {
        this.buttonHelp = new MyButton("AIDE", (WIDTH/5 + 30), 20, 100, 50, fillColor, textColor, backgroundColor);
        java.net.URL iconUrl6 = getClass().getClassLoader().getResource("Images/Icons/plus.png");
        ImageIcon plusIcon = new ImageIcon(iconUrl6);
        java.net.URL iconUrl7 = getClass().getClassLoader().getResource("Images/Icons/moins.png");
        ImageIcon minusIcon = new ImageIcon(iconUrl7);

        int width = 50;
        this.plus = new MyButton(plusIcon, 930, 400, width, width, fillColor,backgroundColor);
        this.minus = new MyButton(minusIcon, 930 + width + 20, 400, width, width, fillColor,backgroundColor);
    }

    @Override
    public void render(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(backgroundColor);
        g2d.fillRect(0, 0, WIDTH, HEIGHT);
        g2d.setStroke(stroke);

        drawButtons(g);
        drawDice(g);
        drawPermTextArea(g2d);

        if (!infoBarDice.getSelectActivated()){
            drawPdvChoice(g2d);
            drawTextPdvArea(g2d);
        }
        else{
            drawTextNiveauCapArea(g2d, infoBarDice.getSelectActivatedIndex());
        }

        sideMenuDice.render(g2d);
        infoBarDice.render(g2d);

        if(isOpen){
            buttonHelp.setX(sideMenuDice.getWIDTH());
        }
        else{
            buttonHelp.setX(sideMenuDice.getWIDTH()/3);
        }

        if (helpOpen){
            //configurer pour le panneau Help
            help.draw(g2d);
        }
    }

    private void drawPdvChoice(Graphics g){
        Graphics2D g2d = (Graphics2D) g;
        int x = 880;
        int y = 185;


        // ----partie pdv draw------


        int width = 30;
        int spacing = 10;
        int coeursParLigne = 5;
        int nbLignes = Math.min(pvTotaux / coeursParLigne + 1, 3); // 3 lignes max

          //position de départ
        int startX = x + 10;
        int startY = y + 10;
    
        java.net.URL PVR = getClass().getClassLoader().getResource("PVRouge.png");
        java.net.URL Plus = getClass().getClassLoader().getResource("plus.jpg");
        ImageIcon iconPVR = new ImageIcon(PVR);
        ImageIcon iconPlus = new ImageIcon(Plus);

        for (int i = 0; i < nbLignes; i++) {
            int nbCoeurs = Math.min(coeursParLigne, pvTotaux - i * coeursParLigne);
            for (int j = 0; j < nbCoeurs; j++) {
                    g.drawImage(iconPVR.getImage(), startX + (width + spacing) * j, startY + (width) * i, width, width, null);
            }
            if (i == nbLignes - 1 && pvTotaux > coeursParLigne * (i + 1)) {
                g.drawImage(iconPlus.getImage(), startX + (width + spacing) * (coeursParLigne - 1), startY + (width) * i, width, width, null);
            }
        }


        //-----partie rectangle draw----

        int rectangleSize = 230;
        int rectanglewidth = nbLignes * 40;
        int arcSize = 10;
        g.drawRoundRect(x, y, rectangleSize, rectanglewidth, arcSize, arcSize);

    }

    private void drawDice(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        int xStart = WIDTH/2 - 2*90;
        int yStart = (HEIGHT - infoBarDice.getHEIGHT())/2;
        int xOffset = 90;
        int imagePadding = 10;

        for(int i = 0; i < 4; i++){
            g.drawRect(xStart + xOffset*i, yStart, 90, 90);
            drawIcon(xStart + xOffset*i, yStart,70, 70, imagePadding, g2d, i);
        }
        g.drawRect(xStart + xOffset, yStart-90, 90, 90);
        drawIcon(xStart + xOffset, yStart - 90,70, 70, imagePadding, g2d, 4);

        g.drawRect(xStart + xOffset, yStart+90, 90, 90);
        drawIcon(xStart + xOffset, yStart + 90,70, 70, imagePadding, g2d, 5);
    }

    private void drawIcon(int x, int y, int w, int h, int padding, Graphics2D g2d, int i){
        String faceSelected = infoBarDice.getCapaciteSelect()[i];

        if(faceSelected != null){
            java.net.URL iconUrl = getClass().getClassLoader().getResource("Images/Capacite/" + faceSelected + ".png");
            BufferedImage icon;
            try {
                icon = ImageIO.read(iconUrl);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            g2d.drawImage(icon, x + padding, y + padding, w, h, null);
        }
        if(nivCap[i] > 1){
            java.net.URL iconUrl = getClass().getClassLoader().getResource("Images/Icons/niveau" + String.valueOf(nivCap[i]) + ".png");
            BufferedImage icon;
            try {
                icon = ImageIO.read(iconUrl);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            g2d.drawImage(icon, x + padding, y + padding, w, h, null);
        }
    }

    private void initDiceSelector(){
        String[] sides = {"1", "2", "3", "4", "5", "6"};
        int xStart = 270;
        int yStart = 460;
        int xOffset = 40;
        for(int i = 0; i < 4; i++){
            diceSelector.add(new MyButton(sides[i], xStart + xOffset* i, yStart, 39, 39, fillColor, textColor, backgroundColor));
        }
        diceSelector.add(new MyButton(sides[4], xStart + xOffset, yStart - 40, 39, 39, fillColor, textColor, backgroundColor));
        diceSelector.add(new MyButton(sides[5], xStart + xOffset, yStart + 40, 39, 39, fillColor, textColor, backgroundColor));
    }

    private void drawButtons(Graphics g) {
        buttonHelp.draw(g);
        minus.draw(g);
        plus.draw(g);


        for(MyButton b : diceSelector){
            b.setButtonColor(new Color(0, 43, 58, 232));
            if(diceSelected == diceSelector.indexOf(b)){
                b.setButtonColor(new Color(144, 0, 0));
            }
            b.draw(g);
        }
    }

    private void drawTextPdvArea(Graphics2D g2) {
        //text
        String text = "Points de vie:" + String.valueOf(pvTotaux) ;
        String text2 = "Vous ne pouvez plus ajouter de\n points de vie.";
        g2.setColor(textColor);
        g2.setFont(new Font("Courier", Font.BOLD, 18));

        int xStart = 930;
        int yStart = 320;
        int yStart2 = 345;
        int lineHeight = g2.getFontMetrics().getHeight();
        if(text != null && text2 != null) {
            for (String line : text.split("\n")) {
                g2.drawString(line, xStart, yStart += lineHeight);
            }
            for (String line : text2.split("\n")) {
                if (!suppPC){
                    g2.drawString(line, xStart - 75, yStart2 += lineHeight);
                }
            }
        }
    }


    private void drawTextNiveauCapArea(Graphics2D g2, int select) {
        //text
        String text = "Niveau:" + String.valueOf(nivCap[select]) ;
        String text2 = "Vous ne pouvez plus incrémenter\n le niveau";
        g2.setColor(textColor);
        g2.setFont(new Font("Courier", Font.BOLD, 18));

        int xStart = 930;
        int yStart = 320;
        int yStart2 = 345;
        int lineHeight = g2.getFontMetrics().getHeight();
        if(text != null && text2 != null) {
            for (String line : text.split("\n")) {
                g2.drawString(line, xStart, yStart += lineHeight);
            }
            for (String line : text2.split("\n")) {
                if (!suppPC){
                    g2.drawString(line, xStart - 75, yStart2 += lineHeight);
                }
            }
        }
    }


    private void drawPermTextArea(Graphics2D g2) {
        //text
        String textPC = "Points de compétences:" + String.valueOf(pointsCompetences);
        g2.setColor(textColor);
        g2.setFont(new Font("Courier", Font.BOLD, 22));

        int xStart = 870;
        if( textPC != null) {
            for (String line : textPC.split("\n")) {
                g2.drawString(line, xStart, 80);
            }
        }
    }

    private void plusPdv(){
        if (pointsCompetences > 0){
            pvTotaux++;
            pointsCompetences--;
            //System.out.println(pointsCompetences);
        }
        else {
            suppPC = false;
        }
        //System.out.println(pvTotaux);
    }

    private void moinsPdv(){
        if (pvTotaux > 1){
            pvTotaux--;
            pointsCompetences++;
            suppPC = true;
        }
    }

    private void incrNiveauCap(int index){
        if (index == -1){
            return;
        }
        if (nivCap[index] < 5 && pointsCompetences > 0){
            nivCap[index]++;
            pointsCompetences--;
        }
        else{
            suppPC = false;
        }
    }

    private void decrNiveauCap(int index){
        if (index == -1){
            return;
        }
        if (nivCap[index] > 1){
            nivCap[index]--;
            pointsCompetences++;
            suppPC = true;
        }
    }

    @Override
    public void mouseClicked(int x, int y) {
        //System.out.println(x);
        //System.out.println(y);
        custom.getMain().getAudio().playMouseclickSound();
        if (buttonHelp.getBounds().contains(x, y)){ //help
            //vers le panneau Help
            if(!helpOpen){
                helpOpen = true;
            }
            else if(helpOpen){
                helpOpen = false;
            }
        }
        else if(helpOpen){
            if(help.getClose().getBounds().contains(x,y)){
                custom.getMain().getAudio().playMouseclickSound();
                helpOpen = false;
            }
            else{
                help.mouseClicked(x, y);
            }
        }
        else if(plus.getBounds().contains(x,y)){
            if (!infoBarDice.getSelectActivated()){  //si on ne sélectionne pas de capacités à augmenter de niveau, on augmente les pv
                plusPdv();
            }
            incrNiveauCap(infoBarDice.getSelectActivatedIndex()); //on augmente le niveau de la capacité

        }
        else if(minus.getBounds().contains(x,y)){
            if (!infoBarDice.getSelectActivated()){
                moinsPdv();   //si on ne sélectionne pas de capacités à diminuer de niveau, on diminue les pv
            }
            decrNiveauCap(infoBarDice.getSelectActivatedIndex());  //on diminue le niveau de la capacité
        }
        else if(isOpen && sideMenuDice.contains(x, y)){ //sidemenu
            sideMenuDice.mouseClicked(x,y);
        }
        else if(sideMenuDice.getButtonMenu().getBounds().contains(x, y)) {
            sideMenuDice.mouseClicked(x,y);
        }
        else if(x > 260 && y > HEIGHT - 260){
            infoBarDice.mouseClicked(x,y);
        }
        else{
            for (MyButton b : diceSelector) { //selection de face
                if (b.getBounds().contains(x, y)) {
                    diceSelected(b);
                }
            }
        }
    }

    private void diceSelected(MyButton b){ 
        if(diceSelected == diceSelector.indexOf(b)){
            diceSelected = -1;
        }
        else {
            diceSelected = diceSelector.indexOf(b);
        }
    }



    //fonction utile pour le custom du dé

    public void fabriqueDeCustom(){ // permet la fabrication du des du custom héros
        String[] faces = infoBarDice.getCapaciteSelect();
        if(faces.length != 6){
            return;
        }
        for (int i = 0; i < faces.length; i++){
            //CoupEpee cp = new CoupEpee(1);
            String nomCapacite = "De.Capacite."+faces[i];
            capacitesCustom[i] = creerCapacite(nomCapacite,nivCap[i]);
        }
        decustom = new De(capacitesCustom[0],capacitesCustom[1],capacitesCustom[2],capacitesCustom[3],capacitesCustom[4],capacitesCustom[5]);
        return;
    }


    public Capacite creerCapacite(String capacite,int niveau) { // fonction qui creer une capacit à partir de son nom et de son niveau
        try {
            // Charge dynamiquement la classe en utilisant son nom
            Class<?> clazz = Class.forName(capacite);
            // Récupère le constructeur avec un paramètre de type int
            Constructor<?> constructor = clazz.getConstructor(int.class);
            // Instancie la classe avec le paramètre et la renvoyer
            return (Capacite) constructor.newInstance(niveau);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean verifDeNonVide(){
        for (int i = 0; i < infoBarDice.getCapaciteSelect().length; i++){
            if (infoBarDice.getCapaciteSelect()[i] == null){
                return false;
            }
        }
        return true;
    }




    @Override
    public void mouseMoved(int x, int y) {
        buttonHelp.setMouseOver(false);
        if (buttonHelp.getBounds().contains(x, y)) {
            buttonHelp.setMouseOver(true);
        }
        else if(helpOpen){
            help.mouseMoved(x, y);
        }
        else if(plus.getBounds().contains(x,y)){
            plus.setMouseOver(true);
        }
        else if(minus.getBounds().contains(x,y)){
            minus.setMouseOver(true);
        }
        else if(isOpen && sideMenuDice.contains(x, y)){
            sideMenuDice.mouseMoved(x,y);
        }
        else if(sideMenuDice.getButtonMenu().getBounds().contains(x, y)) {
            sideMenuDice.mouseMoved(x,y);
        }
        else if(x > 260 && y > HEIGHT - 260){
            infoBarDice.mouseMoved(x,y);
        }
    }

    @Override
    public void mousePressed(int x, int y) {
        if (buttonHelp.getBounds().contains(x, y)) {
            buttonHelp.setMousePressed(true);
        }
        else if(helpOpen){
            help.mousePressed(x, y);
        }
        else if(plus.getBounds().contains(x,y)){
            plus.setMousePressed(true);
        }
        else if(minus.getBounds().contains(x,y)){
            minus.setMousePressed(true);
        }
        else if(isOpen && sideMenuDice.contains(x, y)){
            sideMenuDice.mousePressed(x,y);
        }
        else if(sideMenuDice.getButtonMenu().getBounds().contains(x, y)) {
            sideMenuDice.mousePressed(x,y);
        }
        else if(x > 260 && y > HEIGHT - 260){
            infoBarDice.mousePressed(x,y);
        }
    }

    @Override
    public void mouseReleased(int x, int y) {
        buttonHelp.resetBooleans();
        plus.resetBooleans();
        minus.resetBooleans();
        if(helpOpen){
            help.mouseReleased(x, y);
        }
        if(isOpen && sideMenuDice.contains(x, y)){
            sideMenuDice.mouseReleased(x,y);
        }
        else if(sideMenuDice.getButtonMenu().getBounds().contains(x, y)) {
            sideMenuDice.mouseReleased(x,y);
        }
        else if(x > 260 && y > HEIGHT - 260){
            infoBarDice.mouseReleased(x,y);
        }
    }

    @Override
    public void mouseDragged(int x, int y) {

    }

    //getters and setters
    public void setOpen(boolean b){
        this.isOpen = b;
    }
    public boolean getOpen(){
        return this.isOpen;
    }
    public static int getWIDTH() {
        return WIDTH;
    }
    public static int getHEIGHT() {
        return HEIGHT;
    }

    public InfoBarDice getInfoBar(){
        return this.infoBarDice;
    }
    public Custom getCustom(){
        return this.custom;
    }

    public SideMenuDice getSideMenu() {
        return this.sideMenuDice;
    }

    public int getDiceSelected() {
        return diceSelected;
    }

    public void setDiceSelected(int diceSelected) {
        this.diceSelected = diceSelected;
    }

    public De getDeCustom(){
        return decustom;
    }

    public int getPointsCompetences(){
        return pointsCompetences;
    }

    public int getPvTotaux(){
        return pvTotaux;
    }
}

