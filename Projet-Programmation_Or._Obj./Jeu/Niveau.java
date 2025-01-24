package Jeu;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.Semaphore;
import java.awt.Color;
import java.awt.Graphics2D;

import Personnages.Personnages;
import Personnages.Soldat;
import Personnages.Ennemis.*;
import Tours.*;

public class Niveau extends Thread implements Runnable{
    public Plateau p;
    public Case[] chemin;
    public volatile boolean isRunning = true;
    public volatile boolean enPause = false;
    public volatile boolean wavePause = false;
    public volatile boolean marathonRunning = true;

    public boolean marathon = false;
    public int score = 0;

    public boolean next = false;
    public boolean caseSelectionnee = false;
    public boolean acheterTours = false;

    Color c  = new Color(248, 208, 144);
    public int xSelectTours;
    public int ySelectTours;
    public boolean affichageFin = false;
    
    public int posWave = 0;
    ArrayList <Caserne> listeCasernes = new ArrayList<Caserne>();
    private Semaphore caserneSemaphore = new Semaphore(1);

    //Test pour le niveau 1
    public int[] posChemin; //= {23,38,53,68,67,66,65,80,95,110,125,140,155,170,171,172,173,174,175,176,177,192,207};
    public int nbNiveaux = 3;
    public int numNiveau =0;
    //wave
    public int[] timing;
    public Personnages[] wave;

    public GamePanel gp;

    Thread threadNiveau;
    private Scanner scanner;

    Niveau(){
        scanner = new Scanner(System.in);
        choixMode();
    }

    Niveau(GamePanel gp){
        this.gp = gp;
        
    }

    //Constructeur qui servait à redémarrer une partie après échec ou victoire, abandonné
    Niveau(GamePanel gp, boolean marathon){ 
        if (gp!=null){
            this.gp = gp;
        }else{
            scanner = new Scanner(System.in);
        }
        this.marathon = marathon;
    }

    public void setTours(int x, int y, int i){
        if(i == 0){
            
           
            if(p.getTab()[y][x].type == 3 && p.getTab()[y][x].caseVide()){
                Archers a = new Archers(this, gp);
                System.out.println(p.argent-a.prix>=0);
                if(p.argent-a.prix>=0){
                    p.setTours(a, x, y);
                    p.argent -= p.getTab()[y][x].t.prix;
                    a.startToursThread();
                }
                
            }
            
        }
        else if(i == 1){
            
            
            if(p.getTab()[y][x].type == 3 && p.getTab()[y][x].caseVide()){
                Mage m = new Mage(this, gp);
                if(p.argent-m.prix>=0){
                    p.setTours(m, x, y);
                    p.argent -= p.getTab()[y][x].t.prix;
                    m.startToursThread();
                }
                
            }
            
        }
        else if(i == 2){
            
            
            if(p.getTab()[y][x].type == 3 && p.getTab()[y][x].caseVide()){
                Bomber b = new Bomber(this, gp);
                if(p.argent-p.getTab()[y][x].t.prix>=0){
                    p.setTours(b, x, y);
                    p.argent -= p.getTab()[y][x].t.prix;
                    b.startToursThread();
                }
                
            }
            
        }
    }
    public void ameliorer(int x , int y){
        if(p.getTab()[y][x].t!=null && p.argent - p.getTab()[y][x].t.prix >=0){
            p.getTab()[y][x].t.augmenterNv();
            p.argent = p.argent - p.getTab()[y][x].t.prix;
        }
        
    }
    public void supprimerTour(int x, int y){
        if(p.getTab()[y][x].type == 3 && p.getTab()[y][x].t!=null){
            p.supprimerTours(x, y);
        }
    }
    public void deplacerTour(int x, int y, int x2, int y2){
        if (p.getTab()[y][x].type == 3 && p.getTab()[y][x].t!=null) {
            p.deplacerTour(p.getTab()[y][x].t, x2, y2);
        }
    }

    public void draw(Graphics2D g2){
        if(this.gp!=null){
            p.draw(g2);

            g2.setColor(c);
            g2.fillRect( xSelectTours, ySelectTours , gp.screenWidth,  gp.screenHeight);
        }
        else{
            System.out.println("gp null");
        }
        
    }

    public static int randomInt(int min, int max){
        Random random = new Random();
        int nb;
        if (min<0) min =0;
        nb = min+random.nextInt(max - min +1);
        return nb;
    }

    //Sert à remplir le tableau wave grâce à un fichier texte
    public void loadWave(int i){
        InputStream [] listeWave = new InputStream[30];
        try {
            listeWave[0] = getClass().getResourceAsStream("Niveaux/Niveau_01/wave.txt");
            listeWave[1] = getClass().getResourceAsStream("Niveaux/Niveau_02/wave.txt");
            listeWave[2] = getClass().getResourceAsStream("Niveaux/Niveau_03/wave.txt");

            BufferedReader br = new BufferedReader(new InputStreamReader(listeWave[i]));
            
            //lit premiere ligne == niveau des monstres
            String line = br.readLine();
            String n []= line.split(" ");
            int [] nvMonstres = new int[n.length];

            for(int k = 0; k<n.length;k++){
                int num = Integer.parseInt(n[k]);
                nvMonstres[k] = num;
            }

            //deuxieme ligne == type de monstre
            line = br.readLine();
            n = line.split(" ");
            wave = new Personnages[n.length];

            for(int j = 0 ; j<n.length;j++){
                String m = n[j];
                /* if(m.equals("M")){
                    if(gp!=null){
                        wave[j] = new Moblin(nvMonstres[j],gp);
                    }
                    else{
                        wave[j] = new Moblin(nvMonstres[j]);
                    }
                    
                } */
                switch (m){
                    case "M" : wave[j] = (gp != null) ? new Moblin(nvMonstres[j], gp) : new Moblin(nvMonstres[j]); break;
                    case "B" : wave[j] = (gp != null) ? new Beetle(nvMonstres[j], gp) : new Beetle(nvMonstres[j]); break;
                    case "O" : wave[j] = (gp != null) ? new Bobomb(nvMonstres[j], gp) : new Bobomb(nvMonstres[j]); break;
                    case "C" : wave[j] = (gp != null) ? new Chuchu(nvMonstres[j], gp) : new Chuchu(nvMonstres[j]); break;
                    case "W" : wave[j] = (gp != null) ? new Crow(nvMonstres[j], gp) : new Crow(nvMonstres[j]); break;
                    case "D" : wave[j] = (gp != null) ? new Darknut(nvMonstres[j], gp) : new Darknut(nvMonstres[j]); break;
                    case "K" : wave[j] = (gp != null) ? new Keaton(nvMonstres[j], gp) : new Keaton(nvMonstres[j]); break;
                    case "E" : wave[j] = (gp != null) ? new Keese(nvMonstres[j], gp) : new Keese(nvMonstres[j]); break;
                    case "R" : wave[j] = (gp != null) ? new Rope(nvMonstres[j], gp) : new Rope(nvMonstres[j]); break;
                    case "S" : wave[j] = (gp != null) ? new Stalfos(nvMonstres[j], gp) : new Stalfos(nvMonstres[j]); break;
                }
                setNiveau(wave[j]);
            }

            //troisieme ligne == timing dans la wave
            line = br.readLine();
            n = line.split(" ");
            timing = new int[n.length];
            
            for(int x = 0; x<n.length ; x++){
                int num = Integer.parseInt(n[x]);
                timing[x] = num;
            }
            

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        


    }

    //Permet de choisir entre la campagne ou le marathon
    public void choixMode(){
        System.out.println("Choisissez votre mode de jeu : niveau (n) ou marathon (m).");
        boolean flag = false;
        while (!flag){
            if(scanner.hasNextLine()){
                String choix = scanner.nextLine();
                if (choix.equals("n")){
                    flag = true;
                    this.choixNiveau();
                    break;
                }else if(choix.equals("m")){
                    flag = true;
                    marathon = true;
                    this.choixNiveau();
                    break;
                }else{
                    System.out.println("Veuillez choisir une option entre 'n' et 'm'");
                }
            }
        }
    }
    public void choixNiveauGraphique(int i){
        p = new Plateau(gp,i);
        posChemin = p.posChemin;
        if (!marathon){
            loadWave(i);
        }

        xSelectTours = gp.tileSize*p.largeur;
        ySelectTours = 0;
    }
    public void choixNiveau(){
        boolean flag = false;
        while(!flag){
            System.out.println("Choisissez le niveau auquel vous aimeriez jouer");
            System.out.println("Liste des niveaux disponibles : ");
            for(int i = 0; i<nbNiveaux;i++){

                System.out.println("Niveau "+i);

            }
            if(scanner.hasNextInt()){
                int reponse = scanner.nextInt();
                if (reponse >=0 && reponse <nbNiveaux){
                    flag = true;
                    this.numNiveau = reponse;
                    p = new Plateau(reponse);
                    posChemin = p.posChemin;
                    if (!marathon){
                        loadWave(reponse);
                    }
                }
            }else{
                scanner.next();
            }
        }

    }

    public void setNiveau(Personnages p){
        p.niveau=this;
        p.posChemin=this.posChemin;
    }

    public void setNiveau(Tours t){
        t.niveau=this;
    }

    //Toutes les fonctions en rapport avec les casernes/soldats ne sont pas retenues dans le projet final
    public void stopCaserne(){
        for(int i = 0; i<listeCasernes.size();i++){
            
            listeCasernes.get(i).stopToursThread();
            try {
                caserneSemaphore.acquire(); // Acquire the permit, blocking if necessary
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            
        }
    }

    public void startCaserne(){
        for(int i = 0; i<listeCasernes.size();i++){
            if(listeCasernes.get(i)!=null){
                listeCasernes.get(i).startToursThread();
                caserneSemaphore.release();
            }
        }
    }

    public void tueSoldat(){
        for(int i = 0; i<listeCasernes.size();i++){

            listeCasernes.get(i).tueSoldat();
            System.out.println("soldat tué");

        }
    }

    //Fonction servant à recommencer une partie
    public void recommence(GamePanel gp, boolean marathon){
        this.interrupt();
        //Niveau n = new Niveau(gp,marathon);
        p = new Plateau(numNiveau);
        posChemin = p.posChemin;
        if (!marathon){
            loadWave(numNiveau);
        }
        //n.run();
        isRunning = true;
        enPause = false;
        wavePause = false;
        marathonRunning = true;
        this.start();
    }

    //fonction gameOver qui test si la base n'a plus d'hp
    public boolean gameOver(){
        if (this.p.base<=0){
            for (int i = 0; i<wave.length; i++){
                if (wave[i]!=null){
                    wave[i].interrupt(); // Interruption des threads des ennemis
                    wave[i].enVie = false;
                }
            }
            if (!affichageFin){
                affichageFin = true;
                System.out.println("GAME OVER");
            }
            stopGame();
            return true;
        }
        return false;
    }

    //Test si la wave est terminé et que notre base à encore des pv
    public boolean gameWin(){
        if (this.p.base>0){
            for (int i = 0; i<wave.length;i++){
                if (wave[i]!=null && wave[i].enVie){
                    return false;
                }
            }
            System.out.println("GAME WIN !");
            stopGame();
            return true;
        }else{
            return !gameOver();
        }
    }

    //Arrete tous les threads
    public void stopGame() {
        isRunning = false;
        marathonRunning = false;
        //threadNiveau.interrupt();
        for (Personnages ennemi : wave) {
            if (ennemi != null){
                ennemi.interrupt();
                ennemi.enVie = false;
            }
        }
    }

    public void startThreadNiveau(){
        threadNiveau = new Thread(this);
        threadNiveau.start();
    }

    //Fonction qui gère tout les threads
    public void run(){

        //Thread3 sert à afficher dynamiquement le plateau
        Thread thread3 = new Thread(() -> {
            while (isRunning) {
                if (!enPause){
                System.out.println("argent : "+this.p.argent);
                this.p.afficher();
                System.out.println();
                if (marathon){
                    System.out.println("Score : "+score);
                }
                    try {
                        Thread.sleep((long) 1000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
                if (wave.length>0 && wave[0]!=null){
                    if (!marathon){
                        if (this.gameOver() || this.gameWin()) {
                            isRunning = false;
                            Personnages.fonctionne = false;
                            
                            break;
                        }
                    }else{
                        if (this.gameOver()) {
                            isRunning = false;
                            Personnages.fonctionne = false;
                            
                            break;
                        }
                    }
                }
            }
        });
         
        /* Le Thread4 sert à lancer la/les waves sur le plateau en fonction de leur timing et permet (pas encore) à l'utilisateur 
         * de modifier ses tours lorsqu'un ennemis a un timing de 0 (cad une pause dans la wave)
         * quand c'est le cas, le thread 3 est mis en pause
         * 
         */
    
        if(gp!=null){
            next = false;
            
        }
        else{
            nextEtAjoutTourelle();
        }
        
    
        Thread thread4 = new Thread(() -> {
    
            if (!wavePause){
                for (int i = 0; i < this.wave.length; i++) {
                    if (this.gameOver() || this.gameWin()){
                        stopGame();
                        break;
                    }
                    System.out.println("ID :" +this.wave[i].id);
                    System.out.println("posWave "+posWave);
                    if (!isTimingNull(i)){
                        this.wave[i].plateau=this.p;
                        this.p.setPersonnage(this.wave[i], p.depart[0], p.depart[1]);
                        
                        this.wave[i].start();
                        try {
                            Thread.sleep((long) this.timing[i] * 1000);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }else{
                        caseNull(i);
                    }
                }
            }
        });


        //Thread5 remplace le thread4 pendant le mode marathon
        Thread thread5 = new Thread(() -> {
            int numWave = 0;
            while(marathonRunning){
                System.out.println("Début de la wave " + numWave);
                wave = new Personnages[20];
                int n = randomInt(3,5+numWave);
                if (n>=wave.length){
                    n=wave.length;
                }
                for (int i=0;i<n;i++){
                    int choixPerso = randomInt(0,9);
                    int nvMax = numWave+1; if(nvMax>5){nvMax =5;}
                    int nvPerso = randomInt(1,nvMax);
                    int time = randomInt(2,4);
                    this.timing = new int[wave.length];
                    this.timing[i]=time;

                    //wave[i] = new Moblin(nvPerso);
                    

                    switch (choixPerso){
                        case 0: wave[i] = new Moblin(nvPerso); break;
                        case 1: wave[i] = new Beetle(nvPerso); break;
                        case 2: wave[i] = new Bobomb(nvPerso); break;
                        case 3: wave[i] = new Chuchu(nvPerso); break;
                        case 4: wave[i] = new Crow(nvPerso); break;
                        case 5: wave[i] = new Darknut(nvPerso); break;
                        case 6: wave[i] = new Keaton(nvPerso); break;
                        case 7: wave[i] = new Keese(nvPerso); break;
                        case 8: wave[i] = new Rope(nvPerso); break;
                        case 9: wave[i] = new Stalfos(nvPerso); break;
                    }

                    this.wave[i].plateau=this.p;
                    setNiveau(wave[i]);
                    this.p.setPersonnage(this.wave[i], p.depart[0], p.depart[1]);
                //}
                    if (!wavePause){
                        //for (int i = 0; i < wave.length; i++) {
                            if (this.gameOver()){
                                stopGame();
                                marathonRunning = false;
                                break;
                            }
                            /* System.out.println("ID :" +this.wave[i].id);
                            System.out.println("posWave "+posWave); */
                                
                                
                                this.wave[i].start();
                                try {
                                    Thread.sleep((long) this.timing[i] * 1000);
                                } catch (InterruptedException e) {
                                    Thread.currentThread().interrupt();
                                }
                        //}
                    }
                }
                    caseNull(n);
                    if (isRunning){
                        for (int i = 0;i<wave.length;i++){
                            if (wave[i]!=null){
                                wave[i]=null;
                            }
                            timing[i]=0;
                        }
                         
                        numWave++;
                    }
                }
            
        });
        
        
            //thread5.start();
            
            if (!marathon){
                thread3.start();
                thread4.start();
            }else{
                thread3.start();
                thread5.start();
            }
            
        
    }

    //Test si la wave sur le terrain est complètement morte
    public boolean waveMorte(int i){
        for(int n = 0; n < i ; n++){
            //System.out.println("Perso "+ n + " en vie? " + wave[n].enVie);
            if (wave[n].enVie){
                return false;
            }
        }
        return true;
    }

    public boolean isTimingNull(int i){
        if (this.timing!=null && this.timing.length!=0 && this.timing[i]==0){
            return true;
        }
        return false;
    }

    //Permet de placer des tours pendant les pauses entre les waves
    public void caseNull (int i){
        //System.out.println("timing wave 0");
        wavePause = true;
        while (!waveMorte(i)){
            //System.out.println("waveMorte false");
            try {
                sleep(1000);
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
        //System.out.println("waveMorte true");
        this.p.argent += (this.p.argent/10); // Bonus de 10% si on économise son argent;
        enPause = true;
        if (marathon){
            if (!this.gameOver()){
                if(gp!=null){
                    
                }
            else{
                nextEtAjoutTourelle();
            }
            }
        }else{
            if (!this.gameOver() && !this.gameWin()){
                if(gp!=null){
                    next = false;
                    
                }
            else{
                nextEtAjoutTourelle();
            }
            }
        //nextEtAjoutTourelle();
        }
        

        enPause = false;

        if (!marathon){
            this.wave[i].plateau=this.p;
            this.p.setPersonnage(this.wave[i], p.depart[0], p.depart[1]);
            this.wave[i].start();
            
            try {
                    Thread.sleep((long) 2000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
        }
        wavePause = false;
    }

    public void nextEtAjoutTourelle(){
        
        stopCaserne();
        //tueSoldat();
        boolean bool = true;

        p.afficher();
            System.out.println("Nombre de pv restants " + p.base);
            System.out.println("Solde "+ p.argent);
            System.out.println("Veuillez entrer 'next' pour continuer");
            System.out.println("Veuillez entrer 't' pour placer une tours");
            System.out.println("Veuillez entrer 'a' pour ameliorer une tours");
            System.out.println("Veuillez entrer 'd' pour deplacer une tours");
            System.out.println("Veuillez entrer 's' pour supprimer une tours");

        while(bool){
            
            if(scanner.hasNextLine()){
                String reponse =scanner.nextLine();
                if (reponse.equals("next")) {
                    bool = false;
                    startCaserne();
                }
                else if(reponse.equals("t")){
                    placerTours();
                }
                else if(reponse.equals("a")){
                    ameliorerTours();
                }
                else if(reponse.equals("d")){
                    deplacerTour();
                }
                else if(reponse.equals("s")){
                    supprimerTours();
                }
                else{
                    System.out.println("veuillez entrer une des options suivantes : next, t, a, d ou s.");
                }
            }
            else{
                System.out.println("veuillez entrer une chaine de charactere.");
            }
            
        }
        
    }
    public int[] selectTours(){
        
        int []c = {-1,-1};

        System.out.println("x (-1 pour retourner en arrière)");
        while (!scanner.hasNextInt()) {
            System.out.println("Veuillez entrer un entier pour la coordonnée x (-1 pour retourner en arrière)");
           scanner.next(); // Consume the invalid input
        }
        int x =scanner.nextInt();


        System.out.println("y (-1 pour retourner en arrière)");
        while (!scanner.hasNextInt()) {
            System.out.println("Veuillez entrer un entier pour la coordonnée y (-1 pour retourner en arrière)");
           scanner.next(); // Consume the invalid input
        }
        int y =scanner.nextInt();
        c[0] = x;
        c[1] = y;
        
       
        
        return c;
    }
    public void supprimerTours(){
        System.out.println("Quelle tours voulez-vous supprimer?");
        int[] c = selectTours();
        int x = c[0];
        int y = c[1];

        if(x<0 || y<0){
            return;
        }

        if(p.getTab()[y][x].type == 3 && p.getTab()[y][x].t!=null){
            p.supprimerTours(x, y);
        }
        else{
            System.out.println("La case selectionnée ne correspond à un chemin ou ne contient pas de tours.");
        }
    }

    public void deplacerTour(){
        System.out.println("Quelle tours voulez-vous deplacer?");
        int[] c = selectTours();
        int x = c[0];
        int y = c[1];

        if(x<0 || y<0){
            return;
        }

        System.out.println("Où voulez-vous déplacer la tours?");
        c = selectTours();
        int x2 = c[0];
        int y2 = c[1];

        if (p.getTab()[y][x].type == 3 && p.getTab()[y][x].t!=null) {
            p.deplacerTour(p.getTab()[y][x].t, x2, y2);
        }
        else{
            System.out.println("La case selectionnée ne correspond à un chemin ou ne contient pas de tours.");
        }

    }

    public void ameliorerTours(){
        System.out.println("Quelle tours voulez-vous ameliorer?");
        int[] c = selectTours();
        int x = c[0];
        int y = c[1];

        if(x<0 || y<0){
            return;
        }

        if(p.getTab()[y][x].t!=null && p.argent - p.getTab()[y][x].t.prix >=0){
            p.getTab()[y][x].t.augmenterNv();
            p.argent = p.argent - p.getTab()[y][x].t.prix;
        }
        else{
            System.out.println("Vous n'avez pas sélectionné une tour ou vous n'avez pas assez d'argent");
        }
    }


    public void placerTours(){

        boolean init = false;
        while(!init){
        System.out.println("Où ameriez-vous placer la tour?");
        int[] n = selectTours();
        int x = n[0];
        int y = n[1];

        if(x<0 || y<0){
            return;
        }

            if ( p.getTab()[y][x].type == 3 && p.getTab()[y][x].t==null){
                System.out.println("Choisissez la tour que vous aimeriez placer");
                System.out.println("1. Archer");
                //System.out.println("2. Caserne");
                System.out.println("2. Mage");
                System.out.println("3. Bomber");
                System.out.println("4. Retour");
                
                int tours =scanner.nextInt();
    
                switch (tours) {
                case 1:
                    Archers t = new Archers(this);
                    init = this.p.argent - t.prix >=0;
                    if (!init){
                        System.out.println("Vous n'avez pas assez d'argent pour acheter cette tour.");
                        break;
                    }
                    init = p.setTours(t,x,y);
                    if(init){
                        t.startToursThread();
                    }
                    break;
                /* case 2:
                    Caserne c = new Caserne(this);
                    init = this.p.argent - c.prix >=0;
                    if (!init){
                        System.out.println("Vous n'avez pas assez d'argent pour acheter cette tour.");
                        break;
                    }
                    init = p.setTours(c,x,y);
                    if(init){
                        listeCasernes.add(c);
                    }
                    
                    break; */
                case 2:
                    Mage m = new Mage(this);
                    init = this.p.argent - m.prix >=0;
                    if (!init){
                        System.out.println("Vous n'avez pas assez d'argent pour acheter cette tour.");
                        break;
                    }
                    init = p.setTours(m,x,y);
                    if(init){
                        m.startToursThread();
                    }
                    
                    break;
                case 3:
                    Bomber b = new Bomber(this);
                    init = this.p.argent - b.prix >=0;
                    if (!init){
                        System.out.println("Vous n'avez pas assez d'argent pour acheter cette tour.");
                        break;
                    }
                    init = p.setTours(b,x,y);
                    if(init){
                        b.startToursThread();
                    }
                    
                    break;
                case 4:
                    break;
                default:
                    System.out.println("Choisissez un nombre entre 1 et 4");
                    break;
                }                
                
                if (init){
                    p.argent-= p.getTab()[y][x].t.prix;
                }
            }
            else{
                System.out.println("La case selectionnée ne correspond à un chemin ou contient deja une tour.");
            }
        }
       
            
    }


    


}
