package com.moogu.myweb.client.common.widget.misc;

import com.extjs.gxt.ui.client.widget.HorizontalPanel;

public class Separator extends HorizontalPanel {

    public Separator(int width) {
        super();
        this.addStyleName("effectiveWidget");
        this.setWidth(width);
    }

    public Separator(int width, int height) {
        super();
        this.addStyleName("effectiveWidget");
        this.setWidth(width);
        this.setHeight(height);
    }

}