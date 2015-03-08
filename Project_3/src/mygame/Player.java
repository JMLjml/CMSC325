package mygame;

import animations.AdvAnimationManagerControl;
import animations.CharacterInputAnimationAppState;
import characters.ChaseCamCharacter;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.input.ChaseCamera;
import com.jme3.input.InputManager;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;

/**
 *
 * @author John M. Lasheski
 */
public class Player {
  protected static Node mainPlayer;
  private static AdvAnimationManagerControl animControl;
  private static Vector3f normalGravity = new Vector3f(0, -9.81f, 0);


  public static Node createMainPlayer(AppStateManager stateManager, AssetManager assetManager, InputManager inputManager, PhysicsSpace space, Camera cam) {

    mainPlayer = (Node) assetManager.loadModel("Models/Jaime/Jaime.j3o");
    mainPlayer.setLocalTranslation(200, 10, 0f);

    ChaseCamCharacter charControl = new ChaseCamCharacter(0.5f, 2.5f, 8f);
    charControl.setGravity(normalGravity);
    charControl.setCamera(cam);
        
    ChaseCamera chaseCam = new ChaseCamera(cam, mainPlayer, inputManager);
    chaseCam.setDragToRotate(false);
    chaseCam.setSmoothMotion(true);
    chaseCam.setLookAtOffset(new Vector3f(0f, 2.7f, -0.1f));
    chaseCam.setDefaultDistance(1.75f);
    chaseCam.setMaxDistance(2f);
    chaseCam.setMinDistance(1.5f);       
    chaseCam.setTrailingSensitivity(50);
    chaseCam.setChasingSensitivity(10);
    chaseCam.setRotationSpeed(10);        

    mainPlayer.addControl(charControl);
                  
    space.add(charControl);

    CharacterInputAnimationAppState appState = new CharacterInputAnimationAppState();
    appState.addActionListener(charControl);
    appState.addAnalogListener(charControl);
    appState.setChaseCamera(chaseCam);
    stateManager.attach(appState);
    
    inputManager.setCursorVisible(false);
        
    animControl = new AdvAnimationManagerControl("animations/resources/animations-jaime.properties");
    mainPlayer.addControl(animControl);
    
    appState.addActionListener(animControl);
    appState.addAnalogListener(animControl);
    
    return mainPlayer;
  }  
}

