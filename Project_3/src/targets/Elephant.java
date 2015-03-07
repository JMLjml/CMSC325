package targets;

import effects.Explosion;
import characters.AICharacterControl;
import characters.NavMeshNavigationControl;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.light.DirectionalLight;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import static targets.Buggy.targetNode;

/**
 *
 * @author John M. Lasheski
 */
public class Elephant {  
  protected static Node targetNode = new Node();
  private static boolean elephantActive = false;  
  private static int elephantSpawnTimer = 0;

  private static int navTimer = 3000;
  private static boolean going = false;
  private static Vector3f startLocation = new Vector3f(-30, 1.14f,-140);
  private static Vector3f navLocation = new Vector3f(-44, 1.14f, -40);

  private static NavMeshNavigationControl navMesh;
  private static CharacterControl aiplayer;

  public static Node spawnElephant(Node scene, AssetManager assetManager, PhysicsSpace space) {

    // We have created an elephant and it is going to the navLocation
    elephantActive = true;
    going = true;
    
    // Load model
    Node elephant = (Node) assetManager.loadModel("Models/Elephant/Elephant.mesh.xml");
   
    // Name the elephant for use in physics collision listening
    elephant.setName("Elephant");   

    // Scale the elephant to make it smaller
    elephant.setLocalScale(.25f);
        
    // Set the start location for the elephant
    elephant.setLocalTranslation(startLocation);
    
    // The Elephant needed extra light
    DirectionalLight sun1 = new DirectionalLight();
    sun1.setDirection(new Vector3f(-0.1f, -0.7f, 10.0f));
    targetNode.addLight(sun1);
        
    DirectionalLight sun2 = new DirectionalLight();
    sun2.setDirection(new Vector3f(-0.1f, -0.7f, -10.0f));
    targetNode.addLight(sun2);
    
    // Add the AI Character control to the elephant and add it to the physics space
    AICharacterControl physicsCharacter = new AICharacterControl(10f, 20f, 100f);
    elephant.addControl(physicsCharacter);
    space.add(physicsCharacter);

    // Create the navMesh control and add it to the elephant
    navMesh = new NavMeshNavigationControl((Node) scene);        
    elephant.addControl(navMesh);

    // Tell the elephant to start walking to the navLocation
    navMesh.moveTo(navLocation);

    // Attach the elephant to the targetNode
    targetNode.attachChild(elephant);

    return targetNode;
  }


  // Repond to a collison between a bullet and the elephant
  public static void elephantCollision(PhysicsCollisionEvent event, AssetManager assetManager, PhysicsSpace space) {
        
    // trigger an explosion here
    Explosion.triggerExplosion(targetNode, event, assetManager);
    
    // The elephant is no longer in existance
    elephantActive = false;
    
    // Remove the elephant and the bullet from parent nodes and physics space
    event.getNodeA().removeFromParent();
    space.remove(event.getNodeA());
    event.getNodeB().removeFromParent();
    space.remove(event.getNodeB());
    
    // Set the evilMonkeySpawnTimer here to countdown until a new evilMonkey is spawned
    elephantSpawnTimer = 300;
  }


  // Used to determine if the elephant should turn around and walk back to the previous location
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


  // Returns true if it is time to spawn another elephant
  public static boolean checkElephant() {
    if(!elephantActive) {
      elephantSpawnTimer--;

      if(elephantSpawnTimer == 0) {
        return true;
      }
    }
    
    return false;
  }    
}

