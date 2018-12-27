public class NewNode implements Comparable<NewNode> {
    long id;
    long parentID;
    double g;
    double h;
    double f;

    public NewNode(long id, long parentID, double g, double h) {
        this.id = id;
        this.parentID = parentID;
        this.g = g;
        this.h = h;
        f = g + h;
    }

    @Override
    public int compareTo(NewNode n) {
        if (f > n.f) {
            return 1;
        } else if (f == n.f) {
            return 0;
        } else {
            return -1;
        }
    }
}
