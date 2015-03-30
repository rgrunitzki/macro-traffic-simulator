
import driver.learning.DifferenceQLearning;
import driver.Driver;
import driver.learning.IndividualQLearning;
import driver.learning.RouteChoiceQLearning;
import driver.assignment.AllOrNothing;
import driver.assignment.IncrementalAssignment;
import driver.assignment.SuccessiveAverages;
import driver.learning.LARouteChoiceLearner;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import network.BypassCostFunction;
import network.Graph;
import simulation.Algorithm;
import simulation.Network;
import simulation.Params;
import simulation.ProcessFiles;
import simulation.Simulation;
import network.CostFunction;
import network.VolumeDelayFunction;
import network.OWFunction;
public class Main {

    public static void main(String[] args) throws NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {

        /*
         *Set global variables
         */
        Params.QL_ALPHA = 0.5f;
        Params.QL_GAMMA = 0.99f;
        Params.NUM_EPISODES = 1000;//
        Params.EG_DECAYRATE = 0.99f;//
        Params.NUM_STEPS = 100;
        Params.PRINT_ON_FILE = false;
        Params.PRINT_ON_TERMINAL = true;
        Params.PRINT_ON_EPISODE = true;
        Params.NETWORK = Network.Ortuzar;
        Params.K_ROUTES = 8;
        Params.ALGORITHM = Algorithm.IQLearning;

        Params.QL_ALPHA = 0.5f;
        Params.QL_GAMMA = 0.99f;
        Params.RUNS = 1;

        /*
         *Set the network
         */
        Graph graph = defineNetwork(Params.NETWORK);
        Params.DEMAND_FACTOR = 1.0f;

        System.out.println("experiment_id\t" + Params.EXPERIMENT_ID + "\tK=" + Params.K_ROUTES);

        for (Params.CURRENT_RUN = 1; Params.CURRENT_RUN <= Params.RUNS; Params.CURRENT_RUN++) {

            //force the garbage collection
            System.gc();
            //start time
            long time = System.currentTimeMillis();
            Simulation simulation = null;

            switch (Params.ALGORITHM) {
                case IQLearning:
                    simulation = experimentIndividualQLearning(graph);
                    break;
                case DQLearning:
                    simulation = experimentDifferenceQLearning(graph);
                    break;
                case QLRouteChoice:
                    simulation = experimentQLRouteChoice(graph);
                    break;

                case LearningAutomata:
                    simulation = experimentLALearning(graph);
                    break;
                case AllOrNothing:
                    simulation = experimentAllOrNothing(graph);
                    break;
                case IncrementalAssignment:
                    simulation = experimentIncrementalAssignment(graph);
                    break;
                case SuccessiveAverages:
                    simulation = experimentSuccessiveAverages(graph);
                    break;
            }

            simulation.start();
            simulation.printLinksFlow();

            //print the simulation time
//            System.out.println("Simulation time...\n\t " + getTime(System.currentTimeMillis() - time));
        }

    }

    private static void freeUbuntuMemory() {
        String command = "echo 3 > /proc/sys/vm/drop_caches & sysctl -w vm.drop_caches=3";
        Runtime runtime = Runtime.getRuntime();
        try {
            Process process = runtime.exec(command);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
        }
    }

    private static Simulation experimentIndividualQLearning(Graph graph) throws NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        if (Params.CURRENT_RUN == 1) {
//            System.out.println("Creating drivers...");
            // Params.DIRECTORY_TO_PRINT
        }
        List<Driver> drivers = ProcessFiles.processODMatrix(graph, new File(Params.OD_MATRIZ_FILE), Params.DEMAND_FACTOR, IndividualQLearning.class);
        if (Params.CURRENT_RUN == 1) {
//            System.out.println("\t" + drivers.size() + " drivers created");
        }

        return new Simulation(graph, drivers);
    }

    private static Simulation experimentDifferenceQLearning(Graph graph) throws NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        if (Params.CURRENT_RUN == 1) {
            System.out.println("Creating drivers...");
            // Params.DIRECTORY_TO_PRINT += File.separator + "difference";
        }
        List<Driver> drivers = ProcessFiles.processODMatrix(graph, new File(Params.OD_MATRIZ_FILE), Params.DEMAND_FACTOR, DifferenceQLearning.class);
        if (Params.CURRENT_RUN == 1) {
            System.out.println("\t" + drivers.size() + " drivers created");
        }

        return new Simulation(graph, drivers);
    }

    private static Simulation experimentQLRouteChoice(Graph graph) throws NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
//        Params.EG_DECAYRATE = 0.81f;
        if (Params.CURRENT_RUN == 1) {
            System.out.println("Creating drivers...");
            // Params.DIRECTORY_TO_PRINT += File.separator + "route_choice";
        }
        List<Driver> drivers = ProcessFiles.processODMatrix(graph, new File(Params.OD_MATRIZ_FILE), Params.DEMAND_FACTOR, RouteChoiceQLearning.class);
        if (Params.CURRENT_RUN == 1) {
            System.out.println("\t" + drivers.size() + " drivers created");
        }

        return new Simulation(graph, drivers);
    }

    private static Simulation experimentLALearning(Graph graph) throws NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        if (Params.CURRENT_RUN == 1) {
            System.out.println("Creating drivers...");
            // Params.DIRECTORY_TO_PRINT += File.separator + "difference";
        }
        List<Driver> drivers = ProcessFiles.processODMatrix(graph, new File(Params.OD_MATRIZ_FILE), Params.DEMAND_FACTOR, LARouteChoiceLearner.class);
        if (Params.CURRENT_RUN == 1) {
            System.out.println("\t" + drivers.size() + " drivers created");
        }

        return new Simulation(graph, drivers);
    }

    private static Simulation experimentAllOrNothing(Graph graph) throws NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Params.NUM_EPISODES = 1;
        Params.K_ROUTES = 1;
        Params.RUNS = 1;
        System.out.println("Creating drivers...");
        // Params.DIRECTORY_TO_PRINT += File.separator + "all_or_nothing";
        List<Driver> drivers = ProcessFiles.processODMatrix(graph, new File(Params.OD_MATRIZ_FILE), Params.DEMAND_FACTOR, AllOrNothing.class);
        System.out.println("\t" + drivers.size() + " drivers created");
        return new Simulation(graph, drivers);
    }

    private static Simulation experimentIncrementalAssignment(Graph graph) throws NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Params.NUM_EPISODES = Params.INC_FACTOR.length;
        Params.NUM_STEPS = 101;
        Params.RUNS = 1;
        System.out.println("Creating drivers...");
        List<Driver> drivers = new ArrayList<>();
        for (float incrementalFactor : Params.INC_FACTOR) {
            List<Driver> factor_d = ProcessFiles.processODMatrix(graph, new File(Params.OD_MATRIZ_FILE), Params.DEMAND_FACTOR * incrementalFactor, IncrementalAssignment.class);
            System.out.println("\tPn(" + incrementalFactor + "): " + factor_d.size() + " drivers");
            drivers.addAll(factor_d);
        }
        System.out.println("\t" + drivers.size() + " drivers created");
        return new Simulation(graph, drivers);
    }

    private static Simulation experimentSuccessiveAverages(Graph graph) throws NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Params.NUM_EPISODES = 1000;
        Params.RUNS = 1;
        System.out.println("Creating drivers...");
        //Params.DIRECTORY_TO_PRINT += File.separator + "successive_averages";
        List<Driver> drivers = ProcessFiles.processODMatrix(graph, new File(Params.OD_MATRIZ_FILE), Params.DEMAND_FACTOR, SuccessiveAverages.class);
        System.out.println("\t" + drivers.size() + " drivers created");
        return new Simulation(graph, drivers);
    }

    private static Graph defineNetwork(Network network) {
        System.out.println("Creating network...");
        CostFunction costFunction = null;
        switch (network) {
            case Ortuzar:
                //Ortuzar network
                Params.NETWORK_FILE = "files/ortuzar.net.xml"; //original network
                Params.OD_MATRIZ_FILE = "files/ortuzar.od.xml";
                //Params.DIRECTORY_TO_PRINT += File.separator + "ortuzar";
                costFunction = new OWFunction();
                break;

            case OrtuzarModified:
                Params.NETWORK_FILE = "files/ortuzar_modified.net.xml"; //modifed
                Params.OD_MATRIZ_FILE = "files/ortuzar.od.xml";
                // Params.DIRECTORY_TO_PRINT += File.separator + "ortuzar_modified";
                costFunction = new OWFunction();
                break;
            case SiouxFalls:
                //Sioux Falls network
                Params.NETWORK_FILE = "files/siouxfalls.net.xml";
                Params.OD_MATRIZ_FILE = "files/siouxfalls.od.xml";
                Params.DEMAND_FACTOR = 1.0f;
                // Params.DIRECTORY_TO_PRINT += File.separator + "sioux_falls";
                costFunction = new VolumeDelayFunction();
                break;
            case TwoNodes:
                Params.NETWORK_FILE = "files/two_nodes.net.xml";
                Params.OD_MATRIZ_FILE = "files/two_nodes.od.xml";
                Params.DEMAND_FACTOR = 1f;
                // Params.DIRECTORY_TO_PRINT += File.separator + "two_nodes";
                costFunction = new VolumeDelayFunction();
                break;

            case Bypass:
                Params.NETWORK_FILE = "files/bypass.net.xml";
                Params.OD_MATRIZ_FILE = "files/bypass.od.xml";
                Params.DEMAND_FACTOR = 1f;
                costFunction = new BypassCostFunction();
                break;

        }
        return ProcessFiles.processGraph(new File(Params.NETWORK_FILE), costFunction);
    }

    private static String getTime(long time) {
        return (new SimpleDateFormat("mm:ss:SSS")).format(new Date(time));
    }
}