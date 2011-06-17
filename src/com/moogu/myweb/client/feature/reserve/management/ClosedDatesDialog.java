package com.moogu.myweb.client.feature.reserve.management;

import java.util.Date;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.DateTimePropertyEditor;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.google.gwt.user.client.Element;
import com.moogu.myweb.client.common.widget.Button;
import com.moogu.myweb.client.common.widget.misc.Label;
import com.moogu.myweb.client.common.widget.misc.Separator;
import com.moogu.myweb.shared.utils.IlmsFormats;

/**
 * Let user to add a date to EUR to be set as CLOSED.
 * 
 * @author i21726 - (Patrick Santana)
 */
public abstract class ClosedDatesDialog extends Window implements
		Listener<BaseEvent> {

	private Button okayButton = new Button("OK");

	private Button cancelButton = new Button("Cancel");

	private DateField newClosedDateDataField = new DateField();

	public ClosedDatesDialog() {
		super();
		setModal(true);
		okayButton.setSize(70, 22);
		cancelButton.setSize(70, 22);
		setPosition(440, 365);

		// set format and start date
		newClosedDateDataField.setPropertyEditor(new DateTimePropertyEditor(
				IlmsFormats.DATE_FORMAT));
		newClosedDateDataField.getDatePicker().setStartDay(1);
	}

	/** when user clicks the button OK trigger action */
	public abstract void buttonOKClicked(Date date);

	/**
	 * @see com.extjs.gxt.ui.client.event.Listener#handleEvent(com.extjs.gxt.ui.client.event.BaseEvent)
	 */
	public void handleEvent(BaseEvent baseEvent) {
		if (baseEvent.getSource().equals(okayButton)) {
			if (newClosedDateDataField.validate()) {
				buttonOKClicked(newClosedDateDataField.getValue());
				newClosedDateDataField.setValue(null);
				newClosedDateDataField.clearInvalid();
				this.hide();
			}
		} else if (baseEvent.getSource().equals(cancelButton)) {
			newClosedDateDataField.setValue(null);
			newClosedDateDataField.clearInvalid();
			this.hide();
		}
	}

	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		setHeading("Add new closed date for EUR");
		this.setSize(380, 140);

		final FieldSet objFieldSetCriteria = new FieldSet();
		objFieldSetCriteria.setHeading("Date");
		objFieldSetCriteria.setStyleAttribute("margin", "5px");
		objFieldSetCriteria.setSize(355, 100);
		objFieldSetCriteria.setLayout(new RowLayout(Orientation.VERTICAL));

		// line 1
		final LayoutContainer line1 = new LayoutContainer(new RowLayout(
				Orientation.HORIZONTAL));
		line1.setSize(355, 35);
		line1.add(new Label("New date:"));
		line1.add(new Separator(10));
		line1.add(newClosedDateDataField);
		line1.add(new Separator(20));
		objFieldSetCriteria.add(line1);

		// line 2
		final LayoutContainer line2 = new LayoutContainer(new RowLayout(
				Orientation.HORIZONTAL));
		line2.setSize(355, 35);
		line2.add(okayButton);
		line2.add(new Separator(10));
		line2.add(cancelButton);
		objFieldSetCriteria.add(line2);

		// add to main panel
		this.add(objFieldSetCriteria);

		// adding listener to buttons
		okayButton.addListener(Events.Select, this);
		cancelButton.addListener(Events.Select, this);
	}
}