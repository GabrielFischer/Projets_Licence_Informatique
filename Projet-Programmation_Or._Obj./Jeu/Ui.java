package Jeu;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Ui{
    Graphics2D g2;

    GamePanel gp;
    Font arial_40;

    public int commandNum = 0;
    
    public int xCase = 0;
    public int yCase = 12;

    private static final int CONTINUER_X = 12;
    private static final int CONTINUER_Y = 3;

    private static final int MAX_X = 12;
    private static final int MAX_Y = 11;

    BufferedImage archer;
    BufferedImage mage;
    BufferedImage bomber;
    
    public Ui(GamePanel gp){
        this.gp = gp;

        arial_40 = new Font("Arial",Font.PLAIN,40);
        loadImage();
    }

    public void loadImage(){
        
        try {
            archer = ImageIO.read(getClass().getResourceAsStream("../Images/Tours/Archer/archer1.png"));
            mage = ImageIO.read(getClass().getResourceAsStream("../Images/Tours/Mage/Mage1.png"));
            bomber = ImageIO.read(getClass().getResourceAsStream("../Images/Tours/Bomber/Bomber1.png"));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void draw(Graphics2D g2){
        this.g2 = g2;

        g2.setFont(arial_40);
        g2.setColor(Color.white);

        if(gp.gameState == gp.fileSelectState){
            drawFileSelectScreen();
        }
        if(gp.gameState == gp.playState){
            drawPlayState();
        }    
    }
    public void drawPlayState(){
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN,20));
        

        String intToS = String.valueOf(gp.niveau.p.base);
        String text = "Pv : " + intToS;
        int length = (int)g2.getFontMetrics().getStringBounds(text,g2).getWidth();
        int x = gp.niveau.xSelectTours + gp.tileSize;
        int y = gp.tileSize;
        g2.drawString(text,x,y);

        intToS = String.valueOf(gp.niveau.p.argent);
        text = "Rubis : " + intToS;
        y += gp.tileSize;
        g2.drawString(text,x,y);

        if(!gp.niveau.next){
            if(gp.niveau.caseSelectionnee && !gp.niveau.acheterTours){
                drawAmelioration();
            }else
            if(gp.niveau.acheterTours){
                drawAcheterTours(x,y);            
            }else if(!gp.niveau.acheterTours){
                drawContinuer();
            }
            
        }
        

    }
    private void drawContinuer() {
        String text = "Continuer";
        int y = gp.tileSize * 4;
        g2.drawString(text, gp.niveau.xSelectTours + gp.tileSize, y);
    
        if (xCase == CONTINUER_X && yCase == CONTINUER_Y) {
            g2.drawString(">", gp.niveau.xSelectTours, y);
        } else {
            g2.drawString("<", xCase * gp.tileSize, yCase * gp.tileSize - gp.tileSize / 2);
        }
    }

    private void drawAcheterTours(int x, int y) {
        drawTour(x, y + gp.tileSize, archer, 0);
        drawTour(x, y + 2 * gp.tileSize, mage, 1);
        drawTour(x, y + 3 * gp.tileSize, bomber, 2);
    }
    
    private void drawTour(int x, int y, BufferedImage image, int index) {
        g2.drawImage(image, x, y, gp.tileSize, gp.tileSize, null);

    
        if (gp.ui.yCase == index) {
            g2.drawString(">", x - gp.tileSize / 2, y + gp.tileSize / 2);
        }
    }
    private void drawAmelioration(){
        String text = "Ameliorer";
        int yA = 300;
        g2.drawString(text, gp.niveau.xSelectTours + gp.tileSize, yA);
    
        if (yCase == 0) {
            g2.drawString(">", gp.niveau.xSelectTours, yA);
        } 
        text = "Supprimer";
        yA =400;
        g2.drawString(text, gp.niveau.xSelectTours + gp.tileSize, yA);
    
        if (yCase == 1) {
            g2.drawString(">", gp.niveau.xSelectTours, yA);
        } 
    }
    public void drawFileSelectScreen(){
        
        
        //MENU
        if(!gp.choixNiveau){
            g2.setFont(g2.getFont().deriveFont(Font.PLAIN,48));

            String text = "Campagne";
            int x = getXforCenteredText(text);
            int y = gp.tileSize * 4;
            g2.drawString(text,x,y);

            if(commandNum == 0){
                g2.drawString(">",x-gp.tileSize,y);
            }

            text = "Marathon";
            x = getXforCenteredText(text);
            y += gp.tileSize*4;
            g2.drawString(text,x,y);

            if(commandNum == 1){
                g2.drawString(">",x-gp.tileSize,y);
            }
        }
        else{
            int y = gp.tileSize * 3;
            for(int i = 0;i<gp.niveau.nbNiveaux;i++){
                String iS = String.valueOf(i);
                String text = "Niveau " + iS;
                int x = getXforCenteredText(text);
                g2.drawString(text,x,y);

                if(commandNum == i){
                    g2.drawString(">",x-gp.tileSize,y);
                }

                y += gp.tileSize*2;
           }
        }
    }

    public int getXforCenteredText(String text){
        int length = (int)g2.getFontMetrics().getStringBounds(text,g2).getWidth();
        int x = gp.screenWidth/2 - length/2;
        return x;
    }

}