package Graphics.scenes;

import java.awt.*;
import javax.swing.*;

import Sauvegardes.SauvegardeChargeJoueur;

import main.EtatsJeu;
import main.*;
import Controller.ui.MyButton;
import main.Joueur;
import main.Difficulte;
import Entites.Heros;

/* BUT:
*  Classe contenant l'interface graphique du Menu, avec tous les boutons imprimés sur le Menu.
*/

public class Menu implements SceneMethodes {

    private Main main;
    private MyButton buttonEasy, buttonNormal, buttonHard, buttonQuit, buttonInferno;
    private MyButton buttonSettings, buttonGuide, buttonBoutique ,buttonAchievement, buttonCustom, buttonChoix;
    private Guide guide;
    private boolean guideOuverte = false;
    private Jeu jeu;
    private Playing playing;
    private BoutiqueMenu boutique;
    private Settings settings;
    private Achievement achievement;
    private HeroChoix heroChoix;
    private boolean choixOuvert = false;
    private Joueur joueur;
    public Difficulte difficulte;

    public Heros[] listeHeros = new Heros[5];

    public boolean playCinematique =false;
    
    public Menu(Main main){
        this.main = main;
        this.heroChoix = main.getHeroChoix();
        this.boutique = new BoutiqueMenu(this);
        this.settings = new Settings(main);
        this.joueur = main.getJoueur();
        this.achievement = joueur.achievement;
        this.guide = new Guide(main);
        initButtons();
        initSmallButtons();
        main.getAudio().playBackgroundSound();
    }

    //GESTION DES BUTTONS

    private void initButtons() {
        int w = 150;
        int h = w / 3;
        int x = 255;
        int y = 460;
        int xOffset = 180;
    
        buttonEasy = new MyButton("FACILE", x, y, w, h);
        buttonNormal = new MyButton("NORMAL", x+xOffset, y, w, h);
        buttonHard = new MyButton("DIFFICILE", x+xOffset*2, y, w, h);
        buttonInferno = new MyButton("INFERNO", x+xOffset*3, y, w, h);
        buttonQuit = new MyButton("QUITTER", 535, y + 100, w, h);
    }

    private void initSmallButtons(){
        int w = 50;
        int h = 50;
        int x = 10;
        int y = 10;
        int yOffset = 60;

        java.net.URL iconUrl1 = getClass().getClassLoader().getResource("Images/Icons/settings.png");
        java.net.URL iconUrl2 = getClass().getClassLoader().getResource("Images/Icons/manual.png");
        java.net.URL iconUrl3 = getClass().getClassLoader().getResource("Images/Icons/boutique.png");
        java.net.URL iconUrl4 = getClass().getClassLoader().getResource("Images/Icons/custom.png");
        java.net.URL iconUrl5 = getClass().getClassLoader().getResource("Images/Icons/achievement.png");
        java.net.URL iconUrl6 = getClass().getClassLoader().getResource("Images/Icons/choixHero.png");

        ImageIcon settingsIcon = new ImageIcon(iconUrl1);
        ImageIcon guideIcon = new ImageIcon(iconUrl2);
        ImageIcon boutiqueIcon = new ImageIcon(iconUrl3);
        ImageIcon customIcon= new ImageIcon(iconUrl4);
        ImageIcon achievementIcon = new ImageIcon(iconUrl5);
        ImageIcon choixHeroIcon = new ImageIcon(iconUrl6);

        buttonSettings = new MyButton(settingsIcon, x, y, w, h);
        buttonGuide = new MyButton(guideIcon, x, y + yOffset, w, h);
        buttonBoutique = new MyButton(boutiqueIcon, x, y + yOffset*2, w, h);
        buttonAchievement = new MyButton(achievementIcon, x, y + yOffset*3, w, h);
        buttonCustom = new MyButton(customIcon, 570, y, w, h);
        buttonChoix = new MyButton(choixHeroIcon, 630, y, w, h);

    }


    public void render(Graphics g){
    	try {
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, 1200, 800);
            drawBackground(g);
            drawButtons(g);
            if(choixOuvert){
                heroChoix.draw((Graphics2D) g);
            }
    	} 
    	catch (Exception e) {
    		e.printStackTrace();
    	}	
    }

    private void drawBackground(Graphics g) {
        java.net.URL imageUrl = getClass().getClassLoader().getResource("Images/Backgrounds/menu.png");
        ImageIcon sprite = new ImageIcon(imageUrl);
        Image img = sprite.getImage();
        g.drawImage(img, 0, 0, 1200, 800, null);
    }

    private void drawButtons(Graphics g){
        buttonEasy.draw(g);
        buttonNormal.draw(g);
        buttonHard.draw(g);
        buttonInferno.draw(g);
        buttonQuit.draw(g);
        buttonGuide.draw(g);
        buttonSettings.draw(g);
        buttonBoutique.draw(g);
        buttonCustom.draw(g);
        buttonAchievement.draw(g);
        buttonChoix.draw(g);
    }

    //GESTION DE LA SOURIS
    @Override
    public void mouseClicked(int x, int y) {   
        main.getAudio().playMouseclickSound();
        
        if(buttonBoutique.getBounds().contains(x,y)){
            main.getAudio().playMouseclickSound();
            EtatsJeu.setEtatJeu(EtatsJeu.BOUTIQUEMENU);
        }
        else if(buttonSettings.getBounds().contains(x,y)){
            main.getAudio().playMouseclickSound();
            EtatsJeu.setEtatJeu(EtatsJeu.SETTINGSMENU);
        }
        else if(buttonGuide.getBounds().contains(x,y)){
            main.getAudio().playMouseclickSound();
            EtatsJeu.setEtatJeu(EtatsJeu.GUIDEMENU);
        }
        else if(buttonAchievement.getBounds().contains(x,y)){
            main.getAudio().playMouseclickSound();
            EtatsJeu.setEtatJeu(EtatsJeu.ACHIEVEMENT);
        }
        if(choixOuvert){
            if(heroChoix.getClose().getBounds().contains(x,y)){
                main.getAudio().playMouseclickSound();
                choixOuvert = false;
            }
            else{
                heroChoix.mouseClicked(x, y);
            }
            return;
        }
        else if(buttonChoix.getBounds().contains(x,y)){
            main.getAudio().playMouseclickSound();
            if (!choixOuvert){
                choixOuvert = true;
            }
        }
        if(buttonEasy.getBounds().contains(x, y)){
            Difficulte nouvelleDifficulte = difficulte.FACILE;
            initPartie(nouvelleDifficulte);
            
        }
        else if(buttonNormal.getBounds().contains(x, y)){
            Difficulte nouvelleDifficulte = difficulte.MOYEN;
            initPartie(nouvelleDifficulte);
        }
        else if(buttonHard.getBounds().contains(x, y)){
            Difficulte nouvelleDifficulte = difficulte.DIFFICILE;
            initPartie(nouvelleDifficulte);
        }
        else if(buttonInferno.getBounds().contains(x, y)){
            Difficulte nouvelleDifficulte = difficulte.INFERNO;
            initPartie(nouvelleDifficulte);
        }
        else if(buttonCustom.getBounds().contains(x,y)){
            EtatsJeu.setEtatJeu(EtatsJeu.CUSTOM);
            main.getCustom().setState("Home");
            main.getCustom().getHomePage().getHeroPanel().initXY(570,200);
        }
        //ajouter le bouton inferno, débloquable après une partie en normal
        else if(buttonQuit.getBounds().contains(x, y)){
            SauvegardeChargeJoueur sc = new SauvegardeChargeJoueur(this.main);
            sc.sauvegarde(joueur);
            System.exit(0);
        }
    }

    public void initPartie(Difficulte nouvelleDifficulte){ //vérifie si une partie est déjà en cours et en créé une nouvelle si celle-ci n'est pas de la mm dificulté
        
        if(playing!=null){ // partie deja en cours
            if (difficulte!=nouvelleDifficulte){ //partie d'une autre difficulté
                changeEtatJeuCinematique(nouvelleDifficulte);
                
            }
            else{ // partie d'une mm dificulté
                EtatsJeu.setEtatJeu(EtatsJeu.PLAYING);
            }
        }
        else{ // pas de partie lancée
            changeEtatJeuCinematique(nouvelleDifficulte);
            
        }
             
    }

    public void changeEtatJeuCinematique(Difficulte nouvelleDifficulte){
        EtatsJeu.setEtatJeu(EtatsJeu.CINEMATIQUE);
        difficulte = nouvelleDifficulte;
        main.getAudio().stopBackgroundSound();
        
    }
    public void initPlaying(){
        
        playing = new Playing(main);
        if(listeHeros[4]!=null){
            playing.setListeHeros(listeHeros);
            playing.initBrickHeroes();
        }
    }

    @Override
    public void mouseMoved(int x, int y) {
        buttonEasy.setMouseOver(false);
        buttonNormal.setMouseOver(false);
        buttonHard.setMouseOver(false);
        buttonInferno.setMouseOver(false);
        buttonQuit.setMouseOver(false);
        buttonSettings.setMouseOver(false);
        buttonGuide.setMouseOver(false);
        buttonBoutique.setMouseOver(false);
        buttonCustom.setMouseOver(false);
        buttonAchievement.setMouseOver(false);
        buttonChoix.setMouseOver(false);
        if(choixOuvert){
            heroChoix.mouseMoved(x,y);
        }
        if(buttonEasy.getBounds().contains(x, y)){
            buttonEasy.setMouseOver(true);
        }
        else if(buttonNormal.getBounds().contains(x, y)){
            buttonNormal.setMouseOver(true);
        }
        else if(buttonHard.getBounds().contains(x, y)){
            buttonHard.setMouseOver(true);
        }
        else if(buttonInferno.getBounds().contains(x, y)){
            buttonInferno.setMouseOver(true);
        }
        else if(buttonQuit.getBounds().contains(x, y)){
            buttonQuit.setMouseOver(true);
        }
        else if(buttonSettings.getBounds().contains(x, y)){
            buttonSettings.setMouseOver(true);
        }
        else if(buttonGuide.getBounds().contains(x, y)){
            buttonGuide.setMouseOver(true);
        }
        else if(buttonBoutique.getBounds().contains(x, y)){
            buttonBoutique.setMouseOver(true);
        }
        else if(buttonCustom.getBounds().contains(x, y)){
            buttonCustom.setMouseOver(true);
        }
        else if(buttonAchievement.getBounds().contains(x, y)){
            buttonAchievement.setMouseOver(true);
        }
        else if(buttonChoix.getBounds().contains(x,y)){
            buttonChoix.setMouseOver(true);
        }
    }

    @Override
    public void mousePressed(int x, int y) {
        if(buttonEasy.getBounds().contains(x, y)){
            buttonEasy.setMousePressed(true);
        }
        else if(buttonNormal.getBounds().contains(x, y)){
            buttonNormal.setMousePressed(true);
        }
        else if(buttonHard.getBounds().contains(x, y)){
            buttonHard.setMousePressed(true);
        }
        else if(buttonInferno.getBounds().contains(x, y)){
            buttonInferno.setMousePressed(true);
        }
        else if(buttonQuit.getBounds().contains(x, y)){
            buttonQuit.setMousePressed(true);
        }
        else if(buttonGuide.getBounds().contains(x, y)){
            buttonGuide.setMousePressed(true);
        }
        else if(buttonSettings.getBounds().contains(x, y)){
            buttonSettings.setMousePressed(true);
        }
        else if(buttonBoutique.getBounds().contains(x, y)){
            buttonBoutique.setMousePressed(true);
        }
        else if(buttonCustom.getBounds().contains(x, y)){
            buttonCustom.setMousePressed(true);
        }
        else if(buttonAchievement.getBounds().contains(x, y)){
            buttonAchievement.setMousePressed(true);
        }
        else if(buttonChoix.getBounds().contains(x,y)){
            buttonChoix.setMousePressed(true);
        }
    }

    @Override
    public void mouseReleased(int x, int y) {
        resetButtons();
    }

    private void resetButtons(){
        buttonEasy.resetBooleans();
        buttonNormal.resetBooleans();
        buttonHard.resetBooleans();
        buttonInferno.resetBooleans();
        buttonQuit.resetBooleans();
        buttonSettings.resetBooleans();
        buttonGuide.resetBooleans();
        buttonBoutique.resetBooleans();
        buttonCustom.resetBooleans();
        buttonAchievement.resetBooleans();
        buttonChoix.resetBooleans();
    }

	@Override
	public void mouseDragged(int x, int y) {
		// TODO Auto-generated method stub
	}

    public Guide getGuide() {
        return guide;
    }

    public Main getMain(){
        return this.main;
    }
    public Playing getPlaying(){
        return this.playing;
    }
    public void resetPlaying(){
        this.playing=null;
    }
    public Joueur getJoueur(){
        return this.joueur;
    }
    public Settings getSettings(){
        return this.settings;
    }
    public BoutiqueMenu getBoutiqueMenu(){
        return this.boutique;
    }
}

