package Graphics.scenes;

import java.awt.*;

import main.Main;
import Controller.ui.MyButton;

/* BUT:
*  Cette classe crée une fenêtre pour les paramètres, qui s'imprime au milieu de l'écran lorsque l'on clique sur l'icône dans le
*  menu ou pendant le jeu. Nous pouvons régler le volume ou couper la musique de fond.
*/

public class Settings extends MenuMethods{
    
    private Main main;
    private MyButton muteButton;
    private MyButton volumeUp;
    private MyButton volumeDown;
    


    boolean hover = false;
    int xMouse = 0;
    int yMouse = 0;
    int hoverIndex = -1;

    float strokeWidth = 3.0f; // Épaisseur du trait en pixels
    BasicStroke stroke = new BasicStroke(strokeWidth);
    AlphaComposite alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f); //opacité du remplissage (transparant)
    AlphaComposite opaque = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f); //opacité du remplissage (opaque)

    public Settings(Main main){
        super(main, 360, 850, "SETTINGS");
        this.main = main;
        initButtons();
    }

    private void initButtons() {
        int w = 150;
        int h = w / 3;
        int xOffset = 160;
        volumeDown = new MyButton("-", 365, 400, w, h, borderColor, TextColor, fillColor);
        muteButton = new MyButton("MUTE", 365 + xOffset, 400, w, h, borderColor, TextColor, fillColor);
        volumeUp = new MyButton("+", 365 + xOffset*2, 400, w, h, borderColor, TextColor, fillColor);
    }



    public void draw(Graphics2D g2){
        super.draw(g2);
        
        drawButtons(g2);
    }

    private void drawButtons(Graphics2D g) {
        String text = "Contrôle niveau sonore";
        int length = (int)g.getFontMetrics().getStringBounds(text,g).getWidth();
        int rectY = 350;
        g.drawRoundRect(350, 350, 500, 120, 10, 10);
        g.drawString(text, x + width / 2 - length/2, rectY + 30);
        muteButton.draw(g);
        volumeUp.draw(g);
        volumeDown.draw(g);
    }

   


    public int centerRectangleX(int totalWidth, int rectangleWidth) {
        return (totalWidth - rectangleWidth) / 2;
    }

    public void mouseClicked(int x, int y) {
        super.mouseClicked(x, y);
        main.getAudio().playMouseclickSound();
        if(muteButton.getBounds().contains(x, y)){
            main.getAudio().volumeMute();
            if(main.getAudio().isMute()){
                muteButton.setText("UNMUTE");
            }
            else{
                muteButton.setText("MUTE");
            }
        }
        else if(volumeDown.getBounds().contains(x, y)){
            main.getAudio().volumeDown();
        }
        else if(volumeUp.getBounds().contains(x, y)){
            main.getAudio().volumeUp();
        }
    }

    public void mouseMoved(int x, int y) {
        muteButton.setMouseOver(false);
        volumeUp.setMouseOver(false);
        volumeDown.setMouseOver(false);
        if(muteButton.getBounds().contains(x, y)){
            muteButton.setMouseOver(true);
        }
        else if(volumeUp.getBounds().contains(x, y)){
            volumeUp.setMouseOver(true);
        }
        else if(volumeDown.getBounds().contains(x, y)){
            volumeDown.setMouseOver(true);
        }
    }

    public void mousePressed(int x, int y) {
        if(muteButton.getBounds().contains(x, y)){
            muteButton.setMousePressed(true);
        }
        else if(volumeUp.getBounds().contains(x, y)){
            volumeUp.setMousePressed(true);
        }
        else if(volumeDown.getBounds().contains(x, y)){
            volumeDown.setMousePressed(true);
        }
    }

    public void mouseReleased(int x, int y) {
        muteButton.resetBooleans();
        volumeDown.resetBooleans();
        volumeUp.resetBooleans();
    }

    public void mouseDragged(int x, int y) {

    }
    
 
}

