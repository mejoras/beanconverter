package com.indra.davinci.utils.tests.beans;

import java.util.List;

public class TargetSimpleBean {

	public String stringValue;
	public Integer integerValue;
	public Float floatValue;
	public List<String> list;

	public TargetEnum enumValue;
	
	public String getStringValue() {
		return stringValue;
	}
	public void setStringValue(String stringValue) {
		this.stringValue = stringValue;
	}
	public Integer getIntegerValue() {
		return integerValue;
	}
	public void setIntegerValue(Integer integerValue) {
		this.integerValue = integerValue;
	}
	public Float getFloatValue() {
		return floatValue;
	}
	public void setFloatValue(Float floatValue) {
		this.floatValue = floatValue;
	}
	public List<String> getList() {
		return list;
	}
	public void setList(List<String> list) {
		this.list = list;
	}
	public TargetEnum getEnumValue() {
		return enumValue;
	}
	public void setEnumValue(TargetEnum enumValue) {
		this.enumValue = enumValue;
	}
	@Override
	public String toString() {
		return "TargetSimpleBean [stringValue=" + stringValue
				+ ", integerValue=" + integerValue + ", floatValue="
				+ floatValue + ", list=" + list + ", enumValue=" + enumValue
				+ "]";
	}
	
}
