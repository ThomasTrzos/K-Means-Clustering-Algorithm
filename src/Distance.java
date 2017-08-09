/**
 * Created by tomasztrzos on 27.05.2017.
 */

import static java.lang.Math.*;

class Distance {

    static double euclidean(double x1, double y1, double x2, double y2) {
        return sqrt(pow(x1-x2, 2) + pow(y1-y2, 2));
    }

    static double manhattan(double x1, double y1, double x2, double y2) {
        return abs(x1 - x2) + abs(y1 - y2);
    }

    static double chebyshev(double x1, double y1, double x2, double y2) {

        double x = abs(x1 - x2);
        double y = abs(y1 - y2);

        if(x > y) {
            return x;
        } else {
            return y;
        }
    }

    static double minkowski(double x1, double y1, double x2, double y2, double p) {

        return pow(pow(abs(x1 - x2), p) + pow(abs(y1 - y2), p), 1/p);

        // odległość(x,y) = (|xi - yi|p)1/r
        // Parametr p steruje wzrastającą wagą, która jest przypisana różnicom w poszczególnych wymiarach,
        // parametr r steruje wzrastającą wagą, która jest przypisana większym różnicom między obiektami.
        // Jeśli r i p są równe 2, to odległość ta jest równa odległości euklidesowej.

        // Odległość Minkowskiego (?)

    }


}
