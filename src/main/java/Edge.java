public class Edge {
    long id;
    String name;
    long node1;
    long node2;
    String highwayType;
    int maxSpeed;

    public Edge(long n1) {
        node1 = n1;
    }

    public Edge(long n1, long n2) {
        node1 = n1;
        node2 = n2;
    }

    public Edge(long n1, String name, long id) {
        node1 = n1;
        this.name = name;
        this.id = id;
    }

    public Edge(long n1, long n2, String name, long id) {
        node1 = n1;
        node2 = n2;
        this.name = name;
        this.id = id;
    }

    public long otherNode(long iD) {
        if (iD == node1) {
            return node2;
        } else if (iD == node2) {
            return node1;
        } else {
            System.out.println("the given node doesn't have this edge");
            return -1;
        }
    }
}
