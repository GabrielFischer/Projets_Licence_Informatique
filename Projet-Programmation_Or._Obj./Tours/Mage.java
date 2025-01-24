package Tours;

import java.awt.Graphics2D;
import java.io.IOException;

import javax.imageio.ImageIO;

import Jeu.GamePanel;
import Jeu.Niveau;

public class Mage extends Tours{
    public Mage(Niveau niveau){
        super("Mage",15,0,5,3,3,1,1,false,3);
        this.niveau = niveau;
        this.plateau = niveau.p;
        importImage();
    }
    public Mage(Niveau niveau,GamePanel gp){
        super("Mage",15,0,5,3,3,1,1,false,3);
        this.niveau = niveau;
        this.plateau = niveau.p;
        this.gp = gp;
        importImage();   
    }
    public void draw(Graphics2D g2){
        graphicY = y*gp.tileSize;
        graphicX = x*gp.tileSize;
        g2.drawImage(image,  graphicX, graphicY, gp.tileSize,gp.tileSize,null);
    }
    public void importImage(){
        try {
            image = ImageIO.read(getClass().getResourceAsStream("../Images/Tours/Mage/Mage1.png"));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public boolean augmenterNv(){
        if(super.augmenterNv()){
            this.augmenterDegatsM((5));
            this.augmenterVitesseAttaque(1);
            return true;
        }
        return false;
    }
}