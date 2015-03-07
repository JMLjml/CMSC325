package targets;

import AI.AIControl;
import animations.AIAnimationControl;
import effects.Explosion;
import characters.AICharacterControl;
import characters.NavMeshNavigationControl;
import com.jme3.app.FlyCamAppState;
import com.jme3.asset.AssetManager;
import com.jme3.bounding.BoundingSphere;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.light.DirectionalLight;
import com.jme3.math.Matrix3f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 *
 * @author John M. Lasheski
 */
public class Buggy {
  protected static Node targetNode = new Node();
  private static boolean buggyActive = false;  
  private static int buggySpawnTimer = 0;

  private static Vector3f startLocation = new Vector3f(-40, 7, 25);
  private static Matrix3f startRotation = new Matrix3f(-1f, 0, .4f, 0, 1, 0, -.4f, 0, -1);
 
  public static Node spawnBuggy(Node scene, Node mainPlayer, AssetManager assetManager, PhysicsSpace space) {

    // We have created the buggy
    buggyActive = true;

    // Load model
    Node buggy = (Node) assetManager.loadModel("Models/Buggy/Buggy.j3o");

    // Name the Buggy for use in physics collision listening
    buggy.setName("Buggy");   

    // Scale the model to make it bigger
    buggy.setLocalScale(1f);
  
    // Set the start location and rotation for the buggy
    buggy.setLocalTranslation(startLocation);
   
    // The Buggy needed extra light
    DirectionalLight sun1 = new DirectionalLight();
    sun1.setDirection(new Vector3f(-0.1f, -0.7f, 10.0f));
    targetNode.addLight(sun1);
        
    DirectionalLight sun2 = new DirectionalLight();
    sun2.setDirection(new Vector3f(-0.1f, -0.7f, -10.0f));
    targetNode.addLight(sun2);
       
   
    // Add the AI Control
    AICharacterControl physicsCharacter = new AICharacterControl(2.2f, 4.4f, 50f);
    buggy.addControl(physicsCharacter);
    space.add(physicsCharacter);
    
    AIControl aicontrol = new AIControl();
    aicontrol.setViewDirection(new Vector3f(0,0, -1));
    buggy.addControl(aicontrol);
    
    
    // Add the mainPlayer as a target for the buggy
    List<Spatial> targets = new ArrayList<Spatial>();
    targets.add(mainPlayer);
        
    buggy.getControl(AIControl.class).setTargetList(targets);
            
    // Attach Buggy to the targetNode
    targetNode.attachChild(buggy);
    
    return targetNode;
  }


  // Repond to a collison between a bullet and the buggy
  public static void buggyCollision(PhysicsCollisionEvent event, AssetManager assetManager, PhysicsSpace space) {
        
    // trigger an explosion here
    Explosion.triggerExplosion(targetNode, event, assetManager);
    
    // The buggy is no longer in existance
    buggyActive = false;
    
    // Remove the buggy and the bullet from parent nodes and physics space
    event.getNodeA().removeFromParent();
    space.remove(event.getNodeA());
    event.getNodeB().removeFromParent();
    space.remove(event.getNodeB());
    
    // Set the buggySpawnTimer here to countdown until a new buggy is spawned
    buggySpawnTimer = 300;
  }

  // Returns true if it is time to spawn another EvilMonkey
  public static boolean checkBuggy() {
    if(!buggyActive) {
      buggySpawnTimer--;

      if(buggySpawnTimer == 0) {
        return true;
      }
    }
    
    return false;
  }
}

