package Personnages.Ennemis;

import Jeu.GamePanel;
import Personnages.Personnages;

public class Darknut extends Personnages{
    int graphicX;
    int graphicY;
    public Darknut(int nv){
        super("Darknut",false,30+10*nv,10+10*nv,nv,5,10,2,2,2,5,4);
    }

    public Darknut(int nv, GamePanel gp){
        super("Darknut",false,30+10*nv,10+10*nv,nv,5,10,2,2,2,5,4);
        this.gp = gp;
        this.direction = "up";
        graphicY = y*gp.tileSize;
        graphicX = x*gp.tileSize;
    }

    public boolean levelUp(){
        if (super.levelUp()){
            this.setDegats(this.getDegats()+10);
            this.setPv(this.getPv()+10);
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
