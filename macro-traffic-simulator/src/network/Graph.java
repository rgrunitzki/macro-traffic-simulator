package network;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * A graph structure to represent a road network.
 *
 * @author Ricardo Grunitzki
 */
public class Graph {

    /**
     * Map containing the network intersections.
     */
    private HashMap<String, Vertex> vertex;
    /**
     * Map containing the network roads.
     */
    private HashMap<String, Edge> edges;
    /**
     * boolean flag indicating if the graph is directed or not.
     */
    private boolean directed;

    /**
     * Graph constructor
     *
     * @param vertex Map of intersections
     * @param edges Map of roads
     * @param directed flag indicating if the graph is directed or not
     */
    public Graph(HashMap<String, Vertex> vertex, HashMap<String, Edge> edges, boolean directed) {

        this.vertex = vertex;
        this.edges = edges;
        this.directed = directed;

    }

    /**
     * return a vertex by key
     *
     * @param key with a string representing the vertex's id.
     * @return a vertex object.
     */
    public Vertex getVertex(String key) {
        return vertex.get(key);
    }

    /**
     * return an edge by key
     *
     * @param key with a string representing the edge's id.
     * @return an edge object.
     */
    public Edge getEdge(String key) {
        return edges.get(key);
    }

    /**
     * returns a list of edges
     *
     * @return a list of edges
     */
    public List<Edge> getEdges() {
        return new LinkedList<>(edges.values());
    }

    /**
     * returns a list of vertex
     *
     * @return a List object of vertex
     */
    public List<Vertex> getVertex() {
        return new LinkedList<>(vertex.values());
    }

    public boolean isDirected() {
        return directed;
    }

    /**
     * print the network topology on console.
     */
    public void print() {

        System.out.println("Node\t\tOut nodes\t\tIn nodes");
        for (Vertex v : vertex.values()) {
            System.out.print(v + "\t\t");
            for (Edge e : v.getOutEdges()) {
                System.out.print(e.getTo() + ", ");
            }
            System.out.print("\t\t");
            for (Edge e : v.getInEdges()) {
                System.out.print(e.getFrom() + ", ");
            }
            System.out.println("");
        }

    }

    /**
     * print the edge list on console.
     */
    public void printEdges() {
        for (Edge e : edges.values()) {
            System.out.println(e);
        }
    }
}
