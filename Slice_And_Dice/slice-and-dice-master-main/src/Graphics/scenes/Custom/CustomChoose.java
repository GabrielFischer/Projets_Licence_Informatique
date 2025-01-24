package Graphics.scenes.Custom;

import Graphics.scenes.SceneMethodes;
import main.EtatsJeu;
import Controller.ui.*;
import Controller.ui.Custom.*;

import javax.swing.*;
import java.awt.*;

public class CustomChoose implements SceneMethodes {
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

    public CustomChoose(Custom custom) {
        this.custom = custom;
    }

    //partie vue

    private void initSmallButtons() {
        
    }
    

    @Override
    public void render(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(backgroundColor);
        g2d.fillRect(0, 0, WIDTH, HEIGHT);
        g2d.setStroke(stroke);
    }

    @Override
    public void mouseClicked(int x, int y) {
        
    }

    @Override
    public void mouseMoved(int x, int y) {
       
    }

    @Override
    public void mousePressed(int x, int y) {
        
    }

    @Override
    public void mouseReleased(int x, int y) {

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
