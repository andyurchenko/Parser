package com.company;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;


public class FilesystemHelper {

    private String pathToFilesToParse = null;
    private File csv = null;

    public FilesystemHelper(String inPathToFilesToParse) {
        this.setPathToFilesToParse(inPathToFilesToParse);
        this.setCsv(new File(this.getPathToFilesToParse() + File.separator + "db.csv"));
    }

    public void saveData(Data inData) {
        BufferedWriter writer = this.getWriter();
        this.writeDataToFile(inData, writer);
    }

    private BufferedWriter getWriter() {
        BufferedWriter writer = null;
        try {
            //File csv = new File(this.getPathToFilesToParse() + File.separator + "db.csv");
            writer = new BufferedWriter(new FileWriter(this.getCsv(), true));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return writer;
    }

    private void writeDataToFile(Data inData, BufferedWriter inWriter) {
        if(inWriter != null) {
            try{
                inWriter.write(this.encloseInQuotes(inData.getUrl()), 0, this.encloseInQuotes(inData.getUrl()).length());
                inWriter.write(";", 0, 1);
                inWriter.write(this.encloseInQuotes(inData.getTitle()), 0, this.encloseInQuotes(inData.getTitle()).length());
                inWriter.write(";", 0, 1);
                inWriter.write(this.encloseInQuotes(inData.getDescription()), 0, this.encloseInQuotes(inData.getDescription()).length());
                inWriter.write(";", 0, 1);
                inWriter.write(this.encloseInQuotes(inData.getH1()), 0, this.encloseInQuotes(inData.getH1()).length());
                inWriter.write(";", 0, 1);
                inWriter.write(this.encloseInQuotes(inData.getBestreview()), 0, this.encloseInQuotes(inData.getBestreview()).length());
                inWriter.write(";", 0, 1);
                inWriter.write(this.encloseInQuotes(inData.getBreadcrumbs()), 0, this.encloseInQuotes(inData.getBreadcrumbs()).length());
                inWriter.write(";", 0, 1);
                inWriter.write(this.encloseInQuotes(inData.getFilepath()), 0, this.encloseInQuotes(inData.getFilepath()).length());
                inWriter.newLine();
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
                try {
                    inWriter.close();
                } catch(IOException ex){
                    ex.printStackTrace();
                }
            }
        }
    }

    private String encloseInQuotes(String inString) {
        StringBuilder strBuilder = new StringBuilder();
        if(inString == null || inString.equals("")) {
            return "";
        }
        strBuilder.append("\"");
        for(int i = 0; i < inString.length(); i++) {
            char charInStr = inString.charAt(i);
            if(charInStr  == '\"') {
                strBuilder.append("\"");
            }
            strBuilder.append(charInStr);
        }
        strBuilder.append("\"");
        return strBuilder.toString();
    }

    public void  moveFile(File inFile, String pathToMoveFileTo) {
        try{
            File foldersToCreate = new File(pathToMoveFileTo);
            foldersToCreate.mkdirs();
            Files.move(Paths.get(inFile.getAbsolutePath()), Paths.get(pathToMoveFileTo + inFile.getName()), REPLACE_EXISTING);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    public File[] getFilesToParse() {
        File dirToLookForFilesToParse = new File(this.getPathToFilesToParse());
        if (dirToLookForFilesToParse.isDirectory()) {
            FilenameFilter fileFilter = new FilenameFilter() {
                public boolean accept(File inDir, String inFileName) {
                    String lowercaseName = inFileName.toLowerCase();
                    return (lowercaseName.endsWith(".htm")
                            || lowercaseName.endsWith(".html")
                            || lowercaseName.endsWith(".php")
                            || lowercaseName.endsWith(".txt"));
                }
            };
            return dirToLookForFilesToParse.listFiles(fileFilter);
        } else {
            return null;
        }
    }

    private String getPathToFilesToParse() {
        return pathToFilesToParse;
    }

    private void setPathToFilesToParse(String pathToFilesToParse) {
        this.pathToFilesToParse = pathToFilesToParse;
    }

    private File getCsv() {
        return csv;
    }

    private void setCsv(File csv) {
        this.csv = csv;
    }
}
