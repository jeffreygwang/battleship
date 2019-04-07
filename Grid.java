package BattleshipGame;

/**
 *
 * @author JeffreyWang 
 */
public class Grid {
    int[][] plot;
    
    public Grid() { // Creates a Grid object 
        plot = new int[10][10]; // "0" = water, 1 = "ship", 2 = "hit", 3 = "miss"
    }
    public void alterGrid(int x, int y, int newState) { // changes a coordinate to a new state
        plot[x][y] = newState;
        if (newState == 2 || newState == 3) 
            System.out.println("\n(" + x + ", " + y + ") became " + newState);
    }
    public int getState(int x, int y) {
        return plot[x][y];
    }

    // given a ship object, prints unto board (for the initial process of choosing ships)
    public void shipToBoard(Ship ship) { // p1Grid.shipToBoard
        int[] shipInfo = ship.getShipInfo(); // {startX, startY, endX, endY}
        if (shipInfo[0] == shipInfo[2]) {
            for (int i = shipInfo[1]; i <= shipInfo[3]; i++) {
                alterGrid(shipInfo[0], i, 1);
            }
        }
        if (shipInfo[1] == shipInfo[3]) {
            for (int i = shipInfo[0]; i <= shipInfo[2]; i++) {
                alterGrid(i, shipInfo[1], 1);
            }
        }
    }
 
    public void printBoard() {
        System.out.println("  | 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9 |");
        System.out.println("--|---+---+---+---+---+---+---+---+---+---+");
        for (int i = 0; i < 10; i++) {
            System.out.print(i + " | ");
            for (int x = 0; x < 10; x++) {
                System.out.print(plot[x][i] + " | ");
            }
            System.out.println("\n--|---+---+---+---+---+---+---+---+---+---+");
        }
    }
    // prints board WITHOUT printing where ships are
    public void printOpponentBoard() {
        System.out.println("  | 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9 |");
        System.out.println("--|---+---+---+---+---+---+---+---+---+---+");
        for (int i = 0; i < 10; i++) {
            System.out.print(i + " | ");
            for (int x = 0; x < 10; x++) {
                if ((plot[x][i] == 0) || (plot[x][i] == 2) || (plot[x][i] == 3))
                System.out.print(plot[x][i] + " | ");
                if (plot[x][i] == 1)
                    System.out.print("0" + " | ");
            }
            System.out.println("\n--|---+---+---+---+---+---+---+---+---+---+");
        }
    }
    // checks if ships overlap (for initial ship creation process)
    public boolean checkShipOverlap(int[] array) {
        if (array[0] == array[2]) {
            for (int i = array[1]; i <= array[3]; i++) {
                if (plot[array[0]][i] == 1)
                    return true;
            }
        }
        if (array[1] == array[3]) {
            for (int i = array[0]; i <= array[2]; i++) {
                if (plot[i][array[1]] == 1) 
                    return true;
            }
        }
        return false; 
    }
}

