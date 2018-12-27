public class Node {
    double lat;
    double lon;
    long id;
    String name;


    Node(double lon, double lat, long id) {
        this.lat = lat;
        this.lon = lon;
        this.id = id;
        name = null;
    }

    Node(long id) {
        this.lat = 0;
        this.lon = 0;
        this.id = id;
        name = null;
    }

}
