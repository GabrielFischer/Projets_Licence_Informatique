package Jeu;

import java.net.URL;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class Sound{
    Clip clip;
    URL soundURL[] = new URL[30];

    public Sound(){
        soundURL[0] = getClass().getResource("../Musiques/EcranTitre.wav");
        soundURL[1] = getClass().getResource("../Musiques/SelectionFichier.wav");
        soundURL[2] = getClass().getResource("../Musiques/Partie.wav");
    }

    public void setFile(int i){
        try{
            AudioInputStream ais = AudioSystem.getAudioInputStream(soundURL[i]);
            clip = AudioSystem.getClip();
            clip.open(ais);
        }
        catch(Exception e){
            e.printStackTrace(); // Print the exception details
        }
    }

    public void play() {
        if (clip != null) {
            clip.start();
        } else {
            System.out.println("Clip is null. Call setFile() before calling play().");
        }
    }
    public void loop(){
        clip.loop(Clip.LOOP_CONTINUOUSLY);
    }
    public void stop(){
        clip.stop();
    }
}