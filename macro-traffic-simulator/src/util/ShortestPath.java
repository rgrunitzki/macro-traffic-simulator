package util;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import network.Edge;
import driver.EdgeEntry;
import network.Vertex;

public class ShortestPath {

    public List<Edge> dijkstra(List<Vertex> vertices, Map<String, EdgeEntry> edges, Vertex origin, Vertex destination) {
        return dijkstra(vertices, edges, origin, destination, null);
    }
    
    public List<Edge> dijkstra(List<Vertex> vertices, Map<String, EdgeEntry> edges, Vertex origin, Vertex destination, List<Edge> ignored) {

        List<Vertex> Q = new ArrayList<>();
        Map<Vertex, VertexEntryDijkstra> V = new Hashtable<>();
        for (Vertex v : vertices) {
            V.put(v, new VertexEntryDijkstra(v));
        }

        V.get(origin).dist = 0;
        Q.add(origin);

        Vertex u = null;
        while (Q.size() > 0) {
            u = null;
            for (Vertex v : Q) {
                if (u == null) {
                    u = v;
                } else if (V.get(v).dist < V.get(u).dist) {
                    u = v;
                }
            }
            if (u == destination) {
                break;
            }
            Q.remove(u);
            V.get(u).visited = true;

            for (Edge e : u.getOutEdges()) {
                if (ignored != null && ignored.contains(e)) {
                    continue;
                }
                Vertex v = e.getTo();

                float reward = Math.abs(edges.get(e.toString()).getCost());
//                if (reward < 0) System.err.println("Dijkstra cannot be run for negative valued graphs!");

                float alt = V.get(u).dist + reward;
                if (alt < V.get(v).dist) {
                    V.get(v).dist = alt;
                    V.get(v).previous = u;
                    if (!V.get(v).visited) {
                        Q.add(v);
                    }
                }
            }

        }

        List<Edge> path = new ArrayList<>();
        u = destination;
        while (V.get(u).previous != null) {
//                        String key = V.get(u).previous.toString() + " -- " + u.toString();
//                        EdgeEntry e = edges.get(key);
            path.add(0, edges.get(V.get(u).previous.toString() + " -- " + u.toString()).getEdge());
            u = V.get(u).previous;
        }
        return path;
    }
 
    public class VertexEntryDijkstra {

        Vertex v;
        float dist = Float.MAX_VALUE;
        boolean visited = false;
        Vertex previous = null;

        public VertexEntryDijkstra(Vertex v) {
            this.v = v;
        }
    }

    public List<List<Edge>> kShortestPaths(List<Vertex> vertices, Map<String, EdgeEntry> edges, Vertex origin, Vertex destination, int K) {

        //the K shortest paths
        List<List<Edge>> A = new ArrayList<List<Edge>>();

        //potential shortest paths
        List<List<Edge>> B = new ArrayList<List<Edge>>();

        //k iterations
        for (int k = 0; k < K; k++) {

            //Step 0: iteration 1
            if (k == 0) {
                A.add(dijkstra(vertices, edges, origin, destination));
            } //Step 1: iterations 2 to K
            else {
                float bestCostInB = Float.MAX_VALUE;
                for (int i = 0; i < A.get(k - 1).size(); i++) {

                    //Step I(a)
                    Vertex spurNode = A.get(k - 1).get(i).getFrom();
                    List<Edge> rootPath = A.get(k - 1).subList(0, i);
                    List<Edge> ignore = new ArrayList<Edge>();
                    for (List<Edge> p : A) {
                        if (p.size() >= i && rootPath.equals(p.subList(0, i))) {
                            ignore.add(p.get(i));
                        }
                    }

                    //Step I(b)
                    List<Edge> spurPath = dijkstra(vertices, edges, spurNode, destination, ignore);

                    if ((rootPath.isEmpty() && spurPath.isEmpty()) || spurPath.isEmpty()) {
                        continue;
                    }
                    if (!spurPath.get(spurPath.size() - 1).getTo().equals(destination)) {
                        continue;
                    }

                    //Step I(c)
                    List<Edge> totalPath = new ArrayList<Edge>();
                    totalPath.addAll(rootPath);
                    totalPath.addAll(spurPath);
                    /*double cost = calcCostPath(edges, totalPath);
                     if (B.isEmpty()) {
                     bestCostInB = cost;
                     B.add(totalPath);
                     }
                     else if (cost < bestCostInB) {
                     B.remove(0);
                     bestCostInB = cost;
                     B.add(totalPath);
                     }*/
                    B.add(totalPath);

                }

                List<Edge> best = null;
                for (List<Edge> path : B) {
                    float aux = calcCostPath(edges, path);
                    if (aux < bestCostInB) {
                        best = path;
                        bestCostInB = aux;
                    }
                }

                //Step II
                A.add(best);
                while (B.remove(best));

            }

        }

        return A;

    }

    public float calcCostPath(Map<String, EdgeEntry> edges, List<Edge> path) {
        float c = 0;
        for (Edge e : path) {
            c += edges.get(e.toString()).getCost();
        }
        return c;
    }

}