package com.moogu.myweb.shared.reserve;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ReserveEurPublish implements Serializable, IsSerializable {

    private static final long serialVersionUID = -5864771301576235688L;

    private List<ReserveEurEoDBalance> balances;

    private Date lastPublishTimestamp;

    private Integer periodId;

    // Needed for serialization
    @SuppressWarnings("unused")
    private ReserveEurPublish() {
    }

    public ReserveEurPublish(List<ReserveEurEoDBalance> balances, Date lastPublishTimestamp, Integer periodId) {
        super();
        this.balances = balances;
        this.lastPublishTimestamp = lastPublishTimestamp;
        this.periodId = periodId;
    }

    public List<ReserveEurEoDBalance> getBalances() {
        return this.balances;
    }

    public Date getLastPublishTimestamp() {
        return this.lastPublishTimestamp;
    }

    public Integer getPeriodId() {
        return this.periodId;
    }
}
