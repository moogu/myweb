package com.moogu.myweb.server.feature.reserve;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Component;

import com.moogu.myweb.server.feature.reserve.internal.DbRowReserveClosedDates;
import com.moogu.myweb.server.feature.reserve.internal.DbRowReservePeriod;
import com.moogu.myweb.server.feature.reserve.internal.DbRowReservePublish;
import com.moogu.myweb.server.utils.DateHelper;
import com.moogu.myweb.shared.reserve.ReserveEurPeriod;

/**
 * All methods for the RESERVE EUR Module. That includes Overview and Management.
 * 
 * @author i21726 - Patrick Santana
 * 
 */
@Component
public class ReserveRepository {

    @Autowired
    private HibernateTemplate hibernateTemplate;

    public void addClosedDate(Date closedDate) {
        if (closedDate != null) {
            final DbRowReserveClosedDates entity = new DbRowReserveClosedDates();
            entity.setClosedDate(closedDate);
            this.hibernateTemplate.save(entity);
        }
    }

    public void removeClosedDate(Date closedDate) {
        if (closedDate != null) {
            final DbRowReserveClosedDates entity = (DbRowReserveClosedDates) this.hibernateTemplate.load(
                            DbRowReserveClosedDates.class.getName(),
                            closedDate);
            if (entity != null) {
                this.hibernateTemplate.delete(entity);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public List<Date> getDatesByYear(int year) {
        if (year > 2000 && year < 3000) {
            //use FBI(Function Based Index) for the query
            return this.hibernateTemplate.find(
                            "SELECT closedDate FROM DbRowReserveClosedDates WHERE to_char(closedDate,'yyyy')='" + year
                                            + "'");
        }

        return null;
    }

    public Integer createNewPeriod(Date startDate, Date endDate, Double average) {
        final DbRowReservePeriod entity = new DbRowReservePeriod();
        entity.setStartDate(startDate);
        entity.setEndDate(endDate);

        // average can be null
        if (average != null) {
            entity.setAverage(new BigDecimal(average));
        }

        // return the id
        return (Integer) this.hibernateTemplate.save(entity);
    }

    public List<DbRowReservePeriod> getPeriods() {
        return this.hibernateTemplate.loadAll(DbRowReservePeriod.class);
    }

    public ReserveEurPeriod getPeriod(Integer id) {
        final DbRowReservePeriod period = this.hibernateTemplate.load(DbRowReservePeriod.class, id);
        return period.toReservePeriod();
    }

    public void updatePeriod(Integer id, Date startDate, Date endDate, BigDecimal average) {
        final DbRowReservePeriod period = this.hibernateTemplate.load(DbRowReservePeriod.class, id);
        period.setStartDate(startDate);
        period.setEndDate(endDate);
        period.setAverage(average);

        this.hibernateTemplate.update(period);
    }

    public void publish(Date publishDate, BigDecimal balance, BigDecimal deposit, BigDecimal lending) {
        final DbRowReservePublish publish = new DbRowReservePublish();
        publish.setPublish(publishDate);
        publish.setBalance(balance);
        publish.setDeposit(deposit);
        publish.setMarginalLending(lending);
        this.hibernateTemplate.save(publish);
    }

    @SuppressWarnings("unchecked")
    public List<DbRowReservePublish> getPublishes(Date startDate, Date endDate) {
        return this.hibernateTemplate.find(
                        "FROM DbRowReservePublish WHERE publish >= ?  AND publish <= ?",
                        startDate,
                        endDate);
    }

    @SuppressWarnings("unchecked")
    public List<DbRowReserveClosedDates> getDatesByYear(ReserveEurPeriod reservePeriod) {
        return this.hibernateTemplate.find(
                        "FROM DbRowReserveClosedDates WHERE closedDate >= ?  AND closedDate <= ?",
                        reservePeriod.getStartDate(),
                        reservePeriod.getEndDate());
    }

    public DbRowReservePublish findPublish(Date date) {
        return this.hibernateTemplate.load(DbRowReservePublish.class, date);
    }

    public void rePublish(Date date, Double balance, Double deposit, Double lending) {
        final DbRowReservePublish publish = this.findPublish(date);
        publish.setBalance(new BigDecimal(balance));

        // detail for deposit
        if (deposit != null) {
            publish.setDeposit(new BigDecimal(deposit));
        } else {
            publish.setDeposit(null);
        }

        // detail for lending
        if (lending != null) {
            publish.setMarginalLending(new BigDecimal(lending));
        } else {
            publish.setMarginalLending(null);
        }

        // update
        this.hibernateTemplate.update(publish);
    }

    public DbRowReservePeriod getActualPeriod() {
        final List<DbRowReservePeriod> periods = this.getPeriodsByDate(new Date());
        if (periods != null && periods.size() > 1) {
            // multiples periods, throw an exception
            throw new RuntimeException(
                            "Multiples periods with the actual date. Please, fix the conflict in the Reserve EUR Management");
        } else if (periods != null && periods.size() == 1) {
            return periods.get(0);
        }

        return null;
    }

    public boolean isDateAlreadyInAnotherPeriod(Date date) {
        final List<DbRowReservePeriod> periods = this.getPeriodsByDate(date);
        if (periods != null && periods.size() > 0) {
            // date inside another period
            return true;
        }
        return false;
    }

    public boolean isDateAlreadyInAnotherPeriod(Date date, Integer id) {
        final List<DbRowReservePeriod> periods = this.getPeriodsByDate(date);
        for (final DbRowReservePeriod period : periods) {
            if (!period.getId().equals(id)) {
                // date inside another period
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    private List<DbRowReservePeriod> getPeriodsByDate(Date date) {
        final Date dateMidnight = DateHelper.createMidnightDate(date);
        return this.hibernateTemplate.find(
                        "FROM DbRowReservePeriod WHERE ? >= startDate  AND ? <= endDate",
                        dateMidnight,
                        dateMidnight);
    }
}