/**
 * 
 */
package coinflipper;

import java.util.ArrayList;

/**
 * @author john
 *
 */
public class CoinFlipper {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		System.out.println("Inside of main.");

    randomFlip();
    probabilityFlip();
    generateStats();

		System.out.println("Still inside of main.");

	} // End of main

	public static void randomFlip() {
    Strategy user = new Strategy();
    Strategy coin = new StrategyRandom();

    int count_correct = 0;

    ArrayList<Integer> userGuesses = new ArrayList();
    ArrayList<Integer> coinFlips = new ArrayList();

    for(int i = 0; i < 100; i++) {
      user.saveMyMove(user.nextMove());
      coin.saveMyMove(coin.nextMove());

      userGuesses.add(user.myLastMove);
      coinFlips.add(coin.myLastMove);

      System.out.println("User selected: " + user.myLastMove);
      System.out.println("Coin was: " + coin.myLastMove);

      if(user.myLastMove == coin.myLastMove) {
        count_correct++;
      }
    }

    System.out.println("The number of correct guesses was: " + count_correct);
    System.out.println(userGuesses.get(99) + " " + userGuesses.size());
    System.out.println(coinFlips.get(99) + " " + coinFlips.size());


	}

	public static void probabilityFlip() {
	}

	public static void generateStats() {
	}

}
