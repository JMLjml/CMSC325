package scores;

import java.io.Serializable;

/**
 *
 * @author John M. Lasheski
 */

public class Scores {  
  protected int playerScore = 0;
  protected String playerName;
  
  public Scores(String playerName, int playerScore) {
    this.playerName = playerName;
    this.playerScore = playerScore;
  }
  
  public int getPlayerScore() {
    return playerScore;
  }

  public String getPlayerName() {
    return playerName;
  }
}

