package com.company;

import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;
import java.io.*;
import java.util.*;


public class Parser {

    public static void main(String[] args) {

        try{
            try {
                File pathToExportedFiles = new File(args[0]);

                if(pathToExportedFiles.isDirectory()) {
                    File[] filesToParse = null;//список файлов в выбранной директории
                    Document htmlDocument = null; //html страница, полученная из JSoup после парсинга файла
                    String[] parsedData = new String[7]; //конкретные данные, которые мы ищем на странице


                    FilenameFilter fileFilter = new FilenameFilter() {
                        public boolean accept(File inDir, String inFileName) {
                            String lowercaseName = inFileName.toLowerCase();
                            return (lowercaseName.endsWith(".htm")
                                    || lowercaseName.endsWith(".html")
                                    || lowercaseName.endsWith(".php")
                                    || lowercaseName.endsWith(".txt"));
                        }
                    };
                    filesToParse = pathToExportedFiles.listFiles(fileFilter);
                    if(filesToParse != null && filesToParse.length != 0) {
                        for(File file : filesToParse) {
                            htmlDocument = Jsoup.parse(file, null);

                            // URL
                            if (htmlDocument.selectFirst("meta[property=og:url]") != null) {
                                parsedData[0] = htmlDocument.selectFirst("meta[property=og:url]").attr("content");
                            } else {
                                parsedData[0] = "";
                            }
                            //Title
                            if (htmlDocument.selectFirst("div.o-pdp-topic__title h1[class=e-h1 sel-product-title]") != null) {
                                parsedData[1] = htmlDocument.selectFirst("div.o-pdp-topic__title h1[class=e-h1 sel-product-title]").text();
                            } else {
                                parsedData[1] = "";
                            }
                            //Description
                            if (htmlDocument.selectFirst("div.o-about-product__block div.collapse-text-initial") != null) {
                                parsedData[2] = htmlDocument.selectFirst("div.o-about-product__block div.collapse-text-initial").text();
                            } else {
                                parsedData[2] = "";
                            }
                            parsedData[3] = parsedData[1]; //H1
                            //Best review*/
                            if (htmlDocument.selectFirst("div.review-ext-item-description-item > p > span") != null) {
                                parsedData[4] = htmlDocument.select("div.review-ext-item-description-item > p > span").text();
                            } else {
                                parsedData[4] = "";
                            }
                            //Breadcrumbs
                            StringBuilder strBreadcrumbs;//строка вида Главная > Ноутбуки > HP
                            ArrayList<String> dirs = new ArrayList<String>();//Для хранения каждого элемента меню из breadcrumbs отдельно.
                            if (htmlDocument.select("ul.c-breadcrumbs__list > li.c-breadcrumbs__item > a") != null) {
                                Elements breadcrumbs = htmlDocument.select("ul.c-breadcrumbs__list > li.c-breadcrumbs__item > a");
                                strBreadcrumbs = new StringBuilder();
                                int i = 0;
                                for (Element element : breadcrumbs) {
                                    strBreadcrumbs.append(element.text());
                                    dirs.add(element.text());
                                    if (i < breadcrumbs.size() - 1) {
                                        strBreadcrumbs.append(" > ");
                                    }
                                    i++;
                                }
                            } else {
                                strBreadcrumbs = new StringBuilder();
                            }
                            parsedData[5] = strBreadcrumbs.toString(); //breadcrumbs
                            parsedData[6] = file.getAbsolutePath();//Page path
                            StringBuilder pathname = new StringBuilder();
                            if (dirs.size() == 0) {
                                pathname.append(pathToExportedFiles).append(File.separator).append("_unsorted");
                            } else {
                                pathname.append(pathToExportedFiles).append(File.separator);
                                for (String dir : dirs) {
                                    pathname.append(dir).append(File.separator);
                                }
                            }
                            File foldersToCreate = new File(pathname.toString());
                            foldersToCreate.mkdirs();
                            File csv = new File(foldersToCreate + File.separator + "db.csv");
                            csv.createNewFile();
                            FileWriter fileWriter = new FileWriter(csv, true);
                            BufferedWriter writer = new BufferedWriter(fileWriter);
                            for (int i = 0; i < 7; i++) {
                                String strToWrite = Parser.encloseInQuotes(parsedData[i]);
                                writer.write(strToWrite, 0, strToWrite.length());
                                if (i < 6) {
                                    writer.write(";", 0, 1);
                                }
                            }
                            writer.newLine();
                            writer.close();
                        }
                    }

                }
            } catch(IOException ex) {
                ex.printStackTrace();
            }
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    public static String encloseInQuotes(String inString) {
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
