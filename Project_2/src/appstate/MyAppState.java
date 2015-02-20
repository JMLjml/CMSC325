/*
 * A simple appState so that I can add some extra cleanup items.
 * Specifically needed to close the output file in the event of a non graceful
 * exit. 
 */
package appstate;

import com.jme3.app.state.AbstractAppState;
import mygame.Main;

/**
 *
 * @author john
 */
public class MyAppState extends AbstractAppState {
    
  @Override
  public void cleanup() {
    super.cleanup();
    Main.output.close();
  }
}
