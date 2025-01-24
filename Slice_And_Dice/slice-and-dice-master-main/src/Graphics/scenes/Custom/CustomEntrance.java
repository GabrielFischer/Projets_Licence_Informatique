package Graphics.scenes.Custom;

import Graphics.scenes.SceneMethodes;
import main.EtatsJeu;
import Controller.ui.*;
import Controller.ui.Custom.*;

import javax.swing.*;
import java.awt.*;

public class CustomEntrance implements SceneMethodes {
    Custom custom;
    private static int WIDTH = 1200;
    private static int HEIGHT = 800;
    private CustomHelp help;
    private SideMenuHero sideMenuHero;
    private InfoBarHome infoBarHome;
    private CustomHeroPanel heroPanel;
    private boolean isOpen = false, helpOpen = false;
    private MyButton buttonHelp;
    private MyButton create, choose, quitter;
    //private MyButton[] boutonsHome;

    private final Color backgroundColor = new Color(0,23,30);
    private final Color fillColor = new Color(0, 43, 58, 232);
    private final Color borderColor = new Color(107,111,127);
    private final Color textColor = new Color(255,252,240);
    float strokeWidth = 3.0f; // Épaisseur du trait en pixels
    BasicStroke stroke = new BasicStroke(strokeWidth);

    public CustomEntrance(Custom custom) {
        this.custom = custom;
        this.help = custom.getCustomHelp();

        initSmallButtons();
        //boutonsHome = new MyButton[8];
        //boutonsHome[0] = up; boutonsHome[1] = down; boutonsHome[2] = left; boutonsHome[3] = right; boutonsHome[4] = plus; boutonsHome[5] = minus; boutonsHome[6] = undo; boutonsHome[7] = sideMenuHero.getButtonMenu();
    }

    //partie vue

    private void initSmallButtons() {
        this.create = new MyButton("Créer", (int)(WIDTH/4.0)-100, HEIGHT-200, 200, 50, fillColor, textColor, backgroundColor);
        this.choose = new MyButton("Choisir", (int)(WIDTH*3/4.0)-100, HEIGHT-200, 200, 50, fillColor, textColor, backgroundColor);
        this.quitter = new MyButton("Quitter", (int)(WIDTH/2.0)-100, HEIGHT-100, 200, 50, fillColor, textColor, backgroundColor);
    }
    

    @Override
    public void render(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(backgroundColor);
        g2d.fillRect(0, 0, WIDTH, HEIGHT);
        g2d.setStroke(stroke);
        String message = "Souhaitez-vous créer votre héros ou en choisir un prédéfini ?";
        g.setColor(Color.WHITE);
        Font font = new Font("Arial", Font.BOLD, 20);
        g.setFont(font);
        FontMetrics fm = g.getFontMetrics();
        int x = (1200 - fm.stringWidth(message)) / 2;
        int y = (800 - fm.getHeight()) / 2 + fm.getAscent();
        g.drawString(message, x, y);
        create.draw(g2d);
        choose.draw(g2d);
        quitter.draw(g2d);
    }

    @Override
    public void mouseClicked(int x, int y) {
        custom.getMain().getAudio().playMouseclickSound();
        if (create.getBounds().contains(x, y)){
            custom.setState("Home");
        }else if(choose.getBounds().contains(x,y)){
            custom.setState("Choose");
        }else if(quitter.getBounds().contains(x,y)){
            EtatsJeu.setEtatJeu(EtatsJeu.MENU);
        }
    }

    @Override
    public void mouseMoved(int x, int y) {
        if (create.getBounds().contains(x, y)){
            create.setMouseOver(true);
        }else if(choose.getBounds().contains(x,y)){
            choose.setMouseOver(true);
        }else if(quitter.getBounds().contains(x,y)){
            quitter.setMouseOver(true);
        }else{
            create.setMouseOver(false);
            choose.setMouseOver(false);
            quitter.setMouseOver(false);
        }
    }

    @Override
    public void mousePressed(int x, int y) {
        if (create.getBounds().contains(x, y)){
            create.setMousePressed(true);
        }else if(choose.getBounds().contains(x,y)){
            choose.setMousePressed(true);
        }else if(quitter.getBounds().contains(x,y)){
            quitter.setMousePressed(true);
        }
    }

    @Override
    public void mouseReleased(int x, int y) {
        create.resetBooleans();
        choose.resetBooleans();
        quitter.resetBooleans();
    }

    @Override
    public void mouseDragged(int x, int y) {

    }


    //setters
    public void setOpen(boolean b){
        this.isOpen = b;
    }
    public boolean getOpen(){
        return this.isOpen;
    }


    //getters
    public static int getWIDTH() {
        return WIDTH;
    }
    public static int getHEIGHT() {
        return HEIGHT;
    }

    public Custom getCustom(){
        return this.custom;
    }
    public InfoBarHome getInfoBar(){
        return this.infoBarHome;
    }

    public SideMenuHero getSideMenu(){
        return this.sideMenuHero;
    }

    public CustomHeroPanel getHeroPanel(){
        return this.heroPanel;
    }

}
