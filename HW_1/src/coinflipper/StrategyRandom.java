/*
@AUTHOR: Associate Professor (Adjunct) Mark A. Wireman
@COURSE: CMSC325, Intro to Game Development, UMUC
@CREDITTO: Michael C. Semeniuk

Further modified by John M. Lashedki to be used in conjuction with the 
CoinFlipper class used in a simple simulation for HW_1.
 */

package coinflipper;

public class StrategyRandom extends Strategy {

  /**
   * Encoding for a strategy.
   */

  // 0 = defect, 1 = cooperate

  public StrategyRandom() {
    name = "Random";
  } /* StrategyRandom */

  public int nextMove() {
    if (Math.random() < 0.5)
      return 1;

    return 0;
  } /* nextMove */
} /* class StrategyRandom */
