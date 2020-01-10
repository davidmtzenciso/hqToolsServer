package com.healthsparq.app.util.unit.sqltranslator.model;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name="TABLE_NAME")
public class HasFieldWithoutAnnotation {
	
	@Id
	@Column(name="ID")
	private Integer id;

	private Integer field1;

	public Integer getField1() {
		return field1;
	}

	public void setField1(Integer field1) {
		this.field1 = field1;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
}
