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

		// System.out.println("Inside of main.");

    printWelcomeMessage();
   
    
    try {
      randomFlip();
      probabilityFlip();
      generateStats();

    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
		// System.out.println("Still inside of main.");

	}

  public static void printWelcomeMessage() {
    System.out.println("Welcome to the Coin Flipper Simulation.");
  }

	public static void randomFlip() throws Exception {
    
    java.io.File file = new java.io.File("random.txt");
    java.io.PrintWriter output = new java.io.PrintWriter(file);


    Strategy user = new StrategyRandom();
    Strategy coin = new StrategyRandom();

    int count_correct = 0;

    ArrayList<Integer> userGuesses = new ArrayList<Integer>();
    ArrayList<Integer> coinFlips = new ArrayList<Integer>();

    for(int i = 0; i < 100; i++) {
      user.saveMyMove(user.nextMove());
      coin.saveMyMove(coin.nextMove());

      userGuesses.add(user.myLastMove);
      coinFlips.add(coin.myLastMove);

      output.print(coin.myLastMove);

      // System.out.println("User selected: " + user.myLastMove);
      // System.out.println("Coin was: " + coin.myLastMove);

      if(user.myLastMove == coin.myLastMove) {
        count_correct++;
      }
    }

    

    // output.println(coinFlips);

    System.out.println("The number of correct guesses was: " + count_correct);

    output.println("\nThe number of correct guesses was: " + count_correct);

    output.close();

    // System.out.println(userGuesses.get(99) + " " + userGuesses.size());
    // System.out.println(coinFlips.get(99) + " " + coinFlips.size());


	}

	public static void probabilityFlip() throws Exception {
    Strategy user = new StrategyRandom();
    StrategyProbabilistic coin = new StrategyProbabilistic();


    // Open the data file from the Random trial and use it to seed the
    // Probabilistic coin
    java.io.File file = new java.io.File("random.txt");
    java.util.Scanner input = new java.util.Scanner(file);

    coin.setMoves(input.nextLine());
    input.close();


    file = new java.io.File("probabilistic.txt");
    java.io.PrintWriter output = new java.io.PrintWriter(file);


    int count_correct = 0;

    ArrayList<Integer> userGuesses = new ArrayList<Integer>();
    ArrayList<Integer> coinFlips = new ArrayList<Integer>();

    for(int i = 0; i < 100; i++) {
      user.saveMyMove(user.nextMove());
      coin.saveMyMove(coin.nextMove());

      userGuesses.add(user.myLastMove);
      coinFlips.add(coin.myLastMove);

      output.print(coin.myLastMove);

      // System.out.println("User selected: " + user.myLastMove);
      // System.out.println("Coin was: " + coin.myLastMove);

      if(user.myLastMove == coin.myLastMove) {
        count_correct++;
      }
    }
    
    System.out.println("The number of correct guesses was: " + count_correct);
    output.println("\nThe number of correct guesses was: " + count_correct);

    output.close();
    
	}

	public static void generateStats() {
	}

}
