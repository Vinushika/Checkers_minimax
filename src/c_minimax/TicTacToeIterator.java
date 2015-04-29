package c_minimax;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

//author: Gary Kalmanovich; rights reserved

public class TicTacToeIterator implements InterfaceIterator {

  static int countNewIterators = 0;
  private ArrayList<Integer> shuffledIterators;
  private ArrayList<Integer> inverseShuffleMap;
  private int iterator = 0;

  TicTacToeIterator() {
    shuffledIterators = new ArrayList<Integer>(nC() * nR());
    inverseShuffleMap = new ArrayList<Integer>(nC() * nR());
    for (int i = 0; i < nC() * nR(); i++) {
      shuffledIterators.add(i, i);
      inverseShuffleMap.add(i, 0);
    }
    // If one wishes to play a non-randomized version, just comment out line below
    Collections.shuffle(shuffledIterators, new Random(countNewIterators++));
    for (int i = 0; i < nC() * nR(); i++) {
      inverseShuffleMap.set(shuffledIterators.get(i), i);
    }
  }

  TicTacToeIterator(final InterfaceIterator iter) {
    this.set(iter);
  }

  @Override
  public int iC() {
    return shuffledIterators.get(iterator) % 3;
  }

  @Override
  public int iR() {
    return shuffledIterators.get(iterator) / 3;
  }

  @Override
  public int nC() {
    return 3;
  }

  @Override
  public int nR() {
    return 3;
  }

  @Override
  public void increment() {
    iterator++;
  }

  @Override
  public void resetBack() {
    iterator = 0;
  }

  @Override
  public void set(final InterfaceIterator iter) {
    iterator = ((TicTacToeIterator) iter).iterator;
    shuffledIterators = ((TicTacToeIterator) iter).shuffledIterators;
    inverseShuffleMap = ((TicTacToeIterator) iter).inverseShuffleMap;
  }

  @Override
  public void set(final int iC, final int iR) {
    iterator = inverseShuffleMap.get(3 * iR + iC);
  }

  @Override
  public boolean isInBounds() {
    return 0 <= iterator && iterator < 3 * 3;
  }

  @Override
  public int dC() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public int dR() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public void set(final int iC, final int iR, final int dC, final int dR) {
    // TODO Auto-generated method stub

  }

  @Override
  public boolean isDestinationInBounds() {
    // TODO Auto-generated method stub
    return false;
  }

}
