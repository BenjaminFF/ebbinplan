package com.example.ebbinplan.model;

import androidx.annotation.NonNull;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

public class PlanItem extends LitePalSupport {
    private int id;
    private String name;
    private long timestamp;
    private String planId;          //planId是对同一个计划的标识

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

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    @NonNull
    @Override
    public String toString() {
        return "id:" + id + "/n" +
                "name:" + name + "/n" +
                "timestamp:" + timestamp + "/n" +
                "planId:" + planId;
    }
}
