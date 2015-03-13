
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author rgrunitzki
 */
public class AlphaGammaAnalysis {

    public static void main(String[] args) throws FileNotFoundException, IOException {
        //Directory of the n files
        String directory_path = "/home/gauss/rgrunitzki/simulations_tests/Ortuzar/IQ-learning/";
        BufferedReader reader = null;
        //Line to analyse
        String line = "";
        String csvDivisor = ";";

        
        String filesToRead[] = new File(directory_path).list();
        Arrays.sort(filesToRead);
        System.out.println(filesToRead.length);

        for (String file : filesToRead) {
            List<Float> avgCost = new ArrayList<>();
            reader = new BufferedReader(new FileReader(directory_path + file));
            int row = 0;
            while ((line = reader.readLine()) != null) {
                if (row > 0) {
                    String values[] = line.split(csvDivisor);
                    avgCost.add(Float.parseFloat(values[1]));
                }
                row++;

            }

            System.out.println(file.split("/")[file.split("/").length - 1] + csvDivisor + arithmeticMean(avgCost) + csvDivisor + standardDeviation(avgCost));
        }

    }

    public static float standardDeviation(List<Float> objects) {
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
    private static float arithmeticMean(List<Float> objects) {
        float sum = 0l;
        for (Float d : objects) {
            sum += d;
        }
        return sum / objects.size();
    }

}
