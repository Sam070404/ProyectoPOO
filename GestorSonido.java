import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;

class GestorSonido {
    private Clip clip;

    public GestorSonido(String rutaArchivoAudio) {
        try {
            AudioInputStream flujoAudio = AudioSystem.getAudioInputStream(new File(rutaArchivoAudio));
            clip = AudioSystem.getClip();
            clip.open(flujoAudio);
        } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
            e.printStackTrace();
        }
    }

    public void reproducirEnBucle() {
        if (clip != null) {
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    public void detener() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
        }
    }
}
