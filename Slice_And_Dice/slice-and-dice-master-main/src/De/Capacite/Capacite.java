package De.Capacite;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Paths;

import java.nio.file.Files;

import javax.imageio.ImageIO;

import Entites.*;
import main.Joueur;
import main.Main;

public class Capacite {
    /* classe mere de toutes les capacité, gèere les actions des capacites ainsi que l'affichage de leur description et de leur icone*/
    private int id;
    private String nom;
    public BufferedImage image;
    public int type; //0=undifined 1=degats 2=soin 3=bouclier 4=mana 12=degats+soin 13=degats+bouclier etc..
    //Toujours définir un double niveau avec le plus petit chiffre suivi du grand chiffre (ex 32 impossible, écrire 23)
    //Permet de pouvoir modifier les stats d'un type d'attaque particulier avec les équipements
    
    private String description;
    private int niveau;
    private boolean dist; //booleen pour savoir si attaque peut toucheer backrow
    private BufferedImage  imageNiveau ;
    private int prixDachat=0;//les 5 capacités Arc;Bouclier;Soin;CoupEpee;Mana seront à 0 tandis que les capacités accessibles via la boutique auront naturellement un prix supérieur à 0

    public Capacite(int id,String nom){
        this.nom = nom;
        this.id = id;
        initImageNiveau();
    }

    public void initImageNiveau(){
        try {
            String path;
            if(estDansCapacitesEnnemis()){
                path = "../../../../res/Images/Icons/niveau"+this.niveau + ".png";
                
            }
            else{
                path = "../../../res/Images/Icons/niveau"+this.niveau + ".png";
                
            }
            this.imageNiveau = ImageIO.read(getClass().getResourceAsStream(path));
        } catch (Exception e) {
            
        }
    }
    
    private boolean estDansCapacitesEnnemis() {
        String packageName = this.getClass().getPackageName();
        return packageName.contains("Capacite_Ennemi");
    }


    public void initImage(){
        try {
            String text = "../../../res/Images/Capacite/"+this.nom+".png";
            this.image = ImageIO.read(getClass().getResourceAsStream(text));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void draw(Graphics2D g2,int x,int y,int size){
        if(image!=null){
            if(niveau !=0){
                g2.drawImage(image, x, y,size,size,null);
                g2.drawImage(imageNiveau,x, y,size,size,null);
            }
            
        }
        
        
    }
    

    //Setter
    public void setDist(boolean d){
        this.dist = d;
    }
    public void setDescription(String d){
        this.description = d;
    }
    public void setNiveau(int n){
        this.niveau = n;
        initImageNiveau();
    }
    public void setNom(String n){
        this.nom = n;
    }
    public void setType(int n){
        this.type = n;
    }

    //Getter
    public boolean getDist(){
        return this.dist;
    }
    public String getDescription(){
        return this.description;
    }
    public int getNiveau(){
        return this.niveau;
    }
    public int getId(){
        return this.id;
    }
    public String getNom(){
        return this.nom;
    }
    public int getType(){
        return this.type;
    }

    //action est une méthode propre à chaque attaque/action elle peut appeler les méthodes soin, bouclier, mana et attaque
    //Comme ça si une action a plusieurs effets (ex: soigne et attaque), on peut simplement créer une méthode action qui appelle ces méthodes
    public void action(Heros h){}
    public void action(Ennemis e){

    }
    public void action(Joueur j){}
    public void action(Ennemis e, Heros h){}
    public void action(Entites e){}
    public void soin(Heros h){
        if(h.getPdv()+this.niveau>h.getPdvMax())
        {
            h.setPdv(h.getPdvMax());
        }
        else
        {
            h.setPdv(h.getPdv()+this.niveau);
        }
    }

    public void mortSubite(Ennemis e)
    {
        e.setPdv(0);
    }

    public void soinAccrus(Heros h)
    {
        h.setPdv(h.getPdvMax());
    }

    public void poison(Ennemis e)
    {
        e.setEstEmpoisonne(e.getEstEmpoisonne() + this.getNiveau());
    }
    public void bouclier(Heros h){
        h.setBouclier(h.getBouclier()+this.niveau);
        if (h.getPdvMenaces()>=this.niveau){
            h.setPdvMenaces(h.getPdvMenaces()-this.niveau);
        }else{
            h.setPdvMenaces(0);
        }
    }
    
    public void mana(Joueur j){
        j.setMana(j.getMana()+this.niveau);
    }

    public void attaque(Entites e){
        if(e instanceof Ennemis){
            e.setPdv(e.getPdv()-this.niveau);
        }
        
    }

    public void attaqueEnnemi(Heros h){
        if (h.getBouclier()>0){
            if (h.getBouclier()>=this.niveau){
                h.setBouclier(h.getBouclier()-this.niveau);
            }else{
                int degatsRestants = this.niveau-h.getBouclier();
                h.setBouclier(0);
                h.setPdv(h.getPdv()-degatsRestants);
            }
        }else{
            h.setPdv(h.getPdv() - this.niveau);
        }
    }

    public void attaqueRicochets(Ennemis e){ //Appelé si Heros.ricochet==true
        attaque(e);
        //Si ennemisGauche!=null -1 pv ennemis gauche
        //Si ennemisDroit!=null -1 pv ennemis droit
        //Il faudrait faire un tableau d'ennemis
    }

    public void effetBasiqueDuSort(Entites e){}

    public BufferedImage getImage(){
        return image;
    }

    public int getPrixDachat() {
        return prixDachat;
    }

    public void setPrixDachat(int prixDachat) {
        this.prixDachat = prixDachat;
    }

    public void drawDescription(Graphics2D g2, int x, int y, int size, Color borderColor, Color fillColor, Color textColor) {//méthode qui dessine ma description d'une capacité
        g2.setColor(fillColor);
        g2.fillRect(x, y,size*2, size);

        g2.setColor(borderColor);
        g2.drawRect(x, y, size*2, size);

        g2.setColor(textColor);
        drawTextInRectangle(g2,this.description,x,y,size*2,size);
    }

    private void drawTextInRectangle(Graphics2D g2d, String text, int x, int y, int width, int height) {
        FontMetrics fm = g2d.getFontMetrics();

        // Sépare le texte en lignes pour s'assurer qu'il ne dépasse pas la largeur du rectangle
        String[] lines = getWrappedTextLines(text, fm, width);

        // Dessine chaque ligne de texte à l'intérieur du rectangle
        int lineHeight = fm.getHeight();
        int startY = y + (height - lines.length * lineHeight) / 2 + fm.getAscent();
        for (String line : lines) {
            g2d.drawString(line, x + 5, startY);
            startY += lineHeight;
        }
    }

    private String[] getWrappedTextLines(String text, FontMetrics fm, int width) {
        String[] words = text.split(" ");
        StringBuilder wrappedLines = new StringBuilder();
        int lineWidth = 0;

        for (String word : words) {
            int wordWidth = fm.stringWidth(word + " ");
            if (lineWidth + wordWidth <= width) {
                wrappedLines.append(word).append(" ");
                lineWidth += wordWidth;
            } else {
                wrappedLines.append("\n").append(word).append(" ");
                lineWidth = wordWidth;
            }
        }
        return wrappedLines.toString().split("\n");
    }
}
