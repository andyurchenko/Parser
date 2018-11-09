package com.company;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;


public class QueueHelper implements Runnable {

    ConcurrentLinkedQueue<Data> queue = null;
    String PathToCSVFile;
    private File csv = null;
    Thread thread1 = null;
    Thread thread2 = null;
    Thread thread3 = null;


    public QueueHelper(ConcurrentLinkedQueue<Data> inQueue, String inPath, Thread inThread1, Thread inThread2, Thread inThread3) {
        this.queue = inQueue;
        this.setPathToCSVFile(inPath);
        this.setCsv(new File(this.getPathToCSVFile() + File.separator + "db.csv"));
        this.thread1 = inThread1;
        this.thread2 = inThread2;
        this.thread3 = inThread3;
    }

    public void run() {

        while(thread1.isAlive() || thread2.isAlive() || thread3.isAlive()) {
            Data data;
            while ((data = queue.poll()) != null) {
                this.saveData(data);
            }
            try {
                //Thread.currentThread().sleep(200);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private File getCsv() {
        return csv;
    }

    private void setCsv(File csv) {
        this.csv = csv;
    }

    private String getPathToCSVFile() {
        return PathToCSVFile;
    }

    private void setPathToCSVFile(String pathToFilesToParse) {
        this.PathToCSVFile = pathToFilesToParse;
    }

    public void saveData(Data inData) {
        BufferedWriter writer = this.getWriter();
        this.writeDataToFile(inData, writer);
    }

    private BufferedWriter getWriter() {
        BufferedWriter writer = null;
        try {
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
        if(inString == null || inString.equals("")) {
            return "";
        }
        StringBuilder strBuilder = new StringBuilder();
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


}
