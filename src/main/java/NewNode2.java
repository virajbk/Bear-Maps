public class NewNode2 implements Comparable<NewNode2> {
    long id;
    double fScore;

    public NewNode2(long id, double fScore) {
        this.id = id;
        this.fScore = fScore;
    }

    @Override
    public int compareTo(NewNode2 n) {
        if (fScore > n.fScore) {
            return 1;
        } else if (fScore == n.fScore) {
            return 0;
        } else {
            return -1;
        }
    }
}
