package com.moogu.myweb.client;

import java.util.Date;
import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.moogu.myweb.client.common.genericmodeldata.IdNameModelData;
import com.moogu.myweb.shared.reserve.ReserveEurEoDBalance;
import com.moogu.myweb.shared.reserve.ReserveEurGraph;
import com.moogu.myweb.shared.reserve.ReserveEurPeriod;
import com.moogu.myweb.shared.reserve.ReserveEurPublish;
import com.moogu.myweb.shared.utils.IlmsSharedException;

@RemoteServiceRelativePath("reserveService")
public interface ReserveModuleRemoteService extends RemoteService {

    // This method is to declare explicitly IlmsSharedException as it will be 
    // in GWT serialization white list
    void dummy() throws IlmsSharedException;

    void addClosedDate(Date closedDate);

    void removeClosedDate(List<Date> closedDates);

    List<Date> getDatesByYear(int year);

    Integer createNewPeriod(Date startDate, Date endDate, Double average);

    List<IdNameModelData> getPeriods();

    ReserveEurPeriod getPeriod(Integer id);

    Boolean updatePeriod(Integer id, Date startDate, Date endDate, Double average);

    /**
     * Date to fill the next start date combo box
     * 
     * @return date of next period
     */
    Date getNextStartDate();

    List<ReserveEurEoDBalance> getEoDBalances(Integer id);

    List<ReserveEurEoDBalance> getSimulateEoDBalances(Integer periodId, Date startDate,
                    Date endDate,
                    Number balance,
                    Number deposit,
                    Number marginalLending,
                    List<ReserveEurEoDBalance> currentBalances);

    ReserveEurPublish publishEoDBalances(Integer id, List<ReserveEurEoDBalance> currentBalances);

    ReserveEurEoDBalance getPublish(Date date);

    void republish(Date date, Double balance, Double deposit, Double lending);

    ReserveEurGraph getGraphDetails(Integer periodId, List<ReserveEurEoDBalance> balances);

    Integer getActualPeriod();

    Date getLastPublishTimestamp(Integer periodId);
}
