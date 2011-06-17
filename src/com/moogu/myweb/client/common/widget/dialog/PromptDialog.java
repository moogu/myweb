package com.moogu.myweb.client.common.widget.dialog;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.google.gwt.user.client.Element;
import com.moogu.myweb.client.common.widget.Button;
import com.moogu.myweb.client.common.widget.misc.Label;
import com.moogu.myweb.client.common.widget.misc.Separator;

/**
 * This class is intended to replace the Message.Prompt. With it, you can define
 * rules.
 * 
 * @author i21726 - (Patrick Santana)
 */
public abstract class PromptDialog extends Window implements
		Listener<BaseEvent> {

	/** label for the screen */
	private final Label label;

	/** field in the screen */
	private final TextField<String> field;

	/** button OK */
	private Button btnOK = new Button("OK");

	/** button Cancel */
	private Button btnCancel = new Button("Cancel");

	/** Heading of Dialog */
	private String dialogHeading;

	/** Heading of FieldSet */
	private String fieldSetHeading;

	/**
	 * Create the Prompt Dialog
	 * 
	 * @param label
	 *            the label, eg: Group name
	 * @param widthField
	 *            , the width of field
	 * @param allowBlanck
	 *            if user can set empty values
	 * @param minLength
	 *            minimum length for checking
	 * @param maxLength
	 *            maximum length for checking
	 * @param dialogHeading
	 *            dialog heading
	 * @param fieldSetHeading
	 *            field set heading
	 */
	public PromptDialog(String label, int widthField, boolean allowBlanck,
			int minLength, int maxLength, String dialogHeading,
			String fieldSetHeading) {
		super();

		/** Instantiation of label and field */
		this.label = new Label(label);
		field = new TextField<String>();

		/**
		 * set properties
		 */
		field.setWidth(widthField);
		field.setMaxLength(maxLength);
		field.setMinLength(minLength);
		field.setAllowBlank(allowBlanck);
		field.setValidateOnBlur(false);

		/**
		 * set heading
		 */
		this.dialogHeading = dialogHeading;
		this.fieldSetHeading = fieldSetHeading;

		setModal(true);
		setPosition(440, 365);
	}

	/** when user clicks the button OK */
	public abstract void buttonOKClicked(String field);

	/**
	 * @see com.extjs.gxt.ui.client.event.Listener#handleEvent(com.extjs.gxt.ui.client.event.BaseEvent)
	 */
	public void handleEvent(BaseEvent baseEvent) {
		if (baseEvent.getSource().equals(btnOK)) {
			if (field.validate()) {
				buttonOKClicked(field.getValue());
				field.setValue("");
				field.clearInvalid();
				this.hide();
			}
		} else if (baseEvent.getSource().equals(btnCancel)) {
			field.setValue("");
			field.clearInvalid();
			this.hide();

		}
	}

	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		setHeading(dialogHeading);
		this.setSize(380, 140);

		final FieldSet objFieldSetCriteria = new FieldSet();
		objFieldSetCriteria.setHeading(fieldSetHeading);
		objFieldSetCriteria.setStyleAttribute("margin", "5px");
		objFieldSetCriteria.setSize(355, 100);
		objFieldSetCriteria.setLayout(new RowLayout(Orientation.VERTICAL));

		/** line 1 */
		final LayoutContainer line1 = new LayoutContainer(new RowLayout(
				Orientation.HORIZONTAL));
		line1.setSize(355, 35);
		line1.add(label);
		line1.add(new Separator(10));
		line1.add(field);
		line1.add(new Separator(20));

		objFieldSetCriteria.add(line1);

		final LayoutContainer line2 = new LayoutContainer(new RowLayout(
				Orientation.HORIZONTAL));
		line2.setSize(355, 35);
		line2.add(btnOK);
		line2.add(new Separator(10));
		line2.add(btnCancel);
		objFieldSetCriteria.add(line2);

		/** add */
		this.add(objFieldSetCriteria);

		/** adding listener */
		btnOK.addListener(Events.Select, this);
		btnCancel.addListener(Events.Select, this);
	}

	public void setValue(String value) {
		field.setValue(value);
	}
}