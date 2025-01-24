package Graphics.scenes;

import Entites.*;
import main.Main;
import Controller.ui.MyButton;
import Graphics.scenes.Custom.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/* BUT:
*  Classe contenant une interface graphique pour choisir le héros. On peut accéder à cette page depuis le Menu, en cliquant sur
*  l'icône en haut au milieu de l'écran (à droite). Il contient des héros par défaut, ainsi que ceux créés dans le Custom, et nous
*  pouvons choisir ceux que nous voulons utiliser dans le jeu.
*/

public class HeroChoix {
    private Main main;
    private Playing playing;
    private String text;
    int WINDOWWIDTH = 1200;
    int WINDOWHEIGHT = 800;

    public int x;
    public int y;
    public int height = 600;
    public int width = 850;
    public int barHeight = height/12;
    int yBoxes;
    int boxHeight;
    int boxWidth;
    private Image icon;


    private final Color backgroundColor = new Color(0,23,30);
    private final Color fillColor = new Color(0, 43, 58, 232);
    private final Color borderColor = new Color(107,111,127);
    private final Color TextColor = new Color(255,252,240);

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

    private ArrayList<Heros> listeHeros;
    private ArrayList<CustomHero> herocustom;
    private ArrayList<MyButton> buttonsOptions;
    private Heros[] herosChoisis = new Heros[5];
    private int nullCounter = 5;
    private Heros[] defaultHeros = {new Archer(), new Epeiste(), new Mage(), new Tank(), new Guerisseur()};
    private Rectangle[] iconsBounds  = new Rectangle[5];

    public HeroChoix(Main main) {
        this.main = main;
        this.playing = main.getPlaying();

        initListeHeros();
        this.buttonsOptions = new ArrayList<>();

        initXY();
        initIcon();
    }

    private void initListeHeros() {
        listeHeros = new ArrayList<>();
        listeHeros.add(new Archer());
        listeHeros.add(new Epeiste());
        listeHeros.add(new Mage());
        listeHeros.add(new Tank());
        listeHeros.add(new Guerisseur());
    }

    private void initUpdateListeHeros(){  // fonction pour update la liste?

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

        drawMainRect(g2);

        g2.setColor(TextColor);
        g2.setFont(new Font("Courier", Font.BOLD, 20));
        String text = "CHOISIR HERO";
        int length = (int)g2.getFontMetrics().getStringBounds(text,g2).getWidth();
        int height = (int)g2.getFontMetrics().getStringBounds(text, g2).getHeight();
        g2.drawString(text, x + width / 2 - length/2, y + barHeight /2 + height / 2);
        drawTextArea(g2);

        drawHeroRectangles(g2);
        drawHeroOptions(g2);
    }

    private void drawHeroOptions(Graphics2D g2) {
        int xStart = x+350;
        int yStart = y+height/12+60;
        int w = 70;
        int h = 70;
        int xOffset = 100;
        int yOffset = 100;
        int j = listeHeros.size();
        for(int i = 0; i < j; i++){
            java.net.URL iconUrl = getClass().getClassLoader().getResource("Images/Heros/" + listeHeros.get(i).getNomEntite() + ".png");
            ImageIcon iconHero = new ImageIcon(iconUrl);

            if(buttonsOptions.size() <= i){
                buttonsOptions.add(new MyButton(iconHero, xStart + xOffset * (i % 4), yStart + yOffset * (i / 4), w, h, fillColor, backgroundColor, listeHeros.get(i).getNomEntite()));
            }
            else{
                buttonsOptions.set(i, new MyButton(iconHero, xStart + xOffset * (i % 4), yStart + yOffset * (i / 4), w, h, fillColor, backgroundColor, listeHeros.get(i).getNomEntite()));
            }
            buttonsOptions.get(i).draw(g2);
        }
    }

    private void drawHeroRectangles(Graphics2D g2) {
        int xStart = x+20;
        int yStart = y+height/12+40;
        int h = 70;
        int w = 200;
        int yOffset = 90;

        for(int i = 0; i < herosChoisis.length; i++){
            g2.drawRoundRect(xStart, yStart+yOffset*i, w, h, 10, 10);
            g2.drawRoundRect(xStart+5, yStart+yOffset*i+5, 60, 60, 10, 10);

            iconsBounds[i] = new Rectangle(xStart+5, yStart+yOffset*i+5, 60, 60); 

            //to add the hero icon if the hero is chosen
            if(herosChoisis[i] != null){
                java.net.URL iconUrl = getClass().getClassLoader().getResource("Images/Heros/" + herosChoisis[i].getNomEntite() + ".png");
                ImageIcon iconHero = new ImageIcon(iconUrl);
                g2.drawImage(iconHero.getImage(), xStart+10, yStart+yOffset*i+10, 50, 50, null);
                g2.drawString(herosChoisis[i].getNomEntite(), xStart+75, yStart+h/2+yOffset*i);
            }
        }
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

        //dessine l'icone de la croix pour fermer la boutique
        g2.drawImage(icon, x + (width - barHeight), y,barHeight,barHeight, null);
        g2.draw(close);
    }

    public Rectangle getClose(){
        return close;
    }

    public void mouseClicked(int x, int y) {
        for(int i = 0; i < iconsBounds.length; i++){
            if(iconsBounds[i].getBounds().contains(x,y) && herosChoisis[i] != null){
                herosChoisis[i] = null;
                nullCounter++;
                main.getMenu().listeHeros[i] = null;
            }
        }
        for(MyButton b : buttonsOptions) {
            if (b.getBounds().contains(x, y)) {
                selectOption(b);
                break;
            }
        }
    }

    //adds selected option to the list of heros
    private void selectOption(MyButton b) {
        for(int i = 0; i < herosChoisis.length; i++){
            if(herosChoisis[i] == null){
                for(int j = 0; j < listeHeros.size(); j++){
                    if(b.getName().equals(listeHeros.get(j).getNomEntite())){
                        for(int k = 0; k < herosChoisis.length; k++){
                            if(herosChoisis[k] == listeHeros.get(j)){
                                return;
                            }
                        }
                        herosChoisis[i] = listeHeros.get(j);
                        nullCounter--;
                        if(nullCounter == 0){
                            main.getMenu().listeHeros= herosChoisis;
                            
                        }
                        else{
                            main.getMenu().listeHeros[i] = herosChoisis[i];
                       
                        }
                        break;
                    }
                }
                break;
            }
        }
    }

    public void mouseMoved(int x, int y) {

    }

    public void mousePressed(int x, int y) {

    }

    public void mouseReleased(int x, int y) {

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

    public Heros[] getHerosChoisis(){
        return this.herosChoisis;
    }

    public ArrayList<Heros> getListeHeros(){
        return this.listeHeros;
    }
}
