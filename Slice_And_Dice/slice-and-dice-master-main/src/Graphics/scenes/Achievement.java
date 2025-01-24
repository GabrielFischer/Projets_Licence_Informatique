package Graphics.scenes;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import main.Joueur;

public class Achievement extends MenuMethods{ //classe qui gère et stocke les achievement ainsi que leur recompenses
    private ArrayList<String> descriptionAch;
    private ArrayList<String> imagePaths;
    private ArrayList<Image> imageAch;

    private int monstresTués=0;
    private int manchesFinies=0;
    private int itemsAchetés=0;
    private int attaquesDébloquées=0;
    private boolean facileDone=false;
    private boolean moyenDone=false;
    private boolean difficileDone=false;
    private boolean infernoDone=false;

    private boolean[] achievementDone = new boolean[10];

    Playing playing;
    
    Joueur joueur;
    

    boolean hover = false;
    int xMouse = 0;
    int yMouse = 0;
    int hoverIndex = -1;

    float strokeWidth = 3.0f; // Épaisseur du trait en pixels
    BasicStroke stroke = new BasicStroke(strokeWidth);
    AlphaComposite alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f); //opacité du remplissage (transparant)
    AlphaComposite opaque = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f); //opacité du remplissage (opaque)

    public Achievement(Joueur j){
        super(j.main, 600, 800, "SUCCES");
       
        this.joueur = j;
        this.joueur.achievement=this;
        barHeight = height/12;
        close = new Rectangle(x + (width - barHeight), y, barHeight, barHeight);
        
    
        fillDescriptionAch();
        loadImagesAch();
    }

    


    public void fillDescriptionAch(){
        descriptionAch = new ArrayList<>();
        descriptionAch.add("Tuer 100 monstres");
        descriptionAch.add("Tuer 500 monstres");
        descriptionAch.add("Finir 10 manches");
        descriptionAch.add("Finir 100 manches");
        descriptionAch.add("Acheter 15 objets dans la boutique");
        descriptionAch.add("Débloquer 10 nouvelles attaque");
        descriptionAch.add("Finir l'histoire principale en mode facile");
        descriptionAch.add("Finir l'histoire principale en mode moyen");
        descriptionAch.add("Finir l'histoire principale en mode difficile");
        descriptionAch.add("Finir l'histoire principale en mode inferno");
    }

    public void loadImagesAch() {
        imagePaths = new ArrayList<>();
        imageAch = new ArrayList<>();
    
        InputStream inputStream = getClass().getResourceAsStream("/imagePath.txt");
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                // ajoute le chemin de l'image
                imagePaths.add(line);
                // charge l'image
                try {
                    Image image = ImageIO.read(getClass().getResourceAsStream(line));
                    if (image!=null){
                        imageAch.add(image);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void draw(Graphics2D g2){
        super.draw(g2);
        drawRectangles(g2);

    }

    

    //fonction qui sert à sauter des lignes tout les 20 caractères
    public void drawDescriptionLines(Graphics2D g2, String description, int x, int y) {
        String[] words = description.split(" ");
        StringBuilder line = new StringBuilder();
        int lineHeight = 0;
    
        for (String word : words) {
            if (line.length() + word.length() > 20) {
                g2.drawString(line.toString(), x, y + lineHeight);
                line = new StringBuilder(word + " ");
                lineHeight += g2.getFontMetrics().getHeight();
            } else {
                line.append(word).append(" ");
            }
        }
        if (line.length() > 0) {
            g2.drawString(line.toString(), x, y + lineHeight);
        }
    }

    //Fonction qui met a jour le compteur des succès
    public String drawCompteurSucces(Graphics2D g2, int i){
        switch (i){
            case 0: return String.valueOf(Math.min(monstresTués,100))+"/100"; 
            case 1: return String.valueOf(Math.min(monstresTués,500))+"/500"; 
            case 2: return String.valueOf(Math.min(manchesFinies,10))+"/10"; 
            case 3: return String.valueOf(Math.min(manchesFinies,100))+"/100"; 
            case 4: return String.valueOf(Math.min(itemsAchetés,15))+"/15"; 
            case 5: return String.valueOf(Math.min(attaquesDébloquées,10))+"/10"; 
            case 6: if (facileDone) {return "1/1";}else{return "0/1";} 
            case 7: if (moyenDone) {return "1/1";}else{return "0/1";} 
            case 8: if (difficileDone) {return "1/1";}else{return "0/1";} 
            case 9: if (infernoDone) {return "1/1";}else{return "0/1";} 
            default : return "0/0";
        }
    }

    public void drawRectangles(Graphics2D g2) {
        for (int i = 0; i < Math.min(descriptionAch.size(),10); i++) {
            // coordonées
            int middleRectWidth = 300;
            int middleRectHeight = 75;
            int middleRectX, middleRectY;
    
            if (i < 5) {
                middleRectX = x+50;
                middleRectY = y+100+i*(middleRectHeight+20);
            } else {
                middleRectX = x+width-middleRectWidth-70;
                middleRectY = y+100+(i-5)*(middleRectHeight+20);
            }
    
            // Dessin du rectangle au milieu
            Color recColor = new Color(64,78,86);
            g2.setColor(recColor);
            g2.fillRect(middleRectX, middleRectY, middleRectWidth, middleRectHeight);
    
            // Dessin de la bordure du rectangle au milieu
            g2.setColor(Color.BLACK);
            g2.drawRect(middleRectX, middleRectY, middleRectWidth, middleRectHeight);
    
            // Dessin du carré à côté du rectangle
            int squareSize = 50;
            int squareX = middleRectX + middleRectWidth -10 -squareSize;
            int squareY = middleRectY + (middleRectHeight - squareSize) / 2;
            if (achievementDone[i]){
                g2.setColor(Color.WHITE);
            }else{
                g2.setColor(Color.GRAY);
            }
            g2.fillRect(squareX, squareY, squareSize, squareSize);
            g2.setColor(Color.BLACK);
            g2.drawRect(squareX, squareY, squareSize, squareSize);

            // Dessiner la description de l'achievement dans le rectangle
            String description = descriptionAch.get(i);
            g2.setColor(Color.WHITE);
            drawDescriptionLines(g2,description, middleRectX + 10, middleRectY + 21);

            // Dessiner le compteur du succès
            int counterX = middleRectX + middleRectWidth + 10;
            int counterY = middleRectY + (middleRectHeight / 2);
            g2.drawString(drawCompteurSucces(g2, i), counterX, counterY);

            // Dessiner la récompense dans le carré
            if (i<imageAch.size()){
                Image image = imageAch.get(i);
                image = image.getScaledInstance(50, 50,Image.SCALE_DEFAULT);
                g2.drawImage(image, squareX, squareY, null);
            }
        }
    }
    


    public int centerRectangleX(int totalWidth, int rectangleWidth) {
        return (totalWidth - rectangleWidth) / 2;
    }
    

    public Color getFillColor(){
        return this.fillColor;
    }
    public Color getTextColor(){
        return this.TextColor;
    }
    public Color getborderColor(){
        return this.borderColor;
    }
    public int getMonstresTués(){
        return this.monstresTués;
    }
    public int getManchesFinies(){
        return this.manchesFinies;
    }
    public int getItemsAchetés(){
        return this.itemsAchetés;
    }
    public int getAttaquesDébloquées(){
        return this.attaquesDébloquées;
    }
    public boolean isFacileDone(){
        return facileDone;
    }
    public boolean isMoyenDone(){
        return moyenDone;
    }
    public boolean isDifficileDone(){
        return difficileDone;
    }
    public boolean isInfernoDone(){
        return infernoDone;
    }
    public boolean[] getAchievementDone(){
        return achievementDone;
    }
    public void setAchievementsDone(boolean[] bool){
        achievementDone = bool;
    }
    public void setMonstresTués(int a){
        monstresTués=a;
        if (monstresTués>=100 && !achievementDone[0]){
            joueur.setGold(joueur.getGold()+20);
            achievementDone[0]=true;
        }
        if (monstresTués>=500 && !achievementDone[1]){
            joueur.setGold(joueur.getGold()+50);
            achievementDone[1]=true;
        }
    }
    public void setManchesFinies(int a){
        manchesFinies=a;
        if (manchesFinies>=10 && !achievementDone[2]){
            joueur.setGold(joueur.getGold()+20);
            achievementDone[2]=true;
        }
        if (manchesFinies>=100 && !achievementDone[3]){
            joueur.setGold(joueur.getGold()+50);
            achievementDone[3]=true;
        }
    }
    public void setItemsAchetés(int a){
        itemsAchetés=a;
        if (itemsAchetés>=15 && !achievementDone[4]){
            joueur.setGold(joueur.getGold()+20);
            achievementDone[4]=true;
        }
    }
    public void setAttaquesDébloquées(int a){
        attaquesDébloquées=a;
        if (itemsAchetés>=10 && !achievementDone[5]){
            joueur.setGold(joueur.getGold()+50);
            achievementDone[5]=true;
        }
    }
    public void setFacile(boolean b){
        facileDone=b;
        if (facileDone && !achievementDone[6]){
            joueur.setGold(joueur.getGold()+20);
            achievementDone[6]=true;
        }
    }
    public void setMoyen(boolean b){
        moyenDone=b;
        if (moyenDone && !achievementDone[7]){
            joueur.setGold(joueur.getGold()+20);
            achievementDone[7]=true;
        }
    }
    public void setDifficile(boolean b){
        difficileDone=b;
        if (difficileDone && !achievementDone[8]){
            joueur.setGold(joueur.getGold()+50);
            achievementDone[8]=true;
        }
    }
    public void setInferno(boolean b){
        infernoDone=b;
        if (facileDone && !achievementDone[9]){
            joueur.setGold(joueur.getGold()+50);
            achievementDone[9]=true;
        }
    }
}
