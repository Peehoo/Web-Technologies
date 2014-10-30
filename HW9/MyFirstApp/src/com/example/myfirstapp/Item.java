package com.example.myfirstapp;

public class Item {

    private String title;
    private String link;

    public Item(String name, String link) {
        this.title = name;
        this.link = link;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getName() {
        return title;
    }

    public void setName(String name) {
        this.title = name;
    }

    @Override
    public String toString() {
        return this.title;
    }
}
