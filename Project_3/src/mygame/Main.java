package mygame;

import gui.StartGUI;
import gui.EndGUI;
//import gui.PausedGUI;
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
import java.awt.event.ActionEvent;


import java.io.Console;
import java.util.Date;
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
  
  private Date date;
  private long endTime;
  
  private static BitmapText ballsShot, buggysHit, bouldersHit, cratesHit, elephantsHit, evilMonkeysHit, score, timeRemaining;
          
  public static void main(String[] args) {
    app = new Main();
    
    // Setup The Scores Manager
    scoreManager = new ScoreManager();
    scoreManager.initScoreManager();
    
    // Show the startup GUI
    StartGUI gui = new StartGUI(scoreManager);
   
    app.start();    
  }


  private void shutDown() {
    scoreManager.addPlayerScore();
    scoreManager.writeHighScores();
    app.stop();
    EndGUI gui = new EndGUI(scoreManager);
  }
  
  private ActionListener actionListener = new ActionListener() {
    public void onAction(String name, boolean isPressed, float tpf) {
      System.out.println("key" + name);
      if (name.equals("Game Pause Unpause") && !isPressed) {
        if(isRunning) {

          shutDown();
         }
      }
    }
  };
  
        
        

 
  

  @Override
  public void simpleInitApp() {

    // Set the endTime for game over
    date = new Date();
    endTime = date.getTime() + 300000;
    
     inputManager.addMapping("Game Pause Unpause", pause_trigger);
    inputManager.addListener(actionListener, new String[]{"Game Pause Unpause"});
    
    
    
    lineMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
   
    AmbientLight light = new AmbientLight();
    light.setColor(ColorRGBA.LightGray);
    rootNode.addLight(light);



    
    
    /** Set up Physics */
    bulletAppState = new BulletAppState();
    stateManager.attach(bulletAppState);
    
    
    //creat the charInputAppState 
   // charInputAppState = new CharacterInputAnimationAppState();
    
    
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
    mainPlayer = Player.createMainPlayer(stateManager, assetManager, inputManager, bulletAppState.getPhysicsSpace(), cam);
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
        
    // Init the HUD
    initHud();
  }
  
  
  private PhysicsSpace getPhysicsSpace() {
    return bulletAppState.getPhysicsSpace();
  }
  
    
  
  public void initHud() {
    
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
    
    ballsShot = new BitmapText(myFont, true);
    ballsShot.setName("ballsShot");
    ballsShot.setText("Balls Shot : 0");
    ballsShot.setColor(ColorRGBA.Red);
    ballsShot.setSize(guiFont.getCharSet().getRenderedSize());   
    ballsShot.setLocalTranslation(20, settings.getHeight() - 20, 0f);
    guiNode.attachChild(ballsShot);
    
    bouldersHit = new BitmapText(myFont, true);
    bouldersHit.setName("bouldersHit");
    bouldersHit.setText("Boulders Hit : 0");
    bouldersHit.setColor(ColorRGBA.Red);
    bouldersHit.setSize(guiFont.getCharSet().getRenderedSize());   
    bouldersHit.setLocalTranslation(20, settings.getHeight() - 40, 0f);
    guiNode.attachChild(bouldersHit);

    buggysHit = new BitmapText(myFont, true);
    buggysHit.setName("buggysHit");
    buggysHit.setText("Buggys Hit : 0");
    buggysHit.setColor(ColorRGBA.Red);
    buggysHit.setSize(guiFont.getCharSet().getRenderedSize());   
    buggysHit.setLocalTranslation(20, settings.getHeight() - 60, 0f);
    guiNode.attachChild(buggysHit);

    cratesHit = new BitmapText(myFont, true);
    cratesHit.setName("cratesHit");
    cratesHit.setText("Crates Hit : 0");
    cratesHit.setColor(ColorRGBA.Red);
    cratesHit.setSize(guiFont.getCharSet().getRenderedSize());   
    cratesHit.setLocalTranslation(20, settings.getHeight() - 80, 0f);
    guiNode.attachChild(cratesHit);

    elephantsHit = new BitmapText(myFont, true);
    elephantsHit.setName("elephantsHit");
    elephantsHit.setText("Elephants Hit : 0");
    elephantsHit.setColor(ColorRGBA.Red);
    elephantsHit.setSize(guiFont.getCharSet().getRenderedSize());   
    elephantsHit.setLocalTranslation(20, settings.getHeight() - 100, 0f);
    guiNode.attachChild(elephantsHit);

    evilMonkeysHit = new BitmapText(myFont, true);
    evilMonkeysHit.setName("evilMonkeysHit");
    evilMonkeysHit.setText("EvilMonkeys Hit : 0");
    evilMonkeysHit.setColor(ColorRGBA.Red);
    evilMonkeysHit.setSize(guiFont.getCharSet().getRenderedSize());   
    evilMonkeysHit.setLocalTranslation(20, settings.getHeight() - 120, 0f);
    guiNode.attachChild(evilMonkeysHit);

    score = new BitmapText(myFont, true);
    score.setName("score");
    score.setText("Player Score : 0");
    score.setColor(ColorRGBA.Blue);
    score.setSize(guiFont.getCharSet().getRenderedSize());   
    score.setLocalTranslation(20, settings.getHeight() - 140, 0f);
    guiNode.attachChild(score);


    timeRemaining = new BitmapText(myFont, true);
    timeRemaining.setName("timeRemaining");
    timeRemaining.setText("Time Remaining : 300");
    timeRemaining.setColor(ColorRGBA.Red);
    timeRemaining.setSize(guiFont.getCharSet().getRenderedSize());   
    timeRemaining.setLocalTranslation(settings.getWidth() / 2, settings.getHeight() - 20, 0f);
    guiNode.attachChild(timeRemaining);
  }
  
  
  
  
   
  @Override
  public void simpleUpdate(float tpf) {

    // Get the current time and see if five minutes have passed
    date = new Date();
    
    //If five minutes have passed it is time to end the game
    if(date.getTime() >= endTime) {
      shutDown();
    }


    // update the HUD
    ballsShot.setText("Balls Shot : " + scoreManager.getBallsShot());
    bouldersHit.setText("Boulders Hit : " + scoreManager.getBouldersHit());
    buggysHit.setText("Buggys Hit : " + scoreManager.getBuggysHit());
    cratesHit.setText("Crates Hit : " + scoreManager.getCratesHit());
    elephantsHit.setText("Elephants Hit : " + scoreManager.getElephantsHit());
    evilMonkeysHit.setText("EvilMonkeys Hit : " + scoreManager.getEvilMonkeysHit());
    score.setText("Player Score : " + scoreManager.getPlayerScore());
    timeRemaining.setText("Time Remaining : " + ((endTime - date.getTime()) / 1000));
    
    
    
    
    
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

