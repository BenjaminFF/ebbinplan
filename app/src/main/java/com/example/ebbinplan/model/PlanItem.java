package com.example.ebbinplan.model;

import org.litepal.crud.LitePalSupport;

public class PlanItem extends LitePalSupport {
    private int id;
    private String name;
    private int timestamp;
    private int planId;          //planId是对同一个计划的标识

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

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public int getPlanId() {
        return planId;
    }

    public void setPlanId(int planId) {
        this.planId = planId;
    }
}
