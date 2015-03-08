package physics;

import effects.Sounds;
import com.jme3.app.Application;
import com.jme3.asset.TextureKey;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.material.Material;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Sphere;
import com.jme3.scene.shape.Sphere.TextureMode;
import com.jme3.texture.Texture;
import scores.ScoreManager;


/**
 *
 * @author John M. Lasheski
 */
public class BallShooter {
  public static void createBallShooter(final Application app, final Node rootNode,
                                       final PhysicsSpace space, final ScoreManager scoreManager) {
        
    ActionListener actionListener = new ActionListener() {

        public void onAction(String name, boolean keyPressed, float tpf) {
          Sphere bullet = new Sphere(22, 22, .20f, true, false);
          bullet.setTextureMode(TextureMode.Projected);
          Material mat2 = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
          TextureKey key2 = new TextureKey("Blender/2.4x/textures/Grass_256.png");
          key2.setGenerateMips(true);
          Texture tex2 = app.getAssetManager().loadTexture(key2);
          mat2.setTexture("ColorMap", tex2);
          
          if (name.equals("shoot") && !keyPressed) {
            Geometry bulletg = new Geometry("bullet", bullet);
            bulletg.setMaterial(mat2);
            bulletg.setShadowMode(ShadowMode.CastAndReceive);
            bulletg.setLocalTranslation(app.getCamera().getLocation());
            RigidBodyControl bulletControl = new RigidBodyControl(1);
            bulletg.addControl(bulletControl);
            bulletControl.setLinearVelocity(app.getCamera().getDirection().mult(275));
            bulletg.addControl(bulletControl);
            bulletControl.setRestitution(1.5f);
                  
            rootNode.attachChild(bulletg);
            space.add(bulletControl);
            Sounds.audio_gun.playInstance(); // play each instance once! 
            scoreManager.updateScore(ScoreManager.Points.BULLET);
          }
        }                    
      };
    
    app.getInputManager().addMapping("shoot", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
    app.getInputManager().addListener(actionListener, "shoot");    
  }
}

