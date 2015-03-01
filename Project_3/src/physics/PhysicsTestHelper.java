
package physics;

import appstate.InputAppState;
import com.jme3.app.Application;
import com.jme3.asset.AssetManager;
import com.jme3.asset.TextureKey;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.MeshCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.font.BitmapText;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Matrix3f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Dome;
import com.jme3.scene.shape.Sphere;
import com.jme3.scene.shape.Sphere.TextureMode;
import com.jme3.texture.Texture;
import java.util.HashSet;
import java.util.List;

/**
 *
 * @author normenhansen
 * @author further modified by John M. Lasheski for CMSC325 Spring 2015
 */
public class PhysicsTestHelper {    

    public static void createMyPhysicsTestWorld(Node rootNode, AssetManager assetManager,
        PhysicsSpace space, List<Geometry> targets)  {        
        
        AmbientLight light = new AmbientLight();
        light.setColor(ColorRGBA.LightGray);
        rootNode.addLight(light);

        Material material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        material.setTexture("ColorMap", assetManager.loadTexture("Blender/2.4x/WoodCrate_lighter.png"));
       


        // create the movable boxes and assign them as targets
        for (int i = 0; i < 25; i++) {
            Box box = new Box(5, 5, 5);
            Geometry boxGeometry = new Geometry("box", box);
            boxGeometry.setMaterial(material);
            boxGeometry.setLocalTranslation(-.005f * i, 12 * i,-40 );
            //RigidBodyControl automatically uses sphere collision shapes when attached to single geometry with pqt mesh
            boxGeometry.addControl(new RigidBodyControl(.001f));
            boxGeometry.getControl(RigidBodyControl.class).setRestitution(1f);
            rootNode.attachChild(boxGeometry);
            space.add(boxGeometry);
            
            targets.add(boxGeometry);
        }
        
       Material material_2 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
       material_2.setTexture("ColorMap", assetManager.loadTexture("Blender/2.4x/textures/WarningStrip.png"));

       
        // create the movable spheres and assign them as targets
        for (int i = 0; i < 5; i++) {
            Sphere sphere = new Sphere(22, 22, 6, true, false);
            Geometry sphereGeometry = new Geometry("sphere", sphere);
            sphereGeometry.setMaterial(material_2);
            sphereGeometry.setLocalTranslation(210 - (15 * i), 45, 40 );
            //RigidBodyControl automatically uses sphere collision shapes when attached to single geometry with pqt mesh
            sphereGeometry.addControl(new RigidBodyControl(.001f));
            sphereGeometry.getControl(RigidBodyControl.class).setRestitution(.50f);
//            sphereGeometry.setLinearVelocity(new Vector3f(i, 5, 5));
            RigidBodyControl sphereControl = new RigidBodyControl(10);
                    sphereGeometry.addControl(sphereControl);
                    sphereControl.setLinearVelocity(new Vector3f(5 * i, 50, 50));
                    sphereGeometry.addControl(sphereControl);
            rootNode.attachChild(sphereGeometry);
            space.add(sphereGeometry);
            
            targets.add(sphereGeometry);
        }
        
        
        
        

//        // create immovable Pyramids with mesh collision shape, these are not targets
//        Material pyramidMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
//        pyramidMaterial.setTexture("ColorMap", assetManager.loadTexture("Textures/Terrain/splat/fortress512.png"));
//
//        for(int i = 0; i < 5; i++) {
//            Dome pyramid = new Dome(2,4, 2 * i + 3);
//            Geometry pyramidGeometry = new Geometry("pyramid", pyramid);
//
//            pyramidGeometry.setMaterial(pyramidMaterial);
//            pyramidGeometry.setLocalTranslation((15 * i) -25, 8, 2 * i -25);
//
//            float[] angles = {0, i * 12, 0};
//            pyramidGeometry.setLocalRotation(new Quaternion(angles));
//
//            pyramidGeometry.addControl(new RigidBodyControl(new MeshCollisionShape(pyramid), 0));
//            rootNode.attachChild(pyramidGeometry);
//            space.add(pyramidGeometry);
//        }        
    }



    /**
     * creates the necessary inputlistener and action to shoot balls from teh camera
     * @param app
     * @param rootNode
     * @param space
     */
//    public static void createBallShooter(final Application app, final Node rootNode,
//        final PhysicsSpace space, final InputAppState character, final List<Geometry> targets,
//        final Node guiNode, final BitmapText hitText) {
//        
//        ActionListener actionListener = new ActionListener() {
//
//            public void onAction(String name, boolean keyPressed, float tpf) {
//                Sphere bullet = new Sphere(22, 22, .3f, true, false);
//                bullet.setTextureMode(TextureMode.Projected);
//                Material mat2 = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
//                TextureKey key2 = new TextureKey("Blender/2.4x/textures/Grass_256.png");
//                key2.setGenerateMips(true);
//                Texture tex2 = app.getAssetManager().loadTexture(key2);
//                mat2.setTexture("ColorMap", tex2);
//                if (name.equals("shoot") && !keyPressed) {
//                    Geometry bulletg = new Geometry("bullet", bullet);
//                    bulletg.setMaterial(mat2);
//                    bulletg.setShadowMode(ShadowMode.CastAndReceive);
//                    bulletg.setLocalTranslation(app.getCamera().getLocation());
//                    RigidBodyControl bulletControl = new RigidBodyControl(10);
//                    bulletg.addControl(bulletControl);
//                    bulletControl.setLinearVelocity(app.getCamera().getDirection().mult(150));
//                    bulletg.addControl(bulletControl);
//                    rootNode.attachChild(bulletg);
//                    space.add(bulletControl);
//                    
//                    
//                   
//                    // Delete the hit message now that we have shot again
//                    guiNode.detachChildNamed("hitText");
//                    
//                    // Have the character InputAppState check for hits
//                  //  character.fire(targets, guiNode, hitText);
//                }
//            }        
//            
//        };
//        app.getInputManager().addMapping("shoot", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
//        app.getInputManager().addListener(actionListener, "shoot");
    //}
    
     public static void createBallShooter(final Application app, final Node rootNode,
        final PhysicsSpace space) {
        
        ActionListener actionListener = new ActionListener() {

            public void onAction(String name, boolean keyPressed, float tpf) {
                Sphere bullet = new Sphere(22, 22, .20f, true, false);
                bullet.setTextureMode(TextureMode.Projected);
                Material mat2 = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
                TextureKey key2 = new TextureKey("Blender/2.4x/textures/Grass_256.png");
                key2.setGenerateMips(true);
                Texture tex2 = app.getAssetManager().loadTexture(key2);
                mat2.setTexture("ColorMap", tex2);
                if (name.equals("shoot") && !keyPressed) {
                    Geometry bulletg = new Geometry("bullet", bullet);
                    bulletg.setMaterial(mat2);
                    bulletg.setShadowMode(ShadowMode.CastAndReceive);
                    bulletg.setLocalTranslation(app.getCamera().getLocation());
                    RigidBodyControl bulletControl = new RigidBodyControl(1);
                    bulletg.addControl(bulletControl);
                    bulletControl.setLinearVelocity(app.getCamera().getDirection().mult(50));
                    bulletg.addControl(bulletControl);
                    bulletControl.setRestitution(100);
                  
                    rootNode.attachChild(bulletg);
                    space.add(bulletControl);
                    
                    
                   
                    // Delete the hit message now that we have shot again
                    //guiNode.detachChildNamed("hitText");
                    
                    // Have the character InputAppState check for hits
                  //  character.fire(targets, guiNode, hitText);
                }
            }        
            
        };
        app.getInputManager().addMapping("shoot", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        app.getInputManager().addListener(actionListener, "shoot");
     }
}
