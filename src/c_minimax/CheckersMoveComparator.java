package c_minimax;

import java.util.Comparator;

/**
 * Sorts checkers moves such that they go toward the middle.
 * @author Vinushka
 *
 */
public class CheckersMoveComparator implements Comparator<InterfaceIterator> {

	@Override
	public int compare(InterfaceIterator arg0, InterfaceIterator arg1) {
		//minimize distance to the middle, which is 3.5
		double diff1 = Math.abs(arg0.iR() - 3.5);
		double diff2 = Math.abs(arg1.iR() - 3.5);
		if (diff1 < diff2) {
			return -1;
		} else if (diff1 > diff2) {
			return 1;
		}else {
			return 0;
		}
	}

}
