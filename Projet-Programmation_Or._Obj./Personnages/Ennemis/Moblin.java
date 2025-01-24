package Personnages.Ennemis;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;

import javax.imageio.ImageIO;

import Jeu.GamePanel;
import Personnages.Personnages;

public class Moblin extends Personnages{
    public String couleur;

    int graphicX;
    int graphicY;

    public static String[] couleursDispo = {"Niveau 0 inexistant","Orange","Bleu","Noir","Blanc","Or"};

    //Crée un Moblin en fonction de son niveau
    public Moblin(int nv){
        super("Moblin",false,10+10*nv,5+5*nv,nv,5,10,2,1,0,3,3);
        this.couleur = couleursDispo[nv];
    }

    public Moblin(int nv, GamePanel gp){
        super("Moblin",false,10+10*nv,5+5*nv,nv,5,10,2,1,0,3,3);
        this.couleur = couleursDispo[nv];
        this.gp = gp;
        setDefaultValues();
        getMoblinImage();
    }
    public void setDefaultValues(){
        
        numSprite = 1;
        direction = "up";;
        System.out.println(graphicY);
    }
/* 
    public void draw(Graphics2D g2){
        BufferedImage image = null;
        switch(direction){
            case "up":
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
                break;
            case "left":
                if (numSprite == 1){
                    image = left1;
                }
                if (numSprite == 2){
                    image = left2;
                }
                if (numSprite == 3){
                    image = left3;
                }
                if (numSprite == 4){
                    image = left4;
                }
                break;
            case "right":
                if (numSprite == 1){
                    image = right1;
                }
                if (numSprite == 2){
                    image = right2;
                }
                if (numSprite == 3){
                    image = right3;
                }
                if (numSprite == 4){
                    image = right4;
                }
                break;
            case "down":
                if (numSprite == 1){
                    image = down1;
                }
                if (numSprite == 2){
                    image = down2;
                }
                if (numSprite == 3){
                    image = down3;
                }
                if (numSprite == 4){
                    image = down4;
                }
                break;
                
        }
        graphicY = y*gp.tileSize;
        
        graphicX = x*gp.tileSize;
        
        g2.drawImage(image, graphicX, graphicY, gp.tileSize,gp.tileSize,null);
    }*/



    public void getMoblinImage(){
        try {
            up1 = ImageIO.read(getClass().getResourceAsStream("../../Images/Personnages/Ennemis/Moblin/up1.png"));
            up2 = ImageIO.read(getClass().getResourceAsStream("../../Images/Personnages/Ennemis/Moblin/up2.png"));
            up3 = ImageIO.read(getClass().getResourceAsStream("../../Images/Personnages/Ennemis/Moblin/up3.png"));
            up4 = ImageIO.read(getClass().getResourceAsStream("../../Images/Personnages/Ennemis/Moblin/up4.png"));
            right1 = ImageIO.read(getClass().getResourceAsStream("../../Images/Personnages/Ennemis/Moblin/right1.png"));
            right2 = ImageIO.read(getClass().getResourceAsStream("../../Images/Personnages/Ennemis/Moblin/right2.png"));
            right3 = ImageIO.read(getClass().getResourceAsStream("../../Images/Personnages/Ennemis/Moblin/right3.png"));
            right4 = ImageIO.read(getClass().getResourceAsStream("../../Images/Personnages/Ennemis/Moblin/right4.png"));
            left1 = ImageIO.read(getClass().getResourceAsStream("../../Images/Personnages/Ennemis/Moblin/left1.png"));
            left2 = ImageIO.read(getClass().getResourceAsStream("../../Images/Personnages/Ennemis/Moblin/left2.png"));
            left3 = ImageIO.read(getClass().getResourceAsStream("../../Images/Personnages/Ennemis/Moblin/left3.png"));
            left4 = ImageIO.read(getClass().getResourceAsStream("../../Images/Personnages/Ennemis/Moblin/left4.png"));
            down1 = ImageIO.read(getClass().getResourceAsStream("../../Images/Personnages/Ennemis/Moblin/down1.png"));
            down2 = ImageIO.read(getClass().getResourceAsStream("../../Images/Personnages/Ennemis/Moblin/down2.png"));
            down3 = ImageIO.read(getClass().getResourceAsStream("../../Images/Personnages/Ennemis/Moblin/down3.png"));
            down4 = ImageIO.read(getClass().getResourceAsStream("../../Images/Personnages/Ennemis/Moblin/down4.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    

    public boolean levelUp(){
        if (super.levelUp()){
            this.setDegats(this.getDegats()+5);
            this.setPv(this.getPv()+5);
            this.couleur = couleursDispo[this.getNv()];
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

