package c_minimax;

import java.util.Comparator;

/**
 * Sorts checkers moves such that they go toward the middle.
 *
 * @author Vinushka
 *
 */
public class CheckersMoveComparator implements Comparator<InterfaceIterator> {

  @Override
  public int compare(final InterfaceIterator arg0, final InterfaceIterator arg1) {
    // minimize distance to the middle, which is 3.5
    final double diff1 = Math.abs(arg0.dC() - 3.5);
    final double diff2 = Math.abs(arg1.dC() - 3.5);
    if (diff1 < diff2) {
      return -1;
    } else if (diff1 > diff2) {
      return 1;
    } else {
      return 0;
    }
  }

}
