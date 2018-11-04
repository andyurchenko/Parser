package com.company;

import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;
import java.io.*;

public class DataExtractor {

    public Data parseFile(File inFile) {
        Data dataParsedFromFile = new Data();
        try {
            Document htmlDocument = Jsoup.parse(inFile, null);//html страница, полученная из JSoup после парсинга файла
            //URL
            dataParsedFromFile.setUrl(this.getURL(htmlDocument));
            //Title
            dataParsedFromFile.setTitle(this.getTitle(htmlDocument));
            //Description
            dataParsedFromFile.setDescription(this.getDescription(htmlDocument));
            //H1
            dataParsedFromFile.setH1(this.getH1(htmlDocument));
            //Bestreview
            dataParsedFromFile.setBestreview(this.getBestReview(htmlDocument));
            //Breadcrumbs
            dataParsedFromFile.setBreadcrumbs(this.getBreadcrumbsForCSV(htmlDocument));
            //Page path
            dataParsedFromFile.setFilepath(this.getFilePath(inFile));

            dataParsedFromFile.setPathToMoveFileTo(
                    this.getPathToMoveFileTo(dataParsedFromFile.getBreadcrumbs(), inFile));

            return dataParsedFromFile;

        } catch(IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private String getURL(Document inHTMLDocument) {
        if (inHTMLDocument.selectFirst("meta[property=og:url]") != null) {
            return inHTMLDocument.select("meta[property=og:url]").first().attr("content");
        } else {
            return null;
        }
    }

    private String getTitle(Document inHTMLDocument) {
        return this.execCssQuery("div.o-pdp-topic__title h1[class=e-h1 sel-product-title]", inHTMLDocument);
    }

    private String getDescription(Document inHTMLDocument) {
        return this.execCssQuery("div.o-about-product__block div.collapse-text-initial", inHTMLDocument);
    }

    private String getBestReview(Document inHTMLDocument) {
        return this.execCssQuery("div.review-ext-item-description-item > p > span", inHTMLDocument);
    }

    private String getH1(Document inHTMLDocument) {
        return this.execCssQuery("div.o-pdp-topic__title h1[class=e-h1 sel-product-title]", inHTMLDocument);
    }

    private String execCssQuery(String inCSSQuery, Document inHTMLDocument) {
        if (inHTMLDocument.selectFirst(inCSSQuery) != null) {
            return inHTMLDocument.select(inCSSQuery).text();
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

    private String getFilePath(File inFile) {
        return inFile.getAbsolutePath();
    }

    private String getPathToMoveFileTo(String inBreadcrumbs, File inFile) {
        StringBuilder pathname = new StringBuilder();
        try{
            if(inBreadcrumbs == null) {
                return pathname.append(new File(inFile.getCanonicalPath()).getParent()).append(File.separator).append("_unsorted").toString();
            } else {
                String[] dirs = inBreadcrumbs.split(" > ");
                pathname.append(new File(inFile.getCanonicalPath()).getParent()).append(File.separator);
                for (String dir : dirs) {
                    pathname.append(dir).append(File.separator);
                }
                return pathname.toString();
            }
        } catch(IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
