package mygame;


import appstate.InputAppState;
import physics.PhysicsTestHelper;
import characters.MyGameCharacterControl;


import com.jme3.app.SimpleApplication;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Spatial;
import com.jme3.bullet.BulletAppState;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.scene.Geometry;
import java.util.ArrayList;
import java.util.List;

/**
 * test
 * @author John M Lasheski
 */
public class Main extends SimpleApplication {
    
    protected BulletAppState bulletAppState;
    private RigidBodyControl landscape;
    
    private List<Geometry> targets = new ArrayList<Geometry>();
    
      
    private Vector3f normalGravity = new Vector3f(0, -9.81f, 0);

    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        // Set the start position for he camera
        cam.getLocation().set(-10,12,25);
        cam.update();
        
        // Add the Scene And create collision physics to match the terrain
        Spatial scene = assetManager.loadModel("/Scenes/HW_2_Scene.j3o");
                
        CollisionShape sceneShape = 
                CollisionShapeFactory.createMeshShape(scene);
        landscape = new RigidBodyControl(sceneShape, 0);
        scene.addControl(landscape);
        
        //Attach the scene to the root
        rootNode.attachChild(scene);
        
        // Allows for the use of Physics simulation
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        
        // Add the scene to the PhysicsSpace
        bulletAppState.getPhysicsSpace().add(landscape);
        
        //Create the Physics World based on the Helper class
        PhysicsTestHelper.createMyPhysicsTestWorld(rootNode, assetManager, 
                bulletAppState.getPhysicsSpace(), targets);
        
        // Add the Player to the world and use the customer character 
        // and input control classes
        Node sinbad = (Node)assetManager.loadModel("/Models/Sinbad/Sinbad.mesh.xml");
        Node jaime = (Node)assetManager.loadModel("/Models/Jaime/Jaime.j3o");

        // Set the starting postion and scale for the characters
        sinbad.setLocalTranslation(0, 12, 0);
        jaime.setLocalTranslation(-12, 6, 5);
        jaime.setLocalScale(4, 4, 4);

        
        // Add the controls for the characters        
        MyGameCharacterControl sinbadControl = new MyGameCharacterControl(0.5f,2.5f,8f);
        MyGameCharacterControl jaimeControl = new MyGameCharacterControl(0.5f,2.5f,8f);
        sinbadControl.setCamera(cam);
        jaimeControl.setCamera(cam);
        sinbad.addControl(sinbadControl);
        jaime.addControl(jaimeControl);

        // Set the physics for the characters
        sinbadControl.setGravity(normalGravity);
        jaimeControl.setGravity(normalGravity);
        bulletAppState.getPhysicsSpace().add(sinbadControl);
        bulletAppState.getPhysicsSpace().add(jaimeControl);
        
       
        // Add the input listeners for the characters       
        InputAppState sinbadAppState = new InputAppState();
        sinbadAppState.setCharacter(sinbadControl);
        stateManager.attach(sinbadAppState);
        InputAppState jaimeAppState = new InputAppState();
        jaimeAppState.setCharacter(jaimeControl);
        stateManager.attach(jaimeAppState);
        
        // Attach the characters to the root node        
        rootNode.attachChild(sinbad);
        rootNode.attachChild(jaime);
                
         
        //Add a custom font and text to the scene
        BitmapFont myFont = assetManager.loadFont("Interface/Fonts/DroidSansMono.fnt");
        

        // Create the Text that will be used for displaying hits
        BitmapText hitText = new BitmapText(myFont, true);
        hitText.setName("hitText");
        hitText.setText("HIT !!!");
        hitText.setColor(ColorRGBA.Red);
        hitText.setSize(guiFont.getCharSet().getRenderedSize());   
        hitText.setLocalTranslation(settings.getWidth() / 2 - hitText.getLineWidth() / 2,
            settings.getHeight() - 40, 0f);


        // Create the crosshairs for the ball shooter
        BitmapText crosshairs = new BitmapText(myFont, true); 
        crosshairs.setText("X");
        crosshairs.setColor(ColorRGBA.Yellow);
        crosshairs.setSize(guiFont.getCharSet().getRenderedSize());
        crosshairs.setLocalTranslation(settings.getWidth() / 2,
            settings.getHeight() / 2 + crosshairs.getLineHeight(), 0f);
        guiNode.attachChild(crosshairs);                 
                
        

        // Create the title text for the hud
        BitmapText hudText = new BitmapText(myFont, true);
        hudText.setText("CMSC325 Project 1");
        hudText.setColor(ColorRGBA.Blue);
        hudText.setSize(guiFont.getCharSet().getRenderedSize());
        hudText.setLocalTranslation(settings.getWidth() / 2 - hudText.getLineWidth() / 2 ,
            settings.getHeight() - 10, 0f);
        guiNode.attachChild(hudText);
        

        //Add the "bullets" to the scene to allow the player to shoot the balls
        PhysicsTestHelper.createBallShooter(this,rootNode,bulletAppState.getPhysicsSpace(),
            sinbadAppState, targets, guiNode, hitText);
    }


    @Override
    public void simpleUpdate(float tpf) {
    }


    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
}
