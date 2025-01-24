package Controller.ui;

import Graphics.scenes.Playing;

import java.awt.*;

/* BUT:
*  Ce code définit un élément d'interface utilisateur représentant les dés dans une barre d'outils de jeu.
*  Chaque brique correspond au dé d'un héros spécifique, affichant la face du dé. Les dés peuvent être lancés et pendant
*  l'animation de roulement, chaque dé tourne pour simuler le mouvement de roulement.
*/

public class BrickDice {
    private int x, y, id, size;
    private Rectangle bounds;
    private Image icon;
    private int[] diceAngles;
    private boolean rolling = false;
    private Playing playing;
    public BrickDice(int x, int y, int id, int size,Playing p) {
        this.x = x;
        this.y = y;
        this.id = id;
        this.size = size;
        this.playing = p;
        this.diceAngles = new int[playing.getListeHeros().length];

        initBounds();
    }

    private void initBounds() {
        this.bounds = new Rectangle(x,y,size,size);
    }

    public void draw(Graphics g){ //Dessine les des dans toolbar
        Graphics2D g2d = (Graphics2D) g.create();

        // Translate and rotate the graphics context
        g2d.translate(x + size / 2, y + size / 2);
        g2d.rotate(Math.toRadians(diceAngles[id]));

        // Draw the dice
        if(playing.getListeHeros()[id].aFaceSelectionnee()){  // dessine un dés vide si le héros a déjà une face séléctionnée
            g2d.setColor(new Color(0x2A0202));
            g2d.fillRoundRect(-size / 2, -size / 2, size, size, 10, 10);
        }
        else{ // dessine la face du haut du dés si non
            playing.getListeHeros()[id].getDe().draw(g2d, -size / 2, -size / 2,size);
        }
        g2d.setColor(playing.getListeHeros()[id].getColor());
        g2d.drawRoundRect(-size / 2, -size / 2, size, size, 10, 10);
        g2d.dispose();
    }

    public void rollDices() { //fonction qui permet de lancer les des
        rolling = true;
        final int targetAngle = 360;
        final int duration = 1000;
        final int framesPerSecond = 120;
        final int frameCount = duration * framesPerSecond / 1000;

        Thread animationThread = new Thread(() -> {
            for (int frame = 1; frame <= frameCount; frame++) {
                double progress = (double) frame / frameCount; // Animation progress between 0 and 1
                int currentAngle = (int) (progress * targetAngle); // Current angle for this frame
                for (int i = 0; i < playing.getListeHeros().length; i++) {
                    diceAngles[i] = currentAngle; // Set angle for all dice
                    if(!playing.getListeHeros()[i].aFaceSelectionnee()){
                        playing.getListeHeros()[i].getDe().lancerDes();
                    }
                }
                try {
                    Thread.sleep(duration / frameCount);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            rolling = false;
        });
        animationThread.start();
    }

    public Rectangle getBounds(){
        return this.bounds;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}

