package network;

import java.util.LinkedList;
import java.util.List;

/**
 * An vertex structure to represent de network's intersections
 * @author Ricardo Grunitzki
 */
public class Vertex {
    
    /**
     * integer identifier.
     */
    private final String id;
    
    /**
     * list of edges with destination in this vertex.
     */
    private List<Edge> inEdges;
    
    /**
     * list of edges with origin in this vertex.
     */
    private List<Edge> outEdges;

    /**
     * vertex constructor
     * @param id String identifier
     */
    public Vertex(String id) {
        this.id = id;

        this.inEdges = new LinkedList<>();
        this.outEdges = new LinkedList<>();

    }
    
    /**
     * returns the vertex name <code>id</code>.
     *@return String representing the vertex identifier
     */
    @Override
    public String toString() {
        return id;
    }

    /**
     * Add a new edge with destination in the vertex
     * @param edge
     */
    public void addInEdge(Edge edge) {
        this.inEdges.add(edge);
    }

    /**
     * Add a new edge with origin in the vertex
     * @param edge
     */
    public void addOutEdge(Edge edge) {
        this.outEdges.add(edge);
    }
    public List<Edge> getInEdges() {
        return inEdges;
    }
    
    public List<Edge> getOutEdges() {
        return outEdges;
    } 

    public String getId() {
        return id;
    }    
}
