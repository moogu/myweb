package com.moogu.myweb.client.common.widget.dialog;

import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;

public class ExceptionDetailDialog extends Window {

    public static ExceptionDetailDialog display(String stackTrace) {
        ExceptionDetailDialog dialog = new ExceptionDetailDialog(stackTrace);
        dialog.show();
        return dialog;
    }

    public ExceptionDetailDialog(String stackTrace) {
        super();
        this.setBodyBorder(false);
        this.setHeading("Error detail");
        this.setAutoHeight(false);
        this.setLayout(new FitLayout());
        this.setSize(600, 300);

        TextArea textArea = new TextArea();
        textArea.setValue(stackTrace);
        this.add(textArea);
    }

}
