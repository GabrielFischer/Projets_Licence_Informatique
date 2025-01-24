package Personnages.Ennemis;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import Jeu.GamePanel;
import Personnages.Personnages;

public class Chuchu extends Personnages{
    int graphicX;
    int graphicY;

    public Chuchu(int nv){
        super("Chuchu",false,5+5*nv,1+1*nv,nv,4,10,3,1,3,1,1);
        if (this.getNv() == 4){
            this.armure = true;
        }
    }

    public Chuchu(int nv,GamePanel gp ){
        super("Chuchu",false,5+5*nv,1+1*nv,nv,4,10,3,1,3,1,1);
        if (this.getNv() == 4){
            this.armure = true;
        }
        this.gp = gp;
        setDefaultValues();
        getChuchuImage();
    }

    public void getChuchuImage(){
        try {
            up1 = ImageIO.read(getClass().getResourceAsStream("../../Images/Personnages/Ennemis/Chuchu/up1.png"));
            up2 = ImageIO.read(getClass().getResourceAsStream("../../Images/Personnages/Ennemis/Chuchu/up2.png"));
            up3 = ImageIO.read(getClass().getResourceAsStream("../../Images/Personnages/Ennemis/Chuchu/up3.png"));
            up4 = ImageIO.read(getClass().getResourceAsStream("../../Images/Personnages/Ennemis/Chuchu/up4.png"));
            right1 = ImageIO.read(getClass().getResourceAsStream("../../Images/Personnages/Ennemis/Chuchu/right1.png"));
            right2 = ImageIO.read(getClass().getResourceAsStream("../../Images/Personnages/Ennemis/Chuchu/right2.png"));
            right3 = ImageIO.read(getClass().getResourceAsStream("../../Images/Personnages/Ennemis/Chuchu/right3.png"));
            right4 = ImageIO.read(getClass().getResourceAsStream("../../Images/Personnages/Ennemis/Chuchu/right4.png"));
            left1 = ImageIO.read(getClass().getResourceAsStream("../../Images/Personnages/Ennemis/Chuchu/left1.png"));
            left2 = ImageIO.read(getClass().getResourceAsStream("../../Images/Personnages/Ennemis/Chuchu/left2.png"));
            left3 = ImageIO.read(getClass().getResourceAsStream("../../Images/Personnages/Ennemis/Chuchu/left3.png"));
            left4 = ImageIO.read(getClass().getResourceAsStream("../../Images/Personnages/Ennemis/Chuchu/left4.png"));
            down1 = ImageIO.read(getClass().getResourceAsStream("../../Images/Personnages/Ennemis/Chuchu/down1.png"));
            down2 = ImageIO.read(getClass().getResourceAsStream("../../Images/Personnages/Ennemis/Chuchu/down2.png"));
            down3 = ImageIO.read(getClass().getResourceAsStream("../../Images/Personnages/Ennemis/Chuchu/down3.png"));
            down4 = ImageIO.read(getClass().getResourceAsStream("../../Images/Personnages/Ennemis/Chuchu/down4.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void draw(Graphics2D g2){

        BufferedImage image = null;
        switch (numSprite) {
            case 1:
                image = up1;
                break;
            case 2:
                image = up2;
                break;
            case 3:
                image = up3;
                break;
            case 4:
                image = up4;
                break;
            case 5:
                image = right1;
                break;
            case 6:
                image = right2;
                break;
            case 7:
                image = right3;
                break;
            case 8:
                image = right4;
                break;
            case 9:
                image = left1;
                break;
            case 10:
                image = left2;
                break;
            case 11:
                image = left3;
                break;
            case 12:
                image = left4;
                break;
            case 13:
                image = down1;
                break;
            case 14:
                image = down2;
                break;
            case 15:
                image = down3;
                break;
            case 16:
                image = down4;
                break;

        }

        graphicY = y*gp.tileSize;
        
        graphicX = x*gp.tileSize;
        
        g2.drawImage(image, graphicX, graphicY, gp.tileSize,gp.tileSize,null);
    }

    public void update(){
        
        spriteCounter ++;
        if(spriteCounter>10){
            if (numSprite==16){
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
            this.setPv(this.getPv()+1);
            return true;
        }
        return false;
    }

    @Override
    public void combat(Personnages p) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'combat'");
    }

    //Chuchu vert aucun effet, bleu ralenti, rouge brûle, rock résiste à tout sauf bombe qui casse son armure après devient vert
}
