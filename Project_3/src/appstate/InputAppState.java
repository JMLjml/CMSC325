/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package appstate;

import characters.MyGameCharacterControl;
import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.collision.CollisionResults;
import com.jme3.input.InputManager;
import com.jme3.input.Joystick;
import com.jme3.input.JoystickAxis;
import com.jme3.input.JoystickButton;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.Ray;
import com.jme3.scene.Geometry;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Prof Wireman
 */
public class InputAppState extends AbstractAppState implements AnalogListener, ActionListener {
    
     private Application app;
    private InputManager inputManager;
    private MyGameCharacterControl character; //The Custom Character Control
    private float sensitivity = 5000;

    List<Geometry> targets = new ArrayList<Geometry>();
    
    public enum InputMapping{
        
        LeanLeft,
        LeanRight,
        LeanFree,
        RotateLeft,
        RotateRight,
        LookUp,
        LookDown,
        StrafeLeft,
        StrafeRight,
        MoveForward,
        MoveBackward,
        Fire;
    }
    
    private String[] mappingNames = new String[]{InputAppState.InputMapping.LeanFree.name(), InputAppState.InputMapping.LeanLeft.name(), InputAppState.InputMapping.LeanRight.name(), InputAppState.InputMapping.RotateLeft.name(), InputAppState.InputMapping.RotateRight.name(), InputAppState.InputMapping.LookUp.name(), InputAppState.InputMapping.LookDown.name(), InputAppState.InputMapping.StrafeLeft.name(), InputAppState.InputMapping.StrafeRight.name(), InputAppState.InputMapping.MoveForward.name(), InputAppState.InputMapping.MoveBackward.name()};
    
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.app = app;
        this.inputManager = app.getInputManager();
        addInputMappings();
        assignJoysticks();
    }
    
    private void addInputMappings(){
        inputManager.addMapping(InputAppState.InputMapping.LeanFree.name(), new KeyTrigger(KeyInput.KEY_V));
        inputManager.addMapping(InputAppState.InputMapping.LeanLeft.name(), new KeyTrigger(KeyInput.KEY_Q));
        inputManager.addMapping(InputAppState.InputMapping.LeanRight.name(), new KeyTrigger(KeyInput.KEY_E));
        inputManager.addMapping(InputAppState.InputMapping.RotateLeft.name(), new MouseAxisTrigger(MouseInput.AXIS_X, true));
        inputManager.addMapping(InputAppState.InputMapping.RotateRight.name(), new MouseAxisTrigger(MouseInput.AXIS_X, false));
        inputManager.addMapping(InputAppState.InputMapping.LookUp.name(), new MouseAxisTrigger(MouseInput.AXIS_Y, false));
        inputManager.addMapping(InputAppState.InputMapping.LookDown.name(), new MouseAxisTrigger(MouseInput.AXIS_Y, true));
        inputManager.addMapping(InputAppState.InputMapping.StrafeLeft.name(), new KeyTrigger(KeyInput.KEY_H), new KeyTrigger(KeyInput.KEY_LEFT));
        inputManager.addMapping(InputAppState.InputMapping.StrafeRight.name(), new KeyTrigger(KeyInput.KEY_K), new KeyTrigger(KeyInput.KEY_RIGHT));
        inputManager.addMapping(InputAppState.InputMapping.MoveForward.name(), new KeyTrigger(KeyInput.KEY_U), new KeyTrigger(KeyInput.KEY_UP));
        inputManager.addMapping(InputAppState.InputMapping.MoveBackward.name(), new KeyTrigger(KeyInput.KEY_J), new KeyTrigger(KeyInput.KEY_DOWN));
        inputManager.addListener(this, mappingNames);
        
        inputManager.addMapping(InputAppState.InputMapping.Fire.name(), new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
    }
    /**
    * 
    */
    private void assignJoysticks(){
        Joystick[] joysticks = inputManager.getJoysticks();
        if (joysticks != null){
            for( Joystick j : joysticks ) {
                for(JoystickAxis axis : j.getAxes()){
                    if(axis == j.getXAxis()){
                        axis.assignAxis(InputAppState.InputMapping.StrafeRight.name(), InputAppState.InputMapping.StrafeLeft.name());
                    } else if ( axis == j.getYAxis()){
                        axis.assignAxis(InputAppState.InputMapping.MoveBackward.name(), InputAppState.InputMapping.MoveForward.name());
                    } else if (axis.getLogicalId().equals("ry")){
                        axis.assignAxis(InputAppState.InputMapping.LookDown.name(), InputAppState.InputMapping.LookUp.name());
                    } else if(axis.getLogicalId().equals("rx")){
                        axis.assignAxis(InputAppState.InputMapping.RotateRight.name(), InputAppState.InputMapping.RotateLeft.name());
                    }
                }
                
                for(JoystickButton button : j.getButtons()){
                    button.assignButton("Fire");
                }
            }

            
        }
    }
    /**
    * 
    */
    @Override
    public void cleanup() {
        super.cleanup();
        for (InputAppState.InputMapping i : InputAppState.InputMapping.values()) {
            if (inputManager.hasMapping(i.name())) {
                inputManager.deleteMapping(i.name());
            }
        }
        inputManager.removeListener(this);
    }
    
    public void onAnalog(String action, float value, float tpf) {
        if(character != null){
            character.onAnalog(action, value * sensitivity, tpf);
        }
    }

    public void onAction(String name, boolean isPressed, float tpf) {
        if(character != null){
            
           if (name.equals("Fire")) {
               if (isPressed && character.getCooldown() == 0f){
                   fire();
               }
           } else {
           
               character.onAction(name, isPressed, tpf);
           }
        }
    }
    
    public void setCharacter(MyGameCharacterControl character){
        this.character = character;
    }
    
    public void setTargets(List<Geometry> targets){
        this.targets = targets;
    }
    
    public void fire(){
        if(character != null){
            Ray r = new Ray(app.getCamera().getLocation(), app.getCamera().getDirection());
            
            CollisionResults collRes = new CollisionResults();
            for(Geometry g: targets){
                g.collideWith(r, collRes);
            }
            if(collRes.size() > 0){
                System.out.println("hit");
            }
            character.onFire();
        }
    }
    
}
