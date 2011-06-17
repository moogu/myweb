package com.moogu.myweb.client.common.widget.dialog;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;

public class SharedExceptionDialog {

	public static void display(String message, final String stackTrace) {
		SharedExceptionDialog.display(message, stackTrace, MessageBox.ERROR);

	}

	public static void display(String message, final String stackTrace,
			String iconStyle) {
		final MessageBox messageBox = new MessageBox();

		messageBox.setIcon(iconStyle);
		messageBox.setTitle("Error detected");
		messageBox.setButtons(MessageBox.OKCANCEL);
		messageBox.setMessage(message);
		messageBox.setClosable(true);
		final Button button = (Button) messageBox.getDialog().getButtonBar()
				.getItemByItemId(Dialog.CANCEL);
		button.setText("Details");
		button.removeAllListeners();

		button.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				ExceptionDetailDialog.display(stackTrace);
			}
		});
		messageBox.getDialog().setFocusWidget(null);
		messageBox.show();

	}

	public static void displayAsWarning(String message, final String stackTrace) {
		SharedExceptionDialog.display(message, stackTrace, MessageBox.WARNING);
	}
}
