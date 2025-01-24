package Equipement;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import De.Face;
import Entites.Heros;
import Entites.Heros;

public class Equipement { //classe mère de tous les équipement gère l'affichage de ceux-ci
    public String nom, description;
    public Heros perso;
    public int prix, niveau;
    public BufferedImage image;
    //Ajouter photo de l'équipement

    public Equipement(String nom, String description, int prix, int niveau){
        this.nom = nom;
        this.description = description;
        this.prix = prix;
        this.niveau = niveau;
    }

    public void effet(){}
    public void annuleEffet(){}

    public void draw(Graphics2D g2,int x, int y,int size){
        if(image!=null){
            g2.drawImage(image, x, y,size,size,null);
        }
        else{
            System.out.println("Pas d'image.");
        }
    }

    public void drawInCarre(Graphics2D g2,int x, int y,int size,Color color){
        if(image!=null){
            g2.setColor(color);
            
            g2.drawImage(image, x, y,size,size,null);
            g2.drawRect(x, y, size, size);
        }
        else{
            System.out.println("Pas d'image.");
        }
    }

    public void drawDescription(Graphics2D g2,int x, int y, int size,Color borderColor, Color fillColor,Color textColor){
        g2.setColor(fillColor);
        g2.fillRect(x, y,size*2, size);

        g2.setColor(borderColor);
        g2.drawRect(x, y, size*2, size);

        g2.setColor(textColor);
        drawTextInRectangle(g2,this.description,x,y,size*2,size);
    }

    private void drawTextInRectangle(Graphics2D g2d, String text, int x, int y, int width, int height) {
        FontMetrics fm = g2d.getFontMetrics();

        // Séparer le texte en lignes pour s'assurer qu'il ne dépasse pas la largeur du rectangle
        String[] lines = getWrappedTextLines(text, fm, width);

        // Dessiner chaque ligne de texte à l'intérieur du rectangle
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


    public BufferedImage returnImage(){
        return this.image;
    }
    public static void main(String[] args) {
        
    }

}
