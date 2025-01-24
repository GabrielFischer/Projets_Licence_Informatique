package Personnages.Ennemis;
import javax.imageio.ImageIO;

import Jeu.GamePanel;
import Personnages.Personnages;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class Bobomb extends Personnages{
    int graphicX;
    int graphicY;
    public Bobomb(int nv){
        super("Bob-omb",false,5+2*nv,5+5*nv,nv,5,10,3,1+(nv/2),2,2,2);
    }

    public Bobomb(int nv,GamePanel gp){
        super("Bob-omb",false,5+2*nv,5+5*nv,nv,5,10,3,1+(nv/2),2,2,2);
        this.gp = gp;
        getBobombImage();
        setDefaultValues();
    }

    public void getBobombImage(){
        try {
            if(direction == "left"){
               left1 = ImageIO.read(getClass().getResourceAsStream("../../Images/Personnages/Ennemis/Bobomb/left1.png"));
               left2 = ImageIO.read(getClass().getResourceAsStream("../../Images/Personnages/Ennemis/Bobomb/left2.png"));
               left3 = ImageIO.read(getClass().getResourceAsStream("../../Images/Personnages/Ennemis/Bobomb/left3.png"));
            }
            else{
               right1 = ImageIO.read(getClass().getResourceAsStream("../../Images/Personnages/Ennemis/Bobomb/right1.png"));
               right2 = ImageIO.read(getClass().getResourceAsStream("../../Images/Personnages/Ennemis/Bobomb/right2.png"));
               right3 = ImageIO.read(getClass().getResourceAsStream("../../Images/Personnages/Ennemis/Bobomb/right3.png"));
                
            }
            
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void draw(Graphics2D g2){

        BufferedImage image = null;
        if(direction == "right"){
            if (numSprite == 1){
                image = right1;
            }
            if (numSprite == 2){
                image = right2;
            }
            if (numSprite == 3){
                image = right3;
            }
        }
        else{
            if (numSprite == 1){
                image = left1;
            }
            if (numSprite == 2){
                image = left2;
            }
            if (numSprite == 3){
                image = left3;
            }
        }

        graphicY = y*gp.tileSize;
        
        graphicX = x*gp.tileSize;
        
        g2.drawImage(image, graphicX, graphicY, gp.tileSize,gp.tileSize,null);
    }

    public void update(){
        
        spriteCounter ++;
        if(spriteCounter>10){
            if (numSprite==3){
                numSprite = 1;
            }
            else{
                numSprite++;
            }
            spriteCounter = 0;
        }
    }

    public boolean levelUp(){
        if (super.levelUp()){
            this.setDegats(this.getDegats()+5);
            this.setPv(this.getPv()+2);
            this.setRange(this.getRange()+this.getNv()/2);
            return true;
        }
        return false;
    }

    //Voir comment faire l'attaque de zone, peut-être directement sur le plateau
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

    public static void main(String[] args) {
        System.out.println(1+(3/2));
    }
}
