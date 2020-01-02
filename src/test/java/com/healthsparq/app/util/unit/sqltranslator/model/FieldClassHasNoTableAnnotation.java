package com.healthsparq.app.util.unit.sqltranslator.model;

import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


@Table(name="TABLE_NAME")
public class FieldClassHasNoTableAnnotation {
	
	public FieldClassHasNoTableAnnotation() {
		this.field3 = new HasNoTableAnnotation();
	}

	@Column(name="COLUMN_1")
	private String field1;
	
	@Column(name="COLUMN_2")
	private Integer field2;
	
	@ManyToOne(optional=false) 
	private HasNoTableAnnotation field3;

	public String getField1() {
		return field1;
	}

	public void setField1(String field1) {
		this.field1 = field1;
	}

	public Integer getField2() {
		return field2;
	}

	public void setField2(Integer field2) {
		this.field2 = field2;
	}

	public HasNoTableAnnotation getField3() {
		return field3;
	}

	public void setField3(HasNoTableAnnotation field3) {
		this.field3 = field3;
	}
	
	
}
