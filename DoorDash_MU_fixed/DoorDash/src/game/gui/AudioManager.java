package game.gui;

import javafx.scene.media.AudioClip;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Lightweight audio manager. All play() calls are no-ops when the file is missing
 * or when sound is muted. Audio files (optional) live in /game/gui/audio/{name}.mp3
 *
 * Expected (optional) clips:
 *   dice_roll, energy_gain, energy_loss, monsters_laugher, monster_scarer,
 *   step_move, card_flip, powerup_thunder
 */
public final class AudioManager {

    private static boolean muted = false;
    private static final Map<String, AudioClip> CACHE = new HashMap<>();

    private AudioManager() {}

    public static boolean isMuted()      { return muted; }
    public static void   setMuted(boolean m) { muted = m; }
    public static void   toggleMuted()   { muted = !muted; }

    public static void play(String name) {
        if (muted) return;
        AudioClip clip = load(name);
        if (clip != null) {
            try { clip.play(0.85); } catch (Throwable ignored) {}
        }
    }

    private static AudioClip load(String name) {
        if (CACHE.containsKey(name)) return CACHE.get(name);
        AudioClip clip = null;
        try {
            URL url = AudioManager.class.getResource("/game/gui/audio/" + name + ".mp3");
            if (url != null) clip = new AudioClip(url.toExternalForm());
        } catch (Throwable ignored) {
            // audio module unavailable or file unreadable - silent no-op
        }
        CACHE.put(name, clip);
        return clip;
    }
}
