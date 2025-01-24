package Personnages.Ennemis;
import javax.imageio.ImageIO;

import Jeu.GamePanel;
import Personnages.Personnages;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class Beetle extends Personnages{
    int graphicX;
    int graphicY;

    public Beetle(int nv){
        super("Beetle",false,0+2*nv,2+3*nv,nv,5,10,1,1,1,1,1);
    }

    public Beetle(int nv, GamePanel gp){
        super("Beetle",false,0+2*nv,2+3*nv,nv,5,10,1,1,1,1,1);
        this.gp = gp;
        setDefaultValues();
        getBeetleImage();
    }

    public void getBeetleImage(){
        try {
            up1 = ImageIO.read(getClass().getResourceAsStream("../../Images/Personnages/Ennemis/Beetle/1.png"));
            up2 = ImageIO.read(getClass().getResourceAsStream("../../Images/Personnages/Ennemis/Beetle/2.png"));
            up3 = ImageIO.read(getClass().getResourceAsStream("../../Images/Personnages/Ennemis/Beetle/3.png"));
            up4 = ImageIO.read(getClass().getResourceAsStream("../../Images/Personnages/Ennemis/Beetle/4.png"));
            
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
