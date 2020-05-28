package rocktable.state;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RockTableStateTest {

    @Test
    void testIsFinished() {
        int[][] a = {{0,0,0,0,1},
                    {0,0,0,0,0},
                    {0,0,0,0,0},
                    {0,0,0,0,0},
                    {0,0,0,0,0}};
        RockTableState test = new RockTableState(a);

        test.setChosenRow(0);
        test.pickupRock(4);

        assertFalse(new RockTableState().isFinished());
        assertTrue(test.isFinished());

    }

    @Test
    void testSetChosenRow() {
        int[][] a = {{0,0,0,0,1},
                {0,0,0,0,0},
                {0,0,0,0,0},
                {0,0,1,0,0},
                {0,0,0,0,0}};
        RockTableState test = new RockTableState(a);

        assertEquals(-1,test.getChosenRow());
        assertThrows(IllegalArgumentException.class, () -> test.setChosenRow(6));//row does not exists
        assertThrows(IllegalArgumentException.class, () -> test.setChosenRow(-2));//row does not exists
        assertThrows(IllegalArgumentException.class, () -> test.setChosenRow(2));//empty row
        test.setChosenRow(0);//chosen row 0
        assertEquals(0,test.getChosenRow());
        test.pickupRock(4);//need to pick up before end turn
        assertThrows(IllegalArgumentException.class, () -> test.setChosenRow(3));//not an empty row, but you had already chosen this turn
        test.endTurn();
        test.setChosenRow(3);
        assertEquals(3,test.getChosenRow());
    }

    @Test
    void testIsChoosableRow() {
        int[][] a = {{0,0,0,0,1},
                {0,0,0,0,0},
                {0,0,0,0,0},
                {0,0,1,0,0},
                {0,0,0,0,0}};
        RockTableState test = new RockTableState(a);

        assertTrue(test.isChoosableRow(0));//not empty
        assertTrue(test.isChoosableRow(3));//not empty
        assertFalse(test.isChoosableRow(1));//empty
        assertFalse(test.isChoosableRow(2));//empty
        assertFalse(test.isChoosableRow(4));//empty

        assertFalse(test.isChoosableRow(-1));//non existent
        assertFalse(test.isChoosableRow(6));//non existent

        test.setChosenRow(0);
        assertFalse(test.isChoosableRow(3));//not empty, but the chosen row already set

    }

    @Test
    void testIsPickupable() {
        int[][] a = {{0,0,0,0,1},
                {0,0,0,0,0},
                {0,0,0,0,0},
                {0,0,1,0,0},
                {0,0,0,0,0}};
        RockTableState test = new RockTableState(a);

        assertFalse(test.isPickupable(4));//no chosen row

        test.setChosenRow(0);
        assertTrue(test.isPickupable(4));
        assertFalse(test.isPickupable(0));//empty
        assertFalse(test.isPickupable(1));//empty
        assertFalse(test.isPickupable(2));//empty
        assertFalse(test.isPickupable(3));//empty

        assertFalse(test.isPickupable(-1));//not valid
        assertFalse(test.isPickupable(6));//not valid
    }

    @Test
    void testPickupRock() {
        int[][] a = {{0,1,1,0,1},
                {0,0,0,0,0},
                {0,0,0,0,0},
                {0,0,1,0,0},
                {0,0,0,0,0}};
        RockTableState test = new RockTableState(a);

        assertThrows(IllegalArgumentException.class, () -> test.pickupRock(4));//choose row first
        test.setChosenRow(0);
        assertThrows(IllegalArgumentException.class, () -> test.pickupRock(-1));//not valid
        assertThrows(IllegalArgumentException.class, () -> test.pickupRock(6));//not valid
        assertThrows(IllegalArgumentException.class, () -> test.pickupRock(0));//empty
        assertThrows(IllegalArgumentException.class, () -> test.pickupRock(3));//empty

        assertFalse(test.isPickedUp());//not picked up yet

        int rockBefore = test.getRocks()[test.getActivePlayer()];
        test.pickupRock(4);
        assertEquals(rockBefore+1,test.getRocks()[test.getActivePlayer()]);//incr number of rocks
        assertEquals(4,test.getLeftMostCol());//change left most col if needed(yes)
        assertEquals(0,test.getTable()[test.getChosenRow()][4]);//set cell to 0
        assertTrue(test.isPickedUp());//true, because you picked up rock in your turn

        rockBefore = test.getRocks()[test.getActivePlayer()];
        test.pickupRock(1);
        assertEquals(rockBefore+1,test.getRocks()[test.getActivePlayer()]);//incr number of rocks
        assertEquals(1,test.getLeftMostCol());//change left most col if needed(yes)
        assertEquals(0,test.getTable()[test.getChosenRow()][1]);//set cell to 0
        assertTrue(test.isPickedUp());

        rockBefore = test.getRocks()[test.getActivePlayer()];
        test.pickupRock(2);
        assertEquals(rockBefore+1,test.getRocks()[test.getActivePlayer()]);//incr number of rocks
        assertEquals(1,test.getLeftMostCol());//change left most col if needed(no)
        assertEquals(0,test.getTable()[test.getChosenRow()][2]);//set cell to 0
        assertTrue(test.isPickedUp());
    }

    @Test
    void testIsPlaceable() {
        int[][] a = {{0,1,1,0,0},
                {0,0,0,0,0},
                {0,0,0,0,0},
                {0,0,1,0,0},
                {0,0,0,0,0}};
        RockTableState test = new RockTableState(a);

        assertFalse(test.isPlaceable(3));//choose row and pick up a rock at least
        test.setChosenRow(0);
        assertFalse(test.isPlaceable(3));//pick up a rock at least
        test.pickupRock(2);
        assertTrue(test.isPlaceable(3));//you can place rocks now to the right of the picked up rocks position
        assertFalse(test.isPlaceable(0));//it is to the left from that position, so its nono zone
        assertFalse(test.isPlaceable(2));//it is the left-most position where you picked up from, thats nono zone too

        assertFalse(test.isPlaceable(1));//cant place on top of another rock

        assertFalse(test.isPlaceable(-1));//not valid
        assertFalse(test.isPlaceable(6));

        test.placeRock(3);
        assertFalse(test.isPlaceable(4));//no rocks left to place


    }

    @Test
    void testPlaceRock() {
        int[][] a = {{0,1,1,0,0},
                {0,0,0,0,0},
                {0,0,0,0,0},
                {0,0,1,0,0},
                {0,0,0,0,0}};
        RockTableState test = new RockTableState(a);

        assertThrows(IllegalArgumentException.class, () -> test.placeRock(4));//choose row and pick up a rock at least
        test.setChosenRow(0);
        assertThrows(IllegalArgumentException.class, () -> test.placeRock(4));//pick up a rock at least
        test.pickupRock(2);

        assertThrows(IllegalArgumentException.class, () -> test.placeRock(0));//same as before(isPlaceable)
        assertThrows(IllegalArgumentException.class, () -> test.placeRock(2));//same as before(isPlaceable)

        assertThrows(IllegalArgumentException.class, () -> test.placeRock(1));//same as before(isPlaceable)

        assertThrows(IllegalArgumentException.class, () -> test.placeRock(-1));//same as before(isPlaceable)
        assertThrows(IllegalArgumentException.class, () -> test.placeRock(6));//same as before(isPlaceable)

        int rockBefore = test.getRocks()[test.getActivePlayer()];
        test.placeRock(3);
        assertEquals(1,test.getTable()[test.getChosenRow()][3]);
        assertEquals(rockBefore-1,test.getRocks()[test.getActivePlayer()]);

        assertThrows(IllegalArgumentException.class, () -> test.placeRock(4));//same as before(isPlaceable)

    }

    @Test
    void testCanBeEnded() {
        int[][] a = {{0,1,1,0,0},
                {0,0,0,0,0},
                {0,0,0,0,0},
                {0,0,1,0,0},
                {0,0,0,0,0}};
        RockTableState test = new RockTableState(a);
        //cant be ended until at least a rock is picked up
        assertFalse(test.canBeEnded());
        test.setChosenRow(0);
        assertFalse(test.canBeEnded());
        test.pickupRock(1);
        assertTrue(test.canBeEnded());
        test.pickupRock(2);
        assertTrue(test.canBeEnded());
        test.placeRock(3);
        test.placeRock(4);
        assertTrue(test.canBeEnded());
        test.endTurn();
        //cant be ended until at least a rock is picked up
        assertFalse(test.canBeEnded());
    }

    @Test
    void testEndTurn() {
        int[][] a = {{0,1,1,0,0},
                {0,0,0,0,0},
                {0,0,0,0,0},
                {0,0,1,0,0},
                {0,0,0,0,0}};
        RockTableState test = new RockTableState(a);

        test.setChosenRow(0);
        test.pickupRock(1);
        test.endTurn();

        assertFalse(test.isPickedUp());
        assertFalse(test.isRowChosen());
        assertEquals(1,test.getActivePlayer());//player2 turn
        assertEquals(-1,test.getChosenRow());
        assertEquals(4,test.getLeftMostCol());
        test.setChosenRow(3);
        test.pickupRock(2);
        test.endTurn();

        assertFalse(test.isPickedUp());
        assertFalse(test.isRowChosen());
        assertEquals(0,test.getActivePlayer());//player1 turn
        assertEquals(-1,test.getChosenRow());
        assertEquals(4,test.getLeftMostCol());

    }

    @Test
    void testToString() {
        int[][] a = {{0,1,1,0,0},
                {0,0,0,0,0},
                {0,0,0,0,0},
                {0,0,1,0,0},
                {0,0,0,0,0}};
        RockTableState test = new RockTableState(a);

        assertEquals("Current state:\n0 1 1 0 0 \n0 0 0 0 0 \n0 0 0 0 0 \n0 0 1 0 0 \n0 0 0 0 0 \n" +
                "Active player: "+(test.getActivePlayer()+1)+"\n" +
                "Chosen row: "+test.getChosenRow()+"\n" +
                "Rocks: "+test.getRocks()[test.getActivePlayer()]+"\n",test.toString());

    }
}