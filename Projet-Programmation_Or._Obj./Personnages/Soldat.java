package Personnages;

public class Soldat extends Personnages{
    int graphicX;
    int graphicY;

    //Seul personnage de type true, combat contre les ennemis
    public Soldat(){
        super("Soldat",true,50,5,1,3,10,1,1,2,4,10);
    }

    public boolean levelUp(){
        if(super.levelUp()){
            this.setDegats(this.getNv()*10);
            this.setPv(this.getPv()+ 20);
            return true;
        }
        return false;
        
    }
    public static void main(String[] args) {
        Soldat s = new Soldat();
        System.out.println(s.getNv());
        System.out.println(s.getDegats());
        s.levelUp();
        System.out.println(s.getNv());
        System.out.println(s.getDegats());
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
    public void mort(){
        plateau.getTab()[y][x].setNull();
        enVie = false;
        plateau = null;
        niveau = null;
    }
}