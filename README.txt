This code was very difficult to write. We kept running into brick wall after brick wall, and only managed to get the code to actually make moves about 2 hours before the deadline, even though we were working on it since Friday. At that point the moves were really dumb, still, and our moves right now are still pretty dumb, and only work via a series of heuristics. Also, Dalton fell ill in the home stretch, so while he contributed a lot of ideas as to how to make the iterator work, and for potential strategies, none of the code turned in is his.

That said, we will talk about what we did to get the moves to work. The issue was primarily getting legal moves quickly and efficiently, and the legal move implementation was not working for most of the time. While it is inefficient, at each level of the negamax we get all the pieces and the possible piece moves, and store them in an ArrayList. Then, we iterate through that list, and if that particular move is legal, then we do negamax on it with alpha-beta pruning.

To get the possible piece moves, we just iterate through each of the 32 squares, and if that particular square is owned by us, then just add all 8 possible moves from that position - we check whether it's legal later on in the algorithm.

To get the legal moves, we do the  following:

First, check if it's a jump. If it's not a jump, make sure there are no other possible capture moves on the board, else forbid it.

Then, check if the destination is empty, and if the source is a piece, and it's our piece. If so, check that black can only move down, and white can only move up.

If it is a capture move, check whether it's a legal capture, which we do by checking that 1. the piece capturing is ours, 2. the piece at the intermediate spot is open, 3. the destination is empty.

Overall, this should produce decent results, but we  still sometimes make illegal moves by not capturing when we should.

David and I wanted to implement a mapping of old positions, and something beyond just the code that we had for Connect4 minimax (i.e. the randomized move generator, and the super fast randomizer).

That said, the randomized move generator plays absolutely terribly, and in fact will loop and run forever if not pruned. It seems that either there is a major bug in the randomized move generator code, or there are many more draws possible in Checkers than we thought possible. Either way, we seem to have trouble.

By including draws into the equation for random scores, play seems to improve quite a bit, though the constant put in front of numDraws is kind of arbitrary, and the score isn't very symmetric (what does draws mean for the opponent? Does the opponent want to favor draws)? We know you talked about this in class, but overall it's still a difficult thing to grasp.

We wanted to hardcode opening moves to reduce the search space (since we know from Chinook that 11-15 for white is the standard opening, and that there are several standard responses to a white move from black that tend to lead to a black victory), and prioritize playing near the center of the board by modifying both the iterator and the randomized move generator with a probability distribution that very heavily favors the center of the board, but as it stands, we could only barely get the application functional, much less playing cleverly.

There are many things we can do to make this more efficient, and in fact we have a lot of ideas but just haven't had the time to implement them since it took so long to make it functional. 

At the end of everything, David thought it would be best to shift to a heuristic method, which seemed like a good idea given how bad our randomized move generator was going. The last copy of the code uses an updated randomized move generator, and a better implementation of hasJumps.