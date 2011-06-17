package com.moogu.myweb.client.feature.technical.properties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.SimpleComboValue;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.moogu.myweb.client.ClientTool;
import com.moogu.myweb.client.RemoteServicePool;
import com.moogu.myweb.client.common.IlmsAsyncCallback;
import com.moogu.myweb.client.common.widget.Button;
import com.moogu.myweb.client.common.widget.misc.Separator;

public class PropertiesPanel {

	public static final String KEY_COLUMN = "KEY";

	public static final String VALUE_COLUMN = "VALUE";

	private final FieldSet fieldSet;

	private final SimpleComboBox<String> combo = new SimpleComboBox<String>();

	public PropertiesPanel() {
		fieldSet = new FieldSet();
		fieldSet.setStyleAttribute("margin", "5px");
		fieldSet.setHeading("Properties");
		fieldSet.setLayout(new RowLayout(Orientation.VERTICAL));
		fieldSet.setHeight(700);
		final List<ColumnConfig> configs = new ArrayList<ColumnConfig>(2);

		// Key
		combo.setAllowBlank(true);
		combo.setTriggerAction(TriggerAction.ALL);
		combo.setForceSelection(false);
		final CellEditor keyEditor = new CellEditor(combo) {

			@Override
			public Object postProcessValue(Object value) {
				final String comboValue = combo.getRawValue();
				return comboValue;
			}

			@Override
			public Object preProcessValue(Object value) {
				if (value == null) {
					return null;
				}
				final String stringValue = (String) value;
				SimpleComboValue<String> comboValue = combo
						.findModel(stringValue);
				if (comboValue == null) {
					combo.add(stringValue);
					comboValue = combo.findModel(stringValue);
				}
				return comboValue;
			}
		};
		final ColumnConfig keyConfig = new ColumnConfig(
				PropertiesPanel.KEY_COLUMN, 350);
		keyConfig.setHeader("Key");
		keyConfig.setEditor(keyEditor);
		configs.add(keyConfig);

		// Value
		final ColumnConfig valueConfig = new ColumnConfig(
				PropertiesPanel.VALUE_COLUMN, 350);
		valueConfig.setHeader("Value");
		final TextField<String> valueFieldKey = new TextField<String>();
		valueConfig.setEditor(new CellEditor(valueFieldKey));
		configs.add(valueConfig);

		final ColumnModel columnModel = new ColumnModel(configs);
		final EditorGrid<ModelData> grid = new EditorGrid<ModelData>(
				new ListStore<ModelData>(), columnModel);
		grid.setAutoExpandColumn(PropertiesPanel.VALUE_COLUMN);
		grid.setSize(700, 500);
		grid.setStripeRows(true);
		grid.setBorders(true);

		fieldSet.add(grid);
		fieldSet.add(new Separator(0, 20));

		// Add button
		final Button add = new Button("Add");
		add.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				final ModelData data = new BaseModelData();
				grid.getStore().add(data);
			}
		});
		fieldSet.add(add);

		// Save button
		fieldSet.add(new Separator(0, 5));
		final Button save = new Button("Save");
		save.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				final Map<String, String> props = new HashMap<String, String>();
				for (final ModelData modelData : grid.getStore().getModels()) {
					final String key = modelData
							.get(PropertiesPanel.KEY_COLUMN);
					final String value = modelData
							.get(PropertiesPanel.VALUE_COLUMN);
					if (!ClientTool.isNullOrEmpty(key)) {
						props.put(key, value);
					}
				}
				RemoteServicePool.getCommonModule().saveProperties(props,
						new IlmsAsyncCallback<Void>() {

							@Override
							protected void onCustomSuccess(Void result) {
								PropertiesPanel.this.populate(grid);

							}
						});
			}

		});
		fieldSet.add(save);

		// Refresh button
		fieldSet.add(new Separator(0, 5));
		final Button refresh = new Button("Refresh");
		refresh.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				PropertiesPanel.this.populate(grid);
			}
		});
		fieldSet.add(refresh);

		// On render
		final Listener<BaseEvent> renderListener = new Listener<BaseEvent>() {

			public void handleEvent(BaseEvent be) {
				PropertiesPanel.this.populate(grid);
			};
		};
		fieldSet.addListener(Events.Render, renderListener);
	}

	public Component getComponent() {
		return fieldSet;
	}

	private void populate(final Grid<ModelData> grid) {
		RemoteServicePool.getCommonModule().getProperties(
				new IlmsAsyncCallback<Map<String, String>>(true) {

					@Override
					protected void onCustomSuccess(Map<String, String> result) {
						final List<ModelData> data = new ArrayList<ModelData>();
						for (final String key : result.keySet()) {
							final String value = result.get(key);
							final BaseModelData datum = new BaseModelData();
							datum.set(PropertiesPanel.KEY_COLUMN, key);
							datum.set(PropertiesPanel.VALUE_COLUMN, value);
							data.add(datum);
						}
						grid.getStore().removeAll();
						grid.getStore().add(data);
					}

				});
		RemoteServicePool.getCommonModule().getProperetyKeys(
				new IlmsAsyncCallback<List<String>>() {

					@Override
					protected void onCustomSuccess(List<String> result) {
						combo.removeAll();
						combo.add(result);

					}
				});
	}

}
