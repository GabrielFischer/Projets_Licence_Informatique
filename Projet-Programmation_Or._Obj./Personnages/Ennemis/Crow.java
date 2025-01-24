package Personnages.Ennemis;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import Jeu.GamePanel;
import Personnages.Personnages;

public class Crow extends Personnages{
    int graphicX;
    int graphicY;
    public Crow(int nv){
        super("Crow",false,2+2*nv,2+3*nv,nv,5,10,1,1,1,1,1);
    }

    public Crow(int nv, GamePanel gp){
        super("Crow",false,2+2*nv,2+3*nv,nv,5,10,1,1,1,1,1);
        this.gp = gp;
        this.direction = "up";
        setDefaultValues();
        getCrowImage();
    }

    public void getCrowImage(){
        try {
            up1 = ImageIO.read(getClass().getResourceAsStream("../../Images/Personnages/Ennemis/Crow/1.png"));
            up2 = ImageIO.read(getClass().getResourceAsStream("../../Images/Personnages/Ennemis/Crow/2.png"));
            up3 = ImageIO.read(getClass().getResourceAsStream("../../Images/Personnages/Ennemis/Crow/3.png"));
            up4 = ImageIO.read(getClass().getResourceAsStream("../../Images/Personnages/Ennemis/Crow/4.png"));
            
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
            this.setPv(this.getPv()+3);
            return true;
        }
        return false;
    }

    public void combat(Personnages p){
        double cooldown = this.getAttackSpeed()*1000;
        while (this.attaque(p)){
            System.out.println(this.nom + " à attaqué "+p.nom+". Pv restants : "+p.getPv());
            if (!this.enVie || !p.enVie){
                break;
            }
            try{
                Thread.sleep((long)cooldown);
            }catch (InterruptedException e){}
        }
        if (!p.enVie){
            p.plateau.getTab()[p.y][p.x].p=null;
            System.out.println(p.nom+" est mort");
        }
    }
}
