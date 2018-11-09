package com.company;
import java.io.*;
import java.util.concurrent.*;

public class Parser implements Runnable {

    private FilesystemHelper fsHelper = null;
    private DataExtractor dataExtractor = null;
    ConcurrentLinkedQueue<Data> queue = new ConcurrentLinkedQueue<Data>();

    public static void main(String[] args) {
        if(args[0] == null) {
            System.out.println("Please, provide a folder to parse!");
        } else {
            Parser parser = new Parser(args[0]);
            Thread t1 = new Thread(parser);
            t1.setName("t1");
            Thread t2 = new Thread(parser);
            t2.setName("t2");
            Thread t3 = new Thread(parser);
            t3.setName("t3");
            t1.start();
            t2.start();
            t3.start();
            QueueHelper queueHelper = new QueueHelper(parser.getQueue(), args[0], t1, t2, t3);
            Thread t4_queue = new Thread(queueHelper);
            t4_queue.start();
        }
    }

    public Parser(String inPathToFiles) {
        this.setFsHelper(new FilesystemHelper(inPathToFiles, this.queue));
        this.setDataExtractor(new DataExtractor());
    }

    public void run() {
        File fileToParse = null;
        while( (fileToParse = this.getFsHelper().getFileToParse()) != null) {
            this.processFile(fileToParse);
        }
    }

//    private void run() {
//
//        File[] filesToParse = this.getFsHelper().getFilesToParse();
//
//        if(filesToParse != null) {
//            this.processFiles(filesToParse);
//        } else {
//            System.out.println("There are no files to parse in the folder you've provided.");
//        }
//    }

    private void processFile(File inFile) {
        try {
            Thread.sleep(500);
            //System.out.println(Thread.currentThread().getName());
        } catch(InterruptedException ex) {
            ex.printStackTrace();
        }
        Data dataParsed = this.dataExtractor.parseFile(inFile);
        if(this.getFsHelper().saveData(dataParsed)) {
            this.getFsHelper().moveFile(inFile, dataParsed.getPathToMoveFileTo());
        }


    }

//    private void processFiles(File[] inListFiles) {
//        for(File file : inListFiles) {
//            Data dataParsed = this.dataExtractor.parseFile(file);
//            this.getFsHelper().saveData(dataParsed);
//            this.getFsHelper().moveFile(file, dataParsed.getPathToMoveFileTo());
//        }
//    }



    private FilesystemHelper getFsHelper() {
        return fsHelper;
    }

    private void setFsHelper(FilesystemHelper fsHelper) {
        this.fsHelper = fsHelper;
    }

    private DataExtractor getDataExtractor() {
        return dataExtractor;
    }

    private void setDataExtractor(DataExtractor dataExtractor) {
        this.dataExtractor = dataExtractor;
    }

    public ConcurrentLinkedQueue<Data> getQueue() {
        return queue;
    }

    public void setQueue(ConcurrentLinkedQueue<Data> queue) {
        this.queue = queue;
    }

//    private static final int URL = 0;
//    private static final int TITLE = 1;
//    private static final int DESCRIPTION = 2;
//    private static final int H1 = 3;
//    private static final int BESTREVIEW = 4;
//    private static final int BREADCRUMBS = 5;
//    private static final int PAGEPATH = 6;
//    private File pathToFilesToParse;
//    private File csv;
//
//
//    public static void main(String[] args) {
//        Parser parser = new Parser(args[0]);
//        parser.run();
//    }
//
//    public Parser(String inPathToFiles) {
//        this.pathToFilesToParse = new File(inPathToFiles);
//        this.csv = new File(this.pathToFilesToParse.getAbsolutePath() + File.separator + "db.csv");
//        try{
//            this.csv.createNewFile();
//        } catch(IOException ex) {
//            ex.printStackTrace();
//        }
//
//    }
//
//    public void run() {
//        File[] filesToParse = this.getFilesToParse();
//        if(filesToParse != null) {
//            this.processFiles(filesToParse);
//        }
//    }
//
//    private File[] getFilesToParse() {
//        if (this.pathToFilesToParse.isDirectory()) {
//            FilenameFilter fileFilter = new FilenameFilter() {
//                public boolean accept(File inDir, String inFileName) {
//                    String lowercaseName = inFileName.toLowerCase();
//                    return (lowercaseName.endsWith(".htm")
//                            || lowercaseName.endsWith(".html")
//                            || lowercaseName.endsWith(".php")
//                            || lowercaseName.endsWith(".txt"));
//                }
//            };
//            return this.pathToFilesToParse.listFiles(fileFilter);
//        } else {
//            return null;
//        }
//    }
//
//    private void processFiles(File[] inListFiles) {
//        for(File file : inListFiles) {
//            String[] information = getDataFromFile(file);
//            saveData(information);
//            moveFile(information[BREADCRUMBS] ,file);
//        }
//    }
//
//    private String[] getDataFromFile(File inFile) {
//        String[] parsedData = new String[7];
//        try {
//            Document htmlDocument = Jsoup.parse(inFile, null);//html страница, полученная из JSoup после парсинга файла
//            //URL
//            parsedData[URL] = this.getURL(htmlDocument);
//            //Title
//            parsedData[TITLE] = this.getTitle(htmlDocument);
//            //Description
//            parsedData[DESCRIPTION] = this.getDescription(htmlDocument);
//            //H1
//            parsedData[H1] = this.getH1(htmlDocument);
//            //Bestreview
//            parsedData[BESTREVIEW] = this.getBestReview(htmlDocument);
//            //Breadcrumbs
//            parsedData[BREADCRUMBS] = this.getBreadcrumbsForCSV(htmlDocument);
//            //Page path
//            parsedData[PAGEPATH] = this.getFilePath(inFile);
//            return parsedData;
//        } catch(IOException ex) {
//            ex.printStackTrace();
//        }
//        return null;
//    }
//
//    private String getURL(Document inHTMLDocument) {
//        if (inHTMLDocument.selectFirst("meta[property=og:url]") != null) {
//            return inHTMLDocument.selectFirst("meta[property=og:url]").attr("content");
//        } else {
//            return null;
//        }
//    }
//
//    private String getTitle(Document inHTMLDocument) {
//        if (inHTMLDocument.selectFirst("div.o-pdp-topic__title h1[class=e-h1 sel-product-title]") != null) {
//            return inHTMLDocument.selectFirst("div.o-pdp-topic__title h1[class=e-h1 sel-product-title]").text();
//        } else {
//            return null;
//        }
//    }
//
//    private String getDescription(Document inHTMLDocument) {
//        if (inHTMLDocument.selectFirst("div.o-about-product__block div.collapse-text-initial") != null) {
//            return inHTMLDocument.selectFirst("div.o-about-product__block div.collapse-text-initial").text();
//        } else {
//            return null;
//        }
//    }
//
//    private String getBestReview(Document inHTMLDocument) {
//        if (inHTMLDocument.selectFirst("div.review-ext-item-description-item > p > span") != null) {
//            return inHTMLDocument.select("div.review-ext-item-description-item > p > span").text();
//        } else {
//            return null;
//        }
//    }
//
//    private String getH1(Document inHTMLDocument) {
//        if (inHTMLDocument.selectFirst("div.o-pdp-topic__title h1[class=e-h1 sel-product-title]") != null) {
//            return inHTMLDocument.select("div.o-pdp-topic__title h1[class=e-h1 sel-product-title]").text();
//        } else {
//            return null;
//        }
//    }
//
//    private String getBreadcrumbsForCSV(Document inHTMLDocument) {
//        if (inHTMLDocument.select("ul.c-breadcrumbs__list > li.c-breadcrumbs__item > a") != null) {
//            StringBuilder strBreadcrumbs = new StringBuilder(); //строка вида Главная > Ноутбуки > HP
//            Elements breadcrumbs = inHTMLDocument.select("ul.c-breadcrumbs__list > li.c-breadcrumbs__item > a");
//            int i = 0;
//            for (Element element : breadcrumbs) {
//                strBreadcrumbs.append(element.text());
//                if (i < breadcrumbs.size() - 1) {
//                    strBreadcrumbs.append(" > ");
//                }
//                i++;
//            }
//            return strBreadcrumbs.toString();
//        } else {
//            return null;
//        }
//    }
//
//    private String getFilePath(File inFile) {
//        return inFile.getAbsolutePath();
//    }
//
//    private void saveData(String[] inData) {
//        BufferedWriter writer = this.getWriter();
//        this.writeDataToFile(inData, writer);
//    }
//
//    private BufferedWriter getWriter() {
//        BufferedWriter writer = null;
//        try {
//            writer = new BufferedWriter(new FileWriter(this.csv, true));
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }
//        return writer;
//    }
//
//    private void writeDataToFile(String[] inData, BufferedWriter inWriter) {
//        if(inWriter != null) {
//            try{
//                for (int i = 0; i < 7; i++) {
//                    inWriter.write(this.encloseInQuotes(inData[i]), 0, this.encloseInQuotes(inData[i]).length());
//                    if (i < 6) {
//                        inWriter.write(";", 0, 1);
//                    }
//                }
//                inWriter.newLine();
//            } catch (IOException ex) {
//                ex.printStackTrace();
//            } finally {
//                try {
//                    inWriter.close();
//                } catch(IOException ex){
//                    ex.printStackTrace();
//                }
//            }
//        }
//    }
//
//    private String encloseInQuotes(String inString) {
//        StringBuilder strBuilder = new StringBuilder();
//        if(inString == null || inString.equals("")) {
//            return "";
//        }
//        strBuilder.append("\"");
//        for(int i = 0; i < inString.length(); i++) {
//            char charInStr = inString.charAt(i);
//            if(charInStr  == '\"') {
//                strBuilder.append("\"");
//            }
//            strBuilder.append(charInStr);
//        }
//        strBuilder.append("\"");
//        return strBuilder.toString();
//    }
//
//    private void  moveFile(String inBreadcrumbs, File inFile) {
//
//        String locationToMove = this.getLocationToMove(inBreadcrumbs);
//        this.moveFileToNewLocation(locationToMove, inFile);
//
//    }
//    private String getLocationToMove(String inBreadcrumbs) {
//        StringBuilder pathname = new StringBuilder();
//        if(inBreadcrumbs == null) {
//            return pathname.append(this.pathToFilesToParse.getAbsolutePath()).append(File.separator).append("_unsorted").toString();
//        } else {
//            String[] dirs = inBreadcrumbs.split(" > ");
//            pathname.append(this.pathToFilesToParse.getAbsolutePath()).append(File.separator);
//            for (String dir : dirs) {
//                pathname.append(dir).append(File.separator);
//            }
//            return pathname.toString();
//        }
//    }
//    public void moveFileToNewLocation(String inPath, File inFile){
//        try{
//            File foldersToCreate = new File(inPath);
//            foldersToCreate.mkdirs();
//            Files.move(Paths.get(inFile.getAbsolutePath()), Paths.get(inPath + inFile.getName()), REPLACE_EXISTING);
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }
//    }


}
