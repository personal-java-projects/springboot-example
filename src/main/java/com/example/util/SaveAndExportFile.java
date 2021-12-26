package com.example.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class SaveAndExportFile {
    public static File saveFile (String location, String filename, String extension, String data) throws IOException {
        File file = new File(location+filename+extension);

        //if file doesnt exists, then create it
        if(!file.exists()){
            file.createNewFile();
        }

        //true = append file
        FileWriter fileWritter = new FileWriter(file.getName(),true);
        BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
        bufferWritter.write(data);
        bufferWritter.close();

        return file;
    }
}
