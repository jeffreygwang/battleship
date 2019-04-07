package BattleshipGame;

/**
 *
 * @author JeffreyWang
 */
public class Ship {
    private int startX;
    private int startY;
    private int endX;
    private int endY;
    public Ship(int startX, int startY, int endX, int endY) { // creates ship object
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
    }
    public int[] getShipInfo() { // returns array of ship info
        int[] result = {startX, startY, endX, endY};
        return result;
    }
    public void resetShip() { // resets ship object
        startX = 0;
        startY = 0;
        endX = 0;
        endY = 0;
    }
}
