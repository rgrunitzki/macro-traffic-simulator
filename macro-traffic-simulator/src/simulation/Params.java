package simulation;

import java.io.File;
import java.util.Random;

public class Params {

    //General parameters
    public static boolean PRINT_ON_EPISODE = true;
    public static boolean PRINT_ON_FILE = false;
    public static boolean PRINT_ON_TERMINAL = true;
    public static String DIRECTORY_TO_PRINT = "/home/maslab/rgrunitzki_simulations";
//    public static String DIRECTORY_TO_PRINT = "/home/gauss/rgrunitzki/Dropbox/Profissional/UFRGS/Submissões/ANPET 2014/results";
    public static float DEMAND_FACTOR = 1.0f;
    public static float CAPACITY_FACTOR = 1.0f;
    //Output results
    public static String NETWORK_FILE = "files/ortuzar.net.xml";
    public static String OD_MATRIZ_FILE = "iles/ortuzar.od.xml";
    //Simulation Parameters
    public static Algorithm ALGORITHM = Algorithm.DQLearning;
    public static int NUM_EPISODES = 150;
    public static int NUM_STEPS = 100;
    public static int RUNS = 1;
    public static int CURRENT_RUN = 1;
    public static int STEP = 0;
    public static int EPISODE = 0;
    public static final long EXPERIMENT_ID = 1;// System.currentTimeMillis();
    public static Random RANDOM = new Random(EXPERIMENT_ID);
    public static Network NETWORK = Network.OrtuzarModified;
    public static int DEMAND_SIZE = 0;
    public static int CORES = Runtime.getRuntime().availableProcessors();
    //Q-learning-specific parameters
    public static float QL_ALPHA = 0.5f;
    public static float QL_GAMMA = 0.99f;
    public static int K_ROUTES = 8; // K shortest paths
    //Epsilon-greedy-specific parameters
    public static float EG_EPSILON_DEFAULT = 1.0f;
    public static float EG_EPSILON;
    public static float EG_DECAYRATE = 0.92f;
    //Incremental Assignment Parameter
    public static float INC_FACTOR[] = {0.4f, 0.3f, 0.2f, 0.1f};

    //Successive Averages Parameter
    public static float PHI_MSA = 0.5f;

    public static float AL_P_K_SP = 0.0f;//best 0.1
    public static float AL_P_DIJKSTRA = 0f;
    public static float AL_P_DEV_ROUTE = 0f;

    //Learning Automata Parameters
    public static int AL_INACTION_SUM_FACTOR = 150; //avoids negative rewards
    public static float AL_INACTION_ALPHA = 0.7f;//best 0.7

    public static String getDirectoryName() {
        return DIRECTORY_TO_PRINT + File.separator + NETWORK + File.separator + Params.ALGORITHM + File.separator;
    }
}
