package com.moogu.myweb.client.feature.management.usernames;

import com.google.gwt.user.client.Element;
import com.moogu.myweb.client.common.widget.misc.TabItem;

/**
 * For User names of user
 * 
 * @author i20075
 * 
 */
public class UserNamesTabItem extends TabItem {

	/**
	 * Panel for user management in management module
	 */
	private UserNamesPanel userPanel = new UserNamesPanel();

	public UserNamesTabItem() {
		super("User Names");
	}

	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		this.add(userPanel);
		this.doLayout(true);
		setAutoHeight(true);
	}
}
