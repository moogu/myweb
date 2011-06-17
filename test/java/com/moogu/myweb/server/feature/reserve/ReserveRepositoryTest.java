package com.moogu.myweb.server.feature.reserve;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.transaction.TransactionConfiguration;

import com.moogu.myweb.server.feature.reserve.ReserveRepository;
import com.moogu.myweb.server.feature.reserve.internal.DbRowReservePeriod;
import com.moogu.myweb.server.utils.DateHelper;
import com.moogu.myweb.shared.utils.IlmsFormats;
import com.moogu.myweb.test.common.AbstractIlmsIntegrationTest;

@TransactionConfiguration(defaultRollback = true)
public class ReserveRepositoryTest extends AbstractIlmsIntegrationTest {

    @Autowired
    private ReserveRepository repository;

    public void testAddClosedDate() {
        Assert.assertNotNull(this.repository.getDatesByYear(2011));
        final int size = this.repository.getDatesByYear(2011).size();

        this.repository.addClosedDate(DateHelper.parse("01/02/2011", IlmsFormats.DATE_FORMAT));
        Assert.assertTrue(this.repository.getDatesByYear(2011).size() - 1 == size);
    }

    public void testRemoveClosedDate() {
        this.repository.addClosedDate(DateHelper.parse("01/03/2011", IlmsFormats.DATE_FORMAT));

        final List<Date> closedDates = this.repository.getDatesByYear(2011);
        Assert.assertNotNull(closedDates);
        final int size = closedDates.size();
        Assert.assertTrue(size > 0);

        this.repository.removeClosedDate(DateHelper.parse("01/03/2011", IlmsFormats.DATE_FORMAT));
        Assert.assertTrue(this.repository.getDatesByYear(2011).size() == size - 1);
    }

    public void testNullClosedDate() {
        // should happen nothing
        this.repository.addClosedDate(null);
    }

    public void removeNullClosedDate() {
        Assert.assertNotNull(this.repository.getDatesByYear(2011));
        final int size = this.repository.getDatesByYear(2011).size();

        this.repository.addClosedDate(null);
        Assert.assertTrue(this.repository.getDatesByYear(2011).size() == size);
    }

    public void testGet2011Dates() {
        Assert.assertNotNull(this.repository.getDatesByYear(2011));
        Assert.assertTrue(this.repository.getDatesByYear(2011).size() > 0);
    }

    public void testGetDatesFromWrongYear() {
        Assert.assertNull("We shouldn't have closed dates for this parameter", this.repository.getDatesByYear(1));
        Assert.assertNull("We shouldn't have closed dates for this parameter", this.repository.getDatesByYear(2000));
    }

    public void testAdd() {
        final Integer result = this.repository.createNewPeriod(new Date(), new Date(), new Double(123.123));
        Assert.assertNotNull(result);
        Assert.assertTrue(result.intValue() > 0);
    }

    public void testAddOnlyStartDate() {
        final Integer result = this.repository.createNewPeriod(new Date(), null, null);
        Assert.assertNotNull(result);
        Assert.assertTrue(result.intValue() > 0);
    }

    public void testAddOnlyStartDateAndEndDate() {
        final Integer result = this.repository.createNewPeriod(new Date(), new Date(), null);
        Assert.assertNotNull(result);
        Assert.assertTrue(result.intValue() > 0);
    }

    public void testGetPeriods() {
        final Integer period = this.repository.createNewPeriod(new Date(), new Date(), null);
        Assert.assertNotNull(period);
        Assert.assertTrue(period.intValue() > 0);

        final List<DbRowReservePeriod> result = this.repository.getPeriods();
        Assert.assertNotNull(result);
        Assert.assertTrue(result.size() > 0);
    }

    public void testGetPeriod() {
        final Integer period = this.repository.createNewPeriod(new Date(), new Date(), null);
        Assert.assertNotNull(period);
        Assert.assertTrue(period.intValue() > 0);

        final List<DbRowReservePeriod> result = this.repository.getPeriods();
        Assert.assertNotNull(result);
        Assert.assertTrue(result.size() > 0);

        final Integer id = result.get(0).getId();
        Assert.assertNotNull(this.repository.getPeriod(id));

        // try to get the period with unknown id
        try {
            this.repository.getPeriod(-1);
            Assert.fail();
        } catch (final ObjectNotFoundException e) {
            // correct
        }
    }

    public void testUpdatePeriod() {
        final Integer period = this.repository.createNewPeriod(new Date(), new Date(), null);
        Assert.assertNotNull(period);
        Assert.assertTrue(period.intValue() > 0);

        this.repository.updatePeriod(period, new Date(), new Date(), new BigDecimal(1234d));
    }

    public void testUpdatePeriodWithNullAverage() {
        final Integer period = this.repository.createNewPeriod(new Date(), new Date(), 1234d);
        Assert.assertNotNull(period);
        Assert.assertTrue(period.intValue() > 0);

        this.repository.updatePeriod(period, new Date(), new Date(), null);
    }

    public void testAddReservePublish() {
        final Calendar calendar = Calendar.getInstance();
        this.repository.publish(calendar.getTime(), new BigDecimal(123456), null, new BigDecimal(23));

        // add one day
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        this.repository.publish(calendar.getTime(), new BigDecimal(123456), new BigDecimal(23), null);
    }

    public void testGetActualPeriod() {
        // not period for today, should receive null
        Assert.assertNull(this.repository.getActualPeriod());

        // create a period for today, and search again
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -1);
        final Date startOneMonthAgo = calendar.getTime();

        calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 1);
        final Date endOneMonthAfter = calendar.getTime();

        final Integer result = this.repository.createNewPeriod(startOneMonthAgo, endOneMonthAfter, new Double(
                        232323232d));
        Assert.assertNotNull(result);
        Assert.assertTrue(result.intValue() > 0);
        Assert.assertNotNull(this.repository.getActualPeriod());
    }

    public void testDateCalculation() {

        //12/12/2012 - 30/12/2012 -> 19 days    
        Assert.assertTrue(DateHelper.calculateDiff(
                        DateHelper.createMidnightDate(2012, 12, 12),
                        DateHelper.createMidnightDate(2012, 12, 30)) == 19l);

        //08/12/2010 - 09/01/2011 -> 33 days
        Assert.assertTrue(DateHelper.calculateDiff(
                        DateHelper.createMidnightDate(2010, 12, 8),
                        DateHelper.createMidnightDate(2011, 1, 9)) == 33l);

        //11/05/2011 - 14/06/2011 -> 35 days
        Assert.assertTrue(DateHelper.calculateDiff(
                        DateHelper.createMidnightDate(2011, 5, 11),
                        DateHelper.createMidnightDate(2011, 6, 14)) == 35l);

        //27/04/2011 - 10/05/2011 -> 14 days
        Assert.assertTrue(DateHelper.calculateDiff(
                        DateHelper.createMidnightDate(2011, 4, 27),
                        DateHelper.createMidnightDate(2011, 5, 10)) == 14l);

        //31/03/2011 - 26/04/2011 -> 27 days
        Assert.assertTrue(DateHelper.calculateDiff(
                        DateHelper.createMidnightDate(2011, 3, 31),
                        DateHelper.createMidnightDate(2011, 4, 26)) == 27l);

        //10/01/2011 - 15/01/2011 -> 6 days
        Assert.assertTrue(DateHelper.calculateDiff(
                        DateHelper.createMidnightDate(2011, 1, 10),
                        DateHelper.createMidnightDate(2011, 1, 15)) == 6l);

        //01/03/2011 - 30/03/2011 -> 30 days
        Assert.assertTrue(DateHelper.calculateDiff(
                        DateHelper.createMidnightDate(2011, 3, 1),
                        DateHelper.createMidnightDate(2011, 3, 30)) == 30l);
    }
}