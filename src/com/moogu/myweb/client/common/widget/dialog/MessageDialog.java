package com.moogu.myweb.client.common.widget.dialog;

import com.extjs.gxt.ui.client.widget.MessageBox;

public class MessageDialog {

    /**
     * @param message
     * @param icon something like {@link MessageBox}.WARNING
     * @param warning true to display a warning icon, false to display an error message
     */
    public static void display(String title, String message, String icon) {
        final MessageBox messageBox = new MessageBox();
        messageBox.setModal(true);
        messageBox.setIcon(icon);
        messageBox.setTitle(title);
        messageBox.setButtons(MessageBox.OK);
        messageBox.getDialog().setStyleAttribute("text-align", "left");
        messageBox.setMessage(message);
        messageBox.getDialog().setWidth(400);
        messageBox.getDialog().setAutoHeight(true);
        messageBox.setClosable(true);
        messageBox.getDialog().setFocusWidget(null);
        messageBox.show();
    }

    public static void displayUserEntryError(String message) {
        MessageDialog.display("Invalid entry", message, MessageBox.WARNING);
    }

}
