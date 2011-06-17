package com.moogu.myweb.shared.reserve;

import java.io.Serializable;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ReserveEurGraph implements Serializable, IsSerializable {

    private static final long serialVersionUID = 1399134048701512884L;

    private String title;

    private List<Double> values;

    private Double yMaxBalance;

    private Double average;

    private List<String> xAxis;

    private boolean empty;

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return this.title;
    }

    public void setValues(List<Double> values) {
        this.values = values;
    }

    public List<Double> getValues() {
        return this.values;
    }

    public void setYMaxBalance(Double yMaxBalance) {
        this.yMaxBalance = yMaxBalance;
    }

    public Double getYMaxBalance() {
        return this.yMaxBalance;
    }

    public void setMandatoryAverage(Double average) {
        this.average = average;
    }

    public Double getMandatoryAverage() {
        return this.average;
    }

    public void setXAxis(List<String> xAxis) {
        this.xAxis = xAxis;
    }

    public List<String> getXAxis() {
        return this.xAxis;
    }

    public void setEmpty(boolean empty) {
        this.empty = empty;
    }

    public boolean isEmpty() {
        return this.empty;
    }
}