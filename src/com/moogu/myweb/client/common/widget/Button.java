package com.moogu.myweb.client.common.widget;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;

public class Button extends com.extjs.gxt.ui.client.widget.button.Button {

	public Button() {
		this("");
	}

	public Button(String text) {
		super(text);
		addStyleName("effectiveWidget");
	}

	public Button(String string,
			SelectionListener<ButtonEvent> selectionListener) {
		super(string, selectionListener);
	}
}