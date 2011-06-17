package com.moogu.myweb.shared.reserve;

import java.io.Serializable;
import java.util.Date;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ReserveEurEoDBalance implements Serializable, IsSerializable {

    private static final long serialVersionUID = 3228155971676253772L;

    private Date datePublish;

    private Double balance;

    private Double average;

    private Double deposit;

    private Double marginalLending;

    private boolean readOnly;

    private boolean modified;

    @SuppressWarnings("unused")
    private ReserveEurEoDBalance() {
    }

    public ReserveEurEoDBalance(
                    Date datePublish,
                    Double balance,
                    Double average,
                    Double deposit,
                    Double marginalLending,
                    boolean readOnly,
                    boolean modified) {
        super();
        this.datePublish = datePublish;
        this.balance = balance;
        this.average = average;
        this.deposit = deposit;
        this.marginalLending = marginalLending;
        this.readOnly = readOnly;
        this.modified = modified;
    }

    public Date getDatePublish() {
        return this.datePublish;
    }

    public void setDatePublish(Date datePublish) {
        this.datePublish = datePublish;
    }

    public Double getBalance() {
        return this.balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public Double getAverage() {
        return this.average;
    }

    public void setAverage(Double average) {
        this.average = average;
    }

    public Double getDeposit() {
        return this.deposit;
    }

    public void setDeposit(Double deposit) {
        this.deposit = deposit;
    }

    public Double getMarginalLending() {
        return this.marginalLending;
    }

    public void setMarginalLending(Double marginalLending) {
        this.marginalLending = marginalLending;
    }

    public boolean isReadOnly() {
        return this.readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    public boolean isModified() {
        return this.modified;
    }

    public void setModified(boolean modified) {
        this.modified = modified;
    }

    public void modified() {
        this.setModified(true);
    }
}