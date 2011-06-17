package com.moogu.myweb.client.common.genericmodeldata;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class NameModelData extends BaseModelData {

	private static final long serialVersionUID = -2137711556544523564L;

	public static final String NAME_PROP = "name";

	// You can optionally bind a domain object here
	public static final String VALUE_PROP = "value";

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static List getValues(List<NameModelData> models) {
		final List result = new ArrayList();
		for (final NameModelData modelData : models) {
			result.add(modelData.getValue());
		}
		return result;
	}

	public NameModelData(String name, Object value) {
		super();
		super.set(NameModelData.VALUE_PROP, value);
		super.set(NameModelData.NAME_PROP, name);
	}

	public String getName() {
		return this.get(NameModelData.NAME_PROP);
	}

	public Object getValue() {
		return this.get(NameModelData.VALUE_PROP);
	}

}
