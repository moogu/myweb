package com.moogu.myweb.client.common.widget;

import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.moogu.myweb.client.common.genericmodeldata.IdNameModelData;
import com.moogu.myweb.client.common.genericmodeldata.NameModelData;

/**
 * A comboBox suitable and pre-configured to display {@link IdNameModelData}
 */
public class IdNameComboBox extends ComboBox<IdNameModelData> {

	public IdNameComboBox() {
		super();
		addStyleName("effectiveWidget");
		setDisplayField(NameModelData.NAME_PROP);
		setValueField(IdNameModelData.ID_PROP);
		setStore(new ListStore<IdNameModelData>());
		setEditable(false);
		setTriggerAction(TriggerAction.ALL);
		setForceSelection(true);
	}
}