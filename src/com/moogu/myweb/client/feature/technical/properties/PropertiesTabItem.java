package com.moogu.myweb.client.feature.technical.properties;

import com.extjs.gxt.ui.client.widget.TabItem;
import com.google.gwt.user.client.Element;

public class PropertiesTabItem extends TabItem {

    private final PropertiesPanel panel = new PropertiesPanel();

    public PropertiesTabItem() {
        super("Properties");
    }

    @Override
    protected void onRender(Element parent, int index) {
        super.onRender(parent, index);
        this.add(this.panel.getComponent());
        this.setAutoHeight(true);
    }
}
