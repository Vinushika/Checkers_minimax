package c_minimax;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class CheckersIteratorTest {

  @Before
  public void setUp() throws Exception {
  }

  @Test
  public void test() {
    final CheckersIterator iter = new CheckersIterator(8, 8);
    final int i = 0;
    while (iter.isInBounds()) {
      // System.out.println("Source col: " + iter.iC() + " Source row: "
      // + iter.iR() + " Dest Col " + iter.dC() + " Dest Row " + iter.dR()
      // + " DestInBounds: " + iter.isDestinationInBounds());
      final CheckersIterator newIter = new CheckersIterator(8, 8);
      newIter.set(iter.iC(), iter.iR(), iter.dC(), iter.dR());
      // System.out.println(i++);
      assertTrue(iter.iC() == newIter.iC());
      assertTrue(iter.iR() == newIter.iR());
      assertTrue(iter.dC() == newIter.dC());
      assertTrue(iter.dR() == newIter.dR());
      assertTrue(iter.isDestinationInBounds() == newIter
          .isDestinationInBounds());
      iter.increment();
    }
  }

}
