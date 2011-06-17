package com.moogu.myweb.client.feature.technical.misc;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;

public class LogManagmentPanel {

    private final FieldSet fieldSet;

    public LogManagmentPanel() {
        this.fieldSet = new FieldSet();
        this.fieldSet.setStyleAttribute("margin", "5px");
        this.fieldSet.setHeading("Log Management");
        this.fieldSet.setLayout(new RowLayout(Orientation.HORIZONTAL));

        final Html html = new Html("<a href='monitor/log4j' target='_blank'>Click here to configure logs</a>");
        this.fieldSet.add(html);
        this.fieldSet.setHeight(120);
    }

    public Component getComponent() {
        return this.fieldSet;
    }

}
