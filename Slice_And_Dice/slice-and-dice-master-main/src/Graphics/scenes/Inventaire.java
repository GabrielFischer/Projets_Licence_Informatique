package Graphics.scenes;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;

import javax.swing.ImageIcon;

import Equipement.Equipement;


public class Inventaire extends MenuMethods{ //classe qui gère l'inventaire dans une partie
    public ArrayList<Equipement> equipement;
    int equipementSelectionné ;
    
    Playing playing;


    boolean hover;
    int hoverIndex;
    int xMouse;
    int yMouse;

    
    int inventaireY;
    private Rectangle rectItem;

   

    public Inventaire(Playing p){
        super(p.getMain(), 300, 750, "INVENTAIRE");
        equipement = new ArrayList<Equipement>();
        equipementSelectionné = -1;
        this.playing = p;
        hover = false;
        inventaireY = (WINDOWHEIGHT - height) / 2  + barHeight; 
        rectItem = new Rectangle(x + 10, inventaireY + 10,width/2, height/3);
        
    }

    public void addEquipement(Equipement e){
        equipement.add(e);
        
    }

    public void drawInventaire(Graphics2D g2){
        super.draw(g2);
        
        drawEquipements(g2);

        if (hover){
            equipement.get(hoverIndex).drawDescription(g2, xMouse, yMouse, 100, playing.getBoutique().getborderColor(), playing.getBoutique().getFillColor(), playing.getBoutique().getTextColor());
        }

        drawHeros(g2);
    }

    public void drawHeros(Graphics2D g2){
        g2.setColor(playing.getBoutique().getborderColor());
        for(int i = 0; i<playing.getListeHeros().length;i++){
            String iconNameURL = "Images/Heros/" + playing.getListeHeros()[i].getNomEntite() + ".png";
            java.net.URL iconURL = getClass().getClassLoader().getResource(iconNameURL);
            ImageIcon iconHero = new ImageIcon(iconURL);
            int ytemp =  inventaireY+height-70-barHeight;
            if (iconURL != null) { 
                int xTemp = x+10 + i * 30 * 5;
                
                g2.drawImage(iconHero.getImage(), xTemp , ytemp, 60, 60, null);
                g2.drawRect(xTemp, ytemp, 60, 60);
                
                for(int j = 0;j<2;j++){
                    if(playing.getListeHeros()[i].getInventaire()[j]!=null){
                        playing.getListeHeros()[i].getInventaire()[j].drawInCarre(g2,xTemp+ 65 , ytemp+ j*30, 30,playing.getBoutique().getborderColor());
                    }
                    else{
                        g2.drawRect(xTemp+ 65 , ytemp+ j*30, 30,30);
                    }
                    

                }
            }
        }
    }

    public void drawEquipements(Graphics2D g2){
        for(int i = 0;i<equipement.size();i++){

            int xEquip = x + 10 + (i%10)*70;
            int yEquip =  inventaireY + 10 + i/10*70;
            if(equipementSelectionné == i){
                equipement.get(i).drawInCarre(g2, xEquip,yEquip, 60, playing.getBoutique().getTextColor());
                g2.setColor(playing.getBoutique().getborderColor());
            }
            else{
                equipement.get(i).drawInCarre(g2, xEquip, yEquip, 60, playing.getBoutique().getborderColor());
            }
        }
    }

    public void mouseClicked(int mouseX, int mouseY){
        super.mouseClicked(mouseX, mouseY);
        for(int i = 0;i<equipement.size();i++){
            if(inCarre(mouseX, mouseY,  x + 10 + i*60, inventaireY + 10, 60)){
                equipementSelectionné = i;
            }
        }
        int ytemp =  inventaireY+height-70-barHeight;
        if(mouseY>=ytemp){
            for(int i = 0; i<5; i++){
                int xTemp = x+10 + i * 30 * 5;
                for(int j = 0 ; j<2;j++){
                    if (inCarre(mouseX, mouseY, xTemp+ 65, ytemp+ j*30, 30)){
                        if(equipementSelectionné>=0){
                            if(playing.getListeHeros()[i].getInventaire()[j]!=null){ // si le héro a déjà un objet à cet emplacement
                                equipement.add(playing.getListeHeros()[i].getInventaire()[j]); //l'objet est remis dans la liste d'equipements dispos
                                playing.getListeHeros()[i].desequipe(j);//l'équipement est désequippé
                                
                                playing.getListeHeros()[i].equipe(equipement.get(equipementSelectionné), j);// le héro équipe le nouvel objet
                                equipement.remove(equipementSelectionné);
                                equipementSelectionné = -1;
                            }
                            else{
                                playing.getListeHeros()[i].equipe(equipement.get(equipementSelectionné), j); // le héro équipe l'objet
                                equipement.remove(equipementSelectionné); // l'objet est retiré de la liste des objets disponibles
                                equipementSelectionné = -1;
                            }
                            
                        }
                        else{
                            if(playing.getListeHeros()[i].getInventaire()[j]!=null){ // si aucun objet n'est selectionné
                                equipement.add(playing.getListeHeros()[i].getInventaire()[j]); //l'objet est remis dans la liste d'equipements dispos
                                playing.getListeHeros()[i].desequipe(j);//l'équipement est désequippé
                            }
                        }
                        
                    }
                }

            }
        }
    }
    public void mouseMoved(int mouseX, int mouseY){
        for(int i = 0;i<equipement.size();i++){
            int xEquip = x + 10 + (i%10)*70;
            int yEquip =  inventaireY + 10 + i/10*70;
            if(inCarre(mouseX, mouseY,  xEquip, yEquip, 60)){

                this.hover = true;
                this.xMouse = mouseX;
                this.yMouse = mouseY;
                this.hoverIndex = i;
                break;

            }
            else{
                hover = false;
            }
        }
    }


    public boolean inCarre(int x, int y , int carreX,int carreY, int size){
        if(x < carreX || x > carreX + size){
            return false;
        }
        if(y < carreY || y > carreY + size){
            return false;
        }
        return true;
    }
}
