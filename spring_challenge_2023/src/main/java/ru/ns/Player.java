package ru.ns;

import java.util.*;
import java.io.*;
import java.math.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


class Cell {
    public Hex coord;
    public int type;    // 0 for empty, 1 for eggs, 2 for crystal
    public int resources;
    public int myAnts;
    public int oppAnts;

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
        grid.get(0).coord = new Hex(0, 0, 0);
        for (int i = 0; i < numberOfCells; i++) {
            int type = in.nextInt(); // 0 for empty, 1 for eggs, 2 for crystal
            int initialResources = in.nextInt(); // the initial amount of eggs/crystals on this cell
            grid.get(i).type = type;
            grid.get(i).resources = initialResources;

            for (int neighIndex = 0; neighIndex < 6; neighIndex++) {
                int neigh = in.nextInt(); // the index of the neighbouring cell for each direction
                if (neigh != -1) {
                    // set coords to adjacent cells
                    grid.get(neigh).coord = grid.get(i).coord.neighbor(neighIndex);
                }
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
            Stack<Cell> candidates = new Stack<>();
            for (Cell c : grid) {
                if (c.resources > 0) {
                    int dToMyBase = c.coord.distance(grid.get(myBase).coord);
                    int dToOppBase = c.coord.distance(grid.get(oppBase).coord);
                    if (dToMyBase < dToOppBase) {
                        if (!candidates.empty()) {
                            if (dToMyBase > candidates.peek().coord.distance(grid.get(myBase).coord)) {
                                candidates.push(c);
                            }
                        } else {
                            candidates.push(c);
                        }
                    } else {
                        candidates.add(candidates.size(), c);
                    }
                }
            }
            if (!candidates.isEmpty()) {
                System.out.println("LINE " + myBase + " " + grid.indexOf(candidates.pop()) + " " + 10);
            } else {
                System.out.println("WAIT");
            }

            // WAIT | LINE <sourceIdx> <targetIdx> <strength> | BEACON <cellIdx> <strength> | MESSAGE <text>

        }
    }
}
