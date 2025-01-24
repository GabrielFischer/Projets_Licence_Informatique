package Graphics.scenes;

import java.awt.*;
import javax.swing.*;

import main.Main;
import Controller.ui.MyButton;

/* BUT:
*  Classe contenant la fenêtre Guide qui s'affiche au milieu de l'écran lorsque l'on clique sur l'icône depuis le Menu, ou pendant
*  la partie. Il contient les règles de base du jeu.
*/

public class Guide extends MenuMethods{
    private Playing playing;
    private Menu menu;
    private Main main;
    private MyButton[] buttons = new MyButton[7];
    private String[] buttonNames = {"Bases", "Lancer", "Combat", "Spells", "Astuces", "Avancé", "Glossaire"};
    private String text;

    int yBoxes;
    int boxHeight;
    int boxWidth;
    

    private JTextArea textArea;

    private Rectangle close;

    boolean hover = false;
    int xMouse = 0;
    int yMouse = 0;
    int hoverIndex = -1;

    float strokeWidth = 3.0f; // Épaisseur du trait en pixels
    BasicStroke stroke = new BasicStroke(strokeWidth);
    AlphaComposite alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f); //opacité du remplissage (transparant)
    AlphaComposite opaque = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f); //opacité du remplissage (opaque)

    public Guide(Main main){
        super(main, 400, 850, "GUIDE");
        this.main = main;
        this.menu = main.getMenu();
        this.playing = main.getPlaying();
        
        initBoxes();
        initButtons();
    }

    private void initButtons() {
        
        int w = width/7;
        int h = height/10;
        int yOffset = h + 10;
        int xStart = width/24;
        int yStart = height/8 + 5;
        for(int i = 0; i < 7; i++){
            buttons[i] = new MyButton(buttonNames[i], x + xStart, y + yStart + yOffset*i, w, h, borderColor, TextColor, fillColor);
        }
    }
    
    public void initBoxes(){
       
        yBoxes = y + height/2 - height/6;
        boxHeight = height/4 + height/3;
        boxWidth = width/6;
    }

    private void drawTextArea(Graphics2D g2) {
        //text
        g2.setFont(new Font("Courier", Font.BOLD, 16));

        int xStart = x + width / 5;
        int yStart = y + height / 8 + 40;
        int lineHeight = g2.getFontMetrics().getHeight();
        if(text != null) {
            for (String line : text.split("\n")) {
                g2.drawString(line, xStart, yStart += lineHeight);
            }
        }
    }

    public void draw(Graphics2D g2){
        super.draw(g2);

        drawButtons(g2);
        drawTextArea(g2);
    }

    private void drawButtons(Graphics2D g) {
        for(int i = 0; i < 7; i++){
            buttons[i].draw(g);
        }
    }
    

    public Rectangle getClose(){
        return close;
    }

    public int centerRectangleX(int totalWidth, int rectangleWidth) {
        return (totalWidth - rectangleWidth) / 2;
    }

    public void mouseClicked(int x, int y) {
        super.mouseClicked(x, y);
        // Get the text of the clicked button
        main.getAudio().playMouseclickSound();
        String buttonText = "";
        for(int i = 0; i < buttons.length; i++){
            if(buttons[i].getBounds().contains(x, y)){
                buttonText = buttonNames[i];
            }
        }
        // Display different text based on the button clicked
        switch (buttonText) {
            case "Bases":
                text = "Slice&Dice est un jeu au tour par tour dans lequel les actions \n" +
                        "posibles de vos héros sont définies par des dés. Plusieurs types\n" +
                        "de capacité existent. Utilisez les à bon escient, tout en gardant\n" +
                        "en tête vos sorts, vos équipements, et vos améliorations de héros.\n" +
                        "Chaque partie est composée de 12 niveaux, le dernier niveau \n" +
                        "correspondant au boss final.";
                break;
            case "Lancer":
                text = "Cliquez sur le bouton LANCER en bas à gauche pour relancer vos dés.\n" +
                        "Vous disposez de 2 relances à chaque tour.\n" +
                        "Appuyez sur un dé pour le verrouiller pendant que vous relancez les \nautres.\n" +
                        "Appuyez et maintenez le dé pour découvrir ce qu'il fait.\n" +
                        "Appuyez sur le bouton ACCEPTER en bas à droite pour accepter tous\nles dés et continuer.\n" +
                        "Les dés sont automatiquement acceptés après votre dernière relance.";
                break;
            case "Combat":
                text = "Vous pouvez vous protéger des dégâts reçus, mais pas du poison.\n" +
                        "Vous pouvez tuer un ennemi pour annuler son attaque.\n" +
                        "Utilisez des dégâts à distance et des capacités à zone d'effet\npour tuer les ennemis à l'arrière.\n" +
                        "Appuyez longuement sur un héros pour voir qui peut l'attaquer.\n" +
                        "Les héros reviennent avec la moitié de leur santé au prochain \ncombat s'ils meurent.";
                break;
            case "Spells":
                text = "Gagnez du mana à partir de vos dés pour le dépenser en sorts.\n" +
                        "Vous avez toujours du burst disponible.\n" +
                        "Les autres sorts proviennent de vos héros rouges et bleus.\n" +
                        "Vous pouvez également gagner des sorts auprès de certains héros et \nobjets spéciaux.\n" +
                        "Si un héros doté d'un sort est vaincu, vous ne pouvez pas\nlancer ce sort avant le prochain combat.\n" +
                        "Vous pouvez économiser jusqu'à 3 manas entre les tours.";
                break;
            case "Astuces":
                text = "Vous pouvez échanger vos objets après chaque combat.\n" +
                        "Vérifiez attentivement avant d'abandonner les héros mourants. \nN'oubliez pas le Burst.\n" +
                        "Vous pouvez appuyer longuement sur les options basculables pour \ndécouvrir ce qu'elles font.";
                break;
            case "Avancé":
                text = "N'hésitez pas à utiliser différents combos de capacité.\n" +
                        "Prenez en compte les héros qui sont le plus adaptés à votre \n" +
                        "stratégie, et tenez en compte lors des soins et des améliorations.\n" +
                        "N'oubliez pas de créer votre héros personnalisé afin d'appliquer \n" +
                        "au mieux la stratégie que vous envisagez.\n" +
                        "Utilisez les équipements dès que possible, achetez les nouvelles \ncapacités.";
                break;
            case "Glossaire":
                text = "Capacites : les différentes compténces présentes sur les dés des \nentités et utilisables par ces derniers.\n" +
                        "Sorts : capacités utilisables grâce au mana accumulé par le \njoueur pendant la partie.\n" +
                        "Equipements : objets achetables dans la boutique pendant la \npartie, et octroyant des bonus.";
            default:
                break;
        }
    }

    public void mouseMoved(int x, int y) {
        for(MyButton b: buttons){
            b.setMouseOver(false);
        }
        for(MyButton b: buttons){
            if(b.getBounds().contains(x, y)){
                b.setMouseOver(true);
            }
        }
    }

    public void mousePressed(int x, int y) {
        for(MyButton b: buttons){
            if(b.getBounds().contains(x, y)){
                b.setMousePressed(true);
            }
        }
    }

    public void mouseReleased(int x, int y) {
        for(MyButton b: buttons){
            b.resetBooleans();
        }
    }

    public void mouseDragged(int x, int y) {

    }
}

