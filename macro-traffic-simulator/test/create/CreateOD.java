package create;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author grunitzki
 */
public class CreateOD {

    public static void main(String[] args) throws IOException {
        String local = "/home/grunitzki/Downloads/factor4.od";
        File file = new File(local);

        if (file.exists()) {
            System.out.println("arquivo aberto");

            try {
                //arquivo para leitura
                FileReader reader = new FileReader(file);
                //leitor
                BufferedReader buffer = new BufferedReader(reader);

                System.out.println("<od-matrix>");

                int index = 1;
                String district = "";
                while (true) {
                    String line = buffer.readLine();
                    if (line == null) {
                        break;
                    } else if (!line.contains("*")) {
                        String[] rows = line.split(";");
                        for (int i = 1; i<=rows.length; i++){
                            System.out.println("\t<od" 
                                + " origin=\"" + district + "\""
                                + " destination=\"" + i + "\""
                                + " trips=\"" + Integer.parseInt(rows[i-1])*100 + "\""
                                + "/>");
                        }
                    } else{
                        district = line.split(";")[2];
                    }
                }
                System.out.println("</od-matrix>");

            } catch (FileNotFoundException ex) {
                Logger.getLogger(CreateNet.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }
}
