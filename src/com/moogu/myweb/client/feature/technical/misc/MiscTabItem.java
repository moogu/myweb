package com.moogu.myweb.client.feature.technical.misc;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.moogu.myweb.client.common.widget.misc.TabItem;

public class MiscTabItem extends TabItem {

    private final LogManagmentPanel logManagmentPanel = new LogManagmentPanel();

    public MiscTabItem() {
        super("Misc");
        setLayout(new RowLayout(Orientation.VERTICAL));
        this.add(logManagmentPanel.getComponent());
        setAutoHeight(true);
    }

    @Override
    public void onSelection() {
        super.onShow();
    }
}
