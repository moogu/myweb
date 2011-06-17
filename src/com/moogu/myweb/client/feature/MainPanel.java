package com.moogu.myweb.client.feature;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.moogu.myweb.client.MyWebEntryPoint;
import com.moogu.myweb.client.feature.management.ManagementModuleTabItem;
import com.moogu.myweb.client.feature.reserve.ReserveTabItem;
import com.moogu.myweb.client.feature.technical.TechnicalModuleTabItem;

public class MainPanel extends LayoutContainer {

	final TabPanel tp = new TabPanel();

	final TabItem reserveTabItem = new ReserveTabItem();

	private static MainPanel instance = null;

	public static MainPanel getInstance() {
		if (MainPanel.instance == null) {
			MainPanel.instance = new MainPanel();
		}
		return MainPanel.instance;
	}

	private MainPanel() {
		super(new BorderLayout());
		this.add(tp, new BorderLayoutData(LayoutRegion.CENTER));
		tp.setWidth(1000);
		tp.setHeight(800);

		// for Reserve EUR
		if (MyWebEntryPoint.isUserRole(MyWebEntryPoint.ROLE_4)) {
			tp.add(reserveTabItem);
		}

		if (MyWebEntryPoint.isUserRole(MyWebEntryPoint.ROLE_3)
				|| MyWebEntryPoint.isUserRole(MyWebEntryPoint.ROLE_2)) {
			final TabItem managementModuleTabItem = new ManagementModuleTabItem();
			tp.add(managementModuleTabItem);
		}

		if (MyWebEntryPoint.isUserRole(MyWebEntryPoint.ROLE_4)) {
			// if technical user
			final TabItem TechnicalModuleTabItem = new TechnicalModuleTabItem();
			tp.add(TechnicalModuleTabItem);
		}

		setBorders(false);
		setAutoHeight(false);
		setAutoWidth(false);
		setMonitorWindowResize(false);
		tp.setLayoutData(new FitLayout());
	}

	@Override
	public void onWindowResize(int width, int height) {
		this.setSize(width - 10, height);
	}
}