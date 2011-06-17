package com.moogu.myweb.client.common.widget.dialog;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.google.gwt.user.client.Element;
import com.moogu.myweb.client.common.widget.misc.Label;

/**
 * This is the windows showed when an operation is done in via RPC. It is a
 * singleton. It improves the speed when invoking RPC methods
 * 
 */
public class ProgressDialog extends Window {

	/** singleton of this class */
	private static ProgressDialog instance = null;

	/**
	 * Singleton getInstance implementation
	 * 
	 * @return a instance of this clas
	 */
	public static ProgressDialog getInstance() {
		/** check if it is null */
		if (ProgressDialog.instance == null) {
			/** generate the dialog for the first time */
			ProgressDialog.instance = new ProgressDialog();
		}

		/** return an instance */
		return ProgressDialog.instance;
	}

	/** private method, to not be instanced */
	private ProgressDialog() {
	}

	/**
	 * @see com.extjs.gxt.ui.client.widget.Window#onRender(com.google.gwt.user.client.Element,
	 *      int)
	 */
	@Override
	protected void onRender(Element parent, int pos) {
		super.onRender(parent, pos);
		setBodyBorder(false);
		setLayout(new RowLayout(Orientation.VERTICAL));
		this.add(new LayoutContainer(), new RowData(100, 22));
		this.add(new Label("Processing ..."), new RowData(100, 22));
		this.add(new LayoutContainer(), new RowData(100, 22));
	}
}
