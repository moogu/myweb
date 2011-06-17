package com.moogu.myweb.server.feature.reserve.internal;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.moogu.myweb.shared.reserve.ReserveEurEoDBalance;

/**
 * This pojo is the publish of each BALANCE in the database
 * 
 * @author i21726 - Patrick Santana
 * 
 */
@Entity
@Table(name = "TB_RESERVE_PUBLISH")
public class DbRowReservePublish {

    @Id
    @Column(name = "PUBLISH_DATE")
    private Date publish;

    @Column(name = "BALANCE")
    private BigDecimal balance;

    @Column(name = "DEPOSIT")
    private BigDecimal deposit;

    @Column(name = "LENDING")
    private BigDecimal marginalLending;

    public Date getPublish() {
        return this.publish;
    }

    public void setPublish(Date publish) {
        this.publish = publish;
    }

    public BigDecimal getBalance() {
        return this.balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public BigDecimal getDeposit() {
        return this.deposit;
    }

    public void setDeposit(BigDecimal deposit) {
        this.deposit = deposit;
    }

    public BigDecimal getMarginalLending() {
        return this.marginalLending;
    }

    public void setMarginalLending(BigDecimal marginalLending) {
        this.marginalLending = marginalLending;
    }

    public ReserveEurEoDBalance toEoDBalance() {
        final Double finalDeposit = this.deposit != null ? new Double(this.deposit.doubleValue())
                        : null;
        final Double finalLending = this.marginalLending != null ? new Double(this.marginalLending.doubleValue())
                        : null;

        return new ReserveEurEoDBalance(this.publish, Double.valueOf(this.balance.doubleValue()), null,
                        finalDeposit, finalLending, true, false);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}