package Personnages.Ennemis;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import Jeu.GamePanel;
import Personnages.Personnages;

public class Keese extends Personnages{
    int graphicX;
    int graphicY;

    public Keese(int nv){
        super("Keese",false,5,2+5*nv,nv,5,10,1,1,1,1,1);
        this.vole = true; //vole donc pas targettable de tout les alliés
    }

    public Keese(int nv, GamePanel gp){
        super("Keese",false,5,2+5*nv,nv,5,10,1,1,1,1,1);
        this.vole = true; //vole donc pas targettable de tout les alliés
        this.gp = gp;
        setDefaultValues();
        getKeeseImage();
    }

    public void getKeeseImage(){
        try {
            up1 = ImageIO.read(getClass().getResourceAsStream("../../Images/Personnages/Ennemis/Keese/1.png"));
            up2 = ImageIO.read(getClass().getResourceAsStream("../../Images/Personnages/Ennemis/Keese/2.png"));
            up3 = ImageIO.read(getClass().getResourceAsStream("../../Images/Personnages/Ennemis/Keese/3.png"));
            up4 = ImageIO.read(getClass().getResourceAsStream("../../Images/Personnages/Ennemis/Keese/4.png"));
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void draw(Graphics2D g2){
    BufferedImage image = null;
       
        if (numSprite == 1){
            image = up1;
        }
        if (numSprite == 2){
            image = up2;
        }
        if (numSprite == 3){
            image = up3;
        }
        if (numSprite == 4){
            image = up4;
        }
               
           
        graphicY = y*gp.tileSize;
        
        graphicX = x*gp.tileSize;
        
        g2.drawImage(image, graphicX, graphicY, gp.tileSize,gp.tileSize,null);
    }

    public boolean levelUp(){
        if (super.levelUp()){
            this.setDegats(this.getDegats()+2);
            return true;
        }
        return false;
    }

    public void combat(Personnages p){}
    
}
