package com.moogu.myweb.client.common.widget.misc;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.google.gwt.user.client.Element;

public class TabItem extends com.extjs.gxt.ui.client.widget.TabItem {

	/**
	 * Construct an Item Tab instance
	 * 
	 * @param title
	 */
	public TabItem(String title) {
		super(title);
	}

	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		this.setHeight(795);
		addListener(Events.Select, new Listener<BaseEvent>() {

			public void handleEvent(BaseEvent be) {
				TabItem.this.onSelection();
			}
		});
		setBorders(false);
	}

	/**
	 * To be overridden to handle tab selection event
	 */
	public void onSelection() {
	}
}