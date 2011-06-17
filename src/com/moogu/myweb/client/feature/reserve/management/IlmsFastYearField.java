package com.moogu.myweb.client.feature.reserve.management;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.google.gwt.event.dom.client.KeyCodes;

/**
 * To handle year in a text field
 * 
 * @author i21726 - Patrick Santana
 * 
 */
public abstract class IlmsFastYearField extends LayoutContainer {

    private final TextField<String> textField = new TextField<String>();

    /**
     * Construction
     */
    public IlmsFastYearField() {
        super();
        this.setLayout(new BorderLayout());
        this.setSize(35, 22);

        this.textField.setEmptyText("");
        this.add(this.textField, new BorderLayoutData(LayoutRegion.CENTER));
        this.setBorders(false);

        this.textField.addListener(Events.Focus, new Listener<BaseEvent>() {

            public void handleEvent(BaseEvent be) {
                IlmsFastYearField.this.textField.selectAll();
            }

        });

        this.textField.addKeyListener(new KeyListener() {

            @Override
            public void componentKeyUp(ComponentEvent event) {
                if (event.getKeyCode() == KeyCodes.KEY_TAB || event.getKeyCode() == KeyCodes.KEY_SHIFT
                                || event.getKeyCode() == KeyCodes.KEY_CTRL
                                || event.getKeyCode() == KeyCodes.KEY_ALT
                                || event.getKeyCode() == 65) {
                    return;
                }

                String inText = IlmsFastYearField.this.textField.getValue();
                if (inText == null || inText.length() == 0) {
                    return;
                }

                while (inText.length() > 4) {
                    inText = inText.substring(1);
                }

                if (!IlmsFastYearField.this.containsOnlyNumbers(inText)) {
                    if (inText.length() == 1) {
                        inText = "";
                    } else {
                        inText = inText.substring(0, inText.length() - 1);
                    }

                    return;
                }

                IlmsFastYearField.this.textField.setValue(inText);

                if (inText.length() == 4) {
                    IlmsFastYearField.this.onSelect(inText);
                }
            }
        });
    }

    public abstract void onSelect(String year);

    public String getValue() {
        return this.textField.getValue();
    }

    private boolean containsOnlyNumbers(String str) {
        for (int i = 0; i < str.length(); i++) {
            //If we find a non-digit character we return false.
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }

        return true;
    }
}