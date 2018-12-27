import java.util.HashSet;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Collections;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class provides a shortestPath method for finding routes between two points
 * on the map. Start by using Dijkstra's, and if your code isn't fast enough for your
 * satisfaction (or the autograder), upgrade your implementation by switching it to A*.
 * Your code will probably not be fast enough to pass the autograder unless you use A*.
 * The difference between A* and Dijkstra's is only a couple of lines of code, and boils
 * down to the priority you use to order your vertices.
 */
public class Router {
    /**
     * Return a List of longs representing the shortest path from the node
     * closest to a start location and the node closest to the destination
     * location.
     *
     * @param g       The graph to use.
     * @param stlon   The longitude of the start location.
     * @param stlat   The latitude of the start location.
     * @param destlon The longitude of the destination location.
     * @param destlat The latitude of the destination location.
     * @return A list of node id's in the order visited on the shortest path.
     */
    public static List<Long> shortestPath(GraphDB g, double stlon, double stlat,
                                          double destlon, double destlat) {
        ArrayList<Long> sp = new ArrayList<>();
        PriorityQueue<NewNode3> fringe = new PriorityQueue<>();
        HashMap<Long, NewNode3> best = new HashMap<>();
        HashSet<Long> marked = new HashSet<>();
        HashMap<Long, Double> hVals = new HashMap<>();

        long startNode = g.closest(stlon, stlat);
        long destNode = g.closest(destlon, destlat);

        NewNode3 sNode = new NewNode3(startNode, -1, 0, g.distance(startNode, destNode));

        fringe.add(sNode);
        best.put(startNode, sNode);
        boolean flag = false;
        double edgeDist, sourceDist, hDist;

        while (!flag && !fringe.isEmpty()) {
            NewNode3 currNode = fringe.poll();
            if (currNode.id == destNode) {
                flag = true;
            } else if (!marked.contains(currNode.id)) {
                marked.add(currNode.id);
                for (long v : g.adjacent(currNode.id)) {
                    if (v != best.get(currNode.id).parentID) {

                        //long startTime = System.nanoTime();

                        //long endTime = System.nanoTime();

                        //System.out.println("Took " + (endTime - startTime) + " ns");
                        edgeDist = g.distance(v, currNode.id);
                        sourceDist = best.get(currNode.id).g;

                        if (best.containsKey(v)) {
                            if (sourceDist + edgeDist < best.get(v).g) {
                                if (hVals.containsKey(v)) {
                                    hDist = hVals.get(v);
                                } else {
                                    hDist = g.distance(v, destNode);
                                    hVals.put(v, hDist);
                                }
                                NewNode3 n = new NewNode3(v,
                                        currNode.id, sourceDist + edgeDist, hDist);
                                best.put(v, n);
                                fringe.add(n);
                            }
                        } else {
                            if (hVals.containsKey(v)) {
                                hDist = hVals.get(v);
                            } else {
                                hDist = g.distance(v, destNode);
                                hVals.put(v, hDist);
                            }
                            NewNode3 n = new NewNode3(v, currNode.id, sourceDist + edgeDist, hDist);
                            best.put(v, n);
                            fringe.add(n);
                        }
                    }
                }
            }
        }

        if (flag) {
            while (best.get(destNode).parentID != -1) {
                sp.add(destNode);
                destNode = best.get(destNode).parentID;
            }

            sp.add(destNode);

            Collections.reverse(sp);
        } else {
            sp = null;
        }
        return sp;
    }

    private static ArrayList<Long> reconstructPath(HashMap<Long, Long> cameFrom, Long current) {
        ArrayList<Long> sp = new ArrayList<>();
        sp.add(current);
        while (cameFrom.containsKey(current)) {
            current = cameFrom.get(current);
            sp.add(current);
        }

        sp.remove(sp.size() - 1);

        Collections.reverse(sp);

        return sp;
    }

    public void temp3() {
//        ArrayList<Long> sp = new ArrayList<>();
//
//        long startID = g.closest(stlon, stlat);
//        long destID = g.closest(destlon, destlat);
//
//        HashSet<Long> closedSet = new HashSet<>();
//        PriorityQueue<NewNode2> openSet = new PriorityQueue<>();
//        HashMap<Long, NewNode2> openMap = new HashMap<>();
//        HashMap<Long, Long> cameFrom = new HashMap<>();
//        HashMap<Long, Double> gScore = new HashMap<>();
//        HashMap<Long, Double> fScore = new HashMap<>();
//
//        for (Long v : g.vertices()) {
//            gScore.put(v, Double.MAX_VALUE);
//            fScore.put(v, Double.MAX_VALUE);
//        }
//
//        gScore.put(startID, 0.0);
//        fScore.put(startID, g.distance(startID, destID));
//        NewNode2 startNode = new NewNode2(startID, g.distance(startID, destID));
//        openSet.add(startNode);
//        openMap.put(startNode.id, startNode);
//        cameFrom.put(startID, -1L);
//
//        while (!openSet.isEmpty()) {
//            NewNode2 current = openSet.poll();
//            if (current.id == destID) {
//                return reconstructPath(cameFrom, current.id);
//            }
//
//            closedSet.add(current.id);
//
//            for (long neighbor : g.adjacent(current.id)) {
//                if (neighbor != cameFrom.get(current.id)) {
//                    if (closedSet.contains(neighbor)) {
//                        continue;
//                    }
//
//                    double tentativeGScore = gScore.get(current.id)
// + g.distance(current.id, neighbor);
//                    double tentativeFScore = tentativeGScore + g.distance(neighbor, destID);
//
//                    if (!openMap.containsKey(neighbor)) {
//                        openSet.add(new NewNode2(neighbor, tentativeFScore));
//                    } else {
//                        if (openMap.get(neighbor).fScore < tentativeFScore) {
//                            continue;
//                        }
//                    }
//
//                    if (tentativeGScore >= gScore.get(neighbor)) {
//                        continue;
//                    }
//
//                    cameFrom.put(neighbor, current.id);
//                    gScore.put(neighbor, tentativeGScore);
//                    fScore.put(neighbor, tentativeFScore);
//                }
//            }
//        }
//
//        return null;
    }

    public void temp2() {
//        ArrayList<Long> sp = new ArrayList<>();
//
//        PriorityQueue<NewNode> openListPQ = new PriorityQueue<>();
//        HashMap<Long, NewNode> openList = new HashMap<>();
//        HashMap<Long, NewNode> closedList = new HashMap<>();
//
//        long startID = g.closest(stlon, stlat);
//        long destID = g.closest(destlon, destlat);
//
//        NewNode sNode = new NewNode(startID, -1L, 0, g.distance(startID, destID));
//
//        openList.put(startID, sNode);
//        openListPQ.add(sNode);
//        NewNode q;
//        boolean flag, flag1, flag2, flag3;
//        Long penultimateID = 0L;
//        flag = false;
//
//        while (!openListPQ.isEmpty() && !flag) {
//            q = openListPQ.poll();
//            openList.remove(q.id);
//
//            for (long succesor : g.adjacent(q.id)) {
//                //if (succesor != q.parentID){
//                flag1 = false;
//                flag2 = false;
//                if (succesor == destID) {
//                    penultimateID = q.id;
//                    flag = true;
//                    break;
//                }
//                NewNode s = new NewNode(succesor, q.id,
// q.g + g.distance(q.id, succesor), g.distance(succesor, destID));
//
//                if (openList.containsKey(succesor)) {
//                    //flag1 = openList.get(succesor).f <= s.f;
//                }
//
//                if (closedList.containsKey(succesor)) {
//                    flag2 = closedList.get(succesor).f <= s.f;
//                }
//
//                if (!flag1 && !flag2) {
//                    openList.put(succesor, s);
//                    openListPQ.add(s);
//                }
//                //}
//            }
//
//            closedList.put(q.id, q);
//        }
//
//        sp.add(destID);
//
//        while (penultimateID != -1L) {
//            sp.add(penultimateID);
//            penultimateID = closedList.get(penultimateID).parentID;
//        }
//
//        Collections.reverse(sp);
//
//        return sp;
    }

    public void temp() {
//        PriorityQueue<FringeNode> fringe = new PriorityQueue<>();
//        HashMap<Long, BestNode> best = new HashMap<>();
//        HashSet<Long> marked = new HashSet<>();
//        FringeNode.hVals = new HashMap<>();
//
//        long startNode = g.closest(stlon, stlat);
//        long destNode = g.closest(destlon, destlat);
//
//        fringe.add(new FringeNode(startNode, g.distance(startNode, destNode)));
//        best.put(startNode, new BestNode(startNode, -1, 0));
//        boolean flag = false;
//
//        while (!flag) {
//            //System.out.println("a");
//            FringeNode currNode = fringe.poll();
//            if (currNode.id == destNode) {
//                flag = true;
//            } else if (!marked.contains(currNode.id)) {
//                marked.add(currNode.id);
//                System.out.println("Current Node: " + currNode.id);
//                System.out.println("Priority: " + currNode.priority);
//                for (long v : g.adjacent(currNode.id)) {
//                    if (v != best.get(currNode.id).parentID) {
//                        boolean flag1 = false;
//                        if (best.containsKey(v)) {
//                            if (best.get(currNode.id).distTo
// + g.distance(v, currNode.id) < best.get(v).distTo) {
//                                best.put(v, new BestNode(v,
// currNode.id, best.get(currNode.id).distTo + g.distance(v, currNode.id)));
//                                flag1 = true;
//                            }
//                        } else {
//                            best.put(v, new BestNode(v,
// currNode.id, best.get(currNode.id).distTo + g.distance(v, currNode.id)));
//                            flag1 = true;
//                        }
//                        double priority = 0.0;
//                        if (flag1) {
//
//                            double hDist;
//                            if (FringeNode.hVals.containsKey(v)) {
//                                hDist = FringeNode.hVals.get(v);
//                            } else {
//                                hDist = g.distance(v, destNode);
//                                FringeNode.hVals.put(v, hDist);
//                            }
//                            //double hDist = g.distance(v, targetNodeID);
//                            //hDist = 0;
//
//                            fringe.add(new FringeNode(v, best.get(v).distTo + hDist));
//                            priority = best.get(v).distTo + hDist;
//                            //allFringes.put(v, f);
//                        }
//                        System.out.println("Current Neighbor: " + v);
//                        relax(g, best, fringe, v, currNode.id, destNode);
//                        System.out.println("Priority: " + priority);
//                    }
//                }
//            }
//        }
//
//        while (best.get(destNode).parentID != -1) {
//            sp.add(destNode);
//            destNode = best.get(destNode).parentID;
//        }
//        sp.add(destNode);

        //System.out.println(best.size());
        //System.out.println(g.nodeList.size());
    }

    private static void relax(GraphDB g, HashMap<Long, BestNode> best,
                              PriorityQueue<FringeNode> fringe, Long v,
                              Long currNodeID, Long targetNodeID) {
//        boolean flag = false;
//        if (best.containsKey(v)) {
//            if (best.get(currNodeID).distTo + g.distance(v, currNodeID) < best.get(v).distTo) {
//                best.put(v, new BestNode(v, currNodeID,
// best.get(currNodeID).distTo + g.distance(v, currNodeID)));
//                flag = true;
//            }
//            //fringe.remove(allFringes.get(v));
//        } else {
//            best.put(v, new BestNode(v, currNodeID,
// best.get(currNodeID).distTo + g.distance(v, currNodeID)));
//            flag = true;
//        }
//        if (flag) {
//
//            double hDist;
//            if (hVals.containsKey(v)) {
//                hDist = FringeNode.hVals.get(v);
//            } else {
//                hDist = g.distance(v, targetNodeID);
//                FringeNode.hVals.put(v, hDist);
//            }
//            //double hDist = g.distance(v, targetNodeID);
//            //hDist = 0;
//
//            fringe.add(new FringeNode(v, best.get(v).distTo + hDist));
//            //allFringes.put(v, f);
//        }
    }

    /**
     * Create the list of directions corresponding to a route on the graph.
     *
     * @param g     The graph to use.
     * @param route The route to translate into directions. Each element
     *              corresponds to a node from the graph in the route.
     * @return A list of NavigatiionDirection objects corresponding to the input
     * route.
     */
    public static List<NavigationDirection> routeDirections(GraphDB g, List<Long> route) {
        return null; //
    }


    /**
     * Class to represent a navigation direction, which consists of 3 attributes:
     * a direction to go, a way, and the distance to travel for.
     */
    public static class NavigationDirection {

        /**
         * Integer constants representing directions.
         */
        public static final int START = 0;
        public static final int STRAIGHT = 1;
        public static final int SLIGHT_LEFT = 2;
        public static final int SLIGHT_RIGHT = 3;
        public static final int RIGHT = 4;
        public static final int LEFT = 5;
        public static final int SHARP_LEFT = 6;
        public static final int SHARP_RIGHT = 7;

        /**
         * Number of directions supported.
         */
        public static final int NUM_DIRECTIONS = 8;

        /**
         * A mapping of integer values to directions.
         */
        public static final String[] DIRECTIONS = new String[NUM_DIRECTIONS];

        /**
         * Default name for an unknown way.
         */
        public static final String UNKNOWN_ROAD = "unknown road";

        /** Static initializer. */
        static {
            DIRECTIONS[START] = "Start";
            DIRECTIONS[STRAIGHT] = "Go straight";
            DIRECTIONS[SLIGHT_LEFT] = "Slight left";
            DIRECTIONS[SLIGHT_RIGHT] = "Slight right";
            DIRECTIONS[LEFT] = "Turn left";
            DIRECTIONS[RIGHT] = "Turn right";
            DIRECTIONS[SHARP_LEFT] = "Sharp left";
            DIRECTIONS[SHARP_RIGHT] = "Sharp right";
        }

        /**
         * The direction a given NavigationDirection represents.
         */
        int direction;
        /**
         * The name of the way I represent.
         */
        String way;
        /**
         * The distance along this way I represent.
         */
        double distance;

        /**
         * Create a default, anonymous NavigationDirection.
         */
        public NavigationDirection() {
            this.direction = STRAIGHT;
            this.way = UNKNOWN_ROAD;
            this.distance = 0.0;
        }

        public String toString() {
            return String.format("%s on %s and continue for %.3f miles.",
                    DIRECTIONS[direction], way, distance);
        }

        /**
         * Takes the string representation of a navigation direction and converts it into
         * a Navigation Direction object.
         *
         * @param dirAsString The string representation of the NavigationDirection.
         * @return A NavigationDirection object representing the input string.
         */
        public static NavigationDirection fromString(String dirAsString) {
            String regex = "([a-zA-Z\\s]+) on ([\\w\\s]*) and continue for ([0-9\\.]+) miles\\.";
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(dirAsString);
            NavigationDirection nd = new NavigationDirection();
            if (m.matches()) {
                String direction = m.group(1);
                if (direction.equals("Start")) {
                    nd.direction = NavigationDirection.START;
                } else if (direction.equals("Go straight")) {
                    nd.direction = NavigationDirection.STRAIGHT;
                } else if (direction.equals("Slight left")) {
                    nd.direction = NavigationDirection.SLIGHT_LEFT;
                } else if (direction.equals("Slight right")) {
                    nd.direction = NavigationDirection.SLIGHT_RIGHT;
                } else if (direction.equals("Turn right")) {
                    nd.direction = NavigationDirection.RIGHT;
                } else if (direction.equals("Turn left")) {
                    nd.direction = NavigationDirection.LEFT;
                } else if (direction.equals("Sharp left")) {
                    nd.direction = NavigationDirection.SHARP_LEFT;
                } else if (direction.equals("Sharp right")) {
                    nd.direction = NavigationDirection.SHARP_RIGHT;
                } else {
                    return null;
                }

                nd.way = m.group(2);
                try {
                    nd.distance = Double.parseDouble(m.group(3));
                } catch (NumberFormatException e) {
                    return null;
                }
                return nd;
            } else {
                // not a valid nd
                return null;
            }
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof NavigationDirection) {
                return direction == ((NavigationDirection) o).direction
                        && way.equals(((NavigationDirection) o).way)
                        && distance == ((NavigationDirection) o).distance;
            }
            return false;
        }

        @Override
        public int hashCode() {
            return Objects.hash(direction, way, distance);
        }
    }
}
