package Controller.ui;

import javax.swing.*;
import java.awt.*;

/* BUT:
 * Ce code est utilisé pour créer des boutons, qui sont ensuite implémentés dans d'autres parties du code de différentes manières
 * (les uns avec des icônes, les autres avec du texte dessus).
 */

public class MyButton extends JButton{
    private Image icon;

    private int x,y,width,height, id;
    private String text;
    private String name;
    private Rectangle bounds;
    private boolean mouseOver;
    private boolean mousePressed;

    private Color buttonColor;
    private Color textColor;
    private Color fillColor;

    public MyButton(String text, int x, int y, int width, int height){
        this.text = text;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.id = -1;
        initBounds();
    }
    
	//pour case button
    public MyButton(String text, int x, int y, int width, int height, int id){
        this.text = text;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.id = id;
        initBounds();
    }

    public MyButton(ImageIcon icon, int x, int y, int width, int height) {
        this.icon = icon.getImage();
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        initBounds();
    }

    public MyButton(ImageIcon icon, int x, int y, int width, int height, Color buttonColor, Color fillColor) {
        this.icon = icon.getImage();
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.buttonColor = buttonColor;
        this.fillColor = fillColor;
        initBounds();
    }

    public MyButton(ImageIcon icon, int x, int y, int width, int height, Color buttonColor, Color textColor, Color fillColor) {
        this.icon = icon.getImage();
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.buttonColor = buttonColor;
        this.fillColor = fillColor;
        this.textColor = textColor;
        initBounds();
    }

    public MyButton(ImageIcon icon, int x, int y, int width, int height, Color buttonColor, Color fillColor, String name) {
        this.icon = icon.getImage();
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.buttonColor = buttonColor;
        this.fillColor = fillColor;
        this.name = name;
        initBounds();
    }

    //initialiser un bouton avec une couleur differente
    public MyButton(String text, int x, int y, int width, int height, Color buttonColor, Color textColor) {
        this.text = text;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.buttonColor = buttonColor;
        this.textColor = textColor;
        initBounds();
    }

    public MyButton(String text, int x, int y, int width, int height, Color buttonColor, Color textColor, Color fillColor) {
        this.text = text;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.buttonColor = buttonColor;
        this.textColor = textColor;
        this.fillColor = fillColor;
        initBounds();
    }

    public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
    
    public int getId() {
    	return this.id;
    }
    
    public String getText() {
		return text;
	}
    public String getName(){return name;}

    public void draw(Graphics g){
        //body
        drawBody(g);

        //border
        drawBorder(g);

        //text
        if(this.text != null){
            drawText(g);
        }

        //icon
        if(this.icon != null){
            paintComponent(g);
        }
    }

    private void drawBorder(Graphics g){
        Graphics2D g2d = (Graphics2D) g;
        if(this.buttonColor == null){
            g2d.setColor(new Color(144, 0, 0));
        }
        else{
            g2d.setColor(buttonColor);
        }
        
        g2d.drawRoundRect(x, y, width, height, 10, 10); // Adjust the arc width and height as needed
        if (mousePressed) {
            g2d.drawRoundRect(x + 1, y + 1, width - 2, height - 2, 10, 10); // Adjust the arc width and height as needed
            g2d.drawRoundRect(x + 2, y + 2, width - 4, height - 4, 10, 10); // Adjust the arc width and height as needed
        }
    }

    private void drawBody(Graphics g){
        Graphics2D g2d = (Graphics2D) g;
        if (mouseOver) {
            if(fillColor == null){
                g2d.setColor(new Color(0x2A0202));
            }
            else{
                g2d.setColor(fillColor);
            }
        } else {
            if(buttonColor == null){
                g2d.setColor(new Color(0x000000));
            }
            else{
                g2d.setColor(buttonColor);
            }
        }
        g2d.fillRoundRect(x, y, width, height, 10, 10);
    }

    private void drawText(Graphics g) {
        if(textColor == null){
            g.setColor(new Color(144,0,0));
        }
        else{
            g.setColor(textColor);
        }
        g.setFont(new Font("Courier", Font.BOLD, 16));
        int w = g.getFontMetrics().stringWidth(text);
        int h = g.getFontMetrics().getHeight();
        g.drawString(text, x + (width - w) / 2, y + (height - h) / 2 + h);
    }

    protected void paintComponent(Graphics g){
        if (icon != null) {
            g.drawImage(icon, x, y, width, height, this);
        }
    }

    public void resetBooleans(){
        this.mouseOver = false;
        this.mousePressed = false;
    }

    public void setMouseOver(boolean mouseOver){
        this.mouseOver = mouseOver;
    }
    
    public boolean isOver() {
    	return this.mouseOver;
    }
    
    public boolean isPressed() {
    	return this.mousePressed;
    }

    public void setMousePressed(boolean mousePressed){
        this.mousePressed = mousePressed;
    }

    public void setText(String text){
        this.text=text;
    }

    public void setX(int x) {
        this.x = x;
        initBounds();
    }

    public void setY(int y) {
        this.y = y;
        initBounds();
    }

    public void setButtonColor(Color buttonColor) {
        this.buttonColor = buttonColor;
    }

    public Rectangle getBounds(){
        return this.bounds;
    }

    public void initBounds(){
        this.bounds = new Rectangle(x, y, width, height);
    }

    public void resetButtons() {
    }
}
