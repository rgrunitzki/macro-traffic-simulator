
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author rgrunitzki
 */
public class AverageCost {

    public static void main(String[] args) throws FileNotFoundException, IOException {
        //Directory of the n files
        String directory_path = "/home/gauss/rgrunitzki/Dropbox/Profissional/UFRGS/Doutorado/Artigo TRI15/SF Experiments/IQ-Learning/";
        BufferedReader reader = null;
        //Line to analyse
        String line = "";
        String csvDivisor = ";";
        int totalLines = 1002;
        int totalRows = 532;

        String filesToRead[] = new File(directory_path).list();
        Arrays.sort(filesToRead);
        System.out.println(filesToRead.length);

        List<List<DescriptiveStatistics>> summary = new ArrayList<>();

        for (int i = 0; i <= totalLines; i++) {
            summary.add(new ArrayList<DescriptiveStatistics>());
            for (int j = 0; j <= totalRows; j++) {
                summary.get(i).add(new DescriptiveStatistics());
            }
        }

        //reads all files
        for (String file : filesToRead) {
            reader = new BufferedReader(new FileReader(directory_path + file));
            int lineCounter = 0;
            //reads all file's line
            while ((line = reader.readLine()) != null) {
                if (lineCounter > 0) {
                    String[] rows = line.trim().split(csvDivisor);
                    //reads all line's row
                    for (int r = 0; r < rows.length; r++) {
                        summary.get(lineCounter).get(r).addValue(Double.parseDouble(rows[r]));
                    }
                }
                lineCounter++;
            }

            //System.out.println(file.split("/")[file.split("/").length - 1] + csvDivisor + arithmeticMean(avgCost) + csvDivisor + standardDeviation(avgCost));
        }

        //generate mean and standard deviation;
        for (List<DescriptiveStatistics> summaryLines : summary) {
            System.out.println();
            for (DescriptiveStatistics summaryLineRow : summaryLines) {
                System.out.print(summaryLineRow.getMean() + ";" + summaryLineRow.getStandardDeviation() + ";");
            }
        }
    }
}
