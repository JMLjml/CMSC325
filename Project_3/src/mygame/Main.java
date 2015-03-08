package mygame;

import gui.StartGUI;
import gui.PausedGUI;
import physics.World;
import appstate.GameRunningAppState;
import appstate.PausedAppState;
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

import com.jme3.app.Application;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.MouseButtonTrigger;
import java.io.Console;
import java.util.HashSet;


/**
 * 
 * @author John M. Lasheski
 */
public class Main extends SimpleApplication implements PhysicsCollisionListener {
  
  private BulletAppState bulletAppState;
  
  private GameRunningAppState gameRunningAppState;
  
  private PausedAppState pausedAppState;
  
  private CharacterInputAnimationAppState charInputAppState;
  
  
  private Node scene;
  public static Material lineMat;
  private Node mainPlayer;
  
  // Make the app static so we can call the app.stop() method
  private static Main app;
  
  //private gameRunningState    = new GameRunningState(this);
   
  private Trigger pause_trigger = new KeyTrigger(KeyInput.KEY_BACK);
//  private Trigger save_trigger = new KeyTrigger(KeyInput.KEY_RETURN);
  private boolean isRunning = true; 
  
  private static ScoreManager scoreManager;
          
  public static void main(String[] args) {
    
   
    
    
    app = new Main();
    
    scoreManager = new ScoreManager();
    
    scoreManager.initScoreManager();
    StartGUI gui = new StartGUI(scoreManager);
    //StartGUI.main(args);
  
    
   
    // Display welcome screen
    // make the welcome screen call the initPlayerScore() and 
    // app.start() method after collecting playername
    
    //StartGUI gui = new StartGUI();
    
    app.start();
    
    
    
    // Call app.stop somewhere to end the app
    
    // Display ending screen
    
  }


  private void shutDown() {
    scoreManager.addPlayerScore();
    scoreManager.writeHighScores();
    System.out.println(scoreManager.getHighScores());
    app.stop();
    //show gyui with scores
  }
  
  private ActionListener actionListener = new ActionListener() {
    public void onAction(String name, boolean isPressed, float tpf) {
      System.out.println("key" + name);
      if (name.equals("Game Pause Unpause") && !isPressed) {
        if(isRunning) {
//          pausedAppState.setEnabled(true);
//          isRunning = false;
//          bulletAppState.setEnabled(paused);
//          charInputAppState.setEnabled(paused);
//          inputManager.setCursorVisible(true);
//          flyCam.setEnabled(false);
//          inputManager.deleteMapping("shoot");
          shutDown();
          

          
          
//        } else {
//          pausedAppState.setEnabled(false);
//          isRunning = true;
//          bulletAppState.setEnabled(true);
//          charInputAppState.setEnabled(true);
//          inputManager.setCursorVisible(false);
//          flyCam.setEnabled(true);
//          BallShooter.createBallShooter(app, rootNode, bulletAppState.getPhysicsSpace());  
        }
      }
    }
  };
  
        
        

  
  

  @Override
  public void simpleInitApp() {

    
    
     inputManager.addMapping("Game Pause Unpause", pause_trigger);
    inputManager.addListener(actionListener, new String[]{"Game Pause Unpause"});
    
    
    
    lineMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");

    
    pausedAppState = new PausedAppState();
    stateManager.attach(pausedAppState);
    pausedAppState.setEnabled(paused);
    
    



    
    
    /** Set up Physics */
    bulletAppState = new BulletAppState();
    stateManager.attach(bulletAppState);
    
    
    //creat the charInputAppState 
    charInputAppState = new CharacterInputAnimationAppState();
    
    
    // Turn this on for debug
    //bulletAppState.getPhysicsSpace().enableDebug(assetManager);
    
    stateManager.detach(stateManager.getState(FlyCamAppState.class));

    // Create the world and attach it to the scene node and then to the root node
    scene = World.createWorld(assetManager, bulletAppState.getPhysicsSpace());
    rootNode.attachChild(scene);
     
    // initialize the game's audio 
    rootNode.attachChild(Sounds.initAudio(assetManager));
     
    // add ourselves as collision listener
    getPhysicsSpace().addCollisionListener(this);
     
    // Create the player and initialize the players scores
    mainPlayer = Player.createMainPlayer(stateManager, assetManager, inputManager, bulletAppState.getPhysicsSpace(), cam, charInputAppState);
    rootNode.attachChild(mainPlayer);
    
      
    //Add the "bullets" to the scene to allow the player to shoot the balls
    BallShooter.createBallShooter(this, rootNode, bulletAppState.getPhysicsSpace(), scoreManager);

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
        scoreManager.updateScore(ScoreManager.Points.CRATE);
      }
    }
  
    // check for collisions with the boulders
    if(event.getNodeA().getName().equals("boulder") || event.getNodeB().getName().equals("boulder")) {
      if(event.getNodeA().getName().equals("bullet") || event.getNodeB().getName().equals("bullet")) {
        Boulders.boulderCollision(event, assetManager, bulletAppState.getPhysicsSpace());
        scoreManager.updateScore(ScoreManager.Points.BOULDER);
      }
    }
     
    // check for collisions with the evilMonkey
    if(event.getNodeA().getName().equals("EvilMonkey") || event.getNodeB().getName().equals("EvilMonkey")) {
      if(event.getNodeA().getName().equals("bullet") || event.getNodeB().getName().equals("bullet")) {
        EvilMonkey.evilMonkeyCollision(event, assetManager, bulletAppState.getPhysicsSpace());
        scoreManager.updateScore(ScoreManager.Points.EVILMONKEY);
      }
    }
    

    // check for collisions with the elephant
    if(event.getNodeA().getName().equals("Elephant") || event.getNodeB().getName().equals("Elephant")) {
      if(event.getNodeA().getName().equals("bullet") || event.getNodeB().getName().equals("bullet")) {
        Elephant.elephantCollision(event, assetManager, bulletAppState.getPhysicsSpace());
        scoreManager.updateScore(ScoreManager.Points.ELEPHANT);
      }
    }   
    
    // check for collisions with the buggy
    if(event.getNodeA().getName().equals("Buggy") || event.getNodeB().getName().equals("Buggy")) {
      if(event.getNodeA().getName().equals("bullet") || event.getNodeB().getName().equals("bullet")) {
        Buggy.buggyCollision(event, assetManager, bulletAppState.getPhysicsSpace());
        scoreManager.updateScore(ScoreManager.Points.BUGGY);
      }
    }
  }
}

