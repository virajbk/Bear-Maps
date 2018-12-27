public class FringeNode implements Comparable<FringeNode> {
    long id;
    double priority;

    public FringeNode(long id, double priority) {
        this.id = id;
        this.priority = priority;
    }

    @Override
    public int compareTo(FringeNode f) {
        if (priority > f.priority) {
            return 1;
        } else if (priority == f.priority) {
            return 0;
        } else {
            return -1;
        }
    }
}
