package Graphics.scenes.Custom;

import Graphics.scenes.SceneMethodes;
import Controller.ui.*;
import Controller.ui.Custom.*;
import main.*;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.*;

public class CustomNamePage implements SceneMethodes {
    private static int WIDTH = 1200;
    private static int HEIGHT = 800;
    public String name, classe, text;
    private Custom custom;
    private Main main;
    private CustomHelp help;
    private CustomHeroPanel heropanel;
    private boolean helpOpen = false, vide = true, suivant = false;
    private MyButton buttonHelp, buttonPrevious, buttonContinue;


    private final Color backgroundColor = new Color(0,23,30);
    private final Color fillColor = new Color(0, 43, 58, 232);
    //private final Color borderColor = new Color(107,111,127);
    private final Color textColor = new Color(255,252,240);
    float strokeWidth = 3.0f; // Épaisseur du trait en pixels
    BasicStroke stroke = new BasicStroke(strokeWidth);
    private JFrame frame;
    private JPanel panel;

    JTextField textField1, textField2, textfield3;


    public CustomNamePage(Custom custom) {
        this.custom = custom;
        this.main = custom.getMain();    
        this.help = custom.getCustomHelp();
        this.heropanel = custom.getHomePage().getHeroPanel();
        this.textField1 = this.textField2 = textfield3 = new JTextField();
        name = classe = text = "";

        initSmallButtons();
        initTextFields();
        initFrame();
    }

    //partie vue

    private void initSmallButtons() {
        this.buttonHelp = new MyButton("AIDE", (WIDTH/5 + 30), 20, 100, 50, fillColor, textColor, backgroundColor);
        this.buttonPrevious = new MyButton("PRÉCÉDENT", 5, 20, 100, 50, fillColor, textColor, backgroundColor);
        this.buttonContinue = new MyButton("SUIVANT", WIDTH-105, 20, 100, 50, fillColor, textColor, backgroundColor);
    }

    public void initTextFields(){
        Rectangle rec1 = new Rectangle(500, 500, 400, 40);
        Rectangle rec2 = new Rectangle(500, 550, 400, 40);
        this.textField1.setBounds(rec1);
        this.textField2.setBounds(rec2);
    }

    public void initFrame(){
        frame = new JFrame("Custom Name Page");
        frame.setSize(400, 200);

        // Créer un JPanel
        panel = new JPanel();
        panel.setLayout(null); // Désactiver le gestionnaire de disposition par défaut

        // Créer les champs de texte
        textField1 = new JTextField();
        textField2 = new JTextField();

        textField1.setBounds(50, 50, 200, 30);
        textField2.setBounds(50, 100, 200, 30);
        panel.add(textField1);
        panel.add(textField2);

        //frame.setLocationRelativeTo(null);
        frame.setLocation(450, 500);
        main.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentMoved(ComponentEvent e) {
                super.componentMoved(e);
                // Mettre à jour la position de la pop-up lorsque la fenêtre principale est déplacée
                frame.setLocation(main.getX() + 450, main.getY() + 500);
            }
        });
        Dimension panelSize = new Dimension(200, 200);
        panel.setBounds((WIDTH - panelSize.width) / 2, (HEIGHT - panelSize.height) / 2, panelSize.width, panelSize.height);

        panel.setBackground(backgroundColor); 
        frame.setUndecorated(true);
        frame.add(panel);
    }

    private void drawTextArea(Graphics2D g2) {
        //text
        text = "Vous n'avez adoubé aucun nom ni classe pour votre héros!";
        
        g2.setColor(textColor);
        g2.setFont(new Font("Courier", Font.BOLD, 22));
        int xStart = 200;
        int yStart = 420;
        int lineHeight = g2.getFontMetrics().getHeight();
        if(text != null) {
            for (String line : text.split("\n")) {
                g2.drawString(line, xStart, yStart += lineHeight);
            }
        }
    }

    @Override
    public void render(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(backgroundColor);
        g2d.fillRect(0, 0, WIDTH, HEIGHT);
        g2d.setStroke(stroke);
        if (vide && suivant){
            drawTextArea(g2d);
        }

        drawButtons(g);
        heropanel.draw(g2d);


        if (helpOpen){
            help.draw(g2d);
        }
    }

    public void renderPanel(){
        frame.setVisible(true);
    }

    private void drawButtons(Graphics g) {
        buttonHelp.draw(g);
        buttonPrevious.draw(g);
        buttonContinue.draw(g);
    }

    public void getNameClass(){
        name = textField1.getText();
        classe = textField2.getText();
    }


    @Override
    public void mouseClicked(int x, int y) {
        verifVide();
        custom.getMain().getAudio().playMouseclickSound();
        //System.out.println(x);
        //System.out.println(y);
        if (buttonHelp.getBounds().contains(x, y)){
            //vers le panneau Help
            if(!helpOpen){
                helpOpen = true;
            }
            else if(helpOpen){
                helpOpen = false;
            }
        }
        else if(helpOpen){
            if(help.getClose().getBounds().contains(x,y)){
                custom.getMain().getAudio().playMouseclickSound();
                helpOpen = false;
            }
            else{
                help.mouseClicked(x, y);
            }
        }
        else if(buttonPrevious.getBounds().contains(x,y)){
            custom.setState("Dice");
            frame.setVisible(false);
            getNameClass();
        }
        else if(buttonContinue.getBounds().contains(x,y) && !vide){
            custom.setState("Final");
            heropanel.initXY(200,200);
            frame.setVisible(false);
            getNameClass();
        }
    }

    @Override
    public void mouseMoved(int x, int y) {
        buttonHelp.setMouseOver(false);
        buttonPrevious.setMouseOver(false);
        buttonContinue.setMouseOver(false);
        if (buttonHelp.getBounds().contains(x, y)) {
            buttonHelp.setMouseOver(true);
        }
        else if(helpOpen){
            help.mouseMoved(x, y);
        }
        else if(buttonPrevious.getBounds().contains(x,y)){
            buttonPrevious.setMouseOver(true);
        }
        else if(buttonContinue.getBounds().contains(x,y) && !vide){
            buttonContinue.setMouseOver(true);
        }
    }

    @Override
    public void mousePressed(int x, int y) {
        if (buttonHelp.getBounds().contains(x, y)) {
            buttonHelp.setMousePressed(true);
        }
        else if(helpOpen){
            help.mousePressed(x, y);
        }
        else if(buttonPrevious.getBounds().contains(x,y)){
            buttonPrevious.setMousePressed(true);
        }
        else if(buttonContinue.getBounds().contains(x,y)){
            suivant = true;
            if (!vide){
                buttonContinue.setMousePressed(true);
            }
        }
    }

    @Override
    public void mouseReleased(int x, int y) {
        buttonHelp.resetBooleans();
        buttonPrevious.resetBooleans();
        buttonContinue.resetBooleans();
        if(helpOpen){
            help.mouseReleased(x, y);
        }
    }

    @Override
    public void mouseDragged(int x, int y) {
    }

    private void verifVide(){
        if (!isEmpty(textField1) && !isEmpty(textField2)){
            //System.out.println(vide);
            vide = false;
            return;
        }
        //System.out.println("passé par là");
        vide = true;
        return;
    }

    public boolean isEmpty(JTextField t){
        if(t.getText().isEmpty()){
            return true;
        }
        return false;
    }

    //getters
    public static int getWIDTH() {
        return WIDTH;
    }
    public static int getHEIGHT() {
        return HEIGHT;
    }

    public String getName(){
        return name;
    }
    public String getClasse(){
        return classe;
    }
}

