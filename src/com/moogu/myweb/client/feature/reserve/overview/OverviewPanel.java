package com.moogu.myweb.client.feature.reserve.overview;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.extjs.gxt.charts.client.Chart;
import com.extjs.gxt.charts.client.event.ChartEvent;
import com.extjs.gxt.charts.client.event.ChartListener;
import com.extjs.gxt.charts.client.model.ChartModel;
import com.extjs.gxt.charts.client.model.LineDataProvider;
import com.extjs.gxt.charts.client.model.Text;
import com.extjs.gxt.charts.client.model.ToolTip;
import com.extjs.gxt.charts.client.model.ToolTip.MouseStyle;
import com.extjs.gxt.charts.client.model.axis.XAxis;
import com.extjs.gxt.charts.client.model.axis.XAxis.XLabels;
import com.extjs.gxt.charts.client.model.axis.YAxis;
import com.extjs.gxt.charts.client.model.charts.BarChart;
import com.extjs.gxt.charts.client.model.charts.BarChart.BarStyle;
import com.extjs.gxt.charts.client.model.charts.LineChart;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.HTML;
import com.moogu.myweb.client.ClientTool;
import com.moogu.myweb.client.MyWebEntryPoint;
import com.moogu.myweb.client.RemoteServicePool;
import com.moogu.myweb.client.common.IlmsAsyncCallback;
import com.moogu.myweb.client.common.genericmodeldata.IdNameModelData;
import com.moogu.myweb.client.common.genericmodeldata.NameModelData;
import com.moogu.myweb.client.common.widget.Button;
import com.moogu.myweb.client.common.widget.IdNameComboBox;
import com.moogu.myweb.client.common.widget.html.ProcessingImage;
import com.moogu.myweb.client.common.widget.misc.Label;
import com.moogu.myweb.client.common.widget.misc.Separator;
import com.moogu.myweb.shared.reserve.ReserveEurEoDBalance;
import com.moogu.myweb.shared.reserve.ReserveEurGraph;
import com.moogu.myweb.shared.reserve.ReserveEurPeriod;
import com.moogu.myweb.shared.reserve.ReserveEurPublish;
import com.moogu.myweb.shared.utils.IlmsFormats;
import com.moogu.myweb.shared.utils.IlmsSharedException;

public class OverviewPanel extends LayoutContainer implements
		Listener<BaseEvent> {

	public static final String SCREEN = "overviewReserveEur";

	private final IdNameComboBox periodBox = new IdNameComboBox();

	private ListStore<OverviewModelData> store = new ListStore<OverviewModelData>();

	private Grid<OverviewModelData> grid;

	private final Html excelLink = new Html();

	private final Label mandatoryAverage = new Label();

	private final Label depositOrMaginalLendingValue = new Label();

	private final Label depositOrMaginalLending = new Label();

	private Button publishButton = new Button("Publish");

	private final ProcessingImage ilmsProcessingPeriod = new ProcessingImage(
			22, 22);

	private final String url = GWT.getModuleBaseURL().replace(
			GWT.getModuleName() + "/", "")
			+ "res/chart/open-flash-chart.swf";

	private final Chart chart = new Chart(url);

	private ChartListener listener = new ChartListener() {

		@Override
		public void chartClick(ChartEvent ce) {
			final int row = ce.getChartConfig().getValues()
					.indexOf(ce.getDataType());

			// get the X for it
			final XLabels labels = ce.getChartModel().getXAxis().getLabels();
			final Object[] l = labels.getLabels().toArray();
			final Label label = (Label) l[row];

			for (int i = 0; i < store.getCount(); i++) {
				final OverviewModelData data = store.getAt(i);
				final String date = data
						.get(OverviewModelData.DAY_MONTH_AXIS_PROPERTY);
				if (date.equals(label.getText())) {
					// select this in the store
					grid.getSelectionModel().select(data, false);
					OverviewPanel.this.actionDetailsBalance(data);
				}
			}
		}
	};

	public OverviewPanel() {
		super(new RowLayout(Orientation.VERTICAL));

		// details for the period box
		periodBox.setSize(200, 22);
		periodBox.setEditable(false);
		publishButton.setSize(70, 22);
		publishButton.setEnabled(false);

		ilmsProcessingPeriod.stopProcess();

		chart.setBorders(true);
		chart.setSize(835, 559);
		chart.setChartModel(getBarChart());
	}

	private void actionDetailsBalance(final OverviewModelData data) {
		if (data.get(OverviewModelData.DEPOSIT_PROPERTY) != null) {
			depositOrMaginalLending.setText("Deposit: ");
			depositOrMaginalLendingValue.setText(ClientTool
					.getBlueBoldColorHTML(ClientTool.EURO_HTML + " "
							+ data.get(OverviewModelData.DEPOSIT_PROPERTY)));
		} else if (data.get(OverviewModelData.MARGINAL_LENDING_PROPERTY) != null) {
			depositOrMaginalLending.setText("Marginal Lending: ");
			depositOrMaginalLendingValue
					.setText(ClientTool.getBlueBoldColorHTML(ClientTool.EURO_HTML
							+ " "
							+ data.get(OverviewModelData.MARGINAL_LENDING_PROPERTY)));
		} else {
			depositOrMaginalLending.setText("");
			depositOrMaginalLendingValue.setText("");
		}
	}

	private void actionSelectPeriod() {
		// load the period information
		final IdNameModelData entity = periodBox.getValue();
		if (entity != null) {
			cleanGraph();

			// get the period information
			getPeriodInformation(entity.getId());

			// get the balances specific for the table
			getEoDBalances(entity.getId());
		}
	}

	private void addListeners() {
		periodBox.addListener(Events.Select, this);
		publishButton.addListener(Events.Select, this);
	}

	private void cleanGraph() {
		// clean graph
		chart.getChartModel().getChartConfigs().clear();
		final BarChart bchart = new BarChart(BarStyle.GLASS);
		bchart.setTooltip("EUR #val#");
		chart.getChartModel().setTitle(
				new Text("Realized Reserve",
						"font-size: 14px; font-family: Verdana;"));
		chart.getChartModel().addChartConfig(bchart);
		final YAxis ya = new YAxis();
		ya.setMax(10);
		chart.getChartModel().setYAxis(ya);

		final XAxis xa = new XAxis();
		xa.setMax(10);
		chart.getChartModel().setXAxis(xa);
		chart.refresh();
	}

	private ChartModel getBarChart() {
		final ChartModel cm = new ChartModel("Realized Reserve",
				"font-size: 14px; font-family: Verdana;");
		cm.setBackgroundColour("-1");
		final BarChart bchart = new BarChart(BarStyle.GLASS);
		bchart.setTooltip("$#val#");
		cm.addChartConfig(bchart);

		return cm;
	}

	private ColumnModel getConfigPublishingColumns() {
		final List<ColumnConfig> columnConfigUser = new ArrayList<ColumnConfig>();

		// Special renderer used by the grid for Depositor or Lending
		final GridCellRenderer<OverviewModelData> rendererDepositOrLending = new GridCellRenderer<OverviewModelData>() {

			public Object render(OverviewModelData model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<OverviewModelData> store,
					Grid<OverviewModelData> grid) {
				if (model.get(OverviewModelData.DEPOSIT_PROPERTY) != null) {
					return "<center><img src='./res/images/ReserveEurDeposit.png'></img></center>";
				} else if (model
						.get(OverviewModelData.MARGINAL_LENDING_PROPERTY) != null) {
					return "<center><img src='./res/images/ReserveEurMarginal.png'></img></center>";
				} else {
					return "<center></center>";
				}
			}
		};

		// Special renderer used by the grid when the value is not published yet
		final GridCellRenderer<OverviewModelData> rendererNotYetPublish = new GridCellRenderer<OverviewModelData>() {

			private Object formatNumber(Double value, Boolean changed) {
				// default format for EUR
				final NumberFormat format = NumberFormat
						.getCurrencyFormat("EUR");

				// we check if the value exist and it was changed.
				if (value != null && changed) {
					// return will be in Blue color
					return ClientTool.getBlueBoldColorHTML(format.format(value
							.doubleValue()));
				} else if (value != null) {
					return format.format(value.doubleValue());
				} else {
					return null;
				}
			}

			public Object render(OverviewModelData model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<OverviewModelData> store,
					Grid<OverviewModelData> grid) {

				// if the balance is not publish, let give BLUE color to Date,
				// Average to realize and EoDBalance
				final Boolean changed = model
						.get(OverviewModelData.MODEL_DATA_CHANGED_PROPERTY);

				if (OverviewModelData.DATE_PUBLISH_PROPERTY.equals(property)) {
					final DateTimeFormat formatDate = DateTimeFormat
							.getFormat(IlmsFormats.DATE_FORMAT);
					final Date date = model
							.get(OverviewModelData.DATE_PUBLISH_PROPERTY);
					if (date != null && changed) {
						return ClientTool.getBlueBoldColorHTML(formatDate
								.format(date));
					} else if (date != null) {
						return formatDate.format(date);
					}
				} else if (OverviewModelData.AVERAGE_PROPERTY.equals(property)) {
					return formatNumber(
							(Double) model
									.get(OverviewModelData.AVERAGE_PROPERTY),
							changed);
				} else if (OverviewModelData.BALANCE_PROPERTY.equals(property)) {
					return formatNumber(
							(Double) model
									.get(OverviewModelData.BALANCE_PROPERTY),
							changed);
				}
				return null;
			}
		};

		ColumnConfig column = new ColumnConfig();

		column.setId("id");
		column.setRenderer(rendererDepositOrLending);
		column.setWidth(25);
		column.setAlignment(HorizontalAlignment.CENTER);
		columnConfigUser.add(column);

		column = new ColumnConfig();
		column.setId(OverviewModelData.DATE_PUBLISH_PROPERTY);
		column.setHeader("Date");
		column.setRenderer(rendererNotYetPublish);
		column.setWidth(80);
		column.setAlignment(HorizontalAlignment.CENTER);
		column.setSortable(false);
		columnConfigUser.add(column);

		column = new ColumnConfig();
		column.setId(OverviewModelData.AVERAGE_PROPERTY);
		column.setHeader("Average to realize");
		column.setRenderer(rendererNotYetPublish);
		column.setWidth(110);
		column.setAlignment(HorizontalAlignment.RIGHT);
		column.setSortable(false);
		columnConfigUser.add(column);

		column = new ColumnConfig();
		column.setId(OverviewModelData.BALANCE_PROPERTY);
		column.setHeader("EoD Balance");
		column.setRenderer(rendererNotYetPublish);
		column.setWidth(110);
		column.setAlignment(HorizontalAlignment.RIGHT);
		column.setSortable(false);
		columnConfigUser.add(column);

		return new ColumnModel(columnConfigUser);
	}

	protected void getEoDBalances(final Integer periodId) {
		ilmsProcessingPeriod.setStateProcessing(true);
		RemoteServicePool.getReservetModule().getEoDBalances(periodId,
				new IlmsAsyncCallback<List<ReserveEurEoDBalance>>() {

					@Override
					protected void onCustomFailure(IlmsSharedException e) {
						super.onCustomFailure(e);
						ilmsProcessingPeriod.setStateProcessing(false);
					}

					@Override
					protected void onCustomSuccess(
							List<ReserveEurEoDBalance> result) {
						store.removeAll();
						store.add(OverviewModelData.createFromList(result));
						ilmsProcessingPeriod.setStateProcessing(false);
						if (MyWebEntryPoint.isUserRole(MyWebEntryPoint.ROLE_2)
								|| MyWebEntryPoint
										.isUserRole(MyWebEntryPoint.ROLE_3)) {
							publishButton.setEnabled(true);
						} else {
							publishButton.setEnabled(false);
						}

						OverviewPanel.this.updateChart();
						OverviewPanel.this.updateAndDisplayExportLink(true);
					}
				});
	}

	protected void getPeriodInformation(final Integer periodId) {
		RemoteServicePool.getReservetModule().getPeriod(periodId,
				new IlmsAsyncCallback<ReserveEurPeriod>() {

					@Override
					protected void onCustomFailure(IlmsSharedException e) {
						super.onCustomFailure(e);
					}

					@Override
					protected void onCustomSuccess(ReserveEurPeriod result) {
						mandatoryAverage.setText(ClientTool
								.getBlueBoldColorHTML(ClientTool.EURO_HTML
										+ " " + result.getAverage()));
					}
				});
	}

	public void handleEvent(BaseEvent be) {

		if (be.getSource().equals(periodBox)) {
			actionSelectPeriod();
		} else if (be.getSource().equals(publishButton)) {
			// load the period information
			final IdNameModelData entity = periodBox.getValue();
			if (entity != null) {
				ilmsProcessingPeriod.setStateProcessing(true);

				RemoteServicePool.getReservetModule().publishEoDBalances(
						entity.getId(),
						OverviewModelData.getPojos(store.getModels()),
						new IlmsAsyncCallback<ReserveEurPublish>() {

							@Override
							protected void onCustomFailure(IlmsSharedException e) {
								super.onCustomFailure(e);
								ilmsProcessingPeriod.setStateProcessing(false);
							}

							@Override
							protected void onCustomSuccess(
									ReserveEurPublish result) {
								store.removeAll();
								store.add(OverviewModelData
										.createFromList(result.getBalances()));
								OverviewPanel.this.updateChart();
								ilmsProcessingPeriod.setStateProcessing(false);
							}
						});
			}
		}
	}

	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);

		preparePeriodFieldSet();
		prepareDataFieldSet();

		// listener
		addListeners();
	}

	public void onSelection() {
		periodBox.getStore().removeAll();

		// load periods
		RemoteServicePool.getReservetModule().getPeriods(
				new IlmsAsyncCallback<List<IdNameModelData>>() {

					@Override
					protected void onCustomFailure(IlmsSharedException e) {
						ilmsProcessingPeriod.setStateProcessing(false);
						super.onCustomFailure(e);
					}

					@Override
					protected void onCustomSuccess(List<IdNameModelData> result) {
						OverviewPanel.this.cleanGraph();

						store.removeAll();
						mandatoryAverage.setText(null);
						periodBox.setValue(null);
						periodBox.getStore().add(result);
						depositOrMaginalLending.setText("");
						depositOrMaginalLendingValue.setText("");
						publishButton.setEnabled(false);
						OverviewPanel.this.updateAndDisplayExportLink(false);

						// load the actual period
						RemoteServicePool.getReservetModule().getActualPeriod(
								new IlmsAsyncCallback<Integer>() {

									@Override
									protected void onCustomFailure(
											IlmsSharedException e) {
										ilmsProcessingPeriod
												.setStateProcessing(false);
										super.onCustomFailure(e);
									}

									@Override
									protected void onCustomSuccess(
											Integer result) {
										if (result != null) {
											// select the actual period
											final ListStore<IdNameModelData> ids = periodBox
													.getStore();
											if (ids != null
													&& ids.getCount() > 0) {
												for (int i = 0; i < ids
														.getCount(); i++) {
													final IdNameModelData data = ids
															.getAt(i);
													if (data.getId().equals(
															result)) {
														periodBox
																.setValue(data);
														// trigger actions
														periodBox
																.fireEvent(Events.Select);
													}
												}
											}
										}
										ilmsProcessingPeriod
												.setStateProcessing(false);
									}
								});

					}
				});
	}

	private void prepareDataFieldSet() {
		final FieldSet publishingFieldSet = new FieldSet();
		publishingFieldSet.setLayout(new RowLayout(Orientation.VERTICAL));
		publishingFieldSet.setStyleAttribute("margin", "5px");
		publishingFieldSet.setHeading("Publishing EoD Balances");

		// details for the table
		grid = new Grid<OverviewModelData>(store, getConfigPublishingColumns());
		grid.setBorders(true);
		grid.setSize(350, 558);
		grid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		grid.addListener(Events.RowDoubleClick,
				new Listener<GridEvent<OverviewModelData>>() {

					public void handleEvent(GridEvent<OverviewModelData> be) {
						final OverviewModelData modelData = be.getModel();
						final IdNameModelData entity = periodBox.getValue();
						final ReserveEurEoDBalance balance = (ReserveEurEoDBalance) modelData
								.get(OverviewModelData.ORIGINAL_ENTITY_PROPERTY);

						// get the latest date. User should not publish balances
						// after the end of the period
						final Date latestDate = store.getAt(
								store.getCount() - 1).get(
								OverviewModelData.DATE_PUBLISH_PROPERTY);

						// remove selection from others
						final OverviewDialog dialog = new OverviewDialog(
								balance, latestDate) {

							@Override
							public void buttonOKClicked(Date dateStart,
									Date dateFinish, Number balance,
									Number deposit, Number marginal) {
								// this should just temporary create the
								// balance. It will not PUBLISH.
								ilmsProcessingPeriod.setStateProcessing(true);
								RemoteServicePool
										.getReservetModule()
										.getSimulateEoDBalances(
												entity.getId(),
												dateStart,
												dateFinish,
												balance,
												deposit,
												marginal,
												OverviewModelData
														.getPojos(store
																.getModels()),
												new IlmsAsyncCallback<List<ReserveEurEoDBalance>>() {

													@Override
													protected void onCustomFailure(
															IlmsSharedException e) {
														super.onCustomFailure(e);
														ilmsProcessingPeriod
																.setStateProcessing(false);
													}

													@Override
													protected void onCustomSuccess(
															List<ReserveEurEoDBalance> result) {
														store.removeAll();
														store.add(OverviewModelData
																.createFromList(result));
														ilmsProcessingPeriod
																.setStateProcessing(false);
														OverviewPanel.this
																.updateChart();
													}
												});
							}
						};
						if (!balance.isReadOnly()) {
							dialog.setModal(true);
							dialog.setPosition(350, 350);
							dialog.show();
						} else {
							// it is a read only balance.
							MessageBox
									.alert("Read only",
											"This balance is read only. It has been already published.",
											null);
							return;
						}
					}
				});
		grid.addListener(Events.RowClick,
				new Listener<GridEvent<OverviewModelData>>() {

					public void handleEvent(GridEvent<OverviewModelData> be) {
						OverviewPanel.this.actionDetailsBalance(be.getModel());
					}
				});

		HorizontalPanel panel = new HorizontalPanel();
		panel.add(grid);
		panel.add(new Separator(10));
		panel.add(chart);

		publishingFieldSet.add(panel);

		// jump a line
		publishingFieldSet.add(new HTML("&nbsp;"));
		panel = new HorizontalPanel();
		panel.add(publishButton);
		panel.add(new Separator(10));
		panel.add(depositOrMaginalLending);
		panel.add(new Separator(5));
		panel.add(depositOrMaginalLendingValue);

		publishingFieldSet.add(panel);

		// add to screen
		this.add(publishingFieldSet, new RowData(1234, 650));
	}

	private void preparePeriodFieldSet() {
		final FieldSet periodManagementFieldSet = new FieldSet();
		periodManagementFieldSet
				.setLayout(new RowLayout(Orientation.HORIZONTAL));
		periodManagementFieldSet.setStyleAttribute("margin", "5px");
		periodManagementFieldSet.setHeading("Period");

		// add screen details for the Management Prepare
		HorizontalPanel panel = new HorizontalPanel();
		panel.add(new Label("Period:"));
		panel.add(new Separator(10));
		panel.add(periodBox);
		panel.add(new Separator(10));
		panel.add(new Label("Mandatory Average:"));
		panel.add(new Separator(10));
		panel.add(mandatoryAverage);
		panel.add(new Separator(10));
		periodManagementFieldSet.add(panel, new RowData(600, 75));

		panel = new HorizontalPanel();
		panel.add(excelLink);
		panel.add(new Separator(15));
		panel.add(ilmsProcessingPeriod.getComponent());
		periodManagementFieldSet.add(panel, new RowData(100, 75));

		// add to screen
		this.add(periodManagementFieldSet, new RowData(1234, 75));
	}

	private void updateAndDisplayExportLink(boolean show) {
		if (show) {
			final String excelLink = "./positionReport?screen="
					+ OverviewPanel.SCREEN;
			this.excelLink.setHtml("<a style=\"padding:5px;\" href="
					+ GWT.getModuleBaseURL()
					+ excelLink
					+ ">"
					+ "<img src="
					+ GWT.getModuleBaseURL().replace(GWT.getModuleName() + "/",
							"") + "res/images/ExcelIcon.jpeg></a>");
		} else {
			excelLink.setHtml("");
		}
	}

	private void updateChart() {
		// get the period information
		final IdNameModelData period = periodBox.getValue();

		ilmsProcessingPeriod.setStateProcessing(true);
		// get the actual balances, user can change locally if he wants
		RemoteServicePool.getReservetModule().getGraphDetails(period.getId(),
				OverviewModelData.getPojos(store.getModels()),
				new IlmsAsyncCallback<ReserveEurGraph>() {

					@Override
					protected void onCustomFailure(IlmsSharedException e) {
						super.onCustomFailure(e);
						ilmsProcessingPeriod.setStateProcessing(false);
					}

					@Override
					protected void onCustomSuccess(ReserveEurGraph result) {
						ilmsProcessingPeriod.setStateProcessing(false);

						chart.getChartModel().getChartConfigs().clear();
						chart.getChartModel()
								.setTitle(
										new Text(result.getTitle(),
												"font-size: 14px; font-family: Verdana;"));

						if (result.isEmpty()) {
							// don't generated the graph
							chart.refresh();
							return;
						}

						final BarChart bchart = new BarChart(BarStyle.GLASS);
						bchart.setAnimateOnShow(true);
						bchart.setTooltip("EUR #val#");
						bchart.addChartListener(listener);
						final List<Double> values = result.getValues();
						for (final Double value : values) {
							bchart.addBars(new BarChart.Bar(value));
						}
						// definition of Y
						final YAxis ya = new YAxis();
						ya.setRange(0, result.getYMaxBalance(), 10);
						ya.setGridColour("#8888FF");
						chart.getChartModel().setYAxis(ya);

						// definition of X
						final XAxis xa = new XAxis();
						for (final String m : result.getXAxis()) {
							final com.extjs.gxt.charts.client.model.axis.Label l = new com.extjs.gxt.charts.client.model.axis.Label(
									m, 45);
							l.setSize(10);
							l.setColour("#000000");
							xa.addLabels(l);
						}
						chart.getChartModel().setXAxis(xa);

						// add chart
						chart.getChartModel().addChartConfig(bchart);

						// line with the Mandatory Average
						final LineChart line = new LineChart();
						line.setAnimateOnShow(false);
						line.setText("Mandatory Average");
						line.setColour("#FF0000");
						final ListStore<IdNameModelData> store = new ListStore<IdNameModelData>();
						for (int i = 0; i < result.getValues().size(); i++) {
							store.add(new IdNameModelData(i, String
									.valueOf(result.getMandatoryAverage())));
						}
						final LineDataProvider lineProvider = new LineDataProvider(
								NameModelData.NAME_PROP);
						lineProvider.bind(store);
						line.setDataProvider(lineProvider);
						chart.getChartModel().addChartConfig(line);
						chart.getChartModel().setTooltipStyle(
								new ToolTip(MouseStyle.NORMAL));
						chart.refresh();
					}
				});
	}
}