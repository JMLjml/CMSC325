package mygame;

import appstate.MyAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.TextureKey;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import com.jme3.scene.shape.Sphere.TextureMode;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import characters.AICharacterControl;
import characters.NavMeshNavigationControl;


/**
  * @author John M. Lasheski
 */


public class Main extends SimpleApplication implements PhysicsCollisionListener {
  /** Prepare the Physics Application State (jBullet) */
  private BulletAppState bulletAppState;
     
  private Vector3f normalGravity = new Vector3f(0, -9.81f, 0);
  private Vector3f marsGravity = new Vector3f(0, -1f, 0);

      
  /** create the physics cude for containing the spheres */
  private RigidBodyControl floor_phy, side1_phy, side2_phy, side3_phy, side4_phy, roof_phy;
  private static final Box floor, side1, side2, side3, side4, roof;
  private Material cube_mat, stone_mat;
     
  private RigidBodyControl sphere1_phy, sphere2_phy, sphere3_phy;
  private Sphere sphere1, sphere2, sphere3;
  
  private int collision_count = 0;
   
  
  // Text for displaying sphere position
  private BitmapText sp1Text, sp2Text, sp3Text;
  //Add a custom font and text to the scene
  private BitmapFont myFont;
  
  // Create the file for storing the sphere locations
  private java.io.File file; 
  public static java.io.PrintWriter output;
     
  static {
    floor = new Box(256, 2f, 256f);
    floor.scaleTextureCoordinates(new Vector2f(3, 6));
          
    side1 = new Box(30f, 30f, 0.1f);
    side1.scaleTextureCoordinates(new Vector2f(3, 6));
          
    side2 = new Box(0.1f, 30f, 30f);
    side2.scaleTextureCoordinates(new Vector2f(3, 6));
          
    side3 = new Box(30f, 30f, 0.1f);
    side3.scaleTextureCoordinates(new Vector2f(3, 6));
          
    side4 = new Box(0.1f, 30f, 30f);
    side4.scaleTextureCoordinates(new Vector2f(3, 6));
         
    roof = new Box(30f, 0.1f, 30f);
    roof.scaleTextureCoordinates(new Vector2f(3, 6));
  }

  
  // Make the app static so we can call the app.stop() method
  private static Main app;
  
  public static void main(String[] args) {
    app = new Main();
    app.start();    
    output.close();    
  }

  
  @Override
  public void simpleInitApp() {    
    
    /** Set up Physics Game */
    bulletAppState = new BulletAppState();
    stateManager.attach(bulletAppState);
    
    // Add myAppState for better cleanup()
    MyAppState myappstate = new MyAppState();
    stateManager.attach(myappstate);
    
    // Setup normal gravity
    bulletAppState.getPhysicsSpace().setGravity(normalGravity);
        
    // debug
    //bulletAppState.getPhysicsSpace().enableDebug(assetManager);
        
        
    /** Configure cam to look at scene */
    getFlyByCamera().setMoveSpeed(45f);
    cam.setLocation(new Vector3f(390, 175, 390));
    cam.lookAt(new Vector3f(0,0,0), Vector3f.UNIT_Y);
        
    // Setup the world, collision cube and spheres
    Node scene = setupWorld();
    
    // add ourselves as collision listener
    getPhysicsSpace().addCollisionListener(this);
    
    // Add the AI monkey to the scene
    setupCharacter(scene);
    
   // Add the HUD
    initHud();
   
  }
 
  
  private PhysicsSpace getPhysicsSpace(){
    return bulletAppState.getPhysicsSpace();
  }

  
  private Node setupWorld() {
    Node scene = (Node) assetManager.loadModel("Scenes/P2_Scene.j3o");
    
    rootNode.attachChild(scene);
    
    //TODO: navmesh only for debug
    Geometry navGeom = new Geometry("NavMesh");
    navGeom.setMesh(((Geometry) scene.getChild("NavMesh" )).getMesh());
    Material green = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    green.setColor("Color", ColorRGBA.Green);
    green.getAdditionalRenderState().setWireframe(true);
    navGeom.setMaterial(green);
    rootNode.attachChild(navGeom); 
            
    Spatial terrain = scene.getChild("terrain-P2_Scene");
    terrain.addControl(new RigidBodyControl(0));
    
      
 
    
    bulletAppState.getPhysicsSpace().addAll(terrain);
    
    initMaterials();
    initCube();
    initSpheres();
    
    try {
      initFile();
    } catch (Exception e) {
      System.out.println("File error. Program exiting.");
      e.printStackTrace();
    }
            
    
    return scene;
  }
    
    
  /** Initialize the materials used in this scene. */
  public void initMaterials() {
    stone_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    TextureKey key2 = new TextureKey("Textures/Terrain/Rock/Rock.PNG");
    key2.setGenerateMips(true);
    Texture tex2 = assetManager.loadTexture(key2);
    stone_mat.setTexture("ColorMap", tex2);
    
    cube_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    TextureKey key3 = new TextureKey("Blender/2.4x/textures/WarningStrip.png");
    key3.setGenerateMips(true);
    Texture tex3 = assetManager.loadTexture(key3);
    tex3.setWrap(WrapMode.Repeat);
    cube_mat.setTexture("ColorMap", tex3);
    cube_mat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
  }
    
  
  // Initialize the collision cube used to contain the spheres
  public void initCube() {
    
    // Create the floor
    Geometry floor_geo = new Geometry("Floor", floor);
    floor_geo.setQueueBucket(Bucket.Transparent);
    floor_geo.setMaterial(cube_mat);
    this.rootNode.attachChild(floor_geo);
    floor_phy = new RigidBodyControl(0.0f);
    floor_geo.addControl(floor_phy);
    bulletAppState.getPhysicsSpace().add(floor_phy);
    floor_phy.setPhysicsLocation(new Vector3f(0, -1.8f, 0));
    floor_phy.setFriction(0.0f);
    floor_phy.setRestitution(.1f);
          
    
    // Create side1
    Geometry side1_geo = new Geometry("Side1", side1);
    side1_geo.setQueueBucket(Bucket.Transparent);
    side1_geo.setMaterial(cube_mat);
    this.rootNode.attachChild(side1_geo);
    side1_phy = new RigidBodyControl(0.0f);
    side1_geo.addControl(side1_phy);
    bulletAppState.getPhysicsSpace().add(side1_phy);
    side1_phy.setFriction(0.0f);
    side1_phy.setPhysicsLocation(new Vector3f(226, 30, 256));
    side1_phy.setFriction(0.0f);
    side1_phy.setRestitution(.1f);
    
    
    // Create side2
    Geometry side2_geo = new Geometry("Side2", side2);
    side2_geo.setQueueBucket(Bucket.Transparent);
    side2_geo.setMaterial(cube_mat);
    this.rootNode.attachChild(side2_geo);
    side2_phy = new RigidBodyControl(0.0f);
    side2_geo.addControl(side2_phy);
    bulletAppState.getPhysicsSpace().add(side2_phy);
    side2_phy.setFriction(0.0f);
    side2_phy.setPhysicsLocation(new Vector3f(256, 30f, 226));
    side2_phy.setFriction(0.0f);
    side2_phy.setRestitution(.1f);
          

    // Create side3
    Geometry side3_geo = new Geometry("Side3", side3);
    side3_geo.setQueueBucket(Bucket.Transparent);
    side3_geo.setMaterial(cube_mat);
    this.rootNode.attachChild(side3_geo);
    side3_phy = new RigidBodyControl(0.0f);
    side3_geo.addControl(side3_phy);
    bulletAppState.getPhysicsSpace().add(side3_phy);
    side3_phy.setFriction(0.0f);
    side3_phy.setPhysicsLocation(new Vector3f(226, 30f, 196));
    side3_phy.setFriction(0.0f);
    side3_phy.setRestitution(.1f);
          
          
     // Create side4
    Geometry side4_geo = new Geometry("Side4", side4);
    side4_geo.setQueueBucket(Bucket.Transparent);
    side4_geo.setMaterial(cube_mat);
    this.rootNode.attachChild(side4_geo);
    side4_phy = new RigidBodyControl(0.0f);
    side4_geo.addControl(side4_phy);
    bulletAppState.getPhysicsSpace().add(side4_phy);
    side4_phy.setFriction(0.0f);
    side4_phy.setPhysicsLocation(new Vector3f(196, 30f, 226));
    side4_phy.setFriction(0.0f);
    side4_phy.setRestitution(.1f);
    
    
    // Create the roof
    Geometry roof_geo = new Geometry("Roof", roof);
    roof_geo.setQueueBucket(Bucket.Transparent);
    roof_geo.setMaterial(cube_mat);
    this.rootNode.attachChild(roof_geo);
    roof_phy = new RigidBodyControl(0.0f);
    roof_geo.addControl(roof_phy);
    bulletAppState.getPhysicsSpace().add(roof_phy);
    roof_phy.setPhysicsLocation(new Vector3f(226, 60f, 226));
    roof_phy.setFriction(0.0f);
    roof_phy.setRestitution(.1f);
  }
      
  
  // Create the spheres used in the colission cube
  public void initSpheres() {
    sphere1 = new Sphere(32, 32, 10f, true, false);
    sphere1.setTextureMode(TextureMode.Projected);
    sphere2 = new Sphere(32, 32, 10f, true, false);
    sphere2.setTextureMode(TextureMode.Projected);
    sphere3 = new Sphere(32, 32, 10f, true, false);
    sphere3.setTextureMode(TextureMode.Projected);
     
    
    // Create sphere1
    Geometry sphere1_geo = new Geometry("sphere1", sphere1);
    sphere1_geo.setMaterial(stone_mat);
    this.rootNode.attachChild(sphere1_geo);
    sphere1_geo.setLocalTranslation(240, 20, 240);
    sphere1_phy = new RigidBodyControl(10f);
    sphere1_geo.addControl(sphere1_phy);
    bulletAppState.getPhysicsSpace().add(sphere1_phy);
    sphere1_phy.setRestitution(2.0f);
    sphere1_phy.setFriction(.1f);
    sphere1_phy.setLinearVelocity(new Vector3f(100, 10, 70).mult(1));
    
    
    
    // Create sphere2
    Geometry sphere2_geo = new Geometry("sphere2", sphere2);
    sphere2_geo.setMaterial(stone_mat);
    this.rootNode.attachChild(sphere2_geo);
    sphere2_geo.setLocalTranslation(230, 10, 230);
    sphere2_phy = new RigidBodyControl(10f);
    sphere2_geo.addControl(sphere2_phy);
    bulletAppState.getPhysicsSpace().add(sphere2_phy);
    sphere2_phy.setRestitution(2.0f);
    sphere2_phy.setFriction(.1f);
    sphere2_phy.setLinearVelocity(new Vector3f(50, 40, 10).mult(1));
    
        
    // Create sphere3
    Geometry sphere3_geo = new Geometry("sphere3", sphere3);
    sphere3_geo.setMaterial(stone_mat);
    this.rootNode.attachChild(sphere3_geo);
    sphere3_geo.setLocalTranslation(235, 40, 245);
    sphere3_phy = new RigidBodyControl(10f);
    sphere3_geo.addControl(sphere3_phy);
    bulletAppState.getPhysicsSpace().add(sphere3_phy);
    sphere3_phy.setRestitution(2.0f);
    sphere3_phy.setFriction(.1f);
    sphere3_phy.setLinearVelocity(new Vector3f(25, 30, 50).mult(1));   
  }
    
  
  
  public void initFile() throws Exception {
    // Create the data file for storing the results of the colissions
    file = new java.io.File("colissions.txt");
    output = new java.io.PrintWriter(file);     
  }
  
  
  private void setupCharacter(Node scene) {
    
    // Load model, attach to character node
    Node aiCharacter = (Node) assetManager.loadModel("Models/Jaime/Jaime.j3o");

    AICharacterControl physicsCharacter = new AICharacterControl(0.3f, 2.5f, 1f);
    aiCharacter.addControl(physicsCharacter);
    bulletAppState.getPhysicsSpace().add(physicsCharacter);
    
    aiCharacter.setLocalScale(50f);
    scene.attachChild(aiCharacter);
    NavMeshNavigationControl navMesh = new NavMeshNavigationControl((Node) scene);
        
    aiCharacter.addControl(navMesh);
    navMesh.moveTo(new Vector3f(255, 2, 255));
  }
  
  
  public void initHud() {
    //Add a custom font and text to the scene
    myFont = assetManager.loadFont("Interface/Fonts/DroidSansMono.fnt");
    
    sp1Text = new BitmapText(myFont, true);
    sp1Text.setName("sp1Text");
    sp1Text.setText("sphere 1 " + sphere1_phy.getPhysicsLocation().toString());
    sp1Text.setColor(ColorRGBA.Red);
    sp1Text.setSize(guiFont.getCharSet().getRenderedSize());   
    sp1Text.setLocalTranslation(settings.getWidth() / 2 - sp1Text.getLineWidth() / 2, settings.getHeight() - 40, 0f);
    guiNode.attachChild(sp1Text);
    
    sp2Text = new BitmapText(myFont, true);
    sp2Text.setName("sp2Text");
    sp2Text.setText("sphere 2 " + sphere2_phy.getPhysicsLocation().toString());
    sp2Text.setColor(ColorRGBA.Red);
    sp2Text.setSize(guiFont.getCharSet().getRenderedSize());   
    sp2Text.setLocalTranslation(settings.getWidth() / 2 - sp2Text.getLineWidth() / 2, settings.getHeight() - 60, 0f);
    guiNode.attachChild(sp2Text);
    
    sp3Text = new BitmapText(myFont, true);
    sp3Text.setName("sp3Text");
    sp3Text.setText("sphere 3 " + sphere3_phy.getPhysicsLocation().toString());
    sp3Text.setColor(ColorRGBA.Red);
    sp3Text.setSize(guiFont.getCharSet().getRenderedSize());   
    sp3Text.setLocalTranslation(settings.getWidth() / 2 - sp3Text.getLineWidth() / 2, settings.getHeight() - 80, 0f);
    guiNode.attachChild(sp3Text);
  } 
  
  @Override
  public void simpleUpdate(float tpf) {
    // update the HUD
    sp1Text.setText("sphere 1 " + sphere1_phy.getPhysicsLocation().toString());
    sp2Text.setText("sphere 2 " + sphere2_phy.getPhysicsLocation().toString());
    sp3Text.setText("sphere 3 " + sphere3_phy.getPhysicsLocation().toString());
   
    // End the game if there are 100 collisions
    if(collision_count >= 100) {
      output.println("\nThe monkey was not in time. The spheres collided 100 times. Exiting program.");
      output.close();
      app.stop();
    }    
  }

  
  
  @Override
  public void simpleRender(RenderManager rm) {
    //TODO: add render code
  }

 
  
  public void collision(PhysicsCollisionEvent event) {
    
    if("sphere1".equals(event.getNodeA().getName()) || "sphere1".equals(event.getNodeB().getName())) {
      
      if("sphere2".equals(event.getNodeA().getName()) || "sphere2".equals(event.getNodeB().getName())) {
        
        collision_count++;
        output.println("\nCollision number: " + collision_count);
        output.print(event.getNodeA().getName() + " collided with ");        
        output.println(event.getNodeB().getName() + " at position:");
        output.println("sphere1: " + sphere1_phy.getPhysicsLocation());
        output.println("sphere2: " + sphere2_phy.getPhysicsLocation());
      }
      
      else if("sphere3".equals(event.getNodeA().getName()) || "sphere3".equals(event.getNodeB().getName())) {
        
        collision_count++;
        output.println("\nCollission number: " + collision_count);
        output.print(event.getNodeA().getName() + " collided with ");        
        output.println(event.getNodeB().getName() + " at position:");
        output.println("sphere1: " + sphere1_phy.getPhysicsLocation());
        output.println("sphere3: " + sphere3_phy.getPhysicsLocation());
      }
    }
    
    else if("sphere3".equals(event.getNodeA().getName()) || "sphere3".equals(event.getNodeB().getName())) {
      
      if("sphere2".equals(event.getNodeA().getName()) || "sphere2".equals(event.getNodeB().getName())) {
        
        collision_count++;
        output.println("\nCollission number: " + collision_count);
        output.print(event.getNodeA().getName() + " collided with ");        
        output.println(event.getNodeB().getName() + " at position:");
        output.println("sphere2: " + sphere2_phy.getPhysicsLocation());
        output.println("sphere3: " + sphere3_phy.getPhysicsLocation());
      }
    }    
  }
}
