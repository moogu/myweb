package com.moogu.myweb.client.feature.reserve.overview;

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
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Element;
import com.moogu.myweb.client.common.widget.Button;
import com.moogu.myweb.client.common.widget.dialog.ValidationDialog;
import com.moogu.myweb.client.common.widget.misc.Label;
import com.moogu.myweb.client.common.widget.misc.Separator;
import com.moogu.myweb.shared.reserve.ReserveEurEoDBalance;
import com.moogu.myweb.shared.utils.IlmsFormats;
import com.moogu.myweb.shared.utils.Validation;

public abstract class OverviewDialog extends Window implements
		Listener<BaseEvent> {

	private final DateField dateStart = new DateField();

	private final DateField dateFinish = new DateField();

	private final NumberField eodBalance = new NumberField();

	private final NumberField deposit = new NumberField();

	private final NumberField marginal = new NumberField();

	private final Button okayButton = new Button("OK");

	private final Button cancelButton = new Button("Cancel");

	private final ReserveEurEoDBalance balance;

	private final Date latestDate;

	public OverviewDialog(ReserveEurEoDBalance balance, Date latestDate) {
		super();
		configureTextFields();
		okayButton.setSize(70, 22);
		cancelButton.setSize(70, 22);

		// adding listener to buttons
		okayButton.addListener(Events.Select, this);
		cancelButton.addListener(Events.Select, this);

		this.balance = balance;
		this.latestDate = latestDate;
	}

	/** when user clicks the button OK trigger action */
	public abstract void buttonOKClicked(Date dateStart, Date dateFinish,
			Number balance, Number deposit, Number marginal);

	private void cleanAll() {
		dateStart.setValue(null);
		dateFinish.setValue(null);
		eodBalance.setValue(null);
		deposit.setValue(null);
		marginal.setValue(null);
	}

	private void configureTextFields() {
		dateStart.setWidth(200);
		dateStart.setEnabled(false);

		dateFinish.setWidth(200);
		eodBalance.setSize(100, 22);
		eodBalance.setFormat(NumberFormat.getCurrencyFormat("EUR"));
		deposit.setSize(100, 22);
		deposit.setFormat(NumberFormat.getCurrencyFormat("EUR"));
		marginal.setSize(100, 22);
		marginal.setFormat(NumberFormat.getCurrencyFormat("EUR"));

		dateStart.setPropertyEditor(new DateTimePropertyEditor(
				IlmsFormats.DATE_FORMAT));
		dateStart.getDatePicker().setStartDay(1);

		dateFinish.setPropertyEditor(new DateTimePropertyEditor(
				IlmsFormats.DATE_FORMAT));
		dateFinish.getDatePicker().setStartDay(1);

		eodBalance.setAllowBlank(false);

	}

	public void handleEvent(BaseEvent baseEvent) {
		if (baseEvent.getSource().equals(okayButton)) {
			// validations
			final Validation validation = validate();
			if (validation.hasError()) {
				ValidationDialog.display(validation);
				return;
			}

			buttonOKClicked(dateStart.getValue(), dateFinish.getValue(),
					eodBalance.getValue(), deposit.getValue(),
					marginal.getValue());
			cleanAll();
			this.hide();

		} else if (baseEvent.getSource().equals(cancelButton)) {
			cleanAll();
			this.hide();
		}
	}

	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		setHeading("Add");
		this.setSize(625, 179);

		final FieldSet objFieldSetCriteria = new FieldSet();
		objFieldSetCriteria.setHeading("Add Details");
		objFieldSetCriteria.setStyleAttribute("margin", "5px");
		objFieldSetCriteria.setSize(600, 130);
		objFieldSetCriteria.setLayout(new RowLayout(Orientation.VERTICAL));

		// Line 1
		final LayoutContainer line1 = new LayoutContainer(new RowLayout(
				Orientation.HORIZONTAL));
		line1.setSize(600, 35);
		line1.add(new Label("Date:"));
		line1.add(new Separator(5));
		line1.add(dateStart);
		line1.add(new Separator(27));
		line1.add(new Label("Until:"));
		line1.add(new Separator(5));
		line1.add(dateFinish);
		objFieldSetCriteria.add(line1);

		// Line 2
		final LayoutContainer line2 = new LayoutContainer(new RowLayout(
				Orientation.HORIZONTAL));
		line2.setSize(600, 35);
		line2.add(new Label("EoD Balance:"));
		line2.add(new Separator(5));
		line2.add(eodBalance);
		line2.add(new Separator(10));
		line2.add(new Label("Deposit:"));
		line2.add(new Separator(5));
		line2.add(deposit);
		line2.add(new Separator(10));
		line2.add(new Label("Marginal Lending:"));
		line2.add(new Separator(5));
		line2.add(marginal);
		objFieldSetCriteria.add(line2);

		// Line 3
		final LayoutContainer line3 = new LayoutContainer(new RowLayout(
				Orientation.HORIZONTAL));
		line3.setSize(600, 35);
		line3.add(okayButton);
		line3.add(new Separator(10));
		line3.add(cancelButton);
		objFieldSetCriteria.add(line3);

		this.add(objFieldSetCriteria);

		// set the date value
		if (balance.getDatePublish() != null) {
			dateStart.setValue(balance.getDatePublish());
		}
	}

	public Validation validate() {
		final Validation validation = new Validation();
		validation.addErrorIf("Balance is mandatory.",
				eodBalance.getValue() == null);
		validation.addErrorIf(
				"You must enter Deposit OR Marginal Lending. Not both.",
				deposit.getValue() != null && marginal.getValue() != null);
		validation.addErrorIf("Date Until should be after Initial Date.",
				dateFinish.getValue() != null
						&& dateFinish.getValue().getTime() < dateStart
								.getValue().getTime());
		validation.addErrorIf(
				"End date is after the end period date",
				dateFinish.getValue() != null
						&& dateFinish.getValue().after(latestDate));

		return validation;
	}
}