package c_minimax;

public class CheckersSearchContext implements InterfaceSearchContext {

  long timeLimit; // Original time limit
  long maxTime; // Cut off all calculations by this time (System.nanoTime())
  int minSearchDepth;
  int maxSearchDepth;
  int currentDepth;
  int originalPlayer;

  @Override
  public int getCurrentDepth() {
    return currentDepth;
  }

  @Override
  public void setCurrentDepth(final int depth) {
    currentDepth = depth;
  }

  @Override
  public int getMinDepthSearchForThisPos() {
    return minSearchDepth;
  }

  @Override
  public void setMinDepthSearchForThisPos(final int minDepth) {
    minSearchDepth = minDepth;
  }

  @Override
  public int getMaxDepthSearchForThisPos() {
    return maxSearchDepth;
  }

  @Override
  public void setMaxDepthSearchForThisPos(final int maxDepth) {
    maxSearchDepth = maxDepth;
  }

  @Override
  public long getMaxSearchTimeForThisPos() {
    // Cut off all calculations by this time (System.nanoTime())
    return maxTime;
  }

  @Override
  public void setMaxSearchTimeForThisPos(final long timeLimit) {
    this.timeLimit = timeLimit;
    this.maxTime = System.nanoTime() + timeLimit;
  }

  // TODO: PUT THIS IN THE INTERFACE @Override
  public long getOriginalTimeLimit() {
    return timeLimit;
  }

  // TODO: PUT THIS IN THE INTERFACE @Override
  public int getOriginalPlayer() {
    return originalPlayer;
  }

  // TODO: PUT THIS IN THE INTERFACE @Override
  public void setOriginalPlayer(final int player) {
    originalPlayer = player;
  }

}