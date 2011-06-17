package com.moogu.myweb.client.feature.reserve.management;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.DateTimePropertyEditor;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.HTML;
import com.moogu.myweb.client.RemoteServicePool;
import com.moogu.myweb.client.common.IlmsAsyncCallback;
import com.moogu.myweb.client.common.genericmodeldata.DateModelData;
import com.moogu.myweb.client.common.genericmodeldata.IdNameModelData;
import com.moogu.myweb.client.common.widget.Button;
import com.moogu.myweb.client.common.widget.IdNameComboBox;
import com.moogu.myweb.client.common.widget.dialog.ValidationDialog;
import com.moogu.myweb.client.common.widget.html.ProcessingImage;
import com.moogu.myweb.client.common.widget.misc.Label;
import com.moogu.myweb.client.common.widget.misc.Separator;
import com.moogu.myweb.shared.reserve.ReserveEurEoDBalance;
import com.moogu.myweb.shared.reserve.ReserveEurPeriod;
import com.moogu.myweb.shared.utils.IlmsFormats;
import com.moogu.myweb.shared.utils.IlmsSharedException;
import com.moogu.myweb.shared.utils.IlmsSharedExceptionCodes;
import com.moogu.myweb.shared.utils.Validation;

/**
 * Panel for the Reserve EUR - Management. User will be able to manipulate
 * Period Management, Closed dates for EUR and Re-Publishing EoD Balances
 * 
 * @author i21726
 * 
 */
public class ManagementPanel extends LayoutContainer implements
		Listener<BaseEvent> {

	private final Button newPeriodButton = new Button("New");

	private final Button savePeriodButton = new Button("Save");

	private final Button rePublishingButton = new Button("Re-Publish");

	private final IdNameComboBox periodBox = new IdNameComboBox();

	private DateField startDatePeriod = new DateField();

	private DateField endDatePeriod = new DateField();

	private final NumberField averagePeriod = new NumberField();

	private final IlmsFastYearField yearClosedDates = new IlmsFastYearField() {

		@Override
		public void onSelect(String year) {
			ManagementPanel.this.onSelectYear(year);
		}
	};

	private ListStore<DateModelData> storeClosedDates = new ListStore<DateModelData>();

	private Grid<DateModelData> gridClosedDates;

	private final Button addClosedDate = new Button("New");

	private final Button removeClosedDate = new Button("Remove");

	private final ProcessingImage ilmsProcessingImagePeriodManagement = new ProcessingImage(
			22, 22);

	private final ProcessingImage ilmsProcessingImageClosedDate = new ProcessingImage(
			22, 22);

	private final ProcessingImage ilmsProcessingImageRePublishing = new ProcessingImage(
			22, 22);

	private final DateField rePublishingDate = new DateField();

	private final NumberField rePublishingBalance = new NumberField();

	private final NumberField rePublishingDeposit = new NumberField();

	private final NumberField rePublishingLending = new NumberField();

	public ManagementPanel() {
		super(new RowLayout(Orientation.VERTICAL));

		// details for the buttons. Just new is enabled.
		newPeriodButton.setSize(70, 22);
		newPeriodButton.setEnabled(true);
		savePeriodButton.setSize(70, 22);
		savePeriodButton.setEnabled(false);
		rePublishingButton.setSize(70, 22);
		rePublishingButton.setEnabled(false);

		// details for the period box
		periodBox.setSize(200, 22);
		periodBox.setEditable(false);

		startDatePeriod.getDatePicker().setStartDay(1);
		startDatePeriod.setPropertyEditor(new DateTimePropertyEditor(
				IlmsFormats.DATE_FORMAT));
		endDatePeriod.getDatePicker().setStartDay(1);
		endDatePeriod.setPropertyEditor(new DateTimePropertyEditor(
				IlmsFormats.DATE_FORMAT));

		// initial these fields are not enabled
		startDatePeriod.setEnabled(false);
		endDatePeriod.setEnabled(false);
		averagePeriod.setEnabled(false);
		averagePeriod.setFormat(NumberFormat.getCurrencyFormat("EUR"));

		// set correct size
		yearClosedDates.setSize(40, 22);

		// configuration for the table
		getConfigClosedDateColumn();

		// set details button
		addClosedDate.setSize(70, 22);
		removeClosedDate.setSize(70, 22);
		addClosedDate.setEnabled(false);
		removeClosedDate.setEnabled(false);

		ilmsProcessingImagePeriodManagement.stopProcess();
		ilmsProcessingImageClosedDate.stopProcess();
		ilmsProcessingImageRePublishing.stopProcess();

		// set start date and format
		rePublishingDate.setPropertyEditor(new DateTimePropertyEditor(
				IlmsFormats.DATE_FORMAT));
		rePublishingDate.getDatePicker().setStartDay(1);
		rePublishingDate.addListener(Events.Blur, new Listener<BaseEvent>() {

			public void handleEvent(BaseEvent be) {
				ManagementPanel.this.populateRePublishDate();
			}
		});

		startDatePeriod.setPropertyEditor(new DateTimePropertyEditor(
				IlmsFormats.DATE_FORMAT));
		startDatePeriod.getDatePicker().setStartDay(1);

		endDatePeriod.setPropertyEditor(new DateTimePropertyEditor(
				IlmsFormats.DATE_FORMAT));
		endDatePeriod.getDatePicker().setStartDay(1);

		// fields for the re-publishing
		rePublishingBalance.setFormat(NumberFormat.getCurrencyFormat("EUR"));
		rePublishingDeposit.setFormat(NumberFormat.getCurrencyFormat("EUR"));
		rePublishingLending.setFormat(NumberFormat.getCurrencyFormat("EUR"));
	}

	/**
	 * Add the button the listener for actions when clicked
	 */
	private void addPeriodListeners() {
		newPeriodButton.addListener(Events.Select, this);
		savePeriodButton.addListener(Events.Select, this);
		addClosedDate.addListener(Events.Select, this);
		periodBox.addListener(Events.Select, this);
		removeClosedDate.addListener(Events.Select, new Listener<BaseEvent>() {

			public void handleEvent(BaseEvent event) {
				final Listener<MessageBoxEvent> listener = new Listener<MessageBoxEvent>() {

					public void handleEvent(MessageBoxEvent be) {

						final com.extjs.gxt.ui.client.widget.button.Button btn = be
								.getButtonClicked();

						if (btn.getText().equals("Yes")) {
							// Remove the date
							final List<DateModelData> dates = gridClosedDates
									.getSelectionModel().getSelectedItems();
							if (dates == null || dates.size() == 0) {
								// user didn't selected
								MessageBox.alert("No selection",
										"Please, select date(s) to delete.",
										null);
								return;
							} else {
								ilmsProcessingImageClosedDate
										.setStateProcessing(true);

								// load dates for this year
								RemoteServicePool.getReservetModule()
										.removeClosedDate(
												ManagementPanel.this
														.getDates(dates),
												new IlmsAsyncCallback<Void>() {

													@Override
													protected void onCustomFailure(
															IlmsSharedException e) {
														ilmsProcessingImageClosedDate
																.setStateProcessing(false);
														super.onCustomFailure(e);
													}

													@Override
													protected void onCustomSuccess(
															Void result) {
														for (final DateModelData date : dates) {
															storeClosedDates
																	.remove(date);
														}

														ilmsProcessingImageClosedDate
																.setStateProcessing(false);
													}
												});
							}
						}
					}
				};
				MessageBox
						.confirm(
								"Remove closed dates",
								"Are you sure you want to remove the selected closed date(s)?",
								listener);
			}
		});

		rePublishingButton.addListener(Events.Select,
				new Listener<BaseEvent>() {

					public void handleEvent(BaseEvent event) {
						final Listener<MessageBoxEvent> listener = new Listener<MessageBoxEvent>() {

							public void handleEvent(MessageBoxEvent be) {

								final com.extjs.gxt.ui.client.widget.button.Button btn = be
										.getButtonClicked();

								// republish
								if (btn.getText().equals("Yes")) {
									// validations
									final Validation validation = ManagementPanel.this
											.validate();
									if (validation.hasError()) {
										ValidationDialog.display(validation);
										return;
									}

									ManagementPanel.this.republish();
								}
							}
						};
						MessageBox
								.confirm(
										"IMPORTANT",
										"Are you sure you want to re-publish the selected date",
										listener);
					}
				});
	}

	/**
	 * Clean fields and disable button for the Period Management
	 */
	private void cleanPeriodManagement() {
		periodBox.getStore().removeAll();
		ilmsProcessingImagePeriodManagement.setStateProcessing(true);

		startDatePeriod.clear();
		endDatePeriod.clear();
		averagePeriod.clear();
		savePeriodButton.setEnabled(false);
		startDatePeriod.setEnabled(false);
		endDatePeriod.setEnabled(false);
		averagePeriod.setEnabled(false);

		// load periods
		RemoteServicePool.getReservetModule().getPeriods(
				new IlmsAsyncCallback<List<IdNameModelData>>() {

					@Override
					protected void onCustomFailure(IlmsSharedException e) {
						ilmsProcessingImagePeriodManagement
								.setStateProcessing(false);
						super.onCustomFailure(e);
					}

					@Override
					protected void onCustomSuccess(List<IdNameModelData> result) {
						periodBox.getStore().add(result);
						periodBox.setValue(null);
						ilmsProcessingImagePeriodManagement
								.setStateProcessing(false);
					}
				});
	}

	private void createNewPeriod() {
		ilmsProcessingImagePeriodManagement.setStateProcessing(true);

		// convert the average to Double.
		final Double average = averagePeriod.getValue() != null ? averagePeriod
				.getValue().doubleValue() : null;

		RemoteServicePool.getReservetModule().createNewPeriod(
				startDatePeriod.getValue(), endDatePeriod.getValue(), average,
				new IlmsAsyncCallback<Integer>() {

					@Override
					protected void onCustomFailure(IlmsSharedException e) {
						ilmsProcessingImagePeriodManagement
								.setStateProcessing(false);
						ManagementPanel.this.handleException(e);
					}

					@Override
					protected void onCustomSuccess(Integer id) {
						// add the period in the box
						ManagementPanel.this.cleanPeriodManagement();
						ilmsProcessingImagePeriodManagement
								.setStateProcessing(false);
					}
				});
	}

	private ColumnModel getConfigClosedDateColumn() {
		final List<ColumnConfig> columnConfigUser = new ArrayList<ColumnConfig>();

		// column code
		final ColumnConfig column = new ColumnConfig();
		column.setId(DateModelData.DATE_PROP);
		column.setHeader("Dates");
		column.setWidth(90);
		column.setAlignment(HorizontalAlignment.CENTER);
		column.setDateTimeFormat(DateTimeFormat
				.getFormat(IlmsFormats.DATE_FORMAT));
		columnConfigUser.add(column);

		/** column model for user */
		return new ColumnModel(columnConfigUser);
	}

	private List<Date> getDates(List<DateModelData> datesModel) {
		final List<Date> dates = new ArrayList<Date>();
		if (datesModel != null && datesModel.size() > 0) {
			for (final DateModelData dateModel : datesModel) {
				dates.add(dateModel.getDate());
			}
		}
		return dates;
	}

	public void handleEvent(BaseEvent be) {
		if (be.getSource().equals(newPeriodButton)) {
			savePeriodButton.setEnabled(true);
			startDatePeriod.setEnabled(true);
			endDatePeriod.setEnabled(true);
			averagePeriod.setEnabled(true);

			startDatePeriod.setValue(null);
			endDatePeriod.setValue(null);
			averagePeriod.setValue(null);
			periodBox.setValue(null);

			// add the next start date. Get it from the server.
			setStartDate();
		} else if (be.getSource().equals(savePeriodButton)) {
			// start date is mandatory
			if (startDatePeriod.getValue() == null) {
				MessageBox.alert("Mandatory", "Start Date is mandatory", null);
				return;
			}

			if (endDatePeriod.getValue() == null) {
				MessageBox.alert("Mandatory", "End Date is mandatory", null);
				return;
			}

			if (periodBox.getValue() == null) {
				// if it is a new period
				createNewPeriod();
			} else {
				// if it is a update
				updatePeriod();
			}

		} else if (be.getSource().equals(addClosedDate)) {
			final ClosedDatesDialog dialog = new ClosedDatesDialog() {

				@Override
				public void buttonOKClicked(final Date date) {
					ilmsProcessingImageClosedDate.setStateProcessing(true);

					// load dates for this year
					RemoteServicePool.getReservetModule().addClosedDate(date,
							new IlmsAsyncCallback<Void>() {

								@Override
								protected void onCustomFailure(
										IlmsSharedException e) {
									ilmsProcessingImageClosedDate
											.setStateProcessing(false);
									super.onCustomFailure(e);
								}

								@Override
								protected void onCustomSuccess(Void result) {
									// just add the date if they are from the
									// same year
									final String yearClosedDate = yearClosedDates
											.getValue();
									final DateTimeFormat dateTimeFormat = DateTimeFormat
											.getFormat("yyyy");
									final String year = dateTimeFormat
											.format(date);
									if (yearClosedDate != null
											&& yearClosedDate.equals(year)) {
										storeClosedDates.add(new DateModelData(
												date));
									}

									ilmsProcessingImageClosedDate
											.setStateProcessing(false);
								}
							});
				}
			};
			dialog.show();
		} else if (be.getSource().equals(periodBox)) {
			// load the period information
			final IdNameModelData entity = periodBox.getValue();
			if (entity != null) {
				ilmsProcessingImagePeriodManagement.setStateProcessing(true);

				RemoteServicePool.getReservetModule().getPeriod(entity.getId(),
						new IlmsAsyncCallback<ReserveEurPeriod>() {

							@Override
							protected void onCustomFailure(IlmsSharedException e) {
								ilmsProcessingImagePeriodManagement
										.setStateProcessing(false);
								super.onCustomFailure(e);
							}

							@Override
							protected void onCustomSuccess(
									ReserveEurPeriod result) {
								startDatePeriod.setValue(result.getStartDate());
								endDatePeriod.setValue(result.getEndDate());
								averagePeriod.setValue(result.getAverage());
								savePeriodButton.setEnabled(true);
								startDatePeriod.setEnabled(true);
								endDatePeriod.setEnabled(true);
								averagePeriod.setEnabled(true);
								ilmsProcessingImagePeriodManagement
										.setStateProcessing(false);
							}
						});
			}
		}
	}

	private void handleException(IlmsSharedException e) {
		if (e.getCode().equals(
				IlmsSharedExceptionCodes.END_DATE_BEFORE_START_DATE)) {
			MessageBox.alert("Invalid End Date",
					"The end date should be after the start date", null);
		} else if (e.getCode().equals(
				IlmsSharedExceptionCodes.START_DATE_NOT_AVAILABLE)) {
			MessageBox.alert("Invalid Start Date",
					"This Start Date is alreay used by other period", null);
		} else if (e.getCode().equals(
				IlmsSharedExceptionCodes.START_DATE_ALREADY_IN_ANOTHER_PERIOD)) {
			MessageBox.alert("Invalid Start Date",
					"This Start Date is alreay used in another period", null);
		} else if (e.getCode().equals(
				IlmsSharedExceptionCodes.END_DATE_ALREADY_IN_ANOTHER_PERIOD)) {
			MessageBox.alert("Invalid End Date",
					"This End Date is alreay used in another period", null);
		}
	}

	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);

		// create/modify/set average PERIOD
		preparePeriodFieldSet();

		// set closed dates
		prepareClosedDatesFieldSet();

		// re-publishing
		prepareRePublishingEoDBalancesFieldSet();
	}

	public void onSelection() {
		periodBox.getStore().removeAll();
		ilmsProcessingImagePeriodManagement.setStateProcessing(true);

		// load periods
		RemoteServicePool.getReservetModule().getPeriods(
				new IlmsAsyncCallback<List<IdNameModelData>>() {

					@Override
					protected void onCustomFailure(IlmsSharedException e) {
						ilmsProcessingImagePeriodManagement
								.setStateProcessing(false);
						super.onCustomFailure(e);
					}

					@Override
					protected void onCustomSuccess(List<IdNameModelData> result) {
						periodBox.getStore().add(result);
						ilmsProcessingImagePeriodManagement
								.setStateProcessing(false);
					}
				});
	}

	private void onSelectYear(String year) {
		addClosedDate.setEnabled(true);
		removeClosedDate.setEnabled(true);
		ilmsProcessingImageClosedDate.setStateProcessing(true);
		storeClosedDates.removeAll();

		// load dates for this year
		RemoteServicePool.getReservetModule().getDatesByYear(
				Integer.parseInt(year), new IlmsAsyncCallback<List<Date>>() {

					@Override
					protected void onCustomFailure(IlmsSharedException e) {
						ilmsProcessingImageClosedDate.setStateProcessing(false);
						super.onCustomFailure(e);
					}

					@Override
					protected void onCustomSuccess(List<Date> result) {
						if (result != null && result.size() > 0) {
							for (final Date date : result) {
								storeClosedDates.add(new DateModelData(date));
							}
						}
						ilmsProcessingImageClosedDate.setStateProcessing(false);
					}
				});
	}

	private void populateRePublishDate() {
		// get the date that we should look
		final Date date = rePublishingDate.getValue();

		if (date != null) {
			ilmsProcessingImageRePublishing.setStateProcessing(true);
			// load the Publish
			RemoteServicePool.getReservetModule().getPublish(date,
					new IlmsAsyncCallback<ReserveEurEoDBalance>() {

						@Override
						protected void onCustomFailure(IlmsSharedException e) {
							ilmsProcessingImageRePublishing
									.setStateProcessing(false);
							super.onCustomFailure(e);
						}

						@Override
						protected void onCustomSuccess(
								ReserveEurEoDBalance result) {
							ilmsProcessingImageRePublishing
									.setStateProcessing(false);
							if (result != null) {
								// fill EoD Balance
								rePublishingBalance.setValue(result
										.getBalance());
								// fill Deposit
								rePublishingDeposit.setValue(result
										.getDeposit());
								// fill Marginal Lending
								rePublishingLending.setValue(result
										.getMarginalLending());
								// enable Re-Publish
								rePublishingButton.setEnabled(true);
							} else {
								rePublishingBalance.setValue(null);
								rePublishingDeposit.setValue(null);
								rePublishingLending.setValue(null);
								rePublishingButton.setEnabled(false);
							}
						}
					});
		}
	}

	private void prepareClosedDatesFieldSet() {
		final FieldSet closedDateFieldSet = new FieldSet();
		closedDateFieldSet.setLayout(new RowLayout(Orientation.VERTICAL));
		closedDateFieldSet.setStyleAttribute("margin", "5px");
		closedDateFieldSet.setHeading("Closed dates for EUR");

		// add screen details for the Management Prepare
		final HorizontalPanel panel = new HorizontalPanel();
		panel.add(new Label("Year:"));
		panel.add(new Separator(10));
		panel.add(yearClosedDates);
		panel.add(new Separator(10));

		// details for the table with dates and buttons
		storeClosedDates.sort(DateModelData.DATE_PROP, SortDir.ASC);
		gridClosedDates = new Grid<DateModelData>(storeClosedDates,
				getConfigClosedDateColumn());
		gridClosedDates.setBorders(true);
		gridClosedDates.setSize(110, 160);
		panel.add(gridClosedDates);
		panel.add(new Separator(10));

		final VerticalPanel panelButtons = new VerticalPanel();
		panelButtons.setHorizontalAlign(HorizontalAlignment.CENTER);
		panelButtons.setVerticalAlign(VerticalAlignment.MIDDLE);
		panelButtons.add(addClosedDate);
		panelButtons.add(new Separator(10));
		panelButtons.add(removeClosedDate);
		panelButtons.add(new HTML("&nbsp;"));

		// add the buttons
		panel.add(panelButtons);
		panel.add(new Separator(10));
		panel.add(ilmsProcessingImageClosedDate.getComponent());

		// add everything to the field set and after to the screen
		closedDateFieldSet.add(panel);
		this.add(closedDateFieldSet, new RowData(1234, 210));
	}

	private void preparePeriodFieldSet() {
		final FieldSet periodManagementFieldSet = new FieldSet();
		periodManagementFieldSet.setLayout(new RowLayout(Orientation.VERTICAL));
		periodManagementFieldSet.setStyleAttribute("margin", "5px");
		periodManagementFieldSet.setHeading("Period Management");

		// add screen details for the Management Prepare
		HorizontalPanel panel = new HorizontalPanel();
		panel.add(new Label("Period:"));
		panel.add(new Separator(10));
		panel.add(periodBox);
		panel.add(new Separator(10));
		panel.add(newPeriodButton);
		panel.add(new Separator(10));
		panel.add(savePeriodButton);
		panel.add(new Separator(10));
		panel.add(ilmsProcessingImagePeriodManagement.getComponent());
		periodManagementFieldSet.add(panel);

		// jump a line
		periodManagementFieldSet.add(new HTML("&nbsp;"));

		panel = new HorizontalPanel();
		panel.add(new Label("Start Date:"));
		panel.add(new Separator(10));
		panel.add(startDatePeriod);
		panel.add(new Separator(10));
		panel.add(new Label("End Date:"));
		panel.add(new Separator(10));
		panel.add(endDatePeriod);
		panel.add(new Separator(10));
		panel.add(new Label("Average:"));
		panel.add(new Separator(10));
		panel.add(averagePeriod);
		periodManagementFieldSet.add(panel);

		// add to screen
		this.add(periodManagementFieldSet, new RowData(1234, 110));

		// listener
		addPeriodListeners();
	}

	private void prepareRePublishingEoDBalancesFieldSet() {
		final FieldSet rePublishingEoDBalancesFieldSet = new FieldSet();
		rePublishingEoDBalancesFieldSet.setLayout(new RowLayout(
				Orientation.VERTICAL));
		rePublishingEoDBalancesFieldSet.setStyleAttribute("margin", "5px");
		rePublishingEoDBalancesFieldSet
				.setHeading("Re-Publishing EoD Balances");

		// add screen details for the Management Prepare
		HorizontalPanel panel = new HorizontalPanel();
		panel.add(new Label("Date:"));
		panel.add(new Separator(10));
		panel.add(rePublishingDate);
		panel.add(new Separator(10));
		panel.add(rePublishingButton);
		panel.add(new Separator(10));
		panel.add(ilmsProcessingImageRePublishing.getComponent());
		rePublishingEoDBalancesFieldSet.add(panel);

		// jump a line
		rePublishingEoDBalancesFieldSet.add(new HTML("&nbsp;"));

		panel = new HorizontalPanel();
		panel.add(new Label("EoD Balance:"));
		panel.add(new Separator(10));
		panel.add(rePublishingBalance);
		panel.add(new Separator(10));
		panel.add(new Label("Deposit:"));
		panel.add(new Separator(10));
		panel.add(rePublishingDeposit);
		panel.add(new Separator(10));
		panel.add(new Label("Marginal Lending:"));
		panel.add(new Separator(10));
		panel.add(rePublishingLending);

		rePublishingEoDBalancesFieldSet.add(panel);

		// add to screen
		this.add(rePublishingEoDBalancesFieldSet, new RowData(1234, 110));
	}

	private void republish() {
		final Date date = rePublishingDate.getValue();

		ilmsProcessingImageRePublishing.setStateProcessing(true);
		// load the Publish
		RemoteServicePool.getReservetModule().republish(
				date,
				rePublishingBalance.getValue().doubleValue(),
				rePublishingDeposit.getValue() != null ? rePublishingDeposit
						.getValue().doubleValue() : null,
				rePublishingLending.getValue() != null ? rePublishingLending
						.getValue().doubleValue() : null,
				new IlmsAsyncCallback<Void>() {

					@Override
					protected void onCustomFailure(IlmsSharedException e) {
						ilmsProcessingImageRePublishing
								.setStateProcessing(false);
						super.onCustomFailure(e);
					}

					@Override
					protected void onCustomSuccess(Void result) {
						ilmsProcessingImageRePublishing
								.setStateProcessing(false);
						rePublishingDate.setValue(null);
						rePublishingBalance.setValue(null);
						rePublishingDeposit.setValue(null);
						rePublishingLending.setValue(null);
						rePublishingButton.setEnabled(false);
						MessageBox.info("Sucessed", "EoD Balance re-published",
								null);
					}
				});
	}

	private void setStartDate() {
		ilmsProcessingImagePeriodManagement.setStateProcessing(true);

		// load dates for this year
		RemoteServicePool.getReservetModule().getNextStartDate(
				new IlmsAsyncCallback<Date>() {

					@Override
					protected void onCustomFailure(IlmsSharedException e) {
						ilmsProcessingImagePeriodManagement
								.setStateProcessing(false);
						super.onCustomFailure(e);
					}

					@Override
					protected void onCustomSuccess(Date result) {
						// set the correct date
						startDatePeriod.setValue(result);
						ilmsProcessingImagePeriodManagement
								.setStateProcessing(false);
					}
				});
	}

	private void updatePeriod() {
		ilmsProcessingImagePeriodManagement.setStateProcessing(true);

		// id of period
		final IdNameModelData data = periodBox.getValue();
		if (data == null) {
			if (endDatePeriod.getValue() == null) {
				MessageBox.alert("Error",
						"Error to identify the period to update", null);
				return;
			}
		}

		// convert the average to Double.
		final Double average = averagePeriod.getValue() != null ? averagePeriod
				.getValue().doubleValue() : null;

		RemoteServicePool.getReservetModule().updatePeriod(data.getId(),
				startDatePeriod.getValue(), endDatePeriod.getValue(), average,
				new IlmsAsyncCallback<Boolean>() {

					@Override
					protected void onCustomFailure(IlmsSharedException e) {
						ilmsProcessingImagePeriodManagement
								.setStateProcessing(false);
						ManagementPanel.this.handleException(e);
					}

					@Override
					protected void onCustomSuccess(final Boolean isThereWarning) {
						// add the period in the box
						ManagementPanel.this.cleanPeriodManagement();
						ilmsProcessingImagePeriodManagement
								.setStateProcessing(false);

						if (isThereWarning) {
							MessageBox
									.alert("Warning",
											"Modification of the period requires manual adjustment(s) to other periods",
											null);
						}
					}
				});
	}

	private Validation validate() {
		final Validation validation = new Validation();
		validation.addErrorIf("Balance is mandatory.",
				rePublishingBalance.getValue() == null);
		validation.addErrorIf(
				"You must enter Deposit OR Marginal Lending. Not both.",
				rePublishingDeposit.getValue() != null
						&& rePublishingLending.getValue() != null);

		return validation;
	}
}
