package com.harun.rma_1_ime_prezime.models;


public final class CalcModel {
    private String createdDate;
    private double result ;
    private String resStr ;

    public CalcModel(String createdDate, double result, String resStr) {
        this.createdDate = createdDate;
        this.result = result;
        this.resStr = resStr;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public double getResult() {
        return result;
    }
    public void setResult(double result) {
        this.result = result;
    }

    public String getResStr() {
        return resStr;
    }

    public void setResStr(String resStr) {
        this.resStr = resStr;
    }
}
