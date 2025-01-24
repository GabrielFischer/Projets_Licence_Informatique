package Jeu;

import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.imageio.ImageIO;

import Personnages.Personnages;
import Personnages.Ennemis.*;

import Tours.*;


public class Plateau{
    public GamePanel gp;
    public int hauteur;
    public int largeur;
    public int base;
    public Case [][] tab;
    public Case [] tile = new Case[100];
    public int [] depart = new int[2];
    public Case arrivee;
    public int[] posChemin;
    public int argent;
    int map [][] = new int[12][12];

/* 
    public Plateau(int x, int y,int b){
        this.base = b;
        this.largeur = x;
        this.hauteur = y;
        this.tab = new Case[y][x];
        for (int i = 0;i<y; i++){
            for (int j = 0; j<x; j++){
                tab[i][j]=new Case(3);
                tab[i][j].x=j;
                tab[i][j].y=i;
            }
        }
        getCaseImage();
    }*/
    public Plateau(int i){
        this.tab = new Case[12][12];
        this.largeur = tab.length;
        this.hauteur = tab.length;
        getCaseImage();
        loadMap(i);
    }
    public Plateau(GamePanel gp,int i){
        this.gp = gp;
        this.tab = new Case[12][12];
        this.largeur = tab.length;
        this.hauteur = tab.length;
        getCaseImage();
        loadMap(i);
    }

    public void getCaseImage(){
        try {
            tile[0] = new Case(3);
            tile[0].image = ImageIO.read(getClass().getResourceAsStream("../Images/Tiles/herbe.png"));
        
            tile[1] = new Case(2);
            tile[1].image = ImageIO.read(getClass().getResourceAsStream("../Images/Tiles/chemin.png"));
            
            /* 
            tile[2] = new Case(3);
            tile[2].image = ImageIO.read(getClass().getResourceAsStream("../Images/Tiles/emplacementTourelle.png"));
            */
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void loadMap(int numeroPlateau){
        InputStream [] listePlateau = new InputStream[30];
        try {
            //importer autant de plateaux que souhaité
            listePlateau[0] = getClass().getResourceAsStream("Niveaux/Niveau_01/map.txt");
            listePlateau[1] = getClass().getResourceAsStream("Niveaux/Niveau_02/map.txt");
            listePlateau[2] = getClass().getResourceAsStream("Niveaux/Niveau_03/map.txt");


            BufferedReader br = new BufferedReader(new InputStreamReader(listePlateau[numeroPlateau]));

            for(int k = 0; k < tab.length ; k++ ){
                String line = br.readLine();
                String n []= line.split(" ");
                for(int j = 0; j < tab.length ;j++){
                    int num = Integer.parseInt(n[j]);
            
                    map[j][k] = num;
                    tab[k][j] = new Case(tile[num].type);
                    tab[k][j].x = j;
                    tab[k][j].y = k;
                }                
            }

            String line = br.readLine();
            String n[] = line.split(" ");
            posChemin = new int [n.length];

            //enregistre chemin
            for(int i = 0; i < n.length ; i++){
                int num = Integer.parseInt(n[i]);
               
                posChemin[i] = num;
            }

            //POSE CHEMIN
            for (int i=0;i<posChemin.length;i++){
                int indiceCase = posChemin[i];
                int x = indiceCase / tab[0].length; //x
                int y = indiceCase % tab[0].length; //y
                tab[x][y].setCaseType(2);
                tab[x][y].image = tile[1].image;
            }
            
            {
            line = br.readLine();
            n = line.split(" ");
            int x = Integer.parseInt(n[0]);
            int y = Integer.parseInt(n[1]);
            //setCaseDepart(x, y);
            depart[0] = x ; 
            depart[1] = y ; 
            

            line = br.readLine();
            n = line.split(" ");
            x = Integer.parseInt(n[0]);
            y = Integer.parseInt(n[1]);
            setCaseArrivee(x, y);
            arrivee = tab[y][x];

            line = br.readLine();
            n = line.split(" ");
            base = Integer.parseInt(n[0]);
            
            
            line = br.readLine();
            n = line.split(" ");
            argent = Integer.parseInt(n[0]);
            }

            br.close();
        } catch (Exception e) {
            
        }
    }
    public void setPlateauTerminal(){
        for(int i = 0 ; i< tab.length ; i++){
            for (int j = 0 ; j< tab.length; j++){
                
                tab[i][j] = new Case(3);
            }
        }
        for (int i=0;i<posChemin.length;i++){
            System.out.println( posChemin[i]);
                int indiceCase = posChemin[i];
                int x = indiceCase / tab[0].length; //x
                int y = indiceCase % tab[0].length; //y
                tab[x][y].setCaseType(2);
                //tab[x][y].image = tile[1].image;
            }
    }

    public void draw(Graphics2D g2){
        int n = tab.length;
        for(int i = 0;i<n;i++){
            for (int j = 0;j<n;j++){
                int tileNum = map[j][i];
                g2.drawImage(tile[tileNum].image,j*gp.tileSize,i*gp.tileSize,gp.tileSize,gp.tileSize,null);
                
            }   
        }
        for(int i = 0;i<tab.length;i++){
            for (int j = 0;j<tab.length;j++){
                tab[j][i].draw(g2);
            }   
        }
        
    }   


    public Case[][] getTab(){
        return this.tab;
    }
    public boolean estValide(int x, int y){
        if(x<=largeur || x>0){
            if(y<=hauteur || y>0){
                return true;
            }
        }
        System.out.println("Cordonnées invalides.");
        return false;
    }
    public void afficher(){
        
        System.out.println();
        for(int i = 0 ; i<tab.length; i++){
            
            for(int j = 0; j<tab[i].length;j++){
                System.out.print(this.tab[i][j]+" ");
            }
            System.out.print(i + " ");
            System.out.println();
        }
    }
    public void setCaseDepart(int x, int y){
        tab[y][x].setCaseType(0);
    }
    public void setCaseArrivee(int x, int y){
        tab[y][x].setCaseType(1);
    }
    public int numCase(int x, int y){
        return x+y*hauteur;
    }
    public void setCheminHorizontal(int x, int x1, int y){
        int width = Math.abs(x - x1);    
        int xf = Math.min(x1, x);
            for(int i = xf;i< xf+width;i++){
                tab[y][i].setCaseType(2);
            }
    }
    public void setCheminVertical(int y, int y1, int x){
        int height = Math.abs(y - y1);    
        int yf = Math.min(y1, y);
        for(int i = yf;i< yf+height;i++){
            tab[i][x].setCaseType(2);
        }
    }
    public void setPersonnage(Personnages p , int x, int y){
        if (this.tab[y][x].p==null && estValide(x, y)){
            if (this.tab[y][x].setPersonnage(p)){
                p.plateau=this;
                p.x=x;
                p.y=y;
            }
            
        }
    }
    public void deplacerPerso(Personnages p, int x, int y){
        if (this.tab[y][x].p==null && estValide(x, y)){
            int aX= p.x;
            int aY = p.y;
            if (this.tab[y][x].setPersonnage(p)){
                p.plateau=this;
                p.x=x;
                p.y=y;
                this.tab[aY][aX].setNull();
            }
        }
    }
    public void supprimerTours(int x, int y){       
        this.tab[y][x].t.supprimer();
        this.tab[y][x].setNull();
    }
    public boolean setTours(Tours t , int x, int y){
        if (this.tab[y][x].t==null && this.tab[y][x].type==3 && estValide(x, y)){
            if (t instanceof Caserne){
                Caserne caserne = (Caserne)t;
                if (x>0){
                    if (this.tab[y][x-1].type==2){
                        caserne.orientation = 'w';
                    }
                }if(x<this.tab[0].length-1){
                    if (this.tab[y][x+1].type==2){
                        caserne.orientation = 'e';
                    }
                }if (y>0){
                    if (this.tab[y-1][x].type==2){
                        caserne.orientation = 'n';
                    }
                }if(y<this.tab.length-1){
                    if (this.tab[y+1][x].type==2){
                        caserne.orientation = 's';
                    }
                }

                if (caserne.orientation!=null){
                    if (this.tab[y][x].setTours(caserne)){
                        caserne.plateau=this;
                        caserne.x=x;
                        caserne.y=y;
                        return true;
                    }
                }else{
                    System.out.println("Votre caserne doit être collée à un chemin");
                    return false;
                }

            }

            else if (this.tab[y][x].setTours(t)){
                t.plateau=this;
                t.x=x;
                t.y=y;
                return true;
            }
        }
        else{
            System.out.println("Vous ne pouvez pas placer votre tourelle ici.");
            return false;
        }
        return false;
    }
    public void deplacerTour(Tours t, int x, int y){
        if (this.tab[y][x].t==null && this.tab[y][x].type==3 && estValide(x, y)){
            int aX= t.x;
            int aY = t.y;

            if (t instanceof Caserne){
                Caserne caserne = (Caserne)t;
                if (x>0){
                    if (this.tab[y][x-1].type==2){
                        caserne.orientation = 'w';
                    }
                }if(x<this.tab[0].length-1){
                    if (this.tab[y][x+1].type==2){
                        caserne.orientation = 'e';
                    }
                }if (y>0){
                    if (this.tab[y-1][x].type==2){
                        caserne.orientation = 'n';
                    }
                }if(y<this.tab.length-1){
                    if (this.tab[y+1][x].type==2){
                        caserne.orientation = 's';
                    }
                }

                if (caserne.orientation!=null){
                    if (this.tab[y][x].setTours(caserne)){
                        caserne.plateau=this;
                        caserne.x=x;
                        caserne.y=y;
                        this.tab[aY][aX].setNull();
                    }
                }else{
                    System.out.println("Votre caserne doit être collée à un chemin");
                }

            }

            else if (this.tab[y][x].setTours(t)){
                t.plateau=this;
                t.x=x;
                t.y=y;
                this.tab[aY][aX].setNull();
            }
        }else{
            System.out.println("Vous ne pouvez pas déplacer votre tourelle ici.");
        }
    }


    
}
