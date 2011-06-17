package com.moogu.myweb.server;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.moogu.myweb.client.ReserveModuleRemoteService;
import com.moogu.myweb.client.common.genericmodeldata.IdNameModelData;
import com.moogu.myweb.server.feature.reserve.ReserveRepository;
import com.moogu.myweb.server.feature.reserve.internal.DbRowReserveClosedDates;
import com.moogu.myweb.server.feature.reserve.internal.DbRowReservePeriod;
import com.moogu.myweb.server.feature.reserve.internal.DbRowReservePublish;
import com.moogu.myweb.server.feature.reserve.report.excel.ReserveEURExcelReport;
import com.moogu.myweb.server.utils.DateHelper;
import com.moogu.myweb.server.utils.IlmsExceptionUtils;
import com.moogu.myweb.server.utils.IlmsHttpRequestThreadLocal;
import com.moogu.myweb.shared.reserve.ReserveEurEoDBalance;
import com.moogu.myweb.shared.reserve.ReserveEurGraph;
import com.moogu.myweb.shared.reserve.ReserveEurPeriod;
import com.moogu.myweb.shared.reserve.ReserveEurPublish;
import com.moogu.myweb.shared.utils.IlmsSharedException;
import com.moogu.myweb.shared.utils.IlmsSharedExceptionCodes;

@Component
@Transactional(readOnly = true, timeout = 10)
public class ReserveModuleRemoteServiceImpl implements ReserveModuleRemoteService {

    private static final Logger LOGGER = Logger.getLogger(ReserveModuleRemoteServiceImpl.class);

    public static final String LAST_RESERVE_EUR_REPORT_DETAILS = "lastReserveEurReportDetails";

    @Autowired
    private ReserveRepository repository;

    @Transactional(readOnly = false)
    public void addClosedDate(Date closedDate) {
        repository.addClosedDate(closedDate);
    }

    private double calculateAverage(final ReserveEurPeriod reservePeriod,
                    final Date balanceDate,
                    final double totalDays,
                    final double average,
                    double sumBalance,
                    double iBalance,
                    double averageToRealize) {
        // for the first day, average to realize is null
        if (!balanceDate.equals(reservePeriod.getStartDate())) {
            // calculation
            averageToRealize = ((average * totalDays) - sumBalance) / (totalDays - iBalance);
        }
        return averageToRealize;
    }

    private double calculateTotalDays(ReserveEurPeriod reservePeriod) {
        final Calendar calendarStartDate = Calendar.getInstance();
        calendarStartDate.setTime(reservePeriod.getStartDate());

        final Calendar calendarEndDate = Calendar.getInstance();
        calendarEndDate.setTime(reservePeriod.getEndDate());

        return DateHelper.calculateDiff(calendarStartDate.getTime(), calendarEndDate.getTime());
    }

    private void checkStartDateEndDate(Date startDate, Date endDate) {
        if (DateHelper.calculateDiff(startDate, endDate) < 0) {
            // end date is before start date
            final IlmsSharedException sharedException = IlmsExceptionUtils.toSharedException(
                            new RuntimeException("End Date needs to be after Start Date"),
                            IlmsSharedExceptionCodes.END_DATE_BEFORE_START_DATE);
            throw sharedException;
        }

        // check if startDate is already inside another period.
        if (repository.isDateAlreadyInAnotherPeriod(startDate)) {
            final IlmsSharedException sharedException = IlmsExceptionUtils.toSharedException(
                            new RuntimeException("Start Date is already in another period."),
                            IlmsSharedExceptionCodes.START_DATE_ALREADY_IN_ANOTHER_PERIOD);
            throw sharedException;
        }

        // check if endDate is already inside another period.
        if (repository.isDateAlreadyInAnotherPeriod(endDate)) {
            final IlmsSharedException sharedException = IlmsExceptionUtils.toSharedException(
                            new RuntimeException("End Date is already in another period."),
                            IlmsSharedExceptionCodes.END_DATE_ALREADY_IN_ANOTHER_PERIOD);
            throw sharedException;
        }
    }

    @Transactional(readOnly = false)
    public Integer createNewPeriod(Date startDate, Date endDate, Double average) {
        // end date should never be before start date
        checkStartDateEndDate(startDate, endDate);
        try {
            return repository.createNewPeriod(startDate, endDate, average);
        } catch (final org.hibernate.exception.ConstraintViolationException e) {
            throw IlmsExceptionUtils.toSharedException(
                            new RuntimeException("Start Date is not available"),
                            IlmsSharedExceptionCodes.START_DATE_NOT_AVAILABLE);
        }
    }

    public void dummy() throws IlmsSharedException {
        ReserveModuleRemoteServiceImpl.LOGGER.debug("Dummy implementation");
    }

    public Integer getActualPeriod() {
        final DbRowReservePeriod actualPeriod = repository.getActualPeriod();
        if (actualPeriod != null) {
            return actualPeriod.getId();
        }

        // no period for the current date
        return null;
    }

    public List<Date> getDatesByYear(int year) {
        return repository.getDatesByYear(year);
    }

    public List<ReserveEurEoDBalance> getEoDBalances(Integer periodId) {
        try {

            final List<ReserveEurEoDBalance> result = new ArrayList<ReserveEurEoDBalance>();

            // get details of the period
            final ReserveEurPeriod reservePeriod = repository.getPeriod(periodId);

            final Calendar calendarStartDate = Calendar.getInstance();
            calendarStartDate.setTime(reservePeriod.getStartDate());

            final Calendar calendarEndDate = Calendar.getInstance();
            calendarEndDate.setTime(reservePeriod.getEndDate());

            // Get all publishes for this range already done
            // Create a list with date and publish. It will be faster to search for each date
            final List<DbRowReservePublish> publishes = repository.getPublishes(
                            calendarStartDate.getTime(),
                            calendarEndDate.getTime());
            final Map<Date, DbRowReservePublish> map = new HashMap<Date, DbRowReservePublish>();
            for (final DbRowReservePublish publish : publishes) {
                map.put(publish.getPublish(), publish);
            }

            final double totalDays = calculateTotalDays(reservePeriod);
            final double average = reservePeriod.getAverage();
            double sumBalance = 0;
            double iBalance = 0;

            // loop in the range of date to create the balances.
            // if it is not filled, it means that the user can add
            while (!calendarStartDate.after(calendarEndDate)) {
                double averageToRealize = 0;

                // calculate average
                averageToRealize = calculateAverage(
                                reservePeriod,
                                calendarStartDate.getTime(),
                                totalDays,
                                average,
                                sumBalance,
                                iBalance,
                                averageToRealize);
                // increase the balance for the next loop
                iBalance++;

                // user can publish data. At this moment, it must be summarised with the actual sumBalance.
                if (map.containsKey(calendarStartDate.getTime())) {
                    final DbRowReservePublish publish = map.get(calendarStartDate.getTime());
                    sumBalance = sumBalance + publish.getBalance().doubleValue();
                    // generate a balance and set the average
                    final ReserveEurEoDBalance balance = publish.toEoDBalance();
                    balance.setAverage(averageToRealize);
                    result.add(balance);
                } else {
                    result.add(new ReserveEurEoDBalance(
                                    calendarStartDate.getTime(),
                                    null,
                                    Double.valueOf(averageToRealize),
                                    null,
                                    null,
                                    false,
                                    false));
                }

                // increase the day
                calendarStartDate.add(Calendar.DAY_OF_MONTH, 1);
            }

            return result;
        } catch (final Exception e) {
            ReserveModuleRemoteServiceImpl.LOGGER.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        }
    }

    public ReserveEurGraph getGraphDetails(Integer periodId, List<ReserveEurEoDBalance> rawBalances) {
        // save the raw balances for future report. That is the best place because 
        // it can be for publish data or simulation data
        // We need to put it in session for Excel export
        final HttpServletRequest request = IlmsHttpRequestThreadLocal.get();
        final HttpSession session = request.getSession();
        final ReserveEURExcelReport report = new ReserveEURExcelReport();
        report.balances = rawBalances;

        final ReserveEurGraph graph = new ReserveEurGraph();
        // get details of the period
        final ReserveEurPeriod reservePeriod = repository.getPeriod(periodId);

        // generate the graph text
        final StringBuilder builder = new StringBuilder();
        builder.append("Realized Reserve between ");
        builder.append(DateHelper.formatToDate(reservePeriod.getStartDate())).append(" and ");
        builder.append(DateHelper.formatToDate(reservePeriod.getEndDate()));
        report.header = builder.toString();
        report.mandatoryAverage = reservePeriod.getAverage();

        builder.append(" (in millions €)");
        graph.setTitle(builder.toString());

        session.setAttribute(ReserveModuleRemoteServiceImpl.LAST_RESERVE_EUR_REPORT_DETAILS, report);

        // get the dates that is close for the year of this period
        final List<DbRowReserveClosedDates> rawClosedDates = repository.getDatesByYear(reservePeriod);
        final List<Date> closedDates = new ArrayList<Date>();
        for (final DbRowReserveClosedDates date : rawClosedDates) {
            closedDates.add(date.getClosedDate());
        }

        // check if there is something publish
        graph.setEmpty(true);

        final List<ReserveEurEoDBalance> balances = new ArrayList<ReserveEurEoDBalance>();
        // go to all balances and remove the ones that are in the closed date or Saturday or Sunday
        final Calendar calendar = Calendar.getInstance();
        for (final ReserveEurEoDBalance balance : rawBalances) {
            if (!closedDates.contains(balance.getDatePublish())) {
                // check if it is Saturday or Sunday
                calendar.setTime(balance.getDatePublish());
                final int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
                if (dayOfWeek != Calendar.SATURDAY && dayOfWeek != Calendar.SUNDAY) {
                    balances.add(balance);

                }
            }

            if (balance.isModified() || balance.isReadOnly()) {
                // check if there is at least one publish
                graph.setEmpty(false);
            }
        }

        // get all values to but in the graph 
        final List<Double> values = new ArrayList<Double>();
        // the X Axis will be the date
        final List<String> xAxis = new ArrayList<String>();
        for (final ReserveEurEoDBalance balance : balances) {
            if (balance.getBalance() == null) {
                values.add(new Double(0.0d));
            } else {
                // add the value in million
                values.add(balance.getBalance() / 1000000);
            }
            xAxis.add(DateHelper.formatToDate(balance.getDatePublish(), "dd/MMM"));
        }
        graph.setValues(values);
        graph.setXAxis(xAxis);

        //check the max
        double maxDouble = 0d;
        for (final Double d : values) {
            if (d.doubleValue() > maxDouble) {
                maxDouble = d.doubleValue();
            }
        }

        graph.setYMaxBalance(Math.ceil(maxDouble * 1.25));
        graph.setMandatoryAverage(reservePeriod.getAverage() / 1000000);
        return graph;
    }

    public Date getLastPublishTimestamp(Integer periodId) {
        final Date now = new Date();
        return now;
    }

    public Date getNextStartDate() {
        // get all periods definition to look the last date used
        final List<DbRowReservePeriod> allPeriods = repository.getPeriods();
        Date lastDate = new Date();

        if (allPeriods != null && allPeriods.size() > 0) {
            Collections.sort(allPeriods);
            // get the last one that contain the last end date used in the system
            final DbRowReservePeriod period = allPeriods.get(allPeriods.size() - 1);
            lastDate = period.getEndDate();
        }

        // increase one day
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(lastDate);
        calendar.add(Calendar.DATE, 1);

        return calendar.getTime();
    }

    public ReserveEurPeriod getPeriod(Integer id) {
        return repository.getPeriod(id);
    }

    public List<IdNameModelData> getPeriods() {
        final List<DbRowReservePeriod> allPeriods = repository.getPeriods();
        Collections.sort(allPeriods, Collections.reverseOrder());

        final List<IdNameModelData> result = new ArrayList<IdNameModelData>();

        for (final DbRowReservePeriod period : allPeriods) {
            final StringBuffer buffer = new StringBuffer();
            buffer.append(DateHelper.formatToDate(period.getStartDate()));
            if (period.getEndDate() != null) {
                buffer.append(" - ");
                buffer.append(DateHelper.formatToDate(period.getEndDate()));

                // calculate days
                final long days = DateHelper.calculateDiff(period.getStartDate(), period.getEndDate());
                buffer.append(" (").append(days);

                if (days == 1) {
                    buffer.append(" day)");
                } else {
                    buffer.append(" days)");
                }
            }

            result.add(new IdNameModelData(period.getId(), buffer.toString()));
        }
        return result;
    }

    public ReserveEurEoDBalance getPublish(Date date) {
        try {
            final DbRowReservePublish publish = repository.findPublish(date);
            return publish.toEoDBalance();
        } catch (final ObjectNotFoundException e) {
            return null;
        }
    }

    public List<ReserveEurEoDBalance> getSimulateEoDBalances(Integer periodId,
                    Date startDate,
                    Date endDate,
                    Number balance,
                    Number deposit,
                    Number marginalLending,
                    List<ReserveEurEoDBalance> currentBalances) {

        // get details of the period
        final ReserveEurPeriod reservePeriod = repository.getPeriod(periodId);

        final double totalDays = calculateTotalDays(reservePeriod);
        final double average = reservePeriod.getAverage();
        double sumBalance = 0;
        double iBalance = 0;

        // loop in the current Balances
        for (int i = 0; i < currentBalances.size(); i++) {
            final ReserveEurEoDBalance eodBalance = currentBalances.get(i);
            double averageToRealize = 0;

            // calculate average
            averageToRealize = calculateAverage(
                            reservePeriod,
                            eodBalance.getDatePublish(),
                            totalDays,
                            average,
                            sumBalance,
                            iBalance,
                            averageToRealize);

            // we have to set the balance in two options:
            // 1. If the end date is null it means that user wants to publish the balance only for ONE date
            // 2. If the end date is not null, it means that user wants to publish the balance for a RANGE of date. 
            // NOTE: We could do it in just one if, like the way below is more clear to others developers.
            // TOP NOTE: Don't change the balances that are readOnly
            if (endDate == null && eodBalance.getDatePublish().equals(startDate)) {
                setBalance(balance, deposit, marginalLending, eodBalance);
            } else if (endDate != null && eodBalance.getDatePublish().getTime() >= startDate.getTime()
                            && eodBalance.getDatePublish().getTime() <= endDate.getTime()
                            && !eodBalance.isReadOnly()) {
                setBalance(balance, deposit, marginalLending, eodBalance);
            }

            // the average should change all the time
            eodBalance.setAverage(averageToRealize);
            // if the balance exist, compute it.
            if (eodBalance.getBalance() != null) {
                sumBalance = sumBalance + eodBalance.getBalance().doubleValue();
            }

            // replace the balance with the new change.
            currentBalances.set(i, eodBalance);

            // increase the balance for the next loop
            iBalance++;
        }

        return currentBalances;
    }

    @Transactional(readOnly = false)
    public ReserveEurPublish publishEoDBalances(Integer periodId, List<ReserveEurEoDBalance> currentBalances) {

        for (final ReserveEurEoDBalance eodBalance : currentBalances) {
            if (eodBalance.isModified()) {
                // this object was modified, it needs to be publish
                final BigDecimal balance = new BigDecimal(eodBalance.getBalance());
                final BigDecimal deposit = eodBalance.getDeposit() != null ? new BigDecimal(eodBalance.getDeposit())
                                : null;
                final BigDecimal lending = eodBalance.getMarginalLending() != null ? new BigDecimal(
                                eodBalance.getMarginalLending()) : null;
                repository.publish(eodBalance.getDatePublish(), balance, deposit, lending);
            }
        }

        // create the object that represents this publish and return
        return new ReserveEurPublish(getEoDBalances(periodId), new Date(), periodId);
    }

    @Transactional(readOnly = false)
    public void removeClosedDate(List<Date> closedDates) {
        for (final Date date : closedDates) {
            repository.removeClosedDate(date);
        }
    }

    @Transactional(readOnly = false)
    public void republish(Date date, Double balance, Double deposit, Double lending) {
        repository.rePublish(date, balance, deposit, lending);
    }

    private void setBalance(Number balance,
                    Number deposit,
                    Number marginalLending,
                    final ReserveEurEoDBalance eodBalance) {
        // set the balance of end of day
        eodBalance.setBalance(balance.doubleValue());

        // if there is a deposit
        if (deposit != null) {
            eodBalance.setDeposit(deposit.doubleValue());
        }

        // if there is a marginal lending
        if (marginalLending != null) {
            eodBalance.setMarginalLending(marginalLending.doubleValue());
        }

        // object was modified, so if published, we have to add to the database
        eodBalance.modified();
    }

    @Transactional(readOnly = false)
    public Boolean updatePeriod(Integer id, Date startDate, Date endDate, Double average) {
        // end date should never be before start date
        if (DateHelper.calculateDiff(startDate, endDate) < 0) {
            // end date is before start date
            final IlmsSharedException sharedException = IlmsExceptionUtils.toSharedException(
                            new RuntimeException("End Date needs to be after Start Date"),
                            IlmsSharedExceptionCodes.END_DATE_BEFORE_START_DATE);
            throw sharedException;
        }

        // check if the update is okay. We should ALERT for some problems
        boolean isThereWarning = false;
        // check if startDate is already inside another period.
        if (repository.isDateAlreadyInAnotherPeriod(startDate, id) || repository.isDateAlreadyInAnotherPeriod(
                        endDate,
                        id)) {
            isThereWarning = true;
        }

        try {
            // check if it is a valid average. If null, replace the actual value per null, 
            // otherwise transform the number in a BigDecimal
            final BigDecimal amount = average != null ? new BigDecimal(average) : null;

            repository.updatePeriod(id, startDate, endDate, amount);
            return isThereWarning;
        } catch (final org.hibernate.exception.ConstraintViolationException e) {
            throw IlmsExceptionUtils.toSharedException(
                            new RuntimeException("Start Date is not available"),
                            IlmsSharedExceptionCodes.START_DATE_NOT_AVAILABLE);
        }
    }
}