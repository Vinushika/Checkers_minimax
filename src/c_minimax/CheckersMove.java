package c_minimax;

/**
 * Holds Iterator,Score, such that we can map to it on the TreeMap.
 * This could more efficiently be represented as a long, but I really
 * don't have the time to do this - I want working code before we can have fast efficient code.
 * @author Vinushka
 *
 */
public class CheckersMove {
	InterfaceIterator iterator;
	float score;
	
	public CheckersMove(InterfaceIterator it, float newScore) {
		iterator = it;
		score = newScore;
	}
}
