import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by tomasztrzos on 27.05.2017.
 */

public class Point {
    double x;
    double y;

    Point cluster;
    Color clusterColor;

    public Point() { // constructor for first initialize

        // generate random values for X and Y
        this.x = ThreadLocalRandom.current().nextInt(-100, 100 + 1);
        this.y = ThreadLocalRandom.current().nextInt(-100, 100 + 1);
    }

    public Point(double x, double y) { // constructor for clusters

        this.x = x;
        this.y = y;
    }

    public Point(double x, double y, Color clusterColor) { // constructor for memory of points

        this.x = x;
        this.y = y;
        this.clusterColor = clusterColor;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public Color getClusterColor() {
        return clusterColor;
    }

    public void setClusterColor(Color clusterColor) {
        this.clusterColor = clusterColor;
    }

    public Point getCluster() {
        return cluster;
    }

    public void setCluster(Point cluster) {
        this.cluster = cluster;
    }

}
