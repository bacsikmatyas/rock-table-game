package rocktable.state;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

/**
 * The class that represents the state of the Rock Table game.
 */
@Data
@Slf4j
public class RockTableState {

    /**
     * The array representing the initial state of the table.
     */
    public static final int[][] INITIAL = {
            {1, 0, 0, 0, 0},
            {1, 1, 1, 0, 0},
            {0, 0, 1, 0, 0},
            {0, 1, 0, 1, 1},
            {1, 1, 0, 0, 0}
    };


    /**
     * Indicates that which is the active player.
     */
    private int activePlayer;

    /**
     * The array storing the amount of rocks each player have.
     */
    @Setter(AccessLevel.NONE)
    private int[] rocks = {0, 0};

    /**
     * The number of the chosen row.
     */
    @Setter(AccessLevel.NONE)
    private int chosenRow = -1;

    /**
     * Indicates that if the active player chose row in this turn or not.
     */
    @Setter(AccessLevel.NONE)
    private boolean isRowChosen;

    /**
     * Indicates that if the active player picked up at
     * least a rock in this turn or not.
     */
    @Setter(AccessLevel.NONE)
    private boolean isPickedUp;

    /**
     * The left-most column where the active player picked up rock from.
     */
    @Setter(AccessLevel.NONE)
    private int leftMostCol = 4;

    /**
     * The array represents the current state of the table.
     */
    @Setter(AccessLevel.NONE)
    private int[][] table;

    /**
     * Creates a {@code RockTableState} object representing the (original)
     * initial state of the game.
     */
    public RockTableState(){
        this.table=INITIAL;
    }


    /**
     * Creates a {@code RocktableState} object that is initialized it with
     * the specified array.
     *
     * @param a an array of size 5&#xd7;5 representing the initial configuration
     *          of the table
     * @throws IllegalArgumentException if the array does not represent a valid
     *                                  configuration of the table
     */
    public RockTableState(int[][] a){
        if (!isValidTable(a)){
            throw new IllegalArgumentException();
        }

        this.table = a;
    }

    private boolean isValidTable(int[][] a){
        if (a == null || a.length != 5 || sumOfTable(a) == 0) {
            return false;
        }
        for (int[] ints : a) {
            for (int j = 0; j < a.length; j++) {
                if (ints[j] != 0 && ints[j] != 1) {
                    return false;
                }
            }
        }
        return true;
    }

    private int sumOfTable(int[][] a){
        int sum = 0;
        for (int[] ints : a) {
            for (int j = 0; j < a.length; j++) {
                sum += ints[j];
            }
        }
        return sum;
    }

    /**
     * Checks whether the game is finished.
     *
     * @return {@code true} if the game is finished,
     * {@code false} otherwise
     */
    public boolean isFinished(){
        return sumOfTable(table) == 0;
    }


    /**
     * Sets the {@code chosenRow} to the specified {@code n} value.
     *
     * @param n the row that the active player wants to chose
     * @throws IllegalArgumentException if the active player try to choose a row that
     * is not valid
     */
    public void setChosenRow(int n){
        if (!isChoosableRow(n)){
            throw new IllegalArgumentException();
        }
        else{
            chosenRow=n;
            isRowChosen=true;
            log.info("Player{} chose row {}.",activePlayer+1,chosenRow);
        }
    }

    /**
     * Checks whether the active player can choose a row or not.
     *
     * @param n the row that the active player want to check
     * @return {@code true} if the active player can choose the {@code n} row,
     * {@code false} otherwise
     */
    public boolean isChoosableRow(int n){
        if (isFinished()){
            return false;
        }
        if (isRowChosen){
            return false;
        }
        if (n > 4 || n < 0){
            return false;
        }
        for (int i = 0; i < table.length; i++) {
            if (table[n][i] == 1){
                return true;
            }
        }
        return false;
    }

    /**
     * Checks whether the active player can pick up rock from the {@code n}
     * position in the {@code chosenRow or not.
     *
     * @param n the position(column) that the active player wants to check
     * @return {@code true} if the active player can pick up the rock int the {@code n} position,
     * {@code false} otherwise
     */
    public boolean isPickupable(int n){
        if (isFinished()){
            return false;
        }
        if (!isRowChosen){
            return false;
        }
        if (n > 4 || n < 0){
            return false;
        }
        if (table[chosenRow][n]==0){
            return false;
        }
        return true;
    }

    /**
     * Picks up the rock in the {@code chosenRow}
     * at the specified {@code n} position.
     *
     * @param n the position(column) of the rock that the active player wants to pick up
     * @throws IllegalArgumentException if the active player tries to choose a position that
     * is not valid(can't pick up a rock)
     */
    public void pickupRock(int n){
        if (isPickupable(n)){
            rocks[activePlayer]++;
            table[chosenRow][n]=0;
            if (n<leftMostCol){
                leftMostCol=n;
            }
            isPickedUp=true;
            log.info("Player{} picking up from ({};{}), rocks: {}",activePlayer+1,chosenRow,n,rocks[activePlayer]);
        }
        else {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Checks whether the active player can place a rock to the {@code n}
     * position in the {@code chosenRow or not.
     *
     * @param n the position(column) that the active player wants to check
     * @return {@code true} if the active player can place a rock int the {@code n} position,
     * {@code false} otherwise
     */
    public boolean isPlaceable(int n){
        if (isFinished()){
            return false;
        }
        if (rocks[activePlayer]==0){
            return false;
        }
        if (n > 4 || n < 0){
            return false;
        }
        if (n<=leftMostCol){
            return false;
        }
        if (table[chosenRow][n]==1){
            return false;
        }
        return true;
    }

    /**
     * Places a rock in the {@code chosenRow}
     * at the specified {@code n} position.
     *
     * @param n the position(column) where the active player wants to place a rock
     * @throws IllegalArgumentException if the active player tries to choose a position that
     * is not valid(can't place a rock)
     */
    public void placeRock(int n){
        if (isPlaceable(n)){
            table[chosenRow][n]=1;
            rocks[activePlayer]--;
            log.info("Player{} placing down to ({};{}), rocks: {}",activePlayer+1,chosenRow,n,rocks[activePlayer]);
        }
        else {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Checks whether the active player can end the turn or not.
     *
     * @return {@code true} if the active player can end the turn,
     * {@code false} otherwise
     */
    public boolean canBeEnded(){
        return isPickedUp;
    }

    /**
     * The active player ends the turn.
     */
    public void endTurn(){
        if (canBeEnded()){
            switch (activePlayer){
                case 0:
                    activePlayer=1;
                    break;
                case 1:
                    activePlayer=0;
                    break;
            }
            log.info("Turn ended!");
            if (isFinished()){
                log.info("Player"+activePlayer+1+" won!");
            }
            isPickedUp=false;
            isRowChosen=false;
            leftMostCol=4;
            chosenRow=-1;
        }
        else {
            log.info("The turn cannot be ended!");
        }

    }

    /**
     * Create a {@code String} representation of the current game.
     *
     * @return the {@code String} representation of the current state of the game
     */
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("Current state:\n");
        for (int[] ints : table) {
            for (int j = 0; j < table.length; j++) {
                sb.append(ints[j]).append(' ');
            }
            sb.append('\n');
        }
        sb.append("Active player: ").append(activePlayer+1).append('\n');
        sb.append("Chosen row: ").append(chosenRow).append('\n');
        sb.append("Rocks: ").append(rocks[activePlayer]).append('\n');
        return sb.toString();
    }


}
