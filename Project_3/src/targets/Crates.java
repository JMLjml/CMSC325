package targets;

import effects.Explosion;
import appstate.InputAppState;
import com.jme3.app.Application;
import com.jme3.asset.AssetManager;
import com.jme3.asset.TextureKey;
import com.jme3.audio.AudioNode;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.shapes.MeshCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.font.BitmapText;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Matrix3f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Dome;
import com.jme3.scene.shape.Sphere;
import com.jme3.scene.shape.Sphere.TextureMode;
import com.jme3.texture.Texture;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;



/**
 * @author John M. Lasheski for CMSC325 Spring 2015
 */
public class Crates {

  protected static Node targetNode = new Node();
  protected static List<Geometry> crates = new ArrayList<Geometry>();
  private static int crateSpawnTimer = 0;
  
  
  public static Node spawnCrates(AssetManager assetManager, PhysicsSpace space) {
    
    // Set the material for the crates
    Material material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    material.setTexture("ColorMap", assetManager.loadTexture("Blender/2.4x/WoodCrate_lighter.png"));
    
    // create the movable boxes and assign them as targets
    for (int i = 0; i < 25; i++) {
      Box box = new Box(5, 5, 5);
      Geometry boxGeometry = new Geometry("crate", box);
      boxGeometry.setMaterial(material);
      boxGeometry.setLocalTranslation(-.005f * i, 12 + 12 * i,-40 );
      boxGeometry.addControl(new RigidBodyControl(.001f));
      boxGeometry.getControl(RigidBodyControl.class).setRestitution(.1f);
      targetNode.attachChild(boxGeometry);
      space.add(boxGeometry);
      crates.add(boxGeometry);
    }

    return targetNode;
  }


  public static void crateCollision(PhysicsCollisionEvent event, AssetManager assetManager, PhysicsSpace space) {
    // trigger an explosion here
    Explosion.triggerExplosion(targetNode, event, assetManager);
       
    /* Remove the crate from the targetNode,the physicsSpace and the list 
       of crates. Also remove the bullet from the physicsSpace. */
    if(event.getNodeA().getName().equals("crate")) {
      crates.remove(event.getNodeA());
    
    } else {
      crates.remove(event.getNodeB());
    }
    
    event.getNodeA().removeFromParent();
    space.remove(event.getNodeA());
    event.getNodeB().removeFromParent();
    space.remove(event.getNodeB());
    
    // Set the crateSpawnTimere here to countdown until a new set of crates are spawned
    if(crates.isEmpty()) {
      crateSpawnTimer = 300;
    }
  }


  // Returns true if it is time to respawn the crates
  public static boolean checkCrates() {
    if(crates.isEmpty()) {
      crateSpawnTimer--;

      if(crateSpawnTimer == 0) {
        return true;
      }
    }

    return false;
  }  
}

