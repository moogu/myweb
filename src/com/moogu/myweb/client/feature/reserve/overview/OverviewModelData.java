package com.moogu.myweb.client.feature.reserve.overview;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.data.BaseModelData;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.moogu.myweb.shared.reserve.ReserveEurEoDBalance;

public class OverviewModelData extends BaseModelData {

    private static final long serialVersionUID = 1L;

    public static final String DATE_PUBLISH_PROPERTY = "datePublishProperty";

    public static final String AVERAGE_PROPERTY = "averageProperty";

    public static final String BALANCE_PROPERTY = "balanceProperty";

    public static final String DEPOSIT_PROPERTY = "depositProperty";

    public static final String MARGINAL_LENDING_PROPERTY = "marginalLendingProperty";

    public static final String READ_ONLY_PROPERTY = "readOnlyEntityProperty";

    public static final String MODEL_DATA_CHANGED_PROPERTY = "modelDataChangedProperty";

    public static final String ORIGINAL_ENTITY_PROPERTY = "originalEntityProperty";

    public static final String DAY_MONTH_AXIS_PROPERTY = "dayMonthAxisProperty";

    private static final DateTimeFormat DAY_MONTH_AXIS_X = DateTimeFormat.getFormat("dd/MMM");

    public OverviewModelData(ReserveEurEoDBalance balance) {
        this.set(OverviewModelData.DATE_PUBLISH_PROPERTY, balance.getDatePublish());
        this.set(OverviewModelData.BALANCE_PROPERTY, balance.getBalance());
        this.set(OverviewModelData.AVERAGE_PROPERTY, balance.getAverage());
        this.set(OverviewModelData.DEPOSIT_PROPERTY, balance.getDeposit());
        this.set(OverviewModelData.MARGINAL_LENDING_PROPERTY, balance.getMarginalLending());
        this.set(OverviewModelData.READ_ONLY_PROPERTY, Boolean.valueOf(balance.isReadOnly()));
        this.set(OverviewModelData.MODEL_DATA_CHANGED_PROPERTY, Boolean.valueOf(balance.isModified()));
        this.set(OverviewModelData.ORIGINAL_ENTITY_PROPERTY, balance);
        this.set(OverviewModelData.DAY_MONTH_AXIS_PROPERTY,
                        OverviewModelData.DAY_MONTH_AXIS_X.format(balance.getDatePublish()));

    }

    public static List<OverviewModelData> createFromList(List<ReserveEurEoDBalance> list) {
        final List<OverviewModelData> result = new ArrayList<OverviewModelData>();
        // if list is null, return empty list
        if (list != null) {
            for (final ReserveEurEoDBalance balance : list) {
                result.add(new OverviewModelData(balance));
            }
        }
        return result;
    }

    public static List<ReserveEurEoDBalance> getPojos(List<OverviewModelData> models) {
        final List<ReserveEurEoDBalance> result = new ArrayList<ReserveEurEoDBalance>();
        // if models are null, return empty list
        if (models != null) {
            for (final OverviewModelData modelData : models) {
                final ReserveEurEoDBalance balance = (ReserveEurEoDBalance) modelData.get(OverviewModelData.ORIGINAL_ENTITY_PROPERTY);
                result.add(balance);
            }
        }
        return result;
    }
}