package com.moogu.myweb.client.feature.reserve;

import com.extjs.gxt.ui.client.widget.TabPanel;
import com.google.gwt.user.client.Element;
import com.moogu.myweb.client.MyWebEntryPoint;
import com.moogu.myweb.client.common.widget.misc.TabItem;
import com.moogu.myweb.client.feature.reserve.management.ManagementTabItem;
import com.moogu.myweb.client.feature.reserve.overview.OverviewTabItem;

public class ReserveTabItem extends TabItem {

    private final TabPanel tp = new TabPanel();

    private final OverviewTabItem overview = new OverviewTabItem();

    public ReserveTabItem() {
        super("Reserve EUR");
    }

    @Override
    protected void onRender(Element parent, int index) {
        super.onRender(parent, index);
        tp.setAutoHeight(true);
        this.add(tp);

        // everybody can see it
        tp.add(overview);

        // ILMS01 can't see it
        if (MyWebEntryPoint.isUserRole(MyWebEntryPoint.ROLE_2) || MyWebEntryPoint.isUserRole(MyWebEntryPoint.ROLE_3)) {
            final ManagementTabItem managementTabItem = new ManagementTabItem();
            tp.add(managementTabItem);
        }
    }
}