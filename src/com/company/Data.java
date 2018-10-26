package com.company;

public class Data {
    private String url = null;
    private String title = null;;
    private String description = null;
    private String h1 = null;
    private String bestReview = null;
    private String breadcrumbs = null;
    private String filePath = null;
    private String pathToMoveFileTo = null;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getH1() {
        return h1;
    }

    public void setH1(String h1) {
        this.h1 = h1;
    }

    public String getBestreview() {
        return bestReview;
    }

    public void setBestreview(String bestreview) {
        this.bestReview = bestreview;
    }

    public String getBreadcrumbs() {
        return breadcrumbs;
    }

    public void setBreadcrumbs(String breadcrumbs) {
        this.breadcrumbs = breadcrumbs;
    }

    public String getFilepath() {
        return filePath;
    }

    public void setFilepath(String pagepath) {
        this.filePath = pagepath;
    }

    public String getPathToMoveFileTo() {
        return pathToMoveFileTo;
    }

    public void setPathToMoveFileTo(String pathToMoveFileTo) {
        this.pathToMoveFileTo = pathToMoveFileTo;
    }
}
