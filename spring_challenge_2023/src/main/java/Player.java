
import java.util.*;
import java.io.*;
import java.math.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


class Cell {
    public Hex coord;

    public int id;
    /**
     * 0 for empty, 1 for eggs, 2 for crystal
     */
    public int type;
    public int resources;
    public int myAnts;
    public int oppAnts;
    public ArrayList<Integer> neighbourIds;
    /**
     * 0 - my, 1 - opp
     */
    public int color = -1;

    public Cell(){}
    public Cell(int id, int type, int resources, ArrayList<Integer> neighbourIds) {
        this.id = id;
        this.type = type;
        this.resources = resources;
        this.neighbourIds = neighbourIds;
    }

    public String toString() {
        return String.format("%d t: %d res: %d col: %d", id, type, resources, color);
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

        ArrayList<Integer> myBases = new ArrayList<>();
        ArrayList<Integer> oppBases = new ArrayList<>();
        // init grid with cells
        ArrayList<Cell> grid = Stream.generate(Cell::new)
                .limit(numberOfCells).collect(Collectors.toCollection(ArrayList::new));

        // weights for flow algorithm
        int [][] graph = new int[numberOfCells + 1][];
        for (int i = 0; i < numberOfCells; i++) {
            int type = in.nextInt(); // 0 for empty, 1 for eggs, 2 for crystal
            int initialResources = in.nextInt(); // the initial amount of eggs/crystals on this cell
            grid.get(i).id = i;
            grid.get(i).type = type;
            grid.get(i).resources = initialResources;

            ArrayList<Integer> neighbours = new ArrayList<>();
            for (int nI = 0; nI < 6; nI++) {
                int neighbour = in.nextInt(); // the index of the neighbouring cell for each direction
                neighbours.add(neighbour);
            }
            grid.get(i).neighbourIds = neighbours;
            graph[i] = neighbours.stream().filter(n -> n != -1).mapToInt(Integer::intValue).toArray();
        }
        int numberOfBases = in.nextInt();
        System.err.println("numberOfBases " + numberOfBases);
        for (int i = 0; i < numberOfBases; i++) {
            int myBase = in.nextInt();
            myBases.add(myBase);
            System.err.println("myBase " + myBase);
        }
        for (int i = 0; i < numberOfBases; i++) {
            int oppBase = in.nextInt();
            oppBases.add(oppBase);
            System.err.println("oppBase " + oppBase);
        }

        // weights for flow algorithm add SuperSource node
        graph[graph.length - 1] = new int[]{myBases.get(0)};

        fillCoordinates(grid.get(0), new Hex(0, 0, 0), grid);

        // colorize the graph
        floodFillDouble(grid.get(myBases.get(0)), grid.get(oppBases.get(0)), grid);

        List<Cell> closestResources = grid.stream().filter(cell -> cell.color == 0 && cell.resources != 0).collect(Collectors.toList());
        ArrayList<Route> allRoutes = new ArrayList<>();
        for (Cell target : closestResources) {
            List<Route> routes = findRoutes(grid.get(myBases.get(0)), target, grid);
            allRoutes.addAll(routes);
        }


        Route lastAntRoute = null;
        Route lastResRoute = null;
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
            // update routes
            for (Route route : allRoutes) {
                route.recalcGain();
            }
            allRoutes.removeIf(route -> route.gainAnts == 0 && route.gainResources == 0);
            if (lastAntRoute != null && lastAntRoute.gainAnts == 0 && lastAntRoute.gainResources == 0) {
                lastAntRoute = null;
            }
            if (lastResRoute != null && lastResRoute.gainAnts == 0 && lastResRoute.gainResources == 0) {
                lastResRoute = null;
            }

            List<Route> antRoutes = allRoutes.stream().filter(r -> r.gainAnts > 0).sorted(Comparator.comparingInt(o -> o.gainAnts)).collect(Collectors.toList());
            if (!antRoutes.isEmpty()) {
                Route bestRoute;
                if (lastAntRoute != null) {
                    bestRoute = lastAntRoute;
                } else {
                    bestRoute = antRoutes.get(antRoutes.size() - 1);
                    lastAntRoute = bestRoute;
                }
                for (Cell c : bestRoute.route) {
                    System.out.printf("BEACON %d 1;", c.id);
                }
            } else {
                List<Route> resRoutes = allRoutes.stream().filter(r -> r.gainResources > 0).sorted(Comparator.comparingInt(o -> o.gainResources)).collect(Collectors.toList());
                if (!resRoutes.isEmpty()) {
                    Route bestRoute;
                    if (lastResRoute != null) {
                        bestRoute = lastResRoute;
                    } else {
                        bestRoute = resRoutes.get(resRoutes.size() - 1);
                        lastResRoute = bestRoute;
                    }
                    for (Cell c : bestRoute.route) {
                        System.out.printf("BEACON %d 1;", c.id);
                    }
                } else {
                    Optional<Cell> candidate = grid.stream().filter(c -> c.type == 2).filter(c -> c.resources != 0).sorted(Comparator.comparingInt(c -> c.coord.distance(grid.get(myBases.get(0)).coord))).findAny();
                    if (candidate.isPresent()) {
                        System.out.printf("LINE " + myBases.get(0) + " " + candidate.get().id + " " + 10);
                    } else {
                        System.out.printf("WAIT");
                    }
                }
            }
            System.out.println();


//            if (!candidates.isEmpty()) {
//                System.out.println("LINE " + myBases.get(0) + " " + grid.indexOf(candidates.poll()) + " " + 10);
//            } else {
//                System.out.println("WAIT");
//            }

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

    public static class Route {
        ArrayList<Cell> route = new ArrayList<>();
        int gainResources = 0;
        int gainAnts = 0;

        public void addCell(Cell c) {
            route.add(c);
            if (c.type == 1){
                gainAnts += c.resources;
            }
            if (c.type == 2){
                gainResources += c.resources;
            }
        }

        public void remCell(Cell c) {
            route.remove(c);
            if (c.type == 1){
                gainAnts -= c.resources;
            }
            if (c.type == 2){
                gainResources -= c.resources;
            }
        }

        @Override
        public Route clone() {
            Route clone = new Route();
            clone.gainResources = gainResources;
            clone.gainAnts = gainAnts;
            clone.route = new ArrayList<>(route);
            return clone;
        }

        @Override
        public String toString() {
            String res = "Route[gR: " + gainResources + " gA: " + gainAnts + "]:";
            for (Cell c : route) {
                res += c.id + " -> ";
            }
            return res;
        }

        public void recalcGain() {
            int gR = 0;
            int gA = 0;
            for (Cell c : route) {
                if (c.type == 1){
                    gA += c.resources;
                }
                if (c.type == 2){
                    gR += c.resources;
                }
            }
            this.gainResources = gR;
            this.gainAnts = gA;
        }
    }

    public static List<Route> findRoutes(Cell source, Cell destination, ArrayList<Cell> grid) {
        ArrayList<Route> results = new ArrayList<>();
        Route currentRoute = new Route();
        currentRoute.route.add(source);
        ArrayDeque<Route> stack = new ArrayDeque<>();
        stack.add(currentRoute);
        int len = Integer.MAX_VALUE;
        while (!stack.isEmpty()) {
            Route path = stack.poll();
            Cell lastNode = path.route.get(path.route.size() - 1);
            if (path.route.size() <= len){
                if (lastNode == destination) {
                    results.add(path.clone());
                    System.err.println(path);
                    len = path.route.size();
                } else {
                    List<Integer> neighbours = lastNode.neighbourIds.stream().filter(n -> n != -1).collect(Collectors.toList());

                    for (int n : neighbours) {
                        if (!path.route.contains(grid.get(n))){
                            Route newRoute = path.clone();
                            newRoute.addCell(grid.get(n));
                            stack.add(newRoute);
                        }
                    }
                }
            }
        }

        return results;
    }

    /**
     * Flood-fill (node):
     *   1. Set Q to the empty queue or stack.
     *   2. Add node to the end of Q.
     *   3. While Q is not empty:
     *   4.   Set n equal to the firstlement of Q.
     *   5.   Remove first element from Q.
     *   6.   If n is Inside:
     *          Set the n
     *          Add the node to the west of n to the end of Q.
     *          Add the node to the east of n to the end of Q. e
     *          Add the node to the north of n to the end of Q.
     *          Add the node to the south of n to the end of Q.
     *   7. Continue looping until Q is exhausted.
     *   8. Return.
     */
    public static void floodFillDouble(Cell my, Cell opp, ArrayList<Cell> grid) {
        ArrayDeque<Cell> Q1 = new ArrayDeque<>();
        ArrayDeque<Cell> Q2 = new ArrayDeque<>();
        Q1.add(my);
        Q2.add(opp);
        while(!Q1.isEmpty() || !Q2.isEmpty()) {
            if (!Q1.isEmpty()) {
                Cell n = Q1.poll();
                if (n.color == -1) {
                    n.color = 0;
                    for (int neighbourId : n.neighbourIds) {
                        if (neighbourId != -1){
                            Q1.add(grid.get(neighbourId));
                        }
                    }
                }
            }
            if (!Q2.isEmpty()) {
                Cell n = Q2.poll();
                if (n.color == -1) {
                    n.color = 1;
                    for (int neighbourId : n.neighbourIds) {
                        if (neighbourId != -1) {
                            Q2.add(grid.get(neighbourId));
                        }
                    }
                }
            }
        }
    }

    static final Stack<Cell> fillStack = new Stack<>();
    public static void fillCoordinates(Cell current, Hex coordinates, ArrayList<Cell> grid) {
        fillStack.push(current);
        current.coord = coordinates;
        for (int i = 0; i < current.neighbourIds.size(); i++) {
            if (current.neighbourIds.get(i) != -1 && fillStack.search(grid.get(current.neighbourIds.get(i))) == -1) {
                Hex neighbourCoords = coordinates.neighbor(i);
                fillCoordinates(grid.get(current.neighbourIds.get(i)), neighbourCoords, grid);
            }
        }
    }
}
