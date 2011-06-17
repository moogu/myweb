package com.moogu.myweb.client.feature.management;

import com.extjs.gxt.ui.client.widget.TabPanel;
import com.google.gwt.user.client.Element;
import com.moogu.myweb.client.common.widget.misc.TabItem;
import com.moogu.myweb.client.feature.management.usernames.UserNamesTabItem;

/**
 * Tab item for Management Module
 * 
 * @author i21726
 * 
 */
public class ManagementModuleTabItem extends TabItem {

	private final TabPanel tp = new TabPanel();

	/** user names */
	private final TabItem userNames = new UserNamesTabItem();

	public ManagementModuleTabItem() {
		super("Management Module");
	}

	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		tp.setAutoHeight(true);
		this.add(tp);
		tp.add(userNames);
	}
}