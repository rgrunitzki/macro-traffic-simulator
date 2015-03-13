/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
public class CreateNet {

    public static void main(String[] args) throws IOException {
        String local = "/home/grunitzki/Downloads/SiouxFalls_net.txt";
        File file = new File(local);

        if (file.exists()) {
            System.out.println("arquivo aberto");

            try {
                //arquivo para leitura
                FileReader reader = new FileReader(file);
                //leitor
                BufferedReader buffer = new BufferedReader(reader);

                System.out.println("<graph>");
                int node_count = 24;
                for (int i = 1; i <= node_count; i++) {
                    System.out.println("\t<node id=\"" + i + "\"/>");
                }

                System.out.println("");

                int index = 1;
                while (true) {
                    String line = buffer.readLine();
                    if (line == null) {
                        break;
                    }

                    String[] rows = line.split(";");
                    System.out.println("\t<edge id=\"" + index++ + "\""
                            + "\tfrom=\"" + rows[1] + "\""
                            + "\tto=\"" + rows[2] + "\""
                            + "\tweight=\"" + rows[3] + "\""
                            + "\tlenght=\"" + rows[4] + "\""
                            + "\tfftime=\"" + rows[5] + "\""
                            + "\tbeta=\"" + rows[6] + "\""
                            + "\tpower=\"" + rows[7] + "\""
                            + "/>");
                }
                System.out.println("</graph>");

            } catch (FileNotFoundException ex) {
                Logger.getLogger(CreateNet.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

}
