import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Created by tomasztrzos on 27.05.2017.
 */

public class KmeansClustering {

    // Algorithm
    private int iterationCounter = 0;
    private List<Point> points;
    private List<Point> clusters; // centroids
    private List<Point> memoryOfPoints;

    // Graphical User Interface
    private JFrame mainFrame;
    private JPanel mainPanel;
    private Color[] colors = {Color.BLUE, Color.RED, Color.GREEN, Color.MAGENTA, Color.YELLOW, Color.CYAN, Color.PINK, Color.ORANGE,
            new Color(165, 42, 42), new Color(255, 127, 80), new Color(0, 100, 0), new Color(75, 0 ,130),
            new Color(135, 206, 250), new Color(119, 136, 153), new Color(128, 128, 0)};
    private JTextField pointsTextField;
    private JTextField clustersTextField;
    private JTextField forMinkowskiTextField;
    private JComboBox distanceComboBox;



    public KmeansClustering() {

        mainFrame = new JFrame("K-means clustering - Tomasz Trzos");
        mainPanel = new JPanel(new BorderLayout());
        JPanel coordsPanel = new JPanel();

        coordsPanel.setBackground(Color.WHITE);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        bottomPanel.setBackground(Color.WHITE);

        String[] distanceChoices = { "euclidean", "manhattan", "chebyshev", "minkowski"};
        distanceComboBox = new JComboBox(distanceChoices);
        distanceComboBox.setSelectedIndex(0);
        distanceComboBox.setBackground(Color.WHITE);

        JButton startButton = new JButton("START");
        startButton.addActionListener(l -> start());

        pointsTextField = new JTextField(10);
        clustersTextField = new JTextField(10);
        forMinkowskiTextField = new JTextField(3);

        bottomPanel.add(distanceComboBox);
        bottomPanel.add(new JLabel("Points:"));
        bottomPanel.add(pointsTextField);
        bottomPanel.add(new JLabel("Clusters:"));
        bottomPanel.add(clustersTextField);
        bottomPanel.add(new JLabel("P-value for Minkowski"));
        bottomPanel.add(forMinkowskiTextField);

        bottomPanel.add(startButton);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        CoordinateSystem cS = new CoordinateSystem();
        coordsPanel.add(cS);

        coordsPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
        mainPanel.add(coordsPanel, BorderLayout.CENTER);
        mainFrame.add(mainPanel);
        mainFrame.pack();

        // JFrame options
        mainFrame.setResizable(false);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        mainFrame.setVisible(true);

    }

    public void start() {
        iterationCounter = 0;

        try {
            int amountOfPoints = Integer.parseInt(pointsTextField.getText());
            int amountOfClusters = Integer.parseInt(clustersTextField.getText());

            new Thread(() -> startAlgorithm(amountOfPoints, amountOfClusters)).start();
        } catch(NumberFormatException e) {
            JOptionPane.showMessageDialog(mainFrame,
                    "Enter amount of points and amount of clusters!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void startAlgorithm(int amountOfPoints, int amountOfClusters) {

        points = Collections.synchronizedList(new ArrayList<>());

        // initialize random points to pointList

        for(int i=0; i<amountOfPoints; i++) {
            points.add(new Point());
        }

        // initialize random clusters

        if(amountOfClusters > 15) {
            JOptionPane.showMessageDialog(mainFrame,
                    "Amount of clusters should be less or equal to 15",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        } else {

            clusters = Collections.synchronizedList(new ArrayList<>());

            for (int i = 0; i < amountOfClusters; i++) {
                Point clusterPoint = new Point();
                clusterPoint.setClusterColor(colors[i]);
                clusters.add(clusterPoint);
            }

            // stop condition: If any point does not change his cluster the algorithm can stop
            boolean stopCondition = false;
            long startTime = System.nanoTime();

            while (!stopCondition) {
                iterationCounter++;

                stopCondition = true;
                memoryOfPoints = Collections.synchronizedList(new ArrayList<>());

                for (Point point : points) {
                    memoryOfPoints.add(new Point(point.getX(), point.getY(), point.getClusterColor())); // copy data about point before editing
                    double minDistance = 999; // value much more higher than maximum in this exercise

                    for (Point cluster : clusters) {

                        double distance = 0;
                        if (distanceComboBox.getSelectedItem().toString().equals("chebyshev")) {
                            distance = Distance.chebyshev(point.getX(), point.getY(), cluster.getX(), cluster.getY());
                        } else if (distanceComboBox.getSelectedItem().toString().equals("manhattan")) {
                            distance = Distance.manhattan(point.getX(), point.getY(), cluster.getX(), cluster.getY());
                        } else if (distanceComboBox.getSelectedItem().toString().equals("minkowski")) {
                            double p;
                            if (forMinkowskiTextField.hashCode() == 0) {
                                p = Double.parseDouble(forMinkowskiTextField.getText());
                            } else {
                                p = 0.8;
                            }

                            distance = Distance.minkowski(point.getX(), point.getY(), cluster.getX(), cluster.getY(), p);
                        } else {
                            distance = Distance.euclidean(point.getX(), point.getY(), cluster.getX(), cluster.getY());
                        }

                        if (distance < minDistance) { // assign cluster to point
                            minDistance = distance;

                            point.setCluster(cluster);
                            point.setClusterColor(cluster.getClusterColor());
                        }
                    }
                }

                List<Point> newClusters = new ArrayList<>();
                for (Point cluster : clusters) {
                    double x = 0;
                    double y = 0;
                    double amount = 0;

                    for (Point point : points) {
                        if (point.getCluster() == cluster) { // sum points values
                            x = x + point.getX();
                            y = y + point.getY();
                            amount++;
                        }
                    }

                    x = x / amount;
                    y = y / amount;

                    if (Double.isNaN(x) || Double.isNaN(y)) { // if cluster doesn't have any points
                        // do nothing
                    } else {
                        Point newCluster = new Point(x, y);
                        newCluster.setClusterColor(cluster.getClusterColor()); // same color as before
                        newClusters.add(newCluster);
                    }
                }

                clusters = newClusters; // replace old clusters with new ones


                for (int i = 0; i < points.size(); i++) { // check if any of points change their group
                    if (points.get(i).getClusterColor() != memoryOfPoints.get(i).getClusterColor()) {
                        stopCondition = false;
                    }
                }

                if (iterationCounter == 1000) {
                    stopCondition = true;
                    JOptionPane.showMessageDialog(mainFrame,
                            "Max iteraions = 1000",
                            "Algorithm stopped",
                            JOptionPane.WARNING_MESSAGE);
                }

            }

            long stopTime = System.nanoTime();

            double seconds = (double)(stopTime - startTime) / 1000000000.0;

            JOptionPane.showMessageDialog(mainFrame, "Iterations: " + iterationCounter + "\nTime: " + seconds + " sec");


        }

    }

    class CoordinateSystem extends JPanel implements Runnable {

        private Graphics2D g2d;
        private final int DELAY = 50;
        private Thread animator;

        CoordinateSystem() {}

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(800, 800);
        }

        protected void paintComponent(Graphics g) {

            int yMax = 100;
            int xMin = -100;
            int panelSizeY = 200; // yRange
            int panelSizeX = 200; // xRange

            super.paintComponent(g);
            g2d = (Graphics2D) g.create();

            g2d.drawLine(0, yMax * (getHeight() / panelSizeY), getWidth(), yMax * (getHeight() / panelSizeY));
            g2d.drawLine(-xMin * (getWidth() / panelSizeX), 0, -xMin * (getWidth() / panelSizeX), getHeight());

            int zeroX = -xMin * (getWidth() / panelSizeX) - 4;
            int zeroY = yMax * (getHeight() / panelSizeY) - 4;

            if (points != null) {
                for (int i=0; i< points.size(); i++) {
                    int x = (int) points.get(i).getX();
                    int y = (int) points.get(i).getY();

                    g2d.setColor(points.get(i).getClusterColor());
                    g2d.fillOval(zeroX + x * 4, zeroY - y * 4, 8, 8);
                    repaint();
                }
            }

            if (clusters != null) {
                for (Point cluster : clusters) {
                    int x = (int) cluster.getX();
                    int y = (int) cluster.getY();

                    g2d.setColor(cluster.getClusterColor());
                    g2d.fillRect(zeroX + x * 4, zeroY - y * 4, 8, 8);
                    repaint();

                }
            }

            g2d.dispose();

        }

        @Override
        public void run() {
            long beforeTime, timeDiff, sleep;

            beforeTime = System.currentTimeMillis();

            while (true) {
                repaint();

                timeDiff = System.currentTimeMillis() - beforeTime;
                sleep = DELAY - timeDiff;

                if (sleep < 0)
                    sleep = 2;
                try {
                    Thread.sleep(sleep + 1000);
                } catch (InterruptedException e) {
                    System.out.println("interrupted");
                }

                beforeTime = System.currentTimeMillis();
            }
        }

        @Override
        public void addNotify() {
            super.addNotify();
            animator = new Thread(this);
            animator.start();
        }

    }
}
