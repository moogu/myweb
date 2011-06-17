package com.moogu.myweb.shared.reserve;

import java.io.Serializable;
import java.util.Date;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ReserveEurPeriod implements Serializable, IsSerializable {

    private static final long serialVersionUID = -1575210925694680937L;

    private Integer id;

    private Date startDate;

    private Date endDate;

    private Double average;

    public ReserveEurPeriod() {
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getStartDate() {
        return this.startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return this.endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Double getAverage() {
        return this.average;
    }

    public void setAverage(Double average) {
        this.average = average;
    }
}