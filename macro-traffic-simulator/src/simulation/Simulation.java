package simulation;

import driver.Driver;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import network.Edge;
import network.Graph;

public class Simulation<T extends Driver> {

    //Traffic network
    private final Graph graph;
    //List of drivers
    private final List<T> drivers;

    //Multi core objects
    //Factory class to create ExecuterServices instances
    private final ExecutorService eservice = Executors.newFixedThreadPool(Params.CORES);
    //Task executor
    private final CompletionService<Object> cservice = new ExecutorCompletionService<>(eservice);

    //results parameters;
    //header of od pairs
    private String header = "";

    public Simulation(Graph graph, List<T> drivers) {
        this.graph = graph;
        this.drivers = drivers;
    }

    public void start() {
        //reset the simulation
        this.reset();

        //run the episodes
        Params.EG_EPSILON = Params.EG_EPSILON_DEFAULT;

        while (Params.EPISODE < Params.NUM_EPISODES) {
            this.runEpisode();
            Params.EG_EPSILON = Params.EG_EPSILON * Params.EG_DECAYRATE;

            this.printCostResults();
//            this.printLinksFlow();
//            System.out.println("");

        }

//        for(Driver d:this.drivers){
//            d.printRoute();
//        }
        //shutdown the multicore processing
        this.eservice.shutdown();
    }

    private void reset() {
        //must remove
        Params.EG_EPSILON = Params.EG_EPSILON_DEFAULT;

        //current iteration
        Params.STEP = 0;

        //current episode
        Params.EPISODE = 0;

    }

    private boolean runEpisode() {
        Params.EPISODE++;
        //reset some attributes before running episode
        Params.STEP = 0;

        Params.PHI_MSA = 1.0f / Params.EPISODE;

        //process drivers before episode
        for (Driver d : this.drivers) {
            d.reset();
            d.beforeEpisode();
        }
        while (Params.STEP < Params.NUM_STEPS) {
            //return false if "all agents arrived" stop criterium has been reached
            if (!this.step()) {
                break;
            }
        }

        //process edges after episode
        for (Edge e : this.graph.getEdges()) {
            e.afterEpisode();
        }

        //process drivers after episode
        for (Driver d : this.drivers) {
            d.afterEpisode();
        }
        return true;
    }

    private boolean step() {

        boolean finished = true;
        List<Driver> driversToProcess = new LinkedList<>();

        for (T d : this.drivers) {
            if (!d.hasArrived() && d.mustBeProcessed()) {
                finished = false;
                driversToProcess.add(d);
            }
        }
        if (finished) {
            return false;
        }

        Params.STEP++;

        for (Driver driver : driversToProcess) {
            this.cservice.submit(driver);
        }

        for (Driver driver : driversToProcess) {
            try {
                boolean result = this.cservice.take().isDone();
                if (!result) {
                    System.out.println("step A error");
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(Simulation.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        //intermediate computation
        for (Edge e : this.graph.getEdges()) {
            e.clearVehiclesHere();
            //clean the total flow on the first step.
            if (Params.STEP == 1) {
                e.clearTotalFlow();
            }
        }
        for (Driver d : driversToProcess) {
            if (!d.hasArrived()) {
                d.getCurrent_road().incVehiclesHere();
            }

        }

        for (Edge e : this.graph.getEdges()) {
            e.updateCost();
        }

        //step_b
        for (Driver driver : driversToProcess) {
            this.cservice.submit(driver);
        }

        for (Driver driver : driversToProcess) {
            try {
                boolean result = this.cservice.take().isDone();
                if (!result) {
                    System.out.println("step_b error");
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(Simulation.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return true;

    }

    /**
     * Obtem o desvio padrão de uma Lista de numeros passados
     *
     * @param objects
     * @return
     */
    public float standardDeviation(List<Float> objects) {
        if (objects.size() == 1) {
            return 0.0f;
        } else {
            Float arithmeticMean = arithmeticMean(objects);
            Float sum = 0f;
            for (Float object : objects) {
                Float result = object - arithmeticMean;
                sum = sum + result * result;
            }
            return (float) Math.sqrt(((float) 1 / (objects.size() - 1))
                    * sum);
        }
    }

    /**
     * Obtem o a media aritmetica de um array de Elementos
     *
     * @param objects
     * @return
     */
    private float arithmeticMean(List<Float> objects) {
        float sum = 0l;
        for (Float d : objects) {
            sum += d;
        }
        return sum / objects.size();
    }

    public void printLinksFlow() {
        List<Edge> list = this.graph.getEdges();
        Collections.sort(list);
        //overal cost
        System.out.println("link\tFlow\tCost\tTot. Flow \tTot. Cost\tMsaFlow\tMsaCost");
        //Method os successive averages
//        System.out.println("link\tmasFlow\tmasCost");
        for (Edge e : list) {

            System.out.println(e.toString() + "\t" + e.getVehiclesHere() + "\t" + e.getCost() + "\t" + e.getTotalFlow() + "\t" + e.getAcumulatedCost() + "\t" + e.getMsaFlow() + "\t" + e.msaCost());
//            System.out.println(e.toString() + "\t" + e.getMsaFlow() + "\t" + e.msaCost());
        }
    }

    private String getTravelTimeResults() {

        if (!Params.PRINT_ON_EPISODE && Params.EPISODE < Params.NUM_EPISODES) {
            return "";
        }

        String output = "";

        //map for saving travel time per od
        Map od_results = new HashMap<>(this.graph.getEdges().size());
        //output values
        String values = "";

        //populate the od_pairs
        int drivers_size = 0;

        for (Driver driver : this.drivers) {
            double[] value = new double[2];
            if (driver.mustBeProcessed()) {
                drivers_size++;
                //verifies if key exists
                if (od_results.get(driver.getODPair()) != null) {
                    //update the value
                    //increment de number of vehicles on OD pair
                    value[0] = ((double[]) od_results.get(driver.getODPair()))[0] + 1;
                    //increment the travel time
                    value[1] = ((double[]) od_results.get(driver.getODPair()))[1] + driver.getTravelTime();
                    od_results.put(driver.getODPair(), value);
                } else {
                    //create a new entry
                    value[0] = 1;
                    value[1] = driver.getTravelTime();
                    od_results.put(driver.getODPair(), value);
                }

            }
        }

        //overall travel time 
        double overall_travel_time = 0d;

        for (Object object : od_results.keySet()) {
            //verifies if the header needs to be created

            if (Params.PRINT_ON_EPISODE && Params.CURRENT_RUN == 1 && Params.EPISODE == 1) {
                this.header += object.toString() + ";";
            }
            if (!Params.PRINT_ON_EPISODE) {
                this.header += object.toString() + ";";
            }
            //average travel time on OD pair
            double avg_tt = Math.abs(((double[]) od_results.get(object.toString()))[1] / ((double[]) od_results.get(object.toString()))[0]);
            //put the travel time on the output String value
            values += String.format("%.4f", avg_tt) + ";";

            //increment the overall travel time
            overall_travel_time += (avg_tt * ((double[]) od_results.get(object.toString()))[0]);

            //increment the travel time
//            overall_travel_time += avg_tt;
        }

        overall_travel_time = overall_travel_time / drivers_size;

        //verify if is needed to print the episode's result
        if ((Params.PRINT_ON_EPISODE)) {
            //verify if is needed to print the header
            if (Params.EPISODE == 1) {
                output = ("episode;avg;" + this.header + "\n");
            }
            //print the results
            output += (Integer.toString(Params.EPISODE) + ";" + String.format("%.4f", overall_travel_time) + ";" + values);
        } else {
            //print the simulation results
            if (Params.CURRENT_RUN == 1) {
                output = ("run;avg;" + this.header + "\n");
            }

            output += (Integer.toString(Params.CURRENT_RUN) + ";" + String.format("%.4f", overall_travel_time) + ";" + values);
        }
        output += "\n";
        return output;
    }

    public void printCostResults() {

        //the simulation's output
        String output = this.getTravelTimeResults();

        //print on terminal
        if (Params.PRINT_ON_TERMINAL) {
            System.out.print(output);
        }

        //print on file
        if (Params.PRINT_ON_FILE) {
            boolean printOnEnd;
//            String path = "";
            String file_name = "";
            //print at each episode?
            if (Params.PRINT_ON_EPISODE) {
                //clean the file at first episode
                printOnEnd = Params.EPISODE != 1;
                file_name = File.separator + Params.CURRENT_RUN;

            } else {
                printOnEnd = Params.CURRENT_RUN != 1;
                file_name = File.separator + Params.RUNS + "_runs_results"+"a="+Params.QL_ALPHA+"g="+Params.QL_GAMMA;
            }

            try {
                if (this.createDirectory(Params.getDirectoryName())) {
                    FileWriter file = new FileWriter(Params.getDirectoryName() + file_name + ".csv", printOnEnd);
                    PrintWriter print = new PrintWriter(file);
                    print.print(output);
                    print.close();
                    file.close();
                } else {
                    System.err.println("Erro ao criar diretorio: " + Params.getDirectoryName());
                }

            } catch (IOException ex) {
                Logger.getLogger(Simulation.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public boolean createDirectory(String path) {
        File file = new File(path);
        //verifies if file exisits
        if (!file.exists()) {
            //create files
            return file.mkdirs();
        } else {
            return true;
        }

    }
}
