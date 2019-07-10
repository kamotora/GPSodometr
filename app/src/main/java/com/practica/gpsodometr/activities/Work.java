package com.practica.gpsodometr.activities;

public class Work {
    private String nameWork;
    private String dataStart;
    private String kilometrs;
    private String leftKilo;

    Work(String nameWork, String dataStart, String kilometrs, String leftKilo) {
        this.nameWork = nameWork;
        this.dataStart = dataStart;
        this.kilometrs = kilometrs;
        this.leftKilo = leftKilo;
    }

    public String getNameWork() {
        return nameWork;
    }

    public String getDataStart() {
        return dataStart;
    }

    public String getKilometrs() {
        return kilometrs;
    }

    public String getLeftKilo() {
        return leftKilo;
    }
}
