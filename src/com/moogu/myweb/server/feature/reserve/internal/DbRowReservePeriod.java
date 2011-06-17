package com.moogu.myweb.server.feature.reserve.internal;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.moogu.myweb.shared.reserve.ReserveEurPeriod;

@Entity
@Table(name = "TB_RESERVE_PERIOD")
@SequenceGenerator(name = "RESERVE_PERIOD_SEQ", sequenceName = "SQ_RESERVE_PERIOD")
public class DbRowReservePeriod implements Comparable<DbRowReservePeriod> {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "RESERVE_PERIOD_SEQ")
    private Integer id;

    @Column(name = "START_DATE")
    private Date startDate;

    @Column(name = "END_DATE")
    private Date endDate;

    @Column(name = "AVERAGE")
    private BigDecimal average;

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

    public BigDecimal getAverage() {
        return this.average;
    }

    public void setAverage(BigDecimal average) {
        this.average = average;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    public int compareTo(DbRowReservePeriod o) {
        if (this.getStartDate().getTime() > o.getStartDate().getTime()) {
            return 1;
        } else if (this.getStartDate().getTime() < o.getStartDate().getTime()) {
            return -1;
        } else {
            return 0;
        }
    }

    public ReserveEurPeriod toReservePeriod() {
        final ReserveEurPeriod period = new ReserveEurPeriod();
        period.setId(this.id);
        period.setStartDate(this.startDate);
        period.setEndDate(this.endDate);

        if (this.average != null) {
            period.setAverage(this.average.doubleValue());
        }

        return period;
    }
}