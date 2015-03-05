package targets;

import effects.Explosion;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Sphere;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author John M. Lasheski
 */
public class Boulders {
  protected static Node targetNode = new Node();
  protected static List<Geometry> boulders = new ArrayList<Geometry>();
  private static int boulderSpawnTimer = 0;

  public static Node spawnBoulders(AssetManager assetManager, PhysicsSpace space) {
    
    Material material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    material.setTexture("ColorMap", assetManager.loadTexture("Blender/2.4x/textures/WarningStrip.png"));

    // create the boulders and assign them as targets
    for (int i = 0; i < 5; i++) {
      Sphere sphere = new Sphere(22, 22, 6, true, false);
      Geometry sphereGeometry = new Geometry("boulder", sphere);
      sphereGeometry.setMaterial(material);
      sphereGeometry.setLocalTranslation(210 - (15 * i), 45, 40 );
            
      //RigidBodyControl automatically uses sphere collision shapes when attached to single geometry with pqt mesh
      sphereGeometry.addControl(new RigidBodyControl(.001f));
      sphereGeometry.getControl(RigidBodyControl.class).setRestitution(.50f);
      RigidBodyControl sphereControl = new RigidBodyControl(10);
      sphereGeometry.addControl(sphereControl);
      sphereControl.setLinearVelocity(new Vector3f(5 * i, 50, 50));
      
      targetNode.attachChild(sphereGeometry);
      space.add(sphereGeometry);
      boulders.add(sphereGeometry);
    }

    return targetNode;
  }

  public static void boulderCollision(PhysicsCollisionEvent event, AssetManager assetManager, PhysicsSpace space) {
    // trigger an explosion here
    Explosion.triggerExplosion(targetNode, event, assetManager);
       
    /* Remove the crate from the targetNode,the physicsSpace and the list 
       of crates. Also remove the bullet from the physicsSpace. */
    if(event.getNodeA().getName().equals("boulder")) {
      boulders.remove(event.getNodeA());
    
    } else {
      boulders.remove(event.getNodeB());
    }
    
    event.getNodeA().removeFromParent();
    space.remove(event.getNodeA());
    event.getNodeB().removeFromParent();
    space.remove(event.getNodeB());
    
    // Set the boulderSpawnTimere here to countdown until a new set of boulders are spawned
    if(boulders.isEmpty()) {
      boulderSpawnTimer = 300;
    }
  }

  // Returns true if it is time to respawn the boulders
  public static boolean checkBoulders() {
    if(boulders.isEmpty()) {
      boulderSpawnTimer--;

      if(boulderSpawnTimer == 0) {
        return true;
      }
    }

    return false;
  }  
}

