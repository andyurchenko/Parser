package com.company;

import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;
import java.io.*;
import java.util.*;
import java.nio.file.*;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class Parser {

    private File pathToDataToParse;//Каталог, где лежат фалы для парсинг

    public static void main(String[] args) {
        Parser parser = new Parser(args[0]);
        parser.parseFiles(args[0]);
    }

    public Parser(String inPathToDataToParse) {
        this.pathToDataToParse = new File(inPathToDataToParse);
    }

    public void parseFiles(String inPath) {
        try {
            File[] listOfFilesToParse = this.getListFiles(inPath);
            if(listOfFilesToParse != null && listOfFilesToParse.length != 0) {
                for(File file : listOfFilesToParse) {
                    ParsedData parsedData = this.getAllDataFromHTML(file);
                    this.writeToCSV(parsedData.getParsedData());

                    File foldersToCreate = new File(this.getPathFromBreadcrumbs(parsedData.getBreadcrumbs()));
                    foldersToCreate.mkdirs();
                    Files.move(Paths.get(file.getAbsolutePath()), Paths.get(this.getPathFromBreadcrumbs(parsedData.getBreadcrumbs()) + file.getName()), REPLACE_EXISTING);
                }
            }
        } catch(IOException ex) {
            ex.printStackTrace();
        }
    }

    private ParsedData getAllDataFromHTML(File inFile) {
        ParsedData parsedData = new ParsedData();
        try {
            Document htmlDocument = Jsoup.parse(inFile, null);//html страница, полученная из JSoup после парсинга файла
            parsedData.setUrl(this.getURL(htmlDocument));
            //Title
            parsedData.setTitle(this.getTitle(htmlDocument));
            //Description
            parsedData.setDescription(this.getDescription(htmlDocument));
            //H1
            parsedData.setH1(this.getH1(htmlDocument));
            //Bestreview
            parsedData.setBestreview(this.getBestReview(htmlDocument));
            //Breadcrumbs
            parsedData.setBreadcrumbs(this.getBreadcrumbsForCSV(htmlDocument));
            //Page path
            parsedData.setPagepath(this.getFilePath(inFile));
        } catch(IOException ex) {
            ex.printStackTrace();
        }
        return parsedData;
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

    private File[] getListFiles(String pathToDirToParse) {
        File dir = new File(pathToDirToParse);
        if (dir.isDirectory()) {
            FilenameFilter fileFilter = new FilenameFilter() {
                public boolean accept(File inDir, String inFileName) {
                    String lowercaseName = inFileName.toLowerCase();
                    return (lowercaseName.endsWith(".htm")
                            || lowercaseName.endsWith(".html")
                            || lowercaseName.endsWith(".php")
                            || lowercaseName.endsWith(".txt"));
                }
            };
            return dir.listFiles(fileFilter);
        } else {
            return null;
        }
    }

    private String getURL(Document inHTMLDocument) {
        if (inHTMLDocument.selectFirst("meta[property=og:url]") != null) {
            return inHTMLDocument.selectFirst("meta[property=og:url]").attr("content");
        } else {
            return null;
        }
    }

    private String getTitle(Document inHTMLDocument) {
        if (inHTMLDocument.selectFirst("div.o-pdp-topic__title h1[class=e-h1 sel-product-title]") != null) {
            return inHTMLDocument.selectFirst("div.o-pdp-topic__title h1[class=e-h1 sel-product-title]").text();
        } else {
            return null;
        }
    }

    private String getDescription(Document inHTMLDocument) {
        if (inHTMLDocument.selectFirst("div.o-about-product__block div.collapse-text-initial") != null) {
            return inHTMLDocument.selectFirst("div.o-about-product__block div.collapse-text-initial").text();
        } else {
            return null;
        }
    }

    private String getBestReview(Document inHTMLDocument) {
        if (inHTMLDocument.selectFirst("div.review-ext-item-description-item > p > span") != null) {
            return inHTMLDocument.select("div.review-ext-item-description-item > p > span").text();
        } else {
            return null;
        }
    }

    private String getH1(Document inHTMLDocument) {
        if (inHTMLDocument.selectFirst("div.o-pdp-topic__title h1[class=e-h1 sel-product-title]") != null) {
            return inHTMLDocument.select("div.o-pdp-topic__title h1[class=e-h1 sel-product-title]").text();
        } else {
            return null;
        }
    }

    private String getBreadcrumbsForCSV(Document inHTMLDocument) {
        if (inHTMLDocument.select("ul.c-breadcrumbs__list > li.c-breadcrumbs__item > a") != null) {
            StringBuilder strBreadcrumbs = new StringBuilder(); //строка вида Главная > Ноутбуки > HP
            Elements breadcrumbs = inHTMLDocument.select("ul.c-breadcrumbs__list > li.c-breadcrumbs__item > a");
            int i = 0;
            for (Element element : breadcrumbs) {
                strBreadcrumbs.append(element.text());
                if (i < breadcrumbs.size() - 1) {
                    strBreadcrumbs.append(" > ");
                }
                i++;
            }
            return strBreadcrumbs.toString();
        } else {
            return null;
        }
    }


    public String getPathFromBreadcrumbs(String inBreadcrumbs) {
        StringBuilder pathname = new StringBuilder();
        if(inBreadcrumbs == null) {
            return pathname.append(this.getPathToDataToParse()).append(File.separator).append("_unsorted").toString();
        } else {
            String[] dirs = inBreadcrumbs.split(" > ");
//            if (dirs.length == 0) {
//                return pathname.append(this.getPathToDataToParse()).append(File.separator).append("_unsorted").toString();
//            } else {
                pathname.append(this.getPathToDataToParse()).append(File.separator);
                for (String dir : dirs) {
                    pathname.append(dir).append(File.separator);
                }
                return pathname.toString();
//            }
        }
    }

    private String getFilePath(File inFile) {
        return inFile.getAbsolutePath();
    }

    private File getPathToDataToParse() {
        return this.pathToDataToParse;
    }

    private void writeToCSV(String[] inParsedData) {
        try {
            File csv = new File(this.getPathToDataToParse() + File.separator + "db.csv");
            csv.createNewFile();
            FileWriter fileWriter = new FileWriter(csv, true);
            BufferedWriter writer = new BufferedWriter(fileWriter);
            this.writeParsedDataToCSV(writer, inParsedData);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    private void writeParsedDataToCSV(BufferedWriter inWriter, String[] inParsedData) {
        try {
            for (int i = 0; i < 7; i++) {
                String strToWrite = this.encloseInQuotes(inParsedData[i]);
                inWriter.write(strToWrite, 0, strToWrite.length());
                if (i < 6) {
                    inWriter.write(";", 0, 1);
                }
            }
            inWriter.newLine();
        } catch(IOException ex){
            ex.printStackTrace();
        } finally {
            try {
                inWriter.close();
            } catch(IOException ex){
                ex.printStackTrace();
            }
        }

    }

    public class ParsedData {
        static final int URL = 0;
        static final int TITLE = 1;
        static final int DESCRIPTION = 2;
        static final int H1 = 3;
        static final int BESTREVIEW = 4;
        static final int BREADCRUMBS = 5;
        static final int PAGEPATH = 6;

        private ArrayList<String> breadcrumbs = new ArrayList<String>();

        String[] parsedData = new String[7];

        public void setUrl(String inUrl) {
            this.parsedData[URL] = inUrl;
        }

        public void setTitle(String inTitle) {
            this.parsedData[TITLE] = inTitle;
        }

        public void setDescription(String inDescription) {
            this.parsedData[DESCRIPTION] = inDescription;
        }

        public void setH1(String inH1) {
            this.parsedData[H1] = inH1;
        }


        public void setBestreview(String inBestreview) {
            this.parsedData[BESTREVIEW] = inBestreview;
        }


        public void setBreadcrumbs(String inBreadcrumbs) {
            this.parsedData[BREADCRUMBS] = inBreadcrumbs;
        }

        public String getBreadcrumbs() {
            return this.parsedData[BREADCRUMBS];
        }


        public void setPagepath(String inPagepath) {
            this.parsedData[PAGEPATH] = inPagepath;
        }

        public String[] getParsedData() {
            return parsedData;
        }

    }
}
