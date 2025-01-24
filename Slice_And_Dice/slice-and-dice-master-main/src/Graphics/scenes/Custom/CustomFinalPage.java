package Graphics.scenes.Custom;

import main.EtatsJeu;
import Graphics.scenes.*;
import Controller.ui.*;
import Controller.ui.Custom.*;
import De.*;
import main.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

import static main.EtatsJeu.MENU;

public class CustomFinalPage implements SceneMethodes {
    private static int WIDTH = 1200;
    private static int HEIGHT = 800;

    private Custom custom;
    private CustomHero hero;
    private CustomHelp help;
    private CustomHeroPanel heroPanel;
    private CustomNamePage namepage;
    private CustomDicePage dicePage;

    private HeroChoix herochoix;
    private De deCustom;
    private Main main;
    private String name, classe;

    private boolean helpOpen = false, herosCree;
    private MyButton buttonHelp, buttonHero, buttonDice, buttonName, buttonFinish;


    private final Color backgroundColor = new Color(0,23,30);
    private final Color fillColor = new Color(0, 43, 58, 232);
    private final Color borderColor = new Color(107,111,127);
    private final Color textColor = new Color(255,252,240);

    float strokeWidth = 3.0f; // Épaisseur du trait en pixels
    BasicStroke stroke = new BasicStroke(strokeWidth);
    private String text, textError;


    public CustomFinalPage(Custom custom) {
        this.custom = custom;
        this.main = custom.getMain();
        this.help = custom.getCustomHelp();
        this.heroPanel = custom.getHomePage().getHeroPanel();
        this.namepage = custom.getNamePage();
        this.dicePage = custom.getCustomDice();
        this.herochoix = main.getHeroChoix();
        text = textError = "";

        initSmallButtons();
    }

    //partie vue

    private void initSmallButtons() {
        this.buttonHelp = new MyButton("AIDE", 5, 20, 100, 50, fillColor, textColor, backgroundColor);
        initNavigationButtons();
    }
    private void initNavigationButtons() {
        int xOffset = WIDTH / 4;
        int width = WIDTH / 4 - 5;

        this.buttonHero = new MyButton("HERO", 0, HEIGHT-65, width, 65, fillColor, textColor, backgroundColor);
        this.buttonDice = new MyButton("DE", xOffset, HEIGHT-65, width, 65, fillColor, textColor, backgroundColor);
        this.buttonName = new MyButton("NOM", 2*xOffset, HEIGHT-65, width, 65, fillColor, textColor, backgroundColor);
        this.buttonFinish = new MyButton("FIN", 3*xOffset, HEIGHT-65, width, 65, fillColor, textColor, backgroundColor);
    }

    @Override
    public void render(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(backgroundColor);
        g2d.fillRect(0, 0, WIDTH, HEIGHT);
        g2d.setStroke(stroke);
        heroPanel.draw(g2d);
        drawTextArea(g2d);

        drawButtons(g);
        drawDice(g);

        if (helpOpen){
            //configurer pour le panneau Help
            help.draw(g2d);
        }
    }

    private void drawDice(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        int xStart = WIDTH/2;
        int yStart = HEIGHT/4 + 65;
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
        String faceSelected = custom.getCustomDice().getInfoBar().getCapaciteSelect()[i];

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
    }

    private void drawTextArea(Graphics2D g2) {
        //text
        name = namepage.getName();
        classe = namepage.getClasse();

        text = "Est-ce que " + name + ", un vrai " + classe + ", est le héros élu?";
        
        g2.setColor(textColor);
        g2.setFont(new Font("Courier", Font.BOLD, 20));
        int xStart = 200;
        int yStart = 500;
        int lineHeight = g2.getFontMetrics().getHeight();
        if(text != null) {
            for (String line : text.split("\n")) {
                g2.drawString(line, xStart, yStart += lineHeight);
            }
        }
    }

    private void drawButtons(Graphics g) {
        buttonHelp.draw(g);
        buttonHero.draw(g);
        buttonDice.draw(g);
        buttonName.draw(g);
        buttonFinish.draw(g);
    }

    private boolean createHeroCustom(int pdv, De de, Color couleur, String name, String classe, String imagepath){
        if (imagepath == null || imagepath == ""){
            textError += "erreur de path,";
            return false;
        }
        else if (pdv == 0){
            textError += "erreur de pdv à 0,";
            return false;
        }
        else if (de == null){
            textError += "erreur de Dé nul,";
            return false;
        }
        else if (couleur == null){
            textError += "erreur de couleur nulle,";
            return false;
        }
        else if (name == null || classe == null){
            textError += "erreur de nom ou classe,";
            return false;
        }
        hero = new CustomHero(pdv, de, couleur, name, classe, imagepath);
        herochoix.getListeHeros().add(hero);
        return true;
    }


    @Override
    public void mouseClicked(int x, int y) {
        custom.getMain().getAudio().playMouseclickSound();
        if (buttonHelp.getBounds().contains(x, y)){ //ouvre panneau help
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
        else if(buttonHero.getBounds().contains(x,y)){
            custom.setState("Home");
            heroPanel.initXY(570, 200);
        }
        else if(buttonDice.getBounds().contains(x,y)){
            custom.setState("Dice");
        }
        else if(buttonName.getBounds().contains(x,y)){
            custom.setState("Name");
            heroPanel.initXY(WIDTH/2-heroPanel.getHeightWidth()/2, 200);
        }
        else if(buttonFinish.getBounds().contains(x,y)){ // action lorsque l'on appuie sur le bouton final
            heroPanel.tabPixels(heroPanel.getX(), heroPanel.getY(), name); //enregistre l'image
            deCustom = dicePage.getDeCustom(); //recupère le dé
            herosCree = createHeroCustom(dicePage.getPvTotaux(), deCustom, new Color(100,123,30),namepage.getName(), namepage.getClasse(), heroPanel.getPath());
            reset();
            //EtatsJeu.setEtatJeu(MENU);
            if (herosCree){
                EtatsJeu.setEtatJeu(MENU);
            } 
        }
    }

    @Override
    public void mouseMoved(int x, int y) {
        buttonHelp.setMouseOver(false);
        buttonHero.setMouseOver(false);
        buttonDice.setMouseOver(false);
        buttonName.setMouseOver(false);
        buttonFinish.setMouseOver(false);
        if (buttonHelp.getBounds().contains(x, y)) {
            buttonHelp.setMouseOver(true);
        }
        else if(helpOpen){
            help.mouseMoved(x, y);
        }
        else if(buttonHero.getBounds().contains(x,y)){
            buttonHero.setMouseOver(true);
        }
        else if(buttonDice.getBounds().contains(x,y)){
            buttonDice.setMouseOver(true);
        }
        else if(buttonName.getBounds().contains(x,y)){
            buttonName.setMouseOver(true);
        }
        else if(buttonFinish.getBounds().contains(x,y)){
            buttonFinish.setMouseOver(true);
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
        else if(buttonHero.getBounds().contains(x,y)){
            buttonHero.setMousePressed(true);
        }
        else if(buttonDice.getBounds().contains(x,y)){
            buttonDice.setMousePressed(true);
        }
        else if(buttonName.getBounds().contains(x,y)){
            buttonName.setMousePressed(true);
        }
        else if(buttonFinish.getBounds().contains(x,y)){
            buttonFinish.setMousePressed(true);
        }
    }

    @Override
    public void mouseReleased(int x, int y) {
        buttonHelp.resetBooleans();
        buttonHero.resetBooleans();
        buttonDice.resetBooleans();
        buttonName.resetBooleans();
        buttonFinish.resetBooleans();
        if(helpOpen){
            help.mouseReleased(x, y);
        }
    }

    @Override
    public void mouseDragged(int x, int y) {

    }

    //getters
    public static int getWIDTH() {
        return WIDTH;
    }
    public static int getHEIGHT() {
        return HEIGHT;
    }

    public String getName(){
        return this.name;
    }

    public String getClasse(){
        return this.classe;
    }

    private void reset(){
        
    }

}

