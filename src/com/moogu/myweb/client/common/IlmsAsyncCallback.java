package com.moogu.myweb.client.common;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.moogu.myweb.client.common.widget.dialog.ProgressDialog;
import com.moogu.myweb.client.common.widget.dialog.SharedExceptionDialog;
import com.moogu.myweb.shared.utils.IlmsSharedException;

/**
 * This class implements the AsyncCallback. It is used in our project to show message error and also loading window.
 * 
 * @author i21726 - Patrick Santana
 */
public abstract class IlmsAsyncCallback<T> implements AsyncCallback<T> {

    /** window that represents the Loading */
    private final ProgressDialog dialog = ProgressDialog.getInstance();

    /** show the message to the user or not */
    private boolean showMessage = false;

    /**
     * Call the constructor giving false
     */
    public IlmsAsyncCallback() {
        this(false);
    }

    public IlmsAsyncCallback(boolean showMessage) {
        this(showMessage, "Status");
    }

    /**
     * User can say if wants to show the message
     * 
     * @param pShowMessage true = show; false = don't show
     */
    public IlmsAsyncCallback(boolean pShowMessage, String header) {
        this.showMessage = pShowMessage;
        if (this.showMessage) {
            this.displayProcessDialog(header);
        }
    }

    public IlmsAsyncCallback(String message) {
        this(true, message);
    }

    public void displayProcessDialog(String header) {
        this.dialog.setModal(true);
        this.dialog.setMaximizable(false);
        this.dialog.setMinimizable(false);
        this.dialog.setClosable(false);
        this.dialog.setBorders(false);
        this.dialog.setDraggable(false);
        this.dialog.setHeading(header);
        this.dialog.show();
    }

    /**
     * Simply override this method if you just want to customize user error message.
     */
    protected String getUserMessage(IlmsSharedException e) {
        final String message = "An error occured on server.";
        return message;
    }

    public void hideProcessDialog() {
        this.dialog.hide();
    }

    /**
     * Override this method if you need special handling code at failure
     * 
     * @param result
     */
    protected void onCustomFailure(IlmsSharedException e) {
        final String stringStaceTrace = e.getStringStackTrace();
        final String message = this.getUserMessage(e);
        SharedExceptionDialog.display(message, stringStaceTrace);
    }

    public void onCustomInfrastrucureError(Throwable throwable) {
        final String fullErrorMessage = throwable.getClass().getName() + " "
                        + throwable.getMessage()
                        + "\n"
                        + throwable.getStackTrace();
        ClientLog.error(fullErrorMessage);
        SharedExceptionDialog.display("System error", fullErrorMessage);
    }

    /** Overwrite to manipulate the result result */
    protected abstract void onCustomSuccess(T result);

    /**
     * @see com.google.gwt.user.client.rpc.AsyncCallback#onFailure(java.lang.Throwable)
     */
    public void onFailure(Throwable throwable) {
        if (this.showMessage) {
            this.dialog.hide();
        }
        if (throwable instanceof IlmsSharedException) {
            this.onCustomFailure((IlmsSharedException) throwable);
        } else {
            this.onCustomInfrastrucureError(throwable);
        }
    }

    /**
     * @see com.google.gwt.user.client.rpc.AsyncCallback#onSuccess(java.lang.Object)
     */
    public void onSuccess(T result) {
        if (this.showMessage) {
            this.dialog.hide();
        }
        this.onCustomSuccess(result);
    }
}