package com.moogu.myweb.client;

import java.util.Date;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.moogu.myweb.client.common.genericmodeldata.IdNameModelData;
import com.moogu.myweb.shared.reserve.ReserveEurEoDBalance;
import com.moogu.myweb.shared.reserve.ReserveEurGraph;
import com.moogu.myweb.shared.reserve.ReserveEurPeriod;
import com.moogu.myweb.shared.reserve.ReserveEurPublish;

/**
 * This is the interface for everything related to RESERVE EUR
 * 
 * @author i21726
 * 
 */
public interface ReserveModuleRemoteServiceAsync {

    void dummy(AsyncCallback<Void> callback);

    void addClosedDate(Date closedDate, AsyncCallback<Void> callback);

    void removeClosedDate(List<Date> closedDates, AsyncCallback<Void> callback);

    void getDatesByYear(int year, AsyncCallback<List<Date>> callback);

    void createNewPeriod(Date startDate, Date endDate, Double average, AsyncCallback<Integer> callback);

    void getPeriods(AsyncCallback<List<IdNameModelData>> callback);

    void getPeriod(Integer id, AsyncCallback<ReserveEurPeriod> callback);

    void updatePeriod(Integer id, Date startDate, Date endDate, Double average, AsyncCallback<Boolean> callback);

    void getNextStartDate(AsyncCallback<Date> callback);

    void getEoDBalances(Integer id, AsyncCallback<List<ReserveEurEoDBalance>> callback);

    void getSimulateEoDBalances(Integer periodId, Date startDate,
                    Date endDate,
                    Number balance,
                    Number deposit,
                    Number marginalLending,
                    List<ReserveEurEoDBalance> currentBalances,
                    AsyncCallback<List<ReserveEurEoDBalance>> callback);

    void publishEoDBalances(Integer id,
                    List<ReserveEurEoDBalance> currentBalances,
                    AsyncCallback<ReserveEurPublish> callback);

    void getPublish(Date date, AsyncCallback<ReserveEurEoDBalance> callback);

    void republish(Date date, Double balance, Double deposit, Double lending, AsyncCallback<Void> callback);

    void getGraphDetails(Integer periodId, List<ReserveEurEoDBalance> balances, AsyncCallback<ReserveEurGraph> callback);

    void getActualPeriod(AsyncCallback<Integer> callback);

    void getLastPublishTimestamp(Integer periodId, AsyncCallback<Date> callback);
}
