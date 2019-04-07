package BattleshipGame;
import java.util.Scanner;
import java.util.Random;
import java.util.ArrayList;
/**
 *
 * @author JeffreyWang
 * Because this program is in one upload, SUPPORTING CLASSES - Grid and Ship - are commented in at the end.
 * 
 */ 

public class BattleshipRunner {
    public static void main(String[] args) {
        Grid p1Grid = new Grid();
        Grid p2Grid = new Grid(); // establishes grids
        
        printInstructions(p1Grid);
        
        Ship[] player1ships = getShip(p1Grid);
        Ship[] player2ships = getCompShips(p2Grid);
        
        boardsAndFirstMove(p1Grid, p2Grid);
        
        boolean[] targetAcquired = {false};
        int[] directArray = {0, 1, 2, 3};
        int[] infoArray = {0,0,0,0};
        
        comMove(directArray, infoArray, targetAcquired, p1Grid);
        p1Grid.printBoard();
        newLines();
        
        while (true) {
            System.out.println("\nPlayer 1 turn.");
            p1move(p2Grid);
            p2Grid.printOpponentBoard();
            if (printShipsSunk(1, p2Grid, player2ships).size() == 5) {
                System.out.println("Player 1 (YOU) won!");
                break;
            }
            comMove(directArray, infoArray, targetAcquired, p1Grid);
            p1Grid.printBoard();
            if (CheckOnePointShipSunk(infoArray[0], infoArray[1], p1Grid, player1ships)) {
                    directArray[0] = 0;
                    directArray[1] = 1;
                    directArray[2] = 2;
                    directArray[3] = 3;
                    infoArray[0] = 0;
                    infoArray[1] = 0;
                    infoArray[2] = 0;
                    infoArray[3] = 0;
                    targetAcquired[0] = false; // this is broken
                    // the functionality of computer move is broken   
                } 
             if (printShipsSunk(2, p1Grid, player1ships).size() == 5) {
                System.out.println("COMPUTER WON!");
                break;
            } 
        }
    }
    public static void printInstructions(Grid p1Grid) {
        System.out.println("GAME OF BATTLESHIP.\nYou will enter your ships by entering the end coordinates of the"
                + "ships. You will play against a computer, which will automatically pick its ships. Good luck!\n");
        System.out.println("This is the board: ");
        p1Grid.printBoard();
    }
    public static void boardsAndFirstMove(Grid p1Grid, Grid p2Grid) {
        printWhoBoard(1);
        p1Grid.printBoard();
        printWhoBoard(2); // prints initial boards
        p2Grid.printOpponentBoard();
        System.out.println("\nPlayer 1 turn."); // start game
        p1move(p2Grid);
        System.out.println("This is the new opponent board: ");
        p2Grid.printOpponentBoard();
        newLines();
    }
    public static void newLines() {
        System.out.print("\n");
    }
    /*
    p1Move:
    takes in coordinates, checks if valid or not; if not valid, continues prompting until it is valid
    checks state of point, returns what happened
    */
    public static void p1move(Grid p2Grid) {
        Scanner console = new Scanner(System.in);
        String[] StringCoords = new String[2];
        boolean isValidEntry = false;
        while (!isValidEntry) {
            System.out.println("\nWhere would you like to move? Enter \"x y\"");
            String[] temp = console.nextLine().split(" ");
            StringCoords=temp;
            if (!checkMoveConstruction(temp, p2Grid)) 
                System.out.println("That is an invalid entry (could be due to formatting, incompleteness, or "
                        + "previous entry of coordinate. Try again.");
            else
                isValidEntry = true;
        }
        int[] coords = {Integer.parseInt(StringCoords[0]), Integer.parseInt(StringCoords[1])};
        // 0 or 1
        switch(p2Grid.getState(coords[0], coords[1])) {
            case 0: 
                System.out.println("You missed.");
                p2Grid.alterGrid(coords[0], coords[1], 3);
                break;
            case 1:
                System.out.println("You hit.");
                p2Grid.alterGrid(coords[0], coords[1], 2);
        }
    }
    // prints out all ships that have been sunk; returns an arrayList of all ships that have been sunk
    public static ArrayList<Integer> printShipsSunk(int player, Grid playerGrid, Ship[] ships) {
        boolean anyShipsAreSunk = false;
        System.out.print("\nThe following ships have been sunk by Player " + player + ": ");
        ArrayList<Integer> shipsSunk = new ArrayList<>();
        for (int i = 0; i < ships.length; i++) {
            int[] array = ships[i].getShipInfo();
            int length = 0;
            boolean shipIsSunk = true;
            if (array[0] == array[2]) {
                length = array[3] - array[1] + 1;
                for (int j = array[1]; j <= array[3]; j++) {
                    if (playerGrid.getState(array[0], j) != 2)
                        shipIsSunk = false;
                }
            }
            else {
                length = array[2] - array[0] + 1;
                for (int j = array[0]; j<= array[2]; j++) {
                    if (playerGrid.getState(j, array[1]) != 2)
                        shipIsSunk = false;
                }
            }
            if (shipIsSunk) {
                anyShipsAreSunk = true;
                shipsSunk.add(length);
                System.out.print(length + " ");
            }
        }
        if (!anyShipsAreSunk)
            System.out.print("none");
        System.out.println();
        return shipsSunk;
    }
    /*
    comMove:
    Essentially, there is a "HUNT" mode or a "HIT" mode. the HUNT mode guesses randomly until it hits something.
    the HIT mode is when a ship has been hit. BASIC PREMISE: By checking every direction untl hitting a 3/boundary,
    a ship MUST be sunk.
    
    parameters:
    1. directionArray[]: contains an array of 0,1,2,3 (NORTH EAST SOUTH WEST)
    that represents the directions that need to be checked/are being checked. if 1 is still in the array, then 
    east has not been exhausted. once a direction is exhausted, it becomes -1
    2. infoArray[]: contains four elements (once a point has been hit). 0 and 1 are the original x and y coords of the hit point.
    2 and 3 are the "current points." basically, if the bottom of a ship of length 5 is hit at (5,0) and the program
    is checking upward, then it can only do this one unit at a time. the latest unit to be checked is the current.
    3. targetAcq[] a boolean array storing only 1 value to say whether program is on HUNT or HIT mode. more below.
    4. Grid: self-explanatory. Grid to operate on. 
    HUNT: randomly guess points (unless already 2/3 -- hit or mis). Once a point has been found, the infoArray is 
    set with those coordinates, and targetAcq[0] is set to true.
    HIT: go N->E->S->W until all directions are exhausted or ship is sunk. Once ship is sunk, reset. 
    */
    public static void comMove(int[] directionArray, int[] infoArray, boolean[] targetAcq, Grid p1Grid) {
        Random rnd = new Random(); 
        if (targetAcq[0]) {
            for (int j = 0; j < 4; j++) {
                switch (directionArray[j]) {
                    case -1: 
                        break;
                    case 0:  // lazy operation
                        while ((inBoundary(infoArray[2], infoArray[3] - 1)) && (p1Grid.getState(infoArray[2], infoArray[3] - 1) == 2)) {
                            infoArray[3]--;
                        }
                        // in the case that part of 1 ship has been revealed by a neighbouring ship whose guesses strayed over
                        // this will go past those.
                        if (!inBoundary(infoArray[2], infoArray[3] - 1) || (p1Grid.getState(infoArray[2], infoArray[3] - 1) == 3)) {
                            directionArray[0] = -1;
                            infoArray[2] = infoArray[0];
                            infoArray[3] = infoArray[1];
                            break;
                        }
                        // if the next coordinate in one direction is 3 or a boundary, stop going this direction
                        switch (p1Grid.getState(infoArray[2], infoArray[3] - 1)) {
                            case 0: // empty water, direction exhausted
                                p1Grid.alterGrid(infoArray[2], infoArray[3] - 1, 3);
                                infoArray[2] = infoArray[0];
                                infoArray[3] = infoArray[1];
                                directionArray[0] = -1;
                                return;
                            case 1: // ship has been hit, advance my infoArray (contains current coords) and check if one after the guess 
                                // is already a 3, if so, direction is exhausted. 
                                {
                                    p1Grid.alterGrid(infoArray[2], infoArray[3] - 1, 2);
                                    int X = infoArray[2];
                                    int Y = infoArray[3];
                                    infoArray[3] -= 1;
                                    if ((p1Grid.getState(X, Y-1) == 3)) {
                                        directionArray[0] = -1;
                                        infoArray[2] = infoArray[0];
                                        infoArray[3] = infoArray[1];
                                    }       
                                    return;
                                }
                            default: // shouldn't reach here
                                System.out.println("FATAL ERROR!!!");
                        }
                    case 1:   
                        while ((inBoundary(infoArray[2] + 1, infoArray[3])) && (p1Grid.getState(infoArray[2] + 1, infoArray[3]) == 2)) {
                            infoArray[2]++;
                        }
                        if ((!inBoundary(infoArray[2] + 1, infoArray[3])) || (p1Grid.getState(infoArray[2] + 1, infoArray[3]) == 3)) {
                            directionArray[1] = -1;
                            infoArray[2] = infoArray[0];
                            infoArray[3] = infoArray[1];
                            break;
                        }
                        switch (p1Grid.getState(infoArray[2] + 1, infoArray[3])) {
                            case 0:
                                p1Grid.alterGrid(infoArray[2] + 1, infoArray[3], 3);
                                infoArray[2] = infoArray[0];
                                infoArray[3] = infoArray[1];
                                directionArray[1] = -1;
                                return;
                            case 1:
                                {
                                    p1Grid.alterGrid(infoArray[2] + 1, infoArray[3], 2);
                                    int X = infoArray[2];
                                    int Y = infoArray[3];
                                    infoArray[2] += 1;
                                    if ((p1Grid.getState(X + 1,Y) == 3)) {
                                        directionArray[1] = -1;
                                        infoArray[2] = infoArray[0];
                                        infoArray[3] = infoArray[1];
                                    }       
                                    return;
                                }
                            default:
                                System.out.println("FATAL ERROR!!!");
                        }
                    case 2:
                        while ((inBoundary(infoArray[2], infoArray[3] + 1)) && (p1Grid.getState(infoArray[2], infoArray[3] + 1) == 2)) {
                            infoArray[3]++;
                        }
                        if (!inBoundary(infoArray[2], infoArray[3] + 1) || (p1Grid.getState(infoArray[2], infoArray[3] + 1) == 3)) {
                            directionArray[2] = -1;
                            infoArray[2] = infoArray[0];
                            infoArray[3] = infoArray[1];
                            break;
                        }
                        switch (p1Grid.getState(infoArray[2], infoArray[3] + 1)) {
                            case 0:
                                p1Grid.alterGrid(infoArray[2], infoArray[3] + 1, 3);
                                infoArray[2] = infoArray[0];
                                infoArray[3] = infoArray[1];
                                directionArray[2] = -1;
                                return;
                            case 1:
                                {
                                    p1Grid.alterGrid(infoArray[2], infoArray[3] + 1, 2);
                                    int X = infoArray[2];
                                    int Y = infoArray[3];
                                    infoArray[3] += 1;
                                    if ((p1Grid.getState(X, Y+1) == 3)) {
                                        directionArray[2] = -1;
                                        infoArray[2] = infoArray[0];
                                        infoArray[3] = infoArray[1];
                                    }       
                                    return;
                                }
                            default:
                                System.out.println("FATAL ERROR!!!");
                        }
                    case 3:
                        while ((inBoundary(infoArray[2] - 1, infoArray[3])) && (p1Grid.getState(infoArray[2] - 1, infoArray[3]) == 2)) {
                            infoArray[2]--;
                        }
                        if ((!inBoundary(infoArray[2] - 1, infoArray[3])) || (p1Grid.getState(infoArray[2] - 1, infoArray[3]) == 3)) {
                            directionArray[3] = -1;
                            infoArray[2] = infoArray[0];
                            infoArray[3] = infoArray[1];
                            break;
                        }
                        switch (p1Grid.getState(infoArray[2] - 1, infoArray[3])) {
                            case 0:
                                p1Grid.alterGrid(infoArray[2] - 1, infoArray[3], 3);
                                infoArray[2] = infoArray[0];
                                infoArray[3] = infoArray[1];
                                directionArray[3] = -1;
                                return;
                            case 1:
                                {
                                    p1Grid.alterGrid(infoArray[2] - 1, infoArray[3], 2);
                                    int X = infoArray[2];
                                    int Y = infoArray[3];
                                    infoArray[2] -= 1;
                                    if ((p1Grid.getState(X - 1,Y) == 3)) {
                                        directionArray[3] = -1;
                                        infoArray[2] = infoArray[0];
                                        infoArray[3] = infoArray[1];
                                    }       
                                    return;
                                }
                            default:
                                System.out.println("FATAL ERROR!!!");
                        }
                    default:
                        System.out.println("FATAL ERROR NUMBER 2!!!!!");
                }
            }
        }
        if (!targetAcq[0]) { // HUNT mode
            int xCoord = rnd.nextInt(10);
            int yCoord = rnd.nextInt(10);
            while ((p1Grid.getState(xCoord, yCoord) == 2) || (p1Grid.getState(xCoord, yCoord) == 3)) {
                xCoord = rnd.nextInt(10);
                yCoord = rnd.nextInt(10);
            } // to avoid double guessing
            if (p1Grid.getState(xCoord, yCoord) == 1) { // to determine which directions are feasible to go to
                targetAcq[0] = true;
                infoArray[0] = xCoord; 
                infoArray[1] = yCoord;
                infoArray[2] = xCoord;
                infoArray[3] = yCoord; 
                p1Grid.alterGrid(xCoord, yCoord, 2);
                if (!inBoundary(xCoord, yCoord-1))
                    directionArray[0] = -1; 
                else if ((p1Grid.getState(xCoord, yCoord-1) == 3))
                    directionArray[0] = -1; 
                if (!inBoundary(xCoord + 1, yCoord))
                    directionArray[1] = -1;
                else if ((p1Grid.getState(xCoord+1, yCoord) == 3)) 
                    directionArray[1] = -1;  
                if (!inBoundary(xCoord, yCoord + 1))
                    directionArray[2] = -1;
                else if ((p1Grid.getState(xCoord, yCoord + 1) == 3))
                    directionArray[2] = -1;  
                if (!inBoundary(xCoord - 1, yCoord))
                    directionArray[3] = -1;
                else if ((p1Grid.getState(xCoord - 1, yCoord) == 3))
                    directionArray[3] = -1;  
            }
            else {
                p1Grid.alterGrid(xCoord, yCoord, 3);
            }
        }
    }
    public static boolean inBoundary(int x, int y) { // checks if point is in boundary
        if ((x > 9) || (x < 0) || (y > 9) || (y < 0))
            return false;
        return true;
    }
    // given ONE point, check if the ship the point belongs to is sunk. 
    public static boolean CheckOnePointShipSunk(int x, int y, Grid playerGrid, Ship[] ships) { // for the dad way
        int shipPosInArray = 0;
       for (int j = 0; j < ships.length; j++) {
           int[] shipInfo = ships[j].getShipInfo();
           if (shipInfo[0] == shipInfo[2]) {
               for (int a = shipInfo[1]; a <= shipInfo[3]; a++) {
                   if ((x == shipInfo[0]) && (y == a))
                       shipPosInArray = j;
               }
           }
           if (shipInfo[1] == shipInfo[3]) {
               for (int q = shipInfo[0]; q <= shipInfo[2]; q++) {
                   if ((x == q) && (y == q))
                       shipPosInArray = j;
               }
           }
       }
       boolean shipIsSunk = true;
       int[] info = ships[shipPosInArray].getShipInfo();
       if (info[0] == info[2]) {
           for (int a = info[1]; a <= info[3]; a++) {
               if (playerGrid.getState(info[0], a) != 2)
                   shipIsSunk = false;
           }
       }
       if (info[1] == info[3]) {
           for (int w = info[0]; w <= info[2]; w++) {
               if (playerGrid.getState(w, info[1]) != 2)
                   shipIsSunk = false;
           }
       }
       if (shipIsSunk == true)
               return true;
       return false;
    }
    // checks to make sure the coords user enters works; no commas, extra/less characters, out of bounds numbers, repeats, etc. 
    public static boolean checkMoveConstruction(String[] array, Grid playerGrid) {
        if (array.length != 2) 
            return false;
        for (int i = 0; i < array.length; i++) {
            if (array[i].indexOf(",") > 0)
                return false;
            if (array[i].indexOf("(") > 0) // in case of (x y)
                return false;
            if ((Integer.parseInt(array[i]) < 0) || (Integer.parseInt(array[i]) > 9)) {
                return false;
            }
        }
        int x = Integer.parseInt(array[0]);
        int y = Integer.parseInt(array[1]);
        if ((playerGrid.getState(x, y) == 2) || (playerGrid.getState(x, y) == 3))
            return false;
        return true;
    }
    public static void printWhoBoard(int playNum) { // prints who's board it is
        System.out.println("\nPlayer " + playNum + "'s board.");
    }
    public static Ship[] getCompShips(Grid p2grid) { // part of user beginning process to get ships
        Random rnd = new Random();
        int[] array = new int[4];
        int[] shipLengths = {5, 4, 3, 3, 2};
        Ship[] p2Ships = new Ship[5];
        for (int i = 0; i < 5; i++) {
            array[0] = rnd.nextInt(10);
            array[1] = rnd.nextInt(10);
            int length = shipLengths[i] - 1;
            int temp = rnd.nextInt(2); // 0,1
            if (temp == 0) {
                if (array[0] + length <= 9) {
                    array[2] = array[0] + length;
                }
                else if (array[0] - length >= 0) {
                    array[2] = array[0] - length;
                }
                array[3] = array[1];
            }
            if (temp == 1) {
                if (array[1] + length <= 9) {
                    array[3] = array[1] + length;
                }
                else if (array[1] - length >= 0) {
                    array[3] = array[1] - length;
                }
                array[2] = array[0];
            }
            arrayOrder(array);
            p2Ships[i] = new Ship(array[0], array[1], array[2], array[3]);
            if (p2grid.checkShipOverlap(array)) {
                p2Ships[i].resetShip();
                i--;
            }
            else {
            p2grid.shipToBoard(p2Ships[i]); // can never break on first one, if laters dont work, will simply add prev.
            }
        }
        return p2Ships;
    }
    public static void arrayOrder(int[] array) { // orders ship coords to make sure things go from least to greatest
        int temp;
        if (array[0] == array[2]) {
            if (array[1] > array[3]) {
                temp = array[1];
                array[1] = array[3];
                array[3] = temp;
            }
        }
        if (array[1] == array[3]) {
            if (array[0] > array[2]) {
                temp = array[0];
                array[0] = array[2];
                array[2] = temp;
            }
        }
    }
    public static Ship[] getShip(Grid p1grid) { // method which gets the ships
        Ship[] p1Ships = new Ship[5];
        int[] shipLengths = {5, 4, 3, 3, 2};
        for (int i = 0; i < 5; i++) {
            String[] array = constructShip(shipLengths[i]); // goes through the constructship
            int[] intArray = new int[4];
            if (!checkShipConstruction(array, shipLengths[i])) {
                System.out.println("That is an invalid entry. Try again.");
                i--;
                continue;
            }
            for (int x = 0; x < 4; x++) {
                intArray[x] = Integer.parseInt(array[x]); // int array has the x1, y1, x2, y2
            }
            arrayOrder(intArray);
            p1Ships[i] = new Ship(intArray[0], intArray[1], intArray[2], intArray[3]);
            if (p1grid.checkShipOverlap(intArray)) { // true means it don't work
                System.out.println("There is ship overlap. Try again.");
                p1Ships[i].resetShip();
                i--;
            }
            p1grid.shipToBoard(p1Ships[i]);
        }
        return p1Ships;
    }
    public static String[] constructShip(int shipLength) { // prints prompt for user to enter ship coordinates 
        Scanner console = new Scanner(System.in);
            System.out.println("Enter in where Ship of length " + shipLength + " goes as following:"
                    + " \"startX startY endX endY\"");
            String temp = console.nextLine();
            return temp.split(" ");
    }
    public static boolean checkShipConstruction(String[] array, int shipLength) { // checks validity of user entering of ship coords
        if (array.length != 4)
            return false;
        for (int i = 0; i < 4; i++) {
            if (array[i].indexOf(",") > 0)
                return false;
            if (Integer.parseInt(array[i]) > 9 || Integer.parseInt(array[i]) < 0) 
                return false;
        }
        if ((!array[0].equals(array[2])) && (!array[1].equals(array[3])))
            return false;
        int coordLength;
            if (array[0].equals(array[2]))
                coordLength = Math.abs(Integer.parseInt(array[3]) - Integer.parseInt(array[1])) + 1;
            else 
                coordLength = Math.abs(Integer.parseInt(array[2]) - Integer.parseInt(array[0])) + 1;
            if (coordLength != shipLength)
                return false;
        return true;
    }
}


// SUPPORTING CLASSES - GRID AND SHIP - ARE COMMENTED IN BELOW
// The one below is Grid.

/*
package BattleshipGame;

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

*/

// The following class is Ship. 

/*
package BattleshipGame;

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

*/