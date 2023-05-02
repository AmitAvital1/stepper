package project.java.stepper.dd.impl.file;

import java.io.File;

public class FileData {
    private final String fileName;
    private final String filePath;

    public FileData(String filePath){
        File file = new File(filePath); // create a new file object with the specified file path
        fileName = file.getName(); // get the name of the file
        this.filePath = filePath;
    }
    public String getFilePath() {return filePath;}
    public String getFileName() {return fileName;}

}
