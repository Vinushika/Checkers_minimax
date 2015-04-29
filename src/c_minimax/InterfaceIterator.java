package c_minimax;

//author: Gary Kalmanovich; rights reserved

//This is an iterator for positions. E.g., over squares on a board-game
public interface InterfaceIterator {
  public int iC(); // Initial Column

  public int iR(); // Initial Row

  public int dC(); // Destination Column

  public int dR(); // Destination Row

  public int nC();

  public int nR();

  public void set(int iC, int iR); // Don't use this for Checkers

  public void set(int iC, int iR, int dC, int dR); // Use this for Checkers

  public void set(InterfaceIterator iter);

  public void increment();

  public void resetBack();

  public boolean isInBounds();

  public boolean isDestinationInBounds();
}