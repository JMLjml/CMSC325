package effects;

import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;

/**
 *
 * @author John M. Lasheski
 */
public class Sounds {

  private static AudioNode audioNode = new AudioNode();
  public static AudioNode audio_gun, audio_explosion, audio_nature;

  public static AudioNode initAudio(AssetManager assetManager) {
    
    /* gun shot sound is to be triggered by a mouse click. */
    audio_gun = new AudioNode(assetManager, "Sounds/message.ogg", false);
    audio_gun.setPositional(false);
    audio_gun.setLooping(false);
    audio_gun.setVolume(2);
    audioNode.attachChild(audio_gun);

    /* explosion sound is to be triggered by a collision. */
    audio_explosion = new AudioNode(assetManager, "Sound/Effects/Bang.wav", false);
    audio_explosion.setPositional(true);
    audio_explosion.setLooping(false);
    audio_explosion.setVolume(1);
    audioNode.attachChild(audio_explosion);

    /* nature sound - keeps playing in a loop. */
    audio_nature = new AudioNode(assetManager, "Sounds/Noise.wav", true);
    audio_nature.setPositional(false);
    audio_nature.setLooping(true);  // activate continuous playing
    audio_nature.setVolume(1);
    audioNode.attachChild(audio_nature);
    audio_nature.play(); // play continuously!

    return audioNode;
  }  
}
