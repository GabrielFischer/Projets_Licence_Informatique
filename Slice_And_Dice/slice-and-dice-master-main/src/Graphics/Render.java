package Graphics;

import main.EtatsJeu;
import main.Main;

import java.awt.Graphics;
import java.awt.Graphics2D;

/* BUT
 * La classe agit comme une liaison entre la logique principale du jeu (gérée par différentes classes d'état du jeu) et
 * le processus de rendu. Cela garantit que la méthode de rendu appropriée est appelée en fonction de l'état actuel du jeu.
 */


public class Render{

    public boolean initJeu = false;
    private Main main;
    public Render(Main main){
        this.main = main;
    }

    /*
     * La méthode de rendu prend un objet Graphics comme paramètre, qui est utilisé pour dessiner des graphiques sur
     * l'écran. Il bascule entre les différents états du jeu à l'aide de l'énumération EtatsJeu.etatJeu. En fonction
     * de l'état actuel du jeu, il appelle la méthode de rendu correspondante de la classe associée dans l'instance
     * Main(main).
     */
    public void render(Graphics g){
        Graphics2D g2 = (Graphics2D) g;
        switch(EtatsJeu.etatJeu){
            
            case CINEMATIQUE:
                main.getCinematique().render(g);
                
                break;
            case MENU:
                main.getMenu().render(g);
                break;
            case PLAYING:
                main.getMenu().getPlaying().render(g);
                break;
            case CUSTOM:
                main.getCustom().render(g);
                break;
            case BOUTIQUE:
                main.getMenu().getPlaying().render(g);
                main.getMenu().getPlaying().getBoutique().drawBoutique(g2);
                break;
                
            case INVENTAIRE:
                main.getMenu().getPlaying().render(g);
                main.getMenu().getPlaying().getInventaire().drawInventaire(g2);
                break;
            case GUIDEPLAYING:
                main.getMenu().getPlaying().render(g);
                main.getMenu().getGuide().draw(g2);
                break;
            case GUIDEMENU:
                main.getMenu().render(g);
                main.getMenu().getGuide().draw(g2);
                break;
            case SAUVEGARDE:
                main.getMenu().getPlaying().render(g);
                main.getMenu().getPlaying().sauvegardeCharge.draw(g2);
                break;
            case SETTINGSMENU:
                main.getMenu().render(g);
                main.getMenu().getSettings().draw(g2);
                break;
            case SETTINGSPLAYING:
                main.getMenu().getPlaying().render(g);
                main.getMenu().getSettings().draw(g2);
                break;
            case ACHIEVEMENT:
                main.getMenu().render(g);
                main.getJoueur().achievement.draw(g2);
                break;
            case BOUTIQUEMENU:
                main.getMenu().render(g);
                main.getMenu().getBoutiqueMenu().draw(g2);
            default:
                break;
        }
        if(EtatsJeu.etatJeu == EtatsJeu.CINEMATIQUE && initJeu == false){
            main.getMenu().initPlaying();
            initJeu =true;
        }
    }
}

