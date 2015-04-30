package c_minimax;

import java.util.Random;
import java.util.TreeMap;

public class CheckersStrategyB implements InterfaceStrategy {
	TreeMap<Long, CheckersMove> checkedPositions = new TreeMap<Long, CheckersMove>();
	//TODO: switch to mapDB to reduce memory usage...
	// minor slowdown @16.7
	// million (try mapDB?)
	FastRandomizer rand = new FastRandomizer(); // automatic seeds anyway

	@Override
	public InterfaceSearchResult getBestMove(InterfacePosition position, InterfaceSearchContext context) {
		return negamax(position, context, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY);
	}

	public InterfaceSearchResult negamax(InterfacePosition position, InterfaceSearchContext context, float alpha, float beta) {
		final InterfaceSearchResult searchResult = new CheckersSearchResult();

		// TODO Maybe remove this whole checkedPositions thing if we don't want to hash (without mapdb)
		final CheckersMove checkedResult = checkedPositions.get(position
				.getRawPosition());
		if (checkedResult != null) {
			searchResult.setBestMoveSoFar(checkedResult.iterator, checkedResult.score);
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
			for (int currentMove = 0; currentMove < 256; currentMove++) {
				// destColorChecker.set(iPos.dC(), iPos.dR(), 1, 1);
				if (isLegalMove(position, iPos, destColorChecker, player)) {
					System.out.println(currentMove + " is legal.");
					if (searchResult.getBestMoveSoFar() == null)
						searchResult.setBestMoveSoFar(iPos,
								searchResult.getBestScoreSoFar());
					posNew.setColor(iPos, player);
					final int isWin = posNew.isWinner(iPos); // iPos
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
							// We cannot recurse further down the minimax search
							// play n random boards, collect score
							int numWin = 0;
							int numLose = 0;
							final float total_plays = 10.0f; // change this if we ever want to play less or more
							for (int i = 0; i < total_plays; i++) {
								final int winner = playRandomlyUntilEnd(posNew, player);
								// ok, we have an end state.
								if (winner == player) {
									// we win!
									numWin++;
								} else if (winner == opponent) {
									// we lose!
									numLose++;
								}
							}
							score = (numWin - numLose) / total_plays;
							// score = -uncertaintyPenalty;
							searchResult.setIsResultFinal(false);
						}
					}
					System.out.println(searchResult.getBestScoreSoFar());
					if (searchResult.getBestMoveSoFar() == null
							|| searchResult.getBestScoreSoFar() < score) {
						searchResult.setBestMoveSoFar(iPos, score);
						if (score == 1f)
							break; // No need to search further if one can definitely win
					}
					alpha = Math.max(alpha, score);
					if (alpha >= beta) {
						break; //alpha-beta pruning
					}
				}
				final long timeNow = System.nanoTime();
				if (context.getMaxSearchTimeForThisPos() - timeNow <= 2000000) {
					//get OUT of here so we don't lose!!!
					System.out.println("Time almost up, making any move we can!");
					 System.out.println("CheckersStrategy:getBestMove(): ran out of time: maxTime("
					 +context.getMaxSearchTimeForThisPos()+") :time("
					 +timeNow+"): recDepth("+context.getCurrentDepth()+")");
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
				iPos.increment();
			}
		}

		// if we haven't run out of time yet, then increase the depth
		final long timeLeftInNanoSeconds = context.getMaxSearchTimeForThisPos()
				- System.nanoTime();
		if (context.getCurrentDepth() == 0
				&& !searchResult.isResultFinal()
				&& timeLeftInNanoSeconds > ((CheckersSearchContext) context)
				.getOriginalTimeLimit() * 9 / 10) {
			System.out.print("CheckersStrategyB: Depth limit of "
					+ context.getMinDepthSearchForThisPos() + " -> ");
			context
			.setMinDepthSearchForThisPos(context.getMinDepthSearchForThisPos() + 1);
			System.out.println(context.getMinDepthSearchForThisPos());
			final InterfaceSearchResult anotherResult = getBestMove(position, context);
			if (anotherResult.getBestScoreSoFar() > searchResult.getBestScoreSoFar()) {
				searchResult.setBestMoveSoFar(anotherResult.getBestMoveSoFar(),
						anotherResult.getBestScoreSoFar());
				searchResult.setIsResultFinal(anotherResult.isResultFinal());
			}

		}

		return searchResult;

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
			//strategy for the checkers random move:
			//we know iC and iR go from 1 to 8, and iR goes from 1 to 8
			//therefore pick a random iC and iR from 1 to 8,
			//and set dC and dR to iC -1, iR-1 (since destinationIterator = 1 when you do this)
			//then, check the 8 positions around it (do a forloop with 8 iterations)
			//If you can fill the position, fill it immediately. Otherwise, 
			//then just pick a new random location.
			final InterfaceIterator iPos = new CheckersIterator(posNew.nC(),
					posNew.nR());
			while (!isFillable) {
				int iC = rand.nextInt(8);
				int iR = rand.nextInt(8);
				for (int dMove = 0; dMove < 8; dMove++) {
					//go through the 8 potential destinations
					iPos.set(iC, iR, iC-1, iR-1); //see above strategy
					InterfaceIterator destChecker = new CheckersIterator(iPos.nC(), iPos.nR());
					if (isLegalMove(posNew, iPos, destChecker, current_player)) {
						//it's fillable, so let's put something in it
						isFillable = true;
						final_iR = iR;
						final_iC = iC;
						final_dC = iPos.dC();
						final_dR = iPos.dR();
						break; // defensive programming
					}
					iPos.increment(); //if we haven't broken yet, try a new one.
				}
				// we have a playable position, let's play it
				posNew.setPlayer(current_player);
				iPos.set(final_iC, final_iR, final_dC, final_dR);
				posNew.setColor(iPos, current_player);
				current_player = 3 - current_player;
			}
		}
		return posNew.isWinner();
	}


	public boolean isLegalMove(final InterfacePosition position,
			final InterfaceIterator iPos, final InterfaceIterator destColorChecker,
			final int player) {
		if (iPos.isDestinationInBounds()) {
			destColorChecker.set(iPos.dC(), iPos.dR(), 1, 1);
			if (position.getColor(destColorChecker) == 0) {
				if (position.getColor(iPos) == player) {
					if ((Math.abs(iPos.iC() - iPos.dC()) == 1)) {
						return true;
					}
				}
			}
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
		 * Gets a number in the range (1,max_exclusive), inclusive
		 * 
		 * @param max_exclusive
		 * @return
		 */
		public int nextInt(int max_exclusive) {
			seed ^= (seed << 21);
			seed ^= (seed >>> 35);
			seed ^= (seed << 4);
			// use Math.abs because Java is dumb and doesn't do unsigned longs
			return (int) (Math.abs(seed % max_exclusive) + 1);
		}
	}

	class CheckersSearchContext implements InterfaceSearchContext {

		long timeLimit; // Original time limit
		long maxTime;   // Cut off all calculations by this time (System.nanoTime())
		int  minSearchDepth;
		int  maxSearchDepth;
		int    currentDepth;
		int  originalPlayer;

		@Override
		public int getCurrentDepth() {
			return currentDepth;
		}

		@Override
		public void setCurrentDepth(int depth) {
			currentDepth = depth;
		}

		@Override
		public int getMinDepthSearchForThisPos() {
			return minSearchDepth;
		}

		@Override
		public void setMinDepthSearchForThisPos(int minDepth) {
			minSearchDepth = minDepth;
		}

		@Override
		public int getMaxDepthSearchForThisPos() {
			return maxSearchDepth;
		}

		@Override
		public void setMaxDepthSearchForThisPos(int maxDepth) {
			maxSearchDepth = maxDepth;
		}

		@Override
		public long getMaxSearchTimeForThisPos() {
			// Cut off all calculations by this time (System.nanoTime())
			return maxTime;
		}

		@Override
		public void setMaxSearchTimeForThisPos(long timeLimit) {
			this.timeLimit =                        timeLimit;
			this.maxTime   = System.nanoTime()    + timeLimit;
		}

		//TODO: PUT THIS IN THE INTERFACE @Override
		public long getOriginalTimeLimit() {
			return timeLimit;
		}

		//TODO: PUT THIS IN THE INTERFACE @Override
		public int getOriginalPlayer() {
			return originalPlayer;
		}

		//TODO: PUT THIS IN THE INTERFACE @Override
		public void setOriginalPlayer(int player) {
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
			//UNUSED IN CHECKERS
			return 0;
		}

		@Override
		public void setClassStateFromCompacted(int compacted) {
			//UNUSED IN CHECKERS
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