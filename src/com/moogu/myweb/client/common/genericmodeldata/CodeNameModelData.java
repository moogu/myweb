package com.moogu.myweb.client.common.genericmodeldata;

public class CodeNameModelData extends NameModelData {

	private static final long serialVersionUID = -2137711556544523564L;

	public static final String CODE_PROP = "code";

	public CodeNameModelData(String code, String name) {
		super(name, null);
		if (code == null) {
			throw new NullPointerException("Code can't be null");
		}
		super.set(CodeNameModelData.CODE_PROP, code);
	}

	public CodeNameModelData(String code, String name, Object value) {
		super(name, value);
		super.set(CodeNameModelData.CODE_PROP, code);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		final CodeNameModelData other = (CodeNameModelData) obj;
		return getCode().equals(other.getCode());
	}

	public String getCode() {
		return this.get(CodeNameModelData.CODE_PROP);
	}

	@Override
	public int hashCode() {
		return getCode().hashCode();
	}
}
