package com.moogu.myweb.client.common.genericmodeldata;

import java.util.Date;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class DateModelData extends BaseModelData {

	private static final long serialVersionUID = 7152909747565438358L;

	public static final String DATE_PROP = "date";

	public DateModelData(Date date) {
		super.set(DateModelData.DATE_PROP, date);
	}

	public Date getDate() {
		return this.get(DateModelData.DATE_PROP);
	}

}
