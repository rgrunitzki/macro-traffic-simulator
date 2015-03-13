package simulation;

import network.Edge;
import network.Vertex;
import network.Graph;
import driver.Driver;
import driver.assignment.IncrementalAssignment;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import network.CostFunction;

/**
 *
 * @author rgrunitzki
 * @param <T>
 */
public class ProcessFiles<T extends Driver> {

    /**
     * This method process the network XML files and returns a Graph object
     * representing the environment model.
     *
     * @param netFile a network File
     * @return Graph a network Graph
     */
    public static Graph processGraph(File netFile, CostFunction costFunction) {

        HashMap<String, Vertex> V = null;
        HashMap<String, Edge> E = null;

        try {

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(netFile);

            //Create vertices
            NodeList list = doc.getElementsByTagName("node");
            V = new HashMap<>(list.getLength());
            for (int i = 0; i < list.getLength(); i++) {
                Element e = (Element) list.item(i);
                V.put(e.getAttribute("id"), new Vertex(e.getAttribute("id")));
            }

            //Create edges
            list = doc.getElementsByTagName("edge");
            E = new HashMap<>(list.getLength());
            for (int i = 0; i < list.getLength(); i++) {
                Element e = (Element) list.item(i);
                E.put(e.getAttribute("id"), new Edge(
                        e.getAttribute("id"),
                        V.get(e.getAttribute("from")),
                        V.get(e.getAttribute("to")),
                        stringToFloat(e.getAttribute("capacity")) * Params.CAPACITY_FACTOR,
                        isDirected(e.getAttribute("directed")),
                        stringToFloat(e.getAttribute("fftime")),
                        stringToFloat(e.getAttribute("alpha")),
                        stringToFloat(e.getAttribute("beta")),
                        costFunction
                ));
//                this line prints edges' parameters
//                System.out.println(V.get(e.getAttribute("from"))+" \t" + V.get(e.getAttribute("to")) +"\t"+ stringToFloat(e.getAttribute("capacity")) * Params.CAPACITY_FACTOR + "\t" + stringToFloat(e.getAttribute("fftime")));
            }

        } catch (IOException | NumberFormatException | ParserConfigurationException | SAXException e) {
            System.err.println("Error on reading XML file!");
        }

        return new Graph(V, E, true);
    }

    /**
     *
     * @param <T> the type of the drivers
     * @param G the network Graph
     * @param odFile the File contain the OD matrix
     * @param demand_factor the float value multiplied for each pair od
     * @param clazz the Class of the drivers
     * @return a List of pre-defined agents
     * @throws NoSuchMethodException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
    public static <T> List<T> processODMatrix(Graph G, File odFile, float demand_factor, Class clazz) throws NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        List<T> drivers = new ArrayList<>();
        try {

            //objects to manipulate the XML file
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(odFile);

            //get the OD pairs
            NodeList list = doc.getElementsByTagName("od");

            //driver's count
            int countD = 0;

            if (clazz == IncrementalAssignment.class) {
                countD = Params.DEMAND_SIZE;
            }

            //create drivers for each OD pair
            for (int i = 0; i < list.getLength(); i++) {
                Element e = (Element) list.item(i);
                Vertex origin = G.getVertex(e.getAttribute("origin"));
                Vertex destination = G.getVertex(e.getAttribute("destination"));

                //create as many drivers as the number of trips defined
                int size = (int) (Integer.parseInt(e.getAttribute("trips")) * demand_factor);
                for (int d = size; d > 0; d--) {
                    Object driver = clazz.getConstructor(clazz.getConstructors()[0].getParameterTypes()).newInstance(++countD, origin, destination, G);
                    drivers.add((T) driver);
                }
            }
            Params.DEMAND_SIZE = countD;

        } catch (IOException | NumberFormatException | ParserConfigurationException | SAXException e) {
            System.err.println("Error on reading XML file!");
        }

        return drivers;
    }

    private static Float stringToFloat(String value) {
        if (value.equals("")) {
            return 0f;
        } else {
            return Float.parseFloat(value);
        }
    }

    //problemas aqui!!!!
    private static boolean isDirected(String value) {
        return value.equalsIgnoreCase("true");
    }

}
