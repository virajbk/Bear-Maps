import java.util.HashSet;

public class SearchNode implements Comparable<SearchNode> {

    Node n;
    double distFromSource;
    double hDist;
    SearchNode prevNode;

    public SearchNode(Node n, double dFS, double hD, SearchNode pN) {
        this.n = n;
        distFromSource = dFS;
        hDist = hD;
        prevNode = pN;
    }

    public HashSet<SearchNode> neighbors(GraphDB g, Node target) {
        HashSet<Edge> adjs = g.adjList.get(g.nodeList.get(n.id));
        HashSet<SearchNode> sNodes = new HashSet<>();

        for (Edge e : adjs) {
            double edgeDist = g.distance(e.otherNode(n.id), n.id);
            double hDistE = g.distance(e.otherNode(n.id), target.id);
//            if (prevNode != null) {
//                if (e.otherNode(n.id) != prevNode.n.id) {
//                    //sNodes.add(new SearchNode(e.otherNode(n.id),
// distFromSource + edgeDist, hDistE, this));
//                }
//            } else {
//                //sNodes.add(new SearchNode(e.otherNode(n.id),
// distFromSource + edgeDist, hDistE, this));
//            }
        }

        return sNodes;
    }

    @Override
    public int compareTo(SearchNode s) {
        double diff = ((hDist + distFromSource) - (s.hDist + s.distFromSource));
        //System.out.println((indiff);
        if (diff > 0.0) {
            //System.out.println(1);
            return 1;
        } else if (diff == 0.0) {
            //System.out.println(0);
            return 0;
        } else {
            //System.out.println(-1);
            return -1;
        }
    }
}
