package Personnages;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import Jeu.Case;
import Jeu.GamePanel;
import Jeu.Niveau;
import Jeu.Plateau;

public abstract class Personnages extends Thread{

    public String nom;

    private int pv;
    private int degats;
    public int nv;
    public int nvMax;

    public boolean type; // allié ou ennemi

    private static int compteur=0;
    public int id;

    
    private int vitesse;
    private double attackSpeed; //nombre d'attaque par seconde
    public boolean enVie = true;
    private int range;

    public boolean estBrule;
    public boolean estEmpoisonne;
    public boolean estGele;
    public boolean estRalenti;
    public boolean vole;
    public boolean armure;

    public int x;
    public int y;
    public Plateau plateau;
    public Niveau niveau;
    public int[] posChemin;
    public static boolean fonctionne = true;

    public int resistanceMagique;
    public int resistancePhysique;

    //SPRITE
    public BufferedImage up1,up2,up3,up4,down1,down2,down3,down4,left1,left2,left3,left4,right1,right2,right3,right4;
    public String direction;
    public int spriteCounter = 0;
    public int numSprite;

    public GamePanel gp;
    public int valeur;

    public int graphicX;
    public int graphicY;

    public Personnages(String nom, boolean type, int pv, int degats, int nv, int nvMax, int vitesse, double attackSpeed, int range,int rM, int rP,int valeur){
        this.nom = nom;
        this.pv = pv;
        this.degats = degats;
        this.nv = nv;
        this.type = type;
        this.id = compteur;
        compteur++;
        this.nvMax = nvMax;
        this.vitesse=vitesse;
        this.attackSpeed=attackSpeed;
        this.range = range;
        this.resistanceMagique = rM;
        this.resistancePhysique = rP;
        this.valeur = valeur;
    }

    

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
    }
    public void setDefaultValues(){
        
        numSprite = 1;
        direction = "up";;
        System.out.println(graphicY);
    }
    
    public void update(){
        
        spriteCounter ++;
        if(spriteCounter>10){
            if (numSprite==4){
                numSprite = 1;
            }
            else{
                numSprite++;
            }
            spriteCounter = 0;
        }
    }

    public int getPv(){
        return this.pv;
    }
    public int getDegats(){
        return this.degats;
    }
    public int getNv(){
        return this.nv;
    }
    public int getID(){
        return this.id;
    }
    public int getCompteur(){
        return compteur;
    }
    public int getVitesse(){
        return vitesse;
    }
    public double getAttackSpeed(){
        return attackSpeed;
    }
    public int getRange(){
        return range;
    }
    public void setPv(int pv){
        this.pv = pv;
    }
    public void setDegats(int degats){
        this.degats=degats;
    }
    public void setNv(int nv){
        this.nv = nv;
    }
    public void setVitesse(int vitesse){
        this.vitesse = vitesse;
    }
    public void setAttackSpeed(double AS){
        this.attackSpeed = AS;
    }
    public void setRange(int range){
        this.range = range;
    }

    public boolean levelUp(){
        if (nv<nvMax){
            nv++;
            valeur += valeur;
            return true;
        }
        return false;
    }

    public void arrive(){
        this.enVie=false;
        plateau.getTab()[this.y][this.x].setNull();
        this.plateau.getTab()[this.y][this.x].p=null;
        this.posChemin = null;
        System.out.println(this.nom+" a infligé "+ this.degats + " de dégat à la base.");
        System.out.println("Nombre de pv restant : " +this.plateau.base);
    }

    //La plupart des fonctions suivantes existent à cause du projet d'introduire des casernes et des soldats

    public boolean attaque(Personnages p){
        if (this.enVie && p.enVie){
            if (this.degats >= p.pv){
                p.pv=0;
                p.enVie = false;
                return false;
            }else{
                p.pv-=this.degats;
                return true;
            }
        }
        return false;
    }

    public void attaqueBase(){
        this.plateau.base-=this.degats;
        this.arrive();  
    }

    public abstract void combat(Personnages p);

    public void prendreDegats(int n){
        if (this.enVie){
            if (n>=this.getPv()){
                this.setPv(0);
                this.enVie = false;
            }else{
                this.setPv(this.getPv()-n);
            }
        }
    }

    public Personnages detecte (){
        if (niveau!=null && this.posChemin!=null){
            int pos = x+y*plateau.hauteur;
            for (int i =0;i<this.posChemin.length;i++){
                if (this.posChemin[i]==pos){
                    if (this.type){
                        for (int j=1;j<=this.range;j++){
                            if (i+j<this.posChemin.length){
                                int posSuivant = this.posChemin[i+j];
                                int x = posSuivant / plateau.getTab()[0].length; 
                                int y = posSuivant % plateau.getTab()[0].length; 
                                if (plateau.getTab()[x][y].p!=null){
                                    if (plateau.getTab()[x][y].p.type != this.type){
                                        return plateau.getTab()[x][y].p;
                                    }
                                }
                            }
                        }
                    }else{
                        for (int j=1;j<=this.range;j++){
                            if (i-j>=0){
                                int posSuivant = this.posChemin[i-j];
                                int x = posSuivant / plateau.getTab()[0].length; 
                                int y = posSuivant % plateau.getTab()[0].length; 
                                if (plateau.getTab()[x][y].p!=null){
                                    if (plateau.getTab()[x][y].p.type != this.type){
                                        return plateau.getTab()[x][y].p;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    public void avance(){
        if (niveau!=null && this.posChemin!=null){
            double cooldown = 10/this.vitesse*1000;
            while (this.detecte()==null && this.prochaineCase()!=null){
                Case prochaine = this.prochaineCase();
                int px = prochaine.x;
                int py = prochaine.y;
                if(x>px){
                    direction = "left";
                    
                    
                }
                if(x<px){
                    
                    direction = "right";
                }
                if(y>py){
                    direction = "up";
                }
                if(y<py){
                    direction = "down";
                }
                this.plateau.deplacerPerso(this, px, py);
                try{
                    Thread.sleep((long)cooldown);
                }catch (InterruptedException e){}
            }
        }
    }

    public Case prochaineCase(){
        if (niveau!=null && posChemin!=null){
            int pos = x+y*plateau.hauteur;
            for (int i =0;i<this.posChemin.length;i++){
                if (this.posChemin[i]==pos){
                    if (this.type){
                        if (i+1<posChemin.length){
                            int posSuivant = this.posChemin[i+1];
                            int x = posSuivant / plateau.getTab()[0].length; //x
                            int y = posSuivant % plateau.getTab()[0].length; //y
                            if (plateau.getTab()[x][y].type==2 && plateau.getTab()[x][y].p==null){
                                return plateau.getTab()[x][y];
                            }
                        }
                    }else{
                        if (i-1>=0){
                            int posSuivant = this.posChemin[i-1];
                            int x = posSuivant / plateau.getTab()[0].length; //x
                            int y = posSuivant % plateau.getTab()[0].length; //y
                            if (plateau.getTab()[x][y].type==2){
                                if(plateau.getTab()[x][y].p==null){
                                    return plateau.getTab()[x][y];
                                }/* else{
                                    this.attaque(plateau.getTab()[x][y].p);
                                } */
                            }else{
                                return null;
                            }
                        }else{
                            return null;
                        }
                    }
                }
            }
            if (this.detecte()!=null){
                this.combat(this.detecte());
            }
        }
        return null;
    }

    public void avanceEtCombat(){
        while(this.enVie && fonctionne){
            if (this.detecte()!=null){
                this.combat(this.detecte());
            }else if(this.prochaineCase()==null && !this.type){
                this.attaqueBase();
            }else{
                this.avance();
            }
        }
    }

    public void run(){
        Thread thread1 = new Thread(() -> {
            while (!Thread.interrupted()) {
                while (this.enVie && !niveau.gameOver()){
                    this.avanceEtCombat();
                }
                break;
            }
        });
        thread1.start();
    }


    
}
