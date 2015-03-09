package physics;

import com.jme3.asset.AssetManager;
import com.jme3.asset.TextureKey;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.light.AmbientLight;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.texture.Texture;

/**
 *
 * @author John M. Lasheski
 */
public class World {
  private static RigidBodyControl landscape;
  private static Node scene;
  
  // Variables for the walls of the world
  private static RigidBodyControl side1_phy, side2_phy, side3_phy, side4_phy;
  private static final Box side1, side2, side3, side4;
  private static  Material wall_material;
  
  public static Material lineMat;
    
  static {
    side1 = new Box(256, 5, 0.5f);
    side1.scaleTextureCoordinates(new Vector2f(3, 6));
          
    side2 = new Box(0.5f, 5f, 256f);
    side2.scaleTextureCoordinates(new Vector2f(3, 6));
          
    side3 = new Box(256, 5, 0.5f);
    side3.scaleTextureCoordinates(new Vector2f(3, 6));
          
    side4 = new Box(0.5f, 5f, 256f);
    side4.scaleTextureCoordinates(new Vector2f(3, 6));   
  }

  private Vector3f normalGravity = new Vector3f(0, -9.81f, 0);

  public static Node createWorld(AssetManager assetManager, PhysicsSpace space) {

    // Used by the AI to draw the search rays for debugging
    lineMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");

    // Load the scene we created
    scene = (Node) assetManager.loadModel("/Scenes/P3_Scene.j3o");
    
    // Add some collision physics to the scene
    CollisionShape sceneShape = CollisionShapeFactory.createMeshShape(scene);
    landscape = new RigidBodyControl(sceneShape, 0);
    scene.addControl(landscape);

    scene.addControl(new RigidBodyControl(0));
    space.add(scene);
    space.add(landscape);

    initMaterials(assetManager);
    initWalls(space);
    
    AmbientLight light = new AmbientLight();
    light.setColor(ColorRGBA.LightGray);
    scene.addLight(light);

    return scene;
  }
  

  // Initialize the materials used in this scene.
  private static void initMaterials(AssetManager assetManager) {    
    wall_material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    TextureKey key3 = new TextureKey("Blender/2.4x/textures/WarningStrip.png");
    key3.setGenerateMips(true);
    Texture tex3 = assetManager.loadTexture(key3);
    tex3.setWrap(Texture.WrapMode.Repeat);
    wall_material.setTexture("ColorMap", tex3);
    wall_material.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
  }

  // Initialize the collision cube used to contain the spheres
  private static void initWalls(PhysicsSpace space) {
    // Create side1
    Geometry side1_geo = new Geometry("Side1", side1);
    side1_geo.setQueueBucket(RenderQueue.Bucket.Transparent);
    side1_geo.setMaterial(wall_material);
    scene.attachChild(side1_geo);
    side1_phy = new RigidBodyControl(0.0f);
    side1_geo.addControl(side1_phy);
    space.add(side1_phy);
    side1_phy.setFriction(0.0f);
    side1_phy.setPhysicsLocation(new Vector3f(0, 2.5f, 256));
    side1_phy.setFriction(0.0f);
    side1_phy.setRestitution(.1f);
    
    
    // Create side2
    Geometry side2_geo = new Geometry("Side2", side2);
    side2_geo.setQueueBucket(RenderQueue.Bucket.Transparent);
    side2_geo.setMaterial(wall_material);
    scene.attachChild(side2_geo);
    side2_phy = new RigidBodyControl(0.0f);
    side2_geo.addControl(side2_phy);
    space.add(side2_phy);
    side2_phy.setFriction(0.0f);
    side2_phy.setPhysicsLocation(new Vector3f(256, 2.5f, 0));
    side2_phy.setFriction(0.0f);
    side2_phy.setRestitution(.1f);
    

    // Create side3
    Geometry side3_geo = new Geometry("Side3", side3);
    side3_geo.setQueueBucket(RenderQueue.Bucket.Transparent);
    side3_geo.setMaterial(wall_material);
    scene.attachChild(side3_geo);
    side3_phy = new RigidBodyControl(0.0f);
    side3_geo.addControl(side3_phy);
    space.add(side3_phy);
    side3_phy.setFriction(0.0f);
    side3_phy.setPhysicsLocation(new Vector3f(0, 2.5f, -256));
    side3_phy.setFriction(0.0f);
    side3_phy.setRestitution(.1f);
          
          
    // Create side4
    Geometry side4_geo = new Geometry("Side4", side4);
    side4_geo.setQueueBucket(RenderQueue.Bucket.Transparent);
    side4_geo.setMaterial(wall_material);
    scene.attachChild(side4_geo);
    side4_phy = new RigidBodyControl(0.0f);
    side4_geo.addControl(side4_phy);
    space.add(side4_phy);
    side4_phy.setFriction(0.0f);
    side4_phy.setPhysicsLocation(new Vector3f(-256, 2.5f, 0));
    side4_phy.setFriction(0.0f);
    side4_phy.setRestitution(.1f);   
  }  
}

