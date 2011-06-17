package com.moogu.myweb.client.common.widget.html;

import com.extjs.gxt.ui.client.widget.Html;
import com.google.gwt.core.client.GWT;

public class ProcessingImage {

	private static int count = 0;

	private static String nextId() {
		return "IlmsProcessingImage_" + ProcessingImage.count++;
	}

	private final Html html;

	public ProcessingImage() {
		this(32, 32);
	}

	public ProcessingImage(int width, int height) {
		html = new Html();
		html.setSize(width, height);
		html.setHtml("<img id='" + ProcessingImage.nextId() + "' src='"
				+ GWT.getModuleBaseURL().replace(GWT.getModuleName() + "/", "")
				+ "res/images/ajax-loader-1.gif'/>");
		html.setVisible(false);
	}

	public Html getComponent() {
		return html;
	}

	public boolean isProcessing() {
		return html.isVisible();
	}

	public void setStateProcessing(boolean processing) {
		html.setVisible(processing);
	}

	public void startProcess() {
		html.setVisible(true);
	}

	public void stopProcess() {
		html.setVisible(false);
	}
}
