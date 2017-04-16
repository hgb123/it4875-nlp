package com.nlp.nlptest;

/**
 * Created by NgocDon on 4/16/2017.
 */

public class Truyen {
    private int id;
    private String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Truyen(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
