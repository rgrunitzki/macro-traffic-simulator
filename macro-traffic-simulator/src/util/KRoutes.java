package util;

import driver.Route;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;


import network.Edge;
import driver.EdgeEntry;
import network.Graph;
import network.Vertex;
import simulation.Params;


public class KRoutes {
	
	//private Graph G;
	private static Map<String, EdgeEntry> edges = null;
	private static Map<String, ArrayList<Route>> routesOD = null;
	
	
	public static ArrayList<Route> getKRoutesOD(Vertex origin, Vertex destination, Graph G) {
		
		//initialize variables
		if (edges == null) {
			edges = new Hashtable<>();
			for (Edge e : G.getEdges()) {
				edges.put(e.toString(), new EdgeEntry(e, e.getFreeFlowCost()));
			}
			routesOD = new Hashtable<>();
		}
		
		//define OD pair
		boolean isNumber = true;
		try { 
	        Integer.parseInt(origin.toString()); 
	    } 
		catch(NumberFormatException e) { 
	        isNumber = false; 
	    }
		String od = origin.toString();
		if (isNumber)
			od += "|";
		od += destination.toString();
		
		//get  the list of routes of the OD pair (if it does not exist, creates it)
		ArrayList<Route> k_routes;
		if (!routesOD.containsKey(od)) {
			k_routes = new ArrayList<>();
			
			//add the K shortests routes to k_routes and then store this list into routesOD
			ShortestPath sp = new ShortestPath();
			for (List<Edge> route : sp.kShortestPaths(G.getVertex(), edges, origin, destination, Params.K_ROUTES)) {
				k_routes.add(new Route(route, sp.calcCostPath(edges, route)));
				//print routes
				//System.out.println(origin +","+ destination +": "+ route + " = " +k_routes.get(k_routes.size()-1).reward);
			}
			
			routesOD.put(od, k_routes);
		}
		else {
			k_routes = routesOD.get(od);
		}
		
		return k_routes;
		
	}
	
	public static void reset() {
		edges = null;
		routesOD = null;
	}
	
}