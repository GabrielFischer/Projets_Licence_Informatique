package Controller.ui.Custom;

import Graphics.scenes.Custom.*;
import main.Main;
import Controller.ui.*;

import javax.imageio.ImageIO;
import java.awt.*;
import javax.swing.*;

public class CustomHelp {
    private Custom custom;
    private Main main;
    private MyButton previous, next;
    private String description;
    private String text;

    //variables pour chaque panel Help des différents Custom

    private int nbPanelHelpHero = 7, nbPanelHelpDice = 4, nbPanelHelpFinal = 4, nbActuel;
    private boolean Hero1,Hero2,Hero3,Hero4,Hero5;

    public int x;
    public int y;
    public int height = 400;
    public int width = 700;
    public int barHeight = height/8;
    int yBoxes;
    int boxHeight;
    int boxWidth;
    private Image icon;

    private MyButton[] boutons;
    private MyButton up, down, left, right, plus, minus, menu, undo, continuer, quitter, suivant;
    private String[] boutonsNames = {"up", "down", "left", "right", "plus", "moins", "menu", "undo", "suivant"};

    private final Color backgroundColor = new Color(0,23,30);
    private final Color fillColor = new Color(0, 43, 58, 232);
    private final Color borderColor = new Color(107,111,127);
    private final Color TextColor = new Color(255,252,240);

    private Rectangle close;

    boolean hover = false;
    int WINDOWWIDTH = 1200;
    int WINDOWHEIGHT = 800;
    int xMouse = 0;
    int yMouse = 0;
    int hoverIndex = -1;

    float strokeWidth = 3.0f; // Épaisseur du trait en pixels
    BasicStroke stroke = new BasicStroke(strokeWidth);
    AlphaComposite alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f); //opacité du remplissage (transparant)
    AlphaComposite opaque = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f); //opacité du remplissage (opaque)


    public CustomHelp(Custom custom){
        this.custom = custom;
        this.main = custom.getMain();
        nbActuel = 1;
        boutons = new MyButton[9];
        initXY();
        initIcon();
        initButtons();
        //Hero1 = Hero2 = Hero3 = Hero4 = Hero5 = false;
        Hero1 = true;
        text = "Bienvenue dans le menu de Customisation!\nCette page d'aide est disponible, afin de vous guider, durant votre avancée\ndans la caractérisation du héros qui sommeille en vous.\nOserez-vous combattre, à l'aide de vos amis bestiaux, le Grand Mage Noir?\nPour continuer, veuillez appuyer sur .";
    }

    private void initButtons() {
        int widthTemp = 30;
        previous = new MyButton("PRÉCÉDENT", x + 3, y + height - 38, 95, 35, fillColor, TextColor, backgroundColor);
        next = new MyButton("SUIVANT", x + width - 93 , y + height - 38, 90, 35, fillColor, TextColor, backgroundColor);
        java.net.URL iconUrl2 = getClass().getClassLoader().getResource("Images/Icons/up.png");
        ImageIcon upIcon = new ImageIcon(iconUrl2);
        java.net.URL iconUrl3 = getClass().getClassLoader().getResource("Images/Icons/down.png");
        ImageIcon downIcon = new ImageIcon(iconUrl3);
        java.net.URL iconUrl4 = getClass().getClassLoader().getResource("Images/Icons/left.png");
        ImageIcon leftIcon = new ImageIcon(iconUrl4);
        java.net.URL iconUrl5 = getClass().getClassLoader().getResource("Images/Icons/right.png");
        ImageIcon rightIcon = new ImageIcon(iconUrl5);
        java.net.URL iconUrl6 = getClass().getClassLoader().getResource("Images/Icons/plus.png");
        ImageIcon plusIcon = new ImageIcon(iconUrl6);
        java.net.URL iconUrl7 = getClass().getClassLoader().getResource("Images/Icons/moins.png");
        ImageIcon minusIcon = new ImageIcon(iconUrl7);
        java.net.URL iconUrl8 = getClass().getClassLoader().getResource("Images/Icons/menu.png");
        ImageIcon menuIcon = new ImageIcon(iconUrl8);

        this.up = new MyButton(upIcon,x + 178 , y + 85, widthTemp, widthTemp, fillColor, TextColor, backgroundColor);
        this.down = new MyButton(downIcon, x + widthTemp + 186, y + 85, widthTemp, widthTemp, fillColor, TextColor, backgroundColor);
        this.left = new MyButton(leftIcon, x + 2 *widthTemp + 192 , y + 85, widthTemp, widthTemp, fillColor, TextColor, backgroundColor);
        this.right = new MyButton(rightIcon, x + 3 * widthTemp + 198, y + 85, widthTemp, widthTemp, fillColor, TextColor, backgroundColor);
        this.plus = new MyButton(plusIcon, x + 5 * widthTemp + 241 , y + 85, widthTemp, widthTemp, fillColor, TextColor, backgroundColor);
        this.minus = new MyButton(minusIcon, x + 4 * widthTemp + 235 , y + 85, widthTemp, widthTemp, fillColor, TextColor, backgroundColor);
        this.undo = new MyButton("UNDO", x + 370 , y + height - 308, 60, 24, fillColor, TextColor, backgroundColor);
        this.menu = new MyButton(menuIcon , x + 168 , y + 85, widthTemp ,widthTemp, fillColor,backgroundColor);
        this.continuer = new MyButton("CONTINUER", x + width - 175 , y + 110, 2 * widthTemp + 30, 24,fillColor, TextColor, backgroundColor);
        this.quitter = new MyButton("QUITTER", x + width - 160 , y + 140 , 2 * widthTemp + 10, 16, fillColor, TextColor, backgroundColor);
        this.suivant = new MyButton("SUIVANT", x + width - 220 , y + height - 118, 2 * widthTemp + 10, 24, fillColor, TextColor, backgroundColor);
    }

    public void initIcon(){
        try {
            this.icon = ImageIO.read(getClass().getResourceAsStream("/Images/Icons/croixRouge.png")); // or /Images/Icons/croixRouge.png
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initXY(){
        x = (WINDOWWIDTH - width) / 2; // Coordonnée x du coin supérieur gauche du rectangle
        y = (WINDOWHEIGHT - height) / 2;
        close = new Rectangle(x + (width - barHeight), y, barHeight, barHeight);

        yBoxes = y + height/2 - height/6;
        boxHeight = height/4 + height/3;
        boxWidth = width/6;
    }

    private void drawTextArea(Graphics2D g2) {
        //text
        g2.setColor(TextColor);
        g2.setFont(new Font("Courier", Font.BOLD, 16));

        int xStart = x + previous.getWidth() - 20;
        int yStart = y + height / 8 + 40;
        int lineHeight = g2.getFontMetrics().getHeight();
        if(text != null) {
            for (String line : text.split("\n")) {
                g2.drawString(line, xStart, yStart += lineHeight);
            }
        }
    }

    public void draw(Graphics2D g2){

        drawMainRect(g2);

        g2.setColor(TextColor);
        g2.setFont(new Font("Courier", Font.BOLD, 20));
        String text = "AIDE";
        int length = (int)g2.getFontMetrics().getStringBounds(text,g2).getWidth();
        int height = (int)g2.getFontMetrics().getStringBounds(text, g2).getHeight();
        g2.drawString(text, x + width / 2 - length/2, y + barHeight /2 + height / 2);
        drawButtons(g2);
        drawTextArea(g2);
        //drawButtonHero(g2);
    }

    private void drawButtons(Graphics2D g) {
        previous.draw(g);
        next.draw(g);
    
        drawButtonCustomHero(g);
        //drawButtonDice();
    }

    private void verifPageCustomHero(){
        if (nbActuel == 3){
            Hero1 = true;
            Hero2 = Hero3 = Hero4 = false;
        }
        else if (nbActuel == 5){
            Hero2 = true;
            Hero3 = Hero1 = Hero4 = false;
        }
        else if (nbActuel == 6){
            Hero3 = true;
            Hero2 = Hero4 = Hero1 = false;
        }
        else if (nbActuel == 7){
            Hero4 = true;
            Hero2 = Hero3 = Hero1 = false;
        }
        else {
            Hero1 = Hero2 = Hero3 = Hero4 = false;
        }
    }

    public void drawButtonCustomHero(Graphics g){
        if (custom.getState().equals("Home")){
            verifPageCustomHero();
            if (Hero1){
                menu.draw(g);
            }
            else if (Hero2){
                up.draw(g);
                down.draw(g);
                left.draw(g);   
                right.draw(g);
                plus.draw(g);
                minus.draw(g);
            }
            else if (Hero3){
                undo.draw(g);
            }
            else if (Hero4){
                continuer.draw(g);
                quitter.draw(g);
            }
        }
    }

    public void drawButtonCustomDice(){

    }

    public void drawMainRect(Graphics2D g2){ // dessine le rectangle principal en incluant la barre du haut avec l'icône de la croix

        //dessine le filtre qui assoombri le fond
        g2.setComposite(alphaComposite);
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, WINDOWWIDTH, WINDOWHEIGHT);

        //Rectangle principal
        //Dessine le fond du rectangle
        g2.setComposite(opaque);
        g2.setColor(backgroundColor);
        g2.fillRect(x,y, width, height);

        //dessine la bordure du rectangle
        g2.setColor(borderColor);
        g2.setStroke(stroke);
        g2.drawRect(x, y, width, height);

        //dessine la barre du haut
        g2.drawRect(x, y, width, barHeight);

        //dessine l'icone de la croix pour fermer le menu
        g2.drawImage(icon, x + (width - barHeight), y,barHeight,barHeight, null);
        g2.draw(close);
    }

    public Rectangle getClose(){
        return close;
    }

    public int centerRectangleX(int totalWidth, int rectangleWidth) {
        return (totalWidth - rectangleWidth) / 2;
    }

    public void mouseClicked(int x, int y) {
        // Get the text of the clicked button
        main.getAudio().playMouseclickSound();
        if (previous.getBounds().contains(x, y)){
            if (nbActuel - 1 > 0){
                nbActuel--;
                textContent();
            }
        }
        else if (next.getBounds().contains(x, y)){
            if (custom.getState().equals("Home")){
                if (nbActuel + 1 <= nbPanelHelpHero){
                    nbActuel++;
                    textContent();
                }
            }
            else if (custom.getState().equals("Dice")){
                if (nbActuel + 1 <= nbPanelHelpDice){
                    nbActuel++;
                    textContent();
                }
            }
            else if (custom.getState().equals("Final")){
                if (nbActuel + 1 <= nbPanelHelpFinal){
                    nbActuel++;
                    textContent();
                }
            }

        }
        // Displays different text based on the button clicked and the custom page
    }

    private void textContent(){
        if (custom.getState().equals("Home")){
            switch(nbActuel){
                case 1:
                    text = "Bienvenue dans le menu de Customisation!\nCette page d'aide est disponible, afin de vous guider, durant votre avancée\ndans la caractérisation du héros qui sommeille en vous.\nOserez-vous combattre, à l'aide de vos amis bestiaux, le Grand Mage Noir?\nPour continuer, veuillez appuyer sur  "; 
                    break;
                case 2:
                    text = "Voici donc la page principale, vous avez ainsi au centre le cadre montrant\nbientôt votre avatar.\nVous avez, sous ce cadre, différents boutons permettant de configurer\nles accessoires qui orneront votre plus belle crinière.\nUne barre d'info est présente, en bas de la fenêtre, pour vous permettre\nde le personnaliser au maximum.";
                    break;
                case 3:
                    text = "Le bouton           , situé en haut à droite, vous invite à dérouler\nle menu en appuyant sur le bouton.\nAinsi fait, vous pouvez cliquer sur la catégorie d'objet que vous voulez\nafin de le placer sur votre personnage.";  //insérer le bouton du menu latéral
                    break;
                case 4:
                    text = "Après avoir cliqué sur un des boutons de catégorie, vous avez\ndifférents choix se proposant à vous.\nCliquez sur un objet pour l'appliquer à l'avatar.\nAllez-y!";
                    break;
                case 5:
                    text = "Les boutons                                 et          \nvous permetteront de déplacer et moduler la taille\nde l'accessoire que vous avez choisi.\nPour cela, fermez le menu latéral (si cela n'est pas déjà fait),\net cliquez sur le prop que vous voulez modifier.\nCliquez ensuite sur les boutons de votre choix et *magie*,\nvos désirs deviennent réalité!";  //incruster les boutons directionnels et de grossissement
                    break;
                case 6:
                    text = "Votre allié précieux, le bouton undo!:\nAvec lui, plus de tracas en cas de faux pépin!\nEn appuyant sur ce bouton, vous reviendrez au dernier choix\nque vous avez réalisé.\nAttention cependant, vous ne pouvez revenir en arrière que 10 fois.";  //incruster le bouton undo
                    break;
                case 7:
                    text = "Lorsque vous avez ainsi fini votre héros,\nje vous invite à aller dans le menu latéral, et à appuyer sur \nAu cas où vous voulez quitter la customisation, appuyez sur ";  //incruster continuer et quitter
                    break;
                default: text = "";
            }
        }

        else if (custom.getState().equals("Dice")){
            switch(nbActuel){
                case 1:
                    text = "";
                    break;
                case 2:
                    text = "";
                    break;
                case 3:
                    text = "";
                    break;
                case 4:
                    text = "";
                    break;
                default: text = "";
            }
        }

        else if(custom.getState().equals("Final")){
            switch(nbActuel){
                case 1:
                    text = "";
                    break;
                case 2:
                    text = "";
                    break;
                case 3:
                    text = "";
                    break;
                case 4:
                    text = "";
                    break;
                default: text = "";
            }
        }

        else{
            text = "";
        }
    }

    public void mouseMoved(int x, int y) {
        previous.setMouseOver(false);
        next.setMouseOver(false);
        if(previous.getBounds().contains(x, y)){
            previous.setMouseOver(true);
        }
        if(next.getBounds().contains(x, y)){
            next.setMouseOver(true);
        }
    }

    public void mousePressed(int x, int y) {
        if(previous.getBounds().contains(x, y)){
            previous.setMousePressed(true);
        }
        if(next.getBounds().contains(x, y)){
            next.setMousePressed(true);
        }
    }

    public void mouseReleased(int x, int y) {
        previous.resetBooleans();  
        next.resetBooleans();
    }

    public void mouseDragged(int x, int y) {

    }
    public Color getFillColor(){
        return this.fillColor;
    }
    public Color getTextColor(){
        return this.TextColor;
    }
    public Color getborderColor(){
        return this.borderColor;
    }
    
}
