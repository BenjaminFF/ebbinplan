package com.example.ebbinplan.tables;

import androidx.annotation.NonNull;

import org.litepal.crud.LitePalSupport;

public class Plan extends LitePalSupport {
    private String name;
    private int timeStamp;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
