package coinflipper;

public class CoinFlipper {
  
  static int userMove;  // for storing the user's selection of heads or tails

	public static void main(String[] args) {
    printWelcomeMessage();

    // Get the user's move and store it
    try {
      getUserMove();
    } catch (Exception e) {
      System.out.println("Invalid input. Program exiting.");
      java.lang.System.exit(-1);
    }
        
    // Run the simulation, and calculate the results    
    try {
      randomFlip();
      probabilityFlip();

    } catch (Exception e) {
      System.out.println("File read or write error. Program exiting.");
      e.printStackTrace();
    }
	}



  public static void printWelcomeMessage() {
    String welcome = "\n*** Welcome to the Coin Flipper Simulation ***\n\n"
      + "This simulation allows the user to select either heads"
      + "\nor tails. The simulation then flips a coin 100 times using a random"
      + "\nprobability and then again using a probabilistic probability."
      + "\nThe results of the coin flips are stored in the files random.txt,"
      + "\nand probabilistic.txt. The number of correct matches is also output"
      + "\nto the terminal.\n\n";

    System.out.println(welcome);
  }



  public static void getUserMove() throws Exception {

    System.out.println("Select heads or tails for simualtion.");
    System.out.println("enter a 0 for heads, or a 1 for tails.");

    java.util.Scanner in = new java.util.Scanner(System.in);
    userMove = in.nextInt();
    in.close();

    // Valid moves are 1 or 0, exit otherwise
    if(!(userMove == 0 || userMove == 1)) {
      java.lang.System.exit(-1);
    }

    if(userMove == 0) {
      System.out.println("User selected heads.\n");
    } else {
      System.out.println("User selected tails.\n");
    }
  }

	

  public static void randomFlip() throws Exception {
    
    java.io.File file = new java.io.File("random.txt");
    java.io.PrintWriter output = new java.io.PrintWriter(file);

    Strategy coin = new StrategyRandom();

    int count_correct = 0;
    int count_heads = 0;


    for(int i = 0; i < 100; i++) {
      coin.saveMyMove(coin.nextMove());

      output.print(coin.myLastMove);

      if(userMove == coin.myLastMove) {
        count_correct++;
      }

      if(coin.myLastMove == 0) {
        count_heads++;
      }
    }

    double percent = (double)count_correct;

    System.out.println("Results of StrategyRandom Coin Flips");
    System.out.println("The number of heads was: " + count_heads);
    System.out.println("The number of correct guesses was: " + count_correct);
    System.out.println("The percent of correct guesses was: " + percent + " %\n");


    if(userMove == 0) {
      output.println("\nUser selected heads.");
    } else {
      output.println("\nUser selected tails.");
    }
    output.println("The number of heads was: " + count_heads);
    output.println("The number of correct guesses was: " + count_correct);
    output.println("The percent of correct guesses was: " + percent + " %");

    output.close();
	}



	public static void probabilityFlip() throws Exception {

    // Open the data file from the Random trial and use it to seed the
    // Probabilistic coin
    java.io.File file = new java.io.File("random.txt");
    java.util.Scanner input = new java.util.Scanner(file);

    StrategyProbabilistic coin = new StrategyProbabilistic();

    coin.setMoves(input.nextLine());

    input.close();

    file = new java.io.File("probabilistic.txt");
    java.io.PrintWriter output = new java.io.PrintWriter(file);

    int count_correct = 0;
    int count_heads = 0;

    for(int i = 0; i < 100; i++) {
      coin.saveMyMove(coin.nextMove());

      output.print(coin.myLastMove);

      if(userMove == coin.myLastMove) {
        count_correct++;
      }

      if(coin.myLastMove == 0) {
        count_heads++;
      }
    }
    
    double percent = (double)count_correct;

    System.out.println("Results of StrategyProbabilistic Coin Flips");
    System.out.println("The number of heads was: " + count_heads);
    System.out.println("The number of correct guesses was: " + count_correct);
    System.out.println("The percent of correct guesses was: " + percent + " %\n");

    if(userMove == 0) {
      output.println("\nUser selected heads.");
    } else {
      output.println("\nUser selected tails.");
    }
    output.println("The number of heads was: " + count_heads);
    output.println("The number of correct guesses was: " + count_correct);
    output.println("The percent of correct guesses was: " + percent + " %");

    output.close();    
	}
}
