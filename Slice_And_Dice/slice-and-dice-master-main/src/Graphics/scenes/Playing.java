
package Graphics.scenes;

import java.awt.*;
import javax.swing.*;

import java.util.ArrayList;
import java.util.Random;
import java.util.Iterator;

import De.*;
import De.Capacite.CapaciteSpe;
import Controller.ui.*;
import Entites.*;
import Sauvegardes.SauvegardeCharge;
import main.*;

/* BUT:
*  Classe représentant l'interface graphique du jeu, contenant toutes les briques héros et ennemis, les briques de mana et les dés,
*  ainsi qu'une image de fond qui change au fur et à mesure que l'on passe le niveau. Il appelle les fonctions d'autres classes
*  lorsque l'on clique sur certains boutons.
*/

public class Playing implements SceneMethodes {

    //CLASSES
    private Toolbar toolbar;
    private Main main;
    private Difficulte difficulte;


    //POP-UP WINDOWS
    private Guide guide;
    private Boutique boutique;
    private Settings settings;
    private Inventaire inventaire;
    public SauvegardeCharge sauvegardeCharge;


    //BRICKS AND BUTTONS
    private MyButton buttonSettings, buttonGuide, buttonMenu, buttonBoutique , buttonInventaire, buttonSave, buttonReset, buttonCheat;
    private BrickHero[] brickHeroes = new BrickHero[5];
    private ArrayList<BrickEnnemis> brickEnnemis ;
    private BrickAttaqueMana[] brickMana = new BrickAttaqueMana[3];
    private BrickAmelioration[] bricksAmelioration;
    public boolean ameliorationOuverte=false;
    private BrickInfoHero brickInfoHero;
    private BrickInfoEnnemis brickInfoEnnemis;
    private boolean ecranDeFin = false;


    //GAME
    private Jeu combat;
    private Joueur joueur;


    //ENTITIES
    private Heros[] listeHeros = {new Archer(), new Epeiste(), new Tank(),new Mage(), new Guerisseur()};
    private Ennemis[] listeEnnemis = new Ennemis[5];

    //ANIMATION
    private int xStart, yStart, xEnd, yEnd;
    private double stepX, stepY;
    private int numFrame = 0;
    private boolean animation = false;
    private Face f;

    public Playing(Main main){  //ajouter Jeu jeu plus tard pour compléter la mise en place du déroulement du jeu
        this.main = main;
        this.difficulte = main.getMenu().difficulte;
        initClasses();
        this.joueur.setMonnaiePartie(100);

        //BRICK INITIALISATIONS
        initBrickHeroes();
        initSmallButtons();
        initBrickEnnemis();
        initBrickMana();
        initBrickAmelioration();

    }

    private void initClasses() {
        this.joueur = main.getJoueur();
        this.boutique = new Boutique(this);
        this.inventaire = new Inventaire(this);
        this.toolbar = new Toolbar(0, 640, 1200, 160,this);
        this.settings = main.getMenu().getSettings();
        this.guide = main.getMenu().getGuide();
        this.combat = new Jeu(this);
        this.sauvegardeCharge = new SauvegardeCharge(this);
    }

    public void initBrickHeroes() {
        int x = 10;
        int y = 110;
        int width = 250;
        int height = 80;
        int yOffset = 90;
        for(int i = 0; i < 5; i++){
            brickHeroes[i] = new BrickHero(listeHeros[i],x, y + yOffset*i, i, width, height);
            listeHeros[i].setBrickHero(brickHeroes[i]);
        }
    }

    public void initBrickEnnemis() {
        if (listeEnnemis.length>1 && listeEnnemis[1]!=null){
            listeEnnemis[1].setHimFront();
        }
        if (listeEnnemis.length>3 && listeEnnemis[3]!=null){
            listeEnnemis[3].setHimFront();
        }
        brickEnnemis = new ArrayList<>();
        int x = 900;
        int y = 110;
        int width = 250;
        int height = 80;
        int yOffset = 90;
        for (int i = 0; i < listeEnnemis.length; i++) {
            if (listeEnnemis[i].enVie()) {
                if (listeEnnemis[i].getIsFront()) {
                    brickEnnemis.add(new BrickEnnemis(listeEnnemis[i], x - 260, y + yOffset * i, i, width, height));
                } else {
                brickEnnemis.add(new BrickEnnemis(listeEnnemis[i], x, y + yOffset * i, i, width, height));
                }
            }
        }
    }

    private void initBrickMana() {
        ArrayList<CapaciteSpe> sorts = joueur.getListeSortsDisponibles();
        int x = 400;
        int y = 600;
        int width = 200;
        int height = 80;
        int yOffset = 90;
        for (int i = 0; i < brickMana.length; i++){
            if (sorts.get(i) != null){
                brickMana[i] = new BrickAttaqueMana(sorts.get(i), x + 204 * i , y + yOffset, i,  width, height, sorts.get(i).consommationMana,this.joueur, sorts.get(i).getNom());
            }
        }
    }

    private void initBrickAmelioration(){
        Random random = new Random();
        int n = random.nextInt(listeHeros.length);
        Heros h = listeHeros[n];
        int niveauActuel = h.getNiveauActuel();
        int x = 400;
        int y = 0;
        int yOffset = 330;
        if(niveauActuel == 1){
            this.bricksAmelioration = new BrickAmelioration[2];
            y = 80;
        }
        else if(niveauActuel == 2){
            this.bricksAmelioration = new BrickAmelioration[1];
            y = 250;
        }
        else{
            return;
        }
        for(int i = 0; i < bricksAmelioration.length; i++){
            String nom = h.getMapAmeliorations().get(h.getNomEntite())[i];
            De de = h.getMapDe().get(nom);
            bricksAmelioration[i] = new BrickAmelioration(x, y + yOffset*i, i, 400, 300, h, nom, de);
        }
    }

    @Override
    public void render(Graphics g) {
        
        if (combat != null && (combat.gameLoose || combat.gameWin)) {
            java.net.URL imageUrl = getClass().getClassLoader().getResource("Images/Backgrounds/NIVEAU7.jpg");
            ImageIcon sprite = new ImageIcon(imageUrl);
            Image img = sprite.getImage();
            g.drawImage(img, 0, 0, 1200, 800, null);
            String message = "";
            if(combat.gameWin){
                message="Victoire";
                g.setColor(Color.GREEN);
            }else{
                message="Défaite";
                g.setColor(Color.RED);
            }
            Font font = new Font("Arial", Font.BOLD, 60);
            g.setFont(font);
            FontMetrics fm = g.getFontMetrics();
            int x = (1200 - fm.stringWidth(message)) / 2;
            int y = (800 - fm.getHeight()) / 2 + fm.getAscent();
            g.drawString(message, x, y);
            buttonReset.draw(g);
            ecranDeFin=true;
        }
        else{
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, 1200, 800);
            drawBackground(g);
            drawButtons(g);
            drawBrickHeroes(g);
            drawBrickEnnemis(g);
            drawBrickMana(g);
            toolbar.draw(g);
            

            if(animation){
                
                animationDeplacementDeFrame(g, f, xStart, yStart, xEnd, yEnd, stepX, stepY);
                xStart += stepX;
                yStart += stepY;
                numFrame++;
            }

            if(brickInfoHero != null){
                drawBrickInfoHero(g);
            }
            if(brickInfoEnnemis != null){
                drawBrickInfoEnnemis(g);
            }
            if(combat!=null && combat.transition && bricksAmelioration!=null){
                drawBrickAmelioration(g);
                ameliorationOuverte=true;
                //Mettre combat.transition a false quand la brick d'amélioration se fermera
            }
            
        }
    }

    private void drawBackground(Graphics g){
        String niveau = String.valueOf(combat.getNiveauActuel());
        java.net.URL imageUrl = getClass().getClassLoader().getResource("Images/Backgrounds/NIVEAU" + niveau + ".jpg");
        ImageIcon sprite = new ImageIcon(imageUrl);
        Image img = sprite.getImage();
        g.drawImage(img, 0, 0, 1200, 800, null);
    }

    private void drawBrickInfoHero(Graphics g) {
        brickInfoHero.draw(g);
    }
    private void drawBrickInfoEnnemis(Graphics g) {
        brickInfoEnnemis.draw(g);
    }

    private void drawBrickAmelioration(Graphics g){
        for(BrickAmelioration b: bricksAmelioration){
            if(b != null){
                b.draw(g);
            }
        }
    }


    private void drawBrickMana(Graphics g) {
        for (BrickAttaqueMana c : brickMana){
            if (c != null){
                c.draw(g);
            }
        }
    }

    private void drawBrickHeroes(Graphics g) {
        for(BrickHero b : brickHeroes){
            b.draw(g);
        }
    }

    private void drawBrickEnnemis(Graphics g) {
    Iterator<BrickEnnemis> iterator = brickEnnemis.iterator();
    while (iterator.hasNext()) {
        BrickEnnemis b = iterator.next();
        if (b.getEnnemis() != null) {
            if (b.getEnnemis().enVie()) {
                b.draw(g);
            }
        }
    }
    }

    private void drawButtons(Graphics g){
        buttonGuide.draw(g);
        buttonSettings.draw(g);
        buttonMenu.draw(g);
        buttonBoutique.draw(g);
        buttonInventaire.draw(g);
        buttonSave.draw(g);
        buttonCheat.draw(g);
    }

    private void initSmallButtons(){
        int w = 50;
        int h = 50;
        int x = 485;
        int y = 10;
        int xOffset = 60;

        java.net.URL iconUrl1 = getClass().getClassLoader().getResource("Images/Icons/settings.png");
        java.net.URL iconUrl2 = getClass().getClassLoader().getResource("Images/Icons/manual.png");
        java.net.URL iconUrl3 = getClass().getClassLoader().getResource("Images/Icons/boutique.png");
        java.net.URL iconUrl4 = getClass().getClassLoader().getResource("Images/Icons/inventaire.png");
        java.net.URL iconUrl5 = getClass().getClassLoader().getResource("Images/Icons/save.png");
        java.net.URL iconUrl6 = getClass().getClassLoader().getResource("Images/Icons/home.png");

        ImageIcon settingsIcon = new ImageIcon(iconUrl1);
        ImageIcon guideIcon = new ImageIcon(iconUrl2);
        ImageIcon boutiqueIcon = new ImageIcon(iconUrl3);
        ImageIcon inventaireIcon = new ImageIcon(iconUrl4);
        ImageIcon saveIcon = new ImageIcon(iconUrl5);
        ImageIcon homeIcon = new ImageIcon(iconUrl6);
        
        buttonSettings = new MyButton(settingsIcon, x, y, w, h);
        buttonGuide = new MyButton(guideIcon, x + xOffset, y, w, h);
        buttonMenu = new MyButton(homeIcon, x + xOffset *2, y, w, h);
        buttonBoutique = new MyButton(boutiqueIcon, x + xOffset *3, y, w, h);
        buttonInventaire = new MyButton(inventaireIcon, x + xOffset *4, y, w, h);
        buttonSave = new MyButton(saveIcon, x + xOffset *5, y, w, h);
        buttonReset = new MyButton("Retourner au menu", (int)(1200/2.0)-100, 800-100, 200, 50);
        buttonCheat = new MyButton("12",1200-30,800-30,30,30);

    }

    public void mouseClicked(int x, int y) {
        if (ameliorationOuverte) {
            handleAmeliorationClick(x,y);
            return;
        }else{
            if (y >= 640 && x < 500) {
                toolbar.mouseClicked(x, y);
                return;
            }
            
            

            
            if (buttonGuide.getBounds().contains(x, y)) {
                EtatsJeu.setEtatJeu(EtatsJeu.GUIDEPLAYING);
                main.getAudio().playMouseclickSound();
                return;
            }
            if (buttonMenu.getBounds().contains(x, y)) {
                main.getAudio().playMouseclickSound();
                EtatsJeu.setEtatJeu(EtatsJeu.MENU);
                return;
            }
            if (buttonBoutique.getBounds().contains(x, y)) {
                EtatsJeu.setEtatJeu(EtatsJeu.BOUTIQUE);
                main.getAudio().playMouseclickSound();
                return;
            }
            if (buttonInventaire.getBounds().contains(x, y)) {
                EtatsJeu.setEtatJeu(EtatsJeu.INVENTAIRE);
                main.getAudio().playMouseclickSound();
                return;
            }
            
            if(buttonSave.getBounds().contains(x,y)){
                EtatsJeu.setEtatJeu(EtatsJeu.SAUVEGARDE);
                main.getAudio().playMouseclickSound();
                return;
            }
            if (buttonSettings.getBounds().contains(x, y)) {
                EtatsJeu.setEtatJeu(EtatsJeu.SETTINGSPLAYING);
                main.getAudio().playMouseclickSound();
                return;
            }
            if (ecranDeFin && buttonReset.getBounds().contains(x, y)){
                handleButtonResetClick();
                return;
            }
            if (buttonCheat.getBounds().contains(x, y)){
                handleButtonCheat();
                return;
            }
            if (isBrickDiceClicked(x, y)) {
                handleBrickDiceClick(x, y);
            }
            else{
                selectionAttaqueMana(x, y);
                dessineBrickInfoHeros(x, y);
                dessineBrickInfoEnnemis(x, y);
            }}
            
    }

    private void handleButtonCheat(){
        combat.cheat();
    }

    private void handleButtonResetClick(){
        EtatsJeu.setEtatJeu(EtatsJeu.MENU);
        main.getMenu().resetPlaying();
    }

    private void handleAmeliorationClick(int x, int y){
        for(int i = 0; i < bricksAmelioration.length; i++){
            if (bricksAmelioration[i].getBounds().contains(x,y)){
                
                
                quelleAmeliorationChoisie(bricksAmelioration[i] , i);
                initBrickAmelioration(); //pour voir si la proposition d'amélioration change entre les niveaux
                ameliorationOuverte=false;
                combat.transition=false;
            }
        }
    }

    private void quelleAmeliorationChoisie(BrickAmelioration briqueChoisie, int i)//fonction nécessaire pour vérifier la classe de héros choisie afin de l'améliorer
    {
        if(this.combat.verifAmeliorationLigneeMage(briqueChoisie, i))
        {
            return;
        }
        else if(this.combat.verifAmeliorationLigneeGuerisseur(briqueChoisie, i))
        {
            return;
        }
        else if(this.combat.verifAmeliorationLigneeArcher(briqueChoisie, i))
        {
            return;
        }
        else if(this.combat.verifAmeliorationLigneeEpeiste(briqueChoisie, i))
        {
            return;
        }
        else if(this.combat.verifAmeliorationLigneeTank(briqueChoisie, i))
        {
            return;
        }
    }

   

    private boolean isBrickDiceClicked(int x, int y) {
        return x >= 190 && x <= 190 + 60;
    }

    private void handleBrickDiceClick(int x, int y) {
        int yDice = 120;
        int size = 60;
        int yOffset = 90;
        for(int i = 0; i < 5; i++) {
            int currentY = yDice + yOffset * i;
            if (currentY <= y && currentY + size >= y) {
                if (listeHeros[i].aFaceSelectionnee() && !listeHeros[i].getDe().used && toolbar.getLancesDes()>0) { // vérifie que le héro a un dés séléctionné non utilisé

                    f = brickHeroes[i].getHero().getFaceSelectionnee();

                    xStart = brickHeroes[i].getX() + 180;
                    yStart = brickHeroes[i].getY();
                    
                    xEnd = toolbar.getBrickDice()[i].getX() + 10;
                    yEnd = toolbar.getBrickDice()[i].getY() + 10;
                    stepX = (xEnd - xStart) / (double) 60;
                    stepY = (yEnd - yStart) / (double) 60;
                    
                    animation = true;

                    listeHeros[i].setFaceSelectionnee(null); // remet dés dans toolbar
                    break;
                }
            }
        }
    }

    //ANIMATIONS
    private void animationDeplacementDeFrame(Graphics g, Face f, int xStart, int yStart, int xEnd, int yEnd, double stepX, double stepY) {
        if(numFrame == 60){
            
            Graphics2D g2d = (Graphics2D) g;
            
            f.draw(g2d, xEnd, yStart, 60);
            
            numFrame = 0;
            animation = false;
            return;
        }
        else{
            
            Graphics2D g2d = (Graphics2D) g;
            
            f.draw(g2d, xEnd, yStart, 60);
            
        }

    }

    private void selectionAttaqueMana(int x, int y) {  //Bind, Blaze, Balance, Burst, Drop, Flare, Infuse, Liquor, Renew, Scald, Revive
        if (toolbar.rollDone()){
            for(int i = 0; i < brickMana.length; i++){
                if(brickMana[i].getBounds().contains(x,y)){
                    switch (i){
                        case 0:
                            main.getAudio().playBurstSound();
                            break;
                        case 1:
                            main.getAudio().playFlareSound();
                            break;
                        case 2:
                            main.getAudio().playSparkleSound();
                            break;
                    }
                    if(joueur.getMana()>=brickMana[i].getMana().consommationMana){
                        if (combat.getBrickAttaqueMana()==null){
                            combat.setBrickAttaqueMana(brickMana[i]);
                            brickMana[i].setY(brickMana[i].getY()-20);
                            
                            combat.attaque();
                        }else{
                            combat.getBrickAttaqueMana().setY(combat.getBrickAttaqueMana().getY()+20);
                            if (combat.getBrickAttaqueMana()==brickMana[i]){
                                combat.setBrickAttaqueMana(null);
                                
                            }else{
                                combat.setBrickAttaqueMana(brickMana[i]);
                                brickMana[i].setY(brickMana[i].getY()-20);
                                
                                combat.attaque();
                            }
                        }
                    }
                    
                }
                
            }
        }
    }

    //DRAWING BRICKS
    private void dessineBrickInfoHeros(int x, int y) {
        
            for(int i = 0; i < brickHeroes.length; i++){
                if(brickHeroes[i].getBounds().contains(x,y)){
                    main.getAudio().playSwipeSound();
                    if(!toolbar.rollDone()){
                        if(brickInfoHero != null){
                            if(brickHeroes[i].getHero() == brickInfoHero.getHero()){ // si brickinfo est deja affiché supprime brique info
                                brickInfoHero = null;
                            }
                            else{
                                brickInfoHero = new BrickInfoHero(400, 200, i, 400, 300, brickHeroes[i].getHero());
                            }
                        }
                        else{
                            brickInfoEnnemis = null;
                            brickInfoHero = new BrickInfoHero(400, 200, i, 400, 300, brickHeroes[i].getHero());
                        }
                    }
                    else{ //Défini les heros selected si rollDone()
                        brickInfoHero =null;
                            if (combat.getHeroSelected()==null){ // premiere condition vérifie que aucun héro n'a été selectionné la deuxieme partie vérifie que l'attaque n'est pas null
                                if (brickHeroes[i].getHero().getFaceSelectionnee().getCapacite()==null){ //Pour quand même pouvoir soigner un héro avec attaque==null avec renew
                                    combat.setHeroSelected(brickHeroes[i].getHero());
                                    if (combat.attaque()){ //Au cas où attaqueMana car nécéssite qu'un seul héro selected
                                        combat.verifFinManche();
                                        combat.setHeroSelected(null);
                                    }else{
                                        combat.setHeroSelected(null);
                                    }
                                }else{
                                    if(!brickHeroes[i].getHero().getDe().used){ //Empêche l'utilisation des capacités plus d'une fois
                                        combat.setHeroSelected(brickHeroes[i].getHero());
                                        brickHeroes[i].setX(brickHeroes[i].getX()+30); //Déplace la brick vers la droite
                                        brickHeroes[i].selected=true;
                                        if (combat.attaque()){ //Au cas où attaqueMana car nécéssite qu'un seul héro selected
                                            combat.verifFinManche();
                                            brickHeroes[i].setX(brickHeroes[i].getX()-30); //Déplace la brick vers la gauche
                                            brickHeroes[i].selected=false;
                                            combat.setHeroSelected(null);
                                        }
                                    }
                                }
                            }else{
                                if(combat.getHeroSelected2()==null){
                                    combat.setHeroSelected2(brickHeroes[i].getHero());
                                    combat.attaque();
                                    combat.verifFinManche();
                                    for (int j=0;j<brickHeroes.length;j++){
                                        if (brickHeroes[j].selected){
                                            brickHeroes[j].setX(brickHeroes[j].getX()-30);
                                            brickHeroes[j].selected=false;
                                            combat.setHeroSelected(null);
                                        }
                                    } 
                                }
                            }
                    }
                }
            }
        //}
    }

    private void dessineBrickInfoEnnemis(int x, int y) {
        for(int i = 0; i < brickEnnemis.size(); i++){
            if(brickEnnemis.get(i).getBounds().contains(x,y)){
                main.getAudio().playSwipeSound();
                if(!toolbar.rollDone()){
                    if(brickInfoEnnemis != null){
                        if(brickEnnemis.get(i).getEnnemis() == brickInfoEnnemis.getEnnemis()){ // si brickinfo est deja affiché supprime brique info
                            brickInfoEnnemis = null;
                        }
                        else{
                            brickInfoEnnemis = new BrickInfoEnnemis(400, 200, i, 400, 300, brickEnnemis.get(i).getEnnemis());
                        }
                    }
                    else{
                        brickInfoHero = null;
                        brickInfoEnnemis = new BrickInfoEnnemis(400, 200, i, 400, 300, brickEnnemis.get(i).getEnnemis());
                    }
                }
                else{ //Défini les ennemis selected si rollDone()
                    brickInfoEnnemis =null;
                    if (combat.getEnnemiSelected()==null){
                        combat.setEnnemiSelected(brickEnnemis.get(i).getEnnemis());
                        combat.attaque();
                        combat.verifFinManche();
                        for (int j=0;j<brickHeroes.length;j++){
                            if (brickHeroes[j].selected){
                                brickHeroes[j].setX(brickHeroes[j].getX()-30);
                                brickHeroes[j].selected=false;
                                combat.setHeroSelected(null);
                            }
                        }
                    }
                }
            }
        }
    }


    @Override
    public void mouseMoved(int x, int y) {
        buttonSettings.setMouseOver(false);
        buttonGuide.setMouseOver(false);
        buttonMenu.setMouseOver(false);
        buttonBoutique.setMouseOver(false);
        buttonInventaire.setMouseOver(false);
        if(y >= 640 && x<500){
            toolbar.mouseMoved(x, y);
        }
        else if(buttonMenu.getBounds().contains(x, y)){
            buttonMenu.setMouseOver(true);
        }
        else if(buttonBoutique.getBounds().contains(x, y)){
            buttonBoutique.setMouseOver(true);
        }
        else if(buttonInventaire.getBounds().contains(x, y)){
            buttonInventaire.setMouseOver(true);
        }
        else if(buttonSettings.getBounds().contains(x, y)){
            buttonSettings.setMouseOver(true);
        }
        else if(buttonGuide.getBounds().contains(x, y)){
            buttonGuide.setMouseOver(true);
        }
        else{
            for(BrickEnnemis b : brickEnnemis){
                b.setMouseOver(false);
                if(b.getBounds().contains(x,y)){
                    b.setMouseOver(true);
                }
            }
            for(BrickHero b : brickHeroes){
                b.setMouseOver(false);
                if(b.getBounds().contains(x,y)){
                    b.setMouseOver(true);
                }
            }
            for (BrickAttaqueMana m : brickMana){
                m.setMouseOver(false);
                if(m.getBounds().contains(x,y)){
                    m.setMouseOver(true);
                }
            }
        }
    }

    @Override
    public void mousePressed(int x, int y) {
        if(y >= 640 && x<500){
            toolbar.mousePressed(x, y);
        }
        else if(buttonGuide.getBounds().contains(x, y)){
            buttonGuide.setMousePressed(true);
        }
        else if(buttonSettings.getBounds().contains(x, y)){
            buttonSettings.setMousePressed(true);
        }
        else if(buttonMenu.getBounds().contains(x, y)){
            buttonMenu.setMousePressed(true);
        }
        else if(buttonBoutique.getBounds().contains(x, y)){
            buttonBoutique.setMousePressed(true);
        }
    }

    @Override
    public void mouseReleased(int x, int y) {
        toolbar.mouseReleased(x,y);
        buttonSettings.resetBooleans();
        buttonGuide.resetBooleans();
        buttonMenu.resetBooleans();
        buttonBoutique.resetBooleans();
        buttonInventaire.resetBooleans();
    }

    @Override
    public void mouseDragged(int x, int y) {

    }


    //GETTERS AND SETTERS
    public Heros[] getListeHeros(){
        return listeHeros;
    }
    public Ennemis[] getListeEnnemis(){
        return listeEnnemis;
    }
    public void setListeEnnemis(Ennemis[] e){
        this.listeEnnemis=e;
    }
    public Main getMain(){
        return this.main;
    }
    public Toolbar getToolbar(){
        return this.toolbar;
    }
    public Joueur getJoueur(){
        return this.joueur;
    }
    public Inventaire getInventaire(){
        return this.inventaire;
    }
    public BrickHero[] getBrickHeroes() {
        return brickHeroes;
    }
    public Boutique getBoutique(){
        return boutique;
    }
    
    public Jeu getCombat(){
        return combat;
    }
    public Difficulte getDifficulte(){
        return difficulte;
    }
    public BrickAttaqueMana[] getBrickMana()
    {
        return this.brickMana;
    }
    public void setListeHeros(Heros[] h){
        this.listeHeros = h;
    }


}
