import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Graph for storing all of the intersection (vertex) and road (edge) information.
 * Uses your GraphBuildingHandler to convert the XML files into a graph. Your
 * code must include the vertices, adjacent, distance, closest, lat, and lon
 * methods. You'll also need to include instance variables and methods for
 * modifying the graph (e.g. addNode and addEdge).
 *
 * @author Alan Yao, Josh Hug
 */
public class GraphDB {
    /** Your instance variables for storing the graph. You should consider
     * creating helper classes, e.g. Node, Edge, etc. */
    HashMap<Long, HashSet<Long>> adjNList;
    HashMap<Node, HashSet<Edge>> adjList;
    HashMap<Long, Node> nodeList;

    public HashSet<Edge> neighbors(Node n) {
        return adjList.get(n);
    }

    public void addNode(Node n) {
        if (nodeList.containsKey(n.id)) {
            Node nTemp = nodeList.get(n.id);
            n.lon = nTemp.lon;
            n.lat = nTemp.lat;
            n.name = nTemp.name;
            adjList.put(n, adjList.get(nTemp));
            adjList.remove(nTemp);
            nodeList.put(n.id, n);
            //nodeList.remove(nTemp.id);
        } else {
            adjList.put(n, new HashSet<>());
            nodeList.put(n.id, n);
            adjNList.put(n.id, new HashSet<>());
        }
    }

    public void addNodeName(long id, String name) {
        Node nTemp = nodeList.get(id);
        nTemp.name = name;
    }

    public void addWay(ArrayList<Node> nodes, ArrayList<Edge> edges) {
        for (Node n : nodes) {
            if (!adjList.containsKey(n)) {
                addNode(n);
            }
        }

        for (Edge e : edges) {
            HashSet<Edge> n1Edges = adjList.get(nodeList.get(e.node1));

            if (!adjNList.get(e.node1).contains(e.node2)) {
                adjNList.get(e.node1).add(e.node2);
            }
            if (!adjNList.get(e.node2).contains(e.node1)) {
                adjNList.get(e.node2).add(e.node1);
            }

            if (n1Edges != null) {
                if (!n1Edges.contains(e)) {
                    n1Edges.add(e);
                }
            }
            HashSet<Edge> n2Edges = adjList.get(nodeList.get(e.node2));
            if (n2Edges != null) {
                if (!n2Edges.contains(e)) {
                    n2Edges.add(e);
                }
            }
        }
    }

    /**
     * Example constructor shows how to create and start an XML parser.
     * You do not need to modify this constructor, but you're welcome to do so.
     * @param dbPath Path to the XML file to be parsed.
     */
    public GraphDB(String dbPath) {
        try {
            File inputFile = new File(dbPath);
            FileInputStream inputStream = new FileInputStream(inputFile);
            // GZIPInputStream stream = new GZIPInputStream(inputStream);

            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            GraphBuildingHandler gbh = new GraphBuildingHandler(this);
            saxParser.parse(inputStream, gbh);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        clean();
    }

    /**
     * Helper to process strings into their "cleaned" form, ignoring punctuation and capitalization.
     * @param s Input string.
     * @return Cleaned string.
     */
    static String cleanString(String s) {
        return s.replaceAll("[^a-zA-Z ]", "").toLowerCase();
    }

    /**
     *  Remove nodes with no connections from the graph.
     *  While this does not guarantee that any two nodes in the remaining graph are connected,
     *  we can reasonably assume this since typically roads are connected.
     */
    private void clean() {
        // Your code here.
        for (Map.Entry<Node, HashSet<Edge>> pair: adjList.entrySet()) {
            if (pair.getValue().size() == 0) {
                nodeList.remove(pair.getKey().id);
            }
        }

        HashMap<Node, HashSet<Edge>> tempAdjList = new HashMap<>();

        for (Map.Entry<Long, Node> pair : nodeList.entrySet()) {
            tempAdjList.put(pair.getValue(), adjList.get(pair.getValue()));
        }

        adjList = tempAdjList;
    }

    /**
     * Returns an iterable of all vertex IDs in the graph.
     * @return An iterable of id's of all vertices in the graph.
     */
    Iterable<Long> vertices() {
        //YOUR CODE HERE, this currently returns only an empty list.
        ArrayList<Long> vertexIDs = new ArrayList<>();
        for (Map.Entry<Node, HashSet<Edge>> pair: adjList.entrySet()) {
            vertexIDs.add(pair.getKey().id);
        }
        return vertexIDs;
    }

    /**
     * Returns ids of all vertices adjacent to v.
     * @param v The id of the vertex we are looking adjacent to.
     * @return An iterable of the ids of the neighbors of v.
     */
    Iterable<Long> adjacent(long v) {
        return adjNList.get(v);
    }

    /**
     * Returns the great-circle distance between vertices v and w in miles.
     * Assumes the lon/lat methods are implemented properly.
     * <a href="https://www.movable-type.co.uk/scripts/latlong.html">Source</a>.
     * @param v The id of the first vertex.
     * @param w The id of the second vertex.
     * @return The great-circle distance between the two locations from the graph.
     */
    double distance(long v, long w) {
        return distance(lon(v), lat(v), lon(w), lat(w));
    }

    static double distance(double lonV, double latV, double lonW, double latW) {
        double phi1 = Math.toRadians(latV);
        double phi2 = Math.toRadians(latW);
        double dphi = Math.toRadians(latW - latV);
        double dlambda = Math.toRadians(lonW - lonV);

        double a = Math.sin(dphi / 2.0) * Math.sin(dphi / 2.0);
        a += Math.cos(phi1) * Math.cos(phi2) * Math.sin(dlambda / 2.0) * Math.sin(dlambda / 2.0);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return 3963 * c;
    }

    /**
     * Returns the initial bearing (angle) between vertices v and w in degrees.
     * The initial bearing is the angle that, if followed in a straight line
     * along a great-circle arc from the starting point, would take you to the
     * end point.
     * Assumes the lon/lat methods are implemented properly.
     * <a href="https://www.movable-type.co.uk/scripts/latlong.html">Source</a>.
     * @param v The id of the first vertex.
     * @param w The id of the second vertex.
     * @return The initial bearing between the vertices.
     */
    double bearing(long v, long w) {
        return bearing(lon(v), lat(v), lon(w), lat(w));
    }

    static double bearing(double lonV, double latV, double lonW, double latW) {
        double phi1 = Math.toRadians(latV);
        double phi2 = Math.toRadians(latW);
        double lambda1 = Math.toRadians(lonV);
        double lambda2 = Math.toRadians(lonW);

        double y = Math.sin(lambda2 - lambda1) * Math.cos(phi2);
        double x = Math.cos(phi1) * Math.sin(phi2);
        x -= Math.sin(phi1) * Math.cos(phi2) * Math.cos(lambda2 - lambda1);
        return Math.toDegrees(Math.atan2(y, x));
    }

    /**
     * Returns the vertex closest to the given longitude and latitude.
     * @param lon The target longitude.
     * @param lat The target latitude.
     * @return The id of the node in the graph closest to the target.
     */
    long closest(double lon, double lat) {
        double leastDistance = 0;
        long id = 0;
        boolean flag = true;
        for (Map.Entry<Node, HashSet<Edge>> pair : adjList.entrySet()) {
            if (!flag) {
                double dist = distance(pair.getKey().lon, pair.getKey().lat, lon, lat);
                if (dist < leastDistance) {
                    id = pair.getKey().id;
                    leastDistance = dist;
                }
            } else {
                flag = false;
                leastDistance = distance(pair.getKey().lon, pair.getKey().lat, lon, lat);
                id = pair.getKey().id;
            }
        }

        return id;
    }

    /**
     * Gets the longitude of a vertex.
     * @param v The id of the vertex.
     * @return The longitude of the vertex.
     */
    double lon(long v) {

        return nodeList.get(v).lon;

//        for (Map.Entry<Node, HashSet<Edge>> pair : adjList.entrySet()) {
//            if (v == pair.getKey().id) {
//                return pair.getKey().lon;
//            }
//        }
//        System.out.println("Given vertex id doesn't exist");

        //return 0;
    }

    /**
     * Gets the latitude of a vertex.
     * @param v The id of the vertex.
     * @return The latitude of the vertex.
     */
    double lat(long v) {
        return nodeList.get(v).lat;
//        for (Map.Entry<Node, HashSet<Edge>> pair : adjList.entrySet()) {
//            if (v == pair.getKey().id) {
//                return pair.getKey().lat;
//            }
//        }
//        System.out.println("Given vertex id doesn't exist");
//
//        return 0;
    }
}
