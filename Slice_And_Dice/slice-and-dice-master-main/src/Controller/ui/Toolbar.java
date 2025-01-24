package Controller.ui;

import main.Main;
import Graphics.scenes.Playing;
import De.*;

import java.awt.*;
import javax.swing.*;

/* BUT
 * Cette classe crée une barre d'outils située dans la partie inférieure gauche de l'écran et contient des dés et des boutons pour
 * lancer des dés.
 */

public class Toolbar {
    private int x,y,width,height;

    private int lancesDisponible;

    //animationDe
    private int xStart, yStart, xEnd, yEnd;
    private double stepX, stepY;
    private Face f;
    private boolean animation = false;
    private int numFrame = 0;
    private MyButton buttonRoll;
    private MyButton buttonDone;
    private MyButton undo;
    private Playing playing;
    private Main main;
    private boolean rollDone =false;
    private boolean rolling = false;
    private int[] diceAngles;
    //private Heros[] listeHeros = {new Archer(), new Epeiste(), new Tank(),new Mage(), new Guerisseur()};
    private BrickDice[] brickDices = new BrickDice[5];

    public Toolbar(int x, int y, int width, int height, Playing playing){
        this.playing = playing;
        this.main = playing.getMain();
        this.lancesDisponible = 2;
        initButtons();
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.diceAngles = new int[playing.getListeHeros().length];
        initBrickDices();
    }

    private void initBrickDices() {
        int x = 20;
        int y = 720;
        int size = 60;
        int xOffset = 70;
        for (int i = 0; i < 5; i++) {
            brickDices[i] = new BrickDice(x + xOffset*i, y, i, size,playing);
        }
    }

    private void initButtons(){
        
        java.net.URL iconUrlUndo = getClass().getClassLoader().getResource("undo.png");
        ImageIcon undoImage = new ImageIcon(iconUrlUndo);
        undo = new MyButton(undoImage,45,650,30,30);
        buttonRoll = new MyButton("LANCER", 85, 650, 100, 30);
        buttonDone = new MyButton("DONE", 195, 650, 100, 30);
        

    }

    public void draw(Graphics g) {
        g.setColor(new Color(0x2A0202));
        Graphics2D g2d = (Graphics2D) g;
        
        g2d.drawRoundRect(x,y,380,height,20,20);
        g2d.fillRoundRect(x,y,380,height,20,20);
        drawButtons(g);
        drawBrickDices(g);
        g2d.setColor(Color.WHITE);

        String text = "lancer de dés disponibles : " + this.lancesDisponible;
        g2d.drawString(text, x+30, y+65);
        
        g.setColor(new Color(0x2A0202));
        

        if(animation){
            //animationDeplacementDe(g, f, xStart, yStart, xEnd, yEnd);
            animationDeplacementDeFrame(g, f, xStart, yStart, xEnd, yEnd, stepX, stepY);
            xStart += stepX;
            yStart += stepY;
            numFrame++;
        }
    }



    private void drawBrickDices(Graphics g) {
        for(BrickDice b : brickDices){
            b.draw(g);
        }
    }

    private void drawButtons(Graphics g){
        buttonRoll.draw(g);
        buttonDone.draw(g);
        undo.draw(g);
    }

    public void rollDices(){
        rolling = true;
        for(BrickDice b : brickDices){
            b.rollDices();
        }
        rolling=false;
    }

    public void mouseClicked(int x, int y) {
        if(!rolling && undo.getBounds().contains(x,y)){
            if(!playing.getCombat().undo.isEmpty()){
                playing.getCombat().undo.pop().undo();
            }
        }
        if(!rolling && buttonRoll.getBounds().contains(x, y)){ // lance les des
            main.getAudio().playDiceRollSound();
            if(lancesDisponible>0){ //vérifie qu'il reste bien deux lancés de dés
                rollDices();
                
                //playing.getCombat().undo.clear();
                //System.out.println(playing.getCombat().undo.size());
                if(lancesDisponible==0&& !rolling){//à la fin du lancé de dés on regarde si il reste encore des lancés de dés sinon toutes les faces deviennent selectionnées
                    for(int i = 0;i<brickDices.length;i++){ 
                        Face f = playing.getListeHeros()[i].getDe().getFaceDessus();
                        playing.getListeHeros()[i].setFaceSelectionnee(f);
                        rollDone=true;
                    }
                }
                lancesDisponible-=1;
            }
            
        }else if(!rolling && buttonDone.getBounds().contains(x, y)){ // attribue tous les dés restants lorsque le boutton done est cliqué
            main.getAudio().playMouseclickSound();
            for(int i = 0;i<brickDices.length;i++){
                Face f = playing.getListeHeros()[i].getDe().getFaceDessus();
                playing.getListeHeros()[i].setFaceSelectionnee(f);
                rollDone=true;
            }
        }
        else{ // si un dés de toolbar est cliqué celui-ci est attribué à son héro 
            for(int i = 0;i<brickDices.length;i++){
                if(brickDices[i].getBounds().contains(x,y)){
                    main.getAudio().playSwipeSound();
                    if(!playing.getListeHeros()[i].aFaceSelectionnee()) {
                        f = playing.getListeHeros()[i].getDe().getFaceDessus();

                        xStart = brickDices[i].getX() + 10;
                        yStart = brickDices[i].getY() + 10;
                        //xEnd = playing.getBrickHeroes()[i].getX();
                        xEnd = 220;
                        yEnd = playing.getBrickHeroes()[i].getY() + 30;
                        stepX = (xEnd - xStart) / (double) 60;
                        stepY = (yEnd - yStart) / (double) 60;
                        //System.out.println("xend" + xEnd + "yend" + yEnd);
                        animation = true;

                        playing.getListeHeros()[i].setFaceSelectionnee(f);
                    }
                }
            }
        }
        
    }

    private void animationDeplacementDeFrame(Graphics g, Face f, int xStart, int yStart, int xEnd, int yEnd, double stepX, double stepY) {
        if(numFrame == 60){
            //BufferedImage image = f.getCapacite().getImage();
            Graphics2D g2d = (Graphics2D) g;
            //System.out.println("x" + xStart + "y" + yStart);
            // g2d.drawImage(image, xEnd, yEnd, 60, 60, null);
            f.draw(g2d, xEnd, yStart, 60);
            numFrame = 0;
            animation = false;
            return;
        }
        else{
            //BufferedImage image = f.getCapacite().getImage();
            Graphics2D g2d = (Graphics2D) g;
            //System.out.println("x" + xStart + "y" + yStart);
            //g2d.drawImage(image, xStart, yStart, 60, 60, null);
            f.draw(g2d, xEnd, yStart, 60);


        }

    }

    //Vérifie que tous les dés ont été attribués
    public boolean rollDone(){
        for (int i=0;i<playing.getListeHeros().length;i++){
            if (playing.getListeHeros()[i].enVie() && playing.getListeHeros()[i].getFaceSelectionnee()==null){
                rollDone=false;
                return false;
            }
        }
        rollDone=true;
        return true;
    }

    public void mouseMoved(int x, int y){
        buttonRoll.setMouseOver(false);
        buttonDone.setMouseOver(false);
        undo.setMouseOver(false);
        if(buttonRoll.getBounds().contains(x,y)){
            buttonRoll.setMouseOver(true);
        }else if(buttonDone.getBounds().contains(x,y)){
            buttonDone.setMouseOver(true);
        }
        else if(undo.getBounds().contains(x,y)){
            undo.setMouseOver(true);
        }
    }

    public void mousePressed(int x, int y){
        buttonRoll.setMousePressed(false);
        buttonDone.setMousePressed(false);
        undo.setMousePressed(false);
        if(buttonRoll.getBounds().contains(x, y)){
            buttonRoll.setMousePressed(true);
        }else if(buttonDone.getBounds().contains(x,y)){
            buttonDone.setMousePressed(true);
        }
        else if(undo.getBounds().contains(x,y)){
            undo.setMousePressed(true);
        }
    }

    public void mouseReleased(int x, int y) {
        resetButtons();
    }

    private void resetButtons(){
        buttonRoll.resetBooleans();
        buttonDone.resetBooleans();
        undo.resetBooleans();
    }

    public BrickDice[] getBrickDice() {
        return this.brickDices;
    }

    public void resetLancesDes(){
        this.lancesDisponible=2;
    }
    public int getLancesDes(){
        return this.lancesDisponible;
    }
    public void setLancerDes(int i){
        this.lancesDisponible = i;
    }
}


