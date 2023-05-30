import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    @Test
    public void testBFS() {
        ArrayList<Cell> grid = new ArrayList<>();
        Cell c0 = new Cell(0, 0, 0, new ArrayList<>(Arrays.asList(1, 2)));
        Cell c1 = new Cell(1, 1, 5, new ArrayList<>(Arrays.asList(0, 3)));
        Cell c2 = new Cell(2, 2, 4, new ArrayList<>(Arrays.asList(0, 4)));
        Cell c3 = new Cell(3, 0, 0, new ArrayList<>(Arrays.asList(1, 5)));
        Cell c4 = new Cell(4, 0, 0, new ArrayList<>(Arrays.asList(2, 5)));
        Cell c5 = new Cell(5, 2, 10, new ArrayList<>(Arrays.asList(3, 4)));
        grid.add(c0);
        grid.add(c1);
        grid.add(c2);
        grid.add(c3);
        grid.add(c4);
        grid.add(c5);
        Player.findRoutes(c0, c5, grid);
    }

}