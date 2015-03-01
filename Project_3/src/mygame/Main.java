package mygame;

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
import characters.ChaseCamCharacter;
import com.jme3.app.FlyCamAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.input.ChaseCamera;
import com.jme3.light.AmbientLight;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Node;
import java.util.ArrayList;
import java.util.List;
import physics.PhysicsTestHelper;

/**
 * test
 * @author normenhansen
 */
public class Main extends SimpleApplication {
  
  private BulletAppState bulletAppState;
  private RigidBodyControl landscape;
  
  private AdvAnimationManagerControl animControl;
    
  
  private List<Geometry> targets = new ArrayList<Geometry>();
  private Node mainPlayer;
  
  private Vector3f normalGravity = new Vector3f(0, -9.81f, 0);

  public static void main(String[] args) {
    Main app = new Main();
    app.start();
  }

  @Override
  public void simpleInitApp() {
    
    /** Set up Physics */
    bulletAppState = new BulletAppState();
    stateManager.attach(bulletAppState);
    //bulletAppState.getPhysicsSpace().enableDebug(assetManager);
    
    stateManager.detach(stateManager.getState(FlyCamAppState.class));
    
    AmbientLight light = new AmbientLight();
        light.setColor(ColorRGBA.LightGray);
        rootNode.addLight(light);
    
    // Set the start position for he camera
    //cam.getLocation().set(0,60,0);
    //cam.update();
        
    // Add the Scene And create collision physics to match the terrain
    Spatial scene = assetManager.loadModel("/Scenes/P3_Scene3.j3o");
    
    CollisionShape sceneShape = 
                CollisionShapeFactory.createMeshShape(scene);
        landscape = new RigidBodyControl(sceneShape, 0);
        scene.addControl(landscape);
    
    scene.addControl(new RigidBodyControl(0));
    bulletAppState.getPhysicsSpace().add(scene);
     bulletAppState.getPhysicsSpace().add(landscape);
    
     //Attach the scene to the root
     rootNode.attachChild(scene);
     
      createPlayerCharacter();
      
       //Add the "bullets" to the scene to allow the player to shoot the balls
//        PhysicsTestHelper.createBallShooter(this,rootNode,bulletAppState.getPhysicsSpace(),
//            sinbadAppState, targets, guiNode, hitText);
//        
        PhysicsTestHelper.createBallShooter(this, rootNode, bulletAppState.getPhysicsSpace());
        PhysicsTestHelper.createMyPhysicsTestWorld(rootNode, assetManager, bulletAppState.getPhysicsSpace(), targets);

        
        
        //Add a custom font and text to the scene
        BitmapFont myFont = assetManager.loadFont("Interface/Fonts/DroidSansMono.fnt");
        
        
         // Create the crosshairs for the ball shooter
        BitmapText crosshairs = new BitmapText(myFont, true); 
        crosshairs.setText("X");
        crosshairs.setColor(ColorRGBA.Yellow);
        crosshairs.setSize(guiFont.getCharSet().getRenderedSize());
        crosshairs.setLocalTranslation(settings.getWidth() / 2,
            settings.getHeight() / 2 + crosshairs.getLineHeight(), 0f);
        guiNode.attachChild(crosshairs);                 
                
  }

  private PhysicsSpace getPhysicsSpace() {
        return bulletAppState.getPhysicsSpace();
    }
  
   private void createPlayerCharacter() {
        mainPlayer = (Node) assetManager.loadModel("Models/Jaime/Jaime.j3o");
        mainPlayer.setLocalTranslation(200, 10, 0f);
        ChaseCamCharacter charControl = new ChaseCamCharacter(0.5f, 2.5f, 8f);
        charControl.setGravity(normalGravity);
        charControl.setCamera(cam);
        
        ChaseCamera chaseCam = new ChaseCamera(cam, mainPlayer, inputManager);
        chaseCam.setDragToRotate(false);
        chaseCam.setSmoothMotion(true);
        chaseCam.setLookAtOffset(new Vector3f(0, 1.25f, 1));
        chaseCam.setDefaultDistance(1.5f);
        chaseCam.setMaxDistance(1.75f);
        chaseCam.setMinDistance(1.25f);
       
        chaseCam.setTrailingSensitivity(50);
        chaseCam.setChasingSensitivity(10);
        chaseCam.setRotationSpeed(10);
        //chaseCam.setDragToRotate(true);
        //chaseCam.setToggleRotationTrigger();

        mainPlayer.addControl(charControl);
        //mainPlayer.addControl(new RigidBodyControl(1));
                 
        bulletAppState.getPhysicsSpace().add(charControl);

        CharacterInputAnimationAppState appState = new CharacterInputAnimationAppState();
        appState.addActionListener(charControl);
        appState.addAnalogListener(charControl);
        appState.setChaseCamera(chaseCam);
        stateManager.attach(appState);
        rootNode.attachChild(mainPlayer);
        inputManager.setCursorVisible(false);
        
        animControl = new AdvAnimationManagerControl("animations/resources/animations-jaime.properties");
        mainPlayer.addControl(animControl);
        
        appState.addActionListener(animControl);
        appState.addAnalogListener(animControl);
    }
  
  
  @Override
  public void simpleUpdate(float tpf) {
    //TODO: add update code
  }

  @Override
  public void simpleRender(RenderManager rm) {
    //TODO: add render code
  }
}
