package game;

import java.net.URL;
import javax.sound.sampled.*;

public class Sound {
    private Clip[] clips = new Clip[2]; // Store preloaded sound clips

    public Sound() {
        try {
            // Load both sounds once and store them
            clips[0] = loadSound("sound/sound_1.wav");
            clips[1] = loadSound("sound/sound_2.wav");
        } catch (Exception e) {
            System.out.println("Error loading sounds: " + e);
        }
    }

    private Clip loadSound(String path) throws Exception {
        URL soundURL = getClass().getClassLoader().getResource(path);
        if (soundURL == null) {
            throw new Exception("Sound file not found: " + path);
        }
        AudioInputStream ais = AudioSystem.getAudioInputStream(soundURL);
        Clip clip = AudioSystem.getClip();
        clip.open(ais);
        return clip;
    }

    public void play(int i) {
        if (clips[i] != null) {
            clips[i].stop();  // Stop any existing playback to restart from the beginning
            clips[i].setFramePosition(0);
            clips[i].start();
        }
    }

    public void stop(int i) {
        if (clips[i] != null) {
            clips[i].stop();
        }
    }
}
