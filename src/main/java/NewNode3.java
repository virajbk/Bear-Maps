public class NewNode3 implements Comparable<NewNode3> {
    long id;
    long parentID;
    double g;
    double f;

    public NewNode3(long id, long parentID, double g, double h) {
        this.id = id;
        this.parentID = parentID;
        this.g = g;
        f = g + h;
    }

    @Override
    public int compareTo(NewNode3 n) {
        if (f > n.f) {
            return 1;
        } else if (f == n.f) {
            return 0;
        } else {
            return -1;
        }
    }
}
