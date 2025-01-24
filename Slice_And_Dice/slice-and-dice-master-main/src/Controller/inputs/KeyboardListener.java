package Controller.inputs;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import main.EtatsJeu;
import main.Main;

public class KeyboardListener implements KeyListener{ //Classe qui gère les commandes de clavier
    
    private Main main;
	
	public KeyboardListener(Main main) {
		this.main = main;
	}
	
    @Override
    public void keyTyped(KeyEvent e){

    }

    @Override
    public void keyPressed(KeyEvent e){
    	int code = e.getKeyCode();
        if(EtatsJeu.etatJeu == EtatsJeu.CINEMATIQUE){ //gère les changements de frames pour la cinématique
            if (code == KeyEvent.VK_ENTER || code == KeyEvent.VK_SPACE){
                main.getCinematique().nextFrame();
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e){

    }
}
