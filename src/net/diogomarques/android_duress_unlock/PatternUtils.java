package net.diogomarques.android_duress_unlock;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.diogomarques.com.android.internal.widget.LockPatternView.Cell;

/**
 * Utility class with methods to create valid unlock patterns and convert them
 * to and from a string representation (as a sequence of numbers corresponding
 * to the cells).
 * 
 * @author Diogo Marques <diogohomemmarques@gmail.com>
 * 
 */
public class PatternUtils {

	// possible next targets from each position
	private static int[] from1 = { 2, 4, 5 };
	private static int[] from2 = { 1, 3, 5, 4, 6 };
	private static int[] from3 = { 2, 6, 5 };
	private static int[] from4 = { 1, 5, 7, 2, 8 };
	private static int[] from5 = { 1, 2, 3, 4, 6, 7, 8, 9 };
	private static int[] from6 = { 3, 5, 9, 2, 8 };
	private static int[] from7 = { 4, 8, 5 };
	private static int[] from8 = { 7, 5, 9, 5, 6 };
	private static int[] from9 = { 8, 6, 5 };

	private static List<int[]> positions;

	static {
		positions = new ArrayList<int[]>();
		positions.add(from1);
		positions.add(from2);
		positions.add(from3);
		positions.add(from4);
		positions.add(from5);
		positions.add(from6);
		positions.add(from7);
		positions.add(from8);
		positions.add(from9);
	}

	/**
	 * Generates a pattern, composed by a sequence of 5 contiguous points (all 3
	 * directions). A point appears a max of 1 time in the pattern.
	 * 
	 * The pattern is represented by a string which holds a sequence of numbers
	 * from 1 to 9.
	 * 
	 * @return The string representation of an unlock pattern
	 */
	public static String generate5PointPattern() {
		// Since a random sequence might stall before reaching 5 (e.g.
		// 4-5-8-7-stall), generate sequences until one is 5-digit long.
		String sequence = "";
		while (sequence.length() < 5)
			sequence = generateCandidateUpto5Digits();
		return sequence;
	}

	private static String generateCandidateUpto5Digits() {
		StringBuilder sb = new StringBuilder();
		int curPosition = new Random().nextInt(9) + 1;
		sb.append(curPosition);
		for (int i = 0; i < 4; i++) {
			int[] nextOptions = positions.get(curPosition - 1);
			curPosition = getRandomNextPosition(nextOptions);
			if (!sb.toString().contains(String.valueOf(curPosition)))
				sb.append(curPosition);
			else {
				@SuppressWarnings("unused")
				int attempt = 1;
				int maxAttempts = 10;
				while (sb.toString().contains(String.valueOf(curPosition))
						&& maxAttempts < 10) {
					attempt++;
					curPosition = getRandomNextPosition(nextOptions);
				}
			}
		}
		return sb.toString();
	}

	private static int getRandomNextPosition(int[] nextOptions) {
		int curPosition;
		int indexNext = new Random().nextInt(nextOptions.length);
		curPosition = nextOptions[indexNext];
		return curPosition;
	}

	/**
	 * Convert pattern from string representation to list of cells
	 * representations.
	 */
	public static List<Cell> convert(String sequence) {
		ArrayList<Cell> pattern = new ArrayList<Cell>();
		for (int i = 0; i < sequence.length(); i++) {
			Cell c = getCellFor(sequence.charAt(i));
			if (c == null)
				throw new IllegalStateException(
						"Trying to convert a sequence with chars other than ['1'..'9']");
			pattern.add(c);
		}
		return pattern;
	}

	/**
	 * Convert pattern from list of cells representations to string
	 * representation.
	 */
	public static String convertDrawPattern(List<Cell> pattern) {
		StringBuilder builder = new StringBuilder();
		for (Cell cell : pattern) {
			char c = getCharFor(cell);
			if (c == '0')
				throw new IllegalStateException("Conversion error");
			builder.append(c);
		}
		return builder.toString();
	}

	private static char getCharFor(Cell cell) {
		if (cell.row == 0 && cell.column == 0) {
			return '1';
		} else if (cell.row == 0 && cell.column == 1) {
			return '2';
		} else if (cell.row == 0 && cell.column == 2) {
			return '3';
		} else if (cell.row == 1 && cell.column == 0) {
			return '4';
		} else if (cell.row == 1 && cell.column == 1) {
			return '5';
		} else if (cell.row == 1 && cell.column == 2) {
			return '6';
		} else if (cell.row == 2 && cell.column == 0) {
			return '7';
		} else if (cell.row == 2 && cell.column == 1) {
			return '8';
		} else if (cell.row == 2 && cell.column == 2) {
			return '9';
		} else
			return '0';
	}

	private static Cell getCellFor(char c) {
		switch (c) {
		case '1':
			return new Cell(0, 0);
		case '2':
			return new Cell(0, 1);
		case '3':
			return new Cell(0, 2);
		case '4':
			return new Cell(1, 0);
		case '5':
			return new Cell(1, 1);
		case '6':
			return new Cell(1, 2);
		case '7':
			return new Cell(2, 0);
		case '8':
			return new Cell(2, 1);
		case '9':
			return new Cell(2, 2);
		default:
			return null;
		}
	}
}
