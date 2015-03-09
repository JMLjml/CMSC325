package scores;

import java.util.Comparator;

/**
 *
 * @author John M. Lasheski
 */
public class ScoresComparator implements Comparator<Scores> {
  
  public int compare(Scores score1, Scores score2) {
    
    if (score1.getPlayerScore() > score2.getPlayerScore()) {
      return -1;
    }
    
    else if (score1.getPlayerScore() < score2.getPlayerScore()) {
      return 1;
    }
    
    else {
      return 0;
    }
  }
}
