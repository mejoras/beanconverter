package com.indra.davinci.utils.tests.beans;

import java.util.List;
import java.util.Map;

public class SourceSimpleBean {

    public String stringValue;
    public Integer integerValue;
    public Float floatValue;
    public List<String> list;
    private List<SourceEnum> listComplex;
//  private List<List<SourceEnum>> listComplex2;
    private Map<Integer, String> mapValue;
    private Map<Integer, SourceEnum> mapValueComplex;
    private Map<SourceEnum, SourceEnum> mapKeyValueComplex;
//  private Map<Integer, List<String>> mapValueComplex2;
	private SourceEnum enumValue;

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

    public void setMapValue(Map<Integer, String> mapValue) {
        this.mapValue = mapValue;
    }

    @Override
    public String toString() {
        return "TargetSimpleBean [stringValue=" + stringValue
                + ", integerValue=" + integerValue
                + ", floatValue="   + floatValue
                + ", list=" + list
                + ", listComplex=" + listComplex
//                + ", listComplex2=" + listComplex2
                + ", enumValue=" + enumValue
                + ", mapValue=" + mapValue
                + ", mapValueComplex=" + mapValueComplex
                + "]";
    }

	public Map<Integer, String> getMapValue() {
		return mapValue;
	}

	public Map<Integer, SourceEnum> getMapValueComplex() {
		return mapValueComplex;
	}

	public void setMapValueComplex(Map<Integer, SourceEnum> mapValueComplex) {
		this.mapValueComplex = mapValueComplex;
	}

//	private Map<Integer, List<String>> getMapValueComplex2() {
//		return mapValueComplex2;
//	}
//
//	private void setMapValueComplex2(Map<Integer, List<String>> mapValueComplex2) {
//		this.mapValueComplex2 = mapValueComplex2;
//	}

	public List<SourceEnum> getListComplex() {
		return listComplex;
	}

	public void setListComplex(List<SourceEnum> listComplex) {
		this.listComplex = listComplex;
	}

	public Map<SourceEnum, SourceEnum> getMapKeyValueComplex() {
		return mapKeyValueComplex;
	}

	public void setMapKeyValueComplex(Map<SourceEnum, SourceEnum> mapKeyValueComplex) {
		this.mapKeyValueComplex = mapKeyValueComplex;
	}

//	public List<List<SourceEnum>> getListComplex2() {
//		return listComplex2;
//	}
//
//	public void setListComplex2(List<List<SourceEnum>> listComplex2) {
//		this.listComplex2 = listComplex2;
//	}

}