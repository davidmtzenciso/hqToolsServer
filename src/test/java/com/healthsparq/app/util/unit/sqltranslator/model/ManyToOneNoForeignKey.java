package com.healthsparq.app.util.unit.sqltranslator.model;

import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Table(name="TABLE_NAME")
public class ManyToOneNoForeignKey {

	@ManyToOne(optional=false)
	private ValidClass field1;

	public ValidClass getField1() {
		return field1;
	}

	public void setField1(ValidClass field1) {
		this.field1 = field1;
	}
}
