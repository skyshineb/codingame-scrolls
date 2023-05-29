package ru.ns;

import java.util.*;
import java.io.*;
import java.math.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


class Cell {
    public Hex coord;

    /**
     * 0 for empty, 1 for eggs, 2 for crystal
     */
    public int type;
    public int resources;
    public int myAnts;
    public int oppAnts;
    public int[] neighbourIds;

    public String toString() {
        return String.format("%s t: %d res: %d", coord, type, resources);
    }

}

class Hex {
    public int q;
    public int r;
    public int s;

    public Hex(int q, int r, int s) {
        this.q = q;
        this.r = r;
        this.s = s;
        if (q + r + s != 0) throw new IllegalArgumentException("q + r + s must be 0");
    }

    public Hex add(Hex b) {
        return new Hex(q + b.q, r + b.r, s + b.s);
    }

    public Hex subtract(Hex b) {
        return new Hex(q - b.q, r - b.r, s - b.s);
    }

    static public ArrayList<Hex> directions = new ArrayList<Hex>(){{add(new Hex(1, 0, -1)); add(new Hex(1, -1, 0)); add(new Hex(0, -1, 1)); add(new Hex(-1, 0, 1)); add(new Hex(-1, 1, 0)); add(new Hex(0, 1, -1));}};

    static public Hex direction(int direction) {
        return Hex.directions.get(direction);
    }

    public Hex neighbor(int direction) {
        return add(Hex.direction(direction));
    }

    public int length() {
        return (int)((Math.abs(q) + Math.abs(r) + Math.abs(s)) / 2);
    }

    public int distance(Hex b) {
        return subtract(b).length();
    }

    public String toString() {
        return String.format("[q:%d, r:%d, s:%d]", q, r, s);
    }
}

class Player {

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int numberOfCells = in.nextInt(); // amount of hexagonal cells in this map

        int myBase = 0, oppBase = 0;
        // init grid with cells
        ArrayList<Cell> grid = Stream.generate(Cell::new)
                .limit(numberOfCells).collect(Collectors.toCollection(ArrayList::new));

        // set initial hex coords to 0, 0, 0
//        grid.get(0).coord = new Hex(0, 0, 0);
        for (int i = 0; i < numberOfCells; i++) {
            int type = in.nextInt(); // 0 for empty, 1 for eggs, 2 for crystal
            int initialResources = in.nextInt(); // the initial amount of eggs/crystals on this cell
            grid.get(i).type = type;
            grid.get(i).resources = initialResources;

            grid.get(i).neighbourIds = new int[6];
            for (int nI = 0; nI < 6; nI++) {
                int neighbour = in.nextInt(); // the index of the neighbouring cell for each direction
                grid.get(i).neighbourIds[nI] = neighbour;
//                if (neighbour != -1) {
//                    // set coords to adjacent cells
//                    Hex currCoords = grid.get(i).coord;
//                    if (currCoords != null){
//                        grid.get(neighbour).coord = grid.get(i).coord.neighbor(nI);
//                    }
//                }
            }
        }
        int numberOfBases = in.nextInt();
        System.err.println("numberOfBases " + numberOfBases);
        for (int i = 0; i < numberOfBases; i++) {
            myBase = in.nextInt();
            System.err.println("myBase " + myBase);
        }
        for (int i = 0; i < numberOfBases; i++) {
            oppBase = in.nextInt();
            System.err.println("oppBase " + oppBase);
        }

        fillCoordinates(grid.get(0), new Hex(0, 0, 0), grid);
//        for (Cell c : grid) {
//            System.err.println(c);
//        }

        // game loop
        while (true) {
            System.err.println("numberOfCells " + numberOfCells);
            for (int i = 0; i < numberOfCells; i++) {
                int resources = in.nextInt(); // the current amount of eggs/crystals on this cell
                int myAnts = in.nextInt(); // the amount of your ants on this cell
                int oppAnts = in.nextInt(); // the amount of opponent ants on this cell
                grid.get(i).resources = resources;
                grid.get(i).myAnts = myAnts;
                grid.get(i).oppAnts = oppAnts;
            }
            PriorityQueue<Cell> candidates = new PriorityQueue<>(cellComparator);
            for (Cell c : grid) {
                if (c.resources > 0) {
                    if (c.type == 2) {
                        int dToMyBase = c.coord.distance(grid.get(myBase).coord);
                        int dToOppBase = c.coord.distance(grid.get(oppBase).coord);
                        if (dToMyBase < dToOppBase) {
                            if (!candidates.isEmpty()) {
                                if (dToMyBase > candidates.peek().coord.distance(grid.get(myBase).coord)) {
                                    candidates.add(c);
                                }
                            } else {
                                candidates.add(c);
                            }
                        } else {
                            candidates.add(c);
                        }
                    } else if (c.type == 1) {
                        int dToMyBase = c.coord.distance(grid.get(myBase).coord);
                        int dToOppBase = c.coord.distance(grid.get(oppBase).coord);
                        if (dToMyBase < dToOppBase) {
                            candidates.add(c);
                        }
                    }
                }
            }
            if (!candidates.isEmpty()) {
                System.out.println("LINE " + myBase + " " + grid.indexOf(candidates.poll()) + " " + 10);
            } else {
                System.out.println("WAIT");
            }

            // WAIT | LINE <sourceIdx> <targetIdx> <strength> | BEACON <cellIdx> <strength> | MESSAGE <text>

        }
    }

    public static Comparator<Cell> cellComparator = new Comparator<Cell>(){

        @Override
        public int compare(Cell c1, Cell c2) {
            //добавить дистанцию до базы рассчитывая ее для каждой клетки заранее
            // both eggs
            if (c1.type == 1 && c2.type == 1) {
                // pick the most resources
                return c1.resources - c2.resources;
            }
            if (c1.type == 1) {
                return -1;
            }
            if (c2.type == -1) {
                return  1;
            }
            return (int) (c1.resources) - c2.resources;
        }
    };

    private static final Stack<Cell> fillStack = new Stack<>();
    private static void fillCoordinates(Cell current, Hex coordinates, ArrayList<Cell> grid) {
        fillStack.push(current);
        current.coord = coordinates;
        for (int i = 0; i < current.neighbourIds.length; i++) {
            if (current.neighbourIds[i] != -1 && fillStack.search(grid.get(current.neighbourIds[i])) == -1) {
                Hex neighbourCoords = coordinates.neighbor(i);
                fillCoordinates(grid.get(current.neighbourIds[i]), neighbourCoords, grid);
            }
        }
    }
}
