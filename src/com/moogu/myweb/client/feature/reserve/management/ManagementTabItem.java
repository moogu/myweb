package com.moogu.myweb.client.feature.reserve.management;

import com.google.gwt.user.client.Element;
import com.moogu.myweb.client.common.widget.misc.TabItem;

public class ManagementTabItem extends TabItem {

    /**
     * Panel for user management in management module
     */
    private ManagementPanel managementPanel = new ManagementPanel();

    public ManagementTabItem() {
        super("Management");
    }

    @Override
    protected void onRender(Element parent, int index) {
        super.onRender(parent, index);
        this.add(this.managementPanel);
        this.doLayout(true);
        this.setAutoHeight(true);
    }

    @Override
    public void onSelection() {
        this.managementPanel.onSelection();
    }
}
