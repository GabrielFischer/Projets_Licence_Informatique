package Personnages.Ennemis;

import javax.imageio.ImageIO;

import Jeu.GamePanel;
import Personnages.Personnages;

public class Stalfos extends Personnages{
    int graphicX;
    int graphicY;
    public boolean casse; //Voir comment faire pour que le stalfos casse quand il est censé mourir
    //Meurt seulement si il prend x dégats sous la forme cassé

    public Stalfos(int nv){
        super("Stalfos",false,10+5*nv,5+5*nv,nv,5,10,1,1,1,3,3);
    }

    public Stalfos(int nv, GamePanel gp){
        super("Stalfos",false,10+5*nv,5+5*nv,nv,5,10,1,1,1,3,3);
        this.gp = gp;
        setDefaultValues();
        getStalfosImage();

    }
    public void getStalfosImage(){
        try {
            up1 = ImageIO.read(getClass().getResourceAsStream("../../Images/Personnages/Ennemis/Stalfos/up1.png"));
            up2 = ImageIO.read(getClass().getResourceAsStream("../../Images/Personnages/Ennemis/Stalfos/up2.png"));
            up3 = ImageIO.read(getClass().getResourceAsStream("../../Images/Personnages/Ennemis/Stalfos/up3.png"));
            up4 = ImageIO.read(getClass().getResourceAsStream("../../Images/Personnages/Ennemis/Stalfos/up4.png"));
            right1 = ImageIO.read(getClass().getResourceAsStream("../../Images/Personnages/Ennemis/Stalfos/right1.png"));
            right2 = ImageIO.read(getClass().getResourceAsStream("../../Images/Personnages/Ennemis/Stalfos/right2.png"));
            right3 = ImageIO.read(getClass().getResourceAsStream("../../Images/Personnages/Ennemis/Stalfos/right3.png"));
            right4 = ImageIO.read(getClass().getResourceAsStream("../../Images/Personnages/Ennemis/Stalfos/right4.png"));
            left1 = ImageIO.read(getClass().getResourceAsStream("../../Images/Personnages/Ennemis/Stalfos/left1.png"));
            left2 = ImageIO.read(getClass().getResourceAsStream("../../Images/Personnages/Ennemis/Stalfos/left2.png"));
            left3 = ImageIO.read(getClass().getResourceAsStream("../../Images/Personnages/Ennemis/Stalfos/left3.png"));
            left4 = ImageIO.read(getClass().getResourceAsStream("../../Images/Personnages/Ennemis/Stalfos/left4.png"));
            down1 = ImageIO.read(getClass().getResourceAsStream("../../Images/Personnages/Ennemis/Stalfos/down1.png"));
            down2 = ImageIO.read(getClass().getResourceAsStream("../../Images/Personnages/Ennemis/Stalfos/down2.png"));
            down3 = ImageIO.read(getClass().getResourceAsStream("../../Images/Personnages/Ennemis/Stalfos/down3.png"));
            down4 = ImageIO.read(getClass().getResourceAsStream("../../Images/Personnages/Ennemis/Stalfos/down4.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public boolean levelUp(){
        if (super.levelUp()){
            this.setDegats(this.getDegats()+5);
            this.setPv(this.getPv()+5);
            return true;
        }
        return false;
    }

    @Override
    public void combat(Personnages p) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'combat'");
    }

    
}
