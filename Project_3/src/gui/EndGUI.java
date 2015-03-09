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
public class EndGUI extends JFrame {
  private JTextArea textArea;
  private ScoreManager scoreManager;
  
  public EndGUI(ScoreManager scoreManager) {
    
    this.scoreManager = scoreManager;
    initUI();
  }
  
  private void initUI() {
    String highScores = " High Scores \n\n";
    
    highScores += scoreManager.getHighScores();
    
    highScores += "\n\n Your Score was: ";
    highScores += scoreManager.playerScore.getPlayerScore();
    textArea = new JTextArea(highScores);

    this.add(textArea);
    setLayout(new FlowLayout());
    pack();
    setTitle("Scores");
    setSize(175, 325);
    setLocationRelativeTo(null);
    setDefaultCloseOperation(EXIT_ON_CLOSE);
        
    this.setLocation(500,200);
    this.setVisible(true);
    this.setAlwaysOnTop(true);
  }  
}
