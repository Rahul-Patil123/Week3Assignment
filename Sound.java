/*
 * Name = Rahul Ganeshwar Patil
 * Date = 17-09-2024
 * Description = [This file has all the functionality of the sound integrated with project.]
 * **/

package Game2048;
import java.io.File;
import java.io.IOException;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Sound {
	static Clip backgroundClip;
    static Clip winClip;
    static Clip loseClip;
    
    //This function is used to check the audio stream.
    public static Clip loadSound(String filePath) {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(filePath));
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            return clip;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    //This function is used to set up background audio continuously 
    public static void playBackgroundSound() {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(Constant.BACKGROUND_AUDIO));
            backgroundClip = AudioSystem.getClip();
            backgroundClip.open(audioInputStream);
            backgroundClip.loop(Clip.LOOP_CONTINUOUSLY); 
            backgroundClip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }
    //This function is used to play any sound from starting
    public static void playSound(Clip clip) {
        if (clip != null) {
            clip.setFramePosition(0);
            clip.start();
        }
    }
    //This function is used to stop background audio when the program is exited
    public void stopBackgroundSound() {
        if (backgroundClip != null && backgroundClip.isRunning()) {
            backgroundClip.stop();
            backgroundClip.close();
        }
    }
}
