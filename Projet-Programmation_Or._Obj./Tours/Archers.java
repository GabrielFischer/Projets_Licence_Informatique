package Tours;

import java.awt.Graphics2D;
import java.io.IOException;

import javax.imageio.ImageIO;

import Jeu.GamePanel;
import Jeu.Niveau;

public class Archers extends Tours{
    

    public Archers(Niveau niveau){
        super("Archers",10,5,0,3,4,1,1,false,5);
        this.niveau = niveau;
        this.plateau = niveau.p;
    }
    public Archers(Niveau niveau,GamePanel gp){
        super("Archers",10,5,0,3,4,1,1,false,5);
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
            image = ImageIO.read(getClass().getResourceAsStream("../Images/Tours/Archer/archer1.png"));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public boolean augmenterNv(){
        if(super.augmenterNv()){
            this.augmenterDegatsP((5));
            this.augmenterVitesseAttaque(1);
            return true;
        }
        return false;
    }

    public void Draw(){

    }
}