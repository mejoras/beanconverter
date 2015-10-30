package com.indra.davinci.utils.tests;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.indra.davinci.utils.tests.beans.SourceEnum;
import com.indra.davinci.utils.tests.beans.SourceSimpleBean;
import com.indra.davinci.utils.tests.beans.TargetSimpleBean;
import com.indra.isl.malaga.BeanConverter;
import com.indra.isl.malaga.BeanConverterException;

public class BeanConverterEnumTest {

	@Test
	public void test() {

		List<String> list = new ArrayList<String>();
		list.add("Item 1");
		list.add("Item 2");
		list.add("Item 3");
		
		SourceSimpleBean ssb = new SourceSimpleBean();
		ssb.setStringValue("Value 1");
		ssb.setIntegerValue(new Integer(123));
		ssb.setFloatValue(new Float(456f));
		ssb.setList(list);
		ssb.setEnumValue(SourceEnum.ENUM_VALUE_3);
		
		try {
			TargetSimpleBean tsb = (TargetSimpleBean) BeanConverter.copyProperties(ssb, TargetSimpleBean.class);
			
			Assert.assertNotNull(tsb);
			Assert.assertEquals(tsb.getEnumValue().name(), ssb.getEnumValue().name());
			
		} catch (BeanConverterException e) {
			fail("Error: " + e.getMessage());
		}
	}

}
