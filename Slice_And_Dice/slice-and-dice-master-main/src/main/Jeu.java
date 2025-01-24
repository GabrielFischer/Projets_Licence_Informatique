package main;

import De.Capacite.*;
import Entites.*;
import Graphics.scenes.Playing;
import Controller.ui.BrickAmelioration;
import Controller.ui.BrickAttaqueMana;
import java.util.*;

/* Classe gérant les interactions entre les entités ainsi que le déroulement des niveaux
 * La fin du fichier gère les différents sorts, les effets et les évènements aléatoires
 */

public class Jeu {
        private Main main;
        private Joueur joueur;
        private Playing playing;

        private Heros heroSelected =null;
        private Heros heroSelected2 =null;
        private Ennemis ennemisSelected = null;
        private BrickAttaqueMana brickAttaqueMana = null;
        public boolean gameWin=false;
        public boolean gameLoose=false;
        public boolean transition=false;
        public GenerationEnnemis genEnnemis;
        public Stack<Noeud> undo = new Stack<Noeud>();
        public boolean cheat=false;
    
        public Jeu(Playing p){
            playing = p;
            this.main = p.getMain();
            
            this.joueur=p.getJoueur();
            genEnnemis = new GenerationEnnemis(p,p.getDifficulte());
            manche();
        }
    
        private void initBackgroundMusic() {
            main.getAudio().playBackgroundSound();
        }
    
        public void manche(){ //Un manche correspond à une salve d'attaques des héros et des ennemis
            rollDices();
            playing.getBoutique().rerollBoutique();
            undo.clear();
            capEnnemis();
            choisiCible();
            resetCapHeros();
        }

        public void rollDices(){
            playing.getToolbar().rollDices();
        }
    
        public boolean niveauWin(){ //Vérifie si tout les ennemis sont morts
            for (Ennemis ennemi : playing.getListeEnnemis()){
                if (ennemi.enVie()){
                    return false;
                }
            }
            if (genEnnemis.niveau==12){ //Si on gagne le dernier niveau
                gameWin=true;

            }
            return true;
        }
    
        public boolean niveauLoose(){ //Vérifie si tout les héros sont morts
            for (Heros hero : playing.getListeHeros()){
                if (hero.enVie()){
                    return false;
                }
            }
            gameLoose=true;
    
            return true;
        }
    
        public boolean toutesCapUsed(){ //regarde si toutes les capacités des héros ont été utilisées
            for (Heros hero : playing.getListeHeros()){
                if(hero.enVie()){
                    if(!hero.getDe().used){
                        if(!(hero.getDe().getFaceDessus().getCapacite() == null)){
                            return false;
                        }
                        
                    }
                }
            }
            return true;
        }
    
        //Fonction qui gère la suite des clics reçus pour savoir quel action lancer
        //Exemple : un clic sur une attaque de dégat puis sur un ennemi doit déclancher l'attaque sur l'ennemi
        //En revanche un clic sur une attaque de dégat puis sur un allier ne doit rien déclancher
        public boolean attaque(){
            if (playing.getToolbar().rollDone()){ //Vérifie que les dés ont tous été sélectionnés

                if(brickAttaqueMana!=null){ //BrickAttaqueMana correspond à l'attaque de mana sélectionnée
                    if(brickAttaqueMana.getMana().cible==0){ //0 correspond à une attaque qui a besoin d'être juste sélectionné pour être lancée
                        if(attaqueSpe()){
                            mortEnnemis();
                            brickAttaqueMana.setY(brickAttaqueMana.getY()+20); //Fait descendre la brickMana
                            allNull();
                            return true;
                        }
                    }else if(brickAttaqueMana.getMana().cible==-1){ //-1 correpond à une attaque vers un ennemi
                        if(ennemisSelected!=null){
                            if(attaqueSpe()){
                                mortEnnemis();
                                brickAttaqueMana.setY(brickAttaqueMana.getY()+20);
                                allNull();
                                return true;
                            } 
                        }
                    }else if (brickAttaqueMana.getMana().cible==1){ //1 correspond à une attaque vers un allier
                        if(heroSelected!=null){
                            if(attaqueSpe()){
                                brickAttaqueMana.setY(brickAttaqueMana.getY()+20);
                                allNull();
                                return true;
                            }
                        }
                    }
                    return false;
                }

                if (heroSelected!=null){
                    if (attaqueMana()){
                        allNull();
                        return true;
                    }
                    if (heroSelected2!=null){
                        if(attaqueSoinOuBouclier()){
                            allNull();
                            return true;
                        }
                    }else if (ennemisSelected!=null){
                        if (attaqueDegats()){
                            mortEnnemis();
                            allNull();
                            return true;
                        }
                    }
                }
            } 
            heroSelected2=null;
            ennemisSelected=null;
            brickAttaqueMana=null;
            return false;
        }
    
        public void allNull(){ //reset tout les héros, ennemis et brickMana sélectionnés
            heroSelected=null;
            heroSelected2=null;
            ennemisSelected=null;
            brickAttaqueMana=null;
        }

        public void mortEnnemis(){ //Si une attaque tue un ennemi, reset les pdv menacés des héros
            if (!ennemisSelected.enVie()){
                joueur.setMonnaiePartie(joueur.getMonnaiePartie() + ennemisSelected.monnaieLachee());
                if (ennemisSelected.getDe().getFaceDessus().attaque!=null){
                    Heros h=ennemisSelected.getHeroCible();
                    h.setPdvMenaces(h.getPdvMenaces()-ennemisSelected.getDe().getFaceDessus().attaque.getNiveau());
                }
            }
        }
    
        public void verifFinManche(){ //Vérifie si on a gagné ou perdu la manche (ou aucun des deux)
            if(niveauLoose()){
                return;
            }
            if(niveauWin()){
                joueur.achievement.setManchesFinies(joueur.achievement.getManchesFinies()+1); //Augmente pour les achievement
                niveauSuivant();
                return;
            }
            if(!toutesCapUsed()){
                return;
            }else{
                for (Ennemis ennemi : playing.getListeEnnemis()){ //Fait attaquer tout les ennemis
                    if(ennemi.getFaceSelectionnee() == null){
                        
                    }
                    if(ennemi.enVie() && ennemi.getFaceSelectionnee().attaque!=null && ennemi.getHeroCible()!=null){
                        Heros h = ennemi.getHeroCible();
                        ennemi.getFaceSelectionnee().attaque.action(h);
                    }
                }
                applicationDuPoison(this.playing.getListeEnnemis()); //Applique les tick de poison
                for (Heros hero : playing.getListeHeros()){
                    hero.setPdvMenaces(0); //Reset les pdv menaces
                }
                if(niveauLoose()){ //Vérification nécéssaire avant et après l'attaque des ennemis
                    return;
                }
                if(niveauWin()){
                    niveauSuivant();
                    return;
                }
                manche(); //relance une manche si toutesCapUsed et ni niveauLoose et ni niveauWin
            }   
        }
    
        public void niveauSuivant(){ //Passe au niveau suivant et reset les héros
            for(Heros h : playing.getListeHeros()){
                h.setPdv(h.getPdvMax());
                h.setBouclier(0);
                h.getDe().selected=false;
                h.getDe().used=false;
            }
            for (Ennemis e : playing.getListeEnnemis()){
                e.setPdv(0);     
            }
            changementDeNiveau();
            genEnnemis.genererEnnemis();
            playing.initBrickEnnemis();
            
            eventAleatoire(playing.getListeHeros());
            manche();
        }

        public boolean changementDeNiveau(){  //change le niveau dès que tous les ennemis sont morts
            if (cheat){
                genEnnemis.niveau=12;
                transition=true;
                return true;
            }
            if (niveauWin()){
                if(genEnnemis.niveau==12){ //Dernier niveau
                    switch(playing.getDifficulte()){
                        case FACILE : joueur.achievement.setFacile(true); break;
                        case MOYEN : joueur.achievement.setMoyen(true); break;
                        case INFERNO : joueur.achievement.setDifficile(true); break;
                        case DIFFICILE : joueur.achievement.setInferno(true); break;
                    }
                    return false;
                }
                genEnnemis.niveau++;
                transition=true;
                return true;
                
            }
            return false;
        }
    
        public void resetCapHeros(){
            for (Heros hero : playing.getListeHeros()){
                hero.setFaceSelectionnee(null);
                hero.getDe().used=false;
                playing.getToolbar().resetLancesDes();
            }
        }
    
    
    public boolean attaqueSoinOuBouclier(){ //Attaque qui vise un héro (1)
        if (playing.getToolbar().rollDone()){
            if (heroSelected!=null && heroSelected.enVie()){
                if (heroSelected2!=null && heroSelected2.enVie()){
                    if (heroSelected.getFaceSelectionnee()!=null && heroSelected.getFaceSelectionnee().attaque!=null){
                        //Attaques de soins ou de shield
                        if (heroSelected.getFaceSelectionnee().attaque.getType()==2 ||heroSelected.getFaceSelectionnee().attaque.getType()==3 ||heroSelected.getFaceSelectionnee().attaque.getType()==23 || heroSelected.getFaceSelectionnee().attaque.getType()==12 ||heroSelected.getFaceSelectionnee().attaque.getType()==13 ||heroSelected.getFaceSelectionnee().attaque.getType()==24 ||heroSelected.getFaceSelectionnee().attaque.getType()==34){
                            boolean pdvmax = false;
                            if(heroSelected2.getPdv() == heroSelected2.getPdvMax()){
                                pdvmax = true;
                            }
                            heroSelected.getFaceSelectionnee().attaque.action(heroSelected2);
                            heroSelected.getDe().used=true;
                            Noeud n = new Noeud(this,heroSelected.getFaceSelectionnee().attaque,heroSelected,heroSelected2,pdvmax);
                            if(undo.add(n)){
                                return true;
                            }
                            
                            
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    //Attaque pour récolter de la mana, != de attaque spé
    public boolean attaqueMana(){
        if (playing.getToolbar().rollDone()){
            if (heroSelected!=null && heroSelected.enVie()){
                    if (heroSelected.getFaceSelectionnee()!=null && heroSelected.getFaceSelectionnee().attaque!=null){
                        //Attaques de mana
                        if (heroSelected.getFaceSelectionnee().attaque.getType()==4 ||heroSelected.getFaceSelectionnee().attaque.getType()==14 ||heroSelected.getFaceSelectionnee().attaque.getType()==24 ||heroSelected.getFaceSelectionnee().attaque.getType()==34){
                            heroSelected.getFaceSelectionnee().attaque.action(joueur);
                            Noeud n = new Noeud(this,heroSelected.getFaceSelectionnee().attaque,heroSelected);
                            undo.add(n);
                            heroSelected.getDe().used=true;
                            return true;
                        }
                    }
            }
        }
        return false;
    }

    public boolean attaqueDegats(){ //Attaque vers des ennemis (-1)
        if (playing.getToolbar().rollDone()){
            if (heroSelected!=null && heroSelected.enVie()){
                if (ennemisSelected!=null && ennemisSelected.enVie()){
                    if (heroSelected.getFaceSelectionnee()!=null && heroSelected.getFaceSelectionnee().attaque!=null){
                        //Attaques de dégats
                        if (heroSelected.getFaceSelectionnee().attaque.getType()==1 ||heroSelected.getFaceSelectionnee().attaque.getType()==12 ||heroSelected.getFaceSelectionnee().attaque.getType()==13 || heroSelected.getFaceSelectionnee().attaque.getType()==14){
                            heroSelected.getFaceSelectionnee().attaque.action(ennemisSelected);
                            Noeud n = new Noeud(this,heroSelected.getFaceSelectionnee().attaque,heroSelected,ennemisSelected);
                            undo.add(n);
                            if (!ennemisSelected.enVie()){
                                joueur.achievement.setMonstresTués(joueur.achievement.getMonstresTués()+1);
                            }
                            heroSelected.getDe().used=true;
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public boolean attaqueSpe(){ //Attaque des brickMana (en bas de l'écran)
        if(playing.getToolbar().rollDone()){
            if(brickAttaqueMana!=null){
                if(choixSort()){
                    return true;
                }
            }
        }
        return false;
    }

    public void capEnnemis(){ //Selectionne la capacité des nnemis
        for(Ennemis ennemi : playing.getListeEnnemis()){
            if(ennemi.enVie()){
                ennemi.getDe().lancerDes();
                ennemi.setFaceSelectionnee(ennemi.getDe().getFaceDessus());
            }
        }
    }


    public void choisiCible(){ //Fait choisir un héro à attaquer pour chaque ennemi
        Random rand = new Random();
        for (Ennemis ennemi : playing.getListeEnnemis()){
            if (ennemi.enVie()){ //Parcours les ennemis vivants
                boolean flag=false;
                while(!flag){
                    int heroChoisi = rand.nextInt(5);
                    if (playing.getListeHeros()[heroChoisi].enVie()){
                        ennemi.setHeroCible(playing.getListeHeros()[heroChoisi]);  //changement, c'est pas heroSelected, qui est null, mais heroChoisi
                        Heros h = playing.getListeHeros()[heroChoisi];
                        if (ennemi.getDe().getFaceDessus().attaque!=null){
                            h.setPdvMenaces(h.getPdvMenaces()+ennemi.getDe().getFaceDessus().attaque.getNiveau());
                        }
                        flag=true;
                    }
                }
            }else{
                ennemi.setHeroCible(null); //Enlève la ligne si l'ennemi meurt
            }
        }
    }

    /*FONCTIONS QUI FONT APPEL AUX FONCTIONS D'AMéLIORATIONS DES HéROS*/
    public boolean verifAmeliorationLigneeTank(BrickAmelioration briqueChoisie, int i)
    {
        if(briqueChoisie.getHero().getNomEntite().equals("Tank") && i==0)
        {
            for(int k=0; k<this.playing.getBrickHeroes().length; k++)
            {
                if(this.playing.getBrickHeroes()[k].getHero().getNomEntite().equals("Tank"))
                {
                    this.playing.getBrickHeroes()[k].getHero().ameliorationAuNiveau2("Colosse");
                    return true;//amélioration en brute réussie
                }
            }
        }
        else if(briqueChoisie.getHero().getNomEntite().equals("Tank") && i==1)
        {
            for(int k=0; k<this.playing.getBrickHeroes().length; k++)
            {
                if(this.playing.getBrickHeroes()[k].getHero().getNomEntite().equals("Tank"))
                {
                    this.playing.getBrickHeroes()[k].getHero().ameliorationAuNiveau2("Brute");
                    return true;//amélioration en colosse réussie
                }
            }
        }
        else if(briqueChoisie.getHero().getNomEntite().equals("Brute"))
        {
            for(int k=0; k<this.playing.getBrickHeroes().length; k++)
            {
                if(this.playing.getBrickHeroes()[k].getHero().getNomEntite().equals("Brute"))
                {
                    this.playing.getBrickHeroes()[k].getHero().ameliorationAuNiveau3();
                    return true;//amélioration en Golem réussie
                }
            }
        }
        else if(briqueChoisie.getHero().getNomEntite().equals("Colosse"))
        {
            for(int k=0; k<this.playing.getBrickHeroes().length; k++)
            {
                if(this.playing.getBrickHeroes()[k].getHero().getNomEntite().equals("Colosse"))
                {
                    this.playing.getBrickHeroes()[k].getHero().ameliorationAuNiveau3();
                    return true;//amélioration en titan réussie
                }
            }
        }
        return false;
    }
    public boolean verifAmeliorationLigneeEpeiste( BrickAmelioration briqueChoisie, int i)
    {
        if(briqueChoisie.getHero().getNomEntite().equals("Epeiste") && i==0)
        {
            for(int k=0; k<this.playing.getBrickHeroes().length; k++)
            {
                if(this.playing.getBrickHeroes()[k].getHero().getNomEntite().equals("Epeiste"))
                {
                    this.playing.getBrickHeroes()[k].getHero().ameliorationAuNiveau2("Barbare");
                    return true;//amélioration en barbare réussie
                }
            }
        }
        else if(briqueChoisie.getHero().getNomEntite().equals("Epeiste") && i==1)
        {
            for(int k=0; k<this.playing.getBrickHeroes().length; k++)
            {
                if(this.playing.getBrickHeroes()[k].getHero().getNomEntite().equals("Epeiste"))
                {
                    this.playing.getBrickHeroes()[k].getHero().ameliorationAuNiveau2("Chevalier");
                    return true;//amélioration en chevalier réussie
                }
            }
        }
        else if(briqueChoisie.getHero().getNomEntite().equals("Barbare"))
        {
            for(int k=0; k<this.playing.getBrickHeroes().length; k++)
            {
                if(this.playing.getBrickHeroes()[k].getHero().getNomEntite().equals("Barbare"))
                {
                    this.playing.getBrickHeroes()[k].getHero().ameliorationAuNiveau3();
                    return true;//amélioration en mercenaire réussie
                }
            }
        }
        else if(briqueChoisie.getHero().getNomEntite().equals("Chevalier"))
        {
            for(int k=0; k<this.playing.getBrickHeroes().length; k++)
            {
                if(this.playing.getBrickHeroes()[k].getHero().getNomEntite().equals("Chevalier"))
                {
                    this.playing.getBrickHeroes()[k].getHero().ameliorationAuNiveau3();
                    return true;//amélioration en warmaster réussie
                }
            }
        }
        return false;
    }

    public boolean verifAmeliorationLigneeArcher(BrickAmelioration briqueChoisie, int i)
    {
        if(briqueChoisie.getHero().getNomEntite().equals("Archer") && i==0)
        {
            for(int k=0; k<this.playing.getBrickHeroes().length; k++)
            {
                if(this.playing.getBrickHeroes()[k].getHero().getNomEntite().equals("Archer"))
                {
                    this.playing.getBrickHeroes()[k].getHero().ameliorationAuNiveau2("Ranger");
                    return true;//amélioration en ranger réussie
                }
            }
        }
        else if(briqueChoisie.getHero().getNomEntite().equals("Archer") && i==1)
        {
            for(int k=0; k<this.playing.getBrickHeroes().length; k++)
            {
                if(this.playing.getBrickHeroes()[k].getHero().getNomEntite().equals("Archer"))
                {
                    this.playing.getBrickHeroes()[k].getHero().ameliorationAuNiveau2("Chasseur");
                    return true;//amélioration en chasseur réussie
                }
            }
        }
        else if(briqueChoisie.getHero().getNomEntite().equals("Ranger"))
        {
            for(int k=0; k<this.playing.getBrickHeroes().length; k++)
            {
                if(this.playing.getBrickHeroes()[k].getHero().getNomEntite().equals("Ranger"))
                {
                    this.playing.getBrickHeroes()[k].getHero().ameliorationAuNiveau3();
                    return true;//amélioration en elfe réussie
                }
            }
        }
        else if(briqueChoisie.getHero().getNomEntite().equals("Chasseur"))
        {
            for(int k=0; k<this.playing.getBrickHeroes().length; k++)
            {
                if(this.playing.getBrickHeroes()[k].getHero().getNomEntite().equals("Chasseur"))
                {
                    this.playing.getBrickHeroes()[k].getHero().ameliorationAuNiveau3();
                    return true;//amélioration en beastmaster réussie
                }
            }
        }
        return false;
    }


    public boolean verifAmeliorationLigneeGuerisseur(BrickAmelioration briqueChoisie, int i)
    {
        if(briqueChoisie.getHero().getNomEntite().equals("Guerisseur") && i==0)
        {
            for(int k=0; k<this.playing.getBrickHeroes().length; k++)
            {
                if(this.playing.getBrickHeroes()[k].getHero().getNomEntite().equals("Guerisseur"))
                {
                    this.playing.getBrickHeroes()[k].getHero().ameliorationAuNiveau2("Pretre");
                    remplacementSortLigneeGuerisseur("Pretre");
                    return true;//amélioration en pretre réussie
                }
            }
        }
        else if(briqueChoisie.getHero().getNomEntite().equals("Guerisseur") && i==1)
        {
            for(int k=0; k<this.playing.getBrickHeroes().length; k++)
            {
                if(this.playing.getBrickHeroes()[k].getHero().getNomEntite().equals("Guerisseur"))
                {
                    this.playing.getBrickHeroes()[k].getHero().ameliorationAuNiveau2("Apothicaire");
                    remplacementSortLigneeGuerisseur("Apothicaire");
                    return true;//amélioration en apothicaire réussie
                }
            }
        }
        else if(briqueChoisie.getHero().getNomEntite().equals("Pretre"))
        {
            for(int k=0; k<this.playing.getBrickHeroes().length; k++)
            {
                if(this.playing.getBrickHeroes()[k].getHero().getNomEntite().equals("Pretre"))
                {
                    this.playing.getBrickHeroes()[k].getHero().ameliorationAuNiveau3();
                    remplacementSortLigneeGuerisseur("Starseer");
                    return true;//amélioration en Starseer réussie
                }
            }
        }
        else if(briqueChoisie.getHero().getNomEntite().equals("Apothicaire"))
        {
            for(int k=0; k<this.playing.getBrickHeroes().length; k++)
            {
                if(this.playing.getBrickHeroes()[k].getHero().getNomEntite().equals("Apothicaire"))
                {
                    this.playing.getBrickHeroes()[k].getHero().ameliorationAuNiveau3();
                    remplacementSortLigneeGuerisseur("Miracleur");
                    return true;//amélioration en Miracleur réussie
                }
            }
        }
        return false;
    }

    public boolean verifAmeliorationLigneeMage(BrickAmelioration briqueChoisie, int i)
    {
        if(briqueChoisie.getHero().getNomEntite().equals("Mage") && i==0)
        {
            for(int k=0; k<this.playing.getBrickHeroes().length; k++)
            {
                if(this.playing.getBrickHeroes()[k].getHero().getNomEntite().equals("Mage"))
                {
                    this.playing.getBrickHeroes()[k].getHero().ameliorationAuNiveau2("Sorcier");
                    remplacementSortLigneeMage("Sorcier");
                    return true;//amélioration en sorcier réussie
                }
            }
        }
        else if(briqueChoisie.getHero().getNomEntite().equals("Mage") && i==1)
        {
            for(int k=0; k<this.playing.getBrickHeroes().length; k++)
            {
                if(this.playing.getBrickHeroes()[k].getHero().getNomEntite().equals("Mage"))
                {
                    this.playing.getBrickHeroes()[k].getHero().ameliorationAuNiveau2("Arcaniste");
                    remplacementSortLigneeMage("Arcaniste");
                    return true;//amélioration en arcaniste réussie
                }
            }
        }
        else if(briqueChoisie.getHero().getNomEntite().equals("Sorcier"))
        {
            for(int k=0; k<this.playing.getBrickHeroes().length; k++)
            {
                if(this.playing.getBrickHeroes()[k].getHero().getNomEntite().equals("Sorcier"))
                {
                    this.playing.getBrickHeroes()[k].getHero().ameliorationAuNiveau3();
                    remplacementSortLigneeMage("Chaman");
                    return true;//amélioration en chaman réussie
                }
            }
        }
        else if(briqueChoisie.getHero().getNomEntite().equals("Arcaniste"))
        {
            for(int k=0; k<this.playing.getBrickHeroes().length; k++)
            {
                if(this.playing.getBrickHeroes()[k].getHero().getNomEntite().equals("Arcaniste"))
                {
                    this.playing.getBrickHeroes()[k].getHero().ameliorationAuNiveau3();
                    remplacementSortLigneeMage("Demoniste");
                    return true;//amélioration en démoniste réussie
                }
            }
        }
        return false;
    }
    /*FONCTIONS QUI REMPLACENT LES SORTS EN CONCORDANCE AVEC LES HEROS PRESENTS SUR LE TERRAIN (N'AFFECTE QUE LES
     * AMELIORATIONS DU MAGE ET DU GUERISSEUR)*/
    public void remplacementSortLigneeGuerisseur(String nomAmelioration)
    {
        
        if(nomAmelioration.equals("Pretre"))
        {
            for (int i=0; i<this.playing.getBrickMana().length; i++)
            {
                if(this.playing.getBrickMana()[i].getName().equals("Renew"))
                {
                    this.playing.getBrickMana()[i] = new BrickAttaqueMana(new Balance(), this.playing.getBrickMana()[i].getX(), this.playing.getBrickMana()[i].getY(), 3, this.playing.getBrickMana()[i].getWidth(), this.playing.getBrickMana()[i].getHeight(), new Balance().consommationMana, this.playing.getJoueur(), "Balance");
                    
                }
            }
        }
        else if(nomAmelioration.equals("Apothicaire"))
        {
            for (int i=0; i<this.playing.getBrickMana().length; i++)
            {
                if(this.playing.getBrickMana()[i].getName().equals("Renew"))
                {
                    this.playing.getBrickMana()[i] = new BrickAttaqueMana(new Infuse(), this.playing.getBrickMana()[i].getX(), this.playing.getBrickMana()[i].getY(), 6, this.playing.getBrickMana()[i].getWidth(), this.playing.getBrickMana()[i].getHeight(), new Infuse().consommationMana, this.playing.getJoueur(), "Infuse");
                    
                }
            }
        }
        else if(nomAmelioration.equals("Starseer"))
        {
            for (int i=0; i<this.playing.getBrickMana().length; i++)
            {
                if(this.playing.getBrickMana()[i].getName().equals("Balance"))
                {
                    this.playing.getBrickMana()[i] = new BrickAttaqueMana(new Revive(), this.playing.getBrickMana()[i].getX(), this.playing.getBrickMana()[i].getY(), 8, this.playing.getBrickMana()[i].getWidth(), this.playing.getBrickMana()[i].getHeight(), new Revive().consommationMana, this.playing.getJoueur(), "Revive");
                    
                }
            }
        }
        else if(nomAmelioration.equals("Miracleur"))
        {
            for (int i=0; i<this.playing.getBrickMana().length; i++)
            {
                if(this.playing.getBrickMana()[i].getName().equals("Infuse"))
                {
                    this.playing.getBrickMana()[i] = new BrickAttaqueMana(new Liquor(), this.playing.getBrickMana()[i].getX(), this.playing.getBrickMana()[i].getY(), 7, this.playing.getBrickMana()[i].getWidth(), this.playing.getBrickMana()[i].getHeight(), new Liquor().consommationMana, this.playing.getJoueur(), "Liquor");
                    
                }
            }
        }
        
    }

    public void remplacementSortLigneeMage(String nomAmelioration)
    {
        
        if(nomAmelioration.equals("Arcaniste"))
        {
            for (int i=0; i<this.playing.getBrickMana().length; i++)
            {
                if(this.playing.getBrickMana()[i].getName().equals("Flare"))
                {
                    this.playing.getBrickMana()[i] = new BrickAttaqueMana(new Drop(), this.playing.getBrickMana()[i].getX(), this.playing.getBrickMana()[i].getY(), 4, this.playing.getBrickMana()[i].getWidth(), this.playing.getBrickMana()[i].getHeight(), new Drop().consommationMana, this.playing.getJoueur(), "Drop");
                    
                }
            }
        }
        else if(nomAmelioration.equals("Sorcier"))
        {
            for (int i=0; i<this.playing.getBrickMana().length; i++)
            {
                if(this.playing.getBrickMana()[i].getName().equals("Flare"))
                {
                    this.playing.getBrickMana()[i] = new BrickAttaqueMana(new Scald(), this.playing.getBrickMana()[i].getX(), this.playing.getBrickMana()[i].getY(), 5, this.playing.getBrickMana()[i].getWidth(), this.playing.getBrickMana()[i].getHeight(), new Scald().consommationMana, this.playing.getJoueur(), "Scald");
                    
                }
            }
        }
        else if(nomAmelioration.equals("Demoniste"))
        {
            for (int i=0; i<this.playing.getBrickMana().length; i++)
            {
                if(this.playing.getBrickMana()[i].getName().equals("Drop"))
                {
                    this.playing.getBrickMana()[i] = new BrickAttaqueMana(new Bind(), this.playing.getBrickMana()[i].getX(), this.playing.getBrickMana()[i].getY(), 9, this.playing.getBrickMana()[i].getWidth(), this.playing.getBrickMana()[i].getHeight(), new Bind().consommationMana, this.playing.getJoueur(), "Bind");
                    
                }
            }
        }
        else if(nomAmelioration.equals("Chaman"))
        {
            for (int i=0; i<this.playing.getBrickMana().length; i++)
            {
                if(this.playing.getBrickMana()[i].getName().equals("Scald"))
                {
                    this.playing.getBrickMana()[i] = new BrickAttaqueMana(new Blaze(), this.playing.getBrickMana()[i].getX(), this.playing.getBrickMana()[i].getY(), 10, this.playing.getBrickMana()[i].getWidth(), this.playing.getBrickMana()[i].getHeight(), new Blaze().consommationMana, this.playing.getJoueur(), "Blaze");
                    
                }
            }
        }
        for(BrickAttaqueMana a : this.playing.getBrickMana())
        {
            
        }
    }
    /*---------------------------------------------------------------------------------------------------*/

    /* ****************************Gestion des sorts*******************/

    public boolean choixSort(){
        if(brickAttaqueMana!=null && brickAttaqueMana.getMana()!=null && brickAttaqueMana.getMana().getNom()!=null){
            String name = brickAttaqueMana.getMana().getNom();
            switch (name){
                case "Balance" : sortBalance(playing.getListeEnnemis(), playing.getListeHeros()); return true;
                case "Bind" : sortBind(playing.getListeEnnemis(), playing.getListeHeros()); return true;
                case "Blaze" : if(ennemisSelected!=null){
                    sortBlaze(ennemisSelected);
                    return true;
                } return false;
                case "Burst" : if(ennemisSelected!=null){
                    
                    sortBurst(ennemisSelected);
                    
                    return true;
                } return false;
                case "Drop" : sortDrop(playing.getListeEnnemis()); return true;
                case "Flare" : if(ennemisSelected!=null){
                    sortFlare(ennemisSelected);
                    return true;
                } return false;
                case "Infuse" : sortInfuse(playing.getListeHeros()); return true;
                case "Liquor" : sortLiquor(playing.getListeHeros()); return true;
                case "Renew" : if(heroSelected!=null && heroSelected.getPdv()>0){
                    sortRenew(heroSelected);
                    return true;
                } return false;
                case "Revive" : sortRevive(playing.getListeHeros()); return true;
                case "Scald" : sortScald(playing.getListeEnnemis()); return true;
                default : return false;
            }
        }
        return false;
    }

    /*-------------------------------FONCTIONS DE GESTION DES EVENEMENTS ALEATOIRES------------------------------------------*/
    public void eventAleatoire(Heros [] listeHerosSurLeTerrain)//fonction qui doit être appelée à chaque début de niveau, elle permet au programme de générer un évènement aléatoirement ( 1 chance sur 24 pour pas casser le jeu)
    {
        Random generateur = new Random();
        int nbAlea = generateur.nextInt(25);
        if(nbAlea == 16)
        {
            choixDeLeventAleatoire(listeHerosSurLeTerrain);
        }
    }
    public void choixDeLeventAleatoire(Heros[] listeHerosSurLeTerrain)//fonction qui permet au programme de choisir aléatoirement l'évènement qui va avoir lieu
    {
        Random generateur = new Random();
        int nbAlea = generateur.nextInt(3);
        if(nbAlea==0)
        {
            eventEnleveDesPdv(listeHerosSurLeTerrain);
        }
        else if(nbAlea==1)
        {
            eventBouclierPourTous(listeHerosSurLeTerrain);
        }
        else
        {
            eventAjoutMana();
        }
    }

    public void eventEnleveDesPdv(Heros [] listeHerosSurLeTerrain)//évènement aléatoire qui enlève des pdv à tous les héros présents sur le terrain, s'il s'avère que les dégâts infligés tuent théoriquement le héros, alors ce-dernier ne meurt pas mais est laissé à un point de vie
    {
        
        for(Heros h : listeHerosSurLeTerrain)
        {
            if(h.getPdv()-4 <=0)
            {
                h.setPdv(1);
            }
            else
            {
                h.setPdv(h.getPdv()-4);
            }
        }
    }
    public void eventBouclierPourTous(Heros[] listeHerosSurleTerrain)//évènement aléatoire qui ajoute du bouclier à tous les héros sur le terrain
    {
        
        for(Heros h : listeHerosSurleTerrain)
        {
            h.setBouclier(4);
        }
    }
    public void eventAjoutMana()//évènement aléatoire qui ajoute 3 de mana au joueur, Le joueur sur lequel a lieu cet ajout est ici celui de Combat, mais à voir si il faut agir sur une instance de Joueur qui appartient à PLaying peut-être
    {
        
        this.joueur.setMana(this.joueur.getMana()+3);
    }
    /*------------------------------------------------------------------------------------------------------------------------*/
    public void applicationDuPoison(Ennemis [] ennemisSurLeTerrain)
        /*cette fonction doit être appelée à chaque fin de tour,
    elle sert à appliquer le poison aux ennemis concernés IMPORTANT : vérifier, et changer si nécessaire, le paramètre de la fonction
    d'un tableau vers une liste*/
    {
        for(Ennemis e : ennemisSurLeTerrain)
        {
            if(e.getEstEmpoisonne()>0)
            {
                if(e.getPdv()-2<=0)
                {
                    e.setPdv(0);
                    e.setEstEmpoisonne(0);
                }
                else
                {
                    e.setPdv(e.getPdv()-2);
                    e.setEstEmpoisonne(e.getEstEmpoisonne()-1);
                }
            }
        }
    }

    public void sortBalance(Ennemis[] ennemisAttaquesParLeSort, Heros[] herosSoignesParLeSort)//cette fonction doit être appelée lorsque le joueur clique sur le sort Balance
    {
        Balance tmp = new Balance();
        for(Ennemis e : ennemisAttaquesParLeSort)
        {
            tmp.action(e);
        }
        for(Heros e : herosSoignesParLeSort)
        {
            tmp.action(e);
        }
        this.joueur.setMana(this.joueur.getMana()-tmp.consommationMana);
    }

    public void sortBurst(Ennemis ennemiSelectionne)//cette fonction doit être appelée lorsque le joueur clique sur un ennemi après avoir cliqué sur le sort Burst
    {
        Burst tmp = new Burst();
        
        tmp.action(ennemiSelectionne);
        
        this.joueur.setMana(this.joueur.getMana()-tmp.consommationMana);
    }
    public void sortFlare(Ennemis ennemiSelectionne)
    {
        Flare tmp = new Flare();
        tmp.action(ennemiSelectionne);
        this.joueur.setMana(this.joueur.getMana()-tmp.consommationMana);
    }
    public void sortRenew(Heros herosSelectionne)
    {
        
        Renew tmp = new Renew();
        tmp.action(herosSelectionne);
        this.joueur.setMana(this.joueur.getMana()-tmp.consommationMana);
    }
    public void sortDrop(Ennemis[] listeDesEnnemisSurLeTerrain)//cette fonction prend en paramètres la liste des ennemis sur le terrain pour trouver elle-même l'ennemi qui possède le plus de pdv et infliger le sort à ce-dernier
    {
        Drop tmp = new Drop();
        Ennemis ennemiTmp = listeDesEnnemisSurLeTerrain[0];
        for(Ennemis enn : listeDesEnnemisSurLeTerrain)
        {
            if (enn.getPdv() > ennemiTmp.getPdv())
            {
                ennemiTmp=enn;
            }
        }

        tmp.action(ennemiTmp);
        this.joueur.setMana(this.joueur.getMana()-tmp.consommationMana);
    }
    public void sortScald(Ennemis[] listeEnnemisSurLeTerrain)
    {
        Scald tmp = new Scald();
        for(Ennemis e : listeEnnemisSurLeTerrain)
        {
            if(e.getPdv() < e.getPdvMax())
            {
                tmp.action(e);
            }
        }
        this.joueur.setMana(this.joueur.getMana()-tmp.consommationMana);
    }
    public void sortInfuse(Heros[] listeHerosSurLeTerrain)
    {
        Infuse tmp = new Infuse();
        Heros tmpH = listeHerosSurLeTerrain[0];
        for(Heros h  : listeHerosSurLeTerrain)
        {
            if(h.getPdv() < tmpH.getPdv())
            {
                tmpH=h;
            }
        }
        tmp.action(tmpH);
        this.joueur.setMana(this.joueur.getMana()- tmp.consommationMana);
    }
    public void sortLiquor(Heros[] listeHeros)
    {
        Liquor tmp = new Liquor();
        for(Heros h : listeHeros)
        {
            tmp.action(h);
        }
        this.joueur.setMana(this.joueur.getMana()- tmp.consommationMana);
        this.joueur.setMana(this.joueur.getMana()+1);
    }
    public void sortRevive(Heros[] listeHeros)
    {
        Revive tmp = new Revive();
        for(Heros h : listeHeros)
        {
            if(!h.enVie())
            {
                tmp.action(h);
                break;
            }
        }
        this.joueur.setMana(this.joueur.getMana()- tmp.consommationMana);
    }
    public void sortBind(Ennemis[] listeEnnemisSurLeTerrain,Heros[] listeHerosSurLeTerrain)
    {
        Bind tmp = new Bind();
        for(Ennemis e : listeEnnemisSurLeTerrain)
        {
            tmp.action(e);
        }
        Heros heroTmp = listeHerosSurLeTerrain[0];
        for(Heros h : listeHerosSurLeTerrain)
        {
            if(h.getPdv()>heroTmp.getPdv())
            {
                heroTmp=h;
            }
        }
        heroTmp.setPdv(heroTmp.getPdv()-5);
        this.joueur.setMana(this.joueur.getMana()- tmp.consommationMana);
    }
    public void sortBlaze(Ennemis e)
    {
        Blaze tmp = new Blaze();
        tmp.action(e);
        this.joueur.setMana(this.joueur.getMana()-tmp.consommationMana);
    }
    /* ********************************************************************/

    public Heros getHeroSelected(){
        return heroSelected;
    }
    public Heros getHeroSelected2(){
        return heroSelected2;
    }
    public Ennemis getEnnemiSelected(){
        return ennemisSelected;
    }
    public BrickAttaqueMana getBrickAttaqueMana(){
        return brickAttaqueMana;
    }
    public int getNiveauActuel(){
        return genEnnemis.niveau;
    }


    public void setHeroSelected(Heros h){
        heroSelected=h;
    }
    public void setHeroSelected2(Heros h){
        heroSelected2=h;
    }
    public void setEnnemiSelected(Ennemis e){
        ennemisSelected=e;
    }
    public void setBrickAttaqueMana(BrickAttaqueMana bam){
        
        brickAttaqueMana=bam;
    }

    

    public Playing getPlaying(){
        return playing;
    }

    public void cheat(){
        cheat = true;
        niveauSuivant();
    }


}
