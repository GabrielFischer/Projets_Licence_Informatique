package De;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import De.Capacite.Capacite;


public class Face{
    public Capacite attaque;
    public BufferedImage blank;
    
    public Face(Capacite attaque){
        this.attaque = attaque;
        initBlank();
    }

    public void initBlank(){
        try {
            this.blank = ImageIO.read(getClass().getResourceAsStream("../../res/Images/Icons/blank.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void draw(Graphics2D g2,int x, int y,int size){
        if(attaque!=null){
           attaque.draw(g2, x, y,size); 
        }
        else{
            g2.drawImage(blank, x, y,size,size,null);
        }
        
    }
    public Capacite getCapacite(){
        return attaque;
    }


    public BufferedImage getBlank(){
        return blank;
    }
}