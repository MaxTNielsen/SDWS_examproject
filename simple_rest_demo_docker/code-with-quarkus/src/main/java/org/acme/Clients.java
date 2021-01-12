package org.acme;

public class Clients {
    private String id;
    private String name;
    private String CPR;

    public Clients(){

    }

    public Clients(String id, String name, String CPR) {
        this.id = id;
        this.name = name;
        this.CPR = CPR;
    }

    public Clients(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
