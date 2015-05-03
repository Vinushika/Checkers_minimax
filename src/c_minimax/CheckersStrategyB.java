package c_minimax;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class CheckersStrategyB implements InterfaceStrategy {
  TreeMap<Long, CheckersMove> checkedPositions = new TreeMap<Long, CheckersMove>();
  // TODO: switch to mapDB to reduce memory usage...
  // minor slowdown @16.7
  // million (try mapDB?)
  FastRandomizer rand = new FastRandomizer(); // automatic seeds anyway
  static final float DRAW_PENALTY = 0.5f; // penalizes draws

  public float heuristicScore(final InterfacePosition position,
      final Integer player) {
    final InterfaceIterator iPos = new CheckersIterator(position.nC(),
        position.nR());
    final float CHANGEAMOUNT = .0625f;
    float score = 0f;
    for (int currentPiece = 0; currentPiece < 32; currentPiece++) {
      final int color = position.getColor(iPos);
      if (color == 0) {

      } else {
        if (color == player) {
          score += CHANGEAMOUNT + currentPiece / 33;
        } else {
          score -= CHANGEAMOUNT + currentPiece / 33;
        }
      }
      iPos.increment();
      iPos.increment();
      iPos.increment();
      iPos.increment();
      iPos.increment();
      iPos.increment();
      iPos.increment();
      iPos.increment();
    }
    return score / 2;
  }

  @Override
  public InterfaceSearchResult getBestMove(final InterfacePosition position,
      final InterfaceSearchContext context) {
    return negamax(position, context, Float.NEGATIVE_INFINITY,
        Float.POSITIVE_INFINITY);
  }

  public InterfaceSearchResult negamax(final InterfacePosition position,
      final InterfaceSearchContext context, float alpha, final float beta) {
    final InterfaceSearchResult searchResult = new CheckersSearchResult();

    // TODO Maybe remove Fthis whole checkedPositions thing if we don't want to hash (without mapdb)
    final CheckersMove checkedResult = checkedPositions.get(position
        .getRawPosition());
    if (checkedResult != null) {
      searchResult
          .setBestMoveSoFar(checkedResult.iterator, checkedResult.score);
    } else { // position is not hashed, so let's see if we can process it

      final int player = position.getPlayer();
      final int opponent = 3 - player; // There are two players, 1 and 2.

      final float uncertaintyPenalty = .01f;
      final InterfacePosition posNew = new CheckersPosition(position);
      final InterfaceIterator iPos = new CheckersIterator(position.nC(),
          position.nR());
      final InterfaceIterator destColorChecker = new CheckersIterator(
          position.nC(), position.nR());
      // TODO We're going to have to change how we iterate through the possible positions
      // TODO we'll also have to check to see if positions are legal
      // Strategy: Start near the middle, then fan out.
      final List<InterfaceIterator> pieceMoves = new ArrayList<InterfaceIterator>();
      for (int currentPiece = 0; currentPiece < 32; currentPiece++) {
        // System.out.println("itterating through pieces, player is "
        // + position.getColor(iPos));
        if (position.getColor(iPos) == player) {
          for (int pieceMove = 0; pieceMove < 8; pieceMove++) {
            // System.out.println("iterating through piece moves");
            final InterfaceIterator newIPos = new CheckersIterator(
                position.nC(), position.nR());
            newIPos.set(iPos);
            pieceMoves.add(newIPos);
            iPos.increment();
          }
        } else {
          // iterate through the eight moves for the piece that isn't ours
          iPos.increment();
          iPos.increment();
          iPos.increment();
          iPos.increment();
          iPos.increment();
          iPos.increment();
          iPos.increment();
          iPos.increment();
        }
      }
      pieceMoves.sort(new CheckersMoveComparator());
      final boolean hasJump = hasJumps(position, destColorChecker, player);
      for (final InterfaceIterator pieceMove : pieceMoves) {
        // System.out.println("is in piece move loop");
        if (isLegalMove(position, pieceMove, destColorChecker, player, hasJump)) {
          // System.out.println(pieceMove.iC() + "," + pieceMove.iR() + " to "
          // + pieceMove.dC() + "," + pieceMove.dR() + " is legal move.");
          if (searchResult.getBestMoveSoFar() == null)
            searchResult.setBestMoveSoFar(pieceMove,
                searchResult.getBestScoreSoFar());
          posNew.setColor(pieceMove, player);
          // System.out.println(pieceMove.iC() + "," + pieceMove.iR() + " to "
          // + pieceMove.dC() + "," + pieceMove.dR());
          final int isWin = posNew.isWinner(pieceMove); // iPos
          float score;
          if (isWin == player) {
            score = 1f; // Win
          } else if (isWin == 0) {
            score = 0f; // Draw
          } else if (isWin == opponent) {
            score = -1f; // Loss
          } else { // Game is not over, so check further down the game
            if (context.getCurrentDepth() < context
                .getMaxDepthSearchForThisPos() && // No more than max
                context.getCurrentDepth() < context
                    .getMinDepthSearchForThisPos()) { // No more than min
              posNew.setPlayer(opponent);
              context.setCurrentDepth(context.getCurrentDepth() + 1);
              final InterfaceSearchResult opponentResult = negamax(posNew,
                  context, -alpha, -beta); // Return information is in opponentContext
              context.setCurrentDepth(context.getCurrentDepth() - 1);
              score = -opponentResult.getBestScoreSoFar();
              // Note, for player, opponent's best move has negative worth
              // That is because, score = ((probability of win) - (probability of loss))

              if (opponentResult.isResultFinal() == false) { // if the result is not final, reverse
                // penalty
                searchResult.setIsResultFinal(false);
                score -= 2 * uncertaintyPenalty;
              }
            } else {
              // We cannot recurse further down the minimax search
              // play n random boards, collect score
              // int numWin = 0;
              // int numLose = 0;
              // int numDraws = 0;
              // final float total_plays = 30.0f; // change this if we ever want to play less or
              // // // more
              // for (int i = 0; i < total_plays; i++) {
              // final int winner = playRandomlyUntilEnd(posNew, player);
              // // ok, we have an end state.
              // if (winner == player) {
              // // we win!
              // numWin++;
              // } else if (winner == opponent) {
              // // we lose!
              // numLose++;
              // } else {
              // numDraws++;
              // }
              // }
              // score = (numWin - numLose - (DRAW_PENALTY * numDraws))
              // / total_plays;
              // score = -uncertaintyPenalty;
              final int winner = posNew.isWinner();
              if (winner == -1) {
                score = heuristicScore(posNew, player);
              } else {
                score = winner;
              }
              searchResult.setIsResultFinal(false);
            }
          }
          // System.out.println(searchResult.getBestScoreSoFar());
          if (searchResult.getBestMoveSoFar() == null
              || searchResult.getBestScoreSoFar() < score) {
            searchResult.setBestMoveSoFar(pieceMove, score);
            if (score == 1f)
              break; // No need to search further if one can definitely win
          }
          alpha = Math.max(alpha, score);
          if (alpha >= beta) {
            break; // alpha-beta pruning
          }
          // System.out.println(searchResult.getBestScoreSoFar());
        }
        final long timeNow = System.nanoTime();
        if (context.getMaxSearchTimeForThisPos() - timeNow <= 20000) {
          // get OUT of here so we don't lose!!!
          System.out.println("Time almost up, making any move we can!");
          System.out
              .println("CheckersStrategy:getBestMove(): ran out of time: maxTime("
                  + context.getMaxSearchTimeForThisPos()
                  + ") :time("
                  + timeNow
                  + "): recDepth(" + context.getCurrentDepth() + ")");
          if (context.getCurrentDepth() == 0) {
            // Revert back to a lesser search
            System.out.print("CheckersStrategy: Depth limit of "
                + context.getMinDepthSearchForThisPos() + " -> ");
            context.setMinDepthSearchForThisPos(context
                .getMinDepthSearchForThisPos() - 1);
            System.out.println(context.getMinDepthSearchForThisPos());
          }
          if (((CheckersSearchContext) context).getOriginalPlayer() == opponent) {
            searchResult.setBestMoveSoFar(searchResult.getBestMoveSoFar(),
                0.95f); // Set to original opponent almost-win
          }
          searchResult.setIsResultFinal(false);
          break; // Need to make any move now
        }

      }
    }
    // System.out.println("Returning...");
    return searchResult;

    // // if we haven't run out of time yet, then increase the depth
    // final long timeLeftInNanoSeconds = context.getMaxSearchTimeForThisPos()
    // - System.nanoTime();
    // if (context.getCurrentDepth() == 0
    // && !searchResult.isResultFinal()
    // && timeLeftInNanoSeconds > ((CheckersSearchContext) context)
    // .getOriginalTimeLimit() * 9 / 10) {
    // System.out.print("CheckersStrategyB: Depth limit of "
    // + context.getMinDepthSearchForThisPos() + " -> ");
    // context
    // .setMinDepthSearchForThisPos(context.getMinDepthSearchForThisPos() + 1);
    // System.out.println(context.getMinDepthSearchForThisPos());
    // final InterfaceSearchResult anotherResult = getBestMove(position, context);
    // if (anotherResult.getBestScoreSoFar() > searchResult.getBestScoreSoFar()) {
    // searchResult.setBestMoveSoFar(anotherResult.getBestMoveSoFar(),
    // anotherResult.getBestScoreSoFar());
    // searchResult.setIsResultFinal(anotherResult.isResultFinal());
    // }
    //
    // }

  }

  @Override
  public void setContext(final InterfaceSearchContext strategyContext) {
    // TODO Auto-generated method stub

  }

  public int playRandomlyUntilEnd(final InterfacePosition pos, final int player) {
    // TODO I just copied this from Checkers code, so change it to make it make sense for checkers
    // strategy for this code: while the position is not an ending position,
    // keep making random moves until someone wins, then return the score
    // the calling code calls this 100 times, and computes how many times are win
    // vs how many times are loss, over 100
    // draws are taken out of the equation
    // this should never be called starting from a position with no fillable spots
    int current_player = 3 - player;
    final InterfacePosition posNew = new CheckersPosition(pos);
    while (posNew.isWinner() == -1) {
      // find a position that is playable by iterating through the columns
      boolean isFillable = false;
      int final_iC = -1;
      int final_iR = -1;
      int final_dC = 0;
      int final_dR = 0;
      // strategy for the checkers random move:
      // we know iC and iR go from 1 to 8, and iR goes from 1 to 8
      // therefore pick a random iC and iR from 1 to 8,
      // and set dC and dR to iC -1, iR-1 (since destinationIterator = 1 when you do this)
      // then, check the 8 positions around it (do a forloop with 8 iterations)
      // If you can fill the position, fill it immediately. Otherwise,
      // then just pick a new random location.
      final InterfaceIterator iPos = new CheckersIterator(posNew.nC(),
          posNew.nR());
      // get all pieces we can fill from here
      final List<InterfaceIterator> pieceMoves = new ArrayList<InterfaceIterator>();
      for (int currentPiece = 0; currentPiece < 32; currentPiece++) {
        // System.out.println("itterating through pieces, player is "
        // + position.getColor(iPos));
        if (posNew.getColor(iPos) == player) {
          for (int pieceMove = 0; pieceMove < 8; pieceMove++) {
            // System.out.println("iterating through piece moves");
            final InterfaceIterator newIPos = new CheckersIterator(posNew.nC(),
                posNew.nR());
            newIPos.set(iPos);
            pieceMoves.add(newIPos);
            iPos.increment();
          }
        } else {
          // iterate through the eight moves for the piece that isn't ours
          iPos.increment();
          iPos.increment();
          iPos.increment();
          iPos.increment();
          iPos.increment();
          iPos.increment();
          iPos.increment();
          iPos.increment();
        }
      }
      while (!isFillable) {
        if (pieceMoves.size() == 0) {
          // if we can't make any moves, it's a draw
          return 0;
        }
        final InterfaceIterator destChecker = new CheckersIterator(iPos.nC(),
            iPos.nR());
        final boolean hasJump = hasJumps(posNew, destChecker, player);
        final int moveIndex = rand.nextInt(pieceMoves.size());
        // get a random move from the moves we can make
        iPos.set(pieceMoves.get(moveIndex));
        pieceMoves.remove(moveIndex);
        if (posNew.getColor(iPos) == current_player) {
          for (int dMove = 0; dMove < 8; dMove++) {
            // go through the 8 potential destinations

            if (isLegalMove(posNew, iPos, destChecker, current_player, hasJump)) {
              // it's fillable, so let's put something in it
              isFillable = true;
              final_iR = iPos.iR();
              final_iC = iPos.iC();
              final_dC = iPos.dC();
              final_dR = iPos.dR();
              break; // defensive programming
            }
            iPos.increment(); // if we haven't broken yet, try a new one.
          }
        }
      }
      // we have a playable position, let's play it
      posNew.setPlayer(current_player);
      iPos.set(final_iC, final_iR, final_dC, final_dR);
      posNew.setColor(iPos, current_player);
      current_player = 3 - current_player;
    }
    return posNew.isWinner();
  }

  public boolean isLegalJump(final InterfacePosition position,
      final InterfaceIterator iPos, final InterfaceIterator destColorChecker,
      final int player) {
    // final InterfaceIterator pieceMove = iPos;
    // System.out.println(pieceMove.iC() + "," + pieceMove.iR() + " to "
    // + pieceMove.dC() + "," + pieceMove.dR());
    final int takenCol = (iPos.dC() + iPos.iC()) / 2;
    final int takenRow = (iPos.dR() + iPos.iR()) / 2;
    // System.out.println(takenCol + "," + takenRow + " will be jumped");

    if (!iPos.isDestinationInBounds()) {
      return false;
    }

    if (player == 1) {
      if (iPos.dR() - iPos.iR() > 0) {
        // destination reachable, can move backward.
        return false;
      }
    } else if (player == 2) {
      if (iPos.dR() - iPos.iR() < 0) {
        // destination reachable, can move "forward"
        return false;
      }
    }

    // check that the moving piece is owned by the player
    if (position.getColor(iPos) != player) {
      return false;
    }

    // destination is open
    destColorChecker.set(iPos.dC(), iPos.dR(), 1, 1);
    if (position.getColor(destColorChecker) != 0) {
      return false;
    }

    // piece being jumped is the enemy
    destColorChecker.set(takenCol, takenRow, 1, 1);
    if (position.getColor(destColorChecker) != (3 - player)) {
      return false;
    }

    // otherwise false
    return true;
  }

  public boolean hasJumps(final InterfacePosition position,
      final InterfaceIterator destColorChecker, final int player) {
    final InterfaceIterator iPos = new CheckersIterator(position.nC(),
        position.nR());
    for (int currentPiece = 0; currentPiece < 32; currentPiece++) {
      if (position.getColor(iPos) != player) {
        iPos.increment();
        iPos.increment();
        iPos.increment();
        iPos.increment();
        iPos.increment();
        iPos.increment();
        iPos.increment();
        iPos.increment();
      } else {
        iPos.increment();
        iPos.increment();
        iPos.increment();
        iPos.increment();
        for (int pieceMove = 4; pieceMove < 8; pieceMove++) {
          if (isLegalJump(position, iPos, destColorChecker, player)) {
            return true;
          }
          iPos.increment();
        }
      }
    }
    return false;
  }

  public boolean isLegalMove(final InterfacePosition position,
      final InterfaceIterator iPos, final InterfaceIterator destColorChecker,
      final int player, final boolean hasJump) {

    if (iPos.isDestinationInBounds()) {
      final int dC = iPos.dC();
      final int dR = iPos.dR();
      destColorChecker.set(dC, dR, 1, 1);
      final boolean isJump = Math.abs(dC - iPos.iC()) == 2;
      if (!isJump && !hasJump) {
        // destination is clear
        if (position.getColor(destColorChecker) == 0) {
          // moving piece is yours
          if (position.getColor(iPos) == player) {
            // if white, can only move forward.
            // if black, can only move backward
            if (player == 2) {
              if (iPos.dR() - iPos.iR() > 0) {
                // destination reachable, can move backward.
                return true;
              }
            } else if (player == 1) {
              if (iPos.dR() - iPos.iR() < 0) {
                // destination reachable, can move "forward"
                return true;
              }
            }
          }
        }
      }
      // if it's 2, do stuff.
      if (isJump)
        return isLegalJump(position, iPos, destColorChecker, player);
    }
    return false;
  }

  @Override
  public InterfaceSearchContext getContext() {
    // TODO Auto-generated method stub
    return null;
  }

  public class FastRandomizer {
    long seed = System.nanoTime(); // spawned at launch

    /**
     * Gets a number in the range (0,max_exclusive), exclusive
     *
     * @param max_exclusive
     * @return
     */
    public int nextInt(final int max_exclusive) {
      seed ^= (seed << 21);
      seed ^= (seed >>> 35);
      seed ^= (seed << 4);
      // use Math.abs because Java is dumb and doesn't do unsigned longs
      return (int) Math.abs(seed % max_exclusive);
    }
  }

  class CheckersSearchContext implements InterfaceSearchContext {

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

  class CheckersSearchResult implements InterfaceSearchResult {
    // TODO I just copied this from the checkers search result, ensure that it makes sense.

    InterfaceIterator bestMoveSoFar = null;
    private short bestScoreSoFar = -(1 << 15); // (1<<14) is +1.f and -(1<<14) is -1.f
    boolean isFinal = true;

    @Override
    public InterfaceIterator getBestMoveSoFar() {
      return bestMoveSoFar;
    }

    @Override
    public float getBestScoreSoFar() {
      return bestScoreSoFar / ((float) (1 << 14));
    }

    @Override
    public void setBestMoveSoFar(final InterfaceIterator newMove,
        final float newScore) {
      bestMoveSoFar = newMove;
      bestScoreSoFar = (short) (newScore * (1 << 14));
    }

    @Override
    public int getClassStateCompacted() {
      // UNUSED IN CHECKERS
      return 0;
    }

    @Override
    public void setClassStateFromCompacted(final int compacted) {
      // UNUSED IN CHECKERS
      return;
    }

    @Override
    public void setIsResultFinal(final boolean isFinal) {
      this.isFinal = isFinal;

    }

    @Override
    public boolean isResultFinal() {
      return isFinal;
    }

    @Override
    public float getOpponentBestScoreOnPreviousMoveSoFar() {
      // Not used in this strategy
      return 0 / 0;
    }

    @Override
    public void setOpponentBestScoreOnPreviousMoveSoFar(final float scoreToBeat) {
      // Not used in this strategy
    }

  }

}