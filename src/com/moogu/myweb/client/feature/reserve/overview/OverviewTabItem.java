package com.moogu.myweb.client.feature.reserve.overview;

import com.google.gwt.user.client.Element;
import com.moogu.myweb.client.common.widget.misc.TabItem;

public class OverviewTabItem extends TabItem {

    /**
     * Panel for user management in management module
     */
    private OverviewPanel overviewPanel = new OverviewPanel();

    public OverviewTabItem() {
        super("Overview");
    }

    @Override
    protected void onRender(Element parent, int index) {
        super.onRender(parent, index);
        this.add(overviewPanel);
        this.doLayout(true);
        setAutoHeight(true);
    }

    @Override
    public void onSelection() {
        overviewPanel.onSelection();
    }
}
