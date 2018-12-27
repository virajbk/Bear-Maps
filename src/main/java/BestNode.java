public class BestNode {
    long id;
    long parentID;
    double distTo;

    public BestNode(long id, long pID, double distTo) {
        this.id = id;
        parentID = pID;
        this.distTo = distTo;
    }
}
