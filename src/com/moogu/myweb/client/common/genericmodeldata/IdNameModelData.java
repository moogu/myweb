package com.moogu.myweb.client.common.genericmodeldata;

import com.google.gwt.user.client.rpc.IsSerializable;

public class IdNameModelData extends NameModelData implements IsSerializable {

	private static final long serialVersionUID = -4973333548512394325L;

	public static final String ID_PROP = "id";

	public IdNameModelData() {
		// just for IsSerializable
		super(null, null);
	}

	public IdNameModelData(Integer id, String name) {
		super(name, null);
		super.set(IdNameModelData.ID_PROP, id);
	}

	public IdNameModelData(Integer id, String name, Object value) {
		super(name, value);
		super.set(IdNameModelData.ID_PROP, id);

	}

	public Integer getId() {
		return this.get(IdNameModelData.ID_PROP);
	}

}
