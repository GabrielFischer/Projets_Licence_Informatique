package Personnages.Ennemis;

import javax.imageio.ImageIO;

import Jeu.GamePanel;
import Personnages.Personnages;

public class Keaton extends Personnages {
    int graphicX;
    int graphicY;
    public Keaton(int nv){
        super("Keaton",false,10+5*nv,5+5*nv,nv,5,10,0.5,1,4,2,3);
    }

    public Keaton(int nv, GamePanel gp){
        super("Keaton",false,10+5*nv,5+5*nv,nv,5,10,0.5,1,4,2,3);
        this.gp = gp;
        setDefaultValues();
        getKeatonImage();
    }
    public void getKeatonImage(){
        try {
            up1 = ImageIO.read(getClass().getResourceAsStream("../../Images/Personnages/Ennemis/Keaton/up1.png"));
            up2 = ImageIO.read(getClass().getResourceAsStream("../../Images/Personnages/Ennemis/Keaton/up2.png"));
            up3 = ImageIO.read(getClass().getResourceAsStream("../../Images/Personnages/Ennemis/Keaton/up3.png"));
            up4 = ImageIO.read(getClass().getResourceAsStream("../../Images/Personnages/Ennemis/Keaton/up4.png"));
            right1 = ImageIO.read(getClass().getResourceAsStream("../../Images/Personnages/Ennemis/Keaton/right1.png"));
            right2 = ImageIO.read(getClass().getResourceAsStream("../../Images/Personnages/Ennemis/Keaton/right2.png"));
            right3 = ImageIO.read(getClass().getResourceAsStream("../../Images/Personnages/Ennemis/Keaton/right3.png"));
            right4 = ImageIO.read(getClass().getResourceAsStream("../../Images/Personnages/Ennemis/Keaton/right4.png"));
            left1 = ImageIO.read(getClass().getResourceAsStream("../../Images/Personnages/Ennemis/Keaton/left1.png"));
            left2 = ImageIO.read(getClass().getResourceAsStream("../../Images/Personnages/Ennemis/Keaton/left2.png"));
            left3 = ImageIO.read(getClass().getResourceAsStream("../../Images/Personnages/Ennemis/Keaton/left3.png"));
            left4 = ImageIO.read(getClass().getResourceAsStream("../../Images/Personnages/Ennemis/Keaton/left4.png"));
            down1 = ImageIO.read(getClass().getResourceAsStream("../../Images/Personnages/Ennemis/Keaton/down1.png"));
            down2 = ImageIO.read(getClass().getResourceAsStream("../../Images/Personnages/Ennemis/Keaton/down2.png"));
            down3 = ImageIO.read(getClass().getResourceAsStream("../../Images/Personnages/Ennemis/Keaton/down3.png"));
            down4 = ImageIO.read(getClass().getResourceAsStream("../../Images/Personnages/Ennemis/Keaton/down4.png"));
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
