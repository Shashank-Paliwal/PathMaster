import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;



class Graph<T> {
    private Map<T, Map<T, Integer>> adjacencyList;

    public Graph() {
        this.adjacencyList = new HashMap<>();
    }

    public void addNode(T node) {
        if (!adjacencyList.containsKey(node)) {
            adjacencyList.put(node, new HashMap<>());
        }
    }

    public void addEdge(T source, T destination, int weight) {
        addNode(source);
        addNode(destination);

        adjacencyList.get(source).put(destination, weight);
        // If it's an undirected graph, you'd also add the reverse
        // adjacencyList.get(destination).put(source, weight);
    }

    public int getWeight(T source, T destination) {
        if (adjacencyList.containsKey(source) && adjacencyList.get(source).containsKey(destination)) {
            return adjacencyList.get(source).get(destination);
        } else {
            // Handle the case where the edge doesn't exist
            return -1; // You might want to use a special value to indicate absence
        }
    }

    public Set<T> getNodes() {
        return adjacencyList.keySet();
    }

    public Map<T, Integer> getNeighbors(T node) {
        return adjacencyList.getOrDefault(node, new HashMap<>());
    }
}

class DijkstraShortestPath<T> {
    private Graph<T> graph;
    private Map<T, Map<T, T>> shortestPaths; // Store paths in a map
    
    public DijkstraShortestPath(Graph<T> graph) {
        this.graph = graph;
        this.shortestPaths = new HashMap<>();
    }

    public Map<T, Integer> findShortestPaths(T source) {
        // Priority queue to store nodes with their distances
        PriorityQueue<NodeWithDistance<T>> priorityQueue = new PriorityQueue<>(Comparator.comparingInt(NodeWithDistance::getDistance));

        // Map to store the shortest distances from the source
        Map<T, Integer> shortestDistances = new HashMap<>();

        // Initialize distances
        for (T node : graph.getNodes()) {
            shortestDistances.put(node, Integer.MAX_VALUE);
        }
        shortestDistances.put(source, 0);

        priorityQueue.add(new NodeWithDistance<>(source, 0));

        while (!priorityQueue.isEmpty()) {
            NodeWithDistance<T> currentNode = priorityQueue.poll();
            T current = currentNode.getNode();
            int currentDistance = currentNode.getDistance();

            // If the current distance is already greater than the known shortest distance, skip
            if (currentDistance > shortestDistances.get(current)) {
                continue;
            }

            // Explore neighbors
            for (Map.Entry<T, Integer> neighborEntry : graph.getNeighbors(current).entrySet()) {
                T neighbor = neighborEntry.getKey();
                int newDistance = currentDistance + neighborEntry.getValue();

                // If a shorter path is found, update the distance and add to the priority queue
                if (newDistance < shortestDistances.get(neighbor)) {
                    shortestDistances.put(neighbor, newDistance);
                    priorityQueue.add(new NodeWithDistance<>(neighbor, newDistance));
                }
            }
        }

        return shortestDistances;
    }

    public List<T> getPath(T source, T destination) {
        List<T> path = new ArrayList<>();
        T current = destination;

        while (current != null) {
            path.add(current);
            current = shortestPaths.get(current).get(current);
        }

        Collections.reverse(path);
        return path;
    }

    private static class NodeWithDistance<T> {
        private final T node;
        private final int distance;

        public NodeWithDistance(T node, int distance) {
            this.node = node;
            this.distance = distance;
        }

        public T getNode() {
            return node;
        }

        public int getDistance() {
            return distance;
        }
    }
}

class AStarShortestPath<T> {
    private Graph<T> graph;

    public AStarShortestPath(Graph<T> graph) {
        this.graph = graph;
    }

    public Map<T, Integer> findShortestPaths(T source, T destination) {
        // A* search implementation
        // ...
        

        return new HashMap<>(); // Placeholder, you need to implement A* search
    }
}

public class CityPathFinderAppSwing {
    private Graph<String> cityMap;
    private DijkstraShortestPath<String> dijkstraShortestPath;
    private AStarShortestPath<String> aStarShortestPath;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CityPathFinderAppSwing().createAndShowGUI());
    }

    private void createAndShowGUI() {
        // Create a CityGraph instance and populate it with data
        cityMap = new Graph<>();
        // Add nodes and edges to represent the city
        cityMap.addEdge("A", "B", 10);
        cityMap.addEdge("A", "C", 15);
        cityMap.addEdge("B", "D", 12);
        cityMap.addEdge("C", "D", 5);
        cityMap.addEdge("B", "E", 2);
        cityMap.addEdge("D", "E", 10);

        // Create instances for Dijkstra's and A* algorithms
        dijkstraShortestPath = new DijkstraShortestPath<>(cityMap);
        aStarShortestPath = new AStarShortestPath<>(cityMap);

        // Swing UI
        JFrame frame = new JFrame("City Path Finder");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 200);

        JPanel panel = new JPanel(new FlowLayout());
        JLabel startLabel = new JLabel("Start:");
        JTextField startTextField = new JTextField(5);
        JLabel endLabel = new JLabel("End:");
        JTextField endTextField = new JTextField(5);
        JLabel algorithmLabel = new JLabel("Select Algorithm:");
        JComboBox<String> algorithmComboBox = new JComboBox<>(new String[]{"Dijkstra", "A*"});
        JButton findPathButton = new JButton("Find Path");
        JLabel resultLabel = new JLabel();

        findPathButton.addActionListener(e -> {
            String start = startTextField.getText();
            String end = endTextField.getText();
            String selectedAlgorithm = (String) algorithmComboBox.getSelectedItem();

            if (!start.isEmpty() && !end.isEmpty()) {
                // Find the shortest paths based on the selected algorithm
                Map<String, Integer> shortestDistances;
                if ("Dijkstra".equals(selectedAlgorithm)) {
                    shortestDistances = dijkstraShortestPath.findShortestPaths(start);
                } else {
                    shortestDistances = aStarShortestPath.findShortestPaths(start, end);
                }

                // Display the result
                int distance = shortestDistances.getOrDefault(end, -1);
                resultLabel.setText("Shortest distance from " + start + " to " + end + " using " + selectedAlgorithm + ": " + distance);

                // If you also want to display the path, modify DijkstraShortestPath to store paths
                // and retrieve and display the path here.
            } else {
                resultLabel.setText("Please enter both start and end points.");
            }
        });

        panel.add(startLabel);
        panel.add(startTextField);
        panel.add(endLabel);
        panel.add(endTextField);
        panel.add(algorithmLabel);
        panel.add(algorithmComboBox);
        panel.add(findPathButton);
        panel.add(resultLabel);

        frame.add(panel);
        frame.setVisible(true);
    }
}
