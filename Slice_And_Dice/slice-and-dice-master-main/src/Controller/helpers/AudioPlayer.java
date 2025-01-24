package Controller.helpers;
import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class AudioPlayer {  /* Classe qui permet de gérer le son, inclu des methodes qui permettent de lancer et arreter le son  */

    //VOLUME CONTROL
    boolean mute = false;
    float previousVolume = 0;
    float currentVolume = 0;
    FloatControl fc;
    private String soundsFolder = "res/sounds/";

    //CLIPS
    private Clip mouseclickSound;
    private Clip cashRegisterSound;
    private Clip diceRollSound;
    private Clip swipeSound;
    private Clip burstSound;
    private Clip flareSound;
    private Clip sparkleSound;
    private Clip backgroundSound;

    public AudioPlayer(){
        try {
            mouseclickSound = AudioSystem.getClip();
            mouseclickSound.open(AudioSystem.getAudioInputStream(new File(soundsFolder + "mouseclick.wav").getAbsoluteFile()));

            cashRegisterSound = AudioSystem.getClip();
            cashRegisterSound.open(AudioSystem.getAudioInputStream(new File(soundsFolder + "cashRegister.wav").getAbsoluteFile()));

            diceRollSound = AudioSystem.getClip();
            diceRollSound.open(AudioSystem.getAudioInputStream(new File(soundsFolder + "diceRoll.wav").getAbsoluteFile()));

            swipeSound = AudioSystem.getClip();
            swipeSound.open(AudioSystem.getAudioInputStream(new File(soundsFolder + "swipewhoosh.wav").getAbsoluteFile()));

            burstSound = AudioSystem.getClip();
            burstSound.open(AudioSystem.getAudioInputStream(new File(soundsFolder + "burst.wav").getAbsoluteFile()));

            flareSound = AudioSystem.getClip();
            flareSound.open(AudioSystem.getAudioInputStream(new File(soundsFolder + "flare.wav").getAbsoluteFile()));

            sparkleSound = AudioSystem.getClip();
            sparkleSound.open(AudioSystem.getAudioInputStream(new File(soundsFolder + "sparkle.wav").getAbsoluteFile()));

            backgroundSound = AudioSystem.getClip();
            backgroundSound.open(AudioSystem.getAudioInputStream(new File(soundsFolder + "backgroundsound.wav").getAbsoluteFile()));
            fc = (FloatControl) backgroundSound.getControl(FloatControl.Type.MASTER_GAIN);

        } catch (LineUnavailableException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedAudioFileException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void playMouseclickSound(){
        mouseclickSound.setFramePosition(0);
        mouseclickSound.start();
    }

    public void playCashRegisterSound(){
        cashRegisterSound.setFramePosition(0);
        cashRegisterSound.start();
    }

    public void playDiceRollSound(){
        diceRollSound.setFramePosition(0);
        diceRollSound.start();
    }

    public void playSwipeSound(){
        swipeSound.setFramePosition(0);
        swipeSound.start();
    }

    public void playBurstSound(){
        burstSound.setFramePosition(0);
        burstSound.start();
    }

    public void playFlareSound(){
        flareSound.setFramePosition(0);
        flareSound.start();
    }

    public void playSparkleSound(){
        sparkleSound.setFramePosition(0);
        sparkleSound.start();
    }

    public void playBackgroundSound(){
        backgroundSound.setFramePosition(0);
        backgroundSound.start();
        backgroundSound.loop(Clip.LOOP_CONTINUOUSLY);
    }

    public void stopBackgroundSound(){
        backgroundSound.stop();
    }

    public void volumeUp(){
        currentVolume += 1.0f;
        if(currentVolume > 6.0f){
            currentVolume = 6.0f;

        }
        fc.setValue(currentVolume);
    }

    public void volumeDown(){
        currentVolume -= 1.0f;
        if(currentVolume < -80.0f){
            currentVolume = -80.0f;
        }
        fc.setValue(currentVolume);
    }

    public void volumeMute(){
        if(!mute){
            previousVolume = currentVolume;
            currentVolume = -80.0f;
            fc.setValue(currentVolume);
            mute = true;
        }
        else {
            currentVolume = previousVolume;
            fc.setValue(currentVolume);
            mute = false;
        }
    }

    public boolean isMute() {
        return mute;
    }

    public float getPreviousVolume() {
        return previousVolume;
    }

    public float getCurrentVolume() {
        return currentVolume;
    }

    public FloatControl getFc() {
        return fc;
    }

    public void setMute(boolean mute) {
        this.mute = mute;
    }

    public void setPreviousVolume(float previousVolume) {
        this.previousVolume = previousVolume;
    }

    public void setCurrentVolume(float currentVolume) {
        this.currentVolume = currentVolume;
    }

    public void setFc(FloatControl fc) {
        this.fc = fc;
    }
}
