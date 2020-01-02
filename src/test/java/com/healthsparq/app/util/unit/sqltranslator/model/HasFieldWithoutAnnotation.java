package com.healthsparq.app.util.unit.sqltranslator.model;

import javax.persistence.Table;

@Table(name="TABLE_NAME")
public class HasFieldWithoutAnnotation {

	private Integer field1;

	public Integer getField1() {
		return field1;
	}

	public void setField1(Integer field1) {
		this.field1 = field1;
	}
}
