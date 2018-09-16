package de.dlaube.cupsdroid;

class CupsDroidPrinter {
    private String name;
    private String url;

    public CupsDroidPrinter(String name, String url){
        this.name = name;
        this.url = url;
    }

    public String getName(){
        return name;
    }

    public String getUrl(){
        return url;
    }
}
