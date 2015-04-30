package c_minimax;

//author: Gary Kalmanovich; rights reserved

public class CheckersPosition implements InterfacePosition {

    // This implementation is designed for at most 8 columns by 8 rows
    // It packs the entire position into a single long

    // Rightmost 51=cap(log2(3^32)) bits are used to store a 32 digit trinary (0..2) number
    // Leftmost 1 bit stores the player 1 or 2

    // This is the starting position
    // The  last 3 rows are filled by Player 1
    // The first 3 rows are filled by Player 2
    // It's Player 1's turn.
    private final static long STARTING_POSITION = 2779528540417001L;

    private long position = STARTING_POSITION;
    private int nC = 0;
    private int nR = 0;
    static private long[] powerOfThree = new long[33]; // 32  +1(for player)

    CheckersPosition( int nC, int nR) {
        position = STARTING_POSITION;
        this.nC = nC;
        this.nR = nR;
        if (powerOfThree[0] != 1) setPowerOfThree();
    }

    CheckersPosition( InterfacePosition pos ) {
        position = pos.getRawPosition();
        nC       = pos.nC();
        nR       = pos.nR();
        if (powerOfThree[0] != 1) setPowerOfThree();
    }

    private void setPowerOfThree() {
        powerOfThree[0] = 1;
        for( int iPow = 1 ; iPow < 33 ; iPow++ ) {
            powerOfThree[iPow] = 3*powerOfThree[iPow-1];
        }
    }

    @Override public int nC() { return nC; }
    @Override public int nR() { return nR; }

    @Override public long getRawPosition() { return position; }

    @Override
    public int getColor( InterfaceIterator iPos ) { // 0 if transparent, 1 if white, 2 if black
        return getColor( iPos.iC(), iPos.iR() );
    }

    private int getColor( int iC, int iR ) { // 0 if transparent, 1 if white, 2 if black
        int retVal = (int) ( ( position / powerOfThree[(8*iR+iC)/2] ) % 3 );
        return (retVal < 0) ? retVal+3 : retVal;
    }

    @Override
    public void setColor( InterfaceIterator iPos, int color ) { // 0 if transparent, 1 if white, 2 if black

        // Initial Position
        int iC = iPos.iC();
        int iR = iPos.iR();
        // Destination Position
        int dC = iPos.dC();
        int dR = iPos.dR();
        // Difference in Position
        int diffC = dC - iC;
        int diffR = dR - iR;

        // Remove the checker from the source position
        setColor( iC, iR, 0);

        // If there's a space inbetween, remove the checker there
        if (Math.abs(diffC) > 1 && Math.abs(diffR) > 1) {
            setColor( (diffC/2 + iC), (diffR/2 + iR), 0);
        }

        // Add the checker to the new position
        setColor( dC, dR, color);
    }

    private void setColor( int iC, int iR, int color ) { // 0 if transparent, 1 if white, 2 if black
        int oldColor = getColor(iC,iR);
        position += (color-oldColor) * powerOfThree[(8*iR+iC)/2];
    }

    @Override
    public void setPlayer(int iPlayer) { // Only 1 or 2 are valid
        if ( !(0<iPlayer && iPlayer<3) ) {
            System.err.println("Error(Connect4Position::setPlayer): iPlayer ("+iPlayer+") out of bounds!!!");
        } else {
            setColor(64,0,iPlayer);
        }
    }

    @Override
    public int getPlayer() {
        return getColor(64,0);
    }

    @Override
    public int isWinner(InterfaceIterator iPos) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int isWinner() {
        int topRow = 0;
        int bottomRow = nR - 1;

        // Check if Player 1 won
        for (int iC = 1; iC < nC; iC+=2) {
            if (getColor(iC, topRow) == 1)  return 1;
        }

        // Check if Player 2 won
        for (int iC = 0; iC < nC; iC+=2) {
            if (getColor(iC, bottomRow) == 2) return 2;
        }

        // Otherwise the game has yet to end
        return -1;

        // TODO Check for legal draw (highly improbable)
    }

    @Override
    public float valuePosition() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void reset() {
        position = STARTING_POSITION;
    }

    public void clear() {
        position = 0;
        setPlayer(1);
    }

    @Override
    public int getChipCount() {
        int chipCount = 0;
        for (int iR = 0; iR < 8; iR++) {
            for (int iC = (iR+1)%2; iC < 8; iC+=2) {
                if (getColor(iC, iR) > 0) chipCount++;
            }
        }
        return chipCount;
    }

    @Override
    public int getChipCount(InterfaceIterator iPos) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        for (int iR = 0; iR < 8; iR++) {
            for (int iC = (iR+1)%2; iC < 8; iC+=2) {
                output.append("(" + iC + ", " + iR + ") = " + getColor(iC, iR)+"\n");
            }
        }
        return output.toString();
    }

    public void printMe() {
        System.out.println(getChipCount() + " checkers on the board!");
        System.out.println(this.toString());
    }

    public static void test(String[] args) {// testMe(String[] args) {// Unit test (incomplete)
        CheckersPosition position = new CheckersPosition(8,8);

        for (int iR = 0; iR < 8; iR++) {
            for (int iC = (iR+1)%2; iC < 8; iC+=2) {
                System.out.println("(" + iC + ", " + iR + ") = " + position.getColor(iC, iR));
            }
        }
        System.out.println("-------------------------");

        position.setColor(0, 0, 1);
        position.setColor(1, 0, 0);
        position.setColor(3, 0, 1);
        position.setColor(4, 1, 1);
        position.setPlayer(1);
        for (int iR = 0; iR < 8; iR++) {
            for (int iC = (iR+1)%2; iC < 8; iC+=2) {
                System.out.println("(" + iC + ", " + iR + ") = " + position.getColor(iC, iR));
            }
        }
        System.out.println("player: " + position.getPlayer());
        System.out.println("-------------------------");

        position.setColor(1, 0, 2);
        position.setColor(3, 0, 2);
        position.setColor(5, 0, 2);
        position.setColor(7, 0, 2);
        position.setColor(0, 1, 2);
        position.setColor(2, 1, 2);
        position.setColor(4, 1, 2);
        position.setColor(6, 1, 2);
        position.setColor(1, 2, 2);
        position.setColor(3, 2, 2);
        position.setColor(5, 2, 2);
        position.setColor(7, 2, 2);
        position.setColor(0, 5, 1);
        position.setColor(2, 5, 1);
        position.setColor(4, 5, 1);
        position.setColor(6, 5, 1);
        position.setColor(1, 6, 1);
        position.setColor(3, 6, 1);
        position.setColor(5, 6, 1);
        position.setColor(7, 6, 1);
        position.setColor(0, 7, 1);
        position.setColor(2, 7, 1);
        position.setColor(4, 7, 1);
        position.setColor(6, 7, 1);
        position.setPlayer(2);
        for (int iR = 0; iR < 8; iR++) {
            for (int iC = (iR+1)%2; iC < 8; iC+=2) {
                System.out.println("(" + iC + ", " + iR + ") = " + position.getColor(iC, iR));
            }
        }
        System.out.println("player: " + position.getPlayer());
    }
}
