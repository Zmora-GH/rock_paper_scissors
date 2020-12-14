import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.lang.Math;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

class Game {
	public static void main(String args[]) throws NoSuchAlgorithmException, InvalidKeyException {
		Set<String> argsSet = new HashSet<String>(Arrays.asList(args));
		if (args.length % 2 == 0 || args.length == 1 || args.length != argsSet.size()) {
			printErrorMessage();
			System.exit(0);
		}
		SecureRandom sr = new SecureRandom();
		byte key[] = new byte[16];
		sr.nextBytes(key);
		int cpuMoveIndex = (int) (Math.random() * args.length); 
		System.out.println(String.format("HMAC: %x", getHMAC(key, args[cpuMoveIndex])));
		while (true) {
			try {
				printGameMenu(args);
				Scanner scan = new Scanner(System.in);
				int input = scan.nextInt();
				if (input == 0) {
					System.exit(0);
				} else  if (input > args.length || input < 0) {
					continue;
				} else {
					System.out.println ("Your move: " + args[input-1]);
					System.out.println ("Cpu move: " + args[cpuMoveIndex]);
					System.out.println(getWinner(cpuMoveIndex, input-1, args.length));
					System.out.println(String.format("KEY: %x", new BigInteger(1, key)));
					break;
				}
			} catch (java.util.InputMismatchException e) {
				continue;
			}	
		}
	}

	public static BigInteger getHMAC (byte key[], String message) throws NoSuchAlgorithmException, InvalidKeyException {
		Mac mac = Mac.getInstance("HmacSHA256");
		SecretKeySpec skey = new SecretKeySpec(key, "HmacSHA256");
		mac.init(skey);
		byte[] hmac = mac.doFinal(message.getBytes());
		BigInteger hexHMAC = new BigInteger(1, hmac);
		return hexHMAC;
	}

	public static String getWinner(int cpuMove, int playerMove, int moveMax) {
		int half = (moveMax - 1) / 2;
		int temp = playerMove - cpuMove;
		if ((temp < 0 && Math.abs(temp) > half) || (temp > 0 && Math.abs(temp) <= half)) {
			return "You win!";
		} else if (temp == 0) {
			return "DRAW!";
		}
		return "Cpu win!";
	}

	public static void printErrorMessage() {
		System.out.println("Error: invalid parameters\n" +
		"--|  Game rules:  |-----------------------------------\n" +
		"1. Odd number of moves\n" +
		"2. Minimum of 3 moves\n" +
		"3. Unique moves\n" +
		"Examples:\n" +
		"...>java -jar game.jar rock paper scissors lizard Spock\n" +
		"...>java -jar game.jar rock scissors paper\n" +
		"-----------------------------------------------------");
	}

	public static void printGameMenu(String items[]) {
		System.out.println("Game menu:");
		int index = 1;
		for (String arg : items) {
			System.out.println("[" + index + "] - " + arg);
			index ++;
		}
		System.out.print("[0] - exit\nEnter your move: ");
	}
}