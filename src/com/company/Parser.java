package com.company;

import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;
import java.io.*;
import java.util.*;
import java.nio.file.*;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class Parser {

    private File pathToDataToParse;//Каталог, где лежат фалы для парсинга
    //private String pathnameFromBreadcrumbs;//Каталоги, которые мы получаем из breadcrumbs
    private ArrayList<String> breadcrumbs = new ArrayList<String>(); //Разобранный breadcrumbs

    public void parseFiles(String inPath) {
        try {
            this.setPathToDataToParse(new File(inPath));

            File[] listOfFilesToParse = this.getListFiles(inPath);

            String[] parsedData = new String[7]; //конкретные данные, которые мы ищем на странице

            if(listOfFilesToParse != null && listOfFilesToParse.length != 0) {
                for(File file : listOfFilesToParse) {

                    Document htmlDocument = Jsoup.parse(file, null);//html страница, полученная из JSoup после парсинга файла
                    // URL
                    parsedData[0] = this.getURL(htmlDocument);
                    //Title
                    parsedData[1] = this.getTitle(htmlDocument);
                    //Description
                    parsedData[2] = this.getDescription(htmlDocument);
                    //H1
                    parsedData[3] = parsedData[1];
                    //Best review
                    parsedData[4] = this.getBestReview(htmlDocument);
                    //Breadcrumbs
                    parsedData[5] = this.getBreadcrumbsForCSV(htmlDocument);
                    //Page path
                    parsedData[6] = this.getFilePath(file);

                    this.writeParsedDataToCSV(parsedData);

                    File foldersToCreate = new File(this.getPathFromBreadcrumbs());
                    foldersToCreate.mkdirs();
                    Files.move(Paths.get(file.getAbsolutePath()), Paths.get(this.getPathFromBreadcrumbs() + file.getName()), REPLACE_EXISTING);

                }
            }
        } catch(IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Parser parser = new Parser();
        parser.parseFiles(args[0]);
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

    private void setBreadcrumbs(Document inHTMLDocument) {
        this.breadcrumbs.clear();
        if (inHTMLDocument.select("ul.c-breadcrumbs__list > li.c-breadcrumbs__item > a") != null) {
            Elements breadcrumbs = inHTMLDocument.select("ul.c-breadcrumbs__list > li.c-breadcrumbs__item > a");
            for (Element element : breadcrumbs) {
                this.breadcrumbs.add(element.text());
            }
        }
    }

    private ArrayList<String> getBreadcrumbs() {
        return this.breadcrumbs;
    }

    private String getBreadcrumbsForCSV(Document inHtmlDocument) {
        this.setBreadcrumbs(inHtmlDocument);
        StringBuilder strBreadcrumbs = new StringBuilder(); //строка вида Главная > Ноутбуки > HP
        int i = 0;
        for (String element : this.getBreadcrumbs()) {
                strBreadcrumbs.append(element);
                if (i < breadcrumbs.size() - 1) {
                    strBreadcrumbs.append(" > ");
                }
                i++;
            }
            return strBreadcrumbs.toString();
    }

    public String getPathFromBreadcrumbs() {
        StringBuilder pathname = new StringBuilder();
        ArrayList<String> dirs = this.getBreadcrumbs();
        if (dirs == null || dirs.size() == 0) {
            pathname.append(this.getPathToDataToParse()).append(File.separator).append("_unsorted");
        } else {
            pathname.append(this.getPathToDataToParse()).append(File.separator);
            for (String dir : dirs) {
                pathname.append(dir).append(File.separator);
            }
        }
        return pathname.toString();
    }

    private String getFilePath(File inFile) {
        return inFile.getAbsolutePath();
    }

    private File getPathToDataToParse() {
        return this.pathToDataToParse;
    }

    private void setPathToDataToParse(File inPathToDatatoParse) {
        this.pathToDataToParse = inPathToDatatoParse;
    }

    private void writeParsedDataToCSV(String[] inParsedData) {
        try{
            File csv = new File(this.getPathToDataToParse() + File.separator + "db.csv");
            csv.createNewFile();
            FileWriter fileWriter = new FileWriter(csv, true);
            BufferedWriter writer = new BufferedWriter(fileWriter);
            for (int i = 0; i < 7; i++) {
                String strToWrite = this.encloseInQuotes(inParsedData[i]);
                writer.write(strToWrite, 0, strToWrite.length());
                if (i < 6) {
                    writer.write(";", 0, 1);
                }
            }
            writer.newLine();
            writer.close();
        } catch(IOException ex){
            ex.printStackTrace();
        }
    }
}
