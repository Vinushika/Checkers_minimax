package c_minimax;

//author: Gary Kalmanovich; rights reserved

public class CheckersPosition implements InterfacePosition {

    // This implementation is designed for at most 8 columns by 8 rows
    // It packs the entire position into a single long

    private long position = 0;
    private int nC = 0;
    private int nR = 0;

    CheckersPosition( int nC, int nR) {
        position = 0;
        this.nC = nC;
        this.nR = nR;
    }

    CheckersPosition( InterfacePosition pos ) {
        position = pos.getRawPosition();
        nC       = pos.nC();
        nR       = pos.nR();
    }

    @Override public int nC() { return nC; }
    @Override public int nR() { return nR; }

    @Override public long getRawPosition() { return position; }

    @Override
    public int getColor( InterfaceIterator iPos ) { // 0 if transparent, 1 if white, 2 if black
        return getColor( iPos.iC(), iPos.iR() );
    }

    private int getColor( int iC, int iR ) { // 0 if transparent, 1 if white, 2 if black
        return (int) (position >>> (((iC/2 + iR*4)*2) & 3));
    }

    @Override
    public void setColor( InterfaceIterator iPos, int color ) { // color is 1 if red, 2 if yellow
        setColor( iPos.iC(), iPos.iR(), color );
    }

    private void setColor( int iC, int iR, int color ) { // 0 if transparent, 1 if white, 2 if black
    	int colorNow = getColor(iC, iR);
    	if (color == colorNow){
    		return;
    	} else if (colorNow != 0) {
    		if (color != 0) {
    			System.err.println("Error: trying to place one piece on another");
    		} else {
    			position -= (long) (color << ((iC/2 + iR*4)*2));
    		}
    	} else if (color == 0) {
    		position += (long) (color << ((iC/2 + iR*4)*2));
    	} else {
    		System.err.println("Error: current color is out of bounds at ("+iC+","+iR+")");
    	}
    }

    @Override
    public void setPlayer(int iPlayer) { // Only 1 or 2 are valid
        if ( !(0<iPlayer && iPlayer<3) ) {
            System.err.println("Error(Connect4Position::setPlayer): iPlayer ("+iPlayer+") out of bounds!!!");
        } else {
            int  currentPlayer = getPlayer();
            if ( currentPlayer != iPlayer ) {
                if (iPlayer == 1) {
                	for (int i = 0; i < 32; i++) {
                		int spot = (int) ((position >>> 2*i) & 3L);
                		if (spot == 3) {
                			position -= (1 << (2*i));
                		} else if (spot == 2) {
                			return;
                		}
                	}
                } else if( iPlayer == 2) {
                	for (int i = 0; i < 32; i++) {
                		int spot = (int) ((position >>> 2*i) & 3L);
                		if (spot == 2) {
                			position += (1 << (2*i));
                		} else if (spot == 3) {
                			return;
                		}
                	}
                }
            }
        }
    }
    public boolean isValidMove(int i0C, int i0R, int i1C, int i1R){
    	int iPlayer = getPlayer();
    	int rowsMoved = i0R - i1R;
    	int pieceColor = getColor(i0C, i0R);
    	if (pieceColor != iPlayer){
			System.err.println("Error: Trying to move a piece that doesn't belong to this player");
		} else{
			if (getColor(i1C, i1R) == 0) {
	        	if(iPlayer == 1) {
	        		if(rowsMoved <= 0){
	        			return false;
	        		} else {
	        			if(rowsMoved == 1) return true;
	        			else if(rowsMoved == 2){
	        				int jumpediC = (i0C + i1C)/2;
	        				int jumpediR = (i0R + i1C)/2;
	        				if (getColor(jumpediC, jumpediR) == 2) {
	        					return true;
	        				} else return false;
	        			}
	        		}
	        	} else if(iPlayer == 2) {
	        		if(rowsMoved >= 0){
	        			return false;
	        		} else {
	        			if(rowsMoved == 1) return true;
	        			else if(rowsMoved == 2){
	        				int jumpediC = (i0C + i1C)/2;
	        				int jumpediR = (i0R + i1C)/2;
	        				if (getColor(jumpediC, jumpediR) == 1) {
	        					return true;
	        				} else return false;
	        			}
	        		}
	        	}
	        }
		}
    	return false;
    }
    
    @Override
    public int getPlayer() {
    	for (int i = 0; i < 32; i++) {
    		int spot = (int) ((position >>> 2*i) & 3L);
    		if      (spot == 3) return 2;
    		else if (spot == 2) return 1;
    	}
    	System.err.println("Error: Checked 32 spots, didn't find a 2 or 3, no player found");
    	return 1;
    }

    @Override
    public int isWinner(InterfaceIterator iPos) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int isWinner() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public float valuePosition() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void reset() {
        // TODO Auto-generated method stub

    }

    @Override
    public int getChipCount() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getChipCount(InterfaceIterator iPos) {
        // TODO Auto-generated method stub
        return 0;
    }

}
