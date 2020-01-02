package com.healthsparq.app.util.unit.sqltranslator.model;

import javax.persistence.Column;
import javax.persistence.Table;

@Table(name="TABLE_NAME")
public class HasInvalidFieldType {
	
	@Column(name="COLUMN_1")
	private Double field1;

	public Double getField1() {
		return field1;
	}

	public void setField1(Double field1) {
		this.field1 = field1;
	}
}
