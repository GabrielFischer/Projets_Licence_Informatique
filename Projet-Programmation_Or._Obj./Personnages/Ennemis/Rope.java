package Personnages.Ennemis;

import javax.imageio.ImageIO;

import Jeu.GamePanel;
import Personnages.Personnages;

public class Rope extends Personnages{
    int graphicX;
    int graphicY;
    public Rope (int nv){
        super("Rope",false,3+2*nv,5+5*nv,nv,5,10,1,1,1,2,2);
    }

    public Rope (int nv, GamePanel gp){
        super("Rope",false,3+2*nv,5+5*nv,nv,5,10,1,1,1,2,2);
        this.gp = gp;
        setDefaultValues();
        getRopeImage();
    }

    public void setDefaultValues(){
        
        numSprite = 1;
        direction = "up";
        System.out.println(graphicY);
    }

    public void getRopeImage(){
        try {
            up1 = ImageIO.read(getClass().getResourceAsStream("../../Images/Personnages/Ennemis/Rope/up1.png"));
            up2 = ImageIO.read(getClass().getResourceAsStream("../../Images/Personnages/Ennemis/Rope/up2.png"));
            up3 = ImageIO.read(getClass().getResourceAsStream("../../Images/Personnages/Ennemis/Rope/up3.png"));
            up4 = ImageIO.read(getClass().getResourceAsStream("../../Images/Personnages/Ennemis/Rope/up4.png"));
            right1 = ImageIO.read(getClass().getResourceAsStream("../../Images/Personnages/Ennemis/Rope/right1.png"));
            right2 = ImageIO.read(getClass().getResourceAsStream("../../Images/Personnages/Ennemis/Rope/right2.png"));
            right3 = ImageIO.read(getClass().getResourceAsStream("../../Images/Personnages/Ennemis/Rope/right3.png"));
            right4 = ImageIO.read(getClass().getResourceAsStream("../../Images/Personnages/Ennemis/Rope/right4.png"));
            left1 = ImageIO.read(getClass().getResourceAsStream("../../Images/Personnages/Ennemis/Rope/left1.png"));
            left2 = ImageIO.read(getClass().getResourceAsStream("../../Images/Personnages/Ennemis/Rope/left2.png"));
            left3 = ImageIO.read(getClass().getResourceAsStream("../../Images/Personnages/Ennemis/Rope/left3.png"));
            left4 = ImageIO.read(getClass().getResourceAsStream("../../Images/Personnages/Ennemis/Rope/left4.png"));
            down1 = ImageIO.read(getClass().getResourceAsStream("../../Images/Personnages/Ennemis/Rope/down1.png"));
            down2 = ImageIO.read(getClass().getResourceAsStream("../../Images/Personnages/Ennemis/Rope/down2.png"));
            down3 = ImageIO.read(getClass().getResourceAsStream("../../Images/Personnages/Ennemis/Rope/down3.png"));
            down4 = ImageIO.read(getClass().getResourceAsStream("../../Images/Personnages/Ennemis/Rope/down4.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean levelUp(){
        if (super.levelUp()){
            this.setDegats(this.getDegats()+2);
            this.setPv(this.getPv()+5);
            return true;
        }
        return false;
    }


    //doit empoisonner durant l'attaque
    public void combat(Personnages p){
        double cooldown = this.getAttackSpeed()*1000;
        boolean brulureActive = false;
        while (this.attaque(p)){
            System.out.println(this.nom + " à attaqué "+p.nom+". Pv restants : "+p.getPv());
            if (!brulureActive) {
                activerEffetBrulure(p);
                brulureActive = true;
            }
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

    public void activerEffetBrulure(Personnages p) {
        Thread brulureThread = new Thread(() -> {
            while (p.enVie) {
                try {
                    Thread.sleep(500); // Brulure chaque 0,5 seconde
                    p.prendreDegats(1);
                    System.out.println(p.nom + " brûlure, PV restants : " + p.getPv());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        brulureThread.start();
    }

    
}
