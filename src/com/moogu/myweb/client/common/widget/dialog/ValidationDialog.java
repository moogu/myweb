package com.moogu.myweb.client.common.widget.dialog;

import com.moogu.myweb.shared.utils.Validation;

public class ValidationDialog {

	public static void display(Validation validation) {
		MessageDialog.displayUserEntryError(ValidationDialog
				.getText(validation));
	}

	private static String getText(Validation validation) {
		return "You have entered invalid data.<br/>"
				+ "Please, correct it before submit.<br/><br/>"
				+ validation.toStringLn();
	}

}
