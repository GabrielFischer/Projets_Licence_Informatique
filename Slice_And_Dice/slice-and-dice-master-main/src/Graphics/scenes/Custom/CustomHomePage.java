package Graphics.scenes.Custom;

import Graphics.scenes.SceneMethodes;
import Controller.ui.*;
import Controller.ui.Custom.*;

import javax.swing.*;
import java.awt.*;

public class CustomHomePage implements SceneMethodes {
    Custom custom;
    private static int WIDTH = 1200;
    private static int HEIGHT = 800;
    private CustomHelp help;
    private SideMenuHero sideMenuHero;
    private InfoBarHome infoBarHome;
    private CustomHeroPanel heroPanel;
    private boolean isOpen = false, helpOpen = false;
    private MyButton buttonHelp;
    private MyButton up,down,left,right, undo, plus, minus;
    private boolean select;
    //private MyButton[] boutonsHome;

    private final Color backgroundColor = new Color(0,23,30);
    private final Color fillColor = new Color(0, 43, 58, 232);
    private final Color borderColor = new Color(107,111,127);
    private final Color textColor = new Color(255,252,240);
    float strokeWidth = 3.0f; // Épaisseur du trait en pixels
    BasicStroke stroke = new BasicStroke(strokeWidth);

    public CustomHomePage(Custom custom) {
        this.custom = custom;
        this.sideMenuHero = new SideMenuHero(this);

        this.heroPanel = new CustomHeroPanel(this);

        this.infoBarHome = new InfoBarHome(this);
        this.help = custom.getCustomHelp();

        initSmallButtons();
        //boutonsHome = new MyButton[8];
        //boutonsHome[0] = up; boutonsHome[1] = down; boutonsHome[2] = left; boutonsHome[3] = right; boutonsHome[4] = plus; boutonsHome[5] = minus; boutonsHome[6] = undo; boutonsHome[7] = sideMenuHero.getButtonMenu();
    }

    //partie vue

    private void initSmallButtons() {
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

        int width = 50;
        this.buttonHelp = new MyButton("AIDE", (WIDTH/5 + 30), 20, 100, 50, fillColor, textColor, backgroundColor);
        this.undo = new MyButton("UNDO", WIDTH - 105, infoBarHome.getY()-width, 2*width, width, fillColor,textColor,backgroundColor);
        this.up = new MyButton(upIcon, 260+width + 5, infoBarHome.getY()-width * 2 -5, width, width, fillColor,backgroundColor);
        this.down = new MyButton(downIcon, 260+width+5, infoBarHome.getY()-width, width, width, fillColor,backgroundColor);
        this.left = new MyButton(leftIcon, 260, infoBarHome.getY()-width, width, width, fillColor,backgroundColor);
        this.right = new MyButton(rightIcon, 260+width*2+10, infoBarHome.getY()-width, width, width, fillColor,backgroundColor);
        this.plus = new MyButton(plusIcon, 260+width*3+15, infoBarHome.getY()-width * 2 -5, width, width, fillColor,backgroundColor);
        this.minus = new MyButton(minusIcon, 260+width*3+15, infoBarHome.getY()-width, width, width, fillColor,backgroundColor);
    }

    @Override
    public void render(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(backgroundColor);
        g2d.fillRect(0, 0, WIDTH, HEIGHT);
        g2d.setStroke(stroke);

        undo.draw(g2d);

        if(isOpen){
            buttonHelp.setX(sideMenuHero.getWIDTH());
        }
        else{
            buttonHelp.setX(sideMenuHero.getWIDTH()/3);
        }
        buttonHelp.draw(g2d);
        if (!sideMenuHero.isDrawAnimaux() && !sideMenuHero.isDrawArmes() && !sideMenuHero.isDrawChapeaux()){
            up.draw(g2d);
            down.draw(g2d);
            left.draw(g2d);
            right.draw(g2d);
            minus.draw(g2d);
            plus.draw(g2d);
        }


        heroPanel.draw(g2d);  //dessine le héros au centre

        sideMenuHero.render((Graphics2D) g);
        infoBarHome.render(g2d);

        if (helpOpen){
            //configurer pour le panneau Help
            help.draw(g2d);
        }
    }

    @Override
    public void mouseClicked(int x, int y) {
        custom.getMain().getAudio().playMouseclickSound();
        select = infoBarHome.getSelectActivated();

        if(up.getBounds().contains(x,y)){
            //appel fonction --> augmente la variable
            heroPanel.goingUp(select);
        }
        else if(down.getBounds().contains(x,y)){
            heroPanel.goingDown(select);
        }
        else if(left.getBounds().contains(x,y)){
            heroPanel.goingLeft(select);
        }
        else if(right.getBounds().contains(x,y)){
            heroPanel.goingRight(select);
        }
        else if(plus.getBounds().contains(x,y)){
            heroPanel.plus(select);
        }
        else if(minus.getBounds().contains(x,y)){
            heroPanel.minus(select);
        }
        else if(undo.getBounds().contains(x,y)){
            infoBarHome.undo();
        }


        else if (buttonHelp.getBounds().contains(x, y)){
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
        else if(isOpen && sideMenuHero.contains(x, y)){
            sideMenuHero.mouseClicked(x,y);
        }
        else if(sideMenuHero.getButtonMenu().getBounds().contains(x, y)) {
            sideMenuHero.mouseClicked(x,y);
        }
        else if(x > 260 && y > HEIGHT - 260){
            infoBarHome.mouseClicked(x,y);
        }
    }

    @Override
    public void mouseMoved(int x, int y) {
        buttonHelp.setMouseOver(false);
            up.setMouseOver(false);
            down.setMouseOver(false);
            left.setMouseOver(false);
            right.setMouseOver(false);
            plus.setMouseOver(false);
            minus.setMouseOver(false);
            undo.setMouseOver(false);
        if (buttonHelp.getBounds().contains(x, y)) {
            buttonHelp.setMouseOver(true);
        }

        else if (up.getBounds().contains(x, y)) {
            up.setMouseOver(true);
        }
        else if (down.getBounds().contains(x, y)) {
            down.setMouseOver(true);
        }
        else if (left.getBounds().contains(x, y)) {
            left.setMouseOver(true);
        }
        else if (right.getBounds().contains(x, y)) {
            right.setMouseOver(true);
        }
        else if(plus.getBounds().contains(x,y)){
            plus.setMouseOver(true);
        }
        else if(minus.getBounds().contains(x,y)){
            minus.setMouseOver(true);
        }
        else if(undo.getBounds().contains(x,y)){
            undo.setMouseOver(true);
        }

        else if(helpOpen){
            help.mouseMoved(x, y);
        }
        else if(isOpen && sideMenuHero.contains(x, y)){
            sideMenuHero.mouseMoved(x,y);
        }
        else if(sideMenuHero.getButtonMenu().getBounds().contains(x, y)) {
            sideMenuHero.mouseMoved(x,y);
        }
        else if(x > 260 && y > HEIGHT - 260){
            infoBarHome.mouseMoved(x,y);
        }
    }

    @Override
    public void mousePressed(int x, int y) {
        if (buttonHelp.getBounds().contains(x, y)) {
            buttonHelp.setMousePressed(true);
        }

        else if (up.getBounds().contains(x, y)) {
            up.setMousePressed(true);
        }
        else if (down.getBounds().contains(x, y)) {
            down.setMousePressed(true);
        }
        else if (left.getBounds().contains(x, y)) {
            left.setMousePressed(true);
        }
        else if (right.getBounds().contains(x, y)) {
            right.setMousePressed(true);
        }
        else if(plus.getBounds().contains(x,y)){
            plus.setMousePressed(true);
        }
        else if(minus.getBounds().contains(x,y)){
            minus.setMousePressed(true);
        }
        else if(undo.getBounds().contains(x,y)){
            undo.setMousePressed(true);
        }

        else if(helpOpen){
            help.mousePressed(x, y);
        }
        else if(isOpen && sideMenuHero.contains(x, y)){
            sideMenuHero.mousePressed(x,y);
        }
        else if(sideMenuHero.getButtonMenu().getBounds().contains(x, y)) {
            sideMenuHero.mousePressed(x,y);
        }
        else if(x > 260 && y > HEIGHT - 260){
            infoBarHome.mousePressed(x,y);
        }
    }

    @Override
    public void mouseReleased(int x, int y) {
        buttonHelp.resetBooleans();
        up.resetBooleans();
        down.resetBooleans();
        left.resetBooleans();
        right.resetBooleans();
        plus.resetBooleans();
        minus.resetBooleans();
        undo.resetBooleans();

        if(isOpen && sideMenuHero.contains(x, y)){
            sideMenuHero.mouseReleased(x,y);
        }
        else if(helpOpen){
            help.mouseReleased(x, y);
        }
        else if(sideMenuHero.getButtonMenu().getBounds().contains(x, y)) {
            sideMenuHero.mouseReleased(x,y);
        }
        else if(x > 260 && y > HEIGHT - 260){
            infoBarHome.mouseReleased(x,y);
        }
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

    public CustomHelp getHelp(){
        return this.help;
    }

}
