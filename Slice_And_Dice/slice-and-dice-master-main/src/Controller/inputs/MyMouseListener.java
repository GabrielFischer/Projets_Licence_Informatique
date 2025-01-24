package Controller.inputs;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import main.EtatsJeu;
import main.Main;

/*BUT
 * Cette classe implémente à la fois les interfaces MouseListener et MouseMotionListener. 
 * Le but de cette classe est de gérer les événements de souris (clics, mouvements, etc.) pour le jeu. 
 * La classe remplace les méthodes des interfaces MouseListener et MouseMotionListener pour gérer différents 
 * événements de souris. Elles vérifient l'état actuel du jeu (EtatsJeu.etatJeu) et délèguent l'événement de 
 * la souris aux composants appropriés.
 */

public class MyMouseListener implements MouseListener, MouseMotionListener{

    private Main main;

    public MyMouseListener(Main main) {
        this.main = main;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
    	switch(EtatsJeu.etatJeu) {
            case MENU:
                main.getMenu().mouseDragged(e.getX(), e.getY());
                break;
            case PLAYING:
                main.getMenu().getPlaying().mouseDragged(e.getX(), e.getY());
                break;
            case SETTINGSMENU:
                main.getMenu().getSettings().mouseDragged(e.getX(), e.getY());
                break;
            case SETTINGSPLAYING:
                main.getMenu().getSettings().mouseDragged(e.getX(), e.getY());
                break;
            case CUSTOM:
                main.getCustom().mouseDragged(e.getX(), e.getY());
                break;
            default:
                break;
    	}
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        switch(EtatsJeu.etatJeu) {
            case MENU:
                main.getMenu().mouseMoved(e.getX(), e.getY());
                break;
            case PLAYING:
                main.getMenu().getPlaying().mouseMoved(e.getX(), e.getY());
                break;
            case SETTINGSMENU:
                main.getMenu().getSettings().mouseMoved(e.getX(), e.getY());
                break;
            case SETTINGSPLAYING:
                main.getMenu().getSettings().mouseMoved(e.getX(), e.getY());
                break;
            case CUSTOM:
                main.getCustom().mouseMoved(e.getX(), e.getY());
                break;
            case BOUTIQUE:
                main.getMenu().getPlaying().getBoutique().mouseMoved(e.getX(), e.getY());
                break;
            case INVENTAIRE:
                main.getMenu().getPlaying().getInventaire().mouseMoved(e.getX(), e.getY());
                break;
            case GUIDEMENU:
                main.getMenu().getGuide().mouseMoved(e.getX(), e.getY());
                break;
            case GUIDEPLAYING:
                main.getMenu().getGuide().mouseMoved(e.getX(), e.getY());
                break;
            case BOUTIQUEMENU:
                main.getMenu().getBoutiqueMenu().mouseMoved(e.getX(), e.getY());
                break;
            default:
                break;
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int x = e.getX();
        int y =e.getY();

        if(e.getButton() == MouseEvent.BUTTON1){
            switch(EtatsJeu.etatJeu) {
                case MENU:
                    main.getMenu().mouseClicked(e.getX(), e.getY());
                    break;
                case PLAYING:
                    main.getMenu().getPlaying().mouseClicked(e.getX(), e.getY());
                    break;
                case SETTINGSMENU:
                    main.getMenu().getSettings().mouseClicked(e.getX(), e.getY());
                    break;
                case SETTINGSPLAYING:
                    main.getMenu().getSettings().mouseClicked(e.getX(), e.getY());
                    break;
                case CUSTOM:
                    main.getCustom().mouseClicked(e.getX(), e.getY());
                    break;
                case BOUTIQUE:
                    main.getMenu().getPlaying().getBoutique().mouseClicked(e.getX(), e.getY());
                    break;
                case INVENTAIRE:
                    main.getMenu().getPlaying().getInventaire().mouseClicked(x, y);
                    break;
                case CINEMATIQUE:
                    main.getCinematique().nextFrame();
                    break;
                case GUIDEPLAYING:
                    main.getMenu().getGuide().mouseClicked(x, y);
                    break;
                case GUIDEMENU:
                    main.getMenu().getGuide().mouseClicked(x, y);
                    break;
                case SAUVEGARDE:
                    main.getMenu().getPlaying().sauvegardeCharge.mouseClicked(x, y);
                    break;
                case ACHIEVEMENT:
                    main.getJoueur().achievement.mouseClicked(x, y);
                    break;
                case BOUTIQUEMENU:
                    main.getMenu().getBoutiqueMenu().mouseClicked(x, y);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // TODO Auto-generated method stub
    }

    @Override
    public void mouseExited(MouseEvent e) {
        // TODO Auto-generated method stub
    }

    @Override
    public void mousePressed(MouseEvent e) {
        switch(EtatsJeu.etatJeu) {
            case MENU:
                main.getMenu().mousePressed(e.getX(), e.getY());
                break;
            case PLAYING:
                main.getMenu().getPlaying().mousePressed(e.getX(), e.getY());
                break;
            case SETTINGSMENU:
                main.getMenu().getSettings().mousePressed(e.getX(), e.getY());
                break;
            case SETTINGSPLAYING:
                main.getMenu().getSettings().mousePressed(e.getX(), e.getY());
                break;
            case CUSTOM:
                main.getCustom().mousePressed(e.getX(), e.getY());
                break;
            case GUIDEMENU:
                main.getMenu().getGuide().mousePressed(e.getX(), e.getY());
                break;
            case GUIDEPLAYING:
                main.getMenu().getGuide().mousePressed(e.getX(), e.getY());
                break;
            default:
                break;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        switch(EtatsJeu.etatJeu) {
            case MENU:
                main.getMenu().mouseReleased(e.getX(), e.getY());
                break;
            case PLAYING:
                main.getMenu().getPlaying().mouseReleased(e.getX(), e.getY());
                break;
            case SETTINGSMENU:
                main.getMenu().getSettings().mouseReleased(e.getX(), e.getY());
                break;
            case SETTINGSPLAYING:
                main.getMenu().getSettings().mouseReleased(e.getX(), e.getY());
                break;
            case CUSTOM:
                main.getCustom().mouseReleased(e.getX(), e.getY());
                break;
            case GUIDEMENU:
                main.getMenu().getGuide().mouseReleased(e.getX(), e.getY());
                break;
            case GUIDEPLAYING:
                main.getMenu().getGuide().mouseReleased(e.getX(), e.getY());
                break;
            default:
                break;
        }
    }
}
