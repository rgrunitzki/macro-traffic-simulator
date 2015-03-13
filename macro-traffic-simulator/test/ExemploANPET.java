
import driver.Driver;
import driver.assignment.AllOrNothing;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import network.CostFunction;
import network.Graph;
import network.OWFunction;
import simulation.Params;
import simulation.ProcessFiles;
import simulation.Simulation;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author rgrunitzki
 */
public class ExemploANPET {

    public static void main(String[] args) throws NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {

        /*Parametros da simulação*/
        Params.OD_MATRIZ_FILE = "files/ortuzar.od.xml"; //caminho da matrix OD
        Params.NETWORK_FILE = "files/ortuzar.net.xml"; //caminho da rede
        Params.NUM_EPISODES = 1; //número máximo de episódios
        Params.DEMAND_FACTOR = 1; //fator de multiplicação da demanda
        Params.PRINT_ON_TERMINAL = true; //imprime os resultados no terminal

        /*Gera a rede*/
        File net_xml_file = new File(Params.NETWORK_FILE); //arquivo com a rede
        CostFunction cost_function = new OWFunction(); //função de custo do problema
        Graph network = ProcessFiles.processGraph(net_xml_file, cost_function); //grafo da rede

        /*Gera a demanda*/
        File od_xml_file = new File(Params.OD_MATRIZ_FILE); //arquivo com a matriz OD
        List<Driver> drivers = ProcessFiles.processODMatrix(network, od_xml_file,
                Params.DEMAND_FACTOR, AllOrNothing.class); //lista de motoristas

        /*Gera e executa a simulação*/
        Simulation simulation = new Simulation(network, drivers); //objeto simulação
        simulation.start(); //executa a simulação
        /*Gera os resultados*/
        simulation.printCostResults(); //imprime os custos médios
        //simulation.printLinksFlow(); imprime os fluxos dos links da rede
    }

}