package targets;

import effects.Explosion;
import characters.AICharacterControl;
import characters.NavMeshNavigationControl;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

/**
 *
 * @author John M. Lasheski
 */
public class EvilMonkey {
  protected static Node targetNode = new Node();
  private static boolean evilMonkeyActive = false;  
  private static int evilMonkeySpawnTimer = 0;

  private static int navTimer = 3000;
  private static boolean going = false;
  private static Vector3f startLocation = new Vector3f(-154, 1.14f, 40);
  private static Vector3f navLocation = new Vector3f(-44, 1.14f, -40);
  
  private static NavMeshNavigationControl navMesh;
  private static CharacterControl aiplayer;

  public static Node spawnEvilMonkey(Node scene, AssetManager assetManager, PhysicsSpace space) {
    
    // We have created a monkey and it is going to the navLocation
    evilMonkeyActive = true;
    going = true;

    // Load model
    Node aiEvilMonkey = (Node) assetManager.loadModel("Models/Jaime/Jaime.j3o");

    // Name the EvilMonkey for use in physics collision listening
    aiEvilMonkey.setName("EvilMonkey");   

    // Scale the model to make it big
    aiEvilMonkey.setLocalScale(6f);
  
    // Set the start location for the EvilMonkey
    aiEvilMonkey.setLocalTranslation(startLocation);

    // Add the AI Character control to the monkey and add it to the physics space
    AICharacterControl physicsCharacter = new AICharacterControl(2f, 10f, 1f);
    aiEvilMonkey.addControl(physicsCharacter);
    space.add(physicsCharacter);

    // Create the navMesh control and add it to the monkey
    navMesh = new NavMeshNavigationControl((Node) scene);        
    aiEvilMonkey.addControl(navMesh);

    // Tell the monkey to start walking to the navLocation
    navMesh.moveTo(navLocation);

    // Attach the EvilMonkey to the targetNode
    targetNode.attachChild(aiEvilMonkey);

    return targetNode;
  }
  

  // Repond to a collison between a bullet and the EvilMonkey
  public static void evilMonkeyCollision(PhysicsCollisionEvent event, AssetManager assetManager, PhysicsSpace space) {
        
    // trigger an explosion here
    Explosion.triggerExplosion(targetNode, event, assetManager);
    
    // The evilMonkey is no longer in existance
    evilMonkeyActive = false;
    
    // Remove the monkey and the bullet from parent nodes and physics space
    event.getNodeA().removeFromParent();
    space.remove(event.getNodeA());
    event.getNodeB().removeFromParent();
    space.remove(event.getNodeB());
    
    // Set the evilMonkeySpawnTimer here to countdown until a new evilMonkey is spawned
    evilMonkeySpawnTimer = 300;
  }
  

  // Used to determine if the monkey should turn around and walk back to the previous location
  public static void checkLocation() {

    if(navTimer <= 0 && going) {
       navMesh.moveTo(startLocation);
       navTimer = 3000;
       going = false;
       
    } else if (navTimer <= 0 && !going) {
      navMesh.moveTo(navLocation);
      navTimer = 3000;
      going = true;
    }
    
    navTimer--;    
  }

  
  // Returns true if it is time to spawn another EvilMonkey
  public static boolean checkEvilMonkey() {
    if(!evilMonkeyActive) {
      evilMonkeySpawnTimer--;

      if(evilMonkeySpawnTimer == 0) {
        return true;
      }
    }
    
    return false;
  }
}

