package Graphics;

import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import Controller.inputs.KeyboardListener;
import Controller.inputs.MyMouseListener;
import main.Main;

/* BUT
 * Cette classe sert de panneau de jeu dans lequel les graphiques sont rendus, et elle initialise et gère 
 * les écouteurs d'entrée pour les événements de souris et de clavier. Le rendu lui-même est délégué à la 
 * méthode render de l'instance Jeu associée. La taille préférée du panneau est définie sur 640 x 800 pixels.
 */

public class GamePanel extends JPanel{
    private Main main;
    private Dimension size;
    private int WIDTH = 1200;
    private int HEIGHT = 800;

    private MyMouseListener myMouseListener;
    private KeyboardListener keyboardListener;

    private Image[] icons = new Image[5];

    public GamePanel(Main main){
        this.main = main;
        setPaneneauSize();
    }

    /*
     * Initialise les écouteurs de saisie de la souris et du clavier, les ajoute au panneau et demande 
     * le focus pour la saisie au clavier.
     */
    public void initInputs(){
        myMouseListener = new MyMouseListener(main);
        keyboardListener = new KeyboardListener(main);

        addMouseListener(myMouseListener);
        addMouseMotionListener(myMouseListener);
        addKeyListener(keyboardListener);

        requestFocus(); 
        //pour assurer que le panneau est prêt à gérer les événements de saisie au clavier lorsque l'utilisateur interagit 
        //avec l'application
    }

    private void setPaneneauSize(){
        this.size = new Dimension(WIDTH, HEIGHT);
        setMinimumSize(size);
        setPreferredSize(size);
        setMaximumSize(size);
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        main.getRender().render(g);
    }

    public int getWidth(){
        return this.WIDTH;
    }

    public int getHeight(){
        return this.HEIGHT;
    }

}

