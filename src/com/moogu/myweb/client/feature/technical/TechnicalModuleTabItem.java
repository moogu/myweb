package com.moogu.myweb.client.feature.technical;

import com.extjs.gxt.ui.client.widget.TabPanel;
import com.google.gwt.user.client.Element;
import com.moogu.myweb.client.common.widget.misc.TabItem;
import com.moogu.myweb.client.feature.technical.misc.MiscTabItem;
import com.moogu.myweb.client.feature.technical.properties.PropertiesTabItem;

/**
 * Technical Module
 * 
 * @author i21726 - Patrick Santana
 * 
 */
public class TechnicalModuleTabItem extends TabItem {

    private final TabPanel tp = new TabPanel();

    private final MiscTabItem cacheTabItem = new MiscTabItem();

    private final PropertiesTabItem propertiesTabItem = new PropertiesTabItem();

    public TechnicalModuleTabItem() {
        super("Technical Module");
    }

    @Override
    protected void onRender(Element parent, int index) {
        super.onRender(parent, index);
        tp.setAutoHeight(true);

        this.add(tp);

        // add technical module
        tp.add(cacheTabItem);
        tp.add(propertiesTabItem);
    }
}