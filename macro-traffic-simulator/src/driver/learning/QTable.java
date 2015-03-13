package driver.learning;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import network.Edge;
import network.Vertex;

public class QTable {

    private Map<Vertex, HashMap<Edge, Float>> table;

    public QTable(List<Vertex> V) {

        table = new HashMap<>();

        for (Vertex v : V) {
            HashMap<Edge, Float> h = new HashMap<>();
            for (Edge e : v.getOutEdges()) {
                h.put(e, 0.0f);
            }
            table.put(v, h);
        }

    }

    public float getQValue(Vertex s, Edge a) {
        return table.get(s).get(a);
    }

    public void setQValue(Vertex s, Edge a, float q) {
        table.get(s).put(a, q);
    }

    public void print() {
        for (Vertex v : table.keySet()) {
            for (Edge e : table.get(v).keySet()) {
                System.out.println("Q(" + v + "," + e.getTo() + ")=" + table.get(v).get(e));
            }
        }
    }

}
