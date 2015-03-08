package mygame;

import physics.BallShooter;
import effects.Sounds;
import targets.Boulders;
import targets.Crates;
import targets.Elephant;
import targets.EvilMonkey;
import targets.Buggy;
import scores.Scores;
import scores.ScoreManager;
import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import animations.AdvAnimationManagerControl;
import animations.CharacterInputAnimationAppState;
import characters.AICharacterControl;
import characters.ChaseCamCharacter;
import characters.NavMeshNavigationControl;
import com.jme3.app.FlyCamAppState;
import com.jme3.asset.TextureKey;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.input.ChaseCamera;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.Trigger;
import com.jme3.light.AmbientLight;
import com.jme3.material.RenderState;
import com.jme3.math.Vector2f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;
import com.jme3.texture.Texture;
import java.util.ArrayList;
import java.util.List;

import com.jme3.niftygui.NiftyJmeDisplay;
import de.lessvoid.nifty.Nifty;

/**
 * 
 * @author John M. Lasheski
 */
public class Main extends SimpleApplication implements PhysicsCollisionListener {
  
  private BulletAppState bulletAppState;
  private RigidBodyControl landscape;
  private Node scene;
  
  private AdvAnimationManagerControl animControl;
    
  
  // Variables for the walls of the world
  private RigidBodyControl side1_phy, side2_phy, side3_phy, side4_phy;
  private static final Box side1, side2, side3, side4;
  private Material wall_material;
  
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
  

  private Node mainPlayer;
  
  private Vector3f normalGravity = new Vector3f(0, -9.81f, 0);
  
  // Make the app static so we can call the app.stop() method
  private static Main app;
  
  
  //private gameRunningState    = new GameRunningState(this);
  
  
   
  private Trigger pause_trigger = new KeyTrigger(KeyInput.KEY_BACK);
//  private Trigger save_trigger = new KeyTrigger(KeyInput.KEY_RETURN);
  private boolean isRunning = false; // starts at startscreen

  public static void main(String[] args) {
    
    
    app = new Main();
    
    //ScoreManager.initScoreManager();
    // Display welcome screen
    // make the welcome screen call the initPlayerScore() and 
    // app.start() method after collecting playername
    
    app.start();
    
    // Call app.stop somewhere to end the app
    
    // Display ending screen
    
  }

  
  
//  private ActionListener actionListener = new ActionListener() {
//    public void onAction(String name, boolean isPressed, float tpf) {
//      System.out.println("key" + name);
//      if (name.equals("Game Pause Unpause") && !isPressed) {
//        if (isRunning) {
//          stateManager.detach(bulletAppState);
//          stateManager.attach(startScreenState);
//          System.out.println("switching to startscreen...");
// 
//        } else {
//          stateManager.detach(startScreenState);
//          stateManager.attach(bulletAppState);
//          System.out.println("switching to game...");
//        }
//        isRunning = !isRunning;
//      } 
      
//      else if (name.equals("Toggle Settings") && !isPressed && !isRunning) {
//        if (!isRunning && stateManager.hasState(startScreenState)) {
//          stateManager.detach(startScreenState);
//          stateManager.attach(settingsScreenState);
//          System.out.println("switching to settings...");
//        } else if (!isRunning && stateManager.hasState(settingsScreenState)) {
//          stateManager.detach(settingsScreenState);
//          stateManager.attach(startScreenState);
//          System.out.println("switching to startscreen...");
//        }
      //}
  //  }
  //};
  
  

  @Override
  public void simpleInitApp() {
    
    
    /* If you want to switch back and forth then you could create and add both 
     * of them during simpleInit and just setEnabled(false) the game state. 
     * Main menu can enable it and disable itself and whatever menu key can 
     * reverse that to bring back the menu.
     * 
     * In that case, you want to do your showing/hiding/attaching/removing 
     * scene elements, etc. in the setEnabled() as appropriate. */
    
    

    
    
    
    
    
    //startScreenState    = new StartScreenState(this);
    
    //stateManager.attach(startScreenState);
    
    
    // inputManager.addMapping("Game Pause Unpause", pause_trigger);
   // inputManager.addListener(actionListener, new String[]{"Game Pause Unpause"});
    
    
    
    lineMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");

  
    
    /** Set up Physics */
    bulletAppState = new BulletAppState();
    stateManager.attach(bulletAppState);
    
    
    
    // Turn this on for debug
    //bulletAppState.getPhysicsSpace().enableDebug(assetManager);
    
    stateManager.detach(stateManager.getState(FlyCamAppState.class));
    
    AmbientLight light = new AmbientLight();
        light.setColor(ColorRGBA.LightGray);
        rootNode.addLight(light);
    
    
    scene = (Node) assetManager.loadModel("/Scenes/P3_Scene.j3o");

    CollisionShape sceneShape = 
                CollisionShapeFactory.createMeshShape(scene);
        landscape = new RigidBodyControl(sceneShape, 0);
        scene.addControl(landscape);
    
    scene.addControl(new RigidBodyControl(0));
    bulletAppState.getPhysicsSpace().add(scene);
     bulletAppState.getPhysicsSpace().add(landscape);
    
     //Attach the scene to the root
     rootNode.attachChild(scene);
     
     initMaterials();
     initWalls();
     
     // initialize the game's audio 
     rootNode.attachChild(Sounds.initAudio(assetManager));
     
      // add ourselves as collision listener
    getPhysicsSpace().addCollisionListener(this);
     
     // Create the player and initialize the players scores
     mainPlayer = Player.createMainPlayer(stateManager, assetManager, inputManager, bulletAppState.getPhysicsSpace(), cam);
     rootNode.attachChild(mainPlayer);
     ScoreManager.initPlayerScore();
      
       //Add the "bullets" to the scene to allow the player to shoot the balls
       BallShooter.createBallShooter(this, rootNode, bulletAppState.getPhysicsSpace());

        
        // Create the target crates for the first time
        rootNode.attachChild(Crates.spawnCrates(assetManager, bulletAppState.getPhysicsSpace()));
        

        // Create the target boulders for the first time
        rootNode.attachChild(Boulders.spawnBoulders(assetManager, bulletAppState.getPhysicsSpace()));
            
        
    // Create the evilMonkey for the first time
    rootNode.attachChild(EvilMonkey.spawnEvilMonkey(scene, assetManager, bulletAppState.getPhysicsSpace()));
        
    // Create the elephant for the first time
    rootNode.attachChild(Elephant.spawnElephant(scene, assetManager, bulletAppState.getPhysicsSpace()));
        
    // Create Buggy for the first time
    rootNode.attachChild(Buggy.spawnBuggy(scene, mainPlayer, assetManager, bulletAppState.getPhysicsSpace()));
        
        
        
        
        
        
        
        //Add a custom font and text to the scene
        BitmapFont myFont = assetManager.loadFont("Interface/Fonts/DroidSansMono.fnt");
        
        
         // Create the crosshairs for the ball shooter
        BitmapText crosshairs = new BitmapText(myFont, true); 
        crosshairs.setText("X");
        crosshairs.setColor(ColorRGBA.Yellow);
        crosshairs.setSize(guiFont.getCharSet().getRenderedSize());
        crosshairs.setLocalTranslation(settings.getWidth() / 2,
            settings.getHeight() / 2 + crosshairs.getLineHeight() - 30, 0f);
        guiNode.attachChild(crosshairs);                 
                
  }


  
  
  
  
  
  private PhysicsSpace getPhysicsSpace() {
        return bulletAppState.getPhysicsSpace();
    }
  
    
  
   /** Initialize the materials used in this scene. */
  public void initMaterials() {
    
    wall_material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    TextureKey key3 = new TextureKey("Blender/2.4x/textures/WarningStrip.png");
    key3.setGenerateMips(true);
    Texture tex3 = assetManager.loadTexture(key3);
    tex3.setWrap(Texture.WrapMode.Repeat);
    wall_material.setTexture("ColorMap", tex3);
    wall_material.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
  }
  
   
   
   // Initialize the collision cube used to contain the spheres
  public void initWalls() {
    // Create side1
    Geometry side1_geo = new Geometry("Side1", side1);
    side1_geo.setQueueBucket(RenderQueue.Bucket.Transparent);
    side1_geo.setMaterial(wall_material);
    this.rootNode.attachChild(side1_geo);
    side1_phy = new RigidBodyControl(0.0f);
    side1_geo.addControl(side1_phy);
    bulletAppState.getPhysicsSpace().add(side1_phy);
    side1_phy.setFriction(0.0f);
    side1_phy.setPhysicsLocation(new Vector3f(0, 2.5f, 256));
    side1_phy.setFriction(0.0f);
    side1_phy.setRestitution(.1f);
    
    
    // Create side2
    Geometry side2_geo = new Geometry("Side2", side2);
    side2_geo.setQueueBucket(RenderQueue.Bucket.Transparent);
    side2_geo.setMaterial(wall_material);
    this.rootNode.attachChild(side2_geo);
    side2_phy = new RigidBodyControl(0.0f);
    side2_geo.addControl(side2_phy);
    bulletAppState.getPhysicsSpace().add(side2_phy);
    side2_phy.setFriction(0.0f);
    side2_phy.setPhysicsLocation(new Vector3f(256, 2.5f, 0));
    side2_phy.setFriction(0.0f);
    side2_phy.setRestitution(.1f);
          

    // Create side3
    Geometry side3_geo = new Geometry("Side3", side3);
    side3_geo.setQueueBucket(RenderQueue.Bucket.Transparent);
    side3_geo.setMaterial(wall_material);
    this.rootNode.attachChild(side3_geo);
    side3_phy = new RigidBodyControl(0.0f);
    side3_geo.addControl(side3_phy);
    bulletAppState.getPhysicsSpace().add(side3_phy);
    side3_phy.setFriction(0.0f);
    side3_phy.setPhysicsLocation(new Vector3f(0, 2.5f, -256));
    side3_phy.setFriction(0.0f);
    side3_phy.setRestitution(.1f);
          
          
     // Create side4
    Geometry side4_geo = new Geometry("Side4", side4);
    side4_geo.setQueueBucket(RenderQueue.Bucket.Transparent);
    side4_geo.setMaterial(wall_material);
    this.rootNode.attachChild(side4_geo);
    side4_phy = new RigidBodyControl(0.0f);
    side4_geo.addControl(side4_phy);
    bulletAppState.getPhysicsSpace().add(side4_phy);
    side4_phy.setFriction(0.0f);
    side4_phy.setPhysicsLocation(new Vector3f(-256, 2.5f, 0));
    side4_phy.setFriction(0.0f);
    side4_phy.setRestitution(.1f);   
  }
   
   
   
  @Override
  public void simpleUpdate(float tpf) {

    // See if it is time to spawn more crates
    if(Crates.checkCrates()) {
      rootNode.attachChild(Crates.spawnCrates(assetManager, bulletAppState.getPhysicsSpace()));
    }

    // See if it is time to spawn more boulders
    if(Boulders.checkBoulders()) {
      rootNode.attachChild(Boulders.spawnBoulders(assetManager, bulletAppState.getPhysicsSpace()));
    }    
    
    // See where the evilMonkey is
    EvilMonkey.checkLocation();
    
    // See if it is time to spawn another evilMonkey
    if(EvilMonkey.checkEvilMonkey()) {
      rootNode.attachChild(EvilMonkey.spawnEvilMonkey(scene, assetManager, bulletAppState.getPhysicsSpace()));
    }

    // See where the elephant is
    Elephant.checkLocation();

    // See if it is time to spawn another elephant
    if(Elephant.checkElephant()) {
      rootNode.attachChild(Elephant.spawnElephant(scene, assetManager, bulletAppState.getPhysicsSpace()));
    }  
    
    // See if it is time to spawn another buggy
    if(Buggy.checkBuggy()) {
      rootNode.attachChild(Buggy.spawnBuggy(scene, mainPlayer, assetManager, bulletAppState.getPhysicsSpace()));
    }     
  }




  @Override
  public void simpleRender(RenderManager rm) {
    //TODO: add render code
  }



  public void collision(PhysicsCollisionEvent event) {
   
    // Check for collisions with the crates
    if(event.getNodeA().getName().equals("crate") || event.getNodeB().getName().equals("crate")) {
      if(event.getNodeA().getName().equals("bullet") || event.getNodeB().getName().equals("bullet")) {
        Crates.crateCollision(event, assetManager, bulletAppState.getPhysicsSpace());
        ScoreManager.updateScore(ScoreManager.Points.CRATE);
        ScoreManager.updateScore(ScoreManager.Points.BULLET);
      }
    }
  
    // check for collisions with the boulders
    if(event.getNodeA().getName().equals("boulder") || event.getNodeB().getName().equals("boulder")) {
      if(event.getNodeA().getName().equals("bullet") || event.getNodeB().getName().equals("bullet")) {
        Boulders.boulderCollision(event, assetManager, bulletAppState.getPhysicsSpace());
        ScoreManager.updateScore(ScoreManager.Points.BOULDER);
        ScoreManager.updateScore(ScoreManager.Points.BULLET);
      }
    }
     
    // check for collisions with the evilMonkey
    if(event.getNodeA().getName().equals("EvilMonkey") || event.getNodeB().getName().equals("EvilMonkey")) {
      if(event.getNodeA().getName().equals("bullet") || event.getNodeB().getName().equals("bullet")) {
        EvilMonkey.evilMonkeyCollision(event, assetManager, bulletAppState.getPhysicsSpace());
        ScoreManager.updateScore(ScoreManager.Points.EVILMONKEY);
        ScoreManager.updateScore(ScoreManager.Points.BULLET);
      }
    }
    

    // check for collisions with the elephant
    if(event.getNodeA().getName().equals("Elephant") || event.getNodeB().getName().equals("Elephant")) {
      if(event.getNodeA().getName().equals("bullet") || event.getNodeB().getName().equals("bullet")) {
        Elephant.elephantCollision(event, assetManager, bulletAppState.getPhysicsSpace());
        ScoreManager.updateScore(ScoreManager.Points.ELEPHANT);
        ScoreManager.updateScore(ScoreManager.Points.BULLET);
      }
    }   
    
    // check for collisions with the buggy
    if(event.getNodeA().getName().equals("Buggy") || event.getNodeB().getName().equals("Buggy")) {
      if(event.getNodeA().getName().equals("bullet") || event.getNodeB().getName().equals("bullet")) {
        Buggy.buggyCollision(event, assetManager, bulletAppState.getPhysicsSpace());
        ScoreManager.updateScore(ScoreManager.Points.BUGGY);
        ScoreManager.updateScore(ScoreManager.Points.BULLET);
      }
    }
  }
}

