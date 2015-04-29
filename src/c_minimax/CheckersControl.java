package c_minimax;


//author: Gary Kalmanovich; rights reserved

public class CheckersControl implements InterfaceControl,
ThreadCompleteListener {

  private InterfaceView view;
  private CheckersPosition position;
  private int currentPlayer = 1;
  private InterfaceStrategy player1Strategy = null;// new CheckersStrategy();//
  private InterfaceStrategy player2Strategy = null;// new CheckersStrategy();//
  private boolean isMoveBlockedByCalculation = false;
  private int preferedMinDepthPlayer1 = 5; // set to 5 again in resetGame()
  private int preferedMinDepthPlayer2 = 5; // set to 5 again in resetGame()

  @Override
  public void onMove() {
    position.setPlayer(currentPlayer);
    final CheckersSearchContext context = new CheckersSearchContext();
    context.setOriginalPlayer(currentPlayer);
    context.setMaxSearchTimeForThisPos(2000000000L);// 2 seconds (in nanoseconds) from now
    context.setMaxDepthSearchForThisPos(15); // Search no more than ## moves deep
    if (currentPlayer == 1) {
      if (player1Strategy != null && !isBlockManualMove()) { // isStrategy and game not over
        context.setMinDepthSearchForThisPos(preferedMinDepthPlayer1); // Search no less than ##
        // moves deep
        isMoveBlockedByCalculation = true;
        final NotifyingThread thread = new ThreadStrategy(player1Strategy,
            position, context);
        thread.addListener(this); // add ourselves as a listener
        thread.start(); // Start the Thread
      }
    }
    if (currentPlayer == 2) {
      if (player2Strategy != null && !isBlockManualMove()) { // isStrategy and game not over
        context.setMinDepthSearchForThisPos(preferedMinDepthPlayer2); // Search no less than ##
        // moves deep
        isMoveBlockedByCalculation = true;
        final NotifyingThread thread = new ThreadStrategy(player2Strategy,
            position, context);
        thread.addListener(this); // add ourselves as a listener
        thread.start(); // Start the Thread
      }
    }
  }

  @Override
  // old, old, new, new, player
  public void onMove(final int i0C, final int i0R, final int i1C,
      final int i1R, final int iPlayer) {
    // TODO: Modify to actually use this
    // InterfaceIterator iter = new CheckersIterator( view.nC(), view.nR()); iter.set(iColumn,
    // iRow);
    // //System.out.print("Slot: ( "+iRow+", "+iColumn+" ) is set to color: "+iColor+". Was set to: "+position.getColor(iter));
    // position.setColor(iter, iColor);
    // //System.out.println(". Now: "+position.getColor(iter));
    // currentPlayer = 3 - iColor;
    // onMove();
    final InterfaceIterator iter = new CheckersIterator(view.nC(), view.nR());
    iter.set(i0C, i0R, i1C, i1R);
    position.setColor(iter, iPlayer);
    currentPlayer = 3 - iPlayer;
    onMove();

  }

  @Override
  public void resetGame() {
    position.reset();
    currentPlayer = 1;
    preferedMinDepthPlayer1 = 5; // set to what was in constructor
    preferedMinDepthPlayer2 = 5; // set to what was in constructor
  }

  @Override
  public void setView(final InterfaceView checkersView) {
    view = checkersView;
    position = new CheckersPosition(view.nC(), view.nR());
  }

  @Override
  public InterfaceView getView() {
    return view;
  }

  @Override
  public void setStrategy(final int player, final int strategy) {
    final InterfaceStrategy playerStrategy = strategy == 0 ? null
        : strategy == 1 ? new CheckersStrategyB() : new CheckersStrategyB();
        if (player == 1)
          player1Strategy = playerStrategy;
        else
          player2Strategy = playerStrategy;
        onMove(); // Check if anything needs to be done via a strategy
  }

  @Override
  public int getStrategy(final int player) {
    if (player == 1)
      return player1Strategy == null ? 0 : 1;
    else
      return player2Strategy == null ? 0 : 1;
  }

  @Override
  public boolean isBlockManualMove() {
    return position.isWinner() >= 0 || isMoveBlockedByCalculation;
  }

  @Override
  public void notifyOfThreadComplete(final Thread thread) {
    final InterfaceSearchResult result = ((ThreadStrategy) thread).getResult();
    // System.out.println("Best move(Applct): c "+bestMove.iC()+", r "+bestMove.iR());
    final int iR = result.getBestMoveSoFar().iR();
    final int iC = result.getBestMoveSoFar().iC();
    final int dR = result.getBestMoveSoFar().dR();
    final int dC = result.getBestMoveSoFar().dC();

    thread.interrupt();

    view.performMove(dC, dR, iC, iR, 0); // 0 references is not used in this view
    // I am keeping the iC and iR in the positions that they occupy in Connect4 for the sake of
    // consistency

    final InterfaceSearchContext context = ((ThreadStrategy) thread)
        .getContext();
    if (((CheckersSearchContext) context).getOriginalPlayer() == 1) { // TODO: fix
      // CheckersSearchContext cast
      preferedMinDepthPlayer1 = context.getMinDepthSearchForThisPos();
    } else {
      preferedMinDepthPlayer2 = context.getMinDepthSearchForThisPos();
    }

    isMoveBlockedByCalculation = false;
  }

}