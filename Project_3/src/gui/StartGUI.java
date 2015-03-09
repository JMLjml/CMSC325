package gui;

import scores.ScoreManager;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import static javax.swing.JFrame.EXIT_ON_CLOSE;
import javax.swing.*;


/**
 *
 * @author John M. Lasheski
 */
public class StartGUI extends JFrame {
  
  private JTextField textField;
  private JTextArea textArea;
  
  private String playerName;
  
  private ScoreManager scoreManager;
  
  
  private String instrux = " Welcome to Monkey Shooter! \n\n Controls are simple, WASD or arrows to move, space"
    + " to jump, and left-mouse to fire balls at targets. \n Press backspace to exit the game. \n \n"
    + " Points are awared for hits, and each ball shot costs one point, so don't miss.\n\n"
    + " There are five types of targets. Some move from waypoint to waypoint, and another will "
    + "follow you if you get too close. All targets respawn after they have been completely destroyed."
    + "\n\n Enter your name above to be included in the high scores roster and PRESS ENTER.\n\n"
    + " ENJOY!";
  

  public StartGUI(ScoreManager scoreManager) {
    initUI();
    this.scoreManager = scoreManager;
  }
    
  public void getPlayerName() {
    this.setVisible(false);
    this.dispose();
    
    scoreManager.initPlayerScore(playerName);
    
    System.out.println(playerName);
  }
  
  private void initUI() {
    
    textField = new JTextField(20);
    textField.addActionListener(new ActionListener() {
        @Override    
        public void actionPerformed(ActionEvent event) {
          playerName = textField.getText();
          getPlayerName();
        }
      });
    
    textArea = new JTextArea(instrux);

    this.add(textField);
    this.add(textArea);
      
    setLayout(new FlowLayout());
    pack();
    setTitle("Monkey Shooter Start Menu");
    setSize(1200, 225);
    setLocationRelativeTo(null);
    setDefaultCloseOperation(EXIT_ON_CLOSE);
        
    this.setLocation(0,0);
    this.setVisible(true);
    this.setAlwaysOnTop(true);
  }
}
