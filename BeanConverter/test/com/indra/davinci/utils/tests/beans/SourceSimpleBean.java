package com.indra.davinci.utils.tests.beans;

import java.util.List;

public class SourceSimpleBean {

	public String stringValue;
	public Integer integerValue;
	public Float floatValue;
	public List<String> list;
	
	public SourceEnum enumValue;
	
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
	public SourceEnum getEnumValue() {
		return enumValue;
	}
	public void setEnumValue(SourceEnum enumValue) {
		this.enumValue = enumValue;
	}
	@Override
	public String toString() {
		return "SourceSimpleBean [stringValue=" + stringValue
				+ ", integerValue=" + integerValue + ", floatValue="
				+ floatValue + ", list=" + list + ", enumValue=" + enumValue
				+ "]";
	}
	
}
