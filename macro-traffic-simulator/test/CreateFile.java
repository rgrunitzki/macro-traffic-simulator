
import java.io.File;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author rgrunitzki
 */
public class CreateFile {

    public static void main(String[] args) {
        System.out.println("Estou criando o arquivo...");
        String path = "/home/rgrunitzki/teste/xptx/xdls";
        System.out.println(createDirectory(new File(path)));
    }

    public static boolean createDirectory(File file) {
        
        //verifies if file exisits
        if (!file.exists()) {
            //create files
            return file.mkdirs();
        }else{
            return true;
        }

    }

}
