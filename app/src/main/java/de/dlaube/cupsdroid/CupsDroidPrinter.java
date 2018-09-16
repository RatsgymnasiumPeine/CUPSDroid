package de.dlaube.cupsdroid;

class CupsDroidPrinter {
    private int id;
    private String name;
    private String url;

    public CupsDroidPrinter(int id, String name, String url){
        this.id = id;
        this.name = name;
        this.url = url;
    }

    public String getName(){
        return name;
    }

    public String getUrl(){
        return url;
    }

    public int getId(){
        return id;
    }
}
