package com.moogu.myweb.client.common.widget.misc;

public class Label extends com.extjs.gxt.ui.client.widget.Label {

	public Label() {
		this("");
	}

	public Label(String text) {
		super(text);
		addStyleName("effectiveWidget");
	}
}