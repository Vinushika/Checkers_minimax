package c_minimax;

//author: Gary Kalmanovich; rights reserved

public class CheckersIterator implements InterfaceIterator {

  // NOTE, this iterator just iterates the black squares
  // One would need to write a different iterator (possibly,
  // but not necessarily based on this one) to iterate over
  // possible moves

  // private int iterator = 1; // Starts at 1 and skips every other (+=2)
  private int pieceIterator = 1;
  private int destinationIterator = 1;
  private final int nC;
  private final int nR;

  CheckersIterator(final int nC, final int nR) {
    this.nC = nC;
    this.nR = nR;
    resetBack();
  }

  @Override
  public int iC() {
    return pieceIterator % nC;
  }

  @Override
  public int iR() {
    return pieceIterator / nC;
  }

  @Override
  public int dR() {
    // even
    if (destinationIterator % 2 == 0) {
      if (destinationIterator < 6) {
        return iR() + 1;
      } else {
        return iR() + 2;
      }
      // odd
    } else {
      if (destinationIterator < 5) {
        return iR() - 1;
      } else {
        return iR() - 2;
      }
    }
  }

  @Override
  public int dC() {
    // return destinationIterator % nC;
    // 1-4 inside, 5-8 outside
    // odds above, evens below
    // 1,2 left for inner, 5,6 left for outer
    if (destinationIterator < 5) {
      if (destinationIterator < 3) {
        return iC() - 1;
      } else {
        return iC() + 1;
      }
    } else {
      if (destinationIterator < 7) {
        return iC() - 2;
      } else {
        return iC() + 2;
      }
    }
  }

  @Override
  public int nC() {
    return nC;
  }

  @Override
  public int nR() {
    return nR;
  }

  @Override
  public void increment() {
    // iterator += 2;
    if (destinationIterator == 8) {
      destinationIterator = 1;
      // pieceIterator += 2;
      if (iC() == 7) {
        pieceIterator++;
      } else if (iC() == 6) {
        pieceIterator += 3;
      } else {
        pieceIterator += 2;
      }
    } else {
      destinationIterator += 1;
    }
  }

  @Override
  public void resetBack() {
    pieceIterator = 1;
    destinationIterator = 1;

  }

  @Override
  public void set(final InterfaceIterator iter) {
    pieceIterator = nC * iter.iR() + iter.iC();
    //
    // // Dalen promises this makes sense
    // if (iC() % 2 == 0) {
    // pieceIterator += 1;
    // }

    setDestinationIterator(iter.iR(), iter.iC(), iter.dR(), iter.dC());
  }

  private void setDestinationIterator(final int sourceR, final int sourceC,
      final int destinationR, final int destinationC) {
    final int destSourceRowDif = destinationR - sourceR;
    final int destSourceColDif = destinationC - sourceC;

    if (destSourceRowDif < 0 && destSourceColDif < 0) {
      if (destSourceRowDif == -2) {
        destinationIterator = 5;
      } else {
        destinationIterator = 1;
      }
    } else if (destSourceRowDif > 0 && destSourceColDif < 0) {
      if (destSourceRowDif == 2) {
        destinationIterator = 6;
      } else {
        destinationIterator = 2;
      }
    } else if (destSourceRowDif < 0 && destSourceColDif > 0) {
      if (destSourceRowDif == -2) {
        destinationIterator = 7;
      } else {
        destinationIterator = 3;
      }
    } else {
      if (destSourceRowDif == 2) {
        destinationIterator = 8;
      } else {
        destinationIterator = 4;
      }
    }
  }

  @Override
  public void set(final int sourceC, final int sourceR, final int destinationC,
      final int destinationR) {
    // assert (iR + iC) % 2 == 1;
    // /4 for piece mod 4 for destination
    pieceIterator = nC * sourceR + sourceC;
    setDestinationIterator(sourceR, sourceC, destinationR, destinationC);
  }

  @Override
  public boolean isInBounds() {
    return iC() >= 0 && iC() < nC && iR() >= 0 && iR() < nR;
  }

  @Override
  public void set(final int iC, final int iR) {
    System.err.println("Don't call this for checkers!");
    set(iC, iR, 1, 1);
  }

  @Override
  public boolean isDestinationInBounds() {
    return dC() >= 0 && dC() < nC && dR() >= 0 && dR() < nR;
  }

}
